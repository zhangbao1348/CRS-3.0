package com.crs.controller;

import com.crs.modules.settings.api.GroupSettingsRequest;
import com.crs.modules.settings.api.GroupSettingsResponse;
import com.crs.modules.settings.application.GroupSettingsService;
import com.crs.util.TenantContext;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 当前租户的集团设置接口。 */
@RestController
@RequestMapping("/api/group-settings")
public class GroupSettingsController {

    private final GroupSettingsService service;

    public GroupSettingsController(GroupSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<GroupSettingsResponse> get() {
        return ResponseEntity.ok(service.get(requireTenantId()));
    }

    @PutMapping
    public ResponseEntity<GroupSettingsResponse> save(@Valid @RequestBody GroupSettingsRequest request) {
        return ResponseEntity.ok(service.save(requireTenantId(), request));
    }

    private Integer requireTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) throw new IllegalStateException("缺少可信租户上下文");
        return tenantId;
    }
}
