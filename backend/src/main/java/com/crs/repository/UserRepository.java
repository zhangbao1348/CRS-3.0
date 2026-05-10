package com.crs.repository;

import com.crs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口 (UserRepository)
 * 
 * <p>提供对 {@link User} 实体的数据库操作。支持按用户名、邮箱查询以及租户维度的用户筛选。</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * 根据用户名查找用户。
     * 通常用于登录时的身份验证。
     * 
     * @param username 登录名
     * @return 包含用户实体的 Optional 对象
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据电子邮箱查找用户。
     * 可用于通过邮箱登录或找回密码。
     * 
     * @param email 电子邮箱
     * @return 包含用户实体的 Optional 对象
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据账号状态获取用户列表。
     * 
     * @param status 状态 (active/inactive)
     * @return 用户列表
     */
    List<User> findByStatus(User.Status status);
    
    /**
     * 获取指定租户下的所有用户。
     * 
     * @param tenantId 租户 ID
     * @return 用户列表
     */
    List<User> findByTenantId(Integer tenantId);
    
    /**
     * 获取指定租户下特定状态的用户。
     * 
     * @param tenantId 租户 ID
     * @param status 账号状态
     * @return 用户列表
     */
    List<User> findByTenantIdAndStatus(Integer tenantId, User.Status status);
    
    /**
     * 判断用户名是否已存在。
     * 用于注册或新增用户时的唯一性校验。
     * 
     * @param username 待校验的用户名
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByUsername(String username);
    
    /**
     * 判断电子邮箱是否已存在。
     * 用于注册或修改资料时的唯一性校验。
     * 
     * @param email 待校验的邮箱地址
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByEmail(String email);
}

