package com.crs.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crs.entity.Reservation;
import com.crs.entity.ReservationDailyPrice;
import com.crs.entity.ReservationGuest;
import com.crs.entity.ReservationHistory;
import com.crs.entity.ReservationPayment;
import com.crs.entity.ReservationPromotion;
import com.crs.modules.reservation.domain.ReservationStateMachine;
import com.crs.repository.ReservationDailyPriceRepository;
import com.crs.repository.ReservationGuestRepository;
import com.crs.repository.ReservationHistoryRepository;
import com.crs.repository.ReservationPaymentRepository;
import com.crs.repository.ReservationPromotionRepository;
import com.crs.repository.ReservationRepository;
import com.crs.service.inventory.InventoryDeductionContext;
import com.crs.service.inventory.InventoryDeductionService;
import com.crs.service.inventory.InventoryReleaseContext;
import com.crs.util.DisplayMapper;

/**
 * ReservationService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【ReservationService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 ReservationService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class ReservationService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final ReservationRepository reservationRepository;
    private final ReservationDailyPriceRepository dailyPriceRepository;
    private final ReservationGuestRepository guestRepository;
    private final ReservationPaymentRepository paymentRepository;
    private final ReservationPromotionRepository promotionRepository;
    private final ReservationHistoryRepository historyRepository;
    private final InventoryDeductionService inventoryDeductionService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationDailyPriceRepository dailyPriceRepository,
            ReservationGuestRepository guestRepository,
            ReservationPaymentRepository paymentRepository,
            ReservationPromotionRepository promotionRepository,
            ReservationHistoryRepository historyRepository,
            InventoryDeductionService inventoryDeductionService) {
        this.reservationRepository = reservationRepository;
        this.dailyPriceRepository = dailyPriceRepository;
        this.guestRepository = guestRepository;
        this.paymentRepository = paymentRepository;
        this.promotionRepository = promotionRepository;
        this.historyRepository = historyRepository;
        this.inventoryDeductionService = inventoryDeductionService;
    }

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    public Page<Reservation> listReservations(Integer tenantId, String hotelCode, String orderNo,
                                               String reservationStatus, String channelCode,
                                               String guestName, Date startDate, Date endDate,
                                               Date checkInStart, Date checkInEnd,
                                               int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return reservationRepository.findWithFiltersByCode(getCurrentTenantId(), hotelCode, orderNo,
                reservationStatus, channelCode, guestName, startDate, endDate,
                checkInStart, checkInEnd, pageable);
    }

    public List<Reservation> listReservationsForExport(Integer tenantId, String hotelCode, String orderNo,
                                                       String reservationStatus, String channelCode,
                                                       String guestName, Date startDate, Date endDate,
                                                       Date checkInStart, Date checkInEnd) {
        return reservationRepository.findListWithFiltersByCode(getCurrentTenantId(), hotelCode, orderNo,
                reservationStatus, channelCode, guestName, startDate, endDate,
                checkInStart, checkInEnd);
    }

    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findByIdAndTenantId(id, getCurrentTenantId());
    }

    public Reservation getReservationByCode(String reservationCode) {
        return reservationRepository.findByTenantIdAndReservationCode(getCurrentTenantId(), reservationCode).orElse(null);
    }

    public List<ReservationDailyPrice> getDailyPrices(Integer reservationId) {
        // 先验证订单所有权
        if (getReservationById(reservationId).isEmpty()) {
            return Collections.emptyList();
        }
        return dailyPriceRepository.findByReservationIdOrderByPriceDateAsc(reservationId);
    }

    public List<ReservationGuest> getGuests(Integer reservationId) {
        if (getReservationById(reservationId).isEmpty()) {
            return Collections.emptyList();
        }
        return guestRepository.findByReservationIdOrderBySortOrderAsc(reservationId);
    }

    public List<ReservationPayment> getPayments(Integer reservationId) {
        if (getReservationById(reservationId).isEmpty()) {
            return Collections.emptyList();
        }
        return paymentRepository.findByReservationIdOrderByCreatedAtDesc(reservationId);
    }

    public List<ReservationPromotion> getPromotions(Integer reservationId) {
        if (getReservationById(reservationId).isEmpty()) {
            return Collections.emptyList();
        }
        return promotionRepository.findByReservationId(reservationId);
    }

    public List<ReservationHistory> getHistory(Integer reservationId) {
        if (getReservationById(reservationId).isEmpty()) {
            return Collections.emptyList();
        }
        return historyRepository.findByReservationIdOrderByOperationTimeDesc(reservationId);
    }

    @Transactional
    public Reservation createReservation(Reservation reservation,
                                          List<ReservationDailyPrice> dailyPrices,
                                          List<ReservationGuest> guests,
                                          List<ReservationPromotion> promotions) {
        // 强制设置当前租户ID，防止入参篡改
        reservation.setTenantId(getCurrentTenantId());
        
        if (reservation.getReservationCode() == null || reservation.getReservationCode().isBlank()) {
            reservation.setReservationCode(generateReservationCode());
        }
        if (reservation.getNights() == null && reservation.getCheckInDate() != null && reservation.getCheckOutDate() != null) {
            long nights = (reservation.getCheckOutDate().getTime() - reservation.getCheckInDate().getTime()) / (1000 * 60 * 60 * 24);
            reservation.setNights((int) nights);
        }

        checkAndReserveInventory(reservation);

        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.publishEvent(new ReservationChangedEvent(this, saved, null, saved.getReservationStatus()));

        if (dailyPrices != null && !dailyPrices.isEmpty()) {
            dailyPrices.forEach(dp -> dp.setReservationId(saved.getId()));
            dailyPriceRepository.saveAll(dailyPrices);
        }
        if (guests != null && !guests.isEmpty()) {
            guests.forEach(g -> g.setReservationId(saved.getId()));
            guestRepository.saveAll(guests);
        }
        if (promotions != null && !promotions.isEmpty()) {
            promotions.forEach(p -> p.setReservationId(saved.getId()));
            promotionRepository.saveAll(promotions);
        }

        ReservationHistory history = new ReservationHistory();
        history.setReservationId(saved.getId());
        history.setAction("CREATE");
        history.setContent("创建订单");
        history.setResult("success");
        history.setOperator(saved.getCreatedBy());
        history.setOperatorType("channel".equals(saved.getOrderSource()) ? "channel" : "crs");
        historyRepository.save(history);

        return saved;
    }

    @Transactional
    public Reservation cancelReservation(Integer id, String cancelledBy, String cancelReason) {
        Reservation reservation = reservationRepository.findByIdAndTenantIdForUpdate(id, getCurrentTenantId())
                .orElseThrow(() -> new RuntimeException("订单不存在或无权访问"));

        return cancelReservationInternal(reservation, cancelledBy, cancelReason, "crs");
    }

    @Transactional
    public Reservation cancelReservationBySystem(Integer id, String cancelledBy, String cancelReason) {
        // 使用悲观写锁锁定该订单，确保后续读写数据具有最高并发原子安全性
        Reservation reservation = reservationRepository.findByIdAndTenantIdForUpdate(id, getCurrentTenantId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // [TRACE] 记录悲观锁获取与取消判定决策快照
        Map<String, Object> cancelSnapshot = new java.util.LinkedHashMap<>();
        cancelSnapshot.put("reservationId", id);
        cancelSnapshot.put("reservationCode", reservation.getReservationCode());
        cancelSnapshot.put("reservationStatus", reservation.getReservationStatus());
        cancelSnapshot.put("paymentStatus", reservation.getPaymentStatus());
        cancelSnapshot.put("paymentDeadline", reservation.getPaymentDeadline());
        com.crs.util.TraceContext.recordDecision("reservationCode", reservation.getReservationCode());
        com.crs.util.TraceContext.recordDecision("cancelBySystemDecision", cancelSnapshot);

        if (!"pending_payment".equals(reservation.getReservationStatus())) {
            throw new RuntimeException("仅待支付订单允许执行支付超时自动取消");
        }
        if (!"unpaid".equals(reservation.getPaymentStatus())) {
            throw new RuntimeException("订单已支付或支付状态已变更，不能执行支付超时自动取消");
        }
        if (reservation.getPaymentDeadline() == null || reservation.getPaymentDeadline().after(new Date())) {
            throw new RuntimeException("订单尚未到达支付超时时间");
        }

        return cancelReservationInternal(reservation, cancelledBy, cancelReason, "system");
    }

    /**
     * 对预订订单进行支付回调核销，具备高并发行锁保护及防重幂等机制。
     * 关联PRD文档：.kiro/specs/prd/14-订单管理.md
     */
    @Transactional
    public Reservation payReservation(String reservationCode, String paymentMethod, java.math.BigDecimal paymentAmount, String transactionId, String operator) {
        // 1. 使用悲观写锁获取该订单，防止与超时任务或人工干预并发抢占导致的状态覆盖
        Reservation reservation = reservationRepository.findByTenantIdAndReservationCodeForUpdate(getCurrentTenantId(), reservationCode)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        String oldStatus = reservation.getReservationStatus();

        // [TRACE] 记录支付核销前决策快照
        Map<String, Object> paySnapshot = new java.util.LinkedHashMap<>();
        paySnapshot.put("reservationCode", reservationCode);
        paySnapshot.put("inputPaymentAmount", paymentAmount);
        paySnapshot.put("transactionId", transactionId);
        paySnapshot.put("orderTotalPrice", reservation.getTotalPrice());
        paySnapshot.put("orderReservationStatus", reservation.getReservationStatus());
        paySnapshot.put("orderPaymentStatus", reservation.getPaymentStatus());
        paySnapshot.put("paymentDeadline", reservation.getPaymentDeadline());
        com.crs.util.TraceContext.recordDecision("reservationCode", reservationCode);
        com.crs.util.TraceContext.recordDecision("payReservationDecision", paySnapshot);

        // 2. 状态拦截判断 (订单一旦被系统自动超时取消或渠道主动取消，不可再执行支付)
        if ("cancelled".equals(reservation.getReservationStatus())) {
            throw new RuntimeException("ORDER_ALREADY_CANCELLED");
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new RuntimeException("支付交易流水号不能为空");
        }

        // 3. 幂等性校验 (订单已支付时，如果交易流水号一致，属于重复回调，作幂等返回)
        if ("paid".equals(reservation.getPaymentStatus())) {
            List<ReservationPayment> existingPayments = paymentRepository.findByReservationIdOrderByCreatedAtDesc(reservation.getId());
            boolean sameTransaction = existingPayments.stream()
                    .anyMatch(p -> transactionId != null && transactionId.equals(p.getTransactionId()));
            if (sameTransaction) {
                return reservation;
            }
            throw new RuntimeException("订单已支付，请勿重复支付");
        }

        // 4. 对账金额严格校验
        if (paymentAmount == null || reservation.getTotalPrice() == null 
                || paymentAmount.subtract(reservation.getTotalPrice()).abs().compareTo(new java.math.BigDecimal("0.01")) > 0) {
            throw new RuntimeException("支付金额与订单总价不符，订单总价: " + reservation.getTotalPrice() + ", 传入支付金额: " + paymentAmount);
        }

        // 5. 校验支付流水是否存在 (防止在未支付状态下因网络抖动产生的重复入账)
        List<ReservationPayment> checkTxPayments = paymentRepository.findByReservationIdOrderByCreatedAtDesc(reservation.getId());
        boolean duplicateTx = checkTxPayments.stream().anyMatch(p -> transactionId != null && transactionId.equals(p.getTransactionId()));
        if (duplicateTx) {
            return reservation;
        }

        // 6. 保存支付流水明细
        ReservationPayment payment = new ReservationPayment();
        payment.setTenantId(reservation.getTenantId());
        payment.setReservationId(reservation.getId());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentType("payment");
        payment.setPaymentAmount(paymentAmount);
        payment.setTransactionId(transactionId);
        payment.setStatus("success");
        payment.setPaidAt(new Date());
        paymentRepository.save(payment);

        // 7. 更新订单状态与清空 Deadline (使订单彻底跳出超时扫描器)
        if ("pending_payment".equals(reservation.getReservationStatus())) {
            reservation.setReservationStatus("confirmed");
        }
        reservation.setPaymentStatus("paid");
        reservation.setPaymentDeadline(null);
        reservation.setModifiedBy(operator);

        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.publishEvent(new ReservationChangedEvent(this, saved, oldStatus, saved.getReservationStatus()));

        // 8. 归档操作历史
        ReservationHistory history = new ReservationHistory();
        history.setReservationId(saved.getId());
        history.setAction("PAY");
        history.setContent("支付成功，流水号：" + transactionId);
        history.setResult("success");
        history.setOperator(operator);
        history.setOperatorType("channel");
        historyRepository.save(history);

        return saved;
    }

    private Reservation cancelReservationInternal(Reservation reservation, String cancelledBy, String cancelReason, String operatorType) {
        if (reservation == null) {
            throw new RuntimeException("订单不存在");
        }

        // Cancel is idempotent: an already-cancelled reservation returns success without side effects.
        if ("cancelled".equals(reservation.getReservationStatus())) {
            return reservation;
        }

        if (cancelledBy == null || cancelledBy.isBlank()) {
            throw new RuntimeException("取消操作人不能为空");
        }
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new RuntimeException("取消原因不能为空");
        }
        if (!"confirmed".equals(reservation.getReservationStatus())
                && !"pending".equals(reservation.getReservationStatus())
                && !"pending_payment".equals(reservation.getReservationStatus())) {
            throw new RuntimeException("当前状态不允许取消，仅 confirmed/pending/pending_payment 状态可取消");
        }

        if ("paid".equals(reservation.getPaymentStatus())) {
            createAutomaticRefund(reservation, cancelledBy);
            reservation.setPaymentStatus("refunded");
        }

        try {
            releaseInventory(reservation);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(ReservationService.class)
                    .error("返还库存失败: reservationCode={}, error={}", reservation.getReservationCode(), e.getMessage(), e);
            throw new RuntimeException("返还库存失败: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()), e);
        }

        String oldStatus = reservation.getReservationStatus();
        reservation.setReservationStatus("cancelled");
        reservation.setStatus(Reservation.Status.cancelled);
        reservation.setCancelledBy(cancelledBy);
        reservation.setCancelledAt(new Date());
        reservation.setCancelReason(cancelReason);
        reservation.setModifiedBy(cancelledBy);

        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.publishEvent(new ReservationChangedEvent(this, saved, oldStatus, "cancelled"));

        ReservationHistory history = new ReservationHistory();
        history.setReservationId(saved.getId());
        history.setAction("CANCEL");
        history.setContent("取消订单：" + (cancelReason != null ? cancelReason : ""));
        history.setResult("success");
        history.setOperator(cancelledBy);
        history.setOperatorType(operatorType);
        historyRepository.save(history);

        return saved;
    }

    /**
     * 已支付订单取消时生成全额退款流水，保证订单、支付状态与库存释放在同一事务内提交。
     * 关联模块：订单取消、支付流水、库存释放。
     */
    private void createAutomaticRefund(Reservation reservation, String operator) {
        List<ReservationPayment> payments = paymentRepository
                .findByReservationIdOrderByCreatedAtDesc(reservation.getId());
        String paymentMethod = payments.stream()
                .filter(payment -> "payment".equals(payment.getPaymentType()) && "success".equals(payment.getStatus()))
                .map(ReservationPayment::getPaymentMethod)
                .filter(method -> method != null && !method.isBlank())
                .findFirst()
                .orElse("original_payment");

        ReservationPayment refund = new ReservationPayment();
        refund.setTenantId(reservation.getTenantId());
        refund.setReservationId(reservation.getId());
        refund.setPaymentMethod(paymentMethod);
        refund.setPaymentType("refund");
        refund.setPaymentAmount(reservation.getTotalPrice() != null
                ? reservation.getTotalPrice()
                : java.math.BigDecimal.ZERO);
        refund.setTransactionId("REFUND-" + reservation.getReservationCode() + "-" + System.currentTimeMillis());
        refund.setStatus("refunded");
        refund.setPaidAt(new Date());
        paymentRepository.save(refund);

        ReservationHistory refundHistory = new ReservationHistory();
        refundHistory.setReservationId(reservation.getId());
        refundHistory.setAction("REFUND");
        refundHistory.setContent("订单取消自动退款：" + refund.getPaymentAmount() + " " + reservation.getCurrency());
        refundHistory.setResult("success");
        refundHistory.setOperator(operator);
        refundHistory.setOperatorType("crs");
        historyRepository.save(refundHistory);
    }

    @Transactional
    public Reservation updateReservationStatus(Integer id, String newStatus, String operator) {
        Reservation reservation = reservationRepository.findByIdAndTenantIdForUpdate(id, getCurrentTenantId())
                .orElseThrow(() -> new RuntimeException("订单不存在或无权访问"));

        String oldStatus = reservation.getReservationStatus();
        if ("cancelled".equals(newStatus)) {
            throw new RuntimeException("请使用取消订单接口并填写取消原因");
        }
        ReservationStateMachine.requireTransition(oldStatus, newStatus);

        reservation.setReservationStatus(newStatus);
        reservation.setModifiedBy(operator);

        if ("checked_out".equals(newStatus)) {
            reservation.setStatus(Reservation.Status.completed);
            reservation.setCompletedAt(new Date());
        }

        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.publishEvent(new ReservationChangedEvent(this, saved, oldStatus, newStatus));

        String action = DisplayMapper.historyAction(newStatus);
        String content = DisplayMapper.historyContent(newStatus);
        if ("STATUS_CHANGE".equals(action)) {
            content = "状态变更：" + oldStatus + " → " + newStatus;
        }

        ReservationHistory history = new ReservationHistory();
        history.setReservationId(saved.getId());
        history.setAction(action);
        history.setContent(content);
        history.setResult("success");
        history.setOperator(operator);
        history.setOperatorType("crs");
        historyRepository.save(history);

        return saved;
    }

    @Transactional
    public Reservation manualIntervene(Integer id, String reason, String operator, String targetStatus) {
        Reservation reservation = reservationRepository.findByIdAndTenantIdForUpdate(id, getCurrentTenantId())
                .orElseThrow(() -> new RuntimeException("订单不存在或无权访问"));

        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("人工干预原因不能为空");
        }

        String oldStatus = reservation.getReservationStatus();
        if (targetStatus != null && !targetStatus.isBlank() && !targetStatus.equals(oldStatus)) {
            if (!java.util.Set.of("pending", "pending_payment", "confirmed", "checked_in", "checked_out", "no_show")
                    .contains(targetStatus)) {
                throw new RuntimeException("人工干预目标状态不合法");
            }
            reservation.setReservationStatus(targetStatus);
            if ("checked_out".equals(targetStatus)) {
                reservation.setStatus(Reservation.Status.completed);
                reservation.setCompletedAt(new Date());
            } else {
                reservation.setStatus(Reservation.Status.active);
                reservation.setCompletedAt(null);
            }
        }

        reservation.setIsManual(true);
        reservation.setManualReason(reason);
        reservation.setModifiedBy(operator);

        Reservation saved = reservationRepository.save(reservation);

        ReservationHistory history = new ReservationHistory();
        history.setReservationId(saved.getId());
        history.setAction("MANUAL_INTERVENE");
        history.setContent("人工干预：" + reason
                + (targetStatus != null && !targetStatus.isBlank() && !targetStatus.equals(oldStatus)
                        ? "；强制状态变更：" + oldStatus + " → " + targetStatus
                        : ""));
        history.setResult("success");
        history.setOperator(operator);
        history.setOperatorType("crs");
        historyRepository.save(history);

        return saved;
    }

    private void checkAndReserveInventory(Reservation reservation) {
        InventoryDeductionContext ctx = buildDeductionContext(reservation);
        inventoryDeductionService.deductInventory(ctx);
    }

    private void releaseInventory(Reservation reservation) {
        InventoryReleaseContext ctx = buildReleaseContext(reservation);
        inventoryDeductionService.releaseInventory(ctx);
    }

    private LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private InventoryDeductionContext buildDeductionContext(Reservation reservation) {
        InventoryDeductionContext ctx = new InventoryDeductionContext();
        ctx.setTenantId(reservation.getTenantId());
        ctx.setHotelCode(reservation.getHotelCode());
        ctx.setRoomTypeCode(reservation.getRoomTypeCode());
        ctx.setRateCode(reservation.getRatePlanCode());
        ctx.setChannelCode(reservation.getChannelCode());
        ctx.setCheckInDate(toLocalDate(reservation.getCheckInDate()));
        ctx.setCheckOutDate(toLocalDate(reservation.getCheckOutDate()));
        ctx.setRoomCount(reservation.getRoomCount());
        ctx.setReservationCode(reservation.getReservationCode());
        return ctx;
    }

    private InventoryReleaseContext buildReleaseContext(Reservation reservation) {
        InventoryReleaseContext ctx = new InventoryReleaseContext();
        ctx.setTenantId(reservation.getTenantId());
        ctx.setHotelCode(reservation.getHotelCode());
        ctx.setRoomTypeCode(reservation.getRoomTypeCode());
        ctx.setRateCode(reservation.getRatePlanCode());
        ctx.setChannelCode(reservation.getChannelCode());
        ctx.setCheckInDate(toLocalDate(reservation.getCheckInDate()));
        ctx.setCheckOutDate(toLocalDate(reservation.getCheckOutDate()));
        ctx.setRoomCount(reservation.getRoomCount());
        ctx.setReservationCode(reservation.getReservationCode());
        return ctx;
    }

    private String generateReservationCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        String seq = String.format("%06d", new Random().nextInt(999999) + 1);
        return "CRS" + datePart + seq;
    }

    public String exportReservationsToCsv(List<Reservation> reservations) {
        StringBuilder csv = new StringBuilder();
        csv.append("CRS订单号,渠道订单号,状态,渠道,预订时间,入住日期,离店日期,晚数,房间数,总价,酒店,房型,价格计划,联系人,联系电话,成人,儿童\n");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        for (Reservation r : reservations) {
            csv.append(r.getReservationCode()).append(",");
            csv.append(r.getChannelOrderNumber() != null ? r.getChannelOrderNumber() : "").append(",");
            csv.append(r.getReservationStatus()).append(",");
            csv.append(r.getChannelName() != null ? r.getChannelName() : r.getChannelCode()).append(",");
            csv.append(r.getCreatedAt() != null ? sdf.format(r.getCreatedAt()) : "").append(",");
            csv.append(r.getCheckInDate() != null ? df.format(r.getCheckInDate()) : "").append(",");
            csv.append(r.getCheckOutDate() != null ? df.format(r.getCheckOutDate()) : "").append(",");
            csv.append(r.getNights() != null ? r.getNights() : "").append(",");
            csv.append(r.getRoomCount()).append(",");
            csv.append(r.getTotalPrice()).append(",");
            csv.append(r.getHotelName() != null ? r.getHotelName() : "").append(",");
            csv.append(r.getRoomTypeName() != null ? r.getRoomTypeName() : "").append(",");
            csv.append(r.getRatePlanName() != null ? r.getRatePlanName() : "").append(",");
            csv.append(r.getContactName() != null ? r.getContactName() : "").append(",");
            csv.append(r.getContactPhone() != null ? r.getContactPhone() : "").append(",");
            csv.append(r.getAdultCount()).append(",");
            csv.append(r.getChildCount()).append("\n");
        }
        return csv.toString();
    }

    public List<Reservation> getReservationsByHotelCode(String hotelCode) {
        return reservationRepository.findByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }

    public List<Reservation> getReservationsByHotelCodeAndReservationStatus(String hotelCode, String reservationStatus) {
        return reservationRepository.findByTenantIdAndHotelCodeAndReservationStatus(getCurrentTenantId(), hotelCode, reservationStatus);
    }

    public List<Reservation> getTodayReservations(Integer tenantId, String hotelCode) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.DATE, 1);
        Date tomorrow = cal.getTime();
        return reservationRepository.findByTenantIdAndHotelCodeAndCreatedAtBetween(getCurrentTenantId(), hotelCode, today, tomorrow);
    }
}
