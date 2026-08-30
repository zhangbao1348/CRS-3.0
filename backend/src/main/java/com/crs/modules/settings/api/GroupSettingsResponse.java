package com.crs.modules.settings.api;

import com.crs.entity.GroupSettings;

/** 集团设置读模型。 */
public record GroupSettingsResponse(
        String groupControlMode,
        String hourlyRoom,
        String otaPromotionMode,
        boolean showCtripPrice,
        boolean showMeituanPrice) {

    public static GroupSettingsResponse defaults() {
        return new GroupSettingsResponse("strong", "support", "groupRegistration", false, false);
    }

    public static GroupSettingsResponse from(GroupSettings entity) {
        return new GroupSettingsResponse(entity.getGroupControlMode(), entity.getHourlyRoom(),
                entity.getOtaPromotionMode(), entity.isShowCtripPrice(), entity.isShowMeituanPrice());
    }
}
