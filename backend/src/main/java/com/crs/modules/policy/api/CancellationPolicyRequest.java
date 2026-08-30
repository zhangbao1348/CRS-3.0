package com.crs.modules.policy.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** 取消政策创建与更新请求，屏蔽租户、主键和审计字段的客户端注入。 */
public record CancellationPolicyRequest(
        @NotBlank(message = "取消政策名称不能为空") @Size(max = 100, message = "取消政策名称不能超过100个字符") String name,
        @NotBlank(message = "取消政策代码不能为空") @Size(max = 50, message = "取消政策代码不能超过50个字符") String code,
        @NotBlank(message = "取消政策类型不能为空") @Size(max = 50, message = "取消政策类型不能超过50个字符") String type,
        @PositiveOrZero(message = "提前取消天数不能为负数") Integer cancellationDays,
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "取消截止时间必须使用HH:mm格式") String cancellationTime,
        @Size(max = 50, message = "违约金扣费类型不能超过50个字符") String cancellationFeeType,
        String description,
        @Pattern(regexp = "^(active|inactive)$", message = "状态只能为active或inactive") String status,
        Integer isDefault
) {
}
