package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * RoomTypeCategory 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【RoomTypeCategory】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/12-房型管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 RoomTypeCategory 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */


@Entity
@Table(name = "room_type_categories")
public class RoomTypeCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    @Column(name = "group_id", nullable = false)
    private Integer groupId;
    
    @Column(name = "group_code", length = 50)
    private String groupCode;
    
    @Column(name = "category_code", nullable = false, length = 50)
    private String categoryCode;
    
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    
    public Integer getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    
    public String getGroupCode() {
        return groupCode;
    }
    
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
    
    public String getCategoryCode() {
        return categoryCode;
    }
    
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "RoomTypeCategory{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", categoryCode='" + categoryCode + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}