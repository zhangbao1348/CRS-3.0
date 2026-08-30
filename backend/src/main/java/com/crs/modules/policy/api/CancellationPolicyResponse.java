package com.crs.modules.policy.api;

import java.util.Date;

/** 取消政策响应，字段名与现有前端合同保持一致。 */
public record CancellationPolicyResponse(
        Integer id, String name, String code, String type, Integer cancellationDays,
        String cancellationTime, String cancellationFeeType, String description,
        String status, Integer groupId, Integer tenantId, Date createdAt,
        Date updatedAt, Integer isDefault
) {
}
