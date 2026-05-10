package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 酒店价格操作日志实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_price_logs")
public class HotelPriceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;

    @Column(name = "operator_name", nullable = false, length = 100)
    private String operatorName;

    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType;

    @Column(name = "operation_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date operationTime = new Date();

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
