package com.crs.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crs.entity.TenantChannel;
import com.crs.repository.TenantChannelRepository;

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

    public TenantChannel updateChannel(Integer id, Map<String, Object> payload) {
        TenantChannel existing = tenantChannelRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        applyPayload(existing, payload);
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

    public TenantChannel updateChannelByCode(Integer tenantId, String channelCode, Map<String, Object> payload) {
        TenantChannel existing = tenantChannelRepository.findByTenantIdAndChannelCode(tenantId, channelCode);
        if (existing == null) {
            return null;
        }
        applyPayload(existing, payload);
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
        if (data.getPrepaidOrderRequiresPayment() != null) existing.setPrepaidOrderRequiresPayment(data.getPrepaidOrderRequiresPayment());
        if (data.getCancelOrderChecksCancellationRule() != null) existing.setCancelOrderChecksCancellationRule(data.getCancelOrderChecksCancellationRule());
        if (data.getPrepaidCommissionType() != null) existing.setPrepaidCommissionType(data.getPrepaidCommissionType());
        if (data.getPrepaidCommissionValue() != null) existing.setPrepaidCommissionValue(data.getPrepaidCommissionValue());
        if (data.getPostpaidCommissionType() != null) existing.setPostpaidCommissionType(data.getPostpaidCommissionType());
        if (data.getPostpaidCommissionValue() != null) existing.setPostpaidCommissionValue(data.getPostpaidCommissionValue());
    }

    private void applyPayload(TenantChannel existing, Map<String, Object> payload) {
        if (payload == null) {
            return;
        }
        if (payload.containsKey("connected")) existing.setConnected(toBoolean(payload.get("connected")));
        if (payload.containsKey("switchChannel")) existing.setSwitchChannel(toStringValue(payload.get("switchChannel")));
        if (payload.containsKey("accessKey")) existing.setAccessKey(toStringValue(payload.get("accessKey")));
        if (payload.containsKey("accessSecret")) existing.setAccessSecret(toStringValue(payload.get("accessSecret")));
        if (payload.containsKey("logoUrl")) existing.setLogoUrl(toStringValue(payload.get("logoUrl")));
        if (payload.containsKey("channelName")) existing.setChannelName(toStringValue(payload.get("channelName")));
        if (payload.containsKey("priceRounding")) existing.setPriceRounding(toStringValue(payload.get("priceRounding")));
        if (payload.containsKey("prepaidOrderRequiresPayment")) existing.setPrepaidOrderRequiresPayment(toBoolean(payload.get("prepaidOrderRequiresPayment")));
        if (payload.containsKey("cancelOrderChecksCancellationRule")) existing.setCancelOrderChecksCancellationRule(toBoolean(payload.get("cancelOrderChecksCancellationRule")));
        if (payload.containsKey("prepaidCommissionType")) existing.setPrepaidCommissionType(toStringValue(payload.get("prepaidCommissionType")));
        if (payload.containsKey("prepaidCommissionValue")) existing.setPrepaidCommissionValue(toBigDecimal(payload.get("prepaidCommissionValue")));
        if (payload.containsKey("postpaidCommissionType")) existing.setPostpaidCommissionType(toStringValue(payload.get("postpaidCommissionType")));
        if (payload.containsKey("postpaidCommissionValue")) existing.setPostpaidCommissionValue(toBigDecimal(payload.get("postpaidCommissionValue")));
    }

    private Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private String toStringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        return new BigDecimal(text);
    }
}
