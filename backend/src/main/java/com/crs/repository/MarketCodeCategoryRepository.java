package com.crs.repository;

import com.crs.entity.MarketCodeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketCodeCategoryRepository extends JpaRepository<MarketCodeCategory, Integer> {
    
    List<MarketCodeCategory> findByTenantId(Integer tenantId);
    
    MarketCodeCategory findByTenantIdAndCode(Integer tenantId, String code);
}
