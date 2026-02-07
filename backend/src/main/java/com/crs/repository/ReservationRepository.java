package com.crs.repository;

import com.crs.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

/**
 * 预订仓库接口
 * 用于预订数据的CRUD操作
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    /**
     * 根据预订号查询预订
     * @param reservationCode 预订号
     * @return 预订信息
     */
    Reservation findByReservationCode(String reservationCode);
    
    /**
     * 根据酒店ID查询预订列表
     * @param hotelId 酒店ID
     * @return 预订列表
     */
    List<Reservation> findByHotelId(Integer hotelId);
    
    /**
     * 根据酒店ID和状态查询预订列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 预订列表
     */
    List<Reservation> findByHotelIdAndStatus(Integer hotelId, Reservation.Status status);
    
    /**
     * 根据酒店ID和预订状态查询预订列表
     * @param hotelId 酒店ID
     * @param reservationStatus 预订状态
     * @return 预订列表
     */
    List<Reservation> findByHotelIdAndReservationStatus(Integer hotelId, String reservationStatus);
    
    /**
     * 根据渠道ID查询预订列表
     * @param channelId 渠道ID
     * @return 预订列表
     */
    List<Reservation> findByChannelId(Integer channelId);
    
    /**
     * 根据客人姓名查询预订列表
     * @param guestName 客人姓名
     * @return 预订列表
     */
    List<Reservation> findByGuestNameContaining(String guestName);
    
    /**
     * 根据入住日期范围查询预订列表
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预订列表
     */
    List<Reservation> findByHotelIdAndCheckInDateBetween(Integer hotelId, Date startDate, Date endDate);
    
    /**
     * 根据离店日期范围查询预订列表
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预订列表
     */
    List<Reservation> findByHotelIdAndCheckOutDateBetween(Integer hotelId, Date startDate, Date endDate);
    
    /**
     * 根据日期范围查询在店客人预订列表
     * @param hotelId 酒店ID
     * @param date 日期
     * @return 预订列表
     */
    List<Reservation> findByHotelIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanAndStatus(
            Integer hotelId, Date date, Date date2, Reservation.Status status);
    
    /**
     * 根据支付状态查询预订列表
     * @param hotelId 酒店ID
     * @param paymentStatus 支付状态
     * @return 预订列表
     */
    List<Reservation> findByHotelIdAndPaymentStatus(Integer hotelId, String paymentStatus);
    
    /**
     * 根据创建时间范围查询预订列表
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预订列表
     */
    List<Reservation> findByHotelIdAndCreatedAtBetween(Integer hotelId, Date startDate, Date endDate);
}
