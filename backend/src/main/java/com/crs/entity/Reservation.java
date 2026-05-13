package com.crs.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预订订单核心实体类 (Reservation)
 * 
 * <p>
 * 本类对应数据库中的 `reservation` 表，是 CRS 系统的核心交易实体。记录了从渠道下单、价格计算、担保校验到 PMS 同步的全过程数据。
 * </p>
 * 
 * <p>
 * 核心业务域：
 * </p>
 * <ul>
 * <li>**多维识别**：包含 CRS 内部编码 (`reservationCode`)、外部渠道单号 (`channelOrderNumber`) 及
 * PMS 回传号 (`pmsNumber`)。</li>
 * <li>**资源锁定**：关联酒店、房型、价格计划，并记录入住/退房日期及房间数量。</li>
 * <li>**财务数据**：记录总价、币种、支付状态、佣金计算以及担保详情。</li>
 * <li>**生命周期**：记录创建、修改、取消及完成的时间和操作人，支持完整的审计追踪。</li>
 * </ul>
 */



@Entity
@Table(name = "reservation")
public class Reservation {

    /** 订单内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;

    /** CRS 系统唯一订单号 (如 'RES202605100001') */
    @Column(name = "reservation_code", nullable = false, unique = true, length = 50)
    private String reservationCode;

    /** 外部渠道（如携程、美团）的原始订单号 */
    @Column(name = "channel_order_number", length = 100)
    private String channelOrderNumber;

    /** 酒店本地 PMS 系统生成的订单号 */
    @Column(name = "pms_number", length = 100)
    private String pmsNumber;

    /** 酒店外部编码 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;

    /** 酒店名称冗余 */
    @Column(name = "hotel_name", length = 200)
    private String hotelName;

    /** 房型外部编码 */
    @Column(name = "room_type_code", length = 50)
    private String roomTypeCode;

    /** 房型名称冗余 */
    @Column(name = "room_type_name", length = 200)
    private String roomTypeName;

    /** 价格计划编码 */
    @Column(name = "rate_plan_code", length = 50)
    private String ratePlanCode;

    /** 价格计划名称冗余 */
    @Column(name = "rate_plan_name", length = 200)
    private String ratePlanName;

    /** 渠道编码 (如 'OTA', 'WECHAT') */
    @Column(name = "channel_code", length = 50)
    private String channelCode;

    /** 渠道名称冗余 */
    @Column(name = "channel_name", length = 100)
    private String channelName;

    /** 市场码编码冗余 */
    @Column(name = "market_code", length = 50)
    private String marketCode;

    /** 来源码编码冗余 */
    @Column(name = "source_code", length = 50)
    private String sourceCode;

