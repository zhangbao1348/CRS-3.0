package com.crs.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 酒店包价每日价格单日提交项。
 */
public class PackageDailyPriceRequest {

    private LocalDate priceDate;

    private BigDecimal salePrice;

    public LocalDate getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(LocalDate priceDate) {
        this.priceDate = priceDate;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
}
