package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * RoleMenu 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【RoleMenu】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：单一职责原则，提供 RoleMenu 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */



@Entity
@Table(name = "role_menus")
public class RoleMenu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "role_id", nullable = false)
    private Integer roleId;
    
    @Column(name = "menu_id", nullable = false)
    private Integer menuId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    public Integer getMenuId() { return menuId; }
    public void setMenuId(Integer menuId) { this.menuId = menuId; }
}
