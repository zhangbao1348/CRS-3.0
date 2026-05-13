package com.crs.entity;


import jakarta.persistence.*;

/**
 * 集团设施实体类
 * 对应数据库group_facilities表，存储集团级别的标准设施
 */



@Entity
@Table(name = "group_facilities")
public class GroupFacility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "facility_type", nullable = false, length = 50)
    private String facilityType;
    
    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;
    
    @Column(name = "facility_code", nullable = false, length = 50, unique = true)
    private String facilityCode;
    
    @Column(name = "scope", nullable = false, length = 20)
    private String scope = "hotel";
    
    @Column(name = "available", nullable = false)
    private Boolean available = true;
    
    @Column(name = "description", length = 255)
    private String description;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFacilityType() { return facilityType; }
    public void setFacilityType(String facilityType) { this.facilityType = facilityType; }
    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
    public String getFacilityCode() { return facilityCode; }
    public void setFacilityCode(String facilityCode) { this.facilityCode = facilityCode; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
