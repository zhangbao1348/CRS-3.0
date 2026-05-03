package com.crs.controller;

import com.crs.entity.*;
import com.crs.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 开放API - 酒店查询接口
 * 接口1: GET /api/open/hotels        查询酒店列表
 * 接口2: GET /api/open/hotels/{code} 查询酒店详情与价格
 * 接口3: POST /api/open/availability/check 可订检查
 */
@RestController
@RequestMapping("/api/open")
public class OpenHotelController {

    @Autowired private HotelRepository hotelRepo;
    @Autowired private HotelRoomTypeRepository roomTypeRepo;
    @Autowired private HotelImageRepository imageRepo;
    @Autowired private HotelFacilityRepository facilityRepo;
    @Autowired private RoomTypeFacilityRepository roomTypeFacilityRepo;
    @Autowired private RatePlanRepository ratePlanRepo;
    @Autowired private HotelPriceRepository priceRepo;
    @Autowired private InventoryRepository inventoryRepo;
    @Autowired private RoomStatusRepository roomStatusRepo;
    @Autowired private BookingControlRepository bookingControlRepo;
    @Autowired private CancellationPolicyRepository cancellationPolicyRepo;
    @Autowired private GuaranteePolicyRepository guaranteePolicyRepo;
    @Autowired private ChannelHotelMappingRepository channelHotelMappingRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String now() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "success");
        r.put("data", data); r.put("timestamp", now());
        return r;
    }

    private Map<String, Object> err(int code, String message) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", code); r.put("message", message);
        r.put("timestamp", now());
        return r;
    }

    /** 从请求中获取当前渠道（由 OpenApiAuthFilter 注入） */
    private TenantChannel getChannel(HttpServletRequest req) {
        return (TenantChannel) req.getAttribute("openApiChannel");
    }

    /** 校验渠道是否有权访问该酒店 */
    private boolean hasHotelAccess(TenantChannel channel, Integer hotelId) {
        List<ChannelHotelMapping> mappings = channelHotelMappingRepo
                .findByChannelIdAndHotelId(channel.getId(), hotelId);
        return mappings.stream().anyMatch(m -> "active".equals(m.getStatus()));
    }

    // =========================================================================
    // 接口1: GET /api/open/hotels  查询酒店列表
    // =========================================================================
    @GetMapping("/hotels")
    public ResponseEntity<?> listHotels(
            HttpServletRequest req,
            @RequestParam(required = false) String cityId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkInDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkOutDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            TenantChannel channel = getChannel(req);
            if (pageSize > 50) pageSize = 50;

            // 获取该渠道有权访问的酒店ID列表
            List<ChannelHotelMapping> mappings = channelHotelMappingRepo.findByChannelId(channel.getId())
                    .stream().filter(m -> "active".equals(m.getStatus())).collect(Collectors.toList());
            Set<Integer> allowedHotelIds = mappings.stream()
                    .map(ChannelHotelMapping::getHotelId).collect(Collectors.toSet());

            // 查询 active 酒店
            List<Hotel> hotels = hotelRepo.findByTenantIdAndStatus(channel.getTenantId(), Hotel.Status.active)
                    .stream()
                    .filter(h -> allowedHotelIds.contains(h.getId()))
                    .filter(h -> cityId == null || cityId.isBlank() || cityId.equals(h.getCity()))
                    .filter(h -> keyword == null || keyword.isBlank()
                            || (h.getChineseName() != null && h.getChineseName().contains(keyword))
                            || (h.getEnglishName() != null && h.getEnglishName().toLowerCase().contains(keyword.toLowerCase())))
                    .collect(Collectors.toList());

            int total = hotels.size();
            int fromIdx = Math.min((page - 1) * pageSize, total);
            int toIdx = Math.min(fromIdx + pageSize, total);
            List<Hotel> paged = hotels.subList(fromIdx, toIdx);

            List<Map<String, Object>> list = new ArrayList<>();
            for (Hotel hotel : paged) {
                list.add(buildHotelSummary(hotel, checkInDate, checkOutDate));
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("list", list);
            return ResponseEntity.ok(ok(data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(err(500, e.getMessage()));
        }
    }

    private Map<String, Object> buildHotelSummary(Hotel hotel, Date checkIn, Date checkOut) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("hotelCode", hotel.getHotelCode());
        m.put("chineseName", hotel.getChineseName());
        m.put("englishName", hotel.getEnglishName());
        m.put("city", hotel.getCity());
        m.put("province", hotel.getProvince());
        m.put("address", hotel.getAddress());
        m.put("phone", hotel.getPhone());
        m.put("starRating", hotel.getStarRating());
        m.put("latitude", hotel.getLatitude());
        m.put("longitude", hotel.getLongitude());
        m.put("totalRooms", hotel.getTotalRooms());
        m.put("introduction", hotel.getIntroduction());
        m.put("currency", "CNY");

        // 起价计算
        BigDecimal startingPrice = null;
        if (checkIn != null && checkOut != null) {
            List<HotelPrice> prices = priceRepo.findByTenantIdAndHotelCodeAndPriceDateBetween(
                    hotel.getTenantId(), hotel.getHotelCode(), checkIn, checkOut);
            startingPrice = prices.stream()
                    .filter(p -> p.getPriceWithTax() != null)
                    .map(HotelPrice::getPriceWithTax)
                    .min(BigDecimal::compareTo).orElse(null);
        }
        m.put("startingPrice", startingPrice);

        // 图片
        List<HotelImage> images = imageRepo.findByHotelIdOrderBySortOrderAsc(hotel.getId());
        m.put("images", images.stream().map(img -> {
            Map<String, Object> im = new LinkedHashMap<>();
            im.put("imageType", img.getImageType());
            im.put("imagePath", img.getImagePath());
            im.put("imageName", img.getImageName());
            im.put("description", img.getDescription());
            im.put("sortOrder", img.getSortOrder());
            return im;
        }).collect(Collectors.toList()));

        // 设施
        List<HotelFacility> facilities = facilityRepo.findByHotelIdAndAvailable(hotel.getId(), true);
        m.put("facilities", facilities.stream().map(f -> {
            Map<String, Object> fm = new LinkedHashMap<>();
            fm.put("facilityType", f.getFacilityType());
            fm.put("facilityCode", f.getFacilityCode());
            fm.put("facilityName", f.getFacilityName());
            return fm;
        }).collect(Collectors.toList()));

        // 房型静态信息
        List<HotelRoomType> roomTypes = roomTypeRepo.findByHotelIdAndStatus(hotel.getId(), "active");
        m.put("roomTypes", roomTypes.stream().map(rt -> buildRoomTypeSummary(rt)).collect(Collectors.toList()));

        return m;
    }

    private Map<String, Object> buildRoomTypeSummary(HotelRoomType rt) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("roomTypeCode", rt.getRoomTypeCode());
        m.put("roomTypeName", rt.getRoomTypeName());
        m.put("englishName", rt.getEnglishName());
        m.put("description", rt.getDescription());
        m.put("maxOccupancy", rt.getMaxOccupancy());
        int maxChildren = rt.getMaxChildren() != null ? rt.getMaxChildren() : 0;
        m.put("maxAdults", rt.getMaxOccupancy() - maxChildren);
        m.put("maxChildren", maxChildren);
        m.put("area", rt.getArea());
        m.put("floor", rt.getFloor());
        m.put("windowType", rt.getWindowType());
        m.put("bedType", rt.getBedType());
        m.put("roomQuantity", rt.getTotalRooms());
        m.put("sortOrder", rt.getSortOrder());
        m.put("image", null); // 房型图片暂无独立图片表，预留

        // 房型设施
        List<RoomTypeFacility> rtFacilities = roomTypeFacilityRepo.findByRoomTypeId(rt.getId())
                .stream().filter(f -> Boolean.TRUE.equals(f.getAvailable())).collect(Collectors.toList());
        m.put("facilities", rtFacilities.stream().map(f -> {
            Map<String, Object> fm = new LinkedHashMap<>();
            fm.put("facilityType", f.getFacilityType());
            fm.put("facilityCode", f.getFacilityCode());
            fm.put("facilityName", f.getFacilityName());
            return fm;
        }).collect(Collectors.toList()));

        return m;
    }

    // =========================================================================
    // 接口2: GET /api/open/hotels/{hotelCode}  查询酒店详情与价格
    // =========================================================================
    @GetMapping("/hotels/{hotelCode}")
    public ResponseEntity<?> getHotelDetail(
            HttpServletRequest req,
            @PathVariable String hotelCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkInDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkOutDate,
            @RequestParam(defaultValue = "2") int adultCount,
            @RequestParam(defaultValue = "0") int childCount,
            @RequestParam(defaultValue = "1") int roomCount,
            @RequestParam(required = false) String memberNo,
            @RequestParam(required = false) String memberLevel) {
        try {
            TenantChannel channel = getChannel(req);

            Hotel hotel = hotelRepo.findByHotelCode(hotelCode).orElse(null);
            if (hotel == null || hotel.getStatus() != Hotel.Status.active) {
                return ResponseEntity.status(404).body(err(404, "酒店不存在"));
            }
            if (!hasHotelAccess(channel, hotel.getId())) {
                return ResponseEntity.status(403).body(err(403, "渠道无权访问该酒店"));
            }

            long nights = (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24);
            if (nights <= 0) {
                return ResponseEntity.badRequest().body(err(400, "离店日期必须晚于入住日期"));
            }

            // 酒店基础信息
            Map<String, Object> hotelInfo = new LinkedHashMap<>();
            hotelInfo.put("hotelCode", hotel.getHotelCode());
            hotelInfo.put("chineseName", hotel.getChineseName());
            hotelInfo.put("englishName", hotel.getEnglishName());
            hotelInfo.put("city", hotel.getCity());
            hotelInfo.put("province", hotel.getProvince());
            hotelInfo.put("address", hotel.getAddress());
            hotelInfo.put("phone", hotel.getPhone());
            hotelInfo.put("email", hotel.getEmail());
            hotelInfo.put("starRating", hotel.getStarRating());
            hotelInfo.put("latitude", hotel.getLatitude());
            hotelInfo.put("longitude", hotel.getLongitude());
            hotelInfo.put("totalRooms", hotel.getTotalRooms());
            hotelInfo.put("introduction", hotel.getIntroduction());

            List<HotelImage> images = imageRepo.findByHotelIdOrderBySortOrderAsc(hotel.getId());
            hotelInfo.put("images", images.stream().map(img -> {
                Map<String, Object> im = new LinkedHashMap<>();
                im.put("imageType", img.getImageType()); im.put("imagePath", img.getImagePath());
                im.put("imageName", img.getImageName()); im.put("description", img.getDescription());
                im.put("sortOrder", img.getSortOrder()); return im;
            }).collect(Collectors.toList()));

            List<HotelFacility> facilities = facilityRepo.findByHotelIdAndAvailable(hotel.getId(), true);
            hotelInfo.put("facilities", facilities.stream().map(f -> {
                Map<String, Object> fm = new LinkedHashMap<>();
                fm.put("facilityType", f.getFacilityType()); fm.put("facilityCode", f.getFacilityCode());
                fm.put("facilityName", f.getFacilityName()); return fm;
            }).collect(Collectors.toList()));

            // 房型列表（含价格计划）
            List<HotelRoomType> roomTypes = roomTypeRepo.findByHotelIdAndStatus(hotel.getId(), "active");
            List<RatePlan> allRatePlans = ratePlanRepo.findByHotelIdAndStatus(hotel.getId(), "active");

            // 过滤价格计划：无会员信息时只返回 personal_membership 为空的计划
            List<RatePlan> visiblePlans = allRatePlans.stream().filter(rp -> {
                String pm = rp.getPersonalMembership();
                if (memberLevel == null || memberLevel.isBlank()) {
                    return pm == null || pm.isBlank() || pm.equals("null") || pm.equals("[]") || pm.equals("{}");
                }
                // 有会员信息时，返回无会员限制的 + 匹配会员等级的
                if (pm == null || pm.isBlank() || pm.equals("null") || pm.equals("[]") || pm.equals("{}")) return true;
                return pm.contains(memberLevel);
            }).collect(Collectors.toList());

            // 获取日期范围内的价格数据（checkIn 到 checkOut 前一天）
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkOutDate); cal.add(Calendar.DATE, -1);
            Date lastNight = cal.getTime();

            List<HotelPrice> allPrices = priceRepo.findByTenantIdAndHotelCodeAndPriceDateBetween(
                    hotel.getTenantId(), hotelCode, checkInDate, lastNight);

            // 获取房态数据
            List<RoomStatusRecord> roomStatuses = roomStatusRepo
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndStatusDateBetween(
                            hotel.getTenantId(), hotelCode, "room_type", "", checkInDate, lastNight);

            // 获取预订控制数据
            List<BookingControl> bookingControls = bookingControlRepo
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDateBetween(
                            hotel.getTenantId(), hotelCode, "hotel", "", checkInDate, lastNight);

            List<Map<String, Object>> roomTypeResults = new ArrayList<>();
            for (HotelRoomType rt : roomTypes) {
                Map<String, Object> rtMap = buildRoomTypeSummary(rt);

                // 为该房型构建价格计划列表
                List<Map<String, Object>> ratePlanResults = new ArrayList<>();
                for (RatePlan rp : visiblePlans) {
                    // 检查价格计划是否适用该房型
                    String applicableJson = rp.getApplicableRoomTypes();
                    if (applicableJson != null && !applicableJson.isBlank()
                            && !applicableJson.equals("[]") && !applicableJson.equals("null")) {
                        try {
                            List<String> applicable = objectMapper.readValue(applicableJson, new TypeReference<>() {});
                            if (!applicable.isEmpty() && !applicable.contains(rt.getRoomTypeCode())) continue;
                        } catch (Exception ignored) {}
                    }

                    // 获取该房型+价格计划的每日价格
                    List<HotelPrice> rpPrices = allPrices.stream()
                            .filter(p -> rp.getRateCode().equals(p.getRateCode())
                                    && rt.getRoomTypeCode().equals(p.getRoomTypeCode()))
                            .sorted(Comparator.comparing(HotelPrice::getPriceDate))
                            .collect(Collectors.toList());

                    if (rpPrices.isEmpty()) continue; // 无价格则不展示

                    // 每日价格列表
                    List<Map<String, Object>> dailyPrices = rpPrices.stream().map(p -> {
                        Map<String, Object> dp = new LinkedHashMap<>();
                        dp.put("date", formatDate(p.getPriceDate()));
                        dp.put("priceWithTax", p.getPriceWithTax());
                        return dp;
                    }).collect(Collectors.toList());

                    BigDecimal totalPrice = rpPrices.stream()
                            .filter(p -> p.getPriceWithTax() != null)
                            .map(HotelPrice::getPriceWithTax)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .multiply(BigDecimal.valueOf(roomCount));
                    BigDecimal avgPrice = nights > 0 ? totalPrice.divide(BigDecimal.valueOf(nights), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;

                    // 库存：按渠道channel_code查询，取最小值
                    List<Inventory> invList = inventoryRepo.findByHotelIdAndChannelIdAndDateBetween(
                            hotel.getId(), channel.getId(), checkInDate, lastNight)
                            .stream().filter(inv -> rp.getId().equals(inv.getRatePlanId())
                                    && rt.getId().equals(inv.getRoomTypeId()))
                            .collect(Collectors.toList());
                    int availableRooms = invList.stream()
                            .mapToInt(Inventory::getAvailableRooms).min().orElse(0);

                    // 包价信息
                    List<Map<String, Object>> packages = parsePackages(rp.getPackages());

                    // 取消政策
                    Map<String, Object> cancellationPolicy = buildCancellationPolicy(rp.getCancellationRule());

                    // 担保政策
                    Map<String, Object> guaranteePolicy = buildGuaranteePolicy(rp.getGuaranteeRule());

                    // 预订规则（优先 booking_controls，无则取 rate_plans）
                    Map<String, Object> bookingRules = buildBookingRules(rp, bookingControls);

                    Map<String, Object> rpMap = new LinkedHashMap<>();
                    rpMap.put("ratePlanCode", rp.getRateCode());
                    rpMap.put("ratePlanName", rp.getRateName());
                    rpMap.put("rateType", rp.getRateType());
                    rpMap.put("rateCategory", rp.getRateCategory());
                    rpMap.put("memberLevelCode", parseMemberLevel(rp.getPersonalMembership()));
                    rpMap.put("description", rp.getDescription());
                    rpMap.put("availableRooms", availableRooms);
                    rpMap.put("totalPrice", totalPrice);
                    rpMap.put("averagePrice", avgPrice);
                    rpMap.put("currency", "CNY");
                    rpMap.put("dailyPrices", dailyPrices);
                    rpMap.put("packages", packages);
                    rpMap.put("cancellationPolicy", cancellationPolicy);
                    rpMap.put("guaranteePolicy", guaranteePolicy);
                    rpMap.put("bookingRules", bookingRules);
                    ratePlanResults.add(rpMap);
                }

                rtMap.put("ratePlans", ratePlanResults);
                roomTypeResults.add(rtMap);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("hotel", hotelInfo);
            data.put("roomTypes", roomTypeResults);
            return ResponseEntity.ok(ok(data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(err(500, e.getMessage()));
        }
    }

    // =========================================================================
    // 接口3: POST /api/open/availability/check  可订检查
    // =========================================================================
    @PostMapping("/availability/check")
    public ResponseEntity<?> checkAvailability(
            HttpServletRequest req,
            @RequestBody Map<String, Object> body) {
        try {
            TenantChannel channel = getChannel(req);

            String hotelCode = (String) body.get("hotelCode");
            String roomTypeCode = (String) body.get("roomTypeCode");
            String ratePlanCode = (String) body.get("ratePlanCode");
            String checkInStr = (String) body.get("checkInDate");
            String checkOutStr = (String) body.get("checkOutDate");
            int roomCount = body.containsKey("roomCount") ? ((Number) body.get("roomCount")).intValue() : 1;
            int adultCount = body.containsKey("adultCount") ? ((Number) body.get("adultCount")).intValue() : 2;
            int childCount = body.containsKey("childCount") ? ((Number) body.get("childCount")).intValue() : 0;
            String memberLevel = (String) body.getOrDefault("memberLevel", null);

            if (hotelCode == null || roomTypeCode == null || ratePlanCode == null
                    || checkInStr == null || checkOutStr == null) {
                return ResponseEntity.badRequest().body(err(400, "缺少必填参数"));
            }

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInStr);
            Date checkOut = sdf.parse(checkOutStr);
            long nights = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
            if (nights <= 0) return ResponseEntity.badRequest().body(err(400, "离店日期必须晚于入住日期"));

            Calendar cal = Calendar.getInstance();
            cal.setTime(checkOut); cal.add(Calendar.DATE, -1);
            Date lastNight = cal.getTime();

            // 1. 渠道权限
            Hotel hotel = hotelRepo.findByHotelCode(hotelCode).orElse(null);
            if (hotel == null || hotel.getStatus() != Hotel.Status.active) {
                return ResponseEntity.status(409).body(unavailable("HOTEL_INACTIVE", "酒店不存在或已停用", null));
            }
            if (!hasHotelAccess(channel, hotel.getId())) {
                return ResponseEntity.status(403).body(err(403, "渠道无权访问该酒店"));
            }

            // 2. 房型有效性
            HotelRoomType roomType = roomTypeRepo.findByHotelIdAndRoomTypeCode(hotel.getId(), roomTypeCode).orElse(null);
            if (roomType == null || !"active".equals(roomType.getStatus())) {
                return ResponseEntity.status(409).body(unavailable("ROOM_TYPE_INACTIVE", "房型不存在或已停用", null));
            }

            // 3. 价格计划有效性
            RatePlan ratePlan = ratePlanRepo.findByHotelIdAndRateCode(hotel.getId(), ratePlanCode).orElse(null);
            if (ratePlan == null || !"active".equals(ratePlan.getStatus())) {
                return ResponseEntity.status(409).body(unavailable("RATE_PLAN_INACTIVE", "价格计划不存在或已停用", null));
            }

            // 4. 房型适用性
            String applicableJson = ratePlan.getApplicableRoomTypes();
            if (applicableJson != null && !applicableJson.isBlank()
                    && !applicableJson.equals("[]") && !applicableJson.equals("null")) {
                try {
                    List<String> applicable = objectMapper.readValue(applicableJson, new TypeReference<>() {});
                    if (!applicable.isEmpty() && !applicable.contains(roomTypeCode)) {
                        return ResponseEntity.status(409).body(unavailable("ROOM_TYPE_NOT_APPLICABLE", "价格计划不适用该房型", null));
                    }
                } catch (Exception ignored) {}
            }

            // 5. 会员等级校验
            String pm = ratePlan.getPersonalMembership();
            boolean hasMemberReq = pm != null && !pm.isBlank() && !pm.equals("null") && !pm.equals("[]") && !pm.equals("{}");
            if (hasMemberReq) {
                if (memberLevel == null || memberLevel.isBlank()) {
                    return ResponseEntity.status(409).body(unavailable("MEMBER_INFO_REQUIRED", "该价格计划需要提供会员等级", null));
                }
                if (!pm.contains(memberLevel)) {
                    return ResponseEntity.status(409).body(unavailable("MEMBER_LEVEL_MISMATCH", "会员等级不满足该价格计划要求", null));
                }
            }

            // 6. 房态检查
            List<RoomStatusRecord> statuses = roomStatusRepo
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndStatusDateBetween(
                            hotel.getTenantId(), hotelCode, "room_type", roomTypeCode, checkIn, lastNight);
            Map<String, Boolean> statusMap = new HashMap<>();
            statuses.forEach(s -> statusMap.put(formatDate(s.getStatusDate()), s.getIsOpen()));

            List<String> closedDates = new ArrayList<>();
            Calendar cur = Calendar.getInstance(); cur.setTime(checkIn);
            while (!cur.getTime().after(lastNight)) {
                String ds = sdf.format(cur.getTime());
                if (Boolean.FALSE.equals(statusMap.get(ds))) closedDates.add(ds);
                cur.add(Calendar.DATE, 1);
            }
            if (!closedDates.isEmpty()) {
                return ResponseEntity.status(409).body(unavailableWithDates("ROOM_CLOSED",
                        "房态已关闭：" + closedDates.get(0), closedDates));
            }

            // 7. 库存检查
            List<Inventory> invList = inventoryRepo.findByHotelIdAndChannelIdAndDateBetween(
                    hotel.getId(), channel.getId(), checkIn, lastNight)
                    .stream().filter(inv -> ratePlan.getId().equals(inv.getRatePlanId())
                            && roomType.getId().equals(inv.getRoomTypeId()))
                    .collect(Collectors.toList());

            List<String> insufficientDates = new ArrayList<>();
            cur.setTime(checkIn);
            while (!cur.getTime().after(lastNight)) {
                String ds = sdf.format(cur.getTime());
                Date d = cur.getTime();
                int avail = invList.stream()
                        .filter(inv -> sdf.format(inv.getDate()).equals(ds))
                        .mapToInt(Inventory::getAvailableRooms).findFirst().orElse(0);
                if (avail < roomCount) insufficientDates.add(ds + "(可用:" + avail + ")");
                cur.add(Calendar.DATE, 1);
            }
            if (!insufficientDates.isEmpty()) {
                return ResponseEntity.status(409).body(unavailableWithDates("INSUFFICIENT_INVENTORY",
                        "库存不足：" + insufficientDates.get(0), insufficientDates));
            }

            // 8. 入住人数校验
            int maxOcc = roomType.getMaxOccupancy() != null ? roomType.getMaxOccupancy() : 2;
            int maxChildren = roomType.getMaxChildren() != null ? roomType.getMaxChildren() : 0;
            if (adultCount + childCount > maxOcc) {
                return ResponseEntity.status(409).body(unavailable("EXCEED_MAX_OCCUPANCY",
                        "超出最大入住人数 " + maxOcc, null));
            }

            // 9. 提前预订天数
            List<BookingControl> controls = bookingControlRepo
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDateBetween(
                            hotel.getTenantId(), hotelCode, "hotel", "", checkIn, lastNight);
            int advanceDays = controls.stream().mapToInt(BookingControl::getAdvanceBookingDays).max()
                    .orElse(ratePlan.getAdvanceBookingMin() != null ? ratePlan.getAdvanceBookingMin() : 0);
            long daysUntilCheckIn = (checkIn.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
            if (daysUntilCheckIn < advanceDays) {
                return ResponseEntity.status(409).body(unavailable("ADVANCE_BOOKING_VIOLATION",
                        "需提前 " + advanceDays + " 天预订", null));
            }

            // 10. 入住天数限制
            int minStay = controls.stream().mapToInt(BookingControl::getMinStay).max()
                    .orElse(ratePlan.getMinimumStayMin() != null ? ratePlan.getMinimumStayMin() : 1);
            int maxStay = controls.stream().mapToInt(BookingControl::getMaxStay).min()
                    .orElse(ratePlan.getMinimumStayMax() != null ? ratePlan.getMinimumStayMax() : 30);
            if (nights < minStay || nights > maxStay) {
                return ResponseEntity.status(409).body(unavailable("STAY_DURATION_VIOLATION",
                        "入住天数需在 " + minStay + " ~ " + maxStay + " 晚之间", null));
            }

            // 11. 价格完整性
            List<HotelPrice> prices = priceRepo.findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDateBetween(
                    hotel.getTenantId(), hotelCode, ratePlanCode, roomTypeCode, checkIn, lastNight);
            if (prices.size() < nights) {
                return ResponseEntity.status(409).body(unavailable("PRICE_NOT_SET", "部分日期未设置价格", null));
            }

            // 构建成功响应
            List<Map<String, Object>> dailyPrices = prices.stream()
                    .sorted(Comparator.comparing(HotelPrice::getPriceDate))
                    .map(p -> { Map<String, Object> dp = new LinkedHashMap<>();
                        dp.put("date", formatDate(p.getPriceDate()));
                        dp.put("priceWithTax", p.getPriceWithTax()); return dp; })
                    .collect(Collectors.toList());

            BigDecimal totalPrice = prices.stream().filter(p -> p.getPriceWithTax() != null)
                    .map(HotelPrice::getPriceWithTax).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(roomCount));

            int minAvail = invList.stream().mapToInt(Inventory::getAvailableRooms).min().orElse(0);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("available", true);
            data.put("hotelCode", hotelCode);
            data.put("roomTypeCode", roomTypeCode);
            data.put("ratePlanCode", ratePlanCode);
            data.put("checkInDate", checkInStr);
            data.put("checkOutDate", checkOutStr);
            data.put("nights", nights);
            data.put("roomCount", roomCount);
            data.put("availableRooms", minAvail);
            data.put("dailyPrices", dailyPrices);
            data.put("totalPrice", totalPrice);
            data.put("currency", "CNY");
            data.put("packages", parsePackages(ratePlan.getPackages()));
            data.put("cancellationPolicy", buildCancellationPolicy(ratePlan.getCancellationRule()));
            data.put("guaranteePolicy", buildGuaranteePolicy(ratePlan.getGuaranteeRule()));
            return ResponseEntity.ok(ok(data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(err(500, e.getMessage()));
        }
    }

    // =========================================================================
    // 辅助方法
    // =========================================================================

    private Map<String, Object> unavailable(String reason, String desc, List<String> dates) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 409); r.put("message", desc);
        r.put("timestamp", now());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("available", false);
        data.put("reason", reason);
        data.put("reasonDescription", desc);
        if (dates != null) data.put("unavailableDates", dates);
        r.put("data", data);
        return r;
    }

    private Map<String, Object> unavailableWithDates(String reason, String desc, List<String> dates) {
        return unavailable(reason, desc, dates);
    }

    private String formatDate(Date d) {
        if (d == null) return null;
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    private List<Map<String, Object>> parsePackages(String packagesJson) {
        if (packagesJson == null || packagesJson.isBlank() || packagesJson.equals("null")) return Collections.emptyList();
        try {
            List<Map<String, Object>> raw = objectMapper.readValue(packagesJson, new TypeReference<>() {});
            return raw.stream().map(p -> {
                Map<String, Object> pm = new LinkedHashMap<>();
                pm.put("packageCode", p.get("code"));
                pm.put("packageName", p.get("name"));
                String type = (String) p.getOrDefault("type", "other");
                pm.put("type", type);
                pm.put("typeName", resolvePackageTypeName(type));
                pm.put("frequency", p.getOrDefault("frequency", "daily"));
                pm.put("quantity", p.getOrDefault("fixedQuantity", p.getOrDefault("quantity", 1)));
                return pm;
            }).collect(Collectors.toList());
        } catch (Exception e) { return Collections.emptyList(); }
    }

    private String resolvePackageTypeName(String type) {
        if (type == null) return "其他";
        return switch (type) {
            case "breakfast" -> "早餐"; case "lunch" -> "午餐"; case "dinner" -> "晚餐";
            case "afternoon_tea" -> "下午茶"; case "minibar" -> "迷你吧"; case "spa" -> "SPA/水疗";
            case "parking" -> "停车"; case "airport_transfer" -> "接送机"; case "laundry" -> "洗衣";
            case "gym" -> "健身"; case "pool" -> "泳池"; case "wifi" -> "上网";
            case "voucher" -> "代金券"; case "gift" -> "礼品"; case "upgrade" -> "升级";
            case "late_checkout" -> "延迟退房"; default -> "其他";
        };
    }

    private Map<String, Object> buildCancellationPolicy(String ruleCode) {
        if (ruleCode == null || ruleCode.isBlank()) return null;
        CancellationPolicy policy = cancellationPolicyRepo.findByCode(ruleCode);
        if (policy == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type", policy.getType());
        m.put("description", policy.getDescription());
        m.put("freeCancelBeforeDays", policy.getCancellationDays());
        m.put("freeCancelBeforeTime", policy.getCancellationTime());
        m.put("penaltyType", policy.getCancellationFeeType());
        return m;
    }

    private Map<String, Object> buildGuaranteePolicy(String ruleCode) {
        if (ruleCode == null || ruleCode.isBlank()) return null;
        GuaranteePolicy policy = guaranteePolicyRepo.findByCode(ruleCode);
        if (policy == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type", policy.getType());
        m.put("subType", policy.getGuaranteeSubType());
        m.put("amount", policy.getGuaranteeAmount());
        m.put("latestArrivalTime", policy.getLatestArrivalTime());
        m.put("description", policy.getDescription());
        return m;
    }

    private Map<String, Object> buildBookingRules(RatePlan rp, List<BookingControl> controls) {
        int advMin = controls.stream().mapToInt(BookingControl::getAdvanceBookingDays).max()
                .orElse(rp.getAdvanceBookingMin() != null ? rp.getAdvanceBookingMin() : 0);
        int minStay = controls.stream().mapToInt(BookingControl::getMinStay).max()
                .orElse(rp.getMinimumStayMin() != null ? rp.getMinimumStayMin() : 1);
        int maxStay = controls.stream().mapToInt(BookingControl::getMaxStay).min()
                .orElse(rp.getMinimumStayMax() != null ? rp.getMinimumStayMax() : 30);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("advanceBookingMin", advMin);
        m.put("advanceBookingMax", rp.getAdvanceBookingMax() != null ? rp.getAdvanceBookingMax() : 90);
        m.put("minimumStay", minStay);
        m.put("maximumStay", maxStay);
        return m;
    }

    private String parseMemberLevel(String personalMembership) {
        if (personalMembership == null || personalMembership.isBlank()
                || personalMembership.equals("null") || personalMembership.equals("[]")) return null;
        try {
            List<String> levels = objectMapper.readValue(personalMembership, new TypeReference<>() {});
            return levels.isEmpty() ? null : String.join(",", levels);
        } catch (Exception e) { return null; }
    }
}
