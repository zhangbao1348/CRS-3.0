package com.crs.controller;

import com.crs.entity.*;
import com.crs.repository.*;
import com.crs.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenReservationController(
            ReservationService reservationService,
            HotelRepository hotelRepo,
            HotelRoomTypeRepository roomTypeRepo,
            RatePlanRepository ratePlanRepo,
            ChannelHotelMappingRepository channelHotelMappingRepo) {
        this.reservationService = reservationService;
        this.hotelRepo = hotelRepo;
        this.roomTypeRepo = roomTypeRepo;
        this.ratePlanRepo = ratePlanRepo;
        this.channelHotelMappingRepo = channelHotelMappingRepo;
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

            if (hotelCode == null || roomTypeCode == null || ratePlanCode == null
                    || checkInStr == null || checkOutStr == null) {
                return ResponseEntity.badRequest().body(err(400, "缺少必填参数：hotelCode, roomTypeCode, ratePlanCode, checkInDate, checkOutDate"));
            }

            Hotel hotel = hotelRepo.findByHotelCode(hotelCode).orElse(null);
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
            reservation.setContactName(getString(body, "contactName"));
            reservation.setContactPhone(getString(body, "contactPhone"));
            reservation.setContactEmail(getString(body, "contactEmail"));
            reservation.setMemberNo(getString(body, "memberNo"));
            reservation.setMemberLevel(getString(body, "memberLevel"));
            reservation.setTotalPrice(getBigDecimal(body, "totalPrice") != null ? getBigDecimal(body, "totalPrice") : BigDecimal.ZERO);
            reservation.setOriginalPrice(getBigDecimal(body, "originalPrice"));
            reservation.setCurrency(getString(body, "currency") != null ? getString(body, "currency") : "CNY");
            reservation.setGuaranteeType(getString(body, "guaranteeType"));
            reservation.setGuaranteeInfo(getString(body, "guaranteeInfo"));
            reservation.setCancellationPolicyCode(getString(body, "cancellationPolicyCode"));
            reservation.setCancellationPolicyDesc(getString(body, "cancellationPolicyDesc"));
            reservation.setGuaranteePolicyCode(getString(body, "guaranteePolicyCode"));
            reservation.setGuaranteePolicyDesc(getString(body, "guaranteePolicyDesc"));
            reservation.setSpecialRequest(getString(body, "specialRequest"));
            reservation.setGuestRemark(getString(body, "guestRemark"));
            reservation.setNotes(getString(body, "notes"));
            reservation.setCommissionRate(getBigDecimal(body, "commissionRate"));
            reservation.setCommissionAmount(getBigDecimal(body, "commissionAmount"));
            reservation.setOrderSource("channel");
            reservation.setCreatedBy("channel:" + channel.getChannelCode());
            reservation.setReservationStatus("confirmed");
            reservation.setPaymentStatus(getString(body, "paymentStatus") != null ? getString(body, "paymentStatus") : "unpaid");

            List<ReservationDailyPrice> dailyPrices = parseDailyPrices(body);
            List<ReservationGuest> guests = parseGuests(body);
            List<ReservationPromotion> promotions = parsePromotions(body);

            Reservation created = reservationService.createReservation(
                    reservation, dailyPrices, guests, promotions);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("reservationId", created.getId());
            data.put("reservationCode", created.getReservationCode());
            data.put("reservationStatus", created.getReservationStatus());
            data.put("hotelCode", hotelCode);
            data.put("roomTypeCode", roomTypeCode);
            data.put("ratePlanCode", ratePlanCode);
            data.put("checkInDate", checkInStr);
            data.put("checkOutDate", checkOutStr);
            data.put("nights", nights);
            data.put("roomCount", roomCount);
            data.put("totalPrice", created.getTotalPrice());
            data.put("currency", created.getCurrency());
            data.put("createdAt", formatDateTime(created.getCreatedAt()));

            return ResponseEntity.ok(ok(data));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(err(400, e.getMessage()));
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

            Reservation cancelled = reservationService.cancelReservation(
                    reservation.getId(), operator, cancelReason);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("reservationCode", cancelled.getReservationCode());
            data.put("reservationStatus", cancelled.getReservationStatus());
            data.put("cancelledAt", formatDateTime(cancelled.getCancelledAt()));

            return ResponseEntity.ok(ok(data));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(err(400, e.getMessage()));
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

    @SuppressWarnings("unchecked")
    private List<ReservationDailyPrice> parseDailyPrices(Map<String, Object> body) {
        Object dpObj = body.get("dailyPrices");
        if (dpObj == null) return Collections.emptyList();
        List<Map<String, Object>> dpList = (List<Map<String, Object>>) dpObj;
        List<ReservationDailyPrice> result = new ArrayList<>();
        for (Map<String, Object> dp : dpList) {
            ReservationDailyPrice rdp = new ReservationDailyPrice();
            rdp.setPriceDate(parseDate(getString(dp, "date")));
            rdp.setOriginalPrice(getBigDecimal(dp, "originalPrice"));
            rdp.setActualPrice(getBigDecimal(dp, "actualPrice") != null ? getBigDecimal(dp, "actualPrice") : BigDecimal.ZERO);
            rdp.setTaxAmount(getBigDecimal(dp, "taxAmount"));
            rdp.setServiceCharge(getBigDecimal(dp, "serviceCharge"));
            rdp.setBreakfastIncluded(getBoolean(dp, "breakfastIncluded"));
            rdp.setBreakfastCount(getInteger(dp, "breakfastCount") != null ? getInteger(dp, "breakfastCount") : 0);
            rdp.setPackagesJson(getString(dp, "packagesJson"));
            result.add(rdp);
        }
        return result;
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

    private Integer getInteger(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        try { return Integer.parseInt(val.toString()); } catch (NumberFormatException e) { return null; }
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return null;
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        try { return new BigDecimal(val.toString()); } catch (NumberFormatException e) { return null; }
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return "true".equalsIgnoreCase(val.toString());
    }
}
