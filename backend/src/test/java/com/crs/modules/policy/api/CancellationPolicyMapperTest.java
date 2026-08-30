package com.crs.modules.policy.api;

import com.crs.entity.CancellationPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancellationPolicyMapperTest {

    private final CancellationPolicyMapper mapper = new CancellationPolicyMapper();

    @Test
    void shouldMapAllEditableFieldsWithoutAcceptingTenantFields() {
        CancellationPolicyRequest request = new CancellationPolicyRequest(
                "晚六点前免费", "FREE_18", "限时扣费", 1, "18:00",
                "首晚", "测试政策", "active", 1);

        CancellationPolicy policy = mapper.toEntity(request);

        assertEquals("晚六点前免费", policy.getName());
        assertEquals("FREE_18", policy.getCode());
        assertEquals("限时扣费", policy.getType());
        assertEquals(1, policy.getCancellationDays());
        assertEquals("18:00", policy.getCancellationTime());
        assertEquals("首晚", policy.getCancellationFeeType());
        assertEquals("测试政策", policy.getDescription());
        assertEquals("active", policy.getStatus());
        assertEquals(1, policy.getIsDefault());
        assertNull(policy.getTenantId());
        assertNull(policy.getGroupId());
    }
}
