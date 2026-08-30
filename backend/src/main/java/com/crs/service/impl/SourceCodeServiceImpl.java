package com.crs.service.impl;

import com.crs.entity.SourceCode;
import com.crs.repository.SourceCodeRepository;
import com.crs.service.SourceCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @Override
    public List<Map<String, Object>> getAllSourceCodesAsTree() {
        Integer currentTenantId = getCurrentTenantId();
        List<SourceCode> allSourceCodes = sourceCodeRepository.findByTenantId(currentTenantId);
        List<Map<String, Object>> treeData = new ArrayList<>();

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
    }

    @Override
    public List<SourceCode> getSourceCodesByParentId(Integer parentId) {
        return sourceCodeRepository.findByTenantIdAndParentId(getCurrentTenantId(), parentId);
    }

    @Override
    public List<SourceCode> getThirdLevelSourceCodes() {
        return sourceCodeRepository.findByTenantIdAndLevel(getCurrentTenantId(), 3);
    }

    @Override
    public SourceCode getSourceCodeById(Integer id) {
        return sourceCodeRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElse(null);
    }

    @Override
    public SourceCode createSourceCode(SourceCode sourceCode) {
        Integer tenantId = getCurrentTenantId();
        sourceCode.setId(null);
        sourceCode.setTenantId(tenantId);
        applyHierarchy(sourceCode, tenantId);
        return sourceCodeRepository.save(sourceCode);
    }

    @Override
    public SourceCode updateSourceCode(SourceCode sourceCode) {
        Integer tenantId = getCurrentTenantId();
        SourceCode existing = sourceCodeRepository.findByIdAndTenantId(sourceCode.getId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Source code not found or access denied"));
        
        existing.setCode(sourceCode.getCode());
        existing.setName(sourceCode.getName());
        if (sourceCode.getDescription() != null) {
            existing.setDescription(sourceCode.getDescription());
        }
        if (sourceCode.getStatus() != null) {
            existing.setStatus(sourceCode.getStatus());
        }
        return sourceCodeRepository.save(existing);
    }

    /** 根据当前租户中的父节点计算树层级，树深度最多三级。 */
    private void applyHierarchy(SourceCode sourceCode, Integer tenantId) {
        if (sourceCode.getParentId() == null) {
            sourceCode.setLevel(1);
            sourceCode.setParentCode(null);
            return;
        }
        SourceCode parent = sourceCodeRepository.findByIdAndTenantId(sourceCode.getParentId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("父节点不存在或无权访问"));
        if (parent.getLevel() == null || parent.getLevel() >= 3) {
            throw new IllegalArgumentException("来源码最多支持三级结构");
        }
        sourceCode.setLevel(parent.getLevel() + 1);
        sourceCode.setParentCode(parent.getCode());
    }

    @Override
    @Transactional
    public void deleteSourceCode(Integer id) {
        Integer tenantId = getCurrentTenantId();
        // 验证根节点所有权
        SourceCode root = getSourceCodeById(id);
        if (root != null) {
            // 递归删除子节点
            deleteRecursive(tenantId, id);
        }
    }

    @Override
    public boolean isCodeUnique(String code, Integer excludeId) {
        SourceCode existing = sourceCodeRepository.findByTenantIdAndCode(getCurrentTenantId(), code);
        return existing == null || (excludeId != null && existing.getId().equals(excludeId));
    }

    // 递归删除子节点
    private void deleteRecursive(Integer tenantId, Integer parentId) {
        List<SourceCode> children = sourceCodeRepository.findByTenantIdAndParentId(tenantId, parentId);
        for (SourceCode child : children) {
            deleteRecursive(tenantId, child.getId());
        }
        // 最终删除带上租户验证
        sourceCodeRepository.findByIdAndTenantId(parentId, tenantId)
            .ifPresent(sourceCodeRepository::delete);
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
