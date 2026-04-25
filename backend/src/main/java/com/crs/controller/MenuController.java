package com.crs.controller;

import com.crs.entity.Menu;
import com.crs.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
public class MenuController {
    
    @Autowired
    private MenuService menuService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMenus(
            @RequestParam(required = false) String menuCode,
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus;
            if (status != null) {
                try {
                    Menu.Status statusEnum = Menu.Status.valueOf(status);
                    menus = menuService.getAllMenus(menuCode, menuName, statusEnum);
                } catch (IllegalArgumentException e) {
                    response.put("success", false);
                    response.put("message", "无效的状态值");
                    return ResponseEntity.badRequest().body(response);
                }
            } else {
                // 当status为null时，调用无参方法获取所有菜单
                menus = menuService.getAllMenus();
            }
            response.put("success", true);
            response.put("data", menus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取菜单列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/system/{systemType}")
    public ResponseEntity<Map<String, Object>> getMenusBySystemType(@PathVariable String systemType) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus = menuService.getActiveMenusBySystemType(systemType);
            response.put("success", true);
            response.put("data", menus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取菜单列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/system/{systemType}/parent/{parentId}")
    public ResponseEntity<Map<String, Object>> getMenusByParentId(
            @PathVariable String systemType,
            @PathVariable Integer parentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus = menuService.getMenusByParentIdAndSystemType(parentId, systemType);
            response.put("success", true);
            response.put("data", menus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取菜单列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/system/{systemType}/parent-code/{parentCode}")
    public ResponseEntity<Map<String, Object>> getMenusByParentCode(
            @PathVariable String systemType,
            @PathVariable String parentCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus = menuService.getMenusByParentCodeAndSystemType(parentCode, systemType);
            response.put("success", true);
            response.put("data", menus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取菜单列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMenuById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return menuService.getMenuById(id)
                .map(menu -> {
                    response.put("success", true);
                    response.put("data", menu);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "菜单不存在");
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取菜单失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/code/{menuCode}")
    public ResponseEntity<Map<String, Object>> getMenuByMenuCode(@PathVariable String menuCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            return menuService.getMenuByMenuCode(menuCode)
                .map(menu -> {
                    response.put("success", true);
                    response.put("data", menu);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "菜单不存在");
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取菜单失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMenu(@RequestBody Menu menu) {
        Map<String, Object> response = new HashMap<>();
        try {
            Menu createdMenu = menuService.createMenu(menu);
            response.put("success", true);
            response.put("data", createdMenu);
            response.put("message", "菜单创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建菜单失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMenu(@PathVariable Integer id, @RequestBody Menu menu) {
        Map<String, Object> response = new HashMap<>();
        try {
            Menu updatedMenu = menuService.updateMenu(id, menu);
            if (updatedMenu != null) {
                response.put("success", true);
                response.put("data", updatedMenu);
                response.put("message", "菜单更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "菜单不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新菜单失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMenu(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            menuService.deleteMenu(id);
            response.put("success", true);
            response.put("message", "菜单删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除菜单失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
