package com.crs.service;

import com.crs.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * UserService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【UserService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 UserService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public interface UserService {
    
    List<User> getAllUsers();
    
    List<User> getUsersByTenantId(Integer tenantId);
    
    Optional<User> getUserById(Integer id);
    
    Optional<User> getUserByUsername(String username);
    
    User createUser(User user, List<Integer> roleIds);
    
    User updateUser(Integer id, User user, List<Integer> roleIds);
    
    void deleteUser(Integer id);
    
    User updateUserStatus(Integer id, User.Status status);
    
    User resetPassword(Integer id, String newPassword);
}
