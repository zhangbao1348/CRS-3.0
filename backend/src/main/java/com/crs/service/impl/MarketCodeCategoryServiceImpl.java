package com.crs.service.impl;

import com.crs.entity.MarketCodeCategory;
import com.crs.repository.MarketCodeCategoryRepository;
import com.crs.service.MarketCodeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketCodeCategoryServiceImpl implements MarketCodeCategoryService {

    @Autowired
    private MarketCodeCategoryRepository marketCodeCategoryRepository;

    @Override
    public List<Map<String, Object>> getAllMarketCodeCategories(Integer tenantId) {
        try {
            List<MarketCodeCategory> categories = marketCodeCategoryRepository.findByTenantId(tenantId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (MarketCodeCategory category : categories) {
                Map<String, Object> node = new HashMap<>();
                node.put("key", category.getId().toString());
                node.put("title", category.getName());
                node.put("code", category.getCode());
                node.put("id", category.getId());
                node.put("status", category.getStatus());
                result.add(node);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultMarketCodeCategories();
        }
    }

    private List<Map<String, Object>> getDefaultMarketCodeCategories() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String[] codes = {"DIRECT", "OTA", "CORPORATE", "TRAVEL_AGENCY", "MEMBER", "PROMO"};
        String[] names = {"直接预订", "OTA渠道", "企业客户", "旅行社", "会员预订", "促销活动"};
        
        for (int i = 0; i < codes.length; i++) {
            Map<String, Object> node = new HashMap<>();
            node.put("key", String.valueOf(i + 1));
            node.put("title", names[i]);
            node.put("code", codes[i]);
            node.put("id", i + 1);
            result.add(node);
        }
        
        return result;
    }

    @Override
    public MarketCodeCategory getMarketCodeCategoryById(Integer tenantId, Integer id) {
        MarketCodeCategory category = marketCodeCategoryRepository.findById(id).orElse(null);
        if (category != null && category.getTenantId() != null && category.getTenantId().equals(tenantId)) {
            return category;
        }
        return null;
    }

    @Override
    public MarketCodeCategory createMarketCodeCategory(Integer tenantId, MarketCodeCategory marketCodeCategory) {
        marketCodeCategory.setTenantId(tenantId);
        return marketCodeCategoryRepository.save(marketCodeCategory);
    }

    @Override
    public MarketCodeCategory updateMarketCodeCategory(Integer tenantId, MarketCodeCategory marketCodeCategory) {
        MarketCodeCategory existing = getMarketCodeCategoryById(tenantId, marketCodeCategory.getId());
        if (existing != null) {
            marketCodeCategory.setTenantId(tenantId);
            return marketCodeCategoryRepository.save(marketCodeCategory);
        }
        return null;
    }

    @Override
    public void deleteMarketCodeCategory(Integer tenantId, Integer id) {
        MarketCodeCategory existing = getMarketCodeCategoryById(tenantId, id);
        if (existing != null) {
            marketCodeCategoryRepository.deleteById(id);
        }
    }

    @Override
    public boolean isCodeUnique(Integer tenantId, String code, Integer excludeId) {
        try {
            MarketCodeCategory existing = marketCodeCategoryRepository.findByTenantIdAndCode(tenantId, code);
            return existing == null || (excludeId != null && existing.getId().equals(excludeId));
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
