package com.crs.service.impl;

import com.crs.dto.PackageDailyPriceRequest;
import com.crs.entity.Package;
import com.crs.entity.PackageDailyPrice;
import com.crs.repository.HotelRepository;
import com.crs.repository.PackageDailyPriceRepository;
import com.crs.repository.PackageRepository;
import com.crs.service.PackageDailyPriceService;
import com.crs.util.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * 酒店包价每日价格服务实现。
 */
@Service
@Transactional
public class PackageDailyPriceServiceImpl implements PackageDailyPriceService {

    private final PackageDailyPriceRepository packageDailyPriceRepository;
    private final PackageRepository packageRepository;
    private final HotelRepository hotelRepository;

    public PackageDailyPriceServiceImpl(
            PackageDailyPriceRepository packageDailyPriceRepository,
            PackageRepository packageRepository,
            HotelRepository hotelRepository) {
        this.packageDailyPriceRepository = packageDailyPriceRepository;
        this.packageRepository = packageRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDailyPrice> getDailyPrices(String hotelCode, String packageCode, YearMonth month) {
        Integer tenantId = getCurrentTenantId();
        String normalizedHotelCode = normalizeHotelCode(hotelCode);
        Package pkg = validateContext(tenantId, normalizedHotelCode, packageCode);

        if (!"daily".equals(pkg.getPriceType())) {
            return new ArrayList<>();
        }

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        return packageDailyPriceRepository
                .findByTenantIdAndHotelCodeAndPackageCodeAndPriceDateBetweenOrderByPriceDateAsc(
                        tenantId,
                        normalizedHotelCode,
                        packageCode,
                        startDate,
                        endDate);
    }

    @Override
    public List<PackageDailyPrice> saveDailyPrices(
            String hotelCode,
            String packageCode,
            List<PackageDailyPriceRequest> prices) {
        Integer tenantId = getCurrentTenantId();
        String normalizedHotelCode = normalizeHotelCode(hotelCode);
        Package pkg = validateContext(tenantId, normalizedHotelCode, packageCode);

        if (!"daily".equals(pkg.getPriceType())) {
            throw new IllegalArgumentException("当前包价不是按日期设置价格，无法维护每日价格");
        }

        if (prices == null || prices.isEmpty()) {
            return new ArrayList<>();
        }

        YearMonth targetMonth = null;
        for (PackageDailyPriceRequest request : prices) {
            if (request == null || request.getPriceDate() == null) {
                continue;
            }

            if (targetMonth == null) {
                targetMonth = YearMonth.from(request.getPriceDate());
            } else if (!targetMonth.equals(YearMonth.from(request.getPriceDate()))) {
                throw new IllegalArgumentException("每日价格保存必须限定在同一个月份内");
            }

            BigDecimal salePrice = request.getSalePrice();
            if (salePrice == null) {
                packageDailyPriceRepository.deletePrice(
                        tenantId,
                        normalizedHotelCode,
                        packageCode,
                        request.getPriceDate());
                continue;
            }

            if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("每日价格不能小于0");
            }

            PackageDailyPrice entity = packageDailyPriceRepository
                    .findByTenantIdAndHotelCodeAndPackageCodeAndPriceDate(
                            tenantId,
                            normalizedHotelCode,
                            packageCode,
                            request.getPriceDate())
                    .orElseGet(PackageDailyPrice::new);

            entity.setTenantId(tenantId);
            entity.setHotelCode(normalizedHotelCode);
            entity.setPackageCode(packageCode);
            entity.setPriceDate(request.getPriceDate());
            entity.setSalePrice(salePrice.setScale(2, java.math.RoundingMode.HALF_UP));
            packageDailyPriceRepository.save(entity);
        }

        if (targetMonth == null) {
            return new ArrayList<>();
        }

        return getDailyPrices(normalizedHotelCode, packageCode, targetMonth);
    }

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalArgumentException("租户上下文缺失");
        }
        return tenantId;
    }

    private String normalizeHotelCode(String hotelCode) {
        if (hotelCode == null || hotelCode.trim().isEmpty()) {
            throw new IllegalArgumentException("酒店编码不能为空");
        }
        return hotelCode.trim();
    }

    private Package validateContext(Integer tenantId, String hotelCode, String packageCode) {
        hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("当前酒店不存在或无权访问"));

        return packageRepository.findByTenantIdAndCode(tenantId, packageCode)
                .orElseThrow(() -> new IllegalArgumentException("当前包价不存在或无权访问"));
    }
}
