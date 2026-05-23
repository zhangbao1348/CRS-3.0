package com.crs.repository;

import com.crs.entity.PackageDailyPrice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 酒店包价每日价格数据访问接口。
 */
@Repository
public interface PackageDailyPriceRepository extends JpaRepository<PackageDailyPrice, Integer> {

    List<PackageDailyPrice> findByTenantIdAndHotelCodeAndPackageCodeAndPriceDateBetweenOrderByPriceDateAsc(
            Integer tenantId,
            String hotelCode,
            String packageCode,
            LocalDate startDate,
            LocalDate endDate);

    Optional<PackageDailyPrice> findByTenantIdAndHotelCodeAndPackageCodeAndPriceDate(
            Integer tenantId,
            String hotelCode,
            String packageCode,
            LocalDate priceDate);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM PackageDailyPrice p
        WHERE p.tenantId = :tenantId
          AND p.hotelCode = :hotelCode
          AND p.packageCode = :packageCode
          AND p.priceDate = :priceDate
        """)
    void deletePrice(
            @Param("tenantId") Integer tenantId,
            @Param("hotelCode") String hotelCode,
            @Param("packageCode") String packageCode,
            @Param("priceDate") LocalDate priceDate);
}
