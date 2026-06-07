package com.crs.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 全系统链路追踪与决策审计日志实体类 (SystemTraceLog)
 * 对应数据库 system_trace_logs 表
 * 
 * <p>本类负责结构化存储 API 调用、后台任务、前端报错等的全链路 Trace 信息，
 * 特别包含决策审计快照 (decisionSnapshot)，用于 AI 根因诊断分析。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/16-数据及报表.md</li>
 * </ul>
 */
@Entity
@Table(name = "system_trace_logs", indexes = {
    @Index(name = "idx_trace_id", columnList = "trace_id"),
    @Index(name = "idx_reference_code", columnList = "reference_code"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class SystemTraceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "trace_id", length = 64, nullable = false)
    private String traceId;

    @Column(name = "reference_code", length = 64)
    private String referenceCode;

    @Column(name = "source_type", length = 32, nullable = false)
    private String sourceType;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "operation_name", length = 128)
    private String operationName;

    @Column(name = "error_class", length = 255)
    private String errorClass;

    @Column(name = "error_method", length = 128)
    private String errorMethod;

    @Column(name = "error_line")
    private Integer errorLine;

    @Column(name = "error_stack", columnDefinition = "TEXT")
    private String errorStack;

    @Column(name = "decision_snapshot", columnDefinition = "MEDIUMTEXT")
    private String decisionSnapshot;

    @Column(name = "related_prd_link", length = 255)
    private String relatedPrdLink;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getErrorClass() {
        return errorClass;
    }

    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    public String getErrorMethod() {
        return errorMethod;
    }

    public void setErrorMethod(String errorMethod) {
        this.errorMethod = errorMethod;
    }

    public Integer getErrorLine() {
        return errorLine;
    }

    public void setErrorLine(Integer errorLine) {
        this.errorLine = errorLine;
    }

    public String getErrorStack() {
        return errorStack;
    }

    public void setErrorStack(String errorStack) {
        this.errorStack = errorStack;
    }

    public String getDecisionSnapshot() {
        return decisionSnapshot;
    }

    public void setDecisionSnapshot(String decisionSnapshot) {
        this.decisionSnapshot = decisionSnapshot;
    }

    public String getRelatedPrdLink() {
        return relatedPrdLink;
    }

    public void setRelatedPrdLink(String relatedPrdLink) {
        this.relatedPrdLink = relatedPrdLink;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
