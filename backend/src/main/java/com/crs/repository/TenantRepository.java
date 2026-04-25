package com.crs.repository;

import com.crs.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    
    Optional<Tenant> findByTenantCode(String tenantCode);
    
    boolean existsByTenantCode(String tenantCode);
}
