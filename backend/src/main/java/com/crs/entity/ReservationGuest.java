package com.crs.entity;


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



@Entity
@Table(name = "reservation_guest")
public class ReservationGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id")
    private Integer tenantId;

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
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public String getGuestType() { return guestType; }
    public void setGuestType(String guestType) { this.guestType = guestType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public String getMemberNo() { return memberNo; }
    public void setMemberNo(String memberNo) { this.memberNo = memberNo; }
    public String getMemberLevel() { return memberLevel; }
    public void setMemberLevel(String memberLevel) { this.memberLevel = memberLevel; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getPmsAccount() { return pmsAccount; }
    public void setPmsAccount(String pmsAccount) { this.pmsAccount = pmsAccount; }
    public String getPmsStatus() { return pmsStatus; }
    public void setPmsStatus(String pmsStatus) { this.pmsStatus = pmsStatus; }
    public Integer getRoomIndex() { return roomIndex; }
    public void setRoomIndex(Integer roomIndex) { this.roomIndex = roomIndex; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
