package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Menu 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【Menu】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：单一职责原则，提供 Menu 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menus")
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "parent_id")
    private Integer parentId = 0;
    
    @Column(name = "parent_code", length = 50)
    private String parentCode;
    
    @Column(name = "menu_code", nullable = false, unique = true, length = 50)
    private String menuCode;
    
    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;
    
    @Column(name = "menu_type", nullable = false, length = 20)
    private String menuType;
    
    @Column(name = "path", length = 200)
    private String path;
    
    @Column(name = "component", length = 200)
    private String component;
    
    @Column(name = "icon", length = 100)
    private String icon;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    @Column(name = "permission", length = 100)
    private String permission;
    
    @Column(name = "system_type", length = 20)
    private String systemType = "crs";
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    public enum Status {
        active, inactive
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
