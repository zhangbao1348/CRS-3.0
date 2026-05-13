package com.crs.entity;


import jakarta.persistence.*;
import java.util.Date;

/**
 * 档案实体类
 * 对应数据库archives表
 */



@Entity
@Table(name = "archives")
public class Archive {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    @Column(name = "company_name", length = 200)
    private String companyName;
    
    @Column(name = "company_tax_number", length = 50)
    private String companyTaxNumber;
    
    @Column(name = "member_number", length = 50)
    private String memberNumber;
    
    @Column(name = "member_level", length = 50)
    private String memberLevel;
    
    @Column(name = "contact_name", length = 50)
    private String contactName;
    
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    @Column(name = "contact_email", length = 100)
    private String contactEmail;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "status", length = 20)
    private String status = "active";
    
    @Column(name = "group_id")
    private Integer groupId;
    
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
}
