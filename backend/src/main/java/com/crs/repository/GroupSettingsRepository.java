package com.crs.repository;

import com.crs.entity.GroupSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** 集团设置只允许按可信租户上下文查询。 */
public interface GroupSettingsRepository extends JpaRepository<GroupSettings, Integer> {
    Optional<GroupSettings> findByTenantId(Integer tenantId);
}
