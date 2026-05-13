package com.crs.service.impl;

import com.crs.entity.ChannelCode;
import com.crs.repository.ChannelCodeRepository;
import com.crs.service.ChannelCodeService;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChannelCodeServiceImpl 服务实现类 (Service Implementation)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【ChannelCodeServiceImpl】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/13-渠道管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 ChannelCodeServiceImpl 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class ChannelCodeServiceImpl implements ChannelCodeService {

    @Autowired
    private ChannelCodeRepository channelCodeRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @Override
    public List<Map<String, Object>> getAllChannelCodesAsTree() {
        try {
            List<ChannelCode> allChannelCodes = channelCodeRepository.findByTenantId(getCurrentTenantId());
            List<Map<String, Object>> treeData = new ArrayList<>();

            for (ChannelCode channelCode : allChannelCodes) {
                if (channelCode.getParentId() == null) {
                    Map<String, Object> rootNode = buildTreeNode(channelCode);
                    rootNode.put("children", buildChildNodes(channelCode.getId(), allChannelCodes));
                    treeData.add(rootNode);
                }
            }

            return treeData;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<ChannelCode> getChannelCodesByParentId(Integer tenantId, Integer parentId) {
        return channelCodeRepository.findByTenantIdAndParentId(getCurrentTenantId(), parentId);
    }

    @Override
    public ChannelCode getChannelCodeById(Integer id) {
        return channelCodeRepository.findByTenantIdAndId(getCurrentTenantId(), id);
    }

    @Override
    @Transactional
    public ChannelCode createChannelCode(ChannelCode channelCode) {
        Integer tenantId = getCurrentTenantId();
        channelCode.setTenantId(tenantId);
        
        if (channelCode.getParentId() != null) {
            ChannelCode parent = channelCodeRepository.findByTenantIdAndId(tenantId, channelCode.getParentId());
            if (parent != null) {
                channelCode.setLevel(parent.getLevel() + 1);
            } else {
                channelCode.setLevel(1);
            }
        } else {
            channelCode.setLevel(1);
        }
        
        return channelCodeRepository.save(channelCode);
    }

    @Override
    @Transactional
    public ChannelCode updateChannelCode(ChannelCode channelCode) {
        Integer tenantId = getCurrentTenantId();
        ChannelCode existing = channelCodeRepository.findByTenantIdAndId(tenantId, channelCode.getId());
        if (existing != null) {
            existing.setName(channelCode.getName());
            existing.setCode(channelCode.getCode());
            existing.setDescription(channelCode.getDescription());
            existing.setStatus(channelCode.getStatus());
            return channelCodeRepository.save(existing);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteChannelCode(Integer id) {
        Integer tenantId = getCurrentTenantId();
        try {
            ChannelCode channelCode = channelCodeRepository.findByTenantIdAndId(tenantId, id);
            if (channelCode != null) {
                deleteRecursive(id, tenantId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCodeUnique(Integer tenantId, String code, Integer excludeId) {
        Integer currentTenantId = getCurrentTenantId();
        try {
            if (excludeId != null) {
                return !channelCodeRepository.existsByTenantIdAndCodeAndIdNot(currentTenantId, code, excludeId);
            } else {
                ChannelCode existing = channelCodeRepository.findByTenantIdAndCode(currentTenantId, code);
                return existing == null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private void deleteRecursive(Integer parentId, Integer tenantId) {
        try {
            List<ChannelCode> children = channelCodeRepository.findByTenantIdAndParentId(tenantId, parentId);
            for (ChannelCode child : children) {
                deleteRecursive(child.getId(), tenantId);
            }
            // 确保删除操作携带租户过滤
            ChannelCode cc = channelCodeRepository.findByTenantIdAndId(tenantId, parentId);
            if (cc != null) {
                channelCodeRepository.delete(cc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> buildTreeNode(ChannelCode channelCode) {
        Map<String, Object> node = new HashMap<>();
        node.put("key", channelCode.getId().toString());
        node.put("title", channelCode.getName());
        node.put("id", channelCode.getId());
        node.put("code", channelCode.getCode());
        node.put("name", channelCode.getName());
        node.put("description", channelCode.getDescription());
        node.put("parentId", channelCode.getParentId());
        node.put("level", channelCode.getLevel());
        node.put("status", channelCode.getStatus());
        return node;
    }

    private List<Map<String, Object>> buildChildNodes(Integer parentId, List<ChannelCode> allChannelCodes) {
        List<Map<String, Object>> children = new ArrayList<>();
        for (ChannelCode channelCode : allChannelCodes) {
            if (parentId.equals(channelCode.getParentId())) {
                Map<String, Object> childNode = buildTreeNode(channelCode);
                childNode.put("children", buildChildNodes(channelCode.getId(), allChannelCodes));
                children.add(childNode);
            }
        }
        return children;
    }

    @Override
    @Transactional
    public List<ChannelCode> batchCreateChannelCodes(Integer tenantId, List<ChannelCode> channelCodes) {
        Integer currentTenantId = getCurrentTenantId();
        List<ChannelCode> savedChannelCodes = new ArrayList<>();
        
        for (ChannelCode channelCode : channelCodes) {
            channelCode.setTenantId(currentTenantId);
            
            if (channelCode.getParentId() != null) {
                ChannelCode parent = channelCodeRepository.findByTenantIdAndId(currentTenantId, channelCode.getParentId());
                if (parent != null) {
                    channelCode.setLevel(parent.getLevel() + 1);
                } else {
                    channelCode.setLevel(1);
                }
            } else {
                channelCode.setLevel(1);
            }
            
            savedChannelCodes.add(channelCodeRepository.save(channelCode));
        }
        
        return savedChannelCodes;
    }

    @Override
    @Transactional
    public List<ChannelCode> initDefaultChannelCodesForTenant(Integer tenantId) {
        Integer currentTenantId = getCurrentTenantId();
        List<ChannelCode> defaultCodes = new ArrayList<>();
        
        ChannelCode online = new ChannelCode();
        online.setTenantId(currentTenantId);
        online.setCode("ONLINE");
        online.setName("在线渠道");
        online.setDescription("在线销售渠道");
        online.setParentId(null);
        online.setLevel(1);
        online.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(online));
        
        ChannelCode offline = new ChannelCode();
        offline.setTenantId(currentTenantId);
        offline.setCode("OFFLINE");
        offline.setName("线下渠道");
        offline.setDescription("线下销售渠道");
        offline.setParentId(null);
        offline.setLevel(1);
        offline.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(offline));
        
        ChannelCode ota = new ChannelCode();
        ota.setTenantId(currentTenantId);
        ota.setCode("OTA");
        ota.setName("OTA渠道");
        ota.setDescription("在线旅行社渠道");
        ota.setParentId(online.getId());
        ota.setLevel(2);
        ota.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(ota));
        
        ChannelCode direct = new ChannelCode();
        direct.setTenantId(currentTenantId);
        direct.setCode("DIRECT");
        direct.setName("直销渠道");
        direct.setDescription("直接销售渠道");
        direct.setParentId(online.getId());
        direct.setLevel(2);
        direct.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(direct));
        
        ChannelCode travel = new ChannelCode();
        travel.setTenantId(currentTenantId);
        travel.setCode("TRAVEL");
        travel.setName("旅行社");
        travel.setDescription("旅行社渠道");
        travel.setParentId(offline.getId());
        travel.setLevel(2);
        travel.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(travel));
        
        ChannelCode corp = new ChannelCode();
        corp.setTenantId(currentTenantId);
        corp.setCode("CORP");
        corp.setName("企业协议");
        corp.setDescription("企业协议渠道");
        corp.setParentId(offline.getId());
        corp.setLevel(2);
        corp.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(corp));
        
        ChannelCode ctrip = new ChannelCode();
        ctrip.setTenantId(currentTenantId);
        ctrip.setCode("CTRIP");
        ctrip.setName("携程");
        ctrip.setDescription("携程旅行网");
        ctrip.setParentId(ota.getId());
        ctrip.setLevel(3);
        ctrip.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(ctrip));
        
        ChannelCode meituan = new ChannelCode();
        meituan.setTenantId(currentTenantId);
        meituan.setCode("MEITUAN");
        meituan.setName("美团");
        meituan.setDescription("美团酒店");
        meituan.setParentId(ota.getId());
        meituan.setLevel(3);
        meituan.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(meituan));
        
        ChannelCode fliggy = new ChannelCode();
        fliggy.setTenantId(currentTenantId);
        fliggy.setCode("FLIGGY");
        fliggy.setName("飞猪");
        fliggy.setDescription("飞猪旅行");
        fliggy.setParentId(ota.getId());
        fliggy.setLevel(3);
        fliggy.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(fliggy));
        
        ChannelCode website = new ChannelCode();
        website.setTenantId(currentTenantId);
        website.setCode("WEBSITE");
        website.setName("官网");
        website.setDescription("官方网站");
        website.setParentId(direct.getId());
        website.setLevel(3);
        website.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(website));
        
        ChannelCode app = new ChannelCode();
        app.setTenantId(currentTenantId);
        app.setCode("APP");
        app.setName("APP");
        app.setDescription("手机应用");
        app.setParentId(direct.getId());
        app.setLevel(3);
        app.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(app));
        
        ChannelCode callcenter = new ChannelCode();
        callcenter.setTenantId(currentTenantId);
        callcenter.setCode("CALLCENTER");
        callcenter.setName("呼叫中心");
        callcenter.setDescription("电话预订");
        callcenter.setParentId(direct.getId());
        callcenter.setLevel(3);
        callcenter.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(callcenter));
        
        ChannelCode fortune500 = new ChannelCode();
        fortune500.setTenantId(currentTenantId);
        fortune500.setCode("FORTUNE500");
        fortune500.setName("世界500强");
        fortune500.setDescription("世界500强企业协议");
        fortune500.setParentId(corp.getId());
        fortune500.setLevel(3);
        fortune500.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(fortune500));
        
        ChannelCode gov = new ChannelCode();
        gov.setTenantId(currentTenantId);
        gov.setCode("GOV");
        gov.setName("政府协议");
        gov.setDescription("政府机关协议");
        gov.setParentId(corp.getId());
        gov.setLevel(3);
        gov.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(gov));
        
        ChannelCode mice = new ChannelCode();
        mice.setTenantId(currentTenantId);
        mice.setCode("MICE");
        mice.setName("MICE协议");
        mice.setDescription("会议展览协议");
        mice.setParentId(corp.getId());
        mice.setLevel(3);
        mice.setStatus(ChannelCode.Status.active);
        defaultCodes.add(channelCodeRepository.save(mice));
        
        return defaultCodes;
    }
}