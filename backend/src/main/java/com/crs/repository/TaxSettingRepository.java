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
     * 根据税率类型查询税率设置
     * @param taxType 税率类型
     * @return 税率设置列表
     */
    List<TaxSetting> findByTaxType(String taxType);
    
    /**
     * 根据状态查询税率设置
     * @param status 状态
     * @return 税率设置列表
     */
    List<TaxSetting> findByStatus(TaxSetting.Status status);
}
