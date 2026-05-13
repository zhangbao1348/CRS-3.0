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

/**
 * MarketCodeCategoryServiceImpl 服务实现类 (Service Implementation)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【MarketCodeCategoryServiceImpl】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 MarketCodeCategoryServiceImpl 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class MarketCodeCategoryServiceImpl implements MarketCodeCategoryService {

    @Autowired
    private MarketCodeCategoryRepository marketCodeCategoryRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @Override
    public List<Map<String, Object>> getAllMarketCodeCategories() {
        try {
            List<MarketCodeCategory> categories = marketCodeCategoryRepository.findByTenantId(getCurrentTenantId());
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
            return new ArrayList<>();
        }
    }

    @Override
    public MarketCodeCategory getMarketCodeCategoryById(Integer id) {
        Integer currentTenantId = getCurrentTenantId();
        return marketCodeCategoryRepository.findById(id)
                .filter(c -> c.getTenantId() != null && c.getTenantId().equals(currentTenantId))
                .orElse(null);
    }

    @Override
    public MarketCodeCategory createMarketCodeCategory(MarketCodeCategory marketCodeCategory) {
        marketCodeCategory.setTenantId(getCurrentTenantId());
        return marketCodeCategoryRepository.save(marketCodeCategory);
    }

    @Override
    public MarketCodeCategory updateMarketCodeCategory(MarketCodeCategory marketCodeCategory) {
        Integer currentTenantId = getCurrentTenantId();
        MarketCodeCategory existing = getMarketCodeCategoryById(marketCodeCategory.getId());
        if (existing != null) {
            marketCodeCategory.setTenantId(currentTenantId);
            return marketCodeCategoryRepository.save(marketCodeCategory);
        }
        return null;
    }

    @Override
    public void deleteMarketCodeCategory(Integer id) {
        MarketCodeCategory existing = getMarketCodeCategoryById(id);
        if (existing != null) {
            marketCodeCategoryRepository.deleteById(id);
        }
    }

    @Override
    public boolean isCodeUnique(String code, Integer excludeId) {
        MarketCodeCategory existing = marketCodeCategoryRepository.findByTenantIdAndCode(getCurrentTenantId(), code);
        return existing == null || (excludeId != null && existing.getId().equals(excludeId));
    }
}
