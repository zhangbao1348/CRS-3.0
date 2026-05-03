package com.crs.service;

import com.crs.entity.*;
import com.crs.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationDailyPriceRepository dailyPriceRepository;
    private final ReservationGuestRepository guestRepository;
    private final ReservationPaymentRepository paymentRepository;
    private final ReservationPromotionRepository promotionRepository;
    private final ReservationHistoryRepository historyRepository;
    private final InventoryService inventoryService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationDailyPriceRepository dailyPriceRepository,
            ReservationGuestRepository guestRepository,
            ReservationPaymentRepository paymentRepository,
            ReservationPromotionRepository promotionRepository,
            ReservationHistoryRepository historyRepository,
            InventoryService inventoryService) {
        this.reservationRepository = reservationRepository;
        this.dailyPriceRepository = dailyPriceRepository;
        this.guestRepository = guestRepository;
        this.paymentRepository = paymentRepository;
        this.promotionRepository = promotionRepository;
        this.historyRepository = historyRepository;
        this.inventoryService = inventoryService;
    }

    public Page<Reservation> listReservations(Integer tenantId, Integer hotelId, String orderNo,
                                               String reservationStatus, Integer channelId,
                                               String guestName, Date startDate, Date endDate,
                                               Date checkInStart, Date checkInEnd,
                                               int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return reservationRepository.findWithFilters(tenantId, hotelId, orderNo,
                reservationStatus, channelId, guestName, startDate, endDate,
                checkInStart, checkInEnd, pageable);
    }

    public List<Reservation> listReservationsForExport(Integer tenantId, Integer hotelId, String orderNo,
                                                       String reservationStatus, Integer channelId,
                                                       String guestName, Date startDate, Date endDate,
                                                       Date checkInStart, Date checkInEnd) {
        return reservationRepository.findListWithFilters(tenantId, hotelId, orderNo,
                reservationStatus, channelId, guestName, startDate, endDate,
                checkInStart, checkInEnd);
    }

    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findById(id);
    }

    public Reservation getReservationByCode(String reservationCode) {
        return reservationRepository.findByReservationCode(reservationCode);
    }

    public List<ReservationDailyPrice> getDailyPrices(Integer reservationId) {
        return dailyPriceRepository.findByReservationIdOrderByPriceDateAsc(reservationId);
    }

    public List<ReservationGuest> getGuests(Integer reservationId) {
        return guestRepository.findByReservationIdOrderBySortOrderAsc(reservationId);
    }

    public List<ReservationPayment> getPayments(Integer reservationId) {
        return paymentRepository.findByReservationIdOrderByCreatedAtDesc(reservationId);
    }

    public List<ReservationPromotion> getPromotions(Integer reservationId) {
        return promotionRepository.findByReservationId(reservationId);
    }

    public List<ReservationHistory> getHistory(Integer reservationId) {
        return historyRepository.findByReservationIdOrderByOperationTimeDesc(reservationId);
    }

    @Transactional
    public Reservation createReservation(Reservation reservation,
                                          List<ReservationDailyPrice> dailyPrices,
                                          List<ReservationGuest> guests,
                                          List<ReservationPromotion> promotions) {
        if (reservation.getReservationCode() == null || reservation.getReservationCode().isBlank()) {
            reservation.setReservationCode(generateReservationCode());
        }
        if (reservation.getNights() == null && reservation.getCheckInDate() != null && reservation.getCheckOutDate() != null) {
            long nights = (reservation.getCheckOutDate().getTime() - reservation.getCheckInDate().getTime()) / (1000 * 60 * 60 * 24);
            reservation.setNights((int) nights);
        }

        checkAndReserveInventory(reservation);

        Reservation saved = reservationRepository.save(reservation);

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
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"confirmed".equals(reservation.getReservationStatus())
                && !"pending".equals(reservation.getReservationStatus())) {
            throw new RuntimeException("当前状态不允许取消，仅 confirmed/pending 状态可取消");
        }

        releaseInventory(reservation);

        reservation.setReservationStatus("cancelled");
        reservation.setStatus(Reservation.Status.cancelled);
        reservation.setCancelledBy(cancelledBy);
        reservation.setCancelledAt(new Date());
        reservation.setCancelReason(cancelReason);
        reservation.setModifiedBy(cancelledBy);

        Reservation saved = reservationRepository.save(reservation);

        ReservationHistory history = new ReservationHistory();
        history.setReservationId(saved.getId());
        history.setAction("CANCEL");
        history.setContent("取消订单：" + (cancelReason != null ? cancelReason : ""));
        history.setResult("success");
        history.setOperator(cancelledBy);
        history.setOperatorType("crs");
        historyRepository.save(history);

        return saved;
    }

    @Transactional
    public Reservation updateReservationStatus(Integer id, String newStatus, String operator) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        String oldStatus = reservation.getReservationStatus();
        validateStatusTransition(oldStatus, newStatus);

        reservation.setReservationStatus(newStatus);
        reservation.setModifiedBy(operator);

        if ("checked_out".equals(newStatus)) {
            reservation.setStatus(Reservation.Status.completed);
            reservation.setCompletedAt(new Date());
        }

        Reservation saved = reservationRepository.save(reservation);

        String action = switch (newStatus) {
            case "confirmed" -> "CONFIRM";
            case "checked_in" -> "CHECK_IN";
            case "checked_out" -> "CHECK_OUT";
            case "no_show" -> "NO_SHOW";
            default -> "STATUS_CHANGE";
        };

        String content = switch (newStatus) {
            case "confirmed" -> "确认订单";
            case "checked_in" -> "客人入住";
            case "checked_out" -> "客人离店";
            case "no_show" -> "客人未到";
            default -> "状态变更：" + oldStatus + " → " + newStatus;
        };

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
    public Reservation manualIntervene(Integer id, String reason, String operator) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        reservation.setIsManual(true);
        reservation.setManualReason(reason);
        reservation.setModifiedBy(operator);

        Reservation saved = reservationRepository.save(reservation);

        ReservationHistory history = new ReservationHistory();
        history.setReservationId(saved.getId());
        history.setAction("MANUAL_INTERVENE");
        history.setContent("人工干预：" + reason);
        history.setResult("success");
        history.setOperator(operator);
        history.setOperatorType("crs");
        historyRepository.save(history);

        return saved;
    }

    private void validateStatusTransition(String from, String to) {
        Map<String, Set<String>> allowed = Map.of(
                "pending", Set.of("confirmed", "cancelled"),
                "confirmed", Set.of("checked_in", "cancelled", "no_show"),
                "checked_in", Set.of("checked_out")
        );
        Set<String> allowedTargets = allowed.get(from);
        if (allowedTargets == null || !allowedTargets.contains(to)) {
            throw new RuntimeException("不允许的状态变更：" + from + " → " + to);
        }
    }

    private void checkAndReserveInventory(Reservation reservation) {
        Date current = reservation.getCheckInDate();
        Date end = reservation.getCheckOutDate();
        Calendar cal = Calendar.getInstance();

        while (current.before(end)) {
            boolean available = inventoryService.checkInventoryAvailability(
                    reservation.getHotelId(), reservation.getRatePlanId(),
                    reservation.getRoomTypeId(), current, reservation.getRoomCount());
            if (!available) {
                throw new RuntimeException("库存不足，日期：" + new SimpleDateFormat("yyyy-MM-dd").format(current));
            }
            cal.setTime(current);
            cal.add(Calendar.DATE, 1);
            current = cal.getTime();
        }

        current = reservation.getCheckInDate();
        while (current.before(end)) {
            inventoryService.reserveInventory(
                    reservation.getHotelId(), reservation.getRatePlanId(),
                    reservation.getRoomTypeId(), current, reservation.getRoomCount());
            cal.setTime(current);
            cal.add(Calendar.DATE, 1);
            current = cal.getTime();
        }
    }

    private void releaseInventory(Reservation reservation) {
        Date current = reservation.getCheckInDate();
        Date end = reservation.getCheckOutDate();
        Calendar cal = Calendar.getInstance();

        while (current.before(end)) {
            inventoryService.releaseInventory(
                    reservation.getHotelId(), reservation.getRatePlanId(),
                    reservation.getRoomTypeId(), current, reservation.getRoomCount());
            cal.setTime(current);
            cal.add(Calendar.DATE, 1);
            current = cal.getTime();
        }
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

    public List<Reservation> getReservationsByHotelId(Integer hotelId) {
        return reservationRepository.findByHotelId(hotelId);
    }

    public List<Reservation> getReservationsByHotelIdAndReservationStatus(Integer hotelId, String reservationStatus) {
        return reservationRepository.findByHotelIdAndReservationStatus(hotelId, reservationStatus);
    }

    public List<Reservation> getInHouseReservations(Integer hotelId, Date date) {
        return reservationRepository.findByHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
                hotelId, date, date, Reservation.Status.active);
    }

    public List<Reservation> getTodayReservations(Integer hotelId) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.DATE, 1);
        Date tomorrow = cal.getTime();
        return reservationRepository.findByHotelIdAndCreatedAtBetween(hotelId, today, tomorrow);
    }
}
