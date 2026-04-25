package com.crs.service.impl;

import com.crs.entity.Role;
import com.crs.repository.RoleRepository;
import com.crs.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
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
        return roleRepository.save(role);
    }
    
    @Override
    public Role updateRole(Integer id, Role role) {
        return roleRepository.findById(id).map(existingRole -> {
            if (role.getTenantId() != null) {
                existingRole.setTenantId(role.getTenantId());
            }
            if (role.getRoleCode() != null) {
                existingRole.setRoleCode(role.getRoleCode());
            }
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
        roleRepository.deleteById(id);
    }
}
