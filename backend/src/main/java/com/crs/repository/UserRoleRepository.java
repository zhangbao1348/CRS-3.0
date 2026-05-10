package com.crs.repository;

import com.crs.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserRoleRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【UserRoleRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：单一职责原则，提供 UserRoleRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    
    List<UserRole> findByUserId(Integer userId);
    
    List<UserRole> findByRoleId(Integer roleId);
    
    List<UserRole> findByTenantId(Integer tenantId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId AND ur.roleId = :roleId")
    void deleteByUserIdAndRoleId(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}
