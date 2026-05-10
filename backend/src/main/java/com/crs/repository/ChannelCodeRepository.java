package com.crs.repository;

import com.crs.entity.ChannelCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道码数据访问接口 (ChannelCodeRepository)
 * 
 * <p>提供对 {@link ChannelCode} 实体的数据库操作能力。支持基于租户隔离的多级渠道树形检索及唯一性校验。</p>
 */
@Repository
public interface ChannelCodeRepository extends JpaRepository<ChannelCode, Integer> {

    /**
     * 获取指定租户下的所有渠道定义。
     * 
     * @param tenantId 租户 ID
     * @return 渠道列表
     */
    List<ChannelCode> findByTenantId(Integer tenantId);

    /**
     * 获取指定租户下、特定父节点下的子渠道。
     * 
     * @param tenantId 租户 ID
     * @param parentId 父渠道 ID
     * @return 子渠道列表
     */
    List<ChannelCode> findByTenantIdAndParentId(Integer tenantId, Integer parentId);

    /**
     * 在指定租户内，根据渠道编码精确查找。
     * 
     * @param tenantId 租户 ID
     * @param code 渠道编码
     * @return 渠道实体
     */
    ChannelCode findByTenantIdAndCode(Integer tenantId, String code);

    /**
     * 在指定租户内，根据主键 ID 查找渠道。
     * 
     * @param tenantId 租户 ID
     * @param id 渠道 ID
     * @return 渠道实体
     */
    ChannelCode findByTenantIdAndId(Integer tenantId, Integer id);

    /**
     * 获取指定租户下、特定层级的渠道。
     * 常用于区分渠道分类与具体售卖渠道（叶子节点）。
     * 
     * @param tenantId 租户 ID
     * @param level 层级 (1-一级, 2-二级)
     * @return 渠道列表
     */
    List<ChannelCode> findByTenantIdAndLevel(Integer tenantId, Integer level);

    /**
     * 校验租户内是否存在重复的渠道编码（排除自身）。
     * 
     * @param tenantId 租户 ID
     * @param code 待校验的编码
     * @param id 排除的 ID
     * @return 存在返回 true
     */
    boolean existsByTenantIdAndCodeAndIdNot(Integer tenantId, String code, Integer id);

    /**
     * 根据租户 ID 和父级渠道编码查找子渠道。
     */
    List<ChannelCode> findByTenantIdAndParentCode(Integer tenantId, String parentCode);

    /**
     * 根据租户、自身编码及父级编码精确锁定唯一渠道记录。
     */
    ChannelCode findByTenantIdAndCodeAndParentCode(Integer tenantId, String code, String parentCode);
}

