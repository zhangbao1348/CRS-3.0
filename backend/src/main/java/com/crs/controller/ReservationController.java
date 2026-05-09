package com.crs.controller;

import com.crs.entity.*;
import com.crs.repository.TenantChannelRepository;
import com.crs.service.ReservationService;
import com.crs.util.DisplayMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservation")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;
    private final TenantChannelRepository tenantChannelRepo;

    public ReservationController(ReservationService reservationService, TenantChannelRepository tenantChannelRepo) {
        this.reservationService = reservationService;
        this.tenantChannelRepo = tenantChannelRepo;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listReservations(
            @RequestParam(required = false) Integer tenantId,
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String reservationStatus,
            @RequestParam(required = false) Integer channelId,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String checkInStart,
            @RequestParam(required = false) String checkInEnd,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {

        if (tenantId == null) {
            String tid = request.getHeader("X-Tenant-Id");
            if (tid != null && !tid.isBlank()) {
                tenantId = Integer.parseInt(tid);
            }
        }

        if (channelId == null && channelCode != null && !channelCode.isBlank()) {
            List<TenantChannel> channels = tenantChannelRepo.findByTenantIdAndStatusOrderBySortOrderAsc(tenantId, "active");
            for (TenantChannel tc : channels) {
                if (channelCode.equals(tc.getChannelCode())) {
                    channelId = tc.getId();
                    break;
                }
            }
        }

        Date sDate = parseDate(startDate);
        Date eDate = parseDate(endDate);
        Date ciStart = parseDate(checkInStart);
        Date ciEnd = parseDate(checkInEnd);

        Page<Reservation> result = reservationService.listReservations(
                tenantId, hotelId, orderNo, reservationStatus, channelId,
                guestName, sDate, eDate, ciStart, ciEnd, page, pageSize);

        List<Map<String, Object>> items = result.getContent().stream()
                .map(this::toListItem)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", items);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getReservationDetail(@PathVariable Integer id) {
        Optional<Reservation> opt = reservationService.getReservationById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reservation reservation = opt.get();
        List<ReservationDailyPrice> dailyPrices = reservationService.getDailyPrices(id);
        List<ReservationGuest> guests = reservationService.getGuests(id);
        List<ReservationPayment> payments = reservationService.getPayments(id);
        List<ReservationPromotion> promotions = reservationService.getPromotions(id);
        List<ReservationHistory> history = reservationService.getHistory(id);

        Map<String, Object> detail = new LinkedHashMap<>();

        Map<String, Object> orderInfo = new LinkedHashMap<>();
        orderInfo.put("id", reservation.getId());
        orderInfo.put("crsOrderNumber", reservation.getReservationCode());
        orderInfo.put("channelOrderNumber", reservation.getChannelOrderNumber());
        orderInfo.put("pmsNumber", reservation.getPmsNumber());
        orderInfo.put("sourceChannel", reservation.getChannelName() != null ? reservation.getChannelName() : reservation.getChannelCode());
        orderInfo.put("status", DisplayMapper.reservationStatus(reservation.getReservationStatus()));
        orderInfo.put("statusColor", DisplayMapper.statusColor(reservation.getReservationStatus()));
        orderInfo.put("reservationStatus", reservation.getReservationStatus());
        orderInfo.put("createTime", formatDateTime(reservation.getCreatedAt()));
        orderInfo.put("isManual", reservation.getIsManual());
        orderInfo.put("manualReason", reservation.getManualReason());
        detail.put("orderInfo", orderInfo);

        Map<String, Object> hotelInfo = new LinkedHashMap<>();
        hotelInfo.put("hotelName", reservation.getHotelName());
        hotelInfo.put("hotelCode", reservation.getHotelCode());
        hotelInfo.put("roomType", reservation.getRatePlanName() != null ? reservation.getRatePlanName() : reservation.getRatePlanCode());
        hotelInfo.put("roomTypeName", reservation.getRoomTypeName());
        hotelInfo.put("roomTypeCode", reservation.getRoomTypeCode());
        hotelInfo.put("checkInDate", formatDate(reservation.getCheckInDate()));
        hotelInfo.put("checkOutDate", formatDate(reservation.getCheckOutDate()));
        hotelInfo.put("nights", reservation.getNights());
        hotelInfo.put("roomCount", reservation.getRoomCount());
        hotelInfo.put("adultCount", reservation.getAdultCount());
        hotelInfo.put("childCount", reservation.getChildCount());
        detail.put("hotelInfo", hotelInfo);

        Map<String, Object> bookingInfo = new LinkedHashMap<>();
        bookingInfo.put("name", reservation.getContactName());
        bookingInfo.put("phone", reservation.getContactPhone());
        bookingInfo.put("email", reservation.getContactEmail());
        bookingInfo.put("memberLevel", reservation.getMemberLevel());
        bookingInfo.put("memberNumber", reservation.getMemberNo());
        detail.put("bookingInfo", bookingInfo);

        List<Map<String, Object>> guestList = guests.stream().map(g -> {
            Map<String, Object> gMap = new LinkedHashMap<>();
            gMap.put("id", g.getId());
            gMap.put("guestType", g.getGuestType());
            gMap.put("name", g.getName());
            gMap.put("phone", g.getPhone());
            gMap.put("email", g.getEmail());
            gMap.put("idType", DisplayMapper.idType(g.getIdType()));
            gMap.put("idNumber", g.getIdNumber());
            gMap.put("memberLevel", g.getMemberLevel());
            gMap.put("memberNumber", g.getMemberNo());
            gMap.put("roomNumber", g.getRoomNumber());
            gMap.put("pmsAccount", g.getPmsAccount());
            gMap.put("pmsStatus", g.getPmsStatus());
            return gMap;
        }).collect(Collectors.toList());
        detail.put("guestInfo", guestList);

        Map<String, Object> priceInfo = new LinkedHashMap<>();
        priceInfo.put("originalPrice", reservation.getOriginalPrice());
        priceInfo.put("actualPrice", reservation.getTotalPrice());
        priceInfo.put("currency", reservation.getCurrency());

        List<Map<String, Object>> dailyPriceList = dailyPrices.stream().map(dp -> {
            Map<String, Object> dpMap = new LinkedHashMap<>();
            dpMap.put("date", formatDate(dp.getPriceDate()));
            dpMap.put("originalPrice", dp.getOriginalPrice());
            dpMap.put("actualPrice", dp.getActualPrice());
            dpMap.put("taxAmount", dp.getTaxAmount());
            dpMap.put("serviceCharge", dp.getServiceCharge());
            dpMap.put("breakfastIncluded", dp.getBreakfastIncluded());
            dpMap.put("breakfastCount", dp.getBreakfastCount());
            dpMap.put("packagesJson", dp.getPackagesJson());
            return dpMap;
        }).collect(Collectors.toList());
        priceInfo.put("dailyPrices", dailyPriceList);
        detail.put("priceInfo", priceInfo);

        List<Map<String, Object>> promotionList = promotions.stream().map(p -> {
            Map<String, Object> pMap = new LinkedHashMap<>();
            pMap.put("id", p.getId());
            pMap.put("name", p.getPromotionName());
            pMap.put("discountType", DisplayMapper.discountType(p.getDiscountType()));
            pMap.put("discountValue", p.getDiscountValue());
            pMap.put("discountAmount", p.getDiscountAmount());
            pMap.put("promotionCode", p.getPromotionCode());
            pMap.put("provider", DisplayMapper.promotionProvider(p.getProvider()));
            return pMap;
        }).collect(Collectors.toList());
        detail.put("promotionInfo", promotionList);

        Map<String, Object> paymentInfo = new LinkedHashMap<>();
        paymentInfo.put("guaranteeType", DisplayMapper.guaranteeType(reservation.getGuaranteeType()));
        paymentInfo.put("guaranteeTypeCode", reservation.getGuaranteeType());
        paymentInfo.put("guaranteeInfo", reservation.getGuaranteeInfo());
        paymentInfo.put("paymentStatus", DisplayMapper.paymentStatus(reservation.getPaymentStatus()));
        paymentInfo.put("paymentStatusCode", reservation.getPaymentStatus());

        List<Map<String, Object>> paymentList = payments.stream().map(pay -> {
            Map<String, Object> payMap = new LinkedHashMap<>();
            payMap.put("id", pay.getId());
            payMap.put("method", DisplayMapper.paymentMethod(pay.getPaymentMethod()));
            payMap.put("type", pay.getPaymentType());
            payMap.put("amount", pay.getPaymentAmount());
            payMap.put("transactionId", pay.getTransactionId());
            payMap.put("creditCardLast4", pay.getCreditCardLast4());
            payMap.put("creditCardExpiry", pay.getCreditCardExpiry());
            payMap.put("status", DisplayMapper.paymentRecordStatus(pay.getStatus()));
            payMap.put("paidAt", formatDateTime(pay.getPaidAt()));
            return payMap;
        }).collect(Collectors.toList());
        paymentInfo.put("payments", paymentList);
        detail.put("paymentInfo", paymentInfo);

        Map<String, Object> policyInfo = new LinkedHashMap<>();
        policyInfo.put("cancellationPolicyCode", reservation.getCancellationPolicyCode());
        policyInfo.put("cancellationPolicyDesc", reservation.getCancellationPolicyDesc());
        policyInfo.put("guaranteePolicyCode", reservation.getGuaranteePolicyCode());
        policyInfo.put("guaranteePolicyDesc", reservation.getGuaranteePolicyDesc());
        detail.put("policyInfo", policyInfo);

        Map<String, Object> remarkInfo = new LinkedHashMap<>();
        remarkInfo.put("guestRemark", reservation.getGuestRemark());
        remarkInfo.put("hotelRemark", reservation.getHotelRemark());
        remarkInfo.put("specialRequest", reservation.getSpecialRequest());
        remarkInfo.put("notes", reservation.getNotes());
        detail.put("remarkInfo", remarkInfo);

        List<Map<String, Object>> historyList = history.stream().map(h -> {
            Map<String, Object> hMap = new LinkedHashMap<>();
            hMap.put("id", h.getId());
            hMap.put("action", h.getAction());
            hMap.put("content", h.getContent());
            hMap.put("result", h.getResult());
            hMap.put("operator", h.getOperator());
            hMap.put("operatorType", h.getOperatorType());
            hMap.put("operationTime", formatDateTime(h.getOperationTime()));
            hMap.put("detail", h.getDetail());
            return hMap;
        }).collect(Collectors.toList());
        detail.put("operationHistory", historyList);

        return ResponseEntity.ok(detail);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Map<String, Object>> getReservationByCode(@PathVariable String code) {
        Reservation reservation = reservationService.getReservationByCode(code);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        return getReservationDetail(reservation.getId());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(@RequestBody Map<String, Object> body) {
        try {
            Reservation reservation = parseReservationFromBody(body);
            List<ReservationDailyPrice> dailyPrices = parseDailyPricesFromBody(body);
            List<ReservationGuest> guests = parseGuestsFromBody(body);
            List<ReservationPromotion> promotions = parsePromotionsFromBody(body);

            Reservation created = reservationService.createReservation(
                    reservation, dailyPrices, guests, promotions);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", created.getId());
            response.put("reservationCode", created.getReservationCode());
            response.put("reservationStatus", created.getReservationStatus());
            response.put("message", "订单创建成功");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelReservation(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        try {
            String cancelledBy = (String) body.getOrDefault("cancelledBy", "system");
            String cancelReason = (String) body.getOrDefault("cancelReason", "");

            Reservation cancelled = reservationService.cancelReservation(id, cancelledBy, cancelReason);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", cancelled.getId());
            response.put("reservationStatus", cancelled.getReservationStatus());
            response.put("message", "订单取消成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateReservationStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        try {
            String newStatus = (String) body.get("reservationStatus");
            String operator = (String) body.getOrDefault("operator", "system");

            if (newStatus == null || newStatus.isBlank()) {
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("error", "reservationStatus 不能为空");
                return ResponseEntity.badRequest().body(error);
            }

            Reservation updated = reservationService.updateReservationStatus(id, newStatus, operator);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", updated.getId());
            response.put("reservationStatus", updated.getReservationStatus());
            response.put("message", "状态更新成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/manual-intervene")
    public ResponseEntity<Map<String, Object>> manualIntervene(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        try {
            String reason = (String) body.getOrDefault("reason", "");
            String operator = (String) body.getOrDefault("operator", "system");

            Reservation updated = reservationService.manualIntervene(id, reason, operator);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", updated.getId());
            response.put("isManual", updated.getIsManual());
            response.put("message", "人工干预标记成功");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportReservations(
            @RequestParam(required = false) Integer tenantId,
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String reservationStatus,
            @RequestParam(required = false) Integer channelId,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String checkInStart,
            @RequestParam(required = false) String checkInEnd,
            HttpServletRequest request) {

        if (tenantId == null) {
            String tid = request.getHeader("X-Tenant-Id");
            if (tid != null && !tid.isBlank()) {
                tenantId = Integer.parseInt(tid);
            }
        }

        Date sDate = parseDate(startDate);
        Date eDate = parseDate(endDate);
        Date ciStart = parseDate(checkInStart);
        Date ciEnd = parseDate(checkInEnd);

        List<Reservation> reservations = reservationService.listReservationsForExport(
                tenantId, hotelId, orderNo, reservationStatus, channelId,
                guestName, sDate, eDate, ciStart, ciEnd);

        String csvData = reservationService.exportReservationsToCsv(reservations);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reservations.csv")
                .body("\uFEFF" + csvData);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Reservation>> getReservationsByHotelId(@PathVariable Integer hotelId) {
        List<Reservation> reservations = reservationService.getReservationsByHotelId(hotelId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/hotel/{hotelId}/reservation-status/{reservationStatus}")
    public ResponseEntity<List<Reservation>> getReservationsByHotelIdAndReservationStatus(
            @PathVariable Integer hotelId,
            @PathVariable String reservationStatus) {
        List<Reservation> reservations = reservationService.getReservationsByHotelIdAndReservationStatus(
                hotelId, reservationStatus);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/today")
    public ResponseEntity<List<Reservation>> getTodayReservations(@RequestParam Integer hotelId) {
        List<Reservation> reservations = reservationService.getTodayReservations(hotelId);
        return ResponseEntity.ok(reservations);
    }

    private Map<String, Object> toListItem(Reservation r) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", r.getId());
        item.put("channelOrderNumber", r.getChannelOrderNumber());
        item.put("crsOrderNumber", r.getReservationCode());
        item.put("pmsNumber", r.getPmsNumber());
        item.put("status", DisplayMapper.reservationStatus(r.getReservationStatus()));
        item.put("statusColor", DisplayMapper.statusColor(r.getReservationStatus()));
        item.put("reservationStatus", r.getReservationStatus());
        item.put("channel", r.getChannelName() != null ? r.getChannelName() : r.getChannelCode());
        item.put("channelCode", r.getChannelCode());
        item.put("channelIcon", DisplayMapper.channelIcon(r.getChannelCode()));
        item.put("bookingTime", formatDateTime(r.getCreatedAt()));
        item.put("checkInDate", formatDate(r.getCheckInDate()));
        item.put("checkOutDate", formatDate(r.getCheckOutDate()));
        item.put("nights", r.getNights());
        item.put("roomCount", r.getRoomCount());
        item.put("guestName", r.getContactName());
        item.put("totalPrice", r.getTotalPrice());
        item.put("currency", r.getCurrency());
        item.put("hotelName", r.getHotelName());
        item.put("roomTypeName", r.getRoomTypeName());
        item.put("ratePlanName", r.getRatePlanName());
        item.put("isManual", r.getIsManual());
        return item;
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

    @SuppressWarnings("unchecked")
    private Reservation parseReservationFromBody(Map<String, Object> body) {
        Reservation r = new Reservation();
        r.setTenantId(getInteger(body, "tenantId"));
        r.setHotelId(getInteger(body, "hotelId"));
        r.setHotelCode(getString(body, "hotelCode"));
        r.setHotelName(getString(body, "hotelName"));
        r.setRoomTypeId(getInteger(body, "roomTypeId"));
        r.setRoomTypeCode(getString(body, "roomTypeCode"));
        r.setRoomTypeName(getString(body, "roomTypeName"));
        r.setRatePlanId(getInteger(body, "ratePlanId"));
        r.setRatePlanCode(getString(body, "ratePlanCode"));
        r.setRatePlanName(getString(body, "ratePlanName"));
        r.setChannelId(getInteger(body, "channelId"));
        r.setChannelCode(getString(body, "channelCode"));
        r.setChannelName(getString(body, "channelName"));
        r.setChannelOrderNumber(getString(body, "channelOrderNumber"));
        r.setCheckInDate(parseDate(getString(body, "checkInDate")));
        r.setCheckOutDate(parseDate(getString(body, "checkOutDate")));
        r.setRoomCount(getInteger(body, "roomCount") != null ? getInteger(body, "roomCount") : 1);
        r.setAdultCount(getInteger(body, "adultCount") != null ? getInteger(body, "adultCount") : 1);
        r.setChildCount(getInteger(body, "childCount") != null ? getInteger(body, "childCount") : 0);
        r.setContactName(getString(body, "contactName"));
        r.setContactPhone(getString(body, "contactPhone"));
        r.setContactEmail(getString(body, "contactEmail"));
        r.setMemberNo(getString(body, "memberNo"));
        r.setMemberLevel(getString(body, "memberLevel"));
        r.setOriginalPrice(getBigDecimal(body, "originalPrice"));
        r.setTotalPrice(getBigDecimal(body, "totalPrice") != null ? getBigDecimal(body, "totalPrice") : java.math.BigDecimal.ZERO);
        r.setCurrency(getString(body, "currency") != null ? getString(body, "currency") : "CNY");
        r.setGuaranteeType(getString(body, "guaranteeType"));
        r.setGuaranteeInfo(getString(body, "guaranteeInfo"));
        r.setCancellationPolicyCode(getString(body, "cancellationPolicyCode"));
        r.setCancellationPolicyDesc(getString(body, "cancellationPolicyDesc"));
        r.setGuaranteePolicyCode(getString(body, "guaranteePolicyCode"));
        r.setGuaranteePolicyDesc(getString(body, "guaranteePolicyDesc"));
        r.setSpecialRequest(getString(body, "specialRequest"));
        r.setGuestRemark(getString(body, "guestRemark"));
        r.setHotelRemark(getString(body, "hotelRemark"));
        r.setNotes(getString(body, "notes"));
        r.setCommissionRate(getBigDecimal(body, "commissionRate"));
        r.setCommissionAmount(getBigDecimal(body, "commissionAmount"));
        r.setOrderSource(getString(body, "orderSource") != null ? getString(body, "orderSource") : "crs");
        r.setCreatedBy(getString(body, "createdBy") != null ? getString(body, "createdBy") : "system");
        r.setReservationStatus(getString(body, "reservationStatus") != null ? getString(body, "reservationStatus") : "confirmed");
        r.setPaymentStatus(getString(body, "paymentStatus") != null ? getString(body, "paymentStatus") : "unpaid");
        return r;
    }

    @SuppressWarnings("unchecked")
    private List<ReservationDailyPrice> parseDailyPricesFromBody(Map<String, Object> body) {
        Object dpObj = body.get("dailyPrices");
        if (dpObj == null) return Collections.emptyList();
        List<Map<String, Object>> dpList = (List<Map<String, Object>>) dpObj;
        List<ReservationDailyPrice> result = new ArrayList<>();
        for (Map<String, Object> dp : dpList) {
            ReservationDailyPrice rdp = new ReservationDailyPrice();
            rdp.setPriceDate(parseDate(getString(dp, "date")));
            rdp.setOriginalPrice(getBigDecimal(dp, "originalPrice"));
            rdp.setActualPrice(getBigDecimal(dp, "actualPrice") != null ? getBigDecimal(dp, "actualPrice") : java.math.BigDecimal.ZERO);
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
    private List<ReservationGuest> parseGuestsFromBody(Map<String, Object> body) {
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
            rg.setRoomNumber(getString(g, "roomNumber"));
            rg.setSortOrder(sortOrder++);
            result.add(rg);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<ReservationPromotion> parsePromotionsFromBody(Map<String, Object> body) {
        Object pObj = body.get("promotions");
        if (pObj == null) return Collections.emptyList();
        List<Map<String, Object>> pList = (List<Map<String, Object>>) pObj;
        List<ReservationPromotion> result = new ArrayList<>();
        for (Map<String, Object> p : pList) {
            ReservationPromotion rp = new ReservationPromotion();
            rp.setPromotionName(getString(p, "name"));
            rp.setDiscountType(getString(p, "discountType"));
            rp.setDiscountValue(getBigDecimal(p, "discountValue"));
            rp.setDiscountAmount(getBigDecimal(p, "discountAmount") != null ? getBigDecimal(p, "discountAmount") : java.math.BigDecimal.ZERO);
            rp.setPromotionCode(getString(p, "promotionCode"));
            rp.setProvider(getString(p, "provider"));
            result.add(rp);
        }
        return result;
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
        if (val instanceof Number) return java.math.BigDecimal.valueOf(((Number) val).doubleValue());
        try { return new java.math.BigDecimal(val.toString()); } catch (NumberFormatException e) { return null; }
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return "true".equalsIgnoreCase(val.toString());
    }
}
