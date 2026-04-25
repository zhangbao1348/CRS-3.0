package com.crs.repository;

import com.crs.entity.TaxSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 税率设置仓库接口
 * 用于税率设置数据的CRUD操作
 */
@Repository
public interface TaxSettingRepository extends JpaRepository<TaxSetting, Integer> {
    
    /**
     * 根据租户ID查询所有税率设置
     * @param tenantId 租户ID
     * @return 税率设置列表
     */
    List<TaxSetting> findByTenantId(Integer tenantId);
    
    /**
     * 根据租户ID和税率编码查询
     * @param tenantId 租户ID
     * @param taxCode 税率编码
     * @return 税率设置
     */
    TaxSetting findByTenantIdAndTaxCode(Integer tenantId, String taxCode);
    
    /**
     * 根据租户ID和状态查询
     * @param tenantId 租户ID
     * @param status 状态
     * @return 税率设置列表
     */
    List<TaxSetting> findByTenantIdAndStatus(Integer tenantId, String status);
}
