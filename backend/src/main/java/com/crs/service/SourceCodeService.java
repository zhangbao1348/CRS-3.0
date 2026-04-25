package com.crs.service;

import com.crs.entity.SourceCode;

import java.util.List;
import java.util.Map;

/**
 * 来源码服务接口
 */
public interface SourceCodeService {

    /**
     * 获取所有来源码（树形结构）
     */
    List<Map<String, Object>> getAllSourceCodesAsTree();

    /**
     * 根据租户ID获取所有来源码（树形结构）
     */
    List<Map<String, Object>> getAllSourceCodesAsTreeByTenantId(Integer tenantId);

    /**
     * 根据父ID获取来源码
     */
    List<SourceCode> getSourceCodesByParentId(Integer parentId);

    /**
     * 根据租户ID和父ID获取来源码
     */
    List<SourceCode> getSourceCodesByTenantIdAndParentId(Integer tenantId, Integer parentId);

    /**
     * 获取第三级来源码
     */
    List<SourceCode> getThirdLevelSourceCodes(Integer tenantId);

    /**
     * 根据ID获取来源码
     */
    SourceCode getSourceCodeById(Integer id);

    /**
     * 创建来源码
     */
    SourceCode createSourceCode(SourceCode sourceCode);

    /**
     * 更新来源码
     */
    SourceCode updateSourceCode(SourceCode sourceCode);

    /**
     * 删除来源码
     */
    void deleteSourceCode(Integer id);

    /**
     * 检查来源码CODE是否唯一
     */
    boolean isCodeUnique(String code, Integer excludeId);

    /**
     * 根据租户ID检查来源码CODE是否唯一
     */
    boolean isCodeUniqueByTenantId(Integer tenantId, String code, Integer excludeId);
}