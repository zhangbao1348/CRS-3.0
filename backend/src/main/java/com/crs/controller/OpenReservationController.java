package com.crs.controller;

import com.crs.entity.*;
import com.crs.repository.*;
import com.crs.service.ReservationService;
import com.crs.service.inventory.AvailabilityContext;
import com.crs.service.inventory.AvailabilityResult;
import com.crs.service.inventory.InventoryDeductionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/open")
public class OpenReservationController {

    private final ReservationService reservationService;
    private final HotelRepository hotelRepo;
    private final HotelRoomTypeRepository roomTypeRepo;
    private final RatePlanRepository ratePlanRepo;
    private final ChannelHotelMappingRepository channelHotelMappingRepo;
    private final HotelPriceRepository priceRepo;
    private final InventoryDeductionService inventoryDeductionService;
    private final CancellationPolicyRepository cancellationPolicyRepo;
    private final GuaranteePolicyRepository guaranteePolicyRepo;
    private final ChannelPublishRecordRepository channelPublishRecordRepo;
    private final ReservationRepository reservationRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenReservationController(
            ReservationService reservationService,
            HotelRepository hotelRepo,
            HotelRoomTypeRepository roomTypeRepo,
            RatePlanRepository ratePlanRepo,
            ChannelHotelMappingRepository channelHotelMappingRepo,
            HotelPriceRepository priceRepo,
            InventoryDeductionService inventoryDeductionService,
            CancellationPolicyRepository cancellationPolicyRepo,
            GuaranteePolicyRepository guaranteePolicyRepo,
            ChannelPublishRecordRepository channelPublishRecordRepo,
            ReservationRepository reservationRepo) {
        this.reservationService = reservationService;
        this.hotelRepo = hotelRepo;
        this.roomTypeRepo = roomTypeRepo;
        this.ratePlanRepo = ratePlanRepo;
        this.channelHotelMappingRepo = channelHotelMappingRepo;
        this.priceRepo = priceRepo;
        this.inventoryDeductionService = inventoryDeductionService;
        this.cancellationPolicyRepo = cancellationPolicyRepo;
        this.guaranteePolicyRepo = guaranteePolicyRepo;
        this.channelPublishRecordRepo = channelPublishRecordRepo;
        this.reservationRepo = reservationRepo;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Map<String, Object>> createReservation(
            HttpServletRequest req,
            @RequestBody Map<String, Object> body) {
        try {
            TenantChannel channel = getChannel(req);
            if (channel == null) {
                return ResponseEntity.status(401).body(err(401, "渠道认证失败"));
            }

            String hotelCode = getString(body, "hotelCode");
            String roomTypeCode = getString(body, "roomTypeCode");
            String ratePlanCode = getString(body, "ratePlanCode");
            String checkInStr = getString(body, "checkInDate");
            String checkOutStr = getString(body, "checkOutDate");
            String channelOrderNumber = getString(body, "channelOrderNumber");

            if (hotelCode == null || roomTypeCode == null || ratePlanCode == null
                    || checkInStr == null || checkOutStr == null || channelOrderNumber == null || channelOrderNumber.isBlank()) {
                return ResponseEntity.badRequest().body(err(400, "缺少必填参数：hotelCode, channelOrderNumber, roomTypeCode, ratePlanCode, checkInDate, checkOutDate"));
            }

            if (reservationRepo.existsByChannelIdAndChannelOrderNumber(channel.getId(), channelOrderNumber)) {
                return ResponseEntity.status(409).body(err(409, "订单已存在，请勿重复提交 (Duplicate Order)"));
            }


            Hotel hotel = hotelRepo.findByHotelCodeAndTenantId(hotelCode, channel.getTenantId()).orElse(null);
            if (hotel == null || hotel.getStatus() != Hotel.Status.active) {
                return ResponseEntity.status(404).body(err(404, "酒店不存在或已停用"));
            }

            if (!hasHotelAccess(channel, hotel.getId())) {
                return ResponseEntity.status(403).body(err(403, "渠道无权访问该酒店"));
            }

            HotelRoomType roomType = roomTypeRepo.findByHotelIdAndRoomTypeCode(hotel.getId(), roomTypeCode)
                    .orElse(null);
            if (roomType == null || !"active".equals(roomType.getStatus())) {
                return ResponseEntity.status(404).body(err(404, "房型不存在或已停用"));
            }

            RatePlan ratePlan = ratePlanRepo.findByHotelIdAndRateCode(hotel.getId(), ratePlanCode)
                    .orElse(null);
            if (ratePlan == null || !"active".equals(ratePlan.getStatus())) {
                return ResponseEntity.status(404).body(err(404, "价格计划不存在或已停用"));
            }

            // 新增：渠道发布校验
            boolean isPublished = channelPublishRecordRepo.existsByTenantIdAndHotelCodeAndChannelCodeAndRateCodeAndRoomTypeCode(
                    hotel.getTenantId(), hotelCode, channel.getChannelCode(), ratePlanCode, roomTypeCode);
            if (!isPublished) {
                return ResponseEntity.status(409).body(unavailable("RATE_PLAN_NOT_PUBLISHED", "该房型+价格计划未发布至该渠道"));
            }

            Date checkIn = parseDate(checkInStr);
            Date checkOut = parseDate(checkOutStr);
            if (checkIn == null || checkOut == null || !checkOut.after(checkIn)) {
                return ResponseEntity.badRequest().body(err(400, "入住/离店日期无效"));
            }

            int roomCount = body.get("roomCount") != null ? ((Number) body.get("roomCount")).intValue() : 1;
            int adultCount = body.get("adultCount") != null ? ((Number) body.get("adultCount")).intValue() : 1;
            int childCount = body.get("childCount") != null ? ((Number) body.get("childCount")).intValue() : 0;

            long nights = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
            if (nights <= 0) {
                return ResponseEntity.badRequest().body(err(400, "入住天数必须大于0"));
            }

            String memberLevel = getString(body, "memberLevel");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkOut);
            cal.add(Calendar.DATE, -1);
            Date lastNight = cal.getTime();

            // ========== 可订检查（与可订检查接口逻辑一致） ==========

            // 1. 房型适用性校验
            String applicableJson = ratePlan.getApplicableRoomTypes();
            if (applicableJson != null && !applicableJson.isBlank()
                    && !applicableJson.equals("[]") && !applicableJson.equals("null")) {
                try {
                    List<Object> applicableRaw = objectMapper.readValue(applicableJson, new com.fasterxml.jackson.core.type.TypeReference<>() {});
                    if (!applicableRaw.isEmpty()) {
                        List<String> applicableCodes = applicableRaw.stream().map(Object::toString).collect(Collectors.toList());
                        if (!applicableCodes.contains(roomTypeCode)) {
                            return ResponseEntity.status(409).body(unavailable("ROOM_TYPE_NOT_APPLICABLE", "价格计划不适用该房型"));
                        }
                    }
                } catch (Exception ignored) {}
            }

            // 2. 会员等级校验
            String pm = ratePlan.getPersonalMembership();
            boolean hasMemberReq = pm != null && !pm.isBlank() && !pm.equals("null") && !pm.equals("[]") && !pm.equals("{}");
            if (hasMemberReq) {
                if (memberLevel == null || memberLevel.isBlank()) {
                    return ResponseEntity.status(409).body(unavailable("MEMBER_INFO_REQUIRED", "该价格计划需要提供会员等级"));
                }
                if (!pm.contains(memberLevel)) {
                    return ResponseEntity.status(409).body(unavailable("MEMBER_LEVEL_MISMATCH", "会员等级不满足该价格计划要求"));
                }
            }

            // 3. 综合可售性检查（房态+预订规则+物理库存+超预订+多维度配额）
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
                return ResponseEntity.status(409).body(unavailable(code, availResult.getRejectReason()));
            }

            // 入住人数校验
            int maxOcc = roomType.getMaxOccupancy() != null ? roomType.getMaxOccupancy() : 2;
            if (adultCount + childCount > maxOcc) {
                return ResponseEntity.status(409).body(unavailable("EXCEED_MAX_OCCUPANCY", "超出最大入住人数 " + maxOcc));
            }

            // ========== 实时查询价格与对账 ==========

            List<HotelPrice> prices = priceRepo.findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDateBetween(
                    hotel.getTenantId(), hotelCode, ratePlanCode, roomTypeCode, checkIn, lastNight);
            if (prices.size() < nights) {
                return ResponseEntity.status(409).body(unavailable("PRICE_NOT_SET", "部分日期未设置价格"));
            }
            prices.sort(Comparator.comparing(HotelPrice::getPriceDate));
            Map<String, BigDecimal> dbPriceMap = prices.stream().collect(Collectors.toMap(
                    p -> formatDate(p.getPriceDate()), HotelPrice::getPriceWithTax));

            BigDecimal totalPriceFromDb = prices.stream()
                    .filter(p -> p.getPriceWithTax() != null)
                    .map(HotelPrice::getPriceWithTax)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(roomCount));

