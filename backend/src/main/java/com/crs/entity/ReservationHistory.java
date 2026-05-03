package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

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

    @Column(name = "action", length = 50)
    private String action;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "result", nullable = false, length = 20)
    private String result;

    @Column(name = "operator", nullable = false, length = 50)
    private String operator;

    @Column(name = "operator_type", length = 20)
    private String operatorType = "system";

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "log_id")
    private Integer logId;

    @Column(name = "operation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date operationTime = new Date();
}
