package com.crs.service;

import com.crs.entity.ChannelCode;

import java.util.List;
import java.util.Map;

/**
 * 渠道码服务接口
 */
public interface ChannelCodeService {

    /**
     * 获取所有渠道码（树形结构）
     */
    List<Map<String, Object>> getAllChannelCodesAsTree();

    /**
     * 根据父ID获取渠道码
     */
    List<ChannelCode> getChannelCodesByParentId(Integer tenantId, Integer parentId);

    /**
     * 根据ID获取渠道码
     */
    ChannelCode getChannelCodeById(Integer id);

    /**
     * 创建渠道码
     */
    ChannelCode createChannelCode(ChannelCode channelCode);

    /**
     * 更新渠道码
     */
    ChannelCode updateChannelCode(ChannelCode channelCode);

    /**
     * 删除渠道码
     */
    void deleteChannelCode(Integer id);

    /**
     * 检查渠道码CODE是否唯一
     */
    boolean isCodeUnique(Integer tenantId, String code, Integer excludeId);

    /**
     * 批量创建渠道码（按租户）
     */
    List<ChannelCode> batchCreateChannelCodes(Integer tenantId, List<ChannelCode> channelCodes);

    /**
     * 为指定租户初始化默认渠道码
     */
    List<ChannelCode> initDefaultChannelCodesForTenant(Integer tenantId);
}
