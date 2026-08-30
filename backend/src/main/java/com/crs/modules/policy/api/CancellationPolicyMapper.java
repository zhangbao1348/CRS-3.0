package com.crs.modules.policy.api;

import com.crs.entity.CancellationPolicy;
import org.springframework.stereotype.Component;

/** 取消政策实体与 API 模型的唯一映射入口。 */
@Component
public class CancellationPolicyMapper {
    /** 将可信请求字段映射为待持久化实体。 */
    public CancellationPolicy toEntity(CancellationPolicyRequest request) {
        CancellationPolicy policy = new CancellationPolicy();
        apply(request, policy);
        return policy;
    }

    /** 将请求中的可变业务字段应用到实体。 */
    public void apply(CancellationPolicyRequest request, CancellationPolicy policy) {
        policy.setName(request.name());
        policy.setCode(request.code());
        policy.setType(request.type());
        policy.setCancellationDays(request.cancellationDays());
        policy.setCancellationTime(request.cancellationTime());
        policy.setCancellationFeeType(request.cancellationFeeType());
        policy.setDescription(request.description());
        policy.setStatus(request.status() == null ? "active" : request.status());
        policy.setIsDefault(request.isDefault() == null ? 0 : request.isDefault());
    }

    /** 将实体转换为稳定响应合同。 */
    public CancellationPolicyResponse toResponse(CancellationPolicy policy) {
        return new CancellationPolicyResponse(policy.getId(), policy.getName(), policy.getCode(),
                policy.getType(), policy.getCancellationDays(), policy.getCancellationTime(),
                policy.getCancellationFeeType(), policy.getDescription(), policy.getStatus(),
                policy.getGroupId(), policy.getTenantId(), policy.getCreatedAt(),
                policy.getUpdatedAt(), policy.getIsDefault());
    }
}