            // 执行双重对账
            BigDecimal inputTotal = getBigDecimal(body, "totalPrice");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> inputDailyPrices = (List<Map<String, Object>>) body.get("dailyPrices");

            if (inputDailyPrices != null && !inputDailyPrices.isEmpty()) {
                BigDecimal sumInputDaily = BigDecimal.ZERO;
                for (Map<String, Object> idp : inputDailyPrices) {
                    String d = getString(idp, "date");
                    BigDecimal p = getBigDecimal(idp, "price");
                    if (d == null || p == null) continue;
                    
                    // 1. 每日价格比对
                    BigDecimal dbP = dbPriceMap.get(d);
                    if (dbP == null || p.subtract(dbP).abs().compareTo(new BigDecimal("0.01")) > 0) {
                        return ResponseEntity.status(409).body(unavailable("PRICE_MISMATCH", 
                                String.format("日期 %s 价格不匹配。系统价格: %s, 传入价格: %s", d, dbP, p)));
                    }
                    sumInputDaily = sumInputDaily.add(p);
                }
                // 2. 总价对账
                BigDecimal calculatedTotal = sumInputDaily.multiply(BigDecimal.valueOf(roomCount));
                if (inputTotal != null && inputTotal.subtract(calculatedTotal).abs().compareTo(new BigDecimal("0.01")) > 0) {
                    return ResponseEntity.status(409).body(unavailable("PRICE_MISMATCH", 
                            String.format("总价不匹配。传入明细汇总: %s, 传入总价: %s", calculatedTotal, inputTotal)));
                }
            } else if (inputTotal != null) {
                // 退化逻辑：仅比对总价
                if (inputTotal.subtract(totalPriceFromDb).abs().compareTo(new BigDecimal("0.01")) > 0) {
                    return ResponseEntity.status(409).body(unavailable("PRICE_MISMATCH", 
                            String.format("总价不匹配。系统计算: %s, 传入总价: %s", totalPriceFromDb, inputTotal)));
                }
            }

