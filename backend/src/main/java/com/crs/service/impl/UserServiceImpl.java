package com.crs.service.impl;

import com.crs.entity.User;
import com.crs.entity.UserRole;
import com.crs.repository.UserRepository;
import com.crs.repository.UserRoleRepository;
import com.crs.repository.RoleRepository;
import com.crs.repository.TenantRepository;
import com.crs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * UserServiceImpl 服务实现类 (Service Implementation)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【UserServiceImpl】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 UserServiceImpl 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TenantRepository tenantRepository;
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public List<User> getUsersByTenantId(Integer tenantId) {
        if (tenantId == null) {
            return userRepository.findAll();
        }
        return userRepository.findByTenantId(tenantId);
    }
    
    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional
    public User createUser(User user, List<Integer> roleIds) {
        normalizeAndValidate(user, roleIds, true);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Integer roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(savedUser.getId());
                userRole.setRoleId(roleId);
                userRole.setTenantId(savedUser.getTenantId());
                userRoleRepository.save(userRole);
            }
        }
        
        return savedUser;
    }
    
    @Override
    @Transactional
    public User updateUser(Integer id, User user, List<Integer> roleIds) {
        return userRepository.findById(id).map(existingUser -> {
            normalizeAndValidate(user, roleIds, false);
            if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
                if (userRepository.existsByUsername(user.getUsername())) {
                    throw new IllegalArgumentException("用户名已存在");
                }
                existingUser.setUsername(user.getUsername());
            }
            if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
                if (userRepository.existsByEmail(user.getEmail())) {
                    throw new IllegalArgumentException("邮箱已存在");
                }
                existingUser.setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                existingUser.setName(user.getName());
            }
            if (user.getPhone() != null) {
                existingUser.setPhone(user.getPhone());
            }
            if (user.getAvatar() != null) {
                existingUser.setAvatar(user.getAvatar());
            }
            existingUser.setTenantId(user.getTenantId());
            
            User updatedUser = userRepository.save(existingUser);
            
            userRoleRepository.deleteByUserId(updatedUser.getId());
            
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Integer roleId : roleIds) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(updatedUser.getId());
                    userRole.setRoleId(roleId);
                    userRole.setTenantId(updatedUser.getTenantId());
                    userRoleRepository.save(userRole);
                }
            }
            
            return updatedUser;
        }).orElse(null);
    }
    
    @Override
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }
        userRoleRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public User updateUserStatus(Integer id, User.Status status) {
        return userRepository.findById(id).map(user -> {
            user.setStatus(status);
            return userRepository.save(user);
        }).orElse(null);
    }
    
    @Override
    @Transactional
    public User resetPassword(Integer id, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("密码长度至少6位");
        }
        return userRepository.findById(id).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            return userRepository.save(user);
        }).orElse(null);
    }

    /** 统一收口用户必填字段、租户与角色引用校验。 */
    private void normalizeAndValidate(User user, List<Integer> roleIds, boolean creating) {
        user.setUsername(trim(user.getUsername()));
        user.setName(trim(user.getName()));
        user.setEmail(trim(user.getEmail()));
        user.setPhone(trimToNull(user.getPhone()));
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("请输入有效的邮箱地址");
        }
        if (creating && (user.getPassword() == null || user.getPassword().length() < 6)) {
            throw new IllegalArgumentException("密码长度至少6位");
        }
        if (user.getTenantId() != null && !tenantRepository.existsById(user.getTenantId())) {
            throw new IllegalArgumentException("归属租户不存在");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("请至少选择一个角色");
        }
        if (roleIds.stream().distinct().count() != roleIds.size()) {
            throw new IllegalArgumentException("角色不能重复");
        }
        for (Integer roleId : roleIds) {
            var role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("角色不存在"));
            if (role.getStatus() != com.crs.entity.Role.Status.active) {
                throw new IllegalArgumentException("不能分配已停用的角色");
            }
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String trimToNull(String value) {
        String normalized = trim(value);
        return normalized == null || normalized.isEmpty() ? null : normalized;
    }
}
