package com.crs.service.impl;

import com.crs.entity.SourceCode;
import com.crs.repository.SourceCodeRepository;
import com.crs.service.SourceCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 来源码服务实现类
 */
@Service
public class SourceCodeServiceImpl implements SourceCodeService {

    @Autowired
    private SourceCodeRepository sourceCodeRepository;

    @Override
    public List<Map<String, Object>> getAllSourceCodesAsTree() {
        try {
            List<SourceCode> allSourceCodes = sourceCodeRepository.findAll();
            Map<Integer, SourceCode> sourceCodeMap = new HashMap<>();
            List<Map<String, Object>> treeData = new ArrayList<>();

            // 构建来源码映射
            for (SourceCode sourceCode : allSourceCodes) {
                sourceCodeMap.put(sourceCode.getId(), sourceCode);
            }

            // 构建树形结构
            for (SourceCode sourceCode : allSourceCodes) {
                if (sourceCode.getParentId() == null) {
                    // 根节点
                    Map<String, Object> rootNode = buildTreeNode(sourceCode);
                    rootNode.put("children", buildChildNodes(sourceCode.getId(), allSourceCodes));
                    treeData.add(rootNode);
                }
            }

            return treeData;
        } catch (Exception e) {
            // 如果数据库表不存在或有其他错误，返回默认的树形结构
            e.printStackTrace();
            return getDefaultSourceCodeTree();
        }
    }

