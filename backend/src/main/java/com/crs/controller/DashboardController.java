package com.crs.controller;

import com.crs.entity.Hotel;
import com.crs.entity.Inventory;
import com.crs.entity.Reservation;
import com.crs.repository.HotelRepository;
import com.crs.repository.InventoryRepository;
import com.crs.repository.ReservationRepository;
import com.crs.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 首页数据聚合控制器
 * 提供集团首页和门店首页所需的聚合数据
 * 
 * GET /api/dashboard/group  → 集团维度数据
 * GET /api/dashboard/hotel  → 门店维度数据
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private ReservationRepository reservationRepo;
    @Autowired private InventoryRepository inventoryRepo;
    @Autowired private HotelRepository hotelRepo;
    @Autowired private JwtUtil jwtUtil;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 获取当前请求的tenantId
     * 优先从 X-Tenant-Id header 获取（前端 axios 拦截器注入），
     * 其次从 JWT claims 中提取。
     * 超级管理员的 JWT 中 tenantId 为 null，必须依赖 header。
     */
    private Integer getTenantId(HttpServletRequest req) {
        // 优先从 header 获取（前端拦截器设置）
        String tenantIdStr = req.getHeader("X-Tenant-Id");
        if (tenantIdStr != null && !tenantIdStr.isBlank()) {
            try {
                return Integer.parseInt(tenantIdStr);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        // 其次从 JWT token 中提取
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtUtil.extractTenantId(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    // =========================================================================
    // 集团首页 API
    // =========================================================================
    @GetMapping("/group")
    public ResponseEntity<?> getGroupDashboard(
            HttpServletRequest req,
            @RequestParam(required = false) String hotelCode) {
        try {
            Integer tenantId = getTenantId(req);
            if (tenantId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "无法获取租户信息"));
            }

            Date today = getToday();
            Date monthStart = getMonthStart();
            Date monthEnd = getMonthEnd();
            Date thirtyDaysAgo = getDaysAgo(30);
            Date sevenDaysLater = getDaysLater(7);

            Map<String, Object> data = new LinkedHashMap<>();

            // === 模块 A: 核心指标卡片 ===
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("todayNewOrders", reservationRepo.countByTenantIdAndCreatedAtGreaterThanEqual(tenantId, today));
            stats.put("todayCheckIn", reservationRepo.countByTenantIdAndCheckInDateAndStatusNot(tenantId, today, Reservation.Status.cancelled));
            stats.put("monthRevenue", reservationRepo.sumTotalPriceByTenantIdAndDateRange(tenantId, monthStart, monthEnd));
            
            List<Hotel> activeHotels = hotelRepo.findByTenantIdAndStatus(tenantId, Hotel.Status.active);
            stats.put("activeHotelCount", activeHotels.size());
            stats.put("pendingManual", reservationRepo.countByTenantIdAndIsManualAndStatus(tenantId, true, Reservation.Status.active));
            data.put("stats", stats);

            // === 模块 B: 酒店运营概览表 ===
            List<Map<String, Object>> hotelOverview = new ArrayList<>();
            for (Hotel hotel : activeHotels) {
                Map<String, Object> hotelData = new LinkedHashMap<>();
                hotelData.put("hotelId", hotel.getId());
                hotelData.put("hotelCode", hotel.getHotelCode());
                hotelData.put("hotelName", hotel.getChineseName());
                hotelData.put("starRating", hotel.getStarRating());
                hotelData.put("city", hotel.getCity());
                hotelData.put("totalRooms", hotel.getTotalRooms());

                // 今日入住数
                hotelData.put("todayCheckIn", reservationRepo.countByTenantIdAndHotelCodeAndCheckInDateAndStatusNot(
                        tenantId, hotel.getHotelCode(), today, Reservation.Status.cancelled));
                // 今日退房数
                hotelData.put("todayCheckOut", reservationRepo.countByTenantIdAndHotelCodeAndCheckOutDateAndStatusNot(
                        tenantId, hotel.getHotelCode(), today, Reservation.Status.cancelled));
                // 本月收入
                hotelData.put("monthRevenue", reservationRepo.sumTotalPriceByTenantIdAndHotelCodeAndDateRange(
                        tenantId, hotel.getHotelCode(), monthStart, monthEnd));
                // 今日可用库存
                List<Inventory> todayInv = inventoryRepo.findByTenantIdAndHotelCodeAndDate(tenantId, hotel.getHotelCode(), today);
                int todayAvailable = todayInv.stream().mapToInt(Inventory::getAvailableRooms).sum();
                hotelData.put("todayAvailableRooms", todayAvailable);

                hotelOverview.add(hotelData);
            }
            data.put("hotelOverview", hotelOverview);

            // === 模块 C: 预订趋势（近30天） ===
            List<Object[]> trendRaw = reservationRepo.countByDateGrouped(tenantId, thirtyDaysAgo);
            List<Map<String, Object>> trend = trendRaw.stream().map(row -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("date", row[0] != null ? row[0].toString() : "");
                m.put("count", ((Number) row[1]).longValue());
                return m;
            }).collect(Collectors.toList());
            data.put("bookingTrend", trend);

            // === 模块 D: 渠道贡献 ===
            List<Object[]> channelRaw = reservationRepo.countByChannelGrouped(tenantId, thirtyDaysAgo);
            List<Map<String, Object>> channels = channelRaw.stream().map(row -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("channelName", row[0] != null ? row[0].toString() : "未知");
                m.put("count", ((Number) row[1]).longValue());
                return m;
            }).collect(Collectors.toList());
            data.put("channelDistribution", channels);

            // === 模块 E: 库存预警 ===
            List<Inventory> lowInv = inventoryRepo.findLowInventory(tenantId, 2, today, sevenDaysLater);
            List<Map<String, Object>> alerts = lowInv.stream().limit(20).map(inv -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("hotelCode", inv.getHotelCode());
                m.put("roomTypeCode", inv.getRoomTypeCode());
                m.put("date", sdf.format(inv.getDate()));
                m.put("availableRooms", inv.getAvailableRooms());
                m.put("channelCode", inv.getChannelCode());
                return m;
            }).collect(Collectors.toList());
            data.put("inventoryAlerts", alerts);

            // === 模块 F: 预订流速监测 (Pacing) [New] ===
            data.put("groupPacing", calculatePacingData(tenantId, hotelCode, today, activeHotels));

            // === 模块 G: 最新订单 ===
            List<Reservation> recentOrders = reservationRepo.findTop10ByTenantIdAndStatusNotOrderByCreatedAtDesc(tenantId, Reservation.Status.cancelled);
            List<Map<String, Object>> recentList = recentOrders.stream().map(this::mapReservation).collect(Collectors.toList());
            data.put("recentOrders", recentList);

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // 门店首页 API
    // =========================================================================
    @GetMapping("/hotel")
    public ResponseEntity<?> getHotelDashboard(
            HttpServletRequest req,
            @RequestParam String hotelCode) {
        try {
            Integer tenantId = getTenantId(req);
            if (tenantId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "无法获取租户信息"));
            }

            Hotel hotel = hotelRepo.findByHotelCodeAndTenantId(hotelCode, tenantId).orElse(null);
            if (hotel == null) {
                return ResponseEntity.notFound().build();
            }

            Date today = getToday();
            Date monthStart = getMonthStart();
            Date monthEnd = getMonthEnd();
            Date sevenDaysLater = getDaysLater(6);
            Date sevenDaysAgo = getDaysAgo(7);

            Map<String, Object> data = new LinkedHashMap<>();

            // === 酒店基础信息 ===
            Map<String, Object> hotelInfo = new LinkedHashMap<>();
            hotelInfo.put("hotelCode", hotel.getHotelCode());
            hotelInfo.put("hotelName", hotel.getChineseName());
            hotelInfo.put("starRating", hotel.getStarRating());
            hotelInfo.put("city", hotel.getCity());
            hotelInfo.put("totalRooms", hotel.getTotalRooms() != null ? hotel.getTotalRooms() : 0);
            data.put("hotelInfo", hotelInfo);

            // === 模块 A: 核心 KPI ===
            Map<String, Object> stats = new LinkedHashMap<>();
            long todayCheckIn = reservationRepo.countByTenantIdAndHotelCodeAndCheckInDateAndStatusNot(
                    tenantId, hotel.getHotelCode(), today, Reservation.Status.cancelled);
            long todayCheckOut = reservationRepo.countByTenantIdAndHotelCodeAndCheckOutDateAndStatusNot(
                    tenantId, hotel.getHotelCode(), today, Reservation.Status.cancelled);
            long inHouse = reservationRepo.countByTenantIdAndHotelCodeAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
                    tenantId, hotel.getHotelCode(), today, today, Reservation.Status.active);
            long todayNewOrders = reservationRepo.countByTenantIdAndHotelCodeAndCreatedAtGreaterThanEqual(
                    tenantId, hotel.getHotelCode(), today);

            // 今日可售
            List<Inventory> todayInv = inventoryRepo.findByTenantIdAndHotelCodeAndDate(tenantId, hotel.getHotelCode(), today);
            int todayAvailable = todayInv.stream().mapToInt(Inventory::getAvailableRooms).sum();

            // 出租率
            int totalRooms = hotel.getTotalRooms() != null && hotel.getTotalRooms() > 0 ? hotel.getTotalRooms() : 1;
            double occupancyRate = Math.min(100.0, (double) inHouse * 100 / totalRooms);

            stats.put("todayCheckIn", todayCheckIn);
            stats.put("todayCheckOut", todayCheckOut);
            stats.put("inHouse", inHouse);
            stats.put("todayNewOrders", todayNewOrders);
            stats.put("todayAvailable", todayAvailable);
            stats.put("occupancyRate", Math.round(occupancyRate * 10) / 10.0);
            stats.put("monthRevenue", reservationRepo.sumTotalPriceByTenantIdAndHotelCodeAndDateRange(
                    tenantId, hotel.getHotelCode(), monthStart, monthEnd));
            data.put("stats", stats);

            // === 模块 B: 未来7天库存日历 ===
            List<Inventory> weekInv = inventoryRepo.findByTenantIdAndHotelCodeAndDateBetween(tenantId, hotel.getHotelCode(), today, sevenDaysLater);
            // 按日期分组汇总可用库存
            Map<String, Integer> dailyInventory = new LinkedHashMap<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            for (int i = 0; i < 7; i++) {
                String dateStr = sdf.format(cal.getTime());
                final Date d = cal.getTime();
                int available = weekInv.stream()
                        .filter(inv -> sdf.format(inv.getDate()).equals(dateStr))
                        .mapToInt(Inventory::getAvailableRooms)
                        .sum();
                dailyInventory.put(dateStr, available);
                cal.add(Calendar.DATE, 1);
            }
            data.put("weekInventory", dailyInventory);

            // === 模块 C: 今日订单列表 ===
            List<Reservation> recentOrders = reservationRepo.findTop10ByTenantIdAndHotelCodeOrderByCreatedAtDesc(tenantId, hotel.getHotelCode());
            data.put("recentOrders", recentOrders.stream().map(this::mapReservation).collect(Collectors.toList()));

            // === 模块 D: 经营趋势透视 (OCC & ADR) ===
            Date trendStart = getDaysAgo(30);
            List<Object[]> dailyStats = reservationRepo.getDailyStatsByCheckIn(tenantId, hotel.getHotelCode(), trendStart, today);
            List<Map<String, Object>> trends = dailyStats.stream().map(row -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("date", sdf.format((Date)row[0]));
                long soldNights = ((Number)row[1]).longValue();
                BigDecimal revenue = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
                double occ = totalRooms > 0 ? Math.min(100.0, (double)soldNights * 100 / totalRooms) : 0;
                BigDecimal adr = soldNights > 0 ? revenue.divide(BigDecimal.valueOf(soldNights), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                m.put("occ", Math.round(occ * 10) / 10.0);
                m.put("adr", adr);
                return m;
            }).collect(Collectors.toList());
            data.put("trends", trends);

            // === 模块 E: 渠道价值矩阵 ===
            List<Object[]> channelStats = reservationRepo.getChannelMatrixStats(tenantId, hotel.getHotelCode(), monthStart);
            List<Map<String, Object>> channelMatrix = channelStats.stream().map(row -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("channel", row[0]);
                m.put("bookings", row[1]);
                BigDecimal revenue = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
                long totalNights = row[3] != null ? ((Number)row[3]).longValue() : 0;
                BigDecimal adr = totalNights > 0 ? revenue.divide(BigDecimal.valueOf(totalNights), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                m.put("adr", adr);
                return m;
            }).collect(Collectors.toList());
            data.put("channelMatrix", channelMatrix);

            // === 模块 F: 预订流速监测 (Pacing) ===
            List<Object[]> pickupRaw = reservationRepo.getRecentPickupStatsByHotel(tenantId, hotel.getHotelCode(), getDaysAgo(7));
            // 简单算法：今日 vs 过去3天均值
            long todayPickup = 0;
            double avgPickup = 0;
            if (!pickupRaw.isEmpty()) {
                todayPickup = ((Number)pickupRaw.get(pickupRaw.size()-1)[1]).longValue();
                if (pickupRaw.size() > 1) {
                    avgPickup = pickupRaw.stream().limit(pickupRaw.size()-1).mapToLong(r -> ((Number)r[1]).longValue()).average().orElse(0);
                }
            }
            
            String velocity = "正常";
            if (todayPickup > avgPickup * 1.5) velocity = "非常快";
            else if (todayPickup > avgPickup * 1.2) velocity = "快";
            else if (todayPickup < avgPickup * 0.5) velocity = "非常慢";
            else if (todayPickup < avgPickup * 0.8) velocity = "慢";
            
            Map<String, Object> pacing = new LinkedHashMap<>();
            pacing.put("velocity", velocity);
            pacing.put("todayPickup", todayPickup);
            pacing.put("avgPickup", Math.round(avgPickup));
            data.put("pacing", pacing);

            // === 模块 G: 异常订单监控 ===
            List<Map<String, Object>> exceptions = new ArrayList<>();
            // 示例：查询待确认订单
            long pendingConfirm = reservationRepo.countByTenantIdAndHotelCodeAndReservationStatus(tenantId, hotel.getHotelCode(), "wait_for_confirmation");
            if (pendingConfirm > 0) {
                exceptions.add(Map.of("type", "待确认", "detail", "有 " + pendingConfirm + " 笔订单等待确认", "level", "warning"));
            }
            // 示例：库存不足预警
            List<Inventory> lowInv = inventoryRepo.findLowInventoryByHotel(tenantId, hotel.getHotelCode(), 0, today, getDaysLater(3));
            if (!lowInv.isEmpty()) {
                exceptions.add(Map.of("type", "超卖预警", "detail", "未来3天存在库存为0的房型", "level", "error"));
            }
            data.put("exceptions", exceptions);

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 计算流速监测数据 (未来 7 天)
     */
    private List<Map<String, Object>> calculatePacingData(Integer tenantId, String hotelCode, Date today, List<Hotel> activeHotels) {
        List<Map<String, Object>> pacingList = new ArrayList<>();
        SimpleDateFormat monthDaySdf = new SimpleDateFormat("MM-dd");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        
        for (int i = 0; i < 7; i++) {
            Date date = cal.getTime();
            Map<String, Object> dayPacing = new LinkedHashMap<>();
            dayPacing.put("date", monthDaySdf.format(date));
            
            // 计算出租率 (OCC)
            int totalRooms = 0;
            int availableRooms = 0;
            
            if (hotelCode != null && !hotelCode.isBlank()) {
                // 单个酒店
                final String finalHotelCode = hotelCode;
                Hotel hotel = activeHotels.stream().filter(h -> h.getHotelCode().equals(finalHotelCode)).findFirst().orElse(null);
                if (hotel != null) {
                    totalRooms = hotel.getTotalRooms();
                    List<Inventory> invList = inventoryRepo.findByTenantIdAndHotelCodeAndDate(tenantId, hotelCode, date);
                    availableRooms = invList.stream().mapToInt(Inventory::getAvailableRooms).sum();
                }
            } else {
                // 全集团平均
                for (Hotel hotel : activeHotels) {
                    totalRooms += hotel.getTotalRooms() != null ? hotel.getTotalRooms() : 0;
                    List<Inventory> invList = inventoryRepo.findByTenantIdAndHotelCodeAndDate(tenantId, hotel.getHotelCode(), date);
                    availableRooms += invList.stream().mapToInt(Inventory::getAvailableRooms).sum();
                }
            }
            
            int occ = totalRooms > 0 ? (int) Math.round((1.0 - (double) availableRooms / totalRooms) * 100) : 0;
            occ = Math.min(100, Math.max(0, occ));
            dayPacing.put("avgOcc", occ);
            
            // 计算流速 (Velocity) - 简单模拟逻辑，基于近 24 小时预订增量 (Pickup)
            String velocity = "正常";
            String color = "#52c41a";
            
            if (occ > 90) { velocity = "售罄风险"; color = "#ff4d4f"; }
            else if (occ > 80) { velocity = "极快"; color = "#ff4d4f"; }
            else if (occ > 70) { velocity = "快"; color = "#faad14"; }
            
            dayPacing.put("velocity", velocity);
            dayPacing.put("color", color);
            
            pacingList.add(dayPacing);
            cal.add(Calendar.DATE, 1);
        }
        
        return pacingList;
    }

    // =========================================================================
    // 辅助方法
    // =========================================================================

    private Map<String, Object> mapReservation(Reservation r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reservationCode", r.getReservationCode());
        m.put("hotelName", r.getHotelName());
        m.put("roomTypeName", r.getRoomTypeName());
        m.put("ratePlanName", r.getRatePlanName());
        m.put("channelName", r.getChannelName());
        m.put("contactName", r.getContactName());
        m.put("checkInDate", r.getCheckInDate() != null ? sdf.format(r.getCheckInDate()) : null);
        m.put("checkOutDate", r.getCheckOutDate() != null ? sdf.format(r.getCheckOutDate()) : null);
        m.put("nights", r.getNights());
        m.put("roomCount", r.getRoomCount());
        m.put("totalPrice", r.getTotalPrice());
        m.put("reservationStatus", r.getReservationStatus());
        m.put("paymentStatus", r.getPaymentStatus());
        m.put("createdAt", r.getCreatedAt());
        return m;
    }

    private Date getToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private Date getDaysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getDaysLater(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
