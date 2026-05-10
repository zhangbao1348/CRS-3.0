package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * ReservationGuest 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【ReservationGuest】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：单一职责原则，提供 ReservationGuest 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation_guest")
public class ReservationGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reservation_id", nullable = false)
    private Integer reservationId;

    @Column(name = "guest_type", nullable = false, length = 20)
    private String guestType = "guest";

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "id_type", length = 30)
    private String idType;

    @Column(name = "id_number", length = 100)
    private String idNumber;

    @Column(name = "member_no", length = 50)
    private String memberNo;

    @Column(name = "member_level", length = 30)
    private String memberLevel;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "pms_account", length = 100)
    private String pmsAccount;

    @Column(name = "pms_status", length = 30)
    private String pmsStatus;

    @Column(name = "room_index")
    private Integer roomIndex = 0;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
