package com.crs.repository;

import com.crs.entity.TenantChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 租户可对接渠道仓库接口
 */
@Repository
public interface TenantChannelRepository extends JpaRepository<TenantChannel, Integer> {

    /**
     * 根据租户ID查询所有渠道（按排序序号升序）
     */
    List<TenantChannel> findByTenantIdAndStatusOrderBySortOrderAsc(Integer tenantId, String status);

    /**
     * 根据租户ID和对接状态查询渠道
     */
    List<TenantChannel> findByTenantIdAndConnectedAndStatusOrderBySortOrderAsc(Integer tenantId, Boolean connected, String status);

    /**
     * 根据租户ID和渠道代码查询
     */
    TenantChannel findByTenantIdAndChannelCode(Integer tenantId, String channelCode);

    /**
     * 根据 access_key 查询渠道（用于API认证）
     */
    TenantChannel findByAccessKey(String accessKey);
}
