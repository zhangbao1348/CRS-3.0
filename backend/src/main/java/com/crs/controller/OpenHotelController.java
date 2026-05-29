package com.crs.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crs.entity.BookingControl;
import com.crs.entity.CancellationPolicy;
import com.crs.entity.ChannelHotelMapping;
import com.crs.entity.ChannelPublishRecord;
import com.crs.entity.GuaranteePolicy;
import com.crs.entity.Hotel;
import com.crs.entity.HotelFacility;
import com.crs.entity.HotelImage;
import com.crs.entity.HotelPrice;
import com.crs.entity.HotelRoomType;
import com.crs.entity.Inventory;
import com.crs.entity.RatePlan;
import com.crs.entity.RoomTypeFacility;
import com.crs.entity.TenantChannel;
import com.crs.repository.BookingControlRepository;
import com.crs.repository.CancellationPolicyRepository;
import com.crs.repository.ChannelHotelMappingRepository;
import com.crs.repository.ChannelPublishRecordRepository;
import com.crs.repository.GuaranteePolicyRepository;
import com.crs.repository.HotelFacilityRepository;
import com.crs.repository.HotelImageRepository;
import com.crs.repository.HotelPriceRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.InventoryRepository;
import com.crs.repository.PackageRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.repository.RoomStatusRepository;
import com.crs.repository.RoomTypeFacilityRepository;
import com.crs.service.inventory.AvailabilityContext;
import com.crs.service.inventory.AvailabilityResult;
import com.crs.service.inventory.InventoryDeductionService;
import com.crs.util.CancellationPolicyTypeUtil;
import com.crs.util.DisplayMapper;
import com.crs.util.GuaranteePolicyTypeUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

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
    @Autowired private InventoryDeductionService inventoryDeductionService;
    @Autowired private CancellationPolicyRepository cancellationPolicyRepo;
    @Autowired private GuaranteePolicyRepository guaranteePolicyRepo;
    @Autowired private PackageRepository packageRepo;
    @Autowired private ChannelHotelMappingRepository channelHotelMappingRepo;
    @Autowired private ChannelPublishRecordRepository channelPublishRecordRepo;

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
    private boolean hasHotelAccess(TenantChannel channel, String hotelCode) {
        List<ChannelHotelMapping> mappings = channelHotelMappingRepo
                .findByTenantIdAndChannelCodeAndHotelCode(channel.getTenantId(), channel.getChannelCode(), hotelCode);
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

            String checkInValidationMessage = validateOpenApiCheckInDate(checkInDate);
            if (checkInValidationMessage != null) {
                return ResponseEntity.badRequest().body(err(400, checkInValidationMessage));
            }

            // 获取该渠道有权访问的酒店CODE列表
            List<ChannelHotelMapping> mappings = channelHotelMappingRepo.findByTenantIdAndChannelCode(channel.getTenantId(), channel.getChannelCode())
                    .stream().filter(m -> "active".equals(m.getStatus())).collect(Collectors.toList());
            Set<String> allowedHotelCodes = mappings.stream()
                    .map(ChannelHotelMapping::getHotelCode).collect(Collectors.toSet());

            if (allowedHotelCodes.isEmpty()) {
                return ResponseEntity.ok(ok(Map.of("total", 0, "page", page, "pageSize", pageSize, "list", List.of())));
            }

            // 使用数据库分页查询
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1, pageSize);
            org.springframework.data.domain.Page<Hotel> hotelPage = hotelRepo.findWithFilters(
                    channel.getTenantId(), Hotel.Status.active, cityId, keyword, allowedHotelCodes, pageable);

            List<Map<String, Object>> list = new ArrayList<>();
            for (Hotel hotel : hotelPage.getContent()) {
                list.add(buildHotelSummary(hotel, checkInDate, checkOutDate, channel.getPriceRounding()));
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("total", hotelPage.getTotalElements());
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("list", list);
            return ResponseEntity.ok(ok(data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(err(500, e.getMessage()));
        }
    }

    private Map<String, Object> buildHotelSummary(Hotel hotel, Date checkIn, Date checkOut, String priceRounding) {
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
            BigDecimal minimumPrice = getHotelMinimumPrice(hotel);
            List<HotelPrice> prices = priceRepo.findByTenantIdAndHotelCodeAndPriceDateBetween(
                    hotel.getTenantId(), hotel.getHotelCode(), checkIn, checkOut);
            startingPrice = prices.stream()
                    .filter(this::isEffectiveHotelPrice)
                    .map(HotelPrice::getPriceWithTax)
                    .map(price -> applyHotelMinimumPrice(price, minimumPrice))
                    .min(BigDecimal::compareTo).orElse(null);
        }
        m.put("startingPrice", applyChannelPriceRounding(startingPrice, priceRounding));

        // 图片
        List<HotelImage> images = imageRepo.findByHotelCodeOrderBySortOrderAsc(hotel.getHotelCode());
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
        List<HotelFacility> facilities = facilityRepo.findByHotelCodeAndAvailable(hotel.getHotelCode(), true);
        m.put("facilities", facilities.stream().map(f -> {
            Map<String, Object> fm = new LinkedHashMap<>();
            fm.put("facilityType", f.getFacilityType());
            fm.put("facilityCode", f.getFacilityCode());
            fm.put("facilityName", f.getFacilityName());
            return fm;
        }).collect(Collectors.toList()));

        // 房型静态信息
        List<HotelRoomType> roomTypes = roomTypeRepo.findDistinctByTenantIdAndHotelCodeAndStatus(hotel.getTenantId(), hotel.getHotelCode(), "active");
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
        List<RoomTypeFacility> rtFacilities = roomTypeFacilityRepo.findByHotelCodeAndRoomTypeCode(rt.getHotelCode(), rt.getRoomTypeCode())
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
            String priceRounding = channel.getPriceRounding();

            Hotel hotel = hotelRepo.findByHotelCodeAndTenantId(hotelCode, channel.getTenantId()).orElse(null);
            if (hotel == null || hotel.getStatus() != Hotel.Status.active) {
                return ResponseEntity.status(404).body(err(404, "酒店不存在"));
            }
            if (!hasHotelAccess(channel, hotel.getHotelCode())) {
                return ResponseEntity.status(403).body(err(403, "渠道无权访问该酒店"));
            }
            BigDecimal minimumPrice = getHotelMinimumPrice(hotel);

            String checkInValidationMessage = validateOpenApiCheckInDate(checkInDate);
            if (checkInValidationMessage != null) {
                return ResponseEntity.badRequest().body(err(400, checkInValidationMessage));
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

            List<HotelImage> images = imageRepo.findByHotelCodeOrderBySortOrderAsc(hotel.getHotelCode());
            hotelInfo.put("images", images.stream().map(img -> {
                Map<String, Object> im = new LinkedHashMap<>();
                im.put("imageType", img.getImageType()); im.put("imagePath", img.getImagePath());
                im.put("imageName", img.getImageName()); im.put("description", img.getDescription());
                im.put("sortOrder", img.getSortOrder()); return im;
            }).collect(Collectors.toList()));

            List<HotelFacility> facilities = facilityRepo.findByHotelCodeAndAvailable(hotel.getHotelCode(), true);
            hotelInfo.put("facilities", facilities.stream().map(f -> {
                Map<String, Object> fm = new LinkedHashMap<>();
                fm.put("facilityType", f.getFacilityType()); fm.put("facilityCode", f.getFacilityCode());
                fm.put("facilityName", f.getFacilityName()); return fm;
            }).collect(Collectors.toList()));

            // 1. 获取基础数据列表
            List<HotelRoomType> roomTypes = roomTypeRepo.findDistinctByTenantIdAndHotelCodeAndStatus(hotel.getTenantId(), hotel.getHotelCode(), "active");
            List<RatePlan> allRatePlans = ratePlanRepo.findByTenantIdAndHotelCodeAndStatus(hotel.getTenantId(), hotel.getHotelCode(), "active");

            // 2. 预加载所有价格、库存、房态、规则（一次性批量查询）
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkOutDate); cal.add(Calendar.DATE, -1);
            Date lastNight = cal.getTime();

            // 批量查询价格
            List<HotelPrice> allPrices = priceRepo.findByTenantIdAndHotelCodeAndPriceDateBetween(
                    hotel.getTenantId(), hotelCode, checkInDate, lastNight);
            // 组装价格 Map: "rateCode_roomTypeCode_date" -> HotelPrice
            Map<String, HotelPrice> priceMap = allPrices.stream().collect(Collectors.toMap(
                    p -> p.getRateCode() + "_" + p.getRoomTypeCode() + "_" + formatDate(p.getPriceDate()),
                    p -> p, (a, b) -> a));

            // 批量查询库存
            List<Inventory> allInventory = inventoryRepo.findByTenantIdAndHotelCodeAndChannelCodeAndDateBetween(
                    channel.getTenantId(), hotel.getHotelCode(), channel.getChannelCode(), checkInDate, lastNight);
            // 组装库存 Map: "ratePlanCode_roomTypeCode_date" -> Integer
            Map<String, Integer> invMap = allInventory.stream().collect(Collectors.toMap(
                    i -> i.getRatePlanCode() + "_" + i.getRoomTypeCode() + "_" + formatDate(i.getDate()),
                    Inventory::getAvailableRooms, (a, b) -> Math.min(a, b)));

            // 批量查询发布记录
            List<ChannelPublishRecord> publishRecords = channelPublishRecordRepo.findByTenantIdAndHotelCodeAndChannelCodeAndStatus(
                    channel.getTenantId(), hotelCode, channel.getChannelCode(), "published");
            Set<String> publishedSet = publishRecords.stream()
                    .map(r -> r.getRateCode() + "_" + r.getRoomTypeCode())
                    .collect(Collectors.toSet());

            // 3. 构建返回数据
            List<Map<String, Object>> roomTypeResults = new ArrayList<>();
            for (HotelRoomType rt : roomTypes) {
                Map<String, Object> rtMap = buildRoomTypeSummary(rt);
                List<Map<String, Object>> ratePlanResults = new ArrayList<>();

                for (RatePlan rp : allRatePlans) {
                    // A. 校验发布状态
                    if (!publishedSet.contains(rp.getRateCode() + "_" + rt.getRoomTypeCode())) continue;

                    // B. 校验会员等级
                    String pm = rp.getPersonalMembership();
                    if (memberLevel != null && !memberLevel.isBlank()) {
                        if (pm != null && !pm.isBlank() && !pm.equals("[]") && !pm.equals("{}") && !pm.contains(memberLevel)) continue;
                    } else {
                        if (pm != null && !pm.isBlank() && !pm.equals("[]") && !pm.equals("{}")) continue;
                    }

                    // C. 检查价格计划是否适用该房型
                    if (!isRatePlanApplicable(rp, rt.getRoomTypeCode())) continue;

                    // C.5 校验预订控制规则（开关房、提前预订天数、连住天数限制），不满足时直接前置过滤隐藏价格计划
                    AvailabilityContext availCtx = new AvailabilityContext();
                    availCtx.setTenantId(hotel.getTenantId());
                    availCtx.setHotelCode(hotelCode);
                    availCtx.setRoomTypeCode(rt.getRoomTypeCode());
                    availCtx.setRateCode(rp.getRateCode());
                    availCtx.setChannelCode(channel.getChannelCode());
                    availCtx.setRateCategoryCode(rp.getRateCategory());
                    availCtx.setCheckInDate(checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    availCtx.setCheckOutDate(checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    availCtx.setRequestedRooms(roomCount);

                    AvailabilityResult availResult = inventoryDeductionService.checkAvailability(availCtx);
                    if (!availResult.isAvailable()) {
                        String reject = availResult.getRejectReason();
                        if (reject != null && (reject.contains("房态") || reject.contains("提前") || reject.contains("需住") || reject.contains("可住"))) {
                            continue; // 房态关闭、提前预订违规或连住违规，前置隐藏该价格计划
                        }
                    }

                    // D. 聚合每日价格与总价
                    List<Map<String, Object>> dailyPrices = new ArrayList<>();
                    BigDecimal totalPrice = BigDecimal.ZERO;
                    boolean priceComplete = true;
                    
                    cal.setTime(checkInDate);
                    for (int i = 0; i < nights; i++) {
                        String key = rp.getRateCode() + "_" + rt.getRoomTypeCode() + "_" + formatDate(cal.getTime());
                        HotelPrice hp = priceMap.get(key);
                        if (!isEffectiveHotelPrice(hp)) {
                            priceComplete = false; break;
                        }
                        BigDecimal effectivePrice = applyHotelMinimumPrice(hp.getPriceWithTax(), minimumPrice);
                        BigDecimal roundedPrice = applyChannelPriceRounding(effectivePrice, priceRounding);
                        Map<String, Object> dp = new LinkedHashMap<>();
                        dp.put("date", formatDate(cal.getTime()));
                        dp.put("priceWithTax", roundedPrice);
                        dailyPrices.add(dp);
                        totalPrice = totalPrice.add(roundedPrice);
                        cal.add(Calendar.DATE, 1);
                    }
                    if (!priceComplete) continue;
                    totalPrice = totalPrice.multiply(BigDecimal.valueOf(roomCount));

                    // E. 计算最小可用库存 (直接复用可订检查返回的可用配额/物理库存最小值)
                    int minAvail = availResult.getAvailableCount() != null ? availResult.getAvailableCount() : 0;

                    // F. 合并计算多日期与多维度最严取消规则
                    String effectiveCancelRule = resolveEffectiveCancellationRule(
                            channel.getTenantId(), rp.getCancellationRule(), hotelCode, rp.getRateCode(),
                            channel.getChannelCode(), rp.getRateCategory(), null,
                            checkInDate, checkOutDate);

                    // G. 组装 RatePlan 数据
                    Map<String, Object> rpMap = new LinkedHashMap<>();
                    rpMap.put("ratePlanCode", rp.getRateCode());
                    rpMap.put("ratePlanName", rp.getRateName());
                    rpMap.put("availableRooms", minAvail);
                    rpMap.put("totalPrice", totalPrice);
                    rpMap.put("averagePrice", applyChannelPriceRounding(
                            totalPrice.divide(BigDecimal.valueOf(nights * roomCount), 2, java.math.RoundingMode.HALF_UP),
                            priceRounding));
                    rpMap.put("currency", "CNY");
                    rpMap.put("dailyPrices", dailyPrices);
                    rpMap.put("packages", parsePackages(hotel.getTenantId(), rp.getPackages()));
                    rpMap.put("cancellationPolicy", buildCancellationPolicy(channel.getTenantId(), effectiveCancelRule));
                    rpMap.put("guaranteePolicy", buildGuaranteePolicy(channel.getTenantId(), rp.getGuaranteeRule()));
                    ratePlanResults.add(rpMap);
                }
                
                if (!ratePlanResults.isEmpty()) {
                    rtMap.put("ratePlans", ratePlanResults);
                    roomTypeResults.add(rtMap);
                }
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
            String priceRounding = channel.getPriceRounding();

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

            String checkInValidationMessage = validateOpenApiCheckInDate(checkIn);
            if (checkInValidationMessage != null) {
                return ResponseEntity.badRequest().body(err(400, checkInValidationMessage));
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(checkOut); cal.add(Calendar.DATE, -1);
            Date lastNight = cal.getTime();

            // 1. 渠道权限
            Hotel hotel = hotelRepo.findByHotelCodeAndTenantId(hotelCode, channel.getTenantId()).orElse(null);
            if (hotel == null || hotel.getStatus() != Hotel.Status.active) {
                return ResponseEntity.status(409).body(unavailable("HOTEL_INACTIVE", "酒店不存在或已停用", null));
            }
            if (!hasHotelAccess(channel, hotel.getHotelCode())) {
                return ResponseEntity.status(403).body(err(403, "渠道无权访问该酒店"));
            }
            BigDecimal minimumPrice = getHotelMinimumPrice(hotel);

            // 2. 房型有效性
            HotelRoomType roomType = roomTypeRepo.findByTenantIdAndHotelCodeAndRoomTypeCode(hotel.getTenantId(), hotel.getHotelCode(), roomTypeCode).orElse(null);
            if (roomType == null || !"active".equals(roomType.getStatus())) {
                return ResponseEntity.status(409).body(unavailable("ROOM_TYPE_INACTIVE", "房型不存在或已停用", null));
            }

            // 3. 价格计划有效性
            RatePlan ratePlan = ratePlanRepo.findByTenantIdAndHotelCodeAndRateCode(hotel.getTenantId(), hotel.getHotelCode(), ratePlanCode).orElse(null);
            if (ratePlan == null || !"active".equals(ratePlan.getStatus())) {
                return ResponseEntity.status(409).body(unavailable("RATE_PLAN_INACTIVE", "价格计划不存在或已停用", null));
            }

            // 3.5 渠道发布校验
            boolean isPublished = channelPublishRecordRepo.existsByTenantIdAndHotelCodeAndChannelCodeAndRateCodeAndRoomTypeCode(
                    hotel.getTenantId(), hotelCode, channel.getChannelCode(), ratePlanCode, roomTypeCode);
            if (!isPublished) {
                return ResponseEntity.status(409).body(unavailable("RATE_PLAN_NOT_PUBLISHED", "该房型+价格计划未发布至该渠道", null));
            }

            // 4. 房型适用性
            String applicableJson = ratePlan.getApplicableRoomTypes();
            if (applicableJson != null && !applicableJson.isBlank()
                    && !applicableJson.equals("[]") && !applicableJson.equals("null")) {
                try {
                    List<Object> applicableRaw = objectMapper.readValue(applicableJson, new TypeReference<>() {});
                    if (!applicableRaw.isEmpty()) {
                        List<String> applicableCodes = applicableRaw.stream().map(Object::toString).collect(Collectors.toList());
                        if (!applicableCodes.contains(roomTypeCode)) {
                            return ResponseEntity.status(409).body(unavailable("ROOM_TYPE_NOT_APPLICABLE", "价格计划不适用该房型", null));
                        }
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

            // 6-10. 综合可售性检查（房态+预订规则+物理库存+超预订+多维度配额）
            AvailabilityContext availCtx = new AvailabilityContext();
            availCtx.setTenantId(hotel.getTenantId());
            availCtx.setHotelCode(hotelCode);
            availCtx.setRoomTypeCode(roomTypeCode);
            availCtx.setRateCode(ratePlanCode);
            availCtx.setChannelCode(channel.getChannelCode());
            availCtx.setRateCategoryCode(ratePlan.getRateCategory());
            availCtx.setCheckInDate(checkIn.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            availCtx.setCheckOutDate(checkOut.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            availCtx.setRequestedRooms(roomCount);

            AvailabilityResult availResult = inventoryDeductionService.checkAvailability(availCtx);

            if (!availResult.isAvailable()) {
                String code = availResult.getRejectReason().contains("房态关闭") ? "ROOM_CLOSED"
                        : availResult.getRejectReason().contains("提前") ? "ADVANCE_BOOKING_VIOLATION"
                        : availResult.getRejectReason().contains("连住") || availResult.getRejectReason().contains("入住") ? "STAY_DURATION_VIOLATION"
                        : "INSUFFICIENT_INVENTORY";
                return ResponseEntity.status(409).body(unavailable(code, availResult.getRejectReason(), null));
            }

            // 入住人数校验
            int maxOcc = roomType.getMaxOccupancy() != null ? roomType.getMaxOccupancy() : 2;
            if (adultCount + childCount > maxOcc) {
                return ResponseEntity.status(409).body(unavailable("EXCEED_MAX_OCCUPANCY",
                        "超出最大入住人数 " + maxOcc, null));
            }

            // 11. 价格完整性
            List<HotelPrice> prices = priceRepo.findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDateBetween(
                    hotel.getTenantId(), hotelCode, ratePlanCode, roomTypeCode, checkIn, lastNight);
            Map<String, HotelPrice> validPriceMap = prices.stream()
                    .filter(this::isEffectiveHotelPrice)
                    .collect(Collectors.toMap(
                            p -> formatDate(p.getPriceDate()),
                            p -> p,
                            (existing, replacement) -> replacement,
                            LinkedHashMap::new));

            List<HotelPrice> effectivePrices = new ArrayList<>();
            cal.setTime(checkIn);
            for (int i = 0; i < nights; i++) {
                String dateKey = formatDate(cal.getTime());
                HotelPrice validPrice = validPriceMap.get(dateKey);
                if (validPrice == null) {
                    return ResponseEntity.status(409).body(unavailable("PRICE_NOT_SET", "部分日期未设置价格", null));
                }
                effectivePrices.add(validPrice);
                cal.add(Calendar.DATE, 1);
            }

            if (effectivePrices.size() < nights) {
                return ResponseEntity.status(409).body(unavailable("PRICE_NOT_SET", "部分日期未设置价格", null));
            }

            // 构建成功响应
            List<Map<String, Object>> dailyPrices = effectivePrices.stream()
                    .sorted(Comparator.comparing(HotelPrice::getPriceDate))
                    .map(p -> { Map<String, Object> dp = new LinkedHashMap<>();
                        dp.put("date", formatDate(p.getPriceDate()));
                        BigDecimal effectivePrice = applyHotelMinimumPrice(p.getPriceWithTax(), minimumPrice);
                        dp.put("priceWithTax", applyChannelPriceRounding(effectivePrice, priceRounding)); return dp; })
                    .collect(Collectors.toList());

            BigDecimal totalPrice = effectivePrices.stream()
                    .map(HotelPrice::getPriceWithTax)
                    .map(price -> applyHotelMinimumPrice(price, minimumPrice))
                    .map(price -> applyChannelPriceRounding(price, priceRounding))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(roomCount));

            int minAvail = availResult.getAvailableCount() != null ? availResult.getAvailableCount() : 0;

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
            data.put("packages", parsePackages(hotel.getTenantId(), ratePlan.getPackages()));
            String effectiveCancelRule = resolveEffectiveCancellationRule(
                    channel.getTenantId(), ratePlan.getCancellationRule(), hotelCode, ratePlanCode,
                    channel.getChannelCode(), ratePlan.getRateCategory(), null,
                    checkIn, checkOut);
            data.put("cancellationPolicy", buildCancellationPolicy(channel.getTenantId(), effectiveCancelRule));
            data.put("guaranteePolicy", buildGuaranteePolicy(channel.getTenantId(), ratePlan.getGuaranteeRule()));
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

    private LocalDate getEarliestAllowedCheckInDate() {
        ZonedDateTime now = ZonedDateTime.now();
        LocalDate today = now.toLocalDate();
        return now.getHour() < 6 ? today.minusDays(1) : today;
    }

    private String validateOpenApiCheckInDate(Date checkInDate) {
        if (checkInDate == null) {
            return null;
        }
        LocalDate targetDate = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate earliestDate = getEarliestAllowedCheckInDate();
        if (targetDate.isBefore(earliestDate)) {
            return String.format("入住日期不能早于允许预订日期 %s", earliestDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        return null;
    }

    private BigDecimal getHotelMinimumPrice(Hotel hotel) {
        if (hotel == null) {
            return null;
        }
        BigDecimal minimumPrice = hotel.getMinimumPrice();
        if (minimumPrice == null || minimumPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return minimumPrice;
    }

    private BigDecimal applyHotelMinimumPrice(BigDecimal price, BigDecimal minimumPrice) {
        if (price == null || minimumPrice == null || minimumPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }
        return price.compareTo(minimumPrice) < 0 ? minimumPrice : price;
    }

    private BigDecimal applyChannelPriceRounding(BigDecimal price, String rounding) {
        if (price == null || rounding == null || rounding.isBlank() || "keep".equals(rounding)) {
            return price;
        }
        if ("floor".equals(rounding)) {
            return price.setScale(0, java.math.RoundingMode.FLOOR);
        }
        if ("ceil".equals(rounding)) {
            return price.setScale(0, java.math.RoundingMode.CEILING);
        }
        return price;
    }

    private boolean isEffectiveHotelPrice(HotelPrice hotelPrice) {
        return hotelPrice != null
                && "active".equalsIgnoreCase(hotelPrice.getStatus())
                && hotelPrice.getPriceWithTax() != null
                && hotelPrice.getPriceWithTax().compareTo(BigDecimal.ZERO) > 0;
    }

    private List<Map<String, Object>> parsePackages(Integer tenantId, String packagesJson) {
        if (packagesJson == null || packagesJson.isBlank() || packagesJson.equals("null")) return Collections.emptyList();
        try {
            List<Object> rawItems = objectMapper.readValue(packagesJson, new TypeReference<>() {});
            List<Map<String, Object>> packageBindings = new ArrayList<>();

            for (Object item : rawItems) {
                if (item instanceof String code) {
                    if (code != null && !code.isBlank()) {
                        Map<String, Object> binding = new LinkedHashMap<>();
                        binding.put("code", code);
                        packageBindings.add(binding);
                    }
                    continue;
                }

                if (item instanceof Map<?, ?> rawMap) {
                    Map<String, Object> binding = new LinkedHashMap<>();
                    rawMap.forEach((key, value) -> binding.put(Objects.toString(key, null), value));
                    String code = Objects.toString(binding.get("code"), Objects.toString(binding.get("packageCode"), null));
                    if (code != null && !code.isBlank()) {
                        binding.put("code", code);
                        packageBindings.add(binding);
                    }
                }
            }

            List<String> packageCodes = packageBindings.stream()
                    .map(p -> Objects.toString(p.get("code"), null))
                    .filter(Objects::nonNull)
                    .filter(code -> !code.isBlank())
                    .distinct()
                    .collect(Collectors.toList());

            if (packageCodes.isEmpty()) {
                return Collections.emptyList();
            }

            Map<String, com.crs.entity.Package> activePackageMap = packageRepo
                    .findByTenantIdAndCodeInAndStatus(tenantId, packageCodes, com.crs.entity.Package.Status.active)
                    .stream()
                    .collect(Collectors.toMap(com.crs.entity.Package::getCode, p -> p, (a, b) -> a, LinkedHashMap::new));

            return packageBindings.stream()
                    .filter(p -> {
                        String code = Objects.toString(p.get("code"), null);
                        return code != null && activePackageMap.containsKey(code);
                    })
                    .map(p -> {
                String code = Objects.toString(p.get("code"), null);
                com.crs.entity.Package activePackage = activePackageMap.get(code);
                Map<String, Object> pm = new LinkedHashMap<>();
                pm.put("packageCode", activePackage.getCode());
                pm.put("packageName", activePackage.getName());
                String type = activePackage.getType() != null ? activePackage.getType() : (String) p.getOrDefault("type", "other");
                pm.put("type", type);
                pm.put("typeName", DisplayMapper.packageTypeName(type));
                pm.put("frequency", activePackage.getFrequency() != null ? activePackage.getFrequency() : p.getOrDefault("frequency", "daily"));
                pm.put("quantity", activePackage.getFixedQuantity() != null
                        ? activePackage.getFixedQuantity()
                        : p.getOrDefault("fixedQuantity", p.getOrDefault("quantity", 1)));
                return pm;
            }).collect(Collectors.toList());
        } catch (Exception e) { return Collections.emptyList(); }
    }


    private Map<String, Object> buildCancellationPolicy(Integer tenantId, String ruleCode) {
        if (ruleCode == null || ruleCode.isBlank()) return null;
        CancellationPolicy policy = cancellationPolicyRepo.findByTenantIdAndCode(tenantId, ruleCode);
        if (policy == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type", CancellationPolicyTypeUtil.normalizeType(policy.getType()));
        m.put("description", policy.getDescription());
        m.put("freeCancelBeforeDays", policy.getCancellationDays());
        m.put("freeCancelBeforeTime", policy.getCancellationTime());
        m.put("penaltyType", policy.getCancellationFeeType());
        return m;
    }

    private Map<String, Object> buildGuaranteePolicy(Integer tenantId, String ruleCode) {
        if (ruleCode == null || ruleCode.isBlank()) return null;
        GuaranteePolicy policy = guaranteePolicyRepo.findByTenantIdAndCode(tenantId, ruleCode);
        if (policy == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type", GuaranteePolicyTypeUtil.normalizeType(policy.getType()));
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

    private boolean isRatePlanApplicable(RatePlan rp, String roomTypeCode) {
        String applicableJson = rp.getApplicableRoomTypes();
        if (applicableJson == null || applicableJson.isBlank() || applicableJson.equals("[]") || applicableJson.equals("null")) {
            return true;
        }
        try {
            List<Object> applicableRaw = objectMapper.readValue(applicableJson, new TypeReference<>() {});
            if (applicableRaw.isEmpty()) return true;
            List<String> applicableCodes = applicableRaw.stream().map(Object::toString).collect(Collectors.toList());
            return applicableCodes.contains(roomTypeCode);
        } catch (Exception e) { return true; }
    }

    private int getCancellationPolicySeverityScore(CancellationPolicy policy) {
        if (policy == null) return 0;
        String normType = CancellationPolicyTypeUtil.normalizeType(policy.getType());
        if (CancellationPolicyTypeUtil.isNonRefundableType(normType)) {
            return 99999;
        }
        if (CancellationPolicyTypeUtil.isLimitedType(normType)) {
            int base = 1000;
            if ("full_amount".equalsIgnoreCase(policy.getCancellationFeeType())) {
                base = 2000;
            }
            int days = policy.getCancellationDays() != null ? policy.getCancellationDays() : 0;
            return base + (days * 24);
        }
        if (CancellationPolicyTypeUtil.isFreeType(normType)) {
            return 10;
        }
        return 0;
    }

    private String resolveEffectiveCancellationRule(
            Integer tenantId, String rpCancelRule, String hotelCode, String ratePlanCode,
            String channelCode, String rateCategoryCode, String marketCode,
            Date checkIn, Date checkOut) {
        
        String bestRule = rpCancelRule;
        int maxScore = 0;
        
        if (rpCancelRule != null && !rpCancelRule.isBlank()) {
            CancellationPolicy defaultPolicy = cancellationPolicyRepo.findByTenantIdAndCode(tenantId, rpCancelRule);
            maxScore = getCancellationPolicySeverityScore(defaultPolicy);
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(checkIn);
        
        String[][] dimensions = {
            {"hotel", ""},
            {"rate", ratePlanCode},
            {"channel", channelCode},
            {"rate_category", rateCategoryCode},
            {"market", marketCode}
        };
        
        Map<String, CancellationPolicy> policyCache = new java.util.HashMap<>();
        
        while (cal.getTime().before(checkOut)) {
            Date date = cal.getTime();
            for (String[] dim : dimensions) {
                String dimType = dim[0];
                String dimCode = dim[1];
                if (dimCode == null) continue;
                
                Optional<BookingControl> controlOpt = bookingControlRepo
                        .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDate(
                                tenantId, hotelCode, dimType, dimCode, date);
                
                if (controlOpt.isPresent()) {
                    String ruleCode = controlOpt.get().getCancellationRule();
                    if (ruleCode != null && !ruleCode.isBlank()) {
                        CancellationPolicy policy = policyCache.computeIfAbsent(ruleCode, 
                                code -> cancellationPolicyRepo.findByTenantIdAndCode(tenantId, code));
                        int score = getCancellationPolicySeverityScore(policy);
                        if (score > maxScore) {
                            maxScore = score;
                            bestRule = ruleCode;
                        }
                    }
                }
            }
            cal.add(Calendar.DATE, 1);
        }
        
        return bestRule;
    }
}
