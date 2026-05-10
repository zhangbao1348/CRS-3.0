package com.crs.service.inventory;

import java.time.LocalDate;
import java.util.List;

/**
 * AvailabilityResult 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【AvailabilityResult】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 AvailabilityResult 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
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
