package com.crs.service.impl;

import com.crs.entity.MarketCode;
import com.crs.repository.MarketCodeRepository;
import com.crs.service.MarketCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<Map<String, Object>> getAllMarketCodesAsTree(Integer tenantId) {
        try {
            List<MarketCode> allMarketCodes = marketCodeRepository.findByTenantId(tenantId);
            Map<Integer, MarketCode> marketCodeMap = new HashMap<>();
            List<Map<String, Object>> treeData = new ArrayList<>();

            for (MarketCode marketCode : allMarketCodes) {
                marketCodeMap.put(marketCode.getId(), marketCode);
            }

            for (MarketCode marketCode : allMarketCodes) {
                if (marketCode.getParentId() == null) {
                    Map<String, Object> rootNode = buildTreeNode(marketCode);
                    rootNode.put("children", buildChildNodes(marketCode.getId(), allMarketCodes));
                    treeData.add(rootNode);
                }
            }

            return treeData;
        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultMarketCodeTree();
        }
    }

    private List<Map<String, Object>> getDefaultMarketCodeTree() {
        List<Map<String, Object>> treeData = new ArrayList<>();
        
        Map<String, Object> onlineMarket = new HashMap<>();
        onlineMarket.put("key", "1");
        onlineMarket.put("title", "线上市场");
        onlineMarket.put("code", "ONLINE");
        onlineMarket.put("id", 1);
        
        List<Map<String, Object>> onlineChildren = new ArrayList<>();
        
        Map<String, Object> otaPlatform = new HashMap<>();
        otaPlatform.put("key", "2");
        otaPlatform.put("title", "OTA平台");
        otaPlatform.put("code", "OTA");
        otaPlatform.put("id", 2);
        
        List<Map<String, Object>> otaChildren = new ArrayList<>();
        otaChildren.add(createDefaultNode("3", "携程", "CTRIP", 3));
        otaChildren.add(createDefaultNode("4", "美团", "MEITUAN", 4));
        otaChildren.add(createDefaultNode("5", "飞猪", "FLIGGY", 5));
        otaPlatform.put("children", otaChildren);
        
        Map<String, Object> directPlatform = new HashMap<>();
        directPlatform.put("key", "6");
        directPlatform.put("title", "直销平台");
        directPlatform.put("code", "DIRECT");
        directPlatform.put("id", 6);
        
        List<Map<String, Object>> directChildren = new ArrayList<>();
        directChildren.add(createDefaultNode("7", "官网预订", "OFFICIAL", 7));
        directChildren.add(createDefaultNode("8", "微信小程序", "WECHAT", 8));
        directPlatform.put("children", directChildren);
        
        onlineChildren.add(otaPlatform);
        onlineChildren.add(directPlatform);
        onlineMarket.put("children", onlineChildren);
        
        Map<String, Object> offlineMarket = new HashMap<>();
        offlineMarket.put("key", "9");
        offlineMarket.put("title", "线下市场");
        offlineMarket.put("code", "OFFLINE");
        offlineMarket.put("id", 9);
        
        List<Map<String, Object>> offlineChildren = new ArrayList<>();
        
        Map<String, Object> travelAgency = new HashMap<>();
        travelAgency.put("key", "10");
        travelAgency.put("title", "旅行社");
        travelAgency.put("code", "TRAVEL_AGENCY");
        travelAgency.put("id", 10);
        
        List<Map<String, Object>> agencyChildren = new ArrayList<>();
        agencyChildren.add(createDefaultNode("11", "国内旅行社", "DOMESTIC_TA", 11));
        agencyChildren.add(createDefaultNode("12", "国际旅行社", "INTERNATIONAL_TA", 12));
        travelAgency.put("children", agencyChildren);
        
        Map<String, Object> corporateClient = new HashMap<>();
        corporateClient.put("key", "13");
        corporateClient.put("title", "企业客户");
        corporateClient.put("code", "CORPORATE");
        corporateClient.put("id", 13);
        
        List<Map<String, Object>> corporateChildren = new ArrayList<>();
        corporateChildren.add(createDefaultNode("14", "本地企业", "LOCAL_CORP", 14));
        corporateChildren.add(createDefaultNode("15", "跨国企业", "MNC", 15));
        corporateClient.put("children", corporateChildren);
        
        offlineChildren.add(travelAgency);
        offlineChildren.add(corporateClient);
        offlineMarket.put("children", offlineChildren);
        
        treeData.add(onlineMarket);
        treeData.add(offlineMarket);
        
        return treeData;
    }

    private Map<String, Object> createDefaultNode(String key, String title, String code, Integer id) {
        Map<String, Object> node = new HashMap<>();
        node.put("key", key);
        node.put("title", title);
        node.put("code", code);
        node.put("id", id);
        return node;
    }

    @Override
    public List<MarketCode> getMarketCodesByParentId(Integer tenantId, Integer parentId) {
        return marketCodeRepository.findByTenantIdAndParentId(tenantId, parentId);
    }

    @Override
    public List<MarketCode> getThirdLevelMarketCodes(Integer tenantId) {
        try {
            // 从数据库获取第三级市场码
            List<MarketCode> thirdLevelCodes = marketCodeRepository.findByTenantIdAndLevel(tenantId, 3);
            return thirdLevelCodes;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    @Override
    public MarketCode getMarketCodeById(Integer tenantId, Integer id) {
        MarketCode marketCode = marketCodeRepository.findById(id).orElse(null);
        if (marketCode != null && marketCode.getTenantId() != null && marketCode.getTenantId().equals(tenantId)) {
            return marketCode;
        }
        return null;
    }

    @Override
    public MarketCode createMarketCode(Integer tenantId, MarketCode marketCode) {
        marketCode.setTenantId(tenantId);
        return marketCodeRepository.save(marketCode);
    }

    @Override
    public MarketCode updateMarketCode(Integer tenantId, MarketCode marketCode) {
        MarketCode existing = getMarketCodeById(tenantId, marketCode.getId());
        if (existing != null) {
            marketCode.setTenantId(tenantId);
            return marketCodeRepository.save(marketCode);
        }
        return null;
    }

    @Override
    public void deleteMarketCode(Integer tenantId, Integer id) {
        try {
            deleteRecursive(tenantId, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCodeUnique(Integer tenantId, String code, Integer excludeId) {
        try {
            MarketCode existing = marketCodeRepository.findByTenantIdAndCode(tenantId, code);
            return existing == null || (excludeId != null && existing.getId().equals(excludeId));
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private void deleteRecursive(Integer tenantId, Integer parentId) {
        try {
            List<MarketCode> children = marketCodeRepository.findByTenantIdAndParentId(tenantId, parentId);
            for (MarketCode child : children) {
                deleteRecursive(tenantId, child.getId());
            }
            marketCodeRepository.deleteById(parentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