    // 默认来源码树形结构
    private List<Map<String, Object>> getDefaultSourceCodeTree() {
        List<Map<String, Object>> treeData = new ArrayList<>();
        
        // 直接来源
        Map<String, Object> directSource = new HashMap<>();
        directSource.put("key", "1");
        directSource.put("title", "直接来源");
        directSource.put("code", "DIRECT_SOURCE");
        directSource.put("id", 1);
        
        List<Map<String, Object>> directChildren = new ArrayList<>();
        
        // 官网直接预订
        Map<String, Object> officialBooking = new HashMap<>();
        officialBooking.put("key", "2");
        officialBooking.put("title", "官网直接预订");
        officialBooking.put("code", "OFFICIAL_BOOKING");
        officialBooking.put("id", 2);
        
        List<Map<String, Object>> officialChildren = new ArrayList<>();
        officialChildren.add(createDefaultNode("3", "PC官网", "PC_WEBSITE", 3));
        officialChildren.add(createDefaultNode("4", "移动官网", "MOBILE_WEBSITE", 4));
        officialBooking.put("children", officialChildren);
        
        // 电话预订
        Map<String, Object> phoneBooking = new HashMap<>();
        phoneBooking.put("key", "5");
        phoneBooking.put("title", "电话预订");
        phoneBooking.put("code", "PHONE_BOOKING");
        phoneBooking.put("id", 5);
        
        List<Map<String, Object>> phoneChildren = new ArrayList<>();
        phoneChildren.add(createDefaultNode("6", "前台电话", "FRONT_DESK_PHONE", 6));
        phoneChildren.add(createDefaultNode("7", "预订中心", "RESERVATION_CENTER", 7));
        phoneBooking.put("children", phoneChildren);
        
        directChildren.add(officialBooking);
        directChildren.add(phoneBooking);
        directSource.put("children", directChildren);
        
        // 间接来源
        Map<String, Object> indirectSource = new HashMap<>();
        indirectSource.put("key", "8");
        indirectSource.put("title", "间接来源");
        indirectSource.put("code", "INDIRECT_SOURCE");
        indirectSource.put("id", 8);
        
        List<Map<String, Object>> indirectChildren = new ArrayList<>();
        
        // 搜索引擎
        Map<String, Object> searchEngine = new HashMap<>();
        searchEngine.put("key", "9");
        searchEngine.put("title", "搜索引擎");
        searchEngine.put("code", "SEARCH_ENGINE");
        searchEngine.put("id", 9);
        
        List<Map<String, Object>> searchChildren = new ArrayList<>();
        searchChildren.add(createDefaultNode("10", "百度", "BAIDU", 10));
        searchChildren.add(createDefaultNode("11", "谷歌", "GOOGLE", 11));
        searchChildren.add(createDefaultNode("12", "必应", "BING", 12));
        searchEngine.put("children", searchChildren);
        
        // 社交媒体
        Map<String, Object> socialMedia = new HashMap<>();
        socialMedia.put("key", "13");
        socialMedia.put("title", "社交媒体");
        socialMedia.put("code", "SOCIAL_MEDIA");
        socialMedia.put("id", 13);
        
        List<Map<String, Object>> socialChildren = new ArrayList<>();
        socialChildren.add(createDefaultNode("14", "微信", "WECHAT_SOURCE", 14));
        socialChildren.add(createDefaultNode("15", "微博", "WEIBO", 15));
        socialChildren.add(createDefaultNode("16", "抖音", "DOUYIN", 16));
        socialMedia.put("children", socialChildren);
        
        // 合作网站
        Map<String, Object> partnerWebsite = new HashMap<>();
        partnerWebsite.put("key", "17");
        partnerWebsite.put("title", "合作网站");
        partnerWebsite.put("code", "PARTNER_WEBSITE");
        partnerWebsite.put("id", 17);
        
        List<Map<String, Object>> partnerChildren = new ArrayList<>();
        partnerChildren.add(createDefaultNode("18", "旅游博客", "TRAVEL_BLOG", 18));
        partnerChildren.add(createDefaultNode("19", "酒店比价网站", "PRICE_COMPARISON", 19));
        partnerWebsite.put("children", partnerChildren);
        
        indirectChildren.add(searchEngine);
        indirectChildren.add(socialMedia);
        indirectChildren.add(partnerWebsite);
        indirectSource.put("children", indirectChildren);
        
        treeData.add(directSource);
        treeData.add(indirectSource);
        
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
    public List<SourceCode> getSourceCodesByParentId(Integer parentId) {
        return sourceCodeRepository.findByParentId(parentId);
    }

    @Override
    public SourceCode getSourceCodeById(Integer id) {
        return sourceCodeRepository.findById(id).orElse(null);
    }

    @Override
    public SourceCode createSourceCode(SourceCode sourceCode) {
        return sourceCodeRepository.save(sourceCode);
    }

    @Override
    public SourceCode updateSourceCode(SourceCode sourceCode) {
        return sourceCodeRepository.save(sourceCode);
    }

    @Override
    public void deleteSourceCode(Integer id) {
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
            SourceCode existing = sourceCodeRepository.findByCode(code);
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
            List<SourceCode> children = sourceCodeRepository.findByParentId(parentId);
            for (SourceCode child : children) {
                deleteRecursive(child.getId());
            }
            sourceCodeRepository.deleteById(parentId);
        } catch (Exception e) {
            // 如果数据库操作失败，直接返回
            e.printStackTrace();
        }
    }

    // 构建子节点
    private List<Map<String, Object>> buildChildNodes(Integer parentId, List<SourceCode> allSourceCodes) {
        List<Map<String, Object>> childNodes = new ArrayList<>();
        for (SourceCode sourceCode : allSourceCodes) {
            if (parentId.equals(sourceCode.getParentId())) {
                Map<String, Object> childNode = buildTreeNode(sourceCode);
                List<Map<String, Object>> grandchildren = buildChildNodes(sourceCode.getId(), allSourceCodes);
                if (!grandchildren.isEmpty()) {
                    childNode.put("children", grandchildren);
                }
                childNodes.add(childNode);
            }
        }
        return childNodes;
    }

    // 构建单个树节点
    private Map<String, Object> buildTreeNode(SourceCode sourceCode) {
        Map<String, Object> node = new HashMap<>();
        node.put("key", sourceCode.getId().toString());
        node.put("title", sourceCode.getName());
        node.put("code", sourceCode.getCode());
        node.put("id", sourceCode.getId());
        node.put("parentId", sourceCode.getParentId());
        node.put("level", sourceCode.getLevel());
        node.put("status", sourceCode.getStatus());
        return node;
    }
}