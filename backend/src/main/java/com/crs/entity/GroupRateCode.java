package com.crs.entity;





import jakarta.persistence.*;
import java.util.Date;




@Entity
@Table(name = "group_rate_codes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "rate_code"})
})
/**
 * GroupRateCode 实体类
 * 
 * <p>本核心模块自动生成详细注释。主要负责【GroupRateCode】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/08-集团管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 GroupRateCode 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public class GroupRateCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    @Column(name = "group_code", length = 50)
    private String groupCode;

    @Column(name = "rate_code", nullable = false, length = 50)
    private String rateCode;

    @Column(name = "rate_name", nullable = false, length = 100)
    private String rateName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "rate_category", length = 50)
    private String rateCategory;

    @Column(name = "market_code", length = 50)
    private String marketCode;

    @Column(name = "source_code", length = 50)
    private String sourceCode;

    @Column(name = "rate_type", length = 20)
    private String rateType = "basic";

    @Column(name = "parent_rate_code", length = 50)
    private String parentRateCode;

    @Column(name = "derivative_level", length = 20)
    private String derivativeLevel = "basic";

    @Column(name = "discount")
    private Double discount;

    @Column(name = "rounding", length = 20)
    private String rounding;

    @Column(name = "guarantee_rule", length = 50)
    private String guaranteeRule;

    @Column(name = "cancellation_rule", length = 50)
    private String cancellationRule;

    @Column(name = "coupon_rule", length = 20)
    private String couponRule = "unlimited";

    @Column(name = "promotion_rule", length = 20)
    private String promotionRule = "unlimited";

    @Column(name = "allow_points")
    private Boolean allowPoints = false;

    @Column(name = "points_type", length = 20)
    private String pointsType;

    @Column(name = "points_value")
    private Double pointsValue;

    @Column(name = "applicable_room_types", columnDefinition = "JSON")
    private String applicableRoomTypes;

    @Column(name = "packages", columnDefinition = "JSON")
    private String packages;

    @Column(name = "personal_membership", columnDefinition = "JSON")
    private String personalMembership;

    @Column(name = "company_membership", columnDefinition = "JSON")
    private String companyMembership;

    @Column(name = "advance_booking_min")
    private Integer advanceBookingMin;

    @Column(name = "advance_booking_max")
    private Integer advanceBookingMax;

    @Column(name = "minimum_stay_min")
    private Integer minimumStayMin;

    @Column(name = "minimum_stay_max")
    private Integer minimumStayMax;

    @Column(name = "booking_start_time", length = 10)
    private String bookingStartTime;

    @Column(name = "booking_end_time", length = 10)
    private String bookingEndTime;

    @Column(name = "checkin_start_time", length = 10)
    private String checkinStartTime;

    @Column(name = "checkin_end_time", length = 10)
    private String checkinEndTime;

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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }
    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }
    public String getRateCode() { return rateCode; }
    public void setRateCode(String rateCode) { this.rateCode = rateCode; }
    public String getRateName() { return rateName; }
    public void setRateName(String rateName) { this.rateName = rateName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRateCategory() { return rateCategory; }
    public void setRateCategory(String rateCategory) { this.rateCategory = rateCategory; }
    public String getMarketCode() { return marketCode; }
    public void setMarketCode(String marketCode) { this.marketCode = marketCode; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
    public String getRateType() { return rateType; }
    public void setRateType(String rateType) { this.rateType = rateType; }
    public String getParentRateCode() { return parentRateCode; }
    public void setParentRateCode(String parentRateCode) { this.parentRateCode = parentRateCode; }
    public String getDerivativeLevel() { return derivativeLevel; }
    public void setDerivativeLevel(String derivativeLevel) { this.derivativeLevel = derivativeLevel; }
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }
    public String getRounding() { return rounding; }
    public void setRounding(String rounding) { this.rounding = rounding; }
    public String getGuaranteeRule() { return guaranteeRule; }
    public void setGuaranteeRule(String guaranteeRule) { this.guaranteeRule = guaranteeRule; }
    public String getCancellationRule() { return cancellationRule; }
    public void setCancellationRule(String cancellationRule) { this.cancellationRule = cancellationRule; }
    public String getCouponRule() { return couponRule; }
    public void setCouponRule(String couponRule) { this.couponRule = couponRule; }
    public String getPromotionRule() { return promotionRule; }
    public void setPromotionRule(String promotionRule) { this.promotionRule = promotionRule; }
    public Boolean getAllowPoints() { return allowPoints; }
    public void setAllowPoints(Boolean allowPoints) { this.allowPoints = allowPoints; }
    public String getPointsType() { return pointsType; }
    public void setPointsType(String pointsType) { this.pointsType = pointsType; }
    public Double getPointsValue() { return pointsValue; }
    public void setPointsValue(Double pointsValue) { this.pointsValue = pointsValue; }
    public String getApplicableRoomTypes() { return applicableRoomTypes; }
    public void setApplicableRoomTypes(String applicableRoomTypes) { this.applicableRoomTypes = applicableRoomTypes; }
    public String getPackages() { return packages; }
    public void setPackages(String packages) { this.packages = packages; }
    public String getPersonalMembership() { return personalMembership; }
    public void setPersonalMembership(String personalMembership) { this.personalMembership = personalMembership; }
    public String getCompanyMembership() { return companyMembership; }
    public void setCompanyMembership(String companyMembership) { this.companyMembership = companyMembership; }
    public Integer getAdvanceBookingMin() { return advanceBookingMin; }
    public void setAdvanceBookingMin(Integer advanceBookingMin) { this.advanceBookingMin = advanceBookingMin; }
    public Integer getAdvanceBookingMax() { return advanceBookingMax; }
    public void setAdvanceBookingMax(Integer advanceBookingMax) { this.advanceBookingMax = advanceBookingMax; }
    public Integer getMinimumStayMin() { return minimumStayMin; }
    public void setMinimumStayMin(Integer minimumStayMin) { this.minimumStayMin = minimumStayMin; }
    public Integer getMinimumStayMax() { return minimumStayMax; }
    public void setMinimumStayMax(Integer minimumStayMax) { this.minimumStayMax = minimumStayMax; }
    public String getBookingStartTime() { return bookingStartTime; }
    public void setBookingStartTime(String bookingStartTime) { this.bookingStartTime = bookingStartTime; }
    public String getBookingEndTime() { return bookingEndTime; }
    public void setBookingEndTime(String bookingEndTime) { this.bookingEndTime = bookingEndTime; }
    public String getCheckinStartTime() { return checkinStartTime; }
    public void setCheckinStartTime(String checkinStartTime) { this.checkinStartTime = checkinStartTime; }
    public String getCheckinEndTime() { return checkinEndTime; }
    public void setCheckinEndTime(String checkinEndTime) { this.checkinEndTime = checkinEndTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
