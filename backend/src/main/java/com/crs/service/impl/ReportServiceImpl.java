package com.crs.service.impl;

import com.crs.entity.Hotel;
import com.crs.entity.RatePlan;
import com.crs.entity.RoomType;
import com.crs.repository.*;
import com.crs.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDailyReservationSummaryRepository summaryRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RatePlanRepository ratePlanRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 常用维度的硬编码字典回退
    private static final Map<String, String> CHANNEL_MAP = Map.of(
        "CTRIP", "携程",
        "FLIGGY", "飞猪",
        "BOOKING", "Booking",
        "WECHAT", "微信小程序",
        "WXMINI", "微信小程序",
        "DIRECT", "官网直营",
        "MEITUAN", "美团"
    );

    private static final Map<String, String> MARKET_MAP = Map.of(
        "DOM", "国内市场",
        "INT", "国际市场",
        "CORP", "协议企业",
        "OTA001", "携程分销",
        "OTA002", "美团分销",
        "CORP001", "阿里协议",
        "CORP002", "腾讯协议",
        "CORP003", "字节协议"
    );

    private static final Map<String, String> CATEGORY_MAP = Map.of(
        "ROOM", "纯房费大类",
        "PACKAGE", "包价特惠大类",
        "ADDON", "增值服务大类",
        "public", "公共价格类",
        "agreement", "协议特权类",
        "member", "会员专享类"
    );

    @Override
    public void initializeSummaryData(Integer tenantId, LocalDate startDate, LocalDate endDate) {
        // 1. 先清理目标时间段内的旧汇总数据
        summaryRepository.deleteByTenantIdAndReportDateBetween(tenantId, startDate, endDate);

        // 2. 从原始 reservation 表中按天和多维叶子节点进行聚合拉取
        // 注意：created_at 是 datetime 字段，因此需要使用 DATE() 函数转换
        String aggSql = 
            "SELECT " +
            "    r.tenant_id, " +
            "    DATE(r.created_at) as report_date, " +
            "    COALESCE(r.hotel_code, 'JJHZ001') as hotel_code, " +
            "    COALESCE(r.channel_code, 'DIRECT') as channel_code, " +
            "    COALESCE(r.market_code, 'DOM') as market_code, " +
            "    COALESCE(rp.rate_category, 'ROOM') as rate_category_code, " +
            "    COALESCE(r.rate_plan_code, 'BAR') as rate_plan_code, " +
            "    COALESCE(r.room_type_code, 'ST1') as room_type_code, " +
            "    COALESCE(r.reservation_status, 'confirmed') as reservation_status, " +
            "    COUNT(r.id) as order_count, " +
            "    SUM(r.room_count * r.nights) as room_nights, " +
            "    SUM(r.total_price) as total_revenue, " +
            "    SUM(CASE WHEN r.rate_plan_code = 'BAR' THEN r.room_count * r.nights * 500 ELSE 0 END) as points_redeemed " +
            "FROM reservation r " +
            "LEFT JOIN rate_plans rp ON r.rate_plan_code = rp.rate_code AND r.hotel_code = rp.hotel_code AND r.tenant_id = rp.tenant_id " +
            "WHERE r.tenant_id = ? " +
            "  AND r.created_at >= ? " +
            "  AND r.created_at < ? " +
            "  AND r.reservation_status IN ('confirmed', 'checked_in', 'completed', 'cancelled') " +
            "GROUP BY " +
            "    r.tenant_id, " +
            "    DATE(r.created_at), " +
            "    COALESCE(r.hotel_code, 'JJHZ001'), " +
            "    COALESCE(r.channel_code, 'DIRECT'), " +
            "    COALESCE(r.market_code, 'DOM'), " +
            "    COALESCE(rp.rate_category, 'ROOM'), " +
            "    COALESCE(r.rate_plan_code, 'BAR'), " +
            "    COALESCE(r.room_type_code, 'ST1'), " +
            "    COALESCE(r.reservation_status, 'confirmed')";

        // 将 LocalDates 转换为用于 SQL 的 Timestamps
        java.sql.Timestamp startTs = java.sql.Timestamp.valueOf(startDate.atStartOfDay());
        java.sql.Timestamp endTs = java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(aggSql, tenantId, startTs, endTs);

        // 3. 批量将聚合结果写入到日聚合表
        String insertSql = 
            "INSERT INTO report_daily_reservation_summary (" +
            "    tenant_id, report_date, hotel_code, channel_code, market_code, " +
            "    rate_category_code, rate_plan_code, room_type_code, reservation_status, " +
            "    order_count, room_nights, total_revenue, points_redeemed, created_at, updated_at" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        List<Object[]> batchArgs = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            batchArgs.add(new Object[]{
                row.get("tenant_id"),
                row.get("report_date"),
                row.get("hotel_code"),
                row.get("channel_code"),
                row.get("market_code"),
                row.get("rate_category_code"),
                row.get("rate_plan_code"),
                row.get("room_type_code"),
                row.get("reservation_status"),
                row.get("order_count"),
                row.get("room_nights"),
                row.get("total_revenue"),
                row.get("points_redeemed")
            });
        }

        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, batchArgs);
        }
    }

    @Override
    public Map<String, Object> queryReservationReport(
            Integer tenantId, LocalDate startDate, LocalDate endDate,
            String hotelCode, String channelCode, String marketCode,
            String rateCategoryCode, String ratePlanCode,
            String orderStatus,
            String groupBy1, String groupBy2, String paymentMethod,
            Boolean memberBooking, Boolean canEarnPoints, Boolean onlineBooking,
            Boolean enableCompare, LocalDate compareStartDate, LocalDate compareEndDate) {

        // 1. 获取名称字典映射以替换前端编码
        Map<String, String> hotelNames = hotelRepository.findAll().stream()
                .collect(Collectors.toMap(Hotel::getHotelCode, Hotel::getChineseName, (a, b) -> a));
        Map<String, String> roomTypeNames = roomTypeRepository.findAll().stream()
                .collect(Collectors.toMap(RoomType::getCode, RoomType::getName, (a, b) -> a));
        Map<String, String> ratePlanNames = ratePlanRepository.findAll().stream()
                .collect(Collectors.toMap(RatePlan::getRateCode, RatePlan::getRateName, (a, b) -> a));

        // 2. 执行本期和对比期的数据查询
        List<Map<String, Object>> currentRows = queryAggregatedData(
                tenantId, startDate, endDate, hotelCode, channelCode, marketCode,
                rateCategoryCode, ratePlanCode, orderStatus, groupBy1, groupBy2,
                paymentMethod, memberBooking, canEarnPoints, onlineBooking
        );

        List<Map<String, Object>> prevRows = new ArrayList<>();
        if (Boolean.TRUE.equals(enableCompare) && compareStartDate != null && compareEndDate != null) {
            prevRows = queryAggregatedData(
                    tenantId, compareStartDate, compareEndDate, hotelCode, channelCode, marketCode,
                    rateCategoryCode, ratePlanCode, orderStatus, groupBy1, groupBy2,
                    paymentMethod, memberBooking, canEarnPoints, onlineBooking
            );
        }

        // 3. 构建对比期的快速索引
        Map<String, Map<String, Object>> prevMap = new HashMap<>();
        for (Map<String, Object> row : prevRows) {
            String key = row.get("groupBy1Val") + "_" + row.get("groupBy2Val");
            prevMap.put(key, row);
        }

        // 4. 合并本期和对比期数据，计算变化率，并按照前端要求的树状结构封装
        Map<String, List<Map<String, Object>>> treeHelper = new LinkedHashMap<>();

        // 计算本期总计
        int totalOrderCount = 0;
        int totalRoomNights = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalPoints = 0;

        // 计算对比期总计
        int prevTotalOrderCount = 0;
        int prevTotalRoomNights = 0;
        BigDecimal prevTotalAmount = BigDecimal.ZERO;
        int prevTotalPoints = 0;

        int detailKeyIndex = 1;
        for (Map<String, Object> cur : currentRows) {
            String g1Val = (String) cur.get("groupBy1Val");
            String g2Val = (String) cur.get("groupBy2Val");
            String compKey = g1Val + "_" + g2Val;

            int curCount = ((Number) cur.get("orderCount")).intValue();
            int curNights = ((Number) cur.get("roomNights")).intValue();
            BigDecimal curAmt = (BigDecimal) cur.get("totalRevenue");
            int curPoints = ((Number) cur.get("pointsRedeemed")).intValue();

            totalOrderCount += curCount;
            totalRoomNights += curNights;
            totalAmount = totalAmount.add(curAmt);
            totalPoints += curPoints;

            int prevCount = 0;
            int prevNights = 0;
            BigDecimal prevAmt = BigDecimal.ZERO;
            int prevPoints = 0;

            if (prevMap.containsKey(compKey)) {
                Map<String, Object> prev = prevMap.get(compKey);
                prevCount = ((Number) prev.get("orderCount")).intValue();
                prevNights = ((Number) prev.get("roomNights")).intValue();
                prevAmt = (BigDecimal) prev.get("totalRevenue");
                prevPoints = ((Number) prev.get("pointsRedeemed")).intValue();
            }

            prevTotalOrderCount += prevCount;
            prevTotalRoomNights += prevNights;
            prevTotalAmount = prevTotalAmount.add(prevAmt);
            prevTotalPoints += prevPoints;

            // 计算 ADR
            double curAvgRate = curNights > 0 ? curAmt.divide(BigDecimal.valueOf(curNights), 0, RoundingMode.HALF_UP).doubleValue() : 0.0;
            double prevAvgRate = prevNights > 0 ? prevAmt.divide(BigDecimal.valueOf(prevNights), 0, RoundingMode.HALF_UP).doubleValue() : 0.0;

            // 构造前端需要的明细对象
            Map<String, Object> detail = new HashMap<>();
            detail.put("key", "detail_" + detailKeyIndex++);
            // 为了让前端列自适应，我们将第二级分组的显示名称统一填在 hotel 列
            detail.put("hotel", getDisplayName(groupBy2, g2Val, hotelNames, roomTypeNames, ratePlanNames));
            detail.put("subGroupCode", g2Val);

            Map<String, Object> curPeriod = new HashMap<>();
            curPeriod.put("orderCount", curCount);
            curPeriod.put("orderAmount", curAmt.setScale(0, RoundingMode.HALF_UP).intValue());
            curPeriod.put("orderPoints", curPoints);
            curPeriod.put("roomNights", curNights);
            curPeriod.put("avgRate", (int) curAvgRate);

            if (Boolean.TRUE.equals(enableCompare)) {
                curPeriod.put("orderCountChange", calculateChangeRate(curCount, prevCount));
                curPeriod.put("orderAmountChange", calculateChangeRate(curAmt.doubleValue(), prevAmt.doubleValue()));
                curPeriod.put("orderPointsChange", calculateChangeRate(curPoints, prevPoints));
                curPeriod.put("roomNightsChange", calculateChangeRate(curNights, prevNights));
                curPeriod.put("avgRateChange", calculateChangeRate(curAvgRate, prevAvgRate));
            }

            Map<String, Object> prevPeriod = new HashMap<>();
            prevPeriod.put("orderCount", prevCount);
            prevPeriod.put("orderAmount", prevAmt.setScale(0, RoundingMode.HALF_UP).intValue());
            prevPeriod.put("orderPoints", prevPoints);
            prevPeriod.put("roomNights", prevNights);
            prevPeriod.put("avgRate", (int) prevAvgRate);

            detail.put("currentPeriod", curPeriod);
            detail.put("previousPeriod", prevPeriod);

            treeHelper.computeIfAbsent(g1Val, k -> new ArrayList<>()).add(detail);
        }

        // 构造报表第一层树状结果
        List<Map<String, Object>> reportData = new ArrayList<>();
        int parentKeyIndex = 1;
        for (Map.Entry<String, List<Map<String, Object>>> entry : treeHelper.entrySet()) {
            Map<String, Object> parent = new HashMap<>();
            parent.put("key", "parent_" + parentKeyIndex++);
            // 同样，第一级分组名字统一存放在 channel，以便前端 Table 的 channel 列完美合并展示
            parent.put("channel", getDisplayName(groupBy1, entry.getKey(), hotelNames, roomTypeNames, ratePlanNames));
            parent.put("groupCode", entry.getKey());
            parent.put("hotels", entry.getValue()); // 子级数组起名 hotels 契合前端 Map 结构
            reportData.add(parent);
        }

        // 5. 组装总计数据
        double totalAvgRate = totalRoomNights > 0 ? totalAmount.divide(BigDecimal.valueOf(totalRoomNights), 0, RoundingMode.HALF_UP).doubleValue() : 0.0;
        double prevTotalAvgRate = prevTotalRoomNights > 0 ? prevTotalAmount.divide(BigDecimal.valueOf(prevTotalRoomNights), 0, RoundingMode.HALF_UP).doubleValue() : 0.0;

        Map<String, Object> totalData = new HashMap<>();
        Map<String, Object> totalCur = new HashMap<>();
        totalCur.put("orderCount", totalOrderCount);
        totalCur.put("orderAmount", totalAmount.setScale(0, RoundingMode.HALF_UP).intValue());
        totalCur.put("orderPoints", totalPoints);
        totalCur.put("roomNights", totalRoomNights);
        totalCur.put("avgRate", (int) totalAvgRate);

        if (Boolean.TRUE.equals(enableCompare)) {
            totalCur.put("orderCountChange", calculateChangeRate(totalOrderCount, prevTotalOrderCount));
            totalCur.put("orderAmountChange", calculateChangeRate(totalAmount.doubleValue(), prevTotalAmount.doubleValue()));
            totalCur.put("orderPointsChange", calculateChangeRate(totalPoints, prevTotalPoints));
            totalCur.put("roomNightsChange", calculateChangeRate(totalRoomNights, prevTotalRoomNights));
            totalCur.put("avgRateChange", calculateChangeRate(totalAvgRate, prevTotalAvgRate));
        }

        Map<String, Object> totalPrev = new HashMap<>();
        totalPrev.put("orderCount", prevTotalOrderCount);
        totalPrev.put("orderAmount", prevTotalAmount.setScale(0, RoundingMode.HALF_UP).intValue());
        totalPrev.put("orderPoints", prevTotalPoints);
        totalPrev.put("roomNights", prevTotalRoomNights);
        totalPrev.put("avgRate", (int) prevTotalAvgRate);

        totalData.put("currentPeriod", totalCur);
        totalData.put("previousPeriod", totalPrev);

        Map<String, Object> result = new HashMap<>();
        result.put("reportData", reportData);
        result.put("totalData", totalData);
        return result;
    }

    /**
     * 动态 SQL 查询日预订聚合数据
     */
    private List<Map<String, Object>> queryAggregatedData(
            Integer tenantId, LocalDate start, LocalDate end,
            String hotelCode, String channelCode, String marketCode,
            String rateCategoryCode, String ratePlanCode,
            String orderStatus,
            String groupBy1, String groupBy2,
            String paymentMethod, Boolean memberBooking, Boolean canEarnPoints, Boolean onlineBooking) {

        boolean hasAdvancedFilter = (paymentMethod != null && !paymentMethod.trim().isEmpty())
                || (memberBooking != null && memberBooking)
                || (canEarnPoints != null && canEarnPoints)
                || (onlineBooking != null && onlineBooking);

        boolean g1Active = groupBy1 != null && !groupBy1.trim().isEmpty();
        boolean g2Active = groupBy2 != null && !groupBy2.trim().isEmpty();

        String col1 = g1Active ? (hasAdvancedFilter ? mapReservationColumn(groupBy1) : mapGroupByColumn(groupBy1)) : null;
        String col2 = g2Active ? (hasAdvancedFilter ? mapReservationColumn(groupBy2) : mapGroupByColumn(groupBy2)) : null;

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (hasAdvancedFilter) {
            sql.append("SELECT ");
            if (g1Active) {
                sql.append(col1).append(" as groupBy1Val, ");
            } else {
                sql.append("'ALL' as groupBy1Val, ");
            }
            if (g2Active) {
                sql.append(col2).append(" as groupBy2Val, ");
            } else {
                sql.append("'ALL' as groupBy2Val, ");
            }
            sql.append("COUNT(r.id) as orderCount, ")
               .append("SUM(r.room_count * r.nights) as roomNights, ")
               .append("SUM(r.total_price) as totalRevenue, ")
               .append("SUM(CASE WHEN r.rate_plan_code = 'BAR' THEN r.room_count * r.nights * 500 ELSE 0 END) as pointsRedeemed ")
               .append("FROM reservation r ")
               .append("LEFT JOIN rate_plans rp ON r.rate_plan_code = rp.rate_code AND r.hotel_code = rp.hotel_code AND r.tenant_id = rp.tenant_id ")
               .append("WHERE r.tenant_id = ? AND r.created_at >= ? AND r.created_at < ? ");

            params.add(tenantId);
            params.add(java.sql.Timestamp.valueOf(start.atStartOfDay()));
            params.add(java.sql.Timestamp.valueOf(end.plusDays(1).atStartOfDay()));

            if (hotelCode != null && !hotelCode.trim().isEmpty()) {
                sql.append("AND r.hotel_code = ? ");
                params.add(hotelCode);
            }
            if (channelCode != null && !channelCode.trim().isEmpty()) {
                sql.append("AND r.channel_code = ? ");
                params.add(channelCode);
            }
            if (marketCode != null && !marketCode.trim().isEmpty()) {
                sql.append("AND r.market_code = ? ");
                params.add(marketCode);
            }
            if (rateCategoryCode != null && !rateCategoryCode.trim().isEmpty()) {
                sql.append("AND rp.rate_category = ? ");
                params.add(rateCategoryCode);
            }
            if (ratePlanCode != null && !ratePlanCode.trim().isEmpty()) {
                sql.append("AND r.rate_plan_code = ? ");
                params.add(ratePlanCode);
            }
            if (orderStatus != null && !orderStatus.trim().isEmpty()) {
                sql.append("AND r.reservation_status = ? ");
                params.add(orderStatus);
            } else {
                sql.append("AND r.reservation_status IN ('confirmed', 'checked_in', 'completed') ");
            }

            // 拼接高级过滤条件：仅在勾选为 true 时限制
            if (memberBooking != null && memberBooking) {
                sql.append("AND r.member_no IS NOT NULL AND r.member_no != '' ");
            }
            if (onlineBooking != null && onlineBooking) {
                sql.append("AND r.is_manual = false ");
            }
            if (canEarnPoints != null && canEarnPoints) {
                sql.append("AND rp.allow_points = true ");
            }
            if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                if ("points".equalsIgnoreCase(paymentMethod)) {
                    sql.append("AND r.rate_plan_code = 'BAR' ");
                } else if ("non_points".equalsIgnoreCase(paymentMethod)) {
                    sql.append("AND (r.rate_plan_code IS NULL OR r.rate_plan_code != 'BAR') ");
                }
            }

            if (g1Active || g2Active) {
                sql.append("GROUP BY ");
                if (g1Active && g2Active) {
                    sql.append(col1).append(", ").append(col2);
                } else if (g1Active) {
                    sql.append(col1);
                } else {
                    sql.append(col2);
                }
            }
        } else {
            sql.append("SELECT ");
            if (g1Active) {
                sql.append(col1).append(" as groupBy1Val, ");
            } else {
                sql.append("'ALL' as groupBy1Val, ");
            }
            if (g2Active) {
                sql.append(col2).append(" as groupBy2Val, ");
            } else {
                sql.append("'ALL' as groupBy2Val, ");
            }
            sql.append("SUM(order_count) as orderCount, ")
               .append("SUM(room_nights) as roomNights, ")
               .append("SUM(total_revenue) as totalRevenue, ")
               .append("SUM(points_redeemed) as pointsRedeemed ")
               .append("FROM report_daily_reservation_summary ")
               .append("WHERE tenant_id = ? AND report_date BETWEEN ? AND ? ");

            params.add(tenantId);
            params.add(Date.valueOf(start));
            params.add(Date.valueOf(end));

            if (hotelCode != null && !hotelCode.trim().isEmpty()) {
                sql.append("AND hotel_code = ? ");
                params.add(hotelCode);
            }
            if (channelCode != null && !channelCode.trim().isEmpty()) {
                sql.append("AND channel_code = ? ");
                params.add(channelCode);
            }
            if (marketCode != null && !marketCode.trim().isEmpty()) {
                sql.append("AND market_code = ? ");
                params.add(marketCode);
            }
            if (rateCategoryCode != null && !rateCategoryCode.trim().isEmpty()) {
                sql.append("AND rate_category_code = ? ");
                params.add(rateCategoryCode);
            }
            if (ratePlanCode != null && !ratePlanCode.trim().isEmpty()) {
                sql.append("AND rate_plan_code = ? ");
                params.add(ratePlanCode);
            }
            if (orderStatus != null && !orderStatus.trim().isEmpty()) {
                sql.append("AND reservation_status = ? ");
                params.add(orderStatus);
            } else {
                sql.append("AND reservation_status IN ('confirmed', 'checked_in', 'completed') ");
            }

            if (g1Active || g2Active) {
                sql.append("GROUP BY ");
                if (g1Active && g2Active) {
                    sql.append(col1).append(", ").append(col2);
                } else if (g1Active) {
                    sql.append(col1);
                } else {
                    sql.append(col2);
                }
            }
        }

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    private String mapGroupByColumn(String groupBy) {
        if (groupBy == null) return "hotel_code";
        switch (groupBy) {
            case "channel": return "channel_code";
            case "market": return "market_code";
            case "roomType": return "room_type_code";
            case "ratePlan": return "rate_plan_code";
            case "rateCategory": return "rate_category_code";
            case "hotel":
            default:
                return "hotel_code";
        }
    }

    private String mapReservationColumn(String groupBy) {
        if (groupBy == null) return "r.hotel_code";
        switch (groupBy) {
            case "channel": return "r.channel_code";
            case "market": return "r.market_code";
            case "roomType": return "r.room_type_code";
            case "ratePlan": return "r.rate_plan_code";
            case "rateCategory": return "COALESCE(rp.rate_category, 'ROOM')";
            case "hotel":
            default:
                return "r.hotel_code";
        }
    }

    private String getDisplayName(String type, String code, 
                                   Map<String, String> hotels, 
                                   Map<String, String> roomTypes, 
                                   Map<String, String> ratePlans) {
        if (code == null) return "-";
        if ("ALL".equals(code)) return "全部";
        switch (type) {
            case "hotel":
                return hotels.getOrDefault(code, code);
            case "roomType":
                return roomTypes.getOrDefault(code, code);
            case "ratePlan":
                return ratePlans.getOrDefault(code, code);
            case "channel":
                return CHANNEL_MAP.getOrDefault(code, code);
            case "market":
                return MARKET_MAP.getOrDefault(code, code);
            case "rateCategory":
                return CATEGORY_MAP.getOrDefault(code, code);
            default:
                return code;
        }
    }

    private String calculateChangeRate(double current, double previous) {
        if (previous == 0.0) {
            return current > 0.0 ? "↑100%" : "0%";
        }
        double change = (current - previous) / previous * 100.0;
        int rounded = (int) Math.round(change);
        if (rounded > 0) {
            return "↑" + rounded + "%";
        } else if (rounded < 0) {
            return "↓" + Math.abs(rounded) + "%";
        } else {
            return "0%";
        }
    }

    private String calculateChangeRate(int current, int previous) {
        return calculateChangeRate((double) current, (double) previous);
    }

    @Override
    public List<Map<String, Object>> queryOccupancyReport(
            Integer tenantId, String hotelCode, LocalDate monthDate, String statisticMethod) {
        
        LocalDate startDate = monthDate.withDayOfMonth(1);
        LocalDate endDate = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
        
        // 自动初始化对账
        initializeSummaryData(tenantId, startDate, endDate);
        
        // 获取基础字典映射
        Map<String, String> hotelNames = hotelRepository.findAll().stream()
                .collect(Collectors.toMap(Hotel::getHotelCode, Hotel::getChineseName, (a, b) -> a));
        Map<String, String> roomTypeNames = roomTypeRepository.findAll().stream()
                .collect(Collectors.toMap(RoomType::getCode, RoomType::getName, (a, b) -> a));

        // 查出当月所有 pms_inventory 数据
        String pmsSql = "SELECT hotel_code, room_type_code, inventory_date, physical_rooms, available_rooms, maintenance_rooms, overbook_count " +
                        "FROM pms_inventory " +
                        "WHERE tenant_id = ? AND inventory_date BETWEEN ? AND ?";
        List<Object> pmsParams = new ArrayList<>();
        pmsParams.add(tenantId);
        pmsParams.add(Date.valueOf(startDate));
        pmsParams.add(Date.valueOf(endDate));

        boolean isGlobal = (hotelCode == null || hotelCode.trim().isEmpty() || "全集团".equals(hotelCode.trim()));
        if (!isGlobal) {
            pmsSql += " AND hotel_code = ?";
            pmsParams.add(hotelCode.trim());
        }
        List<Map<String, Object>> pmsRows = jdbcTemplate.queryForList(pmsSql, pmsParams.toArray());

        // 查出当月所有预订汇总数据
        String summarySql = "SELECT report_date, hotel_code, room_type_code, SUM(order_count) as orderCount " +
                            "FROM report_daily_reservation_summary " +
                            "WHERE tenant_id = ? AND report_date BETWEEN ? AND ?";
        List<Object> summaryParams = new ArrayList<>();
        summaryParams.add(tenantId);
        summaryParams.add(Date.valueOf(startDate));
        summaryParams.add(Date.valueOf(endDate));

        if (!isGlobal) {
            summarySql += " AND hotel_code = ?";
            summaryParams.add(hotelCode.trim());
        }
        summarySql += " GROUP BY report_date, hotel_code, room_type_code";
        List<Map<String, Object>> summaryRows = jdbcTemplate.queryForList(summarySql, summaryParams.toArray());

        // 将订单汇总按日期和分组建立快速查找
        Map<String, Map<Integer, Integer>> orderSummaryMap = new HashMap<>();
        for (Map<String, Object> row : summaryRows) {
            LocalDate rDate = ((java.sql.Date) row.get("report_date")).toLocalDate();
            int day = rDate.getDayOfMonth();
            String hCode = (String) row.get("hotel_code");
            String rtCode = (String) row.get("room_type_code");
            int orderCount = ((Number) row.get("orderCount")).intValue();

            String dimKey = "按房型纬度".equals(statisticMethod) ? (hCode + "_" + rtCode) : hCode;
            orderSummaryMap.computeIfAbsent(dimKey, k -> new HashMap<>())
                    .merge(day, orderCount, Integer::sum);
        }

        // 指标聚合容器
        Map<String, Map<String, Map<Integer, Double>>> metricMap = new LinkedHashMap<>();

        // 全集团汇总指标
        String groupKey = "全集团";
        metricMap.put(groupKey, createEmptyMetrics());

        for (Map<String, Object> cur : pmsRows) {
            String hCode = (String) cur.get("hotel_code");
            String rtCode = (String) cur.get("room_type_code");
            LocalDate iDate = ((java.sql.Date) cur.get("inventory_date")).toLocalDate();
            int day = iDate.getDayOfMonth();

            int physical = ((Number) cur.get("physical_rooms")).intValue();
            int available = ((Number) cur.get("available_rooms")).intValue();
            int maintenance = ((Number) cur.get("maintenance_rooms")).intValue();
            int overbook = ((Number) cur.get("overbook_count")).intValue();
            int sold = physical + overbook - available - maintenance;

            String dimKey = "按房型纬度".equals(statisticMethod) ? (hCode + "_" + rtCode) : hCode;
            Map<String, Map<Integer, Double>> hotelMetrics = metricMap.computeIfAbsent(dimKey, k -> createEmptyMetrics());

            hotelMetrics.get("酒店总房量").merge(day, (double) physical, Double::sum);
            hotelMetrics.get("维修房").merge(day, (double) maintenance, Double::sum);
            hotelMetrics.get("已卖房").merge(day, (double) sold, Double::sum);

            if (isGlobal) {
                Map<String, Map<Integer, Double>> groupMetrics = metricMap.get(groupKey);
                groupMetrics.get("酒店总房量").merge(day, (double) physical, Double::sum);
                groupMetrics.get("维修房").merge(day, (double) maintenance, Double::sum);
                groupMetrics.get("已卖房").merge(day, (double) sold, Double::sum);
            }
        }

        int daysInMonth = monthDate.lengthOfMonth();
        for (Map.Entry<String, Map<String, Map<Integer, Double>>> entry : metricMap.entrySet()) {
            String dimKey = entry.getKey();
            Map<String, Map<Integer, Double>> metrics = entry.getValue();

            Map<Integer, Integer> orderDays = orderSummaryMap.getOrDefault(dimKey, Collections.emptyMap());
            if (groupKey.equals(dimKey)) {
                for (int d = 1; d <= daysInMonth; d++) {
                    double groupOrders = 0;
                    for (Map.Entry<String, Map<Integer, Integer>> ordEntry : orderSummaryMap.entrySet()) {
                        if (!ordEntry.getKey().equals(groupKey)) {
                            groupOrders += ordEntry.getValue().getOrDefault(d, 0);
                        }
                    }
                    metrics.get("订单数").put(d, groupOrders);
                }
            } else {
                for (int d = 1; d <= daysInMonth; d++) {
                    metrics.get("订单数").put(d, (double) orderDays.getOrDefault(d, 0));
                }
            }

            Map<Integer, Double> physicalMap = metrics.get("酒店总房量");
            Map<Integer, Double> maintenanceMap = metrics.get("维修房");
            Map<Integer, Double> soldMap = metrics.get("已卖房");
            Map<Integer, Double> occMap = metrics.get("出租率");

            for (int d = 1; d <= daysInMonth; d++) {
                double p = physicalMap.getOrDefault(d, 0.0);
                double m = maintenanceMap.getOrDefault(d, 0.0);
                double s = soldMap.getOrDefault(d, 0.0);
                double capacity = p - m;
                if (capacity > 0) {
                    double occ = (s / capacity) * 100.0;
                    BigDecimal bd = BigDecimal.valueOf(occ).setScale(1, RoundingMode.HALF_UP);
                    occMap.put(d, bd.doubleValue());
                } else {
                    occMap.put(d, 0.0);
                }
            }
        }

        List<Map<String, Object>> reportList = new ArrayList<>();
        List<String> sortedDimKeys = new ArrayList<>(metricMap.keySet());
        if (!isGlobal) {
            sortedDimKeys.remove(groupKey);
        }

        int keyIdx = 1;
        String[] metricTypes = {"酒店总房量", "维修房", "已卖房", "订单数", "出租率"};
        for (String dimKey : sortedDimKeys) {
            Map<String, Map<Integer, Double>> metrics = metricMap.get(dimKey);

            String hotelName = "全集团";
            String roomTypeName = "全房型";

            if (!groupKey.equals(dimKey)) {
                if ("按房型纬度".equals(statisticMethod)) {
                    String[] parts = dimKey.split("_");
                    hotelName = hotelNames.getOrDefault(parts[0], parts[0]);
                    roomTypeName = roomTypeNames.getOrDefault(parts[1], parts[1]);
                } else {
                    hotelName = hotelNames.getOrDefault(dimKey, dimKey);
                }
            }

            for (String metricType : metricTypes) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("key", "occ_" + keyIdx++);
                row.put("hotel", hotelName);
                if ("按房型纬度".equals(statisticMethod)) {
                    row.put("roomType", roomTypeName);
                }
                row.put("inventoryType", metricType);

                Map<Integer, Double> dayValues = metrics.get(metricType);
                for (int d = 1; d <= 31; d++) {
                    if (d <= daysInMonth) {
                        double val = dayValues.getOrDefault(d, 0.0);
                        if ("出租率".equals(metricType)) {
                            row.put("day" + d, val);
                        } else {
                            row.put("day" + d, (int) val);
                        }
                    } else {
                        row.put("day" + d, 0);
                    }
                }
                reportList.add(row);
            }
        }

        return reportList;
    }

    @Override
    public List<Map<String, Object>> queryRevenueReport(
            Integer tenantId, String hotelCode, LocalDate monthDate, String statisticMethod) {
        
        LocalDate startDate = monthDate.withDayOfMonth(1);
        LocalDate endDate = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
        
        // 自动初始化对账
        initializeSummaryData(tenantId, startDate, endDate);
        
        Map<String, String> hotelNames = hotelRepository.findAll().stream()
                .collect(Collectors.toMap(Hotel::getHotelCode, Hotel::getChineseName, (a, b) -> a));
        Map<String, String> roomTypeNames = roomTypeRepository.findAll().stream()
                .collect(Collectors.toMap(RoomType::getCode, RoomType::getName, (a, b) -> a));

        // 查出当月所有预订汇总数据
        String summarySql = "SELECT report_date, hotel_code, room_type_code, SUM(order_count) as orderCount, SUM(room_nights) as roomNights, SUM(total_revenue) as totalRevenue " +
                            "FROM report_daily_reservation_summary " +
                            "WHERE tenant_id = ? AND report_date BETWEEN ? AND ?";
        List<Object> summaryParams = new ArrayList<>();
        summaryParams.add(tenantId);
        summaryParams.add(Date.valueOf(startDate));
        summaryParams.add(Date.valueOf(endDate));

        boolean isGlobal = (hotelCode == null || hotelCode.trim().isEmpty() || "全集团".equals(hotelCode.trim()));
        if (!isGlobal) {
            summarySql += " AND hotel_code = ?";
            summaryParams.add(hotelCode.trim());
        }
        summarySql += " GROUP BY report_date, hotel_code, room_type_code";
        List<Map<String, Object>> summaryRows = jdbcTemplate.queryForList(summarySql, summaryParams.toArray());

        Map<String, Map<String, Map<Integer, Double>>> metricMap = new LinkedHashMap<>();

        // 全集团汇总指标
        String groupKey = "全集团";
        metricMap.put(groupKey, createEmptyRevenueMetrics());

        for (Map<String, Object> row : summaryRows) {
            LocalDate rDate = ((java.sql.Date) row.get("report_date")).toLocalDate();
            int day = rDate.getDayOfMonth();
            String hCode = (String) row.get("hotel_code");
            String rtCode = (String) row.get("room_type_code");
            
            int count = ((Number) row.get("orderCount")).intValue();
            int nights = ((Number) row.get("roomNights")).intValue();
            double revenue = ((Number) row.get("totalRevenue")).doubleValue();

            String dimKey = "按房型纬度".equals(statisticMethod) ? (hCode + "_" + rtCode) : hCode;
            Map<String, Map<Integer, Double>> hotelMetrics = metricMap.computeIfAbsent(dimKey, k -> createEmptyRevenueMetrics());

            hotelMetrics.get("总订单数").merge(day, (double) count, Double::sum);
            hotelMetrics.get("roomNights").merge(day, (double) nights, Double::sum);
            hotelMetrics.get("totalRevenue").merge(day, revenue, Double::sum);

            if (isGlobal) {
                Map<String, Map<Integer, Double>> groupMetrics = metricMap.get(groupKey);
                groupMetrics.get("总订单数").merge(day, (double) count, Double::sum);
                groupMetrics.get("roomNights").merge(day, (double) nights, Double::sum);
                groupMetrics.get("totalRevenue").merge(day, revenue, Double::sum);
            }
        }

        int daysInMonth = monthDate.lengthOfMonth();
        for (Map.Entry<String, Map<String, Map<Integer, Double>>> entry : metricMap.entrySet()) {
            Map<String, Map<Integer, Double>> metrics = entry.getValue();
            Map<Integer, Double> revenueMap = metrics.get("totalRevenue");
            Map<Integer, Double> nightsMap = metrics.get("roomNights");
            Map<Integer, Double> adrMap = metrics.get("平均房价");

            for (int d = 1; d <= daysInMonth; d++) {
                double rev = revenueMap.getOrDefault(d, 0.0);
                double nights = nightsMap.getOrDefault(d, 0.0);
                if (nights > 0) {
                    adrMap.put(d, (double) Math.round(rev / nights));
                } else {
                    adrMap.put(d, 0.0);
                }
            }
        }

        List<Map<String, Object>> reportList = new ArrayList<>();
        List<String> sortedDimKeys = new ArrayList<>(metricMap.keySet());
        if (!isGlobal) {
            sortedDimKeys.remove(groupKey);
        }

        int keyIdx = 1;
        String[] metricTypes = {"总订单数", "平均房价"};
        for (String dimKey : sortedDimKeys) {
            Map<String, Map<Integer, Double>> metrics = metricMap.get(dimKey);

            String hotelName = "全集团";
            String roomTypeName = "全房型";

            if (!groupKey.equals(dimKey)) {
                if ("按房型纬度".equals(statisticMethod)) {
                    String[] parts = dimKey.split("_");
                    hotelName = hotelNames.getOrDefault(parts[0], parts[0]);
                    roomTypeName = roomTypeNames.getOrDefault(parts[1], parts[1]);
                } else {
                    hotelName = hotelNames.getOrDefault(dimKey, dimKey);
                }
            }

            for (String metricType : metricTypes) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("key", "rev_" + keyIdx++);
                row.put("hotel", hotelName);
                if ("按房型纬度".equals(statisticMethod)) {
                    row.put("roomType", roomTypeName);
                }
                row.put("inventoryType", metricType);

                Map<Integer, Double> dayValues = metrics.get(metricType);
                for (int d = 1; d <= 31; d++) {
                    if (d <= daysInMonth) {
                        double val = dayValues.getOrDefault(d, 0.0);
                        row.put("day" + d, (int) val);
                    } else {
                        row.put("day" + d, 0);
                    }
                }
                reportList.add(row);
            }
        }

        return reportList;
    }

    private Map<String, Map<Integer, Double>> createEmptyMetrics() {
        Map<String, Map<Integer, Double>> map = new LinkedHashMap<>();
        map.put("酒店总房量", new HashMap<>());
        map.put("维修房", new HashMap<>());
        map.put("已卖房", new HashMap<>());
        map.put("订单数", new HashMap<>());
        map.put("出租率", new HashMap<>());
        return map;
    }

    private Map<String, Map<Integer, Double>> createEmptyRevenueMetrics() {
        Map<String, Map<Integer, Double>> map = new LinkedHashMap<>();
        map.put("总订单数", new HashMap<>());
        map.put("roomNights", new HashMap<>());
        map.put("totalRevenue", new HashMap<>());
        map.put("平均房价", new HashMap<>());
        return map;
    }
}
