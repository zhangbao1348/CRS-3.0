package com.crs.service.impl;

import com.crs.entity.User;
import com.crs.entity.UserRole;
import com.crs.repository.UserRepository;
import com.crs.repository.UserRoleRepository;
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
        System.out.println("[DEBUG] getUserByUsername called with username: " + username);
        Optional<User> user = userRepository.findByUsername(username);
        System.out.println("[DEBUG] User found: " + user.isPresent());
        if (user.isPresent()) {
            System.out.println("[DEBUG] User details: id=" + user.get().getId() + ", username=" + user.get().getUsername() + ", status=" + user.get().getStatus());
        }
        return user;
    }
    
    @Override
    @Transactional
    public User createUser(User user, List<Integer> roleIds) {
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
            System.out.println("开始更新用户，ID: " + id);
            System.out.println("传入的用户数据: " + user);
            
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
            System.out.println("用户基本信息已保存: " + updatedUser);
            
            userRoleRepository.deleteByUserId(updatedUser.getId());
            System.out.println("已删除用户角色关联");
            
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Integer roleId : roleIds) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(updatedUser.getId());
                    userRole.setRoleId(roleId);
                    userRole.setTenantId(updatedUser.getTenantId());
                    userRoleRepository.save(userRole);
                    System.out.println("已添加角色关联: roleId=" + roleId);
                }
            }
            
            return updatedUser;
        }).orElse(null);
    }
    
    @Override
    @Transactional
    public void deleteUser(Integer id) {
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
        return userRepository.findById(id).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            return userRepository.save(user);
        }).orElse(null);
    }
}
