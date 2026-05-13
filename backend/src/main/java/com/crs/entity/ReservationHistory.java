package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * ReservationHistory 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【ReservationHistory】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：单一职责原则，提供 ReservationHistory 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */



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
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public String getOperatorType() { return operatorType; }
    public void setOperatorType(String operatorType) { this.operatorType = operatorType; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }
    public Date getOperationTime() { return operationTime; }
    public void setOperationTime(Date operationTime) { this.operationTime = operationTime; }
}
