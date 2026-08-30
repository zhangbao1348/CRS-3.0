package com.crs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** 租户级集团业务设置，关联集团管控与渠道促销模块。 */
@Entity
@Table(name = "group_settings")
public class GroupSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private Integer tenantId;

    @Column(name = "group_control_mode", nullable = false, length = 20)
    private String groupControlMode = "strong";

    @Column(name = "hourly_room", nullable = false, length = 20)
    private String hourlyRoom = "support";

    @Column(name = "ota_promotion_mode", nullable = false, length = 50)
    private String otaPromotionMode = "groupRegistration";

    @Column(name = "show_ctrip_price", nullable = false)
    private boolean showCtripPrice;

    @Column(name = "show_meituan_price", nullable = false)
    private boolean showMeituanPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getGroupControlMode() { return groupControlMode; }
    public void setGroupControlMode(String groupControlMode) { this.groupControlMode = groupControlMode; }
    public String getHourlyRoom() { return hourlyRoom; }
    public void setHourlyRoom(String hourlyRoom) { this.hourlyRoom = hourlyRoom; }
    public String getOtaPromotionMode() { return otaPromotionMode; }
    public void setOtaPromotionMode(String otaPromotionMode) { this.otaPromotionMode = otaPromotionMode; }
    public boolean isShowCtripPrice() { return showCtripPrice; }
    public void setShowCtripPrice(boolean showCtripPrice) { this.showCtripPrice = showCtripPrice; }
    public boolean isShowMeituanPrice() { return showMeituanPrice; }
    public void setShowMeituanPrice(boolean showMeituanPrice) { this.showMeituanPrice = showMeituanPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
