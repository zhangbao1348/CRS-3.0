package com.crs.service;

import com.crs.entity.Role;
import java.util.List;
import java.util.Optional;

/**
 * RoleService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RoleService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RoleService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public interface RoleService {
    
    List<Role> getAllRoles();
    
    List<Role> getActiveRoles();
    
    Optional<Role> getRoleById(Integer id);
    
    Optional<Role> getRoleByCode(String roleCode);
    
    Role createRole(Role role);
    
    Role updateRole(Integer id, Role role);
    
    void deleteRole(Integer id);
}
