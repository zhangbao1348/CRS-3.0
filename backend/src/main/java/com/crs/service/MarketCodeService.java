package com.crs.service;

import com.crs.entity.MarketCode;

import java.util.List;
import java.util.Map;

/**
 * 市场码服务接口
 */
public interface MarketCodeService {

    /**
     * 获取所有市场码（树形结构）
     */
    List<Map<String, Object>> getAllMarketCodesAsTree();
    
    /**
     * 根据父ID获取市场码
     */
    List<MarketCode> getMarketCodesByParentId(Integer parentId);
    
    /**
     * 获取第三级市场码
     */
    List<MarketCode> getThirdLevelMarketCodes();
    
    /**
     * 根据ID获取市场码
     */
    MarketCode getMarketCodeById(Integer id);
    
    /**
     * 创建市场码
     */
    MarketCode createMarketCode(MarketCode marketCode);
    
    /**
     * 更新市场码
     */
    MarketCode updateMarketCode(MarketCode marketCode);
    
    /**
     * 删除市场码
     */
    void deleteMarketCode(Integer id);
    
    /**
     * 检查市场码CODE是否唯一
     */
    boolean isCodeUnique(String code, Integer excludeId);
}
