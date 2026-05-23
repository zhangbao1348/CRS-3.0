package com.crs.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 酒店包价每日价格批量保存请求。
 */
public class PackageDailyPriceBatchRequest {

    private String hotelCode;

    private List<PackageDailyPriceRequest> prices = new ArrayList<>();

    public String getHotelCode() {
        return hotelCode;
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public List<PackageDailyPriceRequest> getPrices() {
        return prices;
    }

    public void setPrices(List<PackageDailyPriceRequest> prices) {
        this.prices = prices;
    }
}
