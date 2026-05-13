package com.crs.controller;

import com.crs.entity.TenantChannel;
import com.crs.service.TenantChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 租户可对接渠道控制器
 * 提供渠道列表查询和对接状态管理API
 */
@RestController
@RequestMapping("/api/tenant-channels")
public class TenantChannelController {

    @Autowired
    private TenantChannelService tenantChannelService;

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 获取渠道列表（按已连接/可连接分组）
     * 前端渠道管理 > 渠道列表页面使用
     */
    @GetMapping
    public ResponseEntity<Map<String, List<TenantChannel>>> getChannelsGrouped() {
        try {
            Map<String, List<TenantChannel>> result = tenantChannelService.getChannelsGrouped(getCurrentTenantId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取所有渠道（不分组）
     */
    @GetMapping("/all")
    public ResponseEntity<List<TenantChannel>> getAllChannels() {
        try {
            List<TenantChannel> channels = tenantChannelService.getAllChannels(getCurrentTenantId());
            return ResponseEntity.ok(channels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 获取渠道详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<TenantChannel> getChannelById(@PathVariable Integer id) {
        try {
            TenantChannel channel = tenantChannelService.getChannelById(id);
            if (channel != null && channel.getTenantId().equals(getCurrentTenantId())) {
                return ResponseEntity.ok(channel);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 更新渠道对接信息（对接/断开/修改秘钥等）
     */
    @PutMapping("/{id}")
    public ResponseEntity<TenantChannel> updateChannel(
            @PathVariable Integer id,
            @RequestBody TenantChannel channelData) {
        try {
            // 校验归属权
            TenantChannel existing = tenantChannelService.getChannelById(id);
            if (existing == null || !existing.getTenantId().equals(getCurrentTenantId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            
            TenantChannel updated = tenantChannelService.updateChannel(id, channelData);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据渠道代码获取渠道详情
     */
    @GetMapping("/code/{channelCode}")
    public ResponseEntity<TenantChannel> getChannelByCode(
            @PathVariable String channelCode) {
        try {
            TenantChannel channel = tenantChannelService.getChannelByCode(getCurrentTenantId(), channelCode);
            if (channel != null) {
                return ResponseEntity.ok(channel);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据渠道代码更新渠道配置
     */
    @PutMapping("/code/{channelCode}")
    public ResponseEntity<TenantChannel> updateChannelByCode(
            @PathVariable String channelCode,
            @RequestBody TenantChannel channelData) {
        try {
            TenantChannel updated = tenantChannelService.updateChannelByCode(getCurrentTenantId(), channelCode, channelData);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
