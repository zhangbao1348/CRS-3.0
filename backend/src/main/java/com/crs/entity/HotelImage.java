package com.crs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 酒店图片实体类 (HotelImage)
 * 
 * <p>本类对应数据库中的 `hotel_images` 表，存储酒店的外观、客房、公共区域等各类视觉素材信息。</p>
 * 
 * <p>核心属性：</p>
 * <ul>
 *     <li>**存储路径**：`imagePath` 记录图片在服务器或 CDN 上的访问地址。</li>
 *     <li>**图片分类**：通过 `imageType` 区分（如：外观、大堂、餐厅、客房等）。</li>
 *     <li>**展示顺序**：`sortOrder` 决定了图片在前端轮播或详情页的显示先后。</li>
 * </ul>
 */
@Entity
@Table(name = "hotel_images")
public class HotelImage {
    
    public HotelImage() {}

    public HotelImage(Integer id, Integer tenantId, String hotelCode, String imageType, String imagePath, String imageName, String description, Integer sortOrder, Date createdAt, Hotel hotel) {
        this.id = id;
        this.tenantId = tenantId;
        this.hotelCode = hotelCode;
        this.imageType = imageType;
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.description = description;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
        this.hotel = hotel;
    }

    
    /** 图片主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /** 所属租户 ID */
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    /** 酒店外部编码 */
    @Column(name = "hotel_code", length = 50)
    private String hotelCode;
    
    /** 
     * 图片类型
     * 可选值：exterior(外观), lobby(大堂), room(客房), restaurant(餐厅), other(其他)
     */
    @Column(name = "image_type", nullable = false, length = 50)
    private String imageType;
    
    /** 图片存储的绝对或相对 URL 地址 */
    @Column(name = "image_path", nullable = false, length = 255)
    private String imagePath;
    
    /** 图片文件名 */
    @Column(name = "image_name", nullable = false, length = 100)
    private String imageName;
    
    /** 图片简要说明 */
    @Column(name = "description", length = 200)
    private String description;
    
    /** 排序权重，数值越小越靠前 */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
    
    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    // 关联关系 ---------------------------------------------------------
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_code", referencedColumnName = "hotel_code", insertable = false, updatable = false)
    @JsonIgnore
    private Hotel hotel;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }
}