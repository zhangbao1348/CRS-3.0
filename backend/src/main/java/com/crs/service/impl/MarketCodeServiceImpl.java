package com.crs.service.impl;

import com.crs.entity.MarketCode;
import com.crs.repository.MarketCodeRepository;
import com.crs.service.MarketCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 市场码服务实现类
 */
@Service
public class MarketCodeServiceImpl implements MarketCodeService {

    @Autowired
    private MarketCodeRepository marketCodeRepository;

    @Override
    public List<Map<String, Object>> getAllMarketCodesAsTree() {
        try {
            List<MarketCode> allMarketCodes = marketCodeRepository.findAll();
            Map<Integer, MarketCode> marketCodeMap = new HashMap<>();
            List<Map<String, Object>> treeData = new ArrayList<>();

            // 构建市场码映射
            for (MarketCode marketCode : allMarketCodes) {
                marketCodeMap.put(marketCode.getId(), marketCode);
            }

            // 构建树形结构
            for (MarketCode marketCode : allMarketCodes) {
                if (marketCode.getParentId() == null) {
                    // 根节点
                    Map<String, Object> rootNode = buildTreeNode(marketCode);
                    rootNode.put("children", buildChildNodes(marketCode.getId(), allMarketCodes));
                    treeData.add(rootNode);
                }
            }

            return treeData;
        } catch (Exception e) {
            // 如果数据库表不存在或有其他错误，返回默认的树形结构
            e.printStackTrace();
            return getDefaultMarketCodeTree();
        }
    }

    // 默认市场码树形结构
    private List<Map<String, Object>> getDefaultMarketCodeTree() {
        List<Map<String, Object>> treeData = new ArrayList<>();
        
        // 线上市场
        Map<String, Object> onlineMarket = new HashMap<>();
        onlineMarket.put("key", "1");
        onlineMarket.put("title", "线上市场");
        onlineMarket.put("code", "ONLINE");
        onlineMarket.put("id", 1);
        
        List<Map<String, Object>> onlineChildren = new ArrayList<>();
        
        // OTA平台
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
        
        // 直销平台
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
        
        // 线下市场
        Map<String, Object> offlineMarket = new HashMap<>();
        offlineMarket.put("key", "9");
        offlineMarket.put("title", "线下市场");
        offlineMarket.put("code", "OFFLINE");
        offlineMarket.put("id", 9);
        
        List<Map<String, Object>> offlineChildren = new ArrayList<>();
        
        // 旅行社
        Map<String, Object> travelAgency = new HashMap<>();
        travelAgency.put("key", "10");
        travelAgency.put("title", "旅行社");
        travelAgency.put("code", "TRAVEL_AGENCY");
        travelAgency.put("id", 10);
        
        List<Map<String, Object>> agencyChildren = new ArrayList<>();
        agencyChildren.add(createDefaultNode("11", "国内旅行社", "DOMESTIC_TA", 11));
        agencyChildren.add(createDefaultNode("12", "国际旅行社", "INTERNATIONAL_TA", 12));
        travelAgency.put("children", agencyChildren);
        
        // 企业客户
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

    // 创建默认节点
    private Map<String, Object> createDefaultNode(String key, String title, String code, Integer id) {
        Map<String, Object> node = new HashMap<>();
        node.put("key", key);
        node.put("title", title);
        node.put("code", code);
        node.put("id", id);
        return node;
    }

    @Override
    public List<MarketCode> getMarketCodesByParentId(Integer parentId) {
        return marketCodeRepository.findByParentId(parentId);
    }

    @Override
    public MarketCode getMarketCodeById(Integer id) {
        return marketCodeRepository.findById(id).orElse(null);
    }

    @Override
    public MarketCode createMarketCode(MarketCode marketCode) {
        // 暂时不设置level，使用数据库默认值
        // 计算层级
        /*if (marketCode.getParentId() != null) {
            MarketCode parent = marketCodeRepository.findById(marketCode.getParentId()).orElse(null);
            if (parent != null) {
                marketCode.setLevel(parent.getLevel() + 1);
            } else {
                marketCode.setLevel(1);
            }
        } else {
            marketCode.setLevel(1);
        }*/
        return marketCodeRepository.save(marketCode);
    }

    @Override
    public MarketCode updateMarketCode(MarketCode marketCode) {
        return marketCodeRepository.save(marketCode);
    }

    @Override
    public void deleteMarketCode(Integer id) {
        try {
            // 递归删除子节点
            deleteRecursive(id);
        } catch (Exception e) {
            // 如果数据库操作失败，直接返回
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCodeUnique(String code, Integer excludeId) {
        try {
            MarketCode existing = marketCodeRepository.findByCode(code);
            return existing == null || (excludeId != null && existing.getId().equals(excludeId));
        } catch (Exception e) {
            // 如果数据库操作失败，直接返回true
            e.printStackTrace();
            return true;
        }
    }

    // 递归删除子节点
    private void deleteRecursive(Integer parentId) {
        try {
            List<MarketCode> children = marketCodeRepository.findByParentId(parentId);
            for (MarketCode child : children) {
                deleteRecursive(child.getId());
            }
            marketCodeRepository.deleteById(parentId);
        } catch (Exception e) {
            // 如果数据库操作失败，直接返回
            e.printStackTrace();
        }
    }

    // 构建子节点
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

    // 构建单个树节点
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
