package com.crs.repository;

import com.crs.entity.ChannelCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道码仓库接口
 */
@Repository
public interface ChannelCodeRepository extends JpaRepository<ChannelCode, Integer> {

    /**
     * 根据租户ID查询所有渠道码
     */
    List<ChannelCode> findByTenantId(Integer tenantId);

    /**
     * 根据租户ID和父ID查询渠道码
     */
    List<ChannelCode> findByTenantIdAndParentId(Integer tenantId, Integer parentId);

    /**
     * 根据租户ID和CODE查询渠道码
     */
    ChannelCode findByTenantIdAndCode(Integer tenantId, String code);

    /**
     * 根据租户ID和ID查询渠道码
     */
    ChannelCode findByTenantIdAndId(Integer tenantId, Integer id);

    /**
     * 根据租户ID和层级查询渠道码（用于获取叶子节点）
     */
    List<ChannelCode> findByTenantIdAndLevel(Integer tenantId, Integer level);

    /**
     * 检查租户下CODE是否存在（排除指定ID）
     */
    boolean existsByTenantIdAndCodeAndIdNot(Integer tenantId, String code, Integer id);
}
