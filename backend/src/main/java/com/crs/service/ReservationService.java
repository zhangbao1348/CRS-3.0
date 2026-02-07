package com.crs.service;

import com.crs.entity.Reservation;
import com.crs.repository.ReservationRepository;
import com.crs.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.UUID;

/**
 * 预订服务类
 * 用于处理预订相关的业务逻辑
 */
@Service
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;
    
    public ReservationService(ReservationRepository reservationRepository, InventoryService inventoryService) {
        this.reservationRepository = reservationRepository;
        this.inventoryService = inventoryService;
    }
    
    /**
     * 获取所有预订列表
     * @return 预订列表
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    /**
     * 根据ID获取预订详情
     * @param id 预订ID
     * @return 预订详情
     */
    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findById(id);
    }
    
    /**
     * 根据预订号获取预订详情
     * @param reservationCode 预订号
     * @return 预订详情
     */
    public Reservation getReservationByCode(String reservationCode) {
        return reservationRepository.findByReservationCode(reservationCode);
    }
    
    /**
     * 根据酒店ID获取预订列表
     * @param hotelId 酒店ID
     * @return 预订列表
     */
    public List<Reservation> getReservationsByHotelId(Integer hotelId) {
        return reservationRepository.findByHotelId(hotelId);
    }
    
    /**
     * 根据酒店ID和状态获取预订列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 预订列表
     */
    public List<Reservation> getReservationsByHotelIdAndStatus(Integer hotelId, Reservation.Status status) {
        return reservationRepository.findByHotelIdAndStatus(hotelId, status);
    }
    
    /**
     * 根据酒店ID和预订状态获取预订列表
     * @param hotelId 酒店ID
     * @param reservationStatus 预订状态
     * @return 预订列表
     */
    public List<Reservation> getReservationsByHotelIdAndReservationStatus(Integer hotelId, String reservationStatus) {
        return reservationRepository.findByHotelIdAndReservationStatus(hotelId, reservationStatus);
    }
    
    /**
     * 根据入住日期范围获取预订列表
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预订列表
     */
    public List<Reservation> getReservationsByCheckInDateRange(Integer hotelId, Date startDate, Date endDate) {
        return reservationRepository.findByHotelIdAndCheckInDateBetween(hotelId, startDate, endDate);
    }
    
    /**
     * 根据离店日期范围获取预订列表
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预订列表
     */
    public List<Reservation> getReservationsByCheckOutDateRange(Integer hotelId, Date startDate, Date endDate) {
        return reservationRepository.findByHotelIdAndCheckOutDateBetween(hotelId, startDate, endDate);
    }
    
    /**
     * 获取在店客人预订列表
     * @param hotelId 酒店ID
     * @param date 日期
     * @return 预订列表
     */
    public List<Reservation> getInHouseReservations(Integer hotelId, Date date) {
        return reservationRepository.findByHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
                hotelId, date, date, Reservation.Status.active);
    }
    
    /**
     * 创建预订
     * @param reservation 预订信息
     * @return 创建的预订信息
     */
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // 生成预订号
        String reservationCode = generateReservationCode();
        reservation.setReservationCode(reservationCode);
        
        // 检查库存
        checkInventoryAvailability(reservation);
        
        // 预留库存
        reserveInventory(reservation);
        
        // 保存预订
        return reservationRepository.save(reservation);
    }
    
    /**
     * 更新预订
     * @param id 预订ID
     * @param reservation 预订信息
     * @return 更新后的预订信息
     */
    @Transactional
    public Reservation updateReservation(Integer id, Reservation reservation) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        // 检查库存（如果日期或房间数量有变化）
        if (!existingReservation.getCheckInDate().equals(reservation.getCheckInDate()) ||
            !existingReservation.getCheckOutDate().equals(reservation.getCheckOutDate()) ||
            existingReservation.getRoomCount() != reservation.getRoomCount()) {
            
            // 释放原有库存
            releaseInventory(existingReservation);
            
            // 检查新库存
            checkInventoryAvailability(reservation);
            
            // 预留新库存
            reserveInventory(reservation);
        }
        
        // 更新预订信息
        existingReservation.setRatePlanId(reservation.getRatePlanId());
        existingReservation.setRoomTypeId(reservation.getRoomTypeId());
        existingReservation.setChannelId(reservation.getChannelId());
        existingReservation.setMarketCodeId(reservation.getMarketCodeId());
        existingReservation.setSourceCodeId(reservation.getSourceCodeId());
        existingReservation.setGuestName(reservation.getGuestName());
        existingReservation.setGuestEmail(reservation.getGuestEmail());
        existingReservation.setGuestPhone(reservation.getGuestPhone());
        existingReservation.setCheckInDate(reservation.getCheckInDate());
        existingReservation.setCheckOutDate(reservation.getCheckOutDate());
        existingReservation.setAdultCount(reservation.getAdultCount());
        existingReservation.setChildCount(reservation.getChildCount());
        existingReservation.setRoomCount(reservation.getRoomCount());
        existingReservation.setTotalPrice(reservation.getTotalPrice());
        existingReservation.setCurrency(reservation.getCurrency());
        existingReservation.setPaymentStatus(reservation.getPaymentStatus());
        existingReservation.setReservationStatus(reservation.getReservationStatus());
        existingReservation.setGuaranteeType(reservation.getGuaranteeType());
        existingReservation.setCreditCardInfo(reservation.getCreditCardInfo());
        existingReservation.setSpecialRequest(reservation.getSpecialRequest());
        existingReservation.setNotes(reservation.getNotes());
        existingReservation.setModifiedBy(reservation.getModifiedBy());
        existingReservation.setStatus(reservation.getStatus());
        
        return reservationRepository.save(existingReservation);
    }
    
    /**
     * 取消预订
     * @param id 预订ID
     * @param modifiedBy 修改人
     * @return 取消后的预订信息
     */
    @Transactional
    public Reservation cancelReservation(Integer id, String modifiedBy) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        // 释放库存
        releaseInventory(existingReservation);
        
        // 更新预订状态
        existingReservation.setReservationStatus("cancelled");
        existingReservation.setStatus(Reservation.Status.cancelled);
        existingReservation.setModifiedBy(modifiedBy);
        
        return reservationRepository.save(existingReservation);
    }
    
    /**
     * 完成预订
     * @param id 预订ID
     * @param modifiedBy 修改人
     * @return 完成后的预订信息
     */
    @Transactional
    public Reservation completeReservation(Integer id, String modifiedBy) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        // 更新预订状态
        existingReservation.setReservationStatus("completed");
        existingReservation.setStatus(Reservation.Status.completed);
        existingReservation.setModifiedBy(modifiedBy);
        
        return reservationRepository.save(existingReservation);
    }
    
    /**
     * 删除预订
     * @param id 预订ID
     */
    @Transactional
    public void deleteReservation(Integer id) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        // 释放库存
        releaseInventory(existingReservation);
        
        // 删除预订
        reservationRepository.deleteById(id);
    }
    
    /**
     * 检查库存可用性
     * @param reservation 预订信息
     */
    private void checkInventoryAvailability(Reservation reservation) {
        Date currentDate = reservation.getCheckInDate();
        Date endDate = reservation.getCheckOutDate();
        
        while (currentDate.before(endDate)) {
            boolean isAvailable = inventoryService.checkInventoryAvailability(
                    reservation.getHotelId(),
                    reservation.getRatePlanId(),
                    reservation.getRoomTypeId(),
                    currentDate,
                    reservation.getRoomCount());
            
            if (!isAvailable) {
                throw new RuntimeException("Inventory not available for date: " + currentDate);
            }
            
            // 增加一天
            currentDate = new Date(currentDate.getTime() + 86400000);
        }
    }
    
    /**
     * 预留库存
     * @param reservation 预订信息
     */
    private void reserveInventory(Reservation reservation) {
        Date currentDate = reservation.getCheckInDate();
        Date endDate = reservation.getCheckOutDate();
        
        while (currentDate.before(endDate)) {
            inventoryService.reserveInventory(
                    reservation.getHotelId(),
                    reservation.getRatePlanId(),
                    reservation.getRoomTypeId(),
                    currentDate,
                    reservation.getRoomCount());
            
            // 增加一天
            currentDate = new Date(currentDate.getTime() + 86400000);
        }
    }
    
    /**
     * 释放库存
     * @param reservation 预订信息
     */
    private void releaseInventory(Reservation reservation) {
        Date currentDate = reservation.getCheckInDate();
        Date endDate = reservation.getCheckOutDate();
        
        while (currentDate.before(endDate)) {
            inventoryService.releaseInventory(
                    reservation.getHotelId(),
                    reservation.getRatePlanId(),
                    reservation.getRoomTypeId(),
                    currentDate,
                    reservation.getRoomCount());
            
            // 增加一天
            currentDate = new Date(currentDate.getTime() + 86400000);
        }
    }
    
    /**
     * 生成预订号
     * @return 预订号
     */
    private String generateReservationCode() {
        String prefix = "RES";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp + random;
    }
    
    /**
     * 筛选订单
     * @param hotelId 酒店ID
     * @param orderNo 订单号
     * @param status 状态
     * @param channelId 渠道ID
     * @param startBookingDate 开始预订日期
     * @param endBookingDate 结束预订日期
     * @param startCheckInDate 开始入住日期
     * @param endCheckInDate 结束入住日期
     * @param startCheckOutDate 开始离店日期
     * @param endCheckOutDate 结束离店日期
     * @return 订单列表
     */
    public List<Reservation> filterReservations(
            Integer hotelId,
            String orderNo,
            String status,
            Integer channelId,
            Date startBookingDate,
            Date endBookingDate,
            Date startCheckInDate,
            Date endCheckInDate,
            Date startCheckOutDate,
            Date endCheckOutDate) {
        // 这里实现订单筛选逻辑
        // 由于JPA查询条件较多，这里使用基础实现
        // 实际项目中可以使用Specification或QueryDSL实现复杂查询
        List<Reservation> reservations = reservationRepository.findByHotelId(hotelId);
        
        // 内存中筛选
        return reservations.stream()
                .filter(reservation -> orderNo == null || reservation.getReservationCode().contains(orderNo))
                .filter(reservation -> status == null || reservation.getReservationStatus().equals(status))
                .filter(reservation -> channelId == null || reservation.getChannelId().equals(channelId))
                .filter(reservation -> startBookingDate == null || !reservation.getCreatedAt().before(startBookingDate))
                .filter(reservation -> endBookingDate == null || !reservation.getCreatedAt().after(endBookingDate))
                .filter(reservation -> startCheckInDate == null || !reservation.getCheckInDate().before(startCheckInDate))
                .filter(reservation -> endCheckInDate == null || !reservation.getCheckInDate().after(endCheckInDate))
                .filter(reservation -> startCheckOutDate == null || !reservation.getCheckOutDate().before(startCheckOutDate))
                .filter(reservation -> endCheckOutDate == null || !reservation.getCheckOutDate().after(endCheckOutDate))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 导出订单为CSV格式
     * @param reservations 订单列表
     * @return CSV格式的订单数据
     */
    public String exportReservationsToCsv(List<Reservation> reservations) {
        StringBuilder csv = new StringBuilder();
        
        // CSV表头
        csv.append("订单号,状态,渠道,预订时间,入住日期,离店日期,总价,房型,客人姓名,联系电话,成人数量,儿童数量\n");
        
        // 填充数据
        for (Reservation reservation : reservations) {
            csv.append(reservation.getReservationCode()).append(",");
            csv.append(reservation.getReservationStatus()).append(",");
            csv.append(reservation.getChannelId()).append(",");
            csv.append(reservation.getCreatedAt()).append(",");
            csv.append(reservation.getCheckInDate()).append(",");
            csv.append(reservation.getCheckOutDate()).append(",");
            csv.append(reservation.getTotalPrice()).append(",");
            csv.append(reservation.getRoomTypeId()).append(",");
            csv.append(reservation.getGuestName()).append(",");
            csv.append(reservation.getGuestPhone()).append(",");
            csv.append(reservation.getAdultCount()).append(",");
            csv.append(reservation.getChildCount()).append("\n");
        }
        
        return csv.toString();
    }
    
    /**
     * 获取今日订单
     * @param hotelId 酒店ID
     * @return 今日订单列表
     */
    public List<Reservation> getTodayReservations(Integer hotelId) {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        
        Date tomorrow = new Date(today.getTime() + 86400000);
        
        return reservationRepository.findByHotelIdAndCreatedAtBetween(hotelId, today, tomorrow);
    }
    
    /**
     * 获取明日入住订单
     * @param hotelId 酒店ID
     * @return 明日入住订单列表
     */
    public List<Reservation> getTomorrowCheckInReservations(Integer hotelId) {
        Date tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(0);
        tomorrow.setMinutes(0);
        tomorrow.setSeconds(0);
        
        Date dayAfterTomorrow = new Date(tomorrow.getTime() + 86400000);
        
        return reservationRepository.findByHotelIdAndCheckInDateBetween(hotelId, tomorrow, dayAfterTomorrow);
    }
}

