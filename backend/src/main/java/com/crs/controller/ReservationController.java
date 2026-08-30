package com.crs.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crs.entity.ApiLog;
import com.crs.entity.Reservation;
import com.crs.entity.ReservationDailyPrice;
import com.crs.entity.ReservationGuest;
import com.crs.entity.ReservationHistory;
import com.crs.entity.ReservationPayment;
import com.crs.entity.ReservationPromotion;
import com.crs.entity.User;
import com.crs.repository.ApiLogRepository;
import com.crs.service.ReservationService;
import com.crs.service.UserService;
import com.crs.util.DisplayMapper;

/**
 * ReservationController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【ReservationController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 ReservationController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/reservation")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;
    private final ApiLogRepository apiLogRepository;
    private final UserService userService;
    private final com.crs.repository.PackageRepository packageRepo;
    private final com.crs.repository.ReservationDailyPriceTaxRepository reservationDailyPriceTaxRepo;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    public ReservationController(
            ReservationService reservationService,
            ApiLogRepository apiLogRepository,
            UserService userService,
            com.crs.repository.PackageRepository packageRepo,
            com.crs.repository.ReservationDailyPriceTaxRepository reservationDailyPriceTaxRepo) {
        this.reservationService = reservationService;
        this.apiLogRepository = apiLogRepository;
        this.userService = userService;
        this.packageRepo = packageRepo;
        this.reservationDailyPriceTaxRepo = reservationDailyPriceTaxRepo;
    }

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listReservations(
            @RequestParam(required = false) String hotelCode,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String reservationStatus,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String checkInStart,
            @RequestParam(required = false) String checkInEnd,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        Integer tenantId = getCurrentTenantId();

        Date sDate = parseDate(startDate);
        Date eDate = parseDate(endDate);
        Date ciStart = parseDate(checkInStart);
        Date ciEnd = parseDate(checkInEnd);

        Page<Reservation> result = reservationService.listReservations(
                tenantId, hotelCode, orderNo, reservationStatus, channelCode,
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
        List<ApiLog> apiLogs = apiLogRepository.findByReservationIdOrderByCreatedAtDesc(id);
        Map<Integer, ApiLog> apiLogById = apiLogs.stream()
                .filter(log -> log.getId() != null)
                .collect(Collectors.toMap(ApiLog::getId, log -> log, (existing, ignored) -> existing, LinkedHashMap::new));

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
        orderInfo.put("cancelReason", reservation.getCancelReason());
        orderInfo.put("cancelledBy", reservation.getCancelledBy());
        orderInfo.put("cancelledAt", formatDateTime(reservation.getCancelledAt()));
        detail.put("orderInfo", orderInfo);

        Map<String, Object> hotelInfo = new LinkedHashMap<>();
        hotelInfo.put("hotelName", reservation.getHotelName());
        hotelInfo.put("hotelCode", reservation.getHotelCode());
        hotelInfo.put("roomType", reservation.getRatePlanName() != null ? reservation.getRatePlanName() : reservation.getRatePlanCode());
        hotelInfo.put("roomTypeName", reservation.getRoomTypeName());
        hotelInfo.put("roomTypeCode", reservation.getRoomTypeCode());
        hotelInfo.put("ratePlanName", reservation.getRatePlanName());
        hotelInfo.put("ratePlanCode", reservation.getRatePlanCode());
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

            // 动态关联税费细表回显
            List<com.crs.entity.ReservationDailyPriceTax> taxes = reservationDailyPriceTaxRepo.findByReservationDailyPriceId(dp.getId());
            List<Map<String, Object>> taxesList = taxes.stream().map(t -> {
                Map<String, Object> tMap = new LinkedHashMap<>();
                tMap.put("taxCode", t.getTaxCode());
                tMap.put("taxName", t.getTaxName());
                tMap.put("rateAmount", t.getRateAmount());
                tMap.put("calculatedAmount", t.getCalculatedAmount());
                return tMap;
            }).collect(Collectors.toList());
            dpMap.put("taxes", taxesList);

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
            ApiLog apiLog = h.getLogId() != null ? apiLogById.get(h.getLogId()) : null;
            if (apiLog == null) {
                apiLog = findFallbackApiLog(h, apiLogs);
            }
            Map<String, Object> hMap = new LinkedHashMap<>();
            hMap.put("id", h.getId());
            hMap.put("action", h.getAction());
            hMap.put("content", h.getContent());
            hMap.put("result", h.getResult());
            hMap.put("operator", h.getOperator());
            hMap.put("operatorDisplay", resolveHistoryOperatorDisplay(reservation, h));
            hMap.put("operatorType", h.getOperatorType());
            hMap.put("operationTime", formatDateTime(h.getOperationTime()));
            hMap.put("detail", h.getDetail());
            hMap.put("logId", apiLog != null ? apiLog.getId() : h.getLogId());
            hMap.put("hasApiLog", apiLog != null);
            hMap.put("apiLog", buildApiLogData(apiLog));
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
        String cancelledBy = resolveAuthenticatedOperator();
        if (cancelledBy == null) {
            return unauthorizedResponse("请先登录后再执行取消订单操作");
        }
        try {
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

    private String resolveAuthenticatedOperator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equalsIgnoreCase(authentication.getName())) {
            return null;
        }

        String username = authentication.getName();
        return userService.getUserByUsername(username)
                .map(this::toOperatorName)
                .orElse(username);
    }

    private String toOperatorName(User user) {
        if (user == null) {
            return "system";
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            return user.getName();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> unauthorizedResponse(String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateReservationStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        String operator = resolveAuthenticatedOperator();
        if (operator == null) {
            return unauthorizedResponse("请先登录后再执行状态更新操作");
        }
        try {
            String newStatus = (String) body.get("reservationStatus");

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
        String operator = resolveAuthenticatedOperator();
        if (operator == null) {
            return unauthorizedResponse("请先登录后再执行人工干预操作");
        }
        try {
            String reason = (String) body.getOrDefault("reason", "");
            String targetStatus = (String) body.get("reservationStatus");

            Reservation updated = reservationService.manualIntervene(id, reason, operator, targetStatus);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", updated.getId());
            response.put("isManual", updated.getIsManual());
            response.put("reservationStatus", updated.getReservationStatus());
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
            @RequestParam(required = false) String hotelCode,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String reservationStatus,
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String checkInStart,
            @RequestParam(required = false) String checkInEnd) {

        Integer tenantId = getCurrentTenantId();

        Date sDate = parseDate(startDate);
        Date eDate = parseDate(endDate);
        Date ciStart = parseDate(checkInStart);
        Date ciEnd = parseDate(checkInEnd);

        List<Reservation> reservations = reservationService.listReservationsForExport(
                tenantId, hotelCode, orderNo, reservationStatus, channelCode,
                guestName, sDate, eDate, ciStart, ciEnd);

        String csvData = reservationService.exportReservationsToCsv(reservations);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reservations.csv")
                .body("\uFEFF" + csvData);
    }

    @GetMapping("/hotel/{hotelCode}")
    public ResponseEntity<List<Reservation>> getReservationsByHotelCode(@PathVariable String hotelCode) {
        List<Reservation> reservations = reservationService.getReservationsByHotelCode(hotelCode);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/hotel/{hotelCode}/reservation-status/{reservationStatus}")
    public ResponseEntity<List<Reservation>> getReservationsByHotelCodeAndReservationStatus(
            @PathVariable String hotelCode,
            @PathVariable String reservationStatus) {
        List<Reservation> reservations = reservationService.getReservationsByHotelCodeAndReservationStatus(
                hotelCode, reservationStatus);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/today")
    public ResponseEntity<List<Reservation>> getTodayReservations(@RequestParam String hotelCode) {
        Integer tenantId = getCurrentTenantId();
        List<Reservation> reservations = reservationService.getTodayReservations(tenantId, hotelCode);
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

    private ApiLog findFallbackApiLog(ReservationHistory history, List<ApiLog> apiLogs) {
        if (history == null || apiLogs == null || apiLogs.isEmpty()) {
            return null;
        }
        return apiLogs.stream()
                .filter(log -> matchesHistoryAction(history, log))
                .findFirst()
                .orElse(null);
    }

    private boolean matchesHistoryAction(ReservationHistory history, ApiLog apiLog) {
        if (history == null || apiLog == null) {
            return false;
        }
        String requestBody = apiLog.getRequestBody();
        if (requestBody == null || requestBody.isBlank()) {
            return false;
        }
        String action = history.getAction();
        if ("CREATE".equalsIgnoreCase(action)) {
            return requestBody.contains("/api/open/reservations")
                    && !requestBody.contains("/cancel")
                    && apiLog.getResponseBody() != null
                    && apiLog.getResponseBody().contains("\"reservationId\"");
        }
        if ("CANCEL".equalsIgnoreCase(action)) {
            return requestBody.contains("/api/open/reservations/")
                    && requestBody.contains("/cancel");
        }
        return false;
    }

    private Map<String, Object> buildApiLogData(ApiLog apiLog) {
        if (apiLog == null) {
            return null;
        }
        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("id", apiLog.getId());
        logData.put("requestBody", apiLog.getRequestBody());
        logData.put("responseBody", apiLog.getResponseBody());
        logData.put("errorMessage", apiLog.getErrorMessage());
        logData.put("createdAt", formatDateTime(apiLog.getCreatedAt()));
        return logData;
    }

    private String resolveHistoryOperatorDisplay(Reservation reservation, ReservationHistory history) {
        if (history == null || history.getOperator() == null || history.getOperator().isBlank()) {
            return "-";
        }

        String operator = history.getOperator();
        String operatorType = history.getOperatorType();

        if ("channel".equalsIgnoreCase(operatorType)) {
            String channelName = reservation != null
                    ? (reservation.getChannelName() != null && !reservation.getChannelName().isBlank()
                            ? reservation.getChannelName()
                            : reservation.getChannelCode())
                    : null;
            if ((channelName == null || channelName.isBlank()) && operator.startsWith("channel:")) {
                channelName = operator.substring("channel:".length());
            }
            return "渠道：" + (channelName != null && !channelName.isBlank() ? channelName : operator);
        }

        if ("system".equalsIgnoreCase(operatorType)) {
            if ("system:payment-timeout".equalsIgnoreCase(operator)) {
                return "系统任务：支付超时自动取消";
            }
            if (operator.startsWith("system:")) {
                return "系统任务：" + operator.substring("system:".length());
            }
            return "系统";
        }

        return operator;
    }

    @SuppressWarnings("unchecked")
    private Reservation parseReservationFromBody(Map<String, Object> body) {
        Reservation r = new Reservation();
        r.setTenantId(com.crs.util.TenantContext.getTenantId());
        r.setHotelCode(getString(body, "hotelCode"));
        r.setHotelName(getString(body, "hotelName"));
        r.setRoomTypeCode(getString(body, "roomTypeCode"));
        r.setRoomTypeName(getString(body, "roomTypeName"));
        r.setRatePlanCode(getString(body, "ratePlanCode"));
        r.setRatePlanName(getString(body, "ratePlanName"));
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
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        for (Map<String, Object> dp : dpList) {
            ReservationDailyPrice rdp = new ReservationDailyPrice();
            rdp.setPriceDate(parseDate(getString(dp, "date")));
            rdp.setOriginalPrice(getBigDecimal(dp, "originalPrice"));
            rdp.setActualPrice(getBigDecimal(dp, "actualPrice") != null ? getBigDecimal(dp, "actualPrice") : java.math.BigDecimal.ZERO);
            rdp.setTaxAmount(getBigDecimal(dp, "taxAmount"));
            rdp.setServiceCharge(getBigDecimal(dp, "serviceCharge"));
            rdp.setBreakfastIncluded(getBoolean(dp, "breakfastIncluded"));
            rdp.setBreakfastCount(getInteger(dp, "breakfastCount") != null ? getInteger(dp, "breakfastCount") : 0);
            rdp.setPackagesJson(resolvePackagePriceDetails(tenantId, getString(dp, "packagesJson")));
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

    private String resolvePackagePriceDetails(Integer tenantId, String packagesJson) {
        if (packagesJson == null || packagesJson.isBlank() || packagesJson.equals("null") || packagesJson.equals("[]")) {
            return "[]";
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
                return "[]";
            }

            List<com.crs.entity.Package> activePackages = packageRepo
                    .findByTenantIdAndCodeInAndStatus(tenantId, packageCodes, com.crs.entity.Package.Status.active);

            List<Map<String, Object>> snapshotList = new ArrayList<>();

            for (com.crs.entity.Package ap : activePackages) {
                int qty = 1;
                for (Object item : rawItems) {
                    if (item instanceof Map<?, ?> rawMap) {
                        String code = Objects.toString(rawMap.get("code"), Objects.toString(rawMap.get("packageCode"), null));
                        if (ap.getCode().equalsIgnoreCase(code)) {
                            Object qVal = rawMap.get("quantity");
                            if (qVal instanceof Number num) {
                                qty = num.intValue();
                            } else if (qVal != null) {
                                try { qty = Integer.parseInt(qVal.toString()); } catch (Exception ignored) {}
                            }
                        }
                    }
                }

                BigDecimal price = ap.getFixedPrice() != null ? BigDecimal.valueOf(ap.getFixedPrice()) : BigDecimal.ZERO;
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
            }

            return objectMapper.writeValueAsString(snapshotList);
        } catch (Exception e) {
            return packagesJson;
        }
    }
}
