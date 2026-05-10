package com.crs.service;

import com.crs.entity.Tenant;

import java.util.List;
import java.util.Optional;

/**
 * TenantService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【TenantService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 TenantService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public interface TenantService {
    
    List<Tenant> getAllTenants();
    
    Optional<Tenant> getTenantById(Integer id);
    
    Optional<Tenant> getTenantByCode(String tenantCode);
    
    Tenant createTenant(Tenant tenant);
    
    Tenant updateTenant(Integer id, Tenant tenant);
    
    void deleteTenant(Integer id);
    
    boolean existsByCode(String tenantCode);
}
