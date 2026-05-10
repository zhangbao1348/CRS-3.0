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
 * PermissionDataInitController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【PermissionDataInitController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 PermissionDataInitController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/permission-init")
@CrossOrigin(origins = "*")
@Slf4j
public class PermissionDataInitController {
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private RoleMenuRepository roleMenuRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> initAllPermissionData() {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("开始初始化权限数据...");
            
            initMenus();
            initRoles();
            initRoleMenus();
            initUserRoles();
            
            response.put("success", true);
            response.put("message", "权限数据初始化成功");
            log.info("权限数据初始化完成");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("权限数据初始化失败", e);
            response.put("success", false);
            response.put("message", "权限数据初始化失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    private void initMenus() {
        log.info("初始化菜单数据...");
        
        if (menuRepository.count() > 0) {
            log.info("菜单数据已存在，跳过初始化");
            return;
        }
        
        Menu dashboard = createMenu(0, "dashboard", "首页", "menu", "/dashboard", "HomeOutlined", 1, "dashboard:view", "crs");
        
        Menu reservation = createMenu(0, "reservation", "订单", "dir", "/reservation", "FileTextOutlined", 2, null, "crs");
        createMenu(reservation.getId(), "reservation-list", "订单", "menu", "/reservation/reservation-list", "FileTextOutlined", 1, "reservation:view", "crs");
        
        Menu inventoryManagement = createMenu(0, "inventory-management", "库存管理", "dir", "/inventory-management", "InboxOutlined", 3, null, "crs");
        createMenu(inventoryManagement.getId(), "inventory", "房控日历", "menu", "/inventory", "CalendarOutlined", 1, "inventory:view", "crs");
        createMenu(inventoryManagement.getId(), "room-status", "房态管理", "menu", "/inventory/room-status", "HomeOutlined", 2, "room-status:view", "crs");
        createMenu(inventoryManagement.getId(), "booking-control", "预订控制", "menu", "/inventory/booking-control", "FilterOutlined", 3, "booking-control:view", "crs");
        
        Menu roomManagement = createMenu(0, "room-management", "房型管理", "dir", "/room-management", "ApartmentOutlined", 4, null, "crs");
        createMenu(roomManagement.getId(), "room-type", "房型管理", "menu", "/room-management/room-type", "HomeOutlined", 1, "room-type:view", "crs");
        
        Menu rateManagement = createMenu(0, "rate-management", "价格计划管理", "dir", "/rate-management", "DollarOutlined", 5, null, "crs");
        createMenu(rateManagement.getId(), "rate-plan", "价格计划", "menu", "/rate-management/rate-plan", "TagOutlined", 1, "rate-plan:view", "crs");
        
        Menu channelManagement = createMenu(0, "channel-management", "渠道管理", "dir", "/channel-management", "LinkOutlined", 6, null, "crs");
        createMenu(channelManagement.getId(), "channel-list", "渠道列表", "menu", "/channel-management/channel-list", "LinkOutlined", 1, "channel-list:view", "crs");
        
        createMenu(0, "reports", "数据及报表", "dir", "/reports", "BarChartOutlined", 7, null, "crs");
        
        Menu groupManagement = createMenu(0, "group-management", "集团管理", "dir", "/group-management", "BuildOutlined", 8, null, "crs");
        createMenu(groupManagement.getId(), "hotel-management", "酒店管理", "menu", "/group-management/hotel-management", "ApartmentOutlined", 1, "hotel-management:view", "crs");
        
        Menu systemSettings = createMenu(0, "system-settings", "系统设置", "dir", "/system-settings", "SettingOutlined", 9, null, "crs");
        createMenu(systemSettings.getId(), "user-management", "用户管理", "menu", "/system-settings/user-management", "UserOutlined", 1, "user-management:view", "crs");
        createMenu(systemSettings.getId(), "role-management", "角色管理", "menu", "/system-settings/role-management", "SafetyCertificateOutlined", 2, "role-management:view", "crs");
        
        Menu superAdminSettings = createMenu(0, "super-admin-settings", "超管设置", "dir", "/super-admin-settings", "SafetyCertificateOutlined", 10, null, "crs");
        createMenu(superAdminSettings.getId(), "tenant-management", "租户管理", "menu", "/super-admin-settings/tenant-management", "BuildOutlined", 1, "tenant-management:view", "crs");
        
        log.info("菜单数据初始化完成");
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
    
    private void initRoles() {
        log.info("初始化角色数据...");
        
        if (roleRepository.count() > 0) {
            log.info("角色数据已存在，跳过初始化");
            return;
        }
        
        Role adminRole = new Role();
        adminRole.setRoleCode("admin");
        adminRole.setRoleName("系统管理员");
        adminRole.setDescription("拥有系统所有权限");
        adminRole.setStatus(Role.Status.active);
        adminRole.setDataScope("all");
        roleRepository.save(adminRole);
        
        Role userRole = new Role();
        userRole.setRoleCode("user");
        userRole.setRoleName("普通用户");
        userRole.setDescription("普通用户权限");
        userRole.setStatus(Role.Status.active);
        userRole.setDataScope("self");
        roleRepository.save(userRole);
        
        log.info("角色数据初始化完成");
    }
    
    private void initRoleMenus() {
        log.info("初始化角色菜单关联数据...");
        
        if (roleMenuRepository.count() > 0) {
            log.info("角色菜单关联数据已存在，跳过初始化");
            return;
        }
        
        Role adminRole = roleRepository.findByRoleCode("admin");
        if (adminRole != null) {
            for (Menu menu : menuRepository.findAll()) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(adminRole.getId());
                roleMenu.setMenuId(menu.getId());
                roleMenuRepository.save(roleMenu);
            }
        }
        
        log.info("角色菜单关联数据初始化完成");
    }
    
    private void initUserRoles() {
        log.info("初始化用户角色关联数据...");
        
        if (userRoleRepository.count() > 0) {
            log.info("用户角色关联数据已存在，跳过初始化");
            return;
        }
        
        Role adminRole = roleRepository.findByRoleCode("admin");
        if (adminRole != null) {
            UserRole userRole1 = new UserRole();
            userRole1.setUserId(1);
            userRole1.setRoleId(adminRole.getId());
            userRoleRepository.save(userRole1);
            
            UserRole userRole2 = new UserRole();
            userRole2.setUserId(2);
            userRole2.setRoleId(adminRole.getId());
            userRoleRepository.save(userRole2);
            
            UserRole userRole3 = new UserRole();
            userRole3.setUserId(3);
            userRole3.setRoleId(adminRole.getId());
            userRoleRepository.save(userRole3);
        }
        
        log.info("用户角色关联数据初始化完成");
    }
}
