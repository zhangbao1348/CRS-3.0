package com.crs.service.impl;

import com.crs.entity.Menu;
import com.crs.entity.Role;
import com.crs.entity.RoleMenu;
import com.crs.entity.UserRole;
import com.crs.repository.MenuRepository;
import com.crs.repository.RoleMenuRepository;
import com.crs.repository.UserRoleRepository;
import com.crs.service.MenuService;
import com.crs.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private RoleMenuRepository roleMenuRepository;
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Override
    public List<Menu> getUserMenus(Integer userId, String systemType) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        
        if (userRoles.isEmpty()) {
            return menuRepository.findBySystemTypeAndStatusOrderBySortOrderAsc(systemType, Menu.Status.active);
        }
        
        List<Integer> menuIds = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            List<RoleMenu> roleMenus = roleMenuRepository.findByRoleId(userRole.getRoleId());
            for (RoleMenu roleMenu : roleMenus) {
                if (!menuIds.contains(roleMenu.getMenuId())) {
                    menuIds.add(roleMenu.getMenuId());
                }
            }
        }
        
        if (menuIds.isEmpty()) {
            return menuRepository.findBySystemTypeAndStatusOrderBySortOrderAsc(systemType, Menu.Status.active);
        }
        
        return menuRepository.findAllById(menuIds).stream()
                .filter(menu -> menu.getStatus() == Menu.Status.active)
                .filter(menu -> systemType.equals(menu.getSystemType()))
                .sorted((m1, m2) -> m1.getSortOrder().compareTo(m2.getSortOrder()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getUserPermissions(Integer userId) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Integer> menuIds = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            List<RoleMenu> roleMenus = roleMenuRepository.findByRoleId(userRole.getRoleId());
            for (RoleMenu roleMenu : roleMenus) {
                if (!menuIds.contains(roleMenu.getMenuId())) {
                    menuIds.add(roleMenu.getMenuId());
                }
            }
        }
        
        List<String> permissions = new ArrayList<>();
        for (Integer menuId : menuIds) {
            menuRepository.findById(menuId).ifPresent(menu -> {
                if (menu.getPermission() != null && !menu.getPermission().isEmpty()) {
                    permissions.add(menu.getPermission());
                }
            });
        }
        
        return permissions;
    }
    
    @Override
    public boolean hasPermission(Integer userId, String permission) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permission);
    }
}
