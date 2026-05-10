package com.crs.service.impl;

import com.crs.entity.Tenant;
import com.crs.repository.TenantRepository;
import com.crs.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * TenantServiceImpl 服务实现类 (Service Implementation)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【TenantServiceImpl】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 TenantServiceImpl 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
@Transactional
public class TenantServiceImpl implements TenantService {
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
    
    @Override
    public Optional<Tenant> getTenantById(Integer id) {
        return tenantRepository.findById(id);
    }
    
    @Override
    public Optional<Tenant> getTenantByCode(String tenantCode) {
        return tenantRepository.findByTenantCode(tenantCode);
    }
    
    @Override
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }
    
    @Override
    public Tenant updateTenant(Integer id, Tenant tenant) {
        return tenantRepository.findById(id).map(existingTenant -> {
            if (tenant.getTenantCode() != null) {
                existingTenant.setTenantCode(tenant.getTenantCode());
            }
            if (tenant.getTenantName() != null) {
                existingTenant.setTenantName(tenant.getTenantName());
            }
            if (tenant.getStatus() != null) {
                existingTenant.setStatus(tenant.getStatus());
            }
            if (tenant.getExpireDate() != null) {
                existingTenant.setExpireDate(tenant.getExpireDate());
            }
            if (tenant.getContactName() != null) {
                existingTenant.setContactName(tenant.getContactName());
            }
            if (tenant.getContactPhone() != null) {
                existingTenant.setContactPhone(tenant.getContactPhone());
            }
            if (tenant.getContactEmail() != null) {
                existingTenant.setContactEmail(tenant.getContactEmail());
            }
            if (tenant.getHotelCount() != null) {
                existingTenant.setHotelCount(tenant.getHotelCount());
            }
            if (tenant.getAddress() != null) {
                existingTenant.setAddress(tenant.getAddress());
            }
            return tenantRepository.save(existingTenant);
        }).orElse(null);
    }
    
    @Override
    public void deleteTenant(Integer id) {
        tenantRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByCode(String tenantCode) {
        return tenantRepository.existsByTenantCode(tenantCode);
    }
}
