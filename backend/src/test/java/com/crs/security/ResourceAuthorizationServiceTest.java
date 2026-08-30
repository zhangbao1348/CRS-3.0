package com.crs.security;

import com.crs.entity.User;
import com.crs.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
class ResourceAuthorizationServiceTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowUserToAccessOwnResource() {
        authenticate("alice", "ROLE_user");
        ResourceAuthorizationService authorizationService = serviceWithUsers(user(10, "alice"));

        assertDoesNotThrow(() -> authorizationService.requireUserAccess(10));
    }

    @Test
    void shouldRejectUserAccessingAnotherUserResource() {
        authenticate("alice", "ROLE_user");
        ResourceAuthorizationService authorizationService = serviceWithUsers(user(11, "bob"));

        assertThrows(AccessDeniedException.class,
                () -> authorizationService.requireUserAccess(11));
    }

    @Test
    void shouldRejectUnknownUserIdWithoutLeakingExistence() {
        authenticate("alice", "ROLE_user");
        ResourceAuthorizationService authorizationService = serviceWithUsers();

        assertThrows(AccessDeniedException.class,
                () -> authorizationService.requireUserAccess(404));
    }

    @Test
    void shouldAllowSuperAdminWithoutTargetLookup() {
        authenticate("root", "ROLE_super_admin");
        AtomicInteger lookupCount = new AtomicInteger();
        ResourceAuthorizationService authorizationService =
                new ResourceAuthorizationService(repositoryWithUsers(Map.of(), lookupCount));

        assertDoesNotThrow(() -> authorizationService.requireUserAccess(99));
        org.junit.jupiter.api.Assertions.assertEquals(0, lookupCount.get());
    }

    @Test
    void shouldRejectMissingAuthentication() {
        ResourceAuthorizationService authorizationService = serviceWithUsers();
        assertThrows(AccessDeniedException.class,
                () -> authorizationService.requireUserAccess(1));
    }

    private void authenticate(String username, String authority) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority(authority)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User user(Integer id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private ResourceAuthorizationService serviceWithUsers(User... users) {
        Map<Integer, User> usersById = new HashMap<>();
        for (User user : users) {
            usersById.put(user.getId(), user);
        }
        return new ResourceAuthorizationService(
                repositoryWithUsers(usersById, new AtomicInteger()));
    }

    /**
     * 使用 JDK 原生代理构造只读仓储桩，避免测试环境依赖 JVM 自附加能力。
     */
    private UserRepository repositoryWithUsers(
            Map<Integer, User> usersById,
            AtomicInteger lookupCount) {
        return (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        lookupCount.incrementAndGet();
                        return Optional.ofNullable(usersById.get((Integer) args[0]));
                    }
                    if ("toString".equals(method.getName())) {
                        return "UserRepositoryTestStub";
                    }
                    throw new UnsupportedOperationException("未支持的测试方法: " + method.getName());
                });
    }
}
