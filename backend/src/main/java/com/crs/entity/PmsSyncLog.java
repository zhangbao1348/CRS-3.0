package com.crs.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * PmsSyncLog 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【PmsSyncLog】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：单一职责原则，提供 PmsSyncLog 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
  
@Entity @Table(name = "pms_sync_logs")
public class PmsSyncLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    
    @Column(name = "hotel_code", nullable = false, length = 50)
    private String hotelCode;

    @Column(name = "sync_type", nullable = false, length = 30)
    private String syncType;

    @Column(name = "sync_status", nullable = false, length = 20)
    private String syncStatus;

    @Column(name = "sync_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date syncTime = new Date();

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
