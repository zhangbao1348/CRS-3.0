package com.crs.repository;

import com.crs.entity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Integer> {

    List<RatePlan> findByHotelId(Integer hotelId);

    List<RatePlan> findByHotelIdAndStatus(Integer hotelId, String status);

    Optional<RatePlan> findByHotelIdAndRateCode(Integer hotelId, String rateCode);

    List<RatePlan> findBySourceGroupRateCode(String sourceGroupRateCode);

    boolean existsByHotelIdAndRateCode(Integer hotelId, String rateCode);

    boolean existsByHotelIdAndRateCodeAndIdNot(Integer hotelId, String rateCode, Integer id);

    List<RatePlan> findByHotelCode(String hotelCode);

    List<RatePlan> findByHotelCodeAndParentRateCodeAndStatus(String hotelCode, String parentRateCode, String status);

    List<RatePlan> findByHotelCodeAndStatus(String hotelCode, String status);

    Optional<RatePlan> findByHotelCodeAndRateCode(String hotelCode, String rateCode);

    boolean existsByHotelCodeAndRateCode(String hotelCode, String rateCode);

    boolean existsByHotelCodeAndRateCodeAndIdNot(String hotelCode, String rateCode, Integer id);

    List<RatePlan> findBySourceGroupRateCodeAndHotelCode(String sourceGroupRateCode, String hotelCode);

    List<RatePlan> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    List<RatePlan> findByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, String status);

    List<RatePlan> findByTenantIdAndSourceGroupRateCode(Integer tenantId, String sourceGroupRateCode);

    Optional<RatePlan> findByTenantIdAndHotelCodeAndRateCode(Integer tenantId, String hotelCode, String rateCode);
}
