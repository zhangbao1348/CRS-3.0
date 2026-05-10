package com.crs.controller;

import com.crs.entity.Menu;
import com.crs.entity.Role;
import com.crs.entity.RoleMenu;
import com.crs.entity.UserRole;
import com.crs.repository.MenuRepository;
import com.crs.repository.RoleMenuRepository;
import com.crs.repository.RoleRepository;
import com.crs.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TestPermissionController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【TestPermissionController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 TestPermissionController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/test-permission")
@CrossOrigin(origins = "*")
@Slf4j
public class TestPermissionController {
    
    @Autowired
    private RoleMenuRepository roleMenuRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @PostMapping("/assign-menu/{roleId}/{menuId}")
    public ResponseEntity<Map<String, Object>> assignMenuToRole(
            @PathVariable Integer roleId,
            @PathVariable Integer menuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("开始分配菜单 {} 给角色 {}", menuId, roleId);
            
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuRepository.save(roleMenu);
            
            response.put("success", true);
            response.put("message", "菜单分配成功");
            log.info("菜单 {} 分配给角色 {} 成功", menuId, roleId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("菜单分配失败", e);
            response.put("success", false);
            response.put("message", "菜单分配失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/assign-role/{userId}/{roleId}")
    public ResponseEntity<Map<String, Object>> assignRoleToUser(
            @PathVariable Integer userId,
            @PathVariable Integer roleId) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("开始分配角色 {} 给用户 {}", roleId, userId);
            
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleRepository.save(userRole);
            
            response.put("success", true);
            response.put("message", "角色分配成功");
            log.info("角色 {} 分配给用户 {} 成功", roleId, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("角色分配失败", e);
            response.put("success", false);
            response.put("message", "角色分配失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/add-role-menu-management")
    public ResponseEntity<Map<String, Object>> addRoleAndMenuManagement() {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("开始添加角色管理和菜单管理菜单...");
            
            // 查找超管设置菜单 (parent_id=50)
            Menu superAdminMenu = menuRepository.findById(50).orElse(null);
            if (superAdminMenu == null) {
                response.put("success", false);
                response.put("message", "未找到超管设置菜单");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 创建角色管理菜单
            Menu roleManagementMenu = createMenu(
                50, 
                "super-admin-role-management", 
                "角色管理", 
                "menu", 
                "/super-admin-settings/role-management", 
                "SafetyCertificateOutlined", 
                2, 
                "super-admin-role-management:view", 
                "crs"
            );
            log.info("创建角色管理菜单成功，ID: {}", roleManagementMenu.getId());
            
            // 创建菜单管理菜单
            Menu menuManagementMenu = createMenu(
                50, 
                "super-admin-menu-management", 
                "菜单管理", 
                "menu", 
                "/super-admin-settings/menu-management", 
                "MenuOutlined", 
                3, 
                "super-admin-menu-management:view", 
                "crs"
            );
            log.info("创建菜单管理菜单成功，ID: {}", menuManagementMenu.getId());
            
            // 查找系统管理员角色
            Role adminRole = roleRepository.findByRoleCode("admin");
            if (adminRole != null) {
                // 分配角色管理菜单给系统管理员
                RoleMenu roleMenu1 = new RoleMenu();
                roleMenu1.setRoleId(adminRole.getId());
                roleMenu1.setMenuId(roleManagementMenu.getId());
                roleMenuRepository.save(roleMenu1);
                log.info("分配角色管理菜单给系统管理员成功");
                
                // 分配菜单管理菜单给系统管理员
                RoleMenu roleMenu2 = new RoleMenu();
                roleMenu2.setRoleId(adminRole.getId());
                roleMenu2.setMenuId(menuManagementMenu.getId());
                roleMenuRepository.save(roleMenu2);
                log.info("分配菜单管理菜单给系统管理员成功");
            }
            
            response.put("success", true);
            response.put("message", "角色管理和菜单管理菜单添加成功");
            log.info("角色管理和菜单管理菜单添加完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("添加角色管理和菜单管理菜单失败", e);
            response.put("success", false);
            response.put("message", "添加失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    private Menu createMenu(Integer parentId, String menuCode, String menuName, String menuType, 
                           String path, String icon, Integer sortOrder, String permission, String systemType) {
        Menu menu = new Menu();
        menu.setParentId(parentId);
        menu.setMenuCode(menuCode);
        menu.setMenuName(menuName);
        menu.setMenuType(menuType);
        menu.setPath(path);
        menu.setIcon(icon);
        menu.setSortOrder(sortOrder);
        menu.setPermission(permission);
        menu.setSystemType(systemType);
        menu.setStatus(Menu.Status.active);
        return menuRepository.save(menu);
    }
}
