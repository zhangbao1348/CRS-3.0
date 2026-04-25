package com.crs.service;

import com.crs.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    
    List<Role> getAllRoles();
    
    List<Role> getActiveRoles();
    
    Optional<Role> getRoleById(Integer id);
    
    Optional<Role> getRoleByCode(String roleCode);
    
    Role createRole(Role role);
    
    Role updateRole(Integer id, Role role);
    
    void deleteRole(Integer id);
}
