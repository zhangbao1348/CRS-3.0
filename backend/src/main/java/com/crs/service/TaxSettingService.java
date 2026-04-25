package com.crs.service;

import com.crs.entity.TaxSetting;

import java.util.List;
import java.util.Optional;

/**
 * 税率设置服务接口
 * 用于税率设置的业务逻辑处理
 */
public interface TaxSettingService {
    
    /**
     * 根据租户ID获取所有税率设置
     * @param tenantId 租户ID
     * @return 税率设置列表
     */
    List<TaxSetting> getAllTaxSettings(Integer tenantId);
    
    /**
     * 根据ID获取税率设置
     * @param tenantId 租户ID
     * @param id 税率设置ID
     * @return 税率设置
     */
    Optional<TaxSetting> getById(Integer tenantId, Integer id);
    
    /**
     * 根据税率编码查询
     * @param tenantId 租户ID
     * @param taxCode 税率编码
     * @return 税率设置
     */
    TaxSetting getByTaxCode(Integer tenantId, String taxCode);
    
    /**
     * 创建税率设置
     * @param tenantId 租户ID
     * @param taxSetting 税率设置
     * @return 创建的税率设置
     */
    TaxSetting create(Integer tenantId, TaxSetting taxSetting);
    
    /**
     * 更新税率设置
     * @param tenantId 租户ID
     * @param id 税率设置ID
     * @param taxSetting 税率设置
     * @return 更新后的税率设置
     */
    TaxSetting update(Integer tenantId, Integer id, TaxSetting taxSetting);
    
    /**
     * 删除税率设置
     * @param tenantId 租户ID
     * @param id 税率设置ID
     */
    void delete(Integer tenantId, Integer id);
    
    /**
     * 检查税率编码是否唯一
     * @param tenantId 租户ID
     * @param taxCode 税率编码
     * @param excludeId 排除的ID
     * @return 是否唯一
     */
    boolean isTaxCodeUnique(Integer tenantId, String taxCode, Integer excludeId);
    
    /**
     * 批量创建税率设置
     * @param tenantId 租户ID
     * @param taxSettings 税率设置列表
     * @return 创建的税率设置列表
     */
    List<TaxSetting> batchCreateTaxSettings(Integer tenantId, List<TaxSetting> taxSettings);
    
    /**
     * 为指定租户初始化默认税率设置
     * @param tenantId 租户ID
     * @return 初始化的税率设置列表
     */
    List<TaxSetting> initDefaultTaxSettingsForTenant(Integer tenantId);
}
