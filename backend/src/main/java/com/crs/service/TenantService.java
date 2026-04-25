package com.crs.service;

import com.crs.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    
    List<Tenant> getAllTenants();
    
    Optional<Tenant> getTenantById(Integer id);
    
    Optional<Tenant> getTenantByCode(String tenantCode);
    
    Tenant createTenant(Tenant tenant);
    
    Tenant updateTenant(Integer id, Tenant tenant);
    
    void deleteTenant(Integer id);
    
    boolean existsByCode(String tenantCode);
}