    /** 入住日期 (不含时间) */
    @Column(name = "check_in_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date checkInDate;

    /** 退房日期 (不含时间) */
    @Column(name = "check_out_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date checkOutDate;

    /** 入住晚数 */
    @Column(name = "nights")
    private Integer nights;

    /** 预订房间数 */
    @Column(name = "room_count", nullable = false)
    private Integer roomCount = 1;

    /** 每间房成人人数 */
    @Column(name = "adult_count", nullable = false)
    private Integer adultCount = 1;

    /** 每间房儿童人数 */
    @Column(name = "child_count")
    private Integer childCount = 0;

    /** 联系人姓名 */
    @Column(name = "contact_name", length = 100)
    private String contactName;

    /** 联系人电话 */
    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    /** 联系人邮箱 */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    /** 会员卡号 (若是会员订单) */
    @Column(name = "member_no", length = 50)
    private String memberNo;

    /** 会员等级 */
    @Column(name = "member_level", length = 30)
    private String memberLevel;

    /** 原始报价 (打折前) */
    @Column(name = "original_price", precision = 12, scale = 2)
    private BigDecimal originalPrice;

    /** 订单最终总价 (含税含优惠) */
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /** 结算币种 (默认 CNY) */
    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "CNY";

    /** 担保类型 (如 credit_card, prepay, none) */
    @Column(name = "guarantee_type", length = 50)
    private String guaranteeType;

    /** 担保信息脱敏存储或引用 */
    @Column(name = "guarantee_info", columnDefinition = "TEXT")
    private String guaranteeInfo;

    /** 取消政策编码冗余 */
    @Column(name = "cancellation_policy_code", length = 50)
    private String cancellationPolicyCode;

    /** 取消政策简述 */
    @Column(name = "cancellation_policy_desc", length = 500)
    private String cancellationPolicyDesc;

    /** 担保政策编码冗余 */
    @Column(name = "guarantee_policy_code", length = 50)
    private String guaranteePolicyCode;

    /** 担保政策简述 */
    @Column(name = "guarantee_policy_desc", length = 500)
    private String guaranteePolicyDesc;

    /** 特殊要求 (如：靠近电梯、高楼层) */
    @Column(name = "special_request", length = 500)
    private String specialRequest;

    /** 内部备注 */
    @Column(name = "notes", length = 500)
    private String notes;

    /** 客人备注 (如下单时填写的需求) */
    @Column(name = "guest_remark", length = 500)
    private String guestRemark;

    /** 酒店回传或备注 */
    @Column(name = "hotel_remark", length = 500)
    private String hotelRemark;

    /** 是否为人工下单订单 (区别于自动直连下单) */
    @Column(name = "is_manual")
    private Boolean isManual = false;

    /** 人工下单原因 */
    @Column(name = "manual_reason", length = 500)
    private String manualReason;

    /** 佣金比例 */
    @Column(name = "commission_rate", precision = 5, scale = 4)
    private BigDecimal commissionRate;

    /** 佣金总额 */
    @Column(name = "commission_amount", precision = 12, scale = 2)
    private BigDecimal commissionAmount;

    /** 支付状态：unpaid-待支付, paid-已支付, refunded-已退款 */
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "unpaid";

    /**
     * 业务状态
     * confirmed(已确认), wait_for_confirmation(待确认), cancelled(已取消), noshow(未入住),
     * checked_in(已入住)
     */
    @Column(name = "reservation_status", nullable = false, length = 30)
    private String reservationStatus = "confirmed";

    /** 支付截止期限 */
    @Column(name = "payment_deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDeadline;

    /** 记录逻辑状态：active-活跃，cancelled-取消，completed-完成 */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    /** 订单来源分类 (如 channel, manual, group) */
    @Column(name = "order_source", length = 30)
    private String orderSource = "channel";

    /** 创建人用户名 */
    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    /** 修改人用户名 */
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    /** 取消操作人 */
    @Column(name = "cancelled_by", length = 50)
    private String cancelledBy;

    /** 取消时间 */
    @Column(name = "cancelled_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelledAt;

    /** 取消原因 */
    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    /** 订单完成/离店时间 */
    @Column(name = "completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    /** @deprecated 已迁移至 contact_name */
    @Deprecated
    @Column(name = "guest_name", length = 100)
    private String guestName;

    /** @deprecated 已迁移至 contact_email */
    @Deprecated
    @Column(name = "guest_email", length = 100)
    private String guestEmail;

    /** @deprecated 已迁移至 contact_phone */
    @Deprecated
    @Column(name = "guest_phone", length = 50)
    private String guestPhone;

    /** @deprecated 已迁移至 guarantee_info */
    @Deprecated
    @Column(name = "credit_card_info", length = 255)
    private String creditCardInfo;

    /** 关联酒店实体 (基于 hotel_code) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_code", referencedColumnName = "hotel_code", insertable = false, updatable = false)
    private Hotel hotel;

    /** 关联价格计划实体 (基于 rate_plan_code) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_code", referencedColumnName = "rate_code", insertable = false, updatable = false)
    private RatePlan ratePlan;

    /** 关联房型实体 (基于 room_type_code) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_code", referencedColumnName = "code", insertable = false, updatable = false)
    private RoomType roomType;

    /**
     * 记录状态枚举
     */
    public enum Status {
        /** 活跃/处理中 */
        active,
        /** 已取消 */
        cancelled,
        /** 已完成/已离店 */
        completed
    }

    /**
     * 更新前自动刷新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    /**
     * 获取联系人姓名 (兼容旧数据)。
     */
    public String getContactName() {
        if (contactName != null && !contactName.isBlank())
            return contactName;
        return guestName;
    }

    /**
     * 获取联系人电话 (兼容旧数据)。
     */
    public String getContactPhone() {
        if (contactPhone != null && !contactPhone.isBlank())
            return contactPhone;
        return guestPhone;
    }

    /**
     * 获取联系人邮箱 (兼容旧数据)。
     */
    public String getContactEmail() {
        if (contactEmail != null && !contactEmail.isBlank())
            return contactEmail;
        return guestEmail;
    }

    public void setContactName(String contactName) { this.contactName = contactName; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    // 手写常用 Getter/Setter 以保障环境兼容性
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }
    public String getChannelOrderNumber() { return channelOrderNumber; }
    public void setChannelOrderNumber(String channelOrderNumber) { this.channelOrderNumber = channelOrderNumber; }
    public String getPmsNumber() { return pmsNumber; }
    public void setPmsNumber(String pmsNumber) { this.pmsNumber = pmsNumber; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getRoomTypeCode() { return roomTypeCode; }
    public void setRoomTypeCode(String roomTypeCode) { this.roomTypeCode = roomTypeCode; }
    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }
    public String getRatePlanCode() { return ratePlanCode; }
    public void setRatePlanCode(String ratePlanCode) { this.ratePlanCode = ratePlanCode; }
    public String getRatePlanName() { return ratePlanName; }
    public void setRatePlanName(String ratePlanName) { this.ratePlanName = ratePlanName; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }
    public Integer getNights() { return nights; }
    public void setNights(Integer nights) { this.nights = nights; }
    public Integer getRoomCount() { return roomCount; }
    public void setRoomCount(Integer roomCount) { this.roomCount = roomCount; }
    public Integer getAdultCount() { return adultCount; }
    public void setAdultCount(Integer adultCount) { this.adultCount = adultCount; }
    public Integer getChildCount() { return childCount; }
    public void setChildCount(Integer childCount) { this.childCount = childCount; }
    public String getMemberNo() { return memberNo; }
    public void setMemberNo(String memberNo) { this.memberNo = memberNo; }
    public String getMemberLevel() { return memberLevel; }
    public void setMemberLevel(String memberLevel) { this.memberLevel = memberLevel; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getGuaranteeType() { return guaranteeType; }
    public void setGuaranteeType(String guaranteeType) { this.guaranteeType = guaranteeType; }
    public String getGuaranteeInfo() { return guaranteeInfo; }
    public void setGuaranteeInfo(String guaranteeInfo) { this.guaranteeInfo = guaranteeInfo; }
    public String getCancellationPolicyCode() { return cancellationPolicyCode; }
    public void setCancellationPolicyCode(String cancellationPolicyCode) { this.cancellationPolicyCode = cancellationPolicyCode; }
    public String getCancellationPolicyDesc() { return cancellationPolicyDesc; }
    public void setCancellationPolicyDesc(String cancellationPolicyDesc) { this.cancellationPolicyDesc = cancellationPolicyDesc; }
    public String getGuaranteePolicyCode() { return guaranteePolicyCode; }
    public void setGuaranteePolicyCode(String guaranteePolicyCode) { this.guaranteePolicyCode = guaranteePolicyCode; }
    public String getGuaranteePolicyDesc() { return guaranteePolicyDesc; }
    public void setGuaranteePolicyDesc(String guaranteePolicyDesc) { this.guaranteePolicyDesc = guaranteePolicyDesc; }
    public String getSpecialRequest() { return specialRequest; }
    public void setSpecialRequest(String specialRequest) { this.specialRequest = specialRequest; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getGuestRemark() { return guestRemark; }
    public void setGuestRemark(String guestRemark) { this.guestRemark = guestRemark; }
    public String getHotelRemark() { return hotelRemark; }
    public void setHotelRemark(String hotelRemark) { this.hotelRemark = hotelRemark; }
    public Boolean getIsManual() { return isManual; }
    public void setIsManual(Boolean isManual) { this.isManual = isManual; }
    public String getManualReason() { return manualReason; }
    public void setManualReason(String manualReason) { this.manualReason = manualReason; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getReservationStatus() { return reservationStatus; }
    public void setReservationStatus(String reservationStatus) { this.reservationStatus = reservationStatus; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getOrderSource() { return orderSource; }
    public void setOrderSource(String orderSource) { this.orderSource = orderSource; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
    public Date getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Date cancelledAt) { this.cancelledAt = cancelledAt; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public Date getCompletedAt() { return completedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Date getPaymentDeadline() { return paymentDeadline; }
    public void setPaymentDeadline(Date paymentDeadline) { this.paymentDeadline = paymentDeadline; }
}
