package com.crs.security;

import com.crs.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 资源级授权服务。
 *
 * <p>本服务负责将“已登录”进一步收紧为“可以访问指定资源”，
 * 优先覆盖用户菜单与权限查询等容易出现 IDOR 的接口。</p>
 *
 * <p>关联模块：{@code AuthController}、{@code UserRepository}、Spring Security。</p>
 */
@Service
public class ResourceAuthorizationService {

    private static final String SUPER_ADMIN_AUTHORITY = "ROLE_super_admin";

    private final UserRepository userRepository;

    public ResourceAuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 校验当前身份是否可以访问指定用户资源。
     *
     * <p>超级管理员可代理查询；普通用户只能查询与当前登录名一致的自身资源。
     * 用户不存在时同样按无权处理，避免通过响应差异枚举用户 ID。</p>
     *
     * @param userId 目标用户 ID
     * @throws AccessDeniedException 未认证或无权访问时抛出
     */
    public void requireUserAccess(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("无权访问该用户资源");
        }

        boolean superAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> SUPER_ADMIN_AUTHORITY.equals(authority.getAuthority()));
        if (superAdmin) {
            return;
        }

        boolean ownResource = userRepository.findById(userId)
                .map(user -> authentication.getName().equals(user.getUsername()))
                .orElse(false);
        if (!ownResource) {
            throw new AccessDeniedException("无权访问该用户资源");
        }
    }
}
