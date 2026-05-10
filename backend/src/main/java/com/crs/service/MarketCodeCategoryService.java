package com.crs.service;

import com.crs.entity.MarketCodeCategory;

import java.util.List;
import java.util.Map;

/**
 * MarketCodeCategoryService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【MarketCodeCategoryService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 MarketCodeCategoryService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public interface MarketCodeCategoryService {
    
    List<Map<String, Object>> getAllMarketCodeCategories(Integer tenantId);
    
    MarketCodeCategory getMarketCodeCategoryById(Integer tenantId, Integer id);
    
    MarketCodeCategory createMarketCodeCategory(Integer tenantId, MarketCodeCategory marketCodeCategory);
    
    MarketCodeCategory updateMarketCodeCategory(Integer tenantId, MarketCodeCategory marketCodeCategory);
    
    void deleteMarketCodeCategory(Integer tenantId, Integer id);
    
    boolean isCodeUnique(Integer tenantId, String code, Integer excludeId);
}
