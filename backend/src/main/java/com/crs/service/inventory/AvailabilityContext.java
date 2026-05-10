package com.crs.service.inventory;

import java.time.LocalDate;

/**
 * AvailabilityContext 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【AvailabilityContext】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 AvailabilityContext 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public class AvailabilityContext {
    private Integer tenantId;
    private String hotelCode;
    private String roomTypeCode;
    private String rateCode;
    private String channelCode;
    private String marketCode;
    private String rateCategoryCode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer requestedRooms;

    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public String getHotelCode() { return hotelCode; }
    public void setHotelCode(String hotelCode) { this.hotelCode = hotelCode; }
    public String getRoomTypeCode() { return roomTypeCode; }
    public void setRoomTypeCode(String roomTypeCode) { this.roomTypeCode = roomTypeCode; }
    public String getRateCode() { return rateCode; }
    public void setRateCode(String rateCode) { this.rateCode = rateCode; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getMarketCode() { return marketCode; }
    public void setMarketCode(String marketCode) { this.marketCode = marketCode; }
    public String getRateCategoryCode() { return rateCategoryCode; }
    public void setRateCategoryCode(String rateCategoryCode) { this.rateCategoryCode = rateCategoryCode; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public Integer getRequestedRooms() { return requestedRooms; }
    public void setRequestedRooms(Integer requestedRooms) { this.requestedRooms = requestedRooms; }
}
