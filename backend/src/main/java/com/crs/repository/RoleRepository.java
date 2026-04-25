package com.crs.repository;

import com.crs.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    List<Role> findByStatus(Role.Status status);
    
    Role findByRoleCode(String roleCode);
}
