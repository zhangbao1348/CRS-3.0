package com.crs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 酒店实体类
 * 对应数据库hotels表
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "hotel_code", nullable = false, unique = true, length = 50)
    private String hotelCode;
    
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    @Column(name = "chinese_name", nullable = false, length = 100)
    private String chineseName;
    
    @Column(name = "english_name", nullable = false, length = 100)
    private String englishName;
    
    @Column(name = "star_rating", length = 10)
    private String starRating;
    
    @Column(name = "province", nullable = false, length = 50)
    private String province;
    
    @Column(name = "city", nullable = false, length = 50)
    private String city;
    
    @Column(name = "address", nullable = false, length = 200)
    private String address;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    
    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;
    
    @Column(name = "total_rooms")
    private Integer totalRooms;
    
    @Column(name = "allow_create_rate_code", length = 20)
    private String allowCreateRateCode = "allow";
    
    @Column(name = "allow_create_room_type", length = 20)
    private String allowCreateRoomType = "allow";
    
    @Column(name = "support_multi_price", length = 10)
    private String supportMultiPrice = "no";
    
    @Column(name = "multi_price_options", columnDefinition = "TEXT")
    private String multiPriceOptions;
    
    @Column(name = "support_room_type_price_diff", length = 10)
    private String supportRoomTypePriceDiff = "no";
    
    @Column(name = "support_person_price_diff", length = 10)
    private String supportPersonPriceDiff = "no";
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    
    // 关联关系
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomType> roomTypes;
    
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomTypeDiffSystem> roomTypeDiffSystems;
    
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonDiffSystem> personDiffSystems;
    
    // @JsonIgnore
    // @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<RatePlan> ratePlans;
    
    // @JsonIgnore
    // @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<BasePrice> basePrices;
    
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories;
    
    // 状态枚举
    public enum Status {
        active, inactive
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
    
    // Getter and Setter methods
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
    
    // public List<RatePlan> getRatePlans() {
    //     return ratePlans;
    // }
    
    // public void setRatePlans(List<RatePlan> ratePlans) {
    //     this.ratePlans = ratePlans;
    // }
    
    // public List<BasePrice> getBasePrices() {
    //     return basePrices;
    // }
    
    // public void setBasePrices(List<BasePrice> basePrices) {
    //     this.basePrices = basePrices;
    // }
    
    public List<Inventory> getInventories() {
        return inventories;
    }
    
    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }
    
    // toString method without associations
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
