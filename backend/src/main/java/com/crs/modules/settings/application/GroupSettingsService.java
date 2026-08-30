package com.crs.modules.settings.application;

import com.crs.entity.GroupSettings;
import com.crs.modules.settings.api.GroupSettingsRequest;
import com.crs.modules.settings.api.GroupSettingsResponse;
import com.crs.repository.GroupSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 集团设置应用服务，所有归属字段来自可信租户上下文。 */
@Service
public class GroupSettingsService {

    private final GroupSettingsRepository repository;

    public GroupSettingsService(GroupSettingsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public GroupSettingsResponse get(Integer tenantId) {
        return repository.findByTenantId(tenantId)
                .map(GroupSettingsResponse::from)
                .orElseGet(GroupSettingsResponse::defaults);
    }

    @Transactional
    public GroupSettingsResponse save(Integer tenantId, GroupSettingsRequest request) {
        GroupSettings entity = repository.findByTenantId(tenantId).orElseGet(() -> {
            GroupSettings created = new GroupSettings();
            created.setTenantId(tenantId);
            return created;
        });
        entity.setGroupControlMode(request.groupControlMode());
        entity.setHourlyRoom(request.hourlyRoom());
        entity.setOtaPromotionMode(request.otaPromotionMode());
        entity.setShowCtripPrice(request.showCtripPrice());
        entity.setShowMeituanPrice(request.showMeituanPrice());
        return GroupSettingsResponse.from(repository.save(entity));
    }
}
