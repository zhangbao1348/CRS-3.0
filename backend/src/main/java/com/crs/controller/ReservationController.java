package com.crs.controller;

import com.crs.entity.Reservation;
import com.crs.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Date;

/**
 * 预订控制器
 * 提供预订管理的REST API端点
 */
@RestController
@RequestMapping("/api/reservation")
@CrossOrigin(origins = "*")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    /**
     * 获取所有预订列表
     * @return 预订列表
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 根据ID获取预订详情
     * @param id 预订ID
     * @return 预订详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Integer id) {
        return reservationService.getReservationById(id)
                .map(reservation -> new ResponseEntity<>(reservation, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 根据预订号获取预订详情
     * @param code 预订号
     * @return 预订详情
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Reservation> getReservationByCode(@PathVariable String code) {
        Reservation reservation = reservationService.getReservationByCode(code);
        if (reservation != null) {
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 根据酒店ID获取预订列表
     * @param hotelId 酒店ID
     * @return 预订列表
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Reservation>> getReservationsByHotelId(@PathVariable Integer hotelId) {
        List<Reservation> reservations = reservationService.getReservationsByHotelId(hotelId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 根据酒店ID和状态获取预订列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 预订列表
     */
    @GetMapping("/hotel/{hotelId}/status/{status}")
    public ResponseEntity<List<Reservation>> getReservationsByHotelIdAndStatus(
            @PathVariable Integer hotelId,
            @PathVariable Reservation.Status status) {
        List<Reservation> reservations = reservationService.getReservationsByHotelIdAndStatus(hotelId, status);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 根据酒店ID和预订状态获取预订列表
     * @param hotelId 酒店ID
     * @param reservationStatus 预订状态
     * @return 预订列表
     */
    @GetMapping("/hotel/{hotelId}/reservation-status/{reservationStatus}")
    public ResponseEntity<List<Reservation>> getReservationsByHotelIdAndReservationStatus(
            @PathVariable Integer hotelId,
            @PathVariable String reservationStatus) {
        List<Reservation> reservations = reservationService.getReservationsByHotelIdAndReservationStatus(
                hotelId, reservationStatus);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 根据入住日期范围获取预订列表
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预订列表
     */
    @GetMapping("/check-in-range")
    public ResponseEntity<List<Reservation>> getReservationsByCheckInDateRange(
            @RequestParam Integer hotelId,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        List<Reservation> reservations = reservationService.getReservationsByCheckInDateRange(
                hotelId, startDate, endDate);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 根据离店日期范围获取预订列表
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预订列表
     */
    @GetMapping("/check-out-range")
    public ResponseEntity<List<Reservation>> getReservationsByCheckOutDateRange(
            @RequestParam Integer hotelId,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        List<Reservation> reservations = reservationService.getReservationsByCheckOutDateRange(
                hotelId, startDate, endDate);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 获取在店客人预订列表
     * @param hotelId 酒店ID
     * @param date 日期
     * @return 预订列表
     */
    @GetMapping("/in-house")
    public ResponseEntity<List<Reservation>> getInHouseReservations(
            @RequestParam Integer hotelId,
            @RequestParam Date date) {
        List<Reservation> reservations = reservationService.getInHouseReservations(hotelId, date);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 创建预订
     * @param reservation 预订信息
     * @return 创建的预订信息
     */
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        try {
            Reservation createdReservation = reservationService.createReservation(reservation);
            return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 更新预订
     * @param id 预订ID
     * @param reservation 预订信息
     * @return 更新后的预订信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Integer id, @RequestBody Reservation reservation) {
        try {
            Reservation updatedReservation = reservationService.updateReservation(id, reservation);
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 取消预订
     * @param id 预订ID
     * @param modifiedBy 修改人
     * @return 取消后的预订信息
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelReservation(
            @PathVariable Integer id,
            @RequestParam String modifiedBy) {
        try {
            Reservation cancelledReservation = reservationService.cancelReservation(id, modifiedBy);
            return new ResponseEntity<>(cancelledReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 完成预订
     * @param id 预订ID
     * @param modifiedBy 修改人
     * @return 完成后的预订信息
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Reservation> completeReservation(
            @PathVariable Integer id,
            @RequestParam String modifiedBy) {
        try {
            Reservation completedReservation = reservationService.completeReservation(id, modifiedBy);
            return new ResponseEntity<>(completedReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 删除预订
     * @param id 预订ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer id) {
        try {
            reservationService.deleteReservation(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
    @GetMapping("/filter")
    public ResponseEntity<List<Reservation>> filterReservations(
            @RequestParam Integer hotelId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer channelId,
            @RequestParam(required = false) Date startBookingDate,
            @RequestParam(required = false) Date endBookingDate,
            @RequestParam(required = false) Date startCheckInDate,
            @RequestParam(required = false) Date endCheckInDate,
            @RequestParam(required = false) Date startCheckOutDate,
            @RequestParam(required = false) Date endCheckOutDate) {
        List<Reservation> reservations = reservationService.filterReservations(
                hotelId, orderNo, status, channelId, startBookingDate, endBookingDate,
                startCheckInDate, endCheckInDate, startCheckOutDate, endCheckOutDate);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 导出订单
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
     * @return 导出的CSV数据
     */
    @GetMapping("/export")
    public ResponseEntity<String> exportReservations(
            @RequestParam Integer hotelId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer channelId,
            @RequestParam(required = false) Date startBookingDate,
            @RequestParam(required = false) Date endBookingDate,
            @RequestParam(required = false) Date startCheckInDate,
            @RequestParam(required = false) Date endCheckInDate,
            @RequestParam(required = false) Date startCheckOutDate,
            @RequestParam(required = false) Date endCheckOutDate) {
        List<Reservation> reservations = reservationService.filterReservations(
                hotelId, orderNo, status, channelId, startBookingDate, endBookingDate,
                startCheckInDate, endCheckInDate, startCheckOutDate, endCheckOutDate);
        
        String csvData = reservationService.exportReservationsToCsv(reservations);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=reservations.csv")
                .body(csvData);
    }
    
    /**
     * 获取今日订单
     * @param hotelId 酒店ID
     * @return 今日订单列表
     */
    @GetMapping("/today")
    public ResponseEntity<List<Reservation>> getTodayReservations(@RequestParam Integer hotelId) {
        List<Reservation> reservations = reservationService.getTodayReservations(hotelId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    /**
     * 获取明日入住订单
     * @param hotelId 酒店ID
     * @return 明日入住订单列表
     */
    @GetMapping("/tomorrow-checkin")
    public ResponseEntity<List<Reservation>> getTomorrowCheckInReservations(@RequestParam Integer hotelId) {
        List<Reservation> reservations = reservationService.getTomorrowCheckInReservations(hotelId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
}

