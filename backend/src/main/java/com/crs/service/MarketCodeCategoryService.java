package com.crs.service;

import com.crs.entity.MarketCodeCategory;

import java.util.List;
import java.util.Map;

public interface MarketCodeCategoryService {
    
    List<Map<String, Object>> getAllMarketCodeCategories(Integer tenantId);
    
    MarketCodeCategory getMarketCodeCategoryById(Integer tenantId, Integer id);
    
    MarketCodeCategory createMarketCodeCategory(Integer tenantId, MarketCodeCategory marketCodeCategory);
    
    MarketCodeCategory updateMarketCodeCategory(Integer tenantId, MarketCodeCategory marketCodeCategory);
    
    void deleteMarketCodeCategory(Integer tenantId, Integer id);
    
    boolean isCodeUnique(Integer tenantId, String code, Integer excludeId);
}