            BigDecimal originalPriceFromDb = prices.stream()
                    .filter(p -> p.getPriceWithoutTax() != null)
                    .map(HotelPrice::getPriceWithoutTax)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(roomCount));
            if (originalPriceFromDb.compareTo(BigDecimal.ZERO) == 0) {
                originalPriceFromDb = totalPriceFromDb;
            }

            // 构建每日价格
            List<ReservationDailyPrice> dailyPrices = new ArrayList<>();
            for (HotelPrice hp : prices) {
                ReservationDailyPrice rdp = new ReservationDailyPrice();
                rdp.setPriceDate(hp.getPriceDate());
                rdp.setOriginalPrice(hp.getPriceWithoutTax());
                rdp.setActualPrice(hp.getPriceWithTax());
                rdp.setTaxAmount(hp.getPriceWithTax() != null && hp.getPriceWithoutTax() != null
                        ? hp.getPriceWithTax().subtract(hp.getPriceWithoutTax()) : BigDecimal.ZERO);
                rdp.setServiceCharge(null);
                rdp.setBreakfastIncluded(false);
                rdp.setBreakfastCount(0);
                rdp.setPackagesJson(ratePlan.getPackages());
                dailyPrices.add(rdp);
            }

            // ========== 构建订单 ==========

            Reservation reservation = new Reservation();
            reservation.setTenantId(hotel.getTenantId());
            reservation.setHotelId(hotel.getId());
            reservation.setHotelCode(hotelCode);
            reservation.setHotelName(hotel.getChineseName());
            reservation.setRoomTypeId(roomType.getId());
            reservation.setRoomTypeCode(roomTypeCode);
            reservation.setRoomTypeName(roomType.getRoomTypeName());
            reservation.setRatePlanId(ratePlan.getId());
            reservation.setRatePlanCode(ratePlanCode);
            reservation.setRatePlanName(ratePlan.getRateName());
            reservation.setChannelId(channel.getId());
            reservation.setChannelCode(channel.getChannelCode());
            reservation.setChannelName(channel.getChannelName());
            reservation.setChannelOrderNumber(getString(body, "channelOrderNumber"));
            reservation.setCheckInDate(checkIn);
            reservation.setCheckOutDate(checkOut);
            reservation.setNights((int) nights);
            reservation.setRoomCount(roomCount);
            reservation.setAdultCount(adultCount);
            reservation.setChildCount(childCount);

            @SuppressWarnings("unchecked")
            Map<String, Object> contact = (Map<String, Object>) body.get("contact");
            String contactName = getString(body, "contactName");
            String contactPhone = getString(body, "contactPhone");
            String contactEmail = getString(body, "contactEmail");
            if (contact != null) {
                if (contactName == null) contactName = getString(contact, "name");
                if (contactPhone == null) contactPhone = getString(contact, "phone");
                if (contactEmail == null) contactEmail = getString(contact, "email");
            }
            
            if (contactName == null || contactName.isBlank() || contactPhone == null || contactPhone.isBlank()) {
                return ResponseEntity.badRequest().body(err(400, "必须提供联系人姓名和电话"));
            }

            reservation.setContactName(contactName);
            reservation.setContactPhone(contactPhone);
            reservation.setContactEmail(contactEmail);

            reservation.setMemberNo(getString(body, "memberNo"));
            reservation.setMemberLevel(memberLevel);

            reservation.setTotalPrice(totalPriceFromDb);
            reservation.setOriginalPrice(originalPriceFromDb);
            reservation.setCurrency("CNY");

            @SuppressWarnings("unchecked")
            Map<String, Object> guaranteeInfo = (Map<String, Object>) body.get("guaranteeInfo");
            String guaranteeType = getString(body, "guaranteeType");
            String guaranteeInfoStr = getString(body, "guaranteeInfo");
            if (guaranteeInfo != null) {
                if (guaranteeType == null) guaranteeType = getString(guaranteeInfo, "type");
                if (guaranteeInfoStr == null) guaranteeInfoStr = getString(guaranteeInfo, "creditCardInfo");
            }
            reservation.setGuaranteeType(guaranteeType);
            reservation.setGuaranteeInfo(guaranteeInfoStr);

            // 1. 自动填充政策快照
            String cancelRule = ratePlan.getCancellationRule();
            if (cancelRule != null && !cancelRule.isBlank()) {
                CancellationPolicy cp = cancellationPolicyRepo.findByCode(cancelRule);
                if (cp != null) {
                    reservation.setCancellationPolicyCode(cp.getCode());
                    reservation.setCancellationPolicyDesc(cp.getDescription());
                } else {
                    reservation.setCancellationPolicyCode(cancelRule);
                }
            }

            // 2. 担保与支付逻辑
            reservation.setReservationStatus("confirmed"); // 默认
            reservation.setPaymentStatus("unpaid");
            String paymentType = "postpaid"; // 默认现付

            String guaranteeRule = ratePlan.getGuaranteeRule();
            if (guaranteeRule != null && !guaranteeRule.isBlank()) {
                GuaranteePolicy gp = guaranteePolicyRepo.findByCode(guaranteeRule);
                if (gp != null) {
                    reservation.setGuaranteePolicyCode(gp.getCode());
                    reservation.setGuaranteePolicyDesc(gp.getDescription());
                    
                    if ("credit_card".equalsIgnoreCase(gp.getType()) && (guaranteeInfoStr == null || guaranteeInfoStr.isBlank())) {
                        return ResponseEntity.badRequest().body(err(400, "该价格计划要求信用卡担保，必须提供 guaranteeInfo.creditCardInfo"));
                    }
                    
                    // 预付逻辑校验
                    if ("prepaid".equalsIgnoreCase(gp.getType())) {
                        paymentType = "prepaid";
                        reservation.setReservationStatus("pending_payment");
                        Calendar calDeadline = Calendar.getInstance();
                        calDeadline.add(Calendar.MINUTE, 30);
                        reservation.setPaymentDeadline(calDeadline.getTime());
                    }
                } else {
                    reservation.setGuaranteePolicyCode(guaranteeRule);
                }
            }

            // 3. 补充基础信息
            reservation.setSpecialRequest(getString(body, "specialRequest"));
            reservation.setGuestRemark(getString(body, "guestRemark"));
            reservation.setNotes(getString(body, "notes"));
            reservation.setCommissionRate(getBigDecimal(body, "commissionRate"));
            reservation.setCommissionAmount(getBigDecimal(body, "commissionAmount"));
            reservation.setOrderSource("channel");
            reservation.setCreatedBy("channel:" + channel.getChannelCode());

            List<ReservationGuest> guests = parseGuests(body);
            if (guests.isEmpty()) {
                return ResponseEntity.badRequest().body(err(400, "必须提供至少一位入住人信息"));
            }
            
            // 自动补齐缺失房间的入住人（使用联系人信息）
            if (guests.size() < roomCount) {
                int startIdx = guests.size();
                for (int i = startIdx; i < roomCount; i++) {
                    ReservationGuest rg = new ReservationGuest();
                    rg.setGuestType("guest");
                    rg.setName(contactName);
                    rg.setPhone(contactPhone);
                    rg.setEmail(contactEmail);
                    rg.setRoomIndex(i);
                    rg.setSortOrder(i);
                    guests.add(rg);
                }
            }
            
            List<ReservationPromotion> promotions = parsePromotions(body);

            Reservation created = reservationService.createReservation(
                    reservation, dailyPrices, guests, promotions);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("reservationId", created.getId());
            data.put("reservationCode", created.getReservationCode());
            data.put("reservationStatus", created.getReservationStatus());
            data.put("paymentType", paymentType);
            data.put("paymentDeadline", created.getPaymentDeadline() != null ? formatDateTime(created.getPaymentDeadline()) : null);
            data.put("hotelCode", hotelCode);
            data.put("hotelName", hotel.getChineseName());
            data.put("roomTypeCode", roomTypeCode);
            data.put("roomTypeName", roomType.getRoomTypeName());
            data.put("ratePlanCode", ratePlanCode);
            data.put("ratePlanName", ratePlan.getRateName());
            data.put("checkInDate", checkInStr);
            data.put("checkOutDate", checkOutStr);
            data.put("nights", nights);
            data.put("roomCount", roomCount);
            data.put("totalPrice", created.getTotalPrice());
            data.put("originalPrice", created.getOriginalPrice());
            data.put("currency", created.getCurrency());
            data.put("guaranteeType", created.getGuaranteeType());
            data.put("cancellationPolicyCode", created.getCancellationPolicyCode());
            data.put("cancellationPolicyDesc", created.getCancellationPolicyDesc());
            data.put("guaranteePolicyCode", created.getGuaranteePolicyCode());
            data.put("guaranteePolicyDesc", created.getGuaranteePolicyDesc());
            data.put("paymentStatus", created.getPaymentStatus());
            data.put("createdAt", formatDateTime(created.getCreatedAt()));

            return ResponseEntity.ok(ok(data));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(err(400, e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }

    @GetMapping("/reservations/{reservationCode}")
    public ResponseEntity<Map<String, Object>> getReservation(
            HttpServletRequest req,
            @PathVariable String reservationCode) {
        try {
            TenantChannel channel = getChannel(req);
            if (channel == null) {
                return ResponseEntity.status(401).body(err(401, "渠道认证失败"));
            }

            Reservation reservation = reservationService.getReservationByCode(reservationCode);
            if (reservation == null) {
                return ResponseEntity.status(404).body(err(404, "订单不存在"));
            }

            if (!reservation.getChannelId().equals(channel.getId())) {
                return ResponseEntity.status(403).body(err(403, "无权查看该订单"));
            }

            List<ReservationDailyPrice> dailyPrices = reservationService.getDailyPrices(reservation.getId());
            List<ReservationGuest> guests = reservationService.getGuests(reservation.getId());
            List<ReservationPromotion> promotions = reservationService.getPromotions(reservation.getId());

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("reservationCode", reservation.getReservationCode());
            data.put("channelOrderNumber", reservation.getChannelOrderNumber());
            data.put("reservationStatus", reservation.getReservationStatus());
            data.put("hotelCode", reservation.getHotelCode());
            data.put("hotelName", reservation.getHotelName());
            data.put("roomTypeCode", reservation.getRoomTypeCode());
            data.put("roomTypeName", reservation.getRoomTypeName());
            data.put("ratePlanCode", reservation.getRatePlanCode());
            data.put("ratePlanName", reservation.getRatePlanName());
            data.put("checkInDate", formatDate(reservation.getCheckInDate()));
            data.put("checkOutDate", formatDate(reservation.getCheckOutDate()));
            data.put("nights", reservation.getNights());
            data.put("roomCount", reservation.getRoomCount());
            data.put("adultCount", reservation.getAdultCount());
            data.put("childCount", reservation.getChildCount());
            data.put("contactName", reservation.getContactName());
            data.put("contactPhone", reservation.getContactPhone());
            data.put("totalPrice", reservation.getTotalPrice());
            data.put("originalPrice", reservation.getOriginalPrice());
            data.put("currency", reservation.getCurrency());
            data.put("guaranteeType", reservation.getGuaranteeType());
            data.put("cancellationPolicyCode", reservation.getCancellationPolicyCode());
            data.put("cancellationPolicyDesc", reservation.getCancellationPolicyDesc());
            data.put("guaranteePolicyCode", reservation.getGuaranteePolicyCode());
            data.put("guaranteePolicyDesc", reservation.getGuaranteePolicyDesc());
            data.put("paymentStatus", reservation.getPaymentStatus());
            data.put("createdAt", formatDateTime(reservation.getCreatedAt()));

            List<Map<String, Object>> dpList = dailyPrices.stream().map(dp -> {
                Map<String, Object> dpMap = new LinkedHashMap<>();
                dpMap.put("date", formatDate(dp.getPriceDate()));
                dpMap.put("originalPrice", dp.getOriginalPrice());
                dpMap.put("actualPrice", dp.getActualPrice());
                dpMap.put("taxAmount", dp.getTaxAmount());
                dpMap.put("serviceCharge", dp.getServiceCharge());
                dpMap.put("breakfastIncluded", dp.getBreakfastIncluded());
                dpMap.put("breakfastCount", dp.getBreakfastCount());
                return dpMap;
            }).collect(Collectors.toList());
            data.put("dailyPrices", dpList);

            List<Map<String, Object>> gList = guests.stream().map(g -> {
                Map<String, Object> gMap = new LinkedHashMap<>();
                gMap.put("name", g.getName());
                gMap.put("phone", g.getPhone());
                gMap.put("email", g.getEmail());
                gMap.put("guestType", g.getGuestType());
                gMap.put("roomIndex", g.getRoomIndex());
                return gMap;
            }).collect(Collectors.toList());
            data.put("guests", gList);

            List<Map<String, Object>> pList = promotions.stream().map(p -> {
                Map<String, Object> pMap = new LinkedHashMap<>();
                pMap.put("name", p.getPromotionName());
                pMap.put("discountType", p.getDiscountType());
                pMap.put("discountAmount", p.getDiscountAmount());
                pMap.put("provider", p.getProvider());
                return pMap;
            }).collect(Collectors.toList());
            data.put("promotions", pList);

            return ResponseEntity.ok(ok(data));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(err(400, e.getMessage()));
        }
    }

    @PostMapping("/reservations/{reservationCode}/cancel")
    public ResponseEntity<Map<String, Object>> cancelReservation(
            HttpServletRequest req,
            @PathVariable String reservationCode,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            TenantChannel channel = getChannel(req);
            if (channel == null) {
                return ResponseEntity.status(401).body(err(401, "渠道认证失败"));
            }

            Reservation reservation = reservationService.getReservationByCode(reservationCode);
            if (reservation == null) {
                return ResponseEntity.status(404).body(err(404, "订单不存在"));
            }

            if (!reservation.getChannelId().equals(channel.getId())) {
                return ResponseEntity.status(403).body(err(403, "无权操作该订单"));
            }

            String cancelReason = body != null ? getString(body, "cancelReason") : "";
            String operator = "channel:" + channel.getChannelCode();

            // 校验取消政策
            String policyCode = reservation.getCancellationPolicyCode();
            if (policyCode != null) {
                CancellationPolicy policy = cancellationPolicyRepo.findByCode(policyCode);
                if (policy != null) {
                    if ("non_refundable".equalsIgnoreCase(policy.getType())) {
                        return ResponseEntity.status(409).body(err(409, "该订单不可退（Non-refundable）"));
                    }
                    if ("limited".equalsIgnoreCase(policy.getType())) {
                        // 计算取消截止时间
                        Calendar deadline = Calendar.getInstance();
                        deadline.setTime(reservation.getCheckInDate());
                        int days = policy.getCancellationDays() != null ? policy.getCancellationDays() : 0;
                        deadline.add(Calendar.DATE, -days);
                        
                        String timeStr = policy.getCancellationTime() != null ? policy.getCancellationTime() : "18:00";
                        String[] parts = timeStr.split(":");
                        deadline.set(Calendar.HOUR_OF_DAY, parts.length > 0 ? Integer.parseInt(parts[0]) : 18);
                        deadline.set(Calendar.MINUTE, parts.length > 1 ? Integer.parseInt(parts[1]) : 0);
                        deadline.set(Calendar.SECOND, 0);
                        deadline.set(Calendar.MILLISECOND, 0);

                        if (new Date().after(deadline.getTime())) {
                            return ResponseEntity.status(409).body(err(409, 
                                    "已超过免费取消截止时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(deadline.getTime())));
                        }
                    }
                }
            }

            Reservation cancelled = reservationService.cancelReservation(
                    reservation.getId(), operator, cancelReason);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("reservationCode", cancelled.getReservationCode());
            data.put("reservationStatus", cancelled.getReservationStatus());
            data.put("cancelledAt", formatDateTime(cancelled.getCancelledAt()));

            return ResponseEntity.ok(ok(data));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            if (e.getCause() != null) {
                msg += " | cause: " + (e.getCause().getMessage() != null ? e.getCause().getMessage() : e.getCause().getClass().getSimpleName());
            }
            return ResponseEntity.badRequest().body(err(400, msg));
        }
    }

    private TenantChannel getChannel(HttpServletRequest req) {
        return (TenantChannel) req.getAttribute("openApiChannel");
    }

    private boolean hasHotelAccess(TenantChannel channel, Integer hotelId) {
        List<ChannelHotelMapping> mappings = channelHotelMappingRepo
                .findByChannelIdAndHotelId(channel.getId(), hotelId);
        return mappings.stream().anyMatch(m -> "active".equals(m.getStatus()));
    }

    private String now() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200);
        r.put("message", "success");
        r.put("data", data);
        r.put("timestamp", now());
        return r;
    }

    private Map<String, Object> err(int code, String message) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", code);
        r.put("message", message);
        r.put("timestamp", now());
        return r;
    }

    private Map<String, Object> unavailable(String reason, String desc) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 409);
        r.put("message", desc);
        r.put("timestamp", now());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("available", false);
        data.put("reason", reason);
        data.put("reasonDescription", desc);
        r.put("data", data);
        return r;
    }

    @SuppressWarnings("unchecked")
    private List<ReservationGuest> parseGuests(Map<String, Object> body) {
        Object gObj = body.get("guests");
        if (gObj == null) return Collections.emptyList();
        List<Map<String, Object>> gList = (List<Map<String, Object>>) gObj;
        List<ReservationGuest> result = new ArrayList<>();
        int sortOrder = 0;
        for (Map<String, Object> g : gList) {
            ReservationGuest rg = new ReservationGuest();
            rg.setGuestType(getString(g, "guestType") != null ? getString(g, "guestType") : "guest");
            rg.setName(getString(g, "name"));
            rg.setPhone(getString(g, "phone"));
            rg.setEmail(getString(g, "email"));
            rg.setIdType(getString(g, "idType"));
            rg.setIdNumber(getString(g, "idNumber"));
            rg.setMemberNo(getString(g, "memberNo"));
            rg.setMemberLevel(getString(g, "memberLevel"));
            Object ri = g.get("roomIndex");
            if (ri instanceof Number) rg.setRoomIndex(((Number) ri).intValue());
            rg.setSortOrder(sortOrder++);
            result.add(rg);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<ReservationPromotion> parsePromotions(Map<String, Object> body) {
        Object pObj = body.get("promotions");
        if (pObj == null) return Collections.emptyList();
        List<Map<String, Object>> pList = (List<Map<String, Object>>) pObj;
        List<ReservationPromotion> result = new ArrayList<>();
        for (Map<String, Object> p : pList) {
            ReservationPromotion rp = new ReservationPromotion();
            rp.setPromotionName(getString(p, "name"));
            rp.setDiscountType(getString(p, "discountType"));
            rp.setDiscountValue(getBigDecimal(p, "discountValue"));
            rp.setDiscountAmount(getBigDecimal(p, "discountAmount") != null ? getBigDecimal(p, "discountAmount") : BigDecimal.ZERO);
            rp.setPromotionCode(getString(p, "promotionCode"));
            rp.setProvider(getString(p, "provider"));
            result.add(rp);
        }
        return result;
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private String formatDate(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private String formatDateTime(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return null;
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        try { return new BigDecimal(val.toString()); } catch (NumberFormatException e) { return null; }
    }
}
