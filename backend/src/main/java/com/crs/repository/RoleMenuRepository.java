package com.crs.repository;

import com.crs.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, Integer> {
    
    List<RoleMenu> findByRoleId(Integer roleId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM RoleMenu rm WHERE rm.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Integer roleId);
    
    boolean existsByRoleIdAndMenuId(Integer roleId, Integer menuId);
}
