package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 接口日志实体类
 * 对应数据库api_logs表
 */



@Entity
@Table(name = "api_logs")
public class ApiLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "reservation_id")
    private Integer reservationId;
    
    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;
    
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
