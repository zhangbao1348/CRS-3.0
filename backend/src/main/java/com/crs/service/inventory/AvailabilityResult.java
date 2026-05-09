package com.crs.service.inventory;

import java.time.LocalDate;
import java.util.List;

public class AvailabilityResult {
    private boolean available;
    private Integer availableCount;
    private String rejectReason;
    private List<DailyAvailability> dailyDetails;

    public static AvailabilityResult unavailable(String reason) {
        AvailabilityResult r = new AvailabilityResult();
        r.setAvailable(false);
        r.setAvailableCount(0);
        r.setRejectReason(reason);
        return r;
    }

    public static AvailabilityResult available(Integer count, List<DailyAvailability> details) {
        AvailabilityResult r = new AvailabilityResult();
        r.setAvailable(true);
        r.setAvailableCount(count);
        r.setDailyDetails(details);
        return r;
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public Integer getAvailableCount() { return availableCount; }
    public void setAvailableCount(Integer availableCount) { this.availableCount = availableCount; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public List<DailyAvailability> getDailyDetails() { return dailyDetails; }
    public void setDailyDetails(List<DailyAvailability> dailyDetails) { this.dailyDetails = dailyDetails; }

    public static class DailyAvailability {
        private LocalDate date;
        private Integer pmsAvailable;
        private Integer hotelAvailable;
        private Integer roomTypeOverbookCount;
        private Integer hotelOverbookCount;
        private Integer rateQuotaRemaining;
        private Integer channelQuotaRemaining;
        private Integer marketQuotaRemaining;
        private Integer channelRoomTypeQuotaRemaining;
        private Integer rateCategoryQuotaRemaining;
        private Integer minAvailable;
        private String rejectReason;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public Integer getPmsAvailable() { return pmsAvailable; }
        public void setPmsAvailable(Integer pmsAvailable) { this.pmsAvailable = pmsAvailable; }
        public Integer getHotelAvailable() { return hotelAvailable; }
        public void setHotelAvailable(Integer hotelAvailable) { this.hotelAvailable = hotelAvailable; }
        public Integer getRoomTypeOverbookCount() { return roomTypeOverbookCount; }
        public void setRoomTypeOverbookCount(Integer roomTypeOverbookCount) { this.roomTypeOverbookCount = roomTypeOverbookCount; }
        public Integer getHotelOverbookCount() { return hotelOverbookCount; }
        public void setHotelOverbookCount(Integer hotelOverbookCount) { this.hotelOverbookCount = hotelOverbookCount; }
        public Integer getRateQuotaRemaining() { return rateQuotaRemaining; }
        public void setRateQuotaRemaining(Integer rateQuotaRemaining) { this.rateQuotaRemaining = rateQuotaRemaining; }
        public Integer getChannelQuotaRemaining() { return channelQuotaRemaining; }
        public void setChannelQuotaRemaining(Integer channelQuotaRemaining) { this.channelQuotaRemaining = channelQuotaRemaining; }
        public Integer getMarketQuotaRemaining() { return marketQuotaRemaining; }
        public void setMarketQuotaRemaining(Integer marketQuotaRemaining) { this.marketQuotaRemaining = marketQuotaRemaining; }
        public Integer getChannelRoomTypeQuotaRemaining() { return channelRoomTypeQuotaRemaining; }
        public void setChannelRoomTypeQuotaRemaining(Integer channelRoomTypeQuotaRemaining) { this.channelRoomTypeQuotaRemaining = channelRoomTypeQuotaRemaining; }
        public Integer getRateCategoryQuotaRemaining() { return rateCategoryQuotaRemaining; }
        public void setRateCategoryQuotaRemaining(Integer rateCategoryQuotaRemaining) { this.rateCategoryQuotaRemaining = rateCategoryQuotaRemaining; }
        public Integer getMinAvailable() { return minAvailable; }
        public void setMinAvailable(Integer minAvailable) { this.minAvailable = minAvailable; }
        public String getRejectReason() { return rejectReason; }
        public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    }
}
