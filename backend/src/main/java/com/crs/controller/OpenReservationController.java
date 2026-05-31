package com.crs.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crs.entity.BookingControl;
import com.crs.entity.CancellationPolicy;
import com.crs.entity.ChannelHotelMapping;
import com.crs.entity.GuaranteePolicy;
import com.crs.entity.Hotel;
import com.crs.entity.HotelPrice;
import com.crs.entity.HotelRoomType;
import com.crs.entity.RatePlan;
import com.crs.entity.Reservation;
import com.crs.entity.ReservationDailyPrice;
import com.crs.entity.ReservationDailyPriceTax;
import com.crs.entity.ReservationGuest;
import com.crs.entity.ReservationPromotion;
import com.crs.entity.TenantChannel;
import com.crs.repository.BookingControlRepository;
import com.crs.repository.PackageRepository;
import com.crs.repository.CancellationPolicyRepository;
import com.crs.repository.ChannelHotelMappingRepository;
import com.crs.repository.ChannelPublishRecordRepository;
import com.crs.repository.GuaranteePolicyRepository;
import com.crs.repository.HotelPriceRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.repository.ReservationRepository;
import com.crs.service.ReservationService;
import com.crs.service.inventory.AvailabilityContext;
import com.crs.service.inventory.AvailabilityResult;
import com.crs.service.inventory.InventoryDeductionService;
import com.crs.util.CancellationPolicyTypeUtil;
import com.crs.util.GuaranteePolicyTypeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * OpenReservationController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【OpenReservationController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 OpenReservationController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
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
    private final BookingControlRepository bookingControlRepo;
    private final PackageRepository packageRepo;
    private final com.crs.repository.PackageDailyPriceRepository packageDailyPriceRepo;
    private final com.crs.repository.TaxSettingRepository taxSettingRepo;
    private final com.crs.repository.ReservationDailyPriceTaxRepository reservationDailyPriceTaxRepo;
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
            ReservationRepository reservationRepo,
            BookingControlRepository bookingControlRepo,
            PackageRepository packageRepo,
            com.crs.repository.PackageDailyPriceRepository packageDailyPriceRepo,
            com.crs.repository.TaxSettingRepository taxSettingRepo,
            com.crs.repository.ReservationDailyPriceTaxRepository reservationDailyPriceTaxRepo) {
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
        this.bookingControlRepo = bookingControlRepo;
        this.packageRepo = packageRepo;
        this.packageDailyPriceRepo = packageDailyPriceRepo;
        this.taxSettingRepo = taxSettingRepo;
        this.reservationDailyPriceTaxRepo = reservationDailyPriceTaxRepo;
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

            if (reservationRepo.existsByTenantIdAndChannelCodeAndChannelOrderNumber(channel.getTenantId(), channel.getChannelCode(), channelOrderNumber)) {
                return ResponseEntity.status(409).body(err(409, "订单已存在，请勿重复提交 (Duplicate Order)"));
            }


            Hotel hotel = hotelRepo.findByHotelCodeAndTenantId(hotelCode, channel.getTenantId()).orElse(null);
            if (hotel == null || hotel.getStatus() != Hotel.Status.active) {
                return ResponseEntity.status(404).body(err(404, "酒店不存在或已停用"));
            }
            BigDecimal minimumPrice = getHotelMinimumPrice(hotel);

            if (!hasHotelAccess(channel, hotel.getHotelCode())) {
                return ResponseEntity.status(403).body(err(403, "渠道无权访问该酒店"));
            }

            HotelRoomType roomType = roomTypeRepo.findByTenantIdAndHotelCodeAndRoomTypeCode(hotel.getTenantId(), hotel.getHotelCode(), roomTypeCode)
                    .orElse(null);
            if (roomType == null || !"active".equals(roomType.getStatus())) {
                return ResponseEntity.status(404).body(err(404, "房型不存在或已停用"));
            }

            RatePlan ratePlan = ratePlanRepo.findByTenantIdAndHotelCodeAndRateCode(hotel.getTenantId(), hotel.getHotelCode(), ratePlanCode)
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

            String checkInValidationMessage = validateOpenApiCheckInDate(checkIn);
            if (checkInValidationMessage != null) {
                return ResponseEntity.badRequest().body(err(400, checkInValidationMessage));
            }

            int roomCount = body.get("roomCount") != null ? ((Number) body.get("roomCount")).intValue() : 1;
            int adultCount = body.get("adultCount") != null ? ((Number) body.get("adultCount")).intValue() : 1;
            int childCount = body.get("childCount") != null ? ((Number) body.get("childCount")).intValue() : 0;

            long nights = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
            if (nights <= 0) {
                return ResponseEntity.badRequest().body(err(400, "入住天数必须大于0"));
            }

            String memberLevel = getString(body, "memberLevel");
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
                    return ResponseEntity.status(409).body(unavailable("PRICE_NOT_SET", "部分日期未设置价格"));
                }
                effectivePrices.add(validPrice);
                cal.add(Calendar.DATE, 1);
            }

            effectivePrices.sort(Comparator.comparing(HotelPrice::getPriceDate));
            Map<String, BigDecimal> dbPriceMap = effectivePrices.stream().collect(Collectors.toMap(
                    p -> formatDate(p.getPriceDate()),
                    p -> applyHotelMinimumPrice(p.getPriceWithTax(), minimumPrice)));

            BigDecimal totalPriceFromDb = effectivePrices.stream()
                    .map(HotelPrice::getPriceWithTax)
                    .map(price -> applyHotelMinimumPrice(price, minimumPrice))
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

            BigDecimal originalPriceFromDb = effectivePrices.stream()
                    .filter(p -> p.getPriceWithoutTax() != null)
                    .map(HotelPrice::getPriceWithoutTax)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(roomCount));
            if (originalPriceFromDb.compareTo(BigDecimal.ZERO) == 0) {
                originalPriceFromDb = totalPriceFromDb;
            }

            // 构建每日价格（自动解析包价详情快照与早餐翻译）
            List<ReservationDailyPrice> dailyPrices = new ArrayList<>();
            for (HotelPrice hp : effectivePrices) {
                BigDecimal actualPrice = applyHotelMinimumPrice(hp.getPriceWithTax(), minimumPrice);
                
                Map<String, Object> bfPkgInfo = resolvePackageDetailsAndBreakfast(
                        hotel.getTenantId(), hotelCode, ratePlan.getPackages(), hp.getPriceDate());
                String snapshotJson = (String) bfPkgInfo.get("packagesJson");
                boolean bfIncluded = (Boolean) bfPkgInfo.get("breakfastIncluded");
                int bfCount = (Integer) bfPkgInfo.get("breakfastCount");

                BigDecimal vatRateVal = BigDecimal.ZERO;
                BigDecimal svcRateVal = BigDecimal.ZERO;
                
                String codesStr = hotel.getTaxRateCodes();
                if (codesStr != null && !codesStr.isBlank()) {
                    String[] codes = codesStr.split(",");
                    List<com.crs.entity.TaxSetting> tenantTaxes = taxSettingRepo.findByTenantIdAndStatus(hotel.getTenantId(), "active");
                    for (String code : codes) {
                        if (code == null || code.isBlank()) continue;
                        for (com.crs.entity.TaxSetting tax : tenantTaxes) {
                            if (code.trim().equalsIgnoreCase(tax.getTaxCode())) {
                                BigDecimal rate = tax.getRateAmount();
                                if (rate != null) {
                                    if (tax.getTaxCode().contains("VAT")) {
                                        vatRateVal = rate;
                                    } else if (tax.getTaxCode().contains("SERVICE")) {
                                        svcRateVal = rate;
                                    }
                                }
                            }
                        }
                    }
                }
                
                BigDecimal basePrice = hp.getPriceWithoutTax();
                BigDecimal calculatedVat = basePrice != null && vatRateVal.compareTo(BigDecimal.ZERO) > 0
                        ? basePrice.multiply(vatRateVal.divide(BigDecimal.valueOf(100))).setScale(2, java.math.RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                BigDecimal calculatedSvc = basePrice != null && svcRateVal.compareTo(BigDecimal.ZERO) > 0
                        ? basePrice.multiply(svcRateVal.divide(BigDecimal.valueOf(100))).setScale(2, java.math.RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                ReservationDailyPrice rdp = new ReservationDailyPrice();
                rdp.setPriceDate(hp.getPriceDate());
                rdp.setOriginalPrice(hp.getPriceWithoutTax());
                rdp.setActualPrice(actualPrice);
                rdp.setTaxAmount(calculatedVat);
                rdp.setServiceCharge(calculatedSvc);
                rdp.setBreakfastIncluded(bfIncluded);
                rdp.setBreakfastCount(bfCount);
                rdp.setPackagesJson(snapshotJson);
                dailyPrices.add(rdp);
            }

            // ========== 构建订单 ==========

            Reservation reservation = new Reservation();
            reservation.setTenantId(hotel.getTenantId());
            reservation.setHotelCode(hotelCode);
            reservation.setHotelName(hotel.getChineseName());
            reservation.setRoomTypeCode(roomTypeCode);
            reservation.setRoomTypeName(roomType.getRoomTypeName());
            reservation.setRatePlanCode(ratePlanCode);
            reservation.setRatePlanName(ratePlan.getRateName());
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
            String guaranteeType = GuaranteePolicyTypeUtil.normalizeType(getString(body, "guaranteeType"));
            String guaranteeInfoStr = getString(body, "guaranteeInfo");
            if (guaranteeInfo != null) {
                if (guaranteeType == null) guaranteeType = GuaranteePolicyTypeUtil.normalizeType(getString(guaranteeInfo, "type"));
                if (guaranteeInfoStr == null) guaranteeInfoStr = getString(guaranteeInfo, "creditCardInfo");
            }
            reservation.setGuaranteeInfo(guaranteeInfoStr);

            // 1. 自动填充政策快照（合并计算多日期与多维度最严取消规则）
            String cancelRule = ratePlan.getCancellationRule();
            String effectiveCancelRule = resolveEffectiveCancellationRule(
                    channel.getTenantId(), cancelRule, hotelCode, ratePlanCode,
                    channel.getChannelCode(), ratePlan.getRateCategory(), null,
                    checkIn, checkOut);
            if (effectiveCancelRule != null && !effectiveCancelRule.isBlank()) {
                CancellationPolicy cp = cancellationPolicyRepo.findByTenantIdAndCode(channel.getTenantId(), effectiveCancelRule);
                if (cp != null) {
                    reservation.setCancellationPolicyCode(cp.getCode());
                    reservation.setCancellationPolicyDesc(cp.getDescription());
                } else {
                    reservation.setCancellationPolicyCode(effectiveCancelRule);
                }
            }

            // 2. 担保与支付逻辑
            reservation.setReservationStatus("confirmed"); // 默认
            reservation.setPaymentStatus("unpaid");
            String paymentType = "postpaid"; // 默认现付

            String guaranteeRule = ratePlan.getGuaranteeRule();
            if (guaranteeRule != null && !guaranteeRule.isBlank()) {
                GuaranteePolicy gp = guaranteePolicyRepo.findByTenantIdAndCode(channel.getTenantId(), guaranteeRule);
                if (gp != null) {
                    String appliedGuaranteeType = GuaranteePolicyTypeUtil.normalizeType(gp.getType());
                    reservation.setGuaranteePolicyCode(gp.getCode());
                    reservation.setGuaranteePolicyDesc(gp.getDescription());
                    reservation.setGuaranteeType(appliedGuaranteeType);

                    if (guaranteeType != null && appliedGuaranteeType != null && !appliedGuaranteeType.equals(guaranteeType)) {
                        return ResponseEntity.badRequest().body(err(400, "guaranteeInfo.type 与价格计划绑定的担保政策类型不一致"));
                    }
                    
                    if (GuaranteePolicyTypeUtil.isCreditCardType(appliedGuaranteeType) && (guaranteeInfoStr == null || guaranteeInfoStr.isBlank())) {
                        return ResponseEntity.badRequest().body(err(400, "该价格计划要求信用卡担保，必须提供 guaranteeInfo.creditCardInfo"));
                    }
                    
                    // 预付逻辑校验
                    if (GuaranteePolicyTypeUtil.isPrepaidType(appliedGuaranteeType)) {
                        paymentType = "prepaid";
                        if (!Boolean.FALSE.equals(channel.getPrepaidOrderRequiresPayment())) {
                            reservation.setReservationStatus("pending_payment");
                            Calendar calDeadline = Calendar.getInstance();
                            calDeadline.add(Calendar.MINUTE, 30);
                            reservation.setPaymentDeadline(calDeadline.getTime());
                        }
                    }
                } else {
                    reservation.setGuaranteePolicyCode(guaranteeRule);
                }
            }

            // 3. 补充基础信息
            String specialReqVal = getString(body, "specialRequest");
            reservation.setSpecialRequest(specialReqVal);
            reservation.setGuestRemark(specialReqVal); // 用 specialRequest 作为客人备注
            reservation.setNotes(null); // 系统内部备忘清空，不接受外部渠道越权写入
            reservation.setCommissionRate(getBigDecimal(body, "commissionRate"));
            reservation.setCommissionAmount(getBigDecimal(body, "commissionAmount"));
            reservation.setOrderSource("channel");
            reservation.setCreatedBy("channel:" + channel.getChannelCode());

            String guestPayloadValidationError = validateGuestsPayload(body);
            if (guestPayloadValidationError != null) {
                return ResponseEntity.badRequest().body(err(400, guestPayloadValidationError));
            }

            List<ReservationGuest> guests = parseGuests(body);
            String guestValidationError = validateGuestsForRoomAssignment(guests, roomCount);
            if (guestValidationError != null) {
                return ResponseEntity.badRequest().body(err(400, guestValidationError));
            }

            List<ReservationPromotion> promotions = parsePromotions(body);

            Reservation created = reservationService.createReservation(
                    reservation, dailyPrices, guests, promotions);

            // 联动多税率拆分落库明细
            List<com.crs.entity.TaxSetting> activeTaxes = new ArrayList<>();
            String hotelTaxCodesStr = hotel.getTaxRateCodes();
            if (hotelTaxCodesStr != null && !hotelTaxCodesStr.isBlank()) {
                String[] codes = hotelTaxCodesStr.split(",");
                List<com.crs.entity.TaxSetting> tenantTaxes = taxSettingRepo.findByTenantIdAndStatus(hotel.getTenantId(), "active");
                for (String code : codes) {
                    if (code == null || code.isBlank()) continue;
                    for (com.crs.entity.TaxSetting tax : tenantTaxes) {
                        if (code.trim().equalsIgnoreCase(tax.getTaxCode())) {
                            activeTaxes.add(tax);
                        }
                    }
                }
            }

            for (ReservationDailyPrice dp : dailyPrices) {
                if (dp.getId() != null && dp.getOriginalPrice() != null) {
                    BigDecimal basePrice = dp.getOriginalPrice();
                    for (com.crs.entity.TaxSetting tax : activeTaxes) {
                        BigDecimal rate = tax.getRateAmount();
                        if (rate != null) {
                            BigDecimal taxVal = basePrice.multiply(rate.divide(BigDecimal.valueOf(100))).setScale(2, java.math.RoundingMode.HALF_UP);
                            
                            ReservationDailyPriceTax dailyTax = new ReservationDailyPriceTax();
                            dailyTax.setReservationDailyPriceId(dp.getId());
                            dailyTax.setTaxCode(tax.getTaxCode());
                            dailyTax.setTaxName(tax.getLegalName());
                            dailyTax.setRateAmount(rate);
                            dailyTax.setCalculatedAmount(taxVal);
                            
                            reservationDailyPriceTaxRepo.save(dailyTax);
                        }
                    }
                }
            }

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
            data.put("guaranteeType", GuaranteePolicyTypeUtil.normalizeType(created.getGuaranteeType()));
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

            if (!channel.getChannelCode().equals(reservation.getChannelCode())) {
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
            data.put("guaranteeType", GuaranteePolicyTypeUtil.normalizeType(reservation.getGuaranteeType()));
            data.put("cancellationPolicyCode", reservation.getCancellationPolicyCode());
            data.put("cancellationPolicyDesc", reservation.getCancellationPolicyDesc());
            data.put("guaranteePolicyCode", reservation.getGuaranteePolicyCode());
            data.put("guaranteePolicyDesc", reservation.getGuaranteePolicyDesc());
            data.put("paymentStatus", reservation.getPaymentStatus());
            data.put("specialRequest", reservation.getGuestRemark()); // 补充客人备注回显
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
                
                List<ReservationDailyPriceTax> taxDetails = reservationDailyPriceTaxRepo.findByReservationDailyPriceId(dp.getId());
                List<Map<String, Object>> taxDetailsMap = taxDetails.stream().map(t -> {
                    Map<String, Object> tMap = new LinkedHashMap<>();
                    tMap.put("taxCode", t.getTaxCode());
                    tMap.put("taxName", t.getTaxName());
                    tMap.put("rateAmount", t.getRateAmount());
                    tMap.put("calculatedAmount", t.getCalculatedAmount());
                    return tMap;
                }).collect(Collectors.toList());
                dpMap.put("taxes", taxDetailsMap);
                
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

            if (!channel.getChannelCode().equals(reservation.getChannelCode())) {
                return ResponseEntity.status(403).body(err(403, "无权操作该订单"));
            }

            String cancelReason = body != null ? getString(body, "cancelReason") : "";
            String operator = "channel:" + channel.getChannelCode();

            if (!Boolean.FALSE.equals(channel.getCancelOrderChecksCancellationRule())) {
                // 校验取消政策
                String policyCode = reservation.getCancellationPolicyCode();
                if (policyCode != null) {
                    CancellationPolicy policy = cancellationPolicyRepo.findByTenantIdAndCode(reservation.getTenantId(), policyCode);
                    if (policy != null) {
                        if (CancellationPolicyTypeUtil.isNonRefundableType(policy.getType())) {
                            return ResponseEntity.status(409).body(err(409, "该订单不可退（Non-refundable）"));
                        }
                        if (CancellationPolicyTypeUtil.isLimitedType(policy.getType())) {
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

    @PostMapping("/reservations/{reservationCode}/pay")
    public ResponseEntity<Map<String, Object>> payReservation(
            HttpServletRequest req,
            @PathVariable String reservationCode,
            @RequestBody Map<String, Object> body) {
        try {
            TenantChannel channel = getChannel(req);
            if (channel == null) {
                return ResponseEntity.status(401).body(err(401, "渠道认证失败"));
            }

            String paymentMethod = getString(body, "paymentMethod");
            BigDecimal paymentAmount = getBigDecimal(body, "paymentAmount");
            String transactionId = getString(body, "transactionId");

            if (paymentMethod == null || paymentAmount == null || transactionId == null || transactionId.isBlank()) {
                return ResponseEntity.badRequest().body(err(400, "缺少必填参数：paymentMethod, paymentAmount, transactionId"));
            }

            // 1. 获取订单实体快照，用于前置业务规则及权限判定
            Reservation reservation = reservationService.getReservationByCode(reservationCode);
            if (reservation == null) {
                return ResponseEntity.status(404).body(err(404, "订单不存在"));
            }
            if (!channel.getChannelCode().equals(reservation.getChannelCode())) {
                return ResponseEntity.status(403).body(err(403, "无权操作该订单"));
            }

            // 2. 强类型熔断拦截：如果订单是预付模式，且所属渠道在配置中设定为免 API 支付核销（prepaidOrderRequiresPayment = false）
            if (GuaranteePolicyTypeUtil.isPrepaidType(reservation.getGuaranteeType())
                    && Boolean.FALSE.equals(channel.getPrepaidOrderRequiresPayment())) {
                return ResponseEntity.status(409).body(unavailable("PAYMENT_NOT_REQUIRED", "该渠道的预付订单无需发起在线支付核销回调"));
            }

            String operator = "channel:" + channel.getChannelCode();

            Reservation paidReservation;
            try {
                paidReservation = reservationService.payReservation(
                        reservationCode, paymentMethod, paymentAmount, transactionId, operator);
            } catch (RuntimeException e) {
                if ("ORDER_ALREADY_CANCELLED".equals(e.getMessage())) {
                    return ResponseEntity.status(409).body(unavailable("ORDER_ALREADY_CANCELLED", "当前订单已被取消，无法进行支付核销"));
                }
                return ResponseEntity.status(409).body(err(409, e.getMessage()));
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("reservationCode", paidReservation.getReservationCode());
            data.put("reservationStatus", paidReservation.getReservationStatus());
            data.put("paymentStatus", paidReservation.getPaymentStatus());
            data.put("paidAmount", paymentAmount);
            data.put("transactionId", transactionId);

            return ResponseEntity.ok(ok(data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(err(400, e.getMessage()));
        }
    }

    private TenantChannel getChannel(HttpServletRequest req) {
        return (TenantChannel) req.getAttribute("openApiChannel");
    }

    private boolean hasHotelAccess(TenantChannel channel, String hotelCode) {
        List<ChannelHotelMapping> mappings = channelHotelMappingRepo
                .findByTenantIdAndChannelCodeAndHotelCode(channel.getTenantId(), channel.getChannelCode(), hotelCode);
        return mappings.stream().anyMatch(m -> "active".equals(m.getStatus()));
    }

    private String now() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
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
            rg.setRoomIndex(null);
            Object ri = g.get("roomIndex");
            if (ri instanceof Number) rg.setRoomIndex(((Number) ri).intValue());
            rg.setSortOrder(sortOrder++);
            result.add(rg);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private String validateGuestsPayload(Map<String, Object> body) {
        Object gObj = body.get("guests");
        if (gObj == null) {
            return null;
        }
        if (!(gObj instanceof List<?> rawGuests)) {
            return "guests 格式不正确，必须为数组";
        }

        int guestIndex = 0;
        for (Object rawGuest : rawGuests) {
            if (!(rawGuest instanceof Map<?, ?> rawGuestMap)) {
                return "guests[" + guestIndex + "] 格式不正确，必须为对象";
            }
            Map<String, Object> guest = (Map<String, Object>) rawGuestMap;
            if (!guest.containsKey("roomIndex")) {
                return "guests[" + guestIndex + "].roomIndex 必填";
            }
            Object roomIndex = guest.get("roomIndex");
            if (!(roomIndex instanceof Number)) {
                return "guests[" + guestIndex + "].roomIndex 格式不正确，必须为整数";
            }
            guestIndex++;
        }
        return null;
    }

    private String validateGuestsForRoomAssignment(List<ReservationGuest> guests, int roomCount) {
        if (guests == null || guests.isEmpty()) {
            return "必须提供入住人信息，且每个房间至少需要一位入住人";
        }
        if (roomCount <= 0) {
            return "roomCount 必须大于 0";
        }
        if (guests.size() < roomCount) {
            return "预订" + roomCount + "间房时，至少需要" + roomCount + "位入住人";
        }

        boolean[] coveredRooms = new boolean[roomCount];
        for (int guestIndex = 0; guestIndex < guests.size(); guestIndex++) {
            ReservationGuest guest = guests.get(guestIndex);
            Integer roomIndex = guest.getRoomIndex();
            if (roomIndex == null) {
                return "每位入住人都必须提供 roomIndex";
            }
            if (roomIndex < 0 || roomIndex >= roomCount) {
                if (roomCount == 1) {
                    return "当前只预订了 1 间房，guests[" + guestIndex + "].roomIndex 只能填写 0";
                }
                return "guests[" + guestIndex + "].roomIndex 超出范围，当前预订了 " + roomCount
                        + " 间房，只能填写 0 到 " + (roomCount - 1);
            }
            coveredRooms[roomIndex] = true;
        }

        Set<Integer> missingRooms = new TreeSet<>();
        for (int i = 0; i < coveredRooms.length; i++) {
            if (!coveredRooms[i]) {
                missingRooms.add(i);
            }
        }
        if (!missingRooms.isEmpty()) {
            return "每个房间至少需要一位入住人，缺少房间序号：" + missingRooms;
        }
        return null;
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

    private boolean isEffectiveHotelPrice(HotelPrice hotelPrice) {
        return hotelPrice != null
                && "active".equalsIgnoreCase(hotelPrice.getStatus())
                && hotelPrice.getPriceWithTax() != null
                && hotelPrice.getPriceWithTax().compareTo(BigDecimal.ZERO) > 0;
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

    private Map<String, Object> resolvePackageDetailsAndBreakfast(Integer tenantId, String hotelCode, String packagesJson, Date priceDate) {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("packagesJson", "[]");
        result.put("breakfastIncluded", false);
        result.put("breakfastCount", 0);

        if (packagesJson == null || packagesJson.isBlank() || packagesJson.equals("null")) {
            return result;
        }

        try {
            List<Object> rawItems = objectMapper.readValue(packagesJson, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            List<String> packageCodes = new ArrayList<>();

            for (Object item : rawItems) {
                if (item instanceof String code) {
                    if (code != null && !code.isBlank()) {
                        packageCodes.add(code);
                    }
                } else if (item instanceof Map<?, ?> rawMap) {
                    String code = Objects.toString(rawMap.get("code"), Objects.toString(rawMap.get("packageCode"), null));
                    if (code != null && !code.isBlank()) {
                        packageCodes.add(code);
                    }
                }
            }

            if (packageCodes.isEmpty()) {
                return result;
            }

            List<com.crs.entity.Package> activePackages = packageRepo
                    .findByTenantIdAndCodeInAndStatus(tenantId, packageCodes, com.crs.entity.Package.Status.active);

            List<Map<String, Object>> snapshotList = new ArrayList<>();
            boolean hasBreakfast = false;
            int totalBreakfastCount = 0;

            java.time.LocalDate localPriceDate = priceDate != null ? new java.sql.Date(priceDate.getTime()).toLocalDate() : null;

            for (com.crs.entity.Package ap : activePackages) {
                int qty = ap.getFixedQuantity() != null ? ap.getFixedQuantity() : 1;
                
                BigDecimal price = BigDecimal.ZERO;
                if ("daily".equalsIgnoreCase(ap.getPriceType())) {
                    if (hotelCode != null && localPriceDate != null) {
                        Optional<com.crs.entity.PackageDailyPrice> pdpOpt = packageDailyPriceRepo
                                .findByTenantIdAndHotelCodeAndPackageCodeAndPriceDate(tenantId, hotelCode, ap.getCode(), localPriceDate);
                        if (pdpOpt.isPresent() && pdpOpt.get().getSalePrice() != null) {
                            price = pdpOpt.get().getSalePrice();
                        }
                    }
                } else {
                    price = ap.getFixedPrice() != null ? BigDecimal.valueOf(ap.getFixedPrice()) : BigDecimal.ZERO;
                }

                Boolean taxIncluded = ap.getTaxIncluded() != null ? ap.getTaxIncluded() : false;
                BigDecimal inclusivePrice;
                BigDecimal exclusivePrice;
                
                if (taxIncluded) {
                    inclusivePrice = price.setScale(2, java.math.RoundingMode.HALF_UP);
                    exclusivePrice = price.divide(BigDecimal.valueOf(1.06), 2, java.math.RoundingMode.HALF_UP);
                } else {
                    exclusivePrice = price.setScale(2, java.math.RoundingMode.HALF_UP);
                    inclusivePrice = price.multiply(BigDecimal.valueOf(1.06)).setScale(2, java.math.RoundingMode.HALF_UP);
                }

                Map<String, Object> pSnapshot = new LinkedHashMap<>();
                pSnapshot.put("code", ap.getCode());
                pSnapshot.put("name", ap.getName());
                pSnapshot.put("type", ap.getType());
                pSnapshot.put("quantity", qty);
                pSnapshot.put("price", price.doubleValue());
                pSnapshot.put("taxIncluded", taxIncluded);
                pSnapshot.put("inclusivePrice", inclusivePrice.doubleValue());
                pSnapshot.put("exclusivePrice", exclusivePrice.doubleValue());
                snapshotList.add(pSnapshot);

                if ("breakfast".equalsIgnoreCase(ap.getType())) {
                    hasBreakfast = true;
                    totalBreakfastCount += qty;
                }
            }

            result.put("packagesJson", objectMapper.writeValueAsString(snapshotList));
            result.put("breakfastIncluded", hasBreakfast);
            result.put("breakfastCount", totalBreakfastCount);
        } catch (Exception ignored) {}

        return result;
    }
}
