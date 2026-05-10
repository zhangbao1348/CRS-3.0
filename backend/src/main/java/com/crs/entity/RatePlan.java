package com.crs.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 价格计划实体类 (RatePlan)
 * 
 * <p>本类对应数据库中的 `rate_plans` 表，是 CRS 系统中最核心、最复杂的业务实体之一。</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *     <li>**定义售卖规则**：包含房价码、价格分类、市场来源等基础信息。</li>
 *     <li>**价格派生逻辑**：支持基于 `parentRateCode` 的派生定价，通过 `discount` 和 `rounding` 计算最终价格。</li>
 *     <li>**预订限制**：控制提前预订天数、最短/最长入住天数、预订及入住时间段等。</li>
 *     <li>**差异化定价**：关联 `roomTypeDiffCode` (房型差价) 和 `personDiffCode` (人数差价) 系统。</li>
 *     <li>**多租户与多店**：通过 `tenantId` 和 `hotelId` 实现数据隔离。</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_plans")
public class RatePlan {

    /** 价格计划内部主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;

    /** 所属酒店 ID */
    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    /** 酒店外部编码冗余 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;

    /** 来源集团房价码（若是从集团同步而来的计划） */
    @Column(name = "source_group_rate_code", length = 50)
    private String sourceGroupRateCode;

    /** 价格计划编码 (如 'RACK', 'CORP01')，酒店内唯一 */
    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;

    /** 价格计划名称 */
    @Column(name = "rate_name", nullable = false, length = 100)
    private String rateName;

    /** 计划详细描述及售卖须知 */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 房价类别（如：BAR, 协议价, 促销价） */
    @Column(name = "rate_category", length = 50)
    private String rateCategory;

    /** 市场代码 (Market Code) */
    @Column(name = "market_code", length = 50)
    private String marketCode;

    /** 渠道来源代码 (Source Code) */
    @Column(name = "source_code", length = 50)
    private String sourceCode;

    /** 价格类型：basic-基础价，derived-派生价 */
    @Column(name = "rate_type", length = 20)
    private String rateType = "basic";

    /** 父级价格计划编码（仅用于派生价） */
    @Column(name = "parent_rate_code", length = 50)
    private String parentRateCode;

    /** 派生层级 (basic/derived) */
    @Column(name = "derivative_level", length = 20)
    private String derivativeLevel = "basic";

    /** 派生折扣/加价数值（百分比或固定值） */
    @Column(name = "discount")
    private Double discount;

    /** 取整规则（如：四舍五入、向上取整） */
    @Column(name = "rounding", length = 20)
    private String rounding;

    /** 担保规则编码 */
    @Column(name = "guarantee_rule", length = 50)
    private String guaranteeRule;

    /** 取消政策编码 */
    @Column(name = "cancellation_rule", length = 50)
    private String cancellationRule;

    /** 优惠券适用规则 */
    @Column(name = "coupon_rule", length = 20)
    private String couponRule = "unlimited";

    /** 促销活动适用规则 */
    @Column(name = "promotion_rule", length = 20)
    private String promotionRule = "unlimited";

    /** 是否允许产生积分 */
    @Column(name = "allow_points")
    private Boolean allowPoints = false;

    /** 积分计算类型 */
    @Column(name = "points_type", length = 20)
    private String pointsType;

    /** 积分数值或倍率 */
    @Column(name = "points_value")
    private Double pointsValue;

    /** 适用的房型列表 (JSON 格式) */
    @Column(name = "applicable_room_types", columnDefinition = "JSON")
    private String applicableRoomTypes;

    /** 包含的包价内容 (JSON 格式，如：含早、含SPA) */
    @Column(name = "packages", columnDefinition = "JSON")
    private String packages;

    /** 适用的个人会员等级限制 (JSON) */
    @Column(name = "personal_membership", columnDefinition = "JSON")
    private String personalMembership;

    /** 适用的公司会员/协议单位限制 (JSON) */
    @Column(name = "company_membership", columnDefinition = "JSON")
    private String companyMembership;

    /** 提前预订天数下限 */
    @Column(name = "advance_booking_min")
    private Integer advanceBookingMin;

    /** 提前预订天数上限 */
    @Column(name = "advance_booking_max")
    private Integer advanceBookingMax;

    /** 最短入住晚数 */
    @Column(name = "minimum_stay_min")
    private Integer minimumStayMin;

    /** 最长入住晚数 */
    @Column(name = "minimum_stay_max")
    private Integer minimumStayMax;

    /** 每日开始预订时间 (HH:mm) */
    @Column(name = "booking_start_time", length = 10)
    private String bookingStartTime;

    /** 每日截止预订时间 (HH:mm) */
    @Column(name = "booking_end_time", length = 10)
    private String bookingEndTime;

    /** 入住开始时间段限制 */
    @Column(name = "checkin_start_time", length = 10)
    private String checkinStartTime;

    /** 入住结束时间段限制 */
    @Column(name = "checkin_end_time", length = 10)
    private String checkinEndTime;

    /** 关联的房型差价系统编码 */
    @Column(name = "room_type_diff_code", length = 50)
    private String roomTypeDiffCode;

    /** 关联的人数差价系统编码 */
    @Column(name = "person_diff_code", length = 50)
    private String personDiffCode;

    /** 计划状态：active-启用，inactive-停用 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    /**
     * JPA 更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}

