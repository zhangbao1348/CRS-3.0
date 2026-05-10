package com.crs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 酒店核心实体类 (Hotel)
 * 
 * <p>本类对应数据库中的 `hotels` 表，承载了酒店的基础信息、位置信息以及全局业务控制开关。</p>
 * 
 * <p>关键设计：</p>
 * <ul>
 *     <li>**多租户隔离**：通过 `tenantId` 关联所属租户（集团）。</li>
 *     <li>**权限控制**：包含 `allowCreateRateCode` 和 `allowCreateRoomType` 字段，用于控制集团对单店权限的管控。</li>
 *     <li>**价格模型**：支持多价格体系开关、房型差价系统开关以及人数差价系统开关。</li>
 * </ul>
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel {
    
    /** 酒店唯一内部 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 酒店唯一外部编码 (如 'JJSH001')，用于 API 调用和系统对接 */
    @Column(name = "hotel_code", nullable = false, unique = true, length = 50)
    private String hotelCode;
    
    /** 所属租户（集团）ID，用于数据权限隔离 */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 酒店中文全称 */
    @Column(name = "chinese_name", nullable = false, length = 100)
    private String chineseName;
    
    /** 酒店英文全称 */
    @Column(name = "english_name", nullable = false, length = 100)
    private String englishName;
    
    /** 星级（如：5, 4, 3, economic） */
    @Column(name = "star_rating", length = 10)
    private String starRating;
    
    /** 所在省份/直辖市 */
    @Column(name = "province", nullable = false, length = 50)
    private String province;
    
    /** 所在城市 */
    @Column(name = "city", nullable = false, length = 50)
    private String city;
    
    /** 详细地址 */
    @Column(name = "address", nullable = false, length = 200)
    private String address;
    
    /** 地理经度 (WGS84) */
    @Column(name = "longitude")
    private Double longitude;
    
    /** 地理纬度 (WGS84) */
    @Column(name = "latitude")
    private Double latitude;
    
    /** 酒店前台联系电话 */
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    
    /** 酒店公共联系邮箱 */
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    
    /** 酒店详细介绍（HTML 或 文本格式） */
    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;
    
    /** 总房间数 */
    @Column(name = "total_rooms")
    private Integer totalRooms;
    
    /** 是否允许酒店自行创建房价码：allow-允许，deny-禁止 */
    @Column(name = "allow_create_rate_code", length = 20)
    private String allowCreateRateCode = "allow";
    
    /** 是否允许酒店自行创建房型：allow-允许，deny-禁止 */
    @Column(name = "allow_create_room_type", length = 20)
    private String allowCreateRoomType = "allow";
    
    /** 是否支持多价格体系（如：会员价、OTA价等多维度共存） */
    @Column(name = "support_multi_price", length = 10)
    private String supportMultiPrice = "no";
    
    /** 多价格体系配置选项（JSON 格式） */
    @Column(name = "multi_price_options", columnDefinition = "TEXT")
    private String multiPriceOptions;
    
    /** 是否支持房型差价自动计算 */
    @Column(name = "support_room_type_price_diff", length = 10)
    private String supportRoomTypePriceDiff = "no";
    
    /** 是否支持按人数计算差价 */
    @Column(name = "support_person_price_diff", length = 10)
    private String supportPersonPriceDiff = "no";
    
    /** 酒店运营状态：active-营业中，inactive-停业/禁用 */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 关联关系 ---------------------------------------------------------
    
    /** 酒店下属的所有房型定义 */
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomType> roomTypes;
    
    /** 房型差价策略配置 */
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomTypeDiffSystem> roomTypeDiffSystems;
    
    /** 人数差价策略配置 */
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonDiffSystem> personDiffSystems;
    
    /** 酒店库存记录 */
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories;
    
    /**
     * 运营状态枚举
     */
    public enum Status {
        /** 正常营业 */
        active, 
        /** 暂时关停 */
        inactive
    }
    
    /**
     * JPA 更新前回写时间。
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
    
    // Getter 和 Setter --------------------------------------------------
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getHotelCode() {
        return hotelCode;
    }
    
    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }
    
    public Integer getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getChineseName() {
        return chineseName;
    }
    
    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
    
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }
    
    public String getStarRating() {
        return starRating;
    }
    
    public void setStarRating(String starRating) {
        this.starRating = starRating;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getIntroduction() {
        return introduction;
    }
    
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    
    public Integer getTotalRooms() {
        return totalRooms;
    }
    
    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }
    
    public String getAllowCreateRateCode() {
        return allowCreateRateCode;
    }
    
    public void setAllowCreateRateCode(String allowCreateRateCode) {
        this.allowCreateRateCode = allowCreateRateCode;
    }
    
    public String getAllowCreateRoomType() {
        return allowCreateRoomType;
    }
    
    public void setAllowCreateRoomType(String allowCreateRoomType) {
        this.allowCreateRoomType = allowCreateRoomType;
    }
    
    public String getSupportMultiPrice() {
        return supportMultiPrice;
    }
    
    public void setSupportMultiPrice(String supportMultiPrice) {
        this.supportMultiPrice = supportMultiPrice;
    }
    
    public String getMultiPriceOptions() {
        return multiPriceOptions;
    }
    
    public void setMultiPriceOptions(String multiPriceOptions) {
        this.multiPriceOptions = multiPriceOptions;
    }
    
    public String getSupportRoomTypePriceDiff() {
        return supportRoomTypePriceDiff;
    }
    
    public void setSupportRoomTypePriceDiff(String supportRoomTypePriceDiff) {
        this.supportRoomTypePriceDiff = supportRoomTypePriceDiff;
    }
    
    public String getSupportPersonPriceDiff() {
        return supportPersonPriceDiff;
    }
    
    public void setSupportPersonPriceDiff(String supportPersonPriceDiff) {
        this.supportPersonPriceDiff = supportPersonPriceDiff;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
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
    
    public List<RoomType> getRoomTypes() {
        return roomTypes;
    }
    
    public void setRoomTypes(List<RoomType> roomTypes) {
        this.roomTypes = roomTypes;
    }
    
    public List<RoomTypeDiffSystem> getRoomTypeDiffSystems() {
        return roomTypeDiffSystems;
    }
    
    public void setRoomTypeDiffSystems(List<RoomTypeDiffSystem> roomTypeDiffSystems) {
        this.roomTypeDiffSystems = roomTypeDiffSystems;
    }
    
    public List<PersonDiffSystem> getPersonDiffSystems() {
        return personDiffSystems;
    }
    
    public void setPersonDiffSystems(List<PersonDiffSystem> personDiffSystems) {
        this.personDiffSystems = personDiffSystems;
    }
    
    public List<Inventory> getInventories() {
        return inventories;
    }
    
    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }
    
    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", hotelCode='" + hotelCode + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", status=" + status +
                '}';
    }
}

