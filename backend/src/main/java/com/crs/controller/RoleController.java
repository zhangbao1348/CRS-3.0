package com.crs.controller;

import com.crs.entity.Menu;
import com.crs.entity.Role;
import com.crs.entity.RoleMenu;
import com.crs.entity.Tenant;
import com.crs.repository.MenuRepository;
import com.crs.repository.RoleMenuRepository;
import com.crs.repository.TenantRepository;
import com.crs.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RoleController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RoleController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RoleController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private RoleMenuRepository roleMenuRepository;
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Role> roles = roleService.getAllRoles();
            List<Map<String, Object>> roleDataList = roles.stream().map(role -> {
                Map<String, Object> roleData = new HashMap<>();
                roleData.put("id", role.getId());
                roleData.put("tenantId", role.getTenantId());
                roleData.put("roleCode", role.getRoleCode());
                roleData.put("roleName", role.getRoleName());
                roleData.put("description", role.getDescription());
                roleData.put("status", role.getStatus());
                roleData.put("dataScope", role.getDataScope());
                roleData.put("sortOrder", 0);
                
                if (role.getTenantId() != null) {
                    Optional<Tenant> tenant = tenantRepository.findById(role.getTenantId());
                    tenant.ifPresent(t -> {
                        roleData.put("tenantName", t.getTenantName());
                    });
                } else {
                    roleData.put("tenantName", "平台");
                }
                
                return roleData;
            }).collect(Collectors.toList());
            
            response.put("success", true);
            response.put("data", roleDataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取角色列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveRoles() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Role> roles = roleService.getActiveRoles();
            response.put("success", true);
            response.put("data", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取角色列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return roleService.getRoleById(id)
                .map(role -> {
                    response.put("success", true);
                    response.put("data", role);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "角色不存在");
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取角色失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/code/{roleCode}")
    public ResponseEntity<Map<String, Object>> getRoleByCode(@PathVariable String roleCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            return roleService.getRoleByCode(roleCode)
                .map(role -> {
                    response.put("success", true);
                    response.put("data", role);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "角色不存在");
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取角色失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody Role role) {
        Map<String, Object> response = new HashMap<>();
        try {
            Role createdRole = roleService.createRole(role);
            response.put("success", true);
            response.put("data", createdRole);
            response.put("message", "角色创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建角色失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable Integer id, @RequestBody Role role) {
        Map<String, Object> response = new HashMap<>();
        try {
            Role updatedRole = roleService.updateRole(id, role);
            if (updatedRole != null) {
                response.put("success", true);
                response.put("data", updatedRole);
                response.put("message", "角色更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "角色不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新角色失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            roleService.deleteRole(id);
            response.put("success", true);
            response.put("message", "角色删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除角色失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/{roleId}/menus")
    public ResponseEntity<Map<String, Object>> getRoleMenus(@PathVariable Integer roleId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<RoleMenu> roleMenus = roleMenuRepository.findByRoleId(roleId);
            Set<Integer> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toSet());
            
            response.put("success", true);
            response.put("data", menuIds);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取角色菜单失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/{roleId}/menus")
    @Transactional
    public ResponseEntity<Map<String, Object>> assignMenusToRole(
            @PathVariable Integer roleId,
            @RequestBody Map<String, List<Integer>> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Integer> menuIds = request.get("menuIds");
            
            roleMenuRepository.deleteByRoleId(roleId);
            
            if (menuIds != null && !menuIds.isEmpty()) {
                for (Integer menuId : menuIds) {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    roleMenuRepository.save(roleMenu);
                }
            }
            
            response.put("success", true);
            response.put("message", "菜单分配成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "菜单分配失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
