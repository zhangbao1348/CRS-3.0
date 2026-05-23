package com.crs.service;

import com.crs.dto.PackageDailyPriceRequest;
import com.crs.entity.PackageDailyPrice;

import java.time.YearMonth;
import java.util.List;

/**
 * 酒店包价每日价格服务接口。
 */
public interface PackageDailyPriceService {

    List<PackageDailyPrice> getDailyPrices(String hotelCode, String packageCode, YearMonth month);

    List<PackageDailyPrice> saveDailyPrices(String hotelCode, String packageCode, List<PackageDailyPriceRequest> prices);
}
