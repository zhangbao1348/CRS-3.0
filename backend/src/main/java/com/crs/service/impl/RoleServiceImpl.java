package com.crs.service.impl;

import com.crs.entity.Role;
import com.crs.repository.RoleRepository;
import com.crs.repository.RoleMenuRepository;
import com.crs.repository.UserRoleRepository;
import com.crs.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * RoleServiceImpl 服务实现类 (Service Implementation)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RoleServiceImpl】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RoleServiceImpl 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    @Override
    public List<Role> getActiveRoles() {
        return roleRepository.findByStatus(Role.Status.active);
    }
    
    @Override
    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }
    
    @Override
    public Optional<Role> getRoleByCode(String roleCode) {
        return Optional.ofNullable(roleRepository.findByRoleCode(roleCode));
    }
    
    @Override
    public Role createRole(Role role) {
        validate(role, true);
        role.setId(null);
        return roleRepository.save(role);
    }
    
    @Override
    public Role updateRole(Integer id, Role role) {
        return roleRepository.findById(id).map(existingRole -> {
            validate(role, false);
            if (role.getRoleName() != null) {
                existingRole.setRoleName(role.getRoleName());
            }
            if (role.getDescription() != null) {
                existingRole.setDescription(role.getDescription());
            }
            if (role.getStatus() != null) {
                existingRole.setStatus(role.getStatus());
            }
            if (role.getDataScope() != null) {
                existingRole.setDataScope(role.getDataScope());
            }
            return roleRepository.save(existingRole);
        }).orElse(null);
    }
    
    @Override
    public void deleteRole(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        if (!userRoleRepository.findByRoleId(id).isEmpty()) {
            throw new IllegalArgumentException("角色已分配给用户，不能删除");
        }
        roleMenuRepository.deleteByRoleId(id);
        roleRepository.delete(role);
    }

    /** 角色代码作为稳定权限标识，创建后不允许修改。 */
    private void validate(Role role, boolean creating) {
        role.setRoleCode(role.getRoleCode() == null ? null : role.getRoleCode().trim());
        role.setRoleName(role.getRoleName() == null ? null : role.getRoleName().trim());
        if (role.getRoleName() == null || role.getRoleName().isEmpty()) {
            throw new IllegalArgumentException("角色名称不能为空");
        }
        if (creating) {
            if (role.getRoleCode() == null || !role.getRoleCode().matches("^[A-Za-z0-9_]+$")) {
                throw new IllegalArgumentException("角色代码只能包含英文字母、数字和下划线");
            }
            if (roleRepository.existsByRoleCode(role.getRoleCode())) {
                throw new IllegalArgumentException("角色代码已存在");
            }
        }
        if (role.getStatus() == null) {
            role.setStatus(Role.Status.active);
        }
        if (role.getDataScope() == null || role.getDataScope().isBlank()) {
            role.setDataScope("all");
        }
    }
}
