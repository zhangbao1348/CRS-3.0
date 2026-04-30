package com.crs.service;

import com.crs.entity.TenantChannel;
import com.crs.repository.TenantChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 租户可对接渠道服务
 */
@Service
public class TenantChannelService {

    @Autowired
    private TenantChannelRepository tenantChannelRepository;

    /**
     * 获取租户的所有渠道，按已连接/可连接分组
     */
    public Map<String, List<TenantChannel>> getChannelsGrouped(Integer tenantId) {
        List<TenantChannel> connected = tenantChannelRepository
                .findByTenantIdAndConnectedAndStatusOrderBySortOrderAsc(tenantId, true, "active");
        List<TenantChannel> available = tenantChannelRepository
                .findByTenantIdAndConnectedAndStatusOrderBySortOrderAsc(tenantId, false, "active");

        Map<String, List<TenantChannel>> result = new LinkedHashMap<>();
        result.put("connected", connected);
        result.put("available", available);
        return result;
    }

    /**
     * 获取租户的所有渠道
     */
    public List<TenantChannel> getAllChannels(Integer tenantId) {
        return tenantChannelRepository.findByTenantIdAndStatusOrderBySortOrderAsc(tenantId, "active");
    }

    /**
     * 根据ID获取渠道
     */
    public TenantChannel getChannelById(Integer id) {
        return tenantChannelRepository.findById(id).orElse(null);
    }

    /**
     * 更新渠道对接信息
     */
    public TenantChannel updateChannel(Integer id, TenantChannel channelData) {
        TenantChannel existing = tenantChannelRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        copyNonNullFields(existing, channelData);
        return tenantChannelRepository.save(existing);
    }

    /**
     * 根据渠道代码获取渠道
     */
    public TenantChannel getChannelByCode(Integer tenantId, String channelCode) {
        return tenantChannelRepository.findByTenantIdAndChannelCode(tenantId, channelCode);
    }

    /**
     * 根据渠道代码更新渠道配置
     */
    public TenantChannel updateChannelByCode(Integer tenantId, String channelCode, TenantChannel channelData) {
        TenantChannel existing = tenantChannelRepository.findByTenantIdAndChannelCode(tenantId, channelCode);
        if (existing == null) {
            return null;
        }
        copyNonNullFields(existing, channelData);
        return tenantChannelRepository.save(existing);
    }

    private void copyNonNullFields(TenantChannel existing, TenantChannel data) {
        if (data.getConnected() != null) existing.setConnected(data.getConnected());
        if (data.getSwitchChannel() != null) existing.setSwitchChannel(data.getSwitchChannel());
        if (data.getAccessKey() != null) existing.setAccessKey(data.getAccessKey());
        if (data.getAccessSecret() != null) existing.setAccessSecret(data.getAccessSecret());
        if (data.getLogoUrl() != null) existing.setLogoUrl(data.getLogoUrl());
        if (data.getChannelName() != null) existing.setChannelName(data.getChannelName());
        if (data.getPriceRounding() != null) existing.setPriceRounding(data.getPriceRounding());
        if (data.getPrepaidCommissionType() != null) existing.setPrepaidCommissionType(data.getPrepaidCommissionType());
        if (data.getPrepaidCommissionValue() != null) existing.setPrepaidCommissionValue(data.getPrepaidCommissionValue());
        if (data.getPostpaidCommissionType() != null) existing.setPostpaidCommissionType(data.getPostpaidCommissionType());
        if (data.getPostpaidCommissionValue() != null) existing.setPostpaidCommissionValue(data.getPostpaidCommissionValue());
    }
}
