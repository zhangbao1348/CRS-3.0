package com.crs.repository;

import com.crs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓库接口
 * 用于用户数据的CRUD操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据状态查询用户
     * @param status 状态
     * @return 用户列表
     */
    List<User> findByStatus(User.Status status);
    
    /**
     * 根据租户ID查询用户
     * @param tenantId 租户ID
     * @return 用户列表
     */
    List<User> findByTenantId(Integer tenantId);
    
    /**
     * 根据租户ID和状态查询用户
     * @param tenantId 租户ID
     * @param status 状态
     * @return 用户列表
     */
    List<User> findByTenantIdAndStatus(Integer tenantId, User.Status status);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}
