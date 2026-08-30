package com.crs.modules.settings.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** 集团设置写入契约，不接收主键、租户和审计字段。 */
public record GroupSettingsRequest(
        @NotBlank(message = "集团管控模式不能为空") @Pattern(regexp = "strong|weak", message = "集团管控模式无效") String groupControlMode,
        @NotBlank(message = "钟点房配置不能为空") @Pattern(regexp = "support|notSupport", message = "钟点房配置无效") String hourlyRoom,
        @NotBlank(message = "OTA促销模式不能为空") @Pattern(regexp = "groupRegistration|groupRuleHotelRegistration|hotelSelfManagement", message = "OTA促销模式无效") String otaPromotionMode,
        @NotNull(message = "携程预测价格开关不能为空") Boolean showCtripPrice,
        @NotNull(message = "美团预测价格开关不能为空") Boolean showMeituanPrice) {
}
