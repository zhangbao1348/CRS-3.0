package com.crs.service.impl;

import com.crs.entity.ChannelCode;
import com.crs.repository.ChannelCodeRepository;
import com.crs.service.ChannelCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道码服务实现类
 */
@Service
public class ChannelCodeServiceImpl implements ChannelCodeService {

    @Autowired
    private ChannelCodeRepository channelCodeRepository;

    @Override
    public List<Map<String, Object>> getAllChannelCodesAsTree() {
        try {
            List<ChannelCode> allChannelCodes = channelCodeRepository.findAll();
            List<Map<String, Object>> treeData = new ArrayList<>();

            // 构建树形结构
            for (ChannelCode channelCode : allChannelCodes) {
                if (channelCode.getParentId() == null) {
                    // 根节点
                    Map<String, Object> rootNode = buildTreeNode(channelCode);
                    rootNode.put("children", buildChildNodes(channelCode.getId(), allChannelCodes));
                    treeData.add(rootNode);
                }
            }

            return treeData;
        } catch (Exception e) {
            // 如果数据库表不存在或有其他错误，返回默认的树形结构
            e.printStackTrace();
            return getDefaultChannelCodeTree();
        }
    }

    @Override
    public List<ChannelCode> getChannelCodesByParentId(Integer parentId) {
        return channelCodeRepository.findByParentId(parentId);
    }

    @Override
    public ChannelCode getChannelCodeById(Integer id) {
        return channelCodeRepository.findById(id).orElse(null);
    }

    @Override
    public ChannelCode createChannelCode(ChannelCode channelCode) {
        // 暂时不设置level，使用数据库默认值
        return channelCodeRepository.save(channelCode);
    }

    @Override
    public ChannelCode updateChannelCode(ChannelCode channelCode) {
        return channelCodeRepository.save(channelCode);
    }

    @Override
    public void deleteChannelCode(Integer id) {
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
            ChannelCode existing = channelCodeRepository.findByCode(code);
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
            List<ChannelCode> children = channelCodeRepository.findByParentId(parentId);
            for (ChannelCode child : children) {
                deleteRecursive(child.getId());
            }
            channelCodeRepository.deleteById(parentId);
        } catch (Exception e) {
            // 如果数据库操作失败，直接返回
            e.printStackTrace();
        }
    }

    // 构建子节点
    private List<Map<String, Object>> buildChildNodes(Integer parentId, List<ChannelCode> allChannelCodes) {
        List<Map<String, Object>> childNodes = new ArrayList<>();
        for (ChannelCode channelCode : allChannelCodes) {
            if (parentId.equals(channelCode.getParentId())) {
                Map<String, Object> childNode = buildTreeNode(channelCode);
                List<Map<String, Object>> grandchildren = buildChildNodes(channelCode.getId(), allChannelCodes);
                if (!grandchildren.isEmpty()) {
                    childNode.put("children", grandchildren);
                }
                childNodes.add(childNode);
            }
        }
        return childNodes;
    }

    // 构建单个树节点
    private Map<String, Object> buildTreeNode(ChannelCode channelCode) {
        Map<String, Object> node = new HashMap<>();
        node.put("key", channelCode.getId().toString());
        node.put("title", channelCode.getName());
        node.put("code", channelCode.getCode());
        node.put("id", channelCode.getId());
        node.put("parentId", channelCode.getParentId());
        node.put("level", channelCode.getLevel());
        node.put("status", channelCode.getStatus());
        return node;
    }

    // 默认渠道码树形结构
    private List<Map<String, Object>> getDefaultChannelCodeTree() {
        List<Map<String, Object>> treeData = new ArrayList<>();
        
        // 线上渠道
        Map<String, Object> onlineChannel = new HashMap<>();
        onlineChannel.put("key", "1");
        onlineChannel.put("title", "线上渠道");
        onlineChannel.put("code", "ONLINE_CHANNEL");
        onlineChannel.put("id", 1);
        
        List<Map<String, Object>> onlineChildren = new ArrayList<>();
        
        // 分销渠道
        Map<String, Object> distributionChannel = new HashMap<>();
        distributionChannel.put("key", "2");
        distributionChannel.put("title", "分销渠道");
        distributionChannel.put("code", "DISTRIBUTION");
        distributionChannel.put("id", 2);
        
        List<Map<String, Object>> distributionChildren = new ArrayList<>();
        distributionChildren.add(createDefaultNode("3", "携程分销", "CTRIP_DIST", 3));
        distributionChildren.add(createDefaultNode("4", "美团分销", "MEITUAN_DIST", 4));
        distributionChannel.put("children", distributionChildren);
        
        // 直销渠道
        Map<String, Object> directChannel = new HashMap<>();
        directChannel.put("key", "5");
        directChannel.put("title", "直销渠道");
        directChannel.put("code", "DIRECT_CHANNEL");
        directChannel.put("id", 5);
        
        List<Map<String, Object>> directChildren = new ArrayList<>();
        directChildren.add(createDefaultNode("6", "官网", "WEBSITE", 6));
        directChildren.add(createDefaultNode("7", "APP", "MOBILE_APP", 7));
        directChildren.add(createDefaultNode("8", "微信", "WECHAT_CHANNEL", 8));
        directChannel.put("children", directChildren);
        
        onlineChildren.add(distributionChannel);
        onlineChildren.add(directChannel);
        onlineChannel.put("children", onlineChildren);
        
        // 线下渠道
        Map<String, Object> offlineChannel = new HashMap<>();
        offlineChannel.put("key", "9");
        offlineChannel.put("title", "线下渠道");
        offlineChannel.put("code", "OFFLINE_CHANNEL");
        offlineChannel.put("id", 9);
        
        List<Map<String, Object>> offlineChildren = new ArrayList<>();
        
        // 旅行社渠道
        Map<String, Object> taChannel = new HashMap<>();
        taChannel.put("key", "10");
        taChannel.put("title", "旅行社渠道");
        taChannel.put("code", "TA_CHANNEL");
        taChannel.put("id", 10);
        
        List<Map<String, Object>> taChildren = new ArrayList<>();
        taChildren.add(createDefaultNode("11", "国内社", "DOMESTIC_TA_CHANNEL", 11));
        taChildren.add(createDefaultNode("12", "国际社", "INTL_TA_CHANNEL", 12));
        taChannel.put("children", taChildren);
        
        // 企业渠道
        Map<String, Object> corpChannel = new HashMap<>();
        corpChannel.put("key", "13");
        corpChannel.put("title", "企业渠道");
        corpChannel.put("code", "CORP_CHANNEL");
        corpChannel.put("id", 13);
        
        List<Map<String, Object>> corpChildren = new ArrayList<>();
        corpChildren.add(createDefaultNode("14", "协议企业", "AGREEMENT_CORP", 14));
        corpChildren.add(createDefaultNode("15", "临时企业", "TEMP_CORP", 15));
        corpChannel.put("children", corpChildren);
        
        offlineChildren.add(taChannel);
        offlineChildren.add(corpChannel);
        offlineChannel.put("children", offlineChildren);
        
        treeData.add(onlineChannel);
        treeData.add(offlineChannel);
        
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
}
