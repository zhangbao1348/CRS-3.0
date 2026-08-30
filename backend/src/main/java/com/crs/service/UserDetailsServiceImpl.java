package com.crs.service;

import com.crs.entity.Role;
import com.crs.entity.User;
import com.crs.entity.UserRole;
import com.crs.repository.RoleRepository;
import com.crs.repository.UserRepository;
import com.crs.repository.UserRoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户详情服务类
 * 用于加载用户信息和提供给Spring Security使用
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    
    public UserDetailsServiceImpl(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<Integer> roleIds = userRoleRepository.findByUserId(user.getId()).stream()
                .map(UserRole::getRoleId)
                .toList();
        Set<GrantedAuthority> authorities = roleRepository.findAllById(roleIds).stream()
                .filter(role -> role.getStatus() == Role.Status.active)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == User.Status.active,
                true,
                true,
                true,
                authorities
        );
    }
}
