package com.crs.service.inventory;

import java.time.LocalDate;

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
