package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 预订操作历史实体类
 * 对应数据库reservation_history表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation_history")
public class ReservationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "reservation_id", nullable = false)
    private Integer reservationId;
    
    @Column(name = "content", nullable = false, length = 200)
    private String content;
    
    @Column(name = "result", nullable = false, length = 20)
    private String result;
    
    @Column(name = "operator", nullable = false, length = 50)
    private String operator;
    
    @Column(name = "operation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date operationTime = new Date();
    
    @Column(name = "log_id")
    private Integer logId;
}
