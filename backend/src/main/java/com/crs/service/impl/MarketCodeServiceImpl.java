package com.crs.service.impl;

import com.crs.entity.MarketCode;
import com.crs.repository.MarketCodeRepository;
import com.crs.service.MarketCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MarketCodeServiceImpl 服务实现类 (Service Implementation)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【MarketCodeServiceImpl】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 MarketCodeServiceImpl 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class MarketCodeServiceImpl implements MarketCodeService {

    @Autowired
    private MarketCodeRepository marketCodeRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @Override
    public List<Map<String, Object>> getAllMarketCodesAsTree() {
        Integer currentTenantId = getCurrentTenantId();
        List<MarketCode> allMarketCodes = marketCodeRepository.findByTenantId(currentTenantId);
        List<Map<String, Object>> treeData = new ArrayList<>();

        for (MarketCode marketCode : allMarketCodes) {
            if (marketCode.getParentId() == null) {
                Map<String, Object> rootNode = buildTreeNode(marketCode);
                rootNode.put("children", buildChildNodes(marketCode.getId(), allMarketCodes));
                treeData.add(rootNode);
            }
        }

        return treeData;
    }

    @Override
    public List<MarketCode> getMarketCodesByParentId(Integer parentId) {
        return marketCodeRepository.findByTenantIdAndParentId(getCurrentTenantId(), parentId);
    }

    @Override
    public List<MarketCode> getThirdLevelMarketCodes() {
        return marketCodeRepository.findByTenantIdAndLevel(getCurrentTenantId(), 3);
    }

    @Override
    public MarketCode getMarketCodeById(Integer id) {
        return marketCodeRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElse(null);
    }

    @Override
    public MarketCode createMarketCode(MarketCode marketCode) {
        Integer tenantId = getCurrentTenantId();
        marketCode.setId(null);
        marketCode.setTenantId(tenantId);
        applyHierarchy(marketCode, tenantId);
        return marketCodeRepository.save(marketCode);
    }

    @Override
    public MarketCode updateMarketCode(MarketCode marketCode) {
        Integer currentTenantId = getCurrentTenantId();
        MarketCode existing = getMarketCodeById(marketCode.getId());
        if (existing != null) {
            existing.setCode(marketCode.getCode());
            existing.setName(marketCode.getName());
            if (marketCode.getDescription() != null) {
                existing.setDescription(marketCode.getDescription());
            }
            if (marketCode.getStatus() != null) {
                existing.setStatus(marketCode.getStatus());
            }
            return marketCodeRepository.save(existing);
        }
        return null;
    }

    /** 根据当前租户中的父节点计算树层级，树深度最多三级。 */
    private void applyHierarchy(MarketCode marketCode, Integer tenantId) {
        if (marketCode.getParentId() == null) {
            marketCode.setLevel(1);
            marketCode.setParentCode(null);
            return;
        }
        MarketCode parent = marketCodeRepository.findByIdAndTenantId(marketCode.getParentId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("父节点不存在或无权访问"));
        if (parent.getLevel() == null || parent.getLevel() >= 3) {
            throw new IllegalArgumentException("市场码最多支持三级结构");
        }
        marketCode.setLevel(parent.getLevel() + 1);
        marketCode.setParentCode(parent.getCode());
    }

    @Override
    @Transactional
    public void deleteMarketCode(Integer id) {
        Integer currentTenantId = getCurrentTenantId();
        // 验证根节点所有权
        MarketCode root = getMarketCodeById(id);
        if (root != null) {
            deleteRecursive(currentTenantId, id);
        }
    }

    @Override
    public boolean isCodeUnique(String code, Integer excludeId) {
        MarketCode existing = marketCodeRepository.findByTenantIdAndCode(getCurrentTenantId(), code);
        return existing == null || (excludeId != null && existing.getId().equals(excludeId));
    }

    private void deleteRecursive(Integer tenantId, Integer parentId) {
        List<MarketCode> children = marketCodeRepository.findByTenantIdAndParentId(tenantId, parentId);
        for (MarketCode child : children) {
            deleteRecursive(tenantId, child.getId());
        }
        // 最终删除操作也应带上 tenantId 验证
        marketCodeRepository.findByIdAndTenantId(parentId, tenantId)
            .ifPresent(marketCodeRepository::delete);
    }

    private List<Map<String, Object>> buildChildNodes(Integer parentId, List<MarketCode> allMarketCodes) {
        List<Map<String, Object>> childNodes = new ArrayList<>();
        for (MarketCode marketCode : allMarketCodes) {
            if (parentId.equals(marketCode.getParentId())) {
                Map<String, Object> childNode = buildTreeNode(marketCode);
                List<Map<String, Object>> grandchildren = buildChildNodes(marketCode.getId(), allMarketCodes);
                if (!grandchildren.isEmpty()) {
                    childNode.put("children", grandchildren);
                }
                childNodes.add(childNode);
            }
        }
        return childNodes;
    }

    private Map<String, Object> buildTreeNode(MarketCode marketCode) {
        Map<String, Object> node = new HashMap<>();
        node.put("key", marketCode.getId().toString());
        node.put("title", marketCode.getName());
        node.put("code", marketCode.getCode());
        node.put("id", marketCode.getId());
        node.put("parentId", marketCode.getParentId());
        node.put("level", marketCode.getLevel());
        node.put("status", marketCode.getStatus());
        return node;
    }
}
