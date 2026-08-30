package com.crs.modules.settings.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.crs.entity.GroupSettings;
import com.crs.modules.settings.api.GroupSettingsRequest;
import com.crs.repository.GroupSettingsRepository;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class GroupSettingsServiceBoundaryTest {

    @Test
    void returnsDefaultsWithoutWritingWhenTenantHasNoSettings() {
        AtomicInteger saves = new AtomicInteger();
        GroupSettingsRepository repository = repositoryStub(saves, new AtomicReference<>());

        var response = new GroupSettingsService(repository).get(7);

        assertEquals("strong", response.groupControlMode());
        assertFalse(response.showCtripPrice());
        assertEquals(0, saves.get());
    }

    @Test
    void ignoresClientIdentityBecauseRequestHasNoTenantOrId() {
        AtomicInteger saves = new AtomicInteger();
        AtomicReference<GroupSettings> saved = new AtomicReference<>();
        GroupSettingsRepository repository = repositoryStub(saves, saved);

        var request = new GroupSettingsRequest("weak", "notSupport", "hotelSelfManagement", true, false);
        var response = new GroupSettingsService(repository).save(7, request);

        assertEquals("weak", response.groupControlMode());
        assertEquals(1, saves.get());
        assertEquals(7, saved.get().getTenantId());
    }

    private GroupSettingsRepository repositoryStub(
            AtomicInteger saves, AtomicReference<GroupSettings> saved) {
        return (GroupSettingsRepository) Proxy.newProxyInstance(
                GroupSettingsRepository.class.getClassLoader(),
                new Class<?>[]{GroupSettingsRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findByTenantId")) return Optional.empty();
                    if (method.getName().equals("save")) {
                        saves.incrementAndGet();
                        saved.set((GroupSettings) args[0]);
                        return args[0];
                    }
                    if (method.getName().equals("toString")) return "GroupSettingsRepositoryStub";
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
