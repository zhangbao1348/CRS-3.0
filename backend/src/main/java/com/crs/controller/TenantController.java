package com.crs.controller;

import com.crs.entity.Tenant;
import com.crs.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
public class TenantController {
    
    @Autowired
    private TenantService tenantService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTenants() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Tenant> tenants = tenantService.getAllTenants();
            response.put("success", true);
            response.put("data", tenants);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取租户列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTenantById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            return tenantService.getTenantById(id)
                .map(tenant -> {
                    response.put("success", true);
                    response.put("data", tenant);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "租户不存在");
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取租户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTenant(@RequestBody Tenant tenant) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (tenantService.existsByCode(tenant.getTenantCode())) {
                response.put("success", false);
                response.put("message", "租户代码已存在");
                return ResponseEntity.badRequest().body(response);
            }
            Tenant createdTenant = tenantService.createTenant(tenant);
            response.put("success", true);
            response.put("data", createdTenant);
            response.put("message", "租户创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建租户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTenant(@PathVariable Integer id, @RequestBody Tenant tenant) {
        Map<String, Object> response = new HashMap<>();
        try {
            Tenant updatedTenant = tenantService.updateTenant(id, tenant);
            if (updatedTenant != null) {
                response.put("success", true);
                response.put("data", updatedTenant);
                response.put("message", "租户更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "租户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新租户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTenant(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            tenantService.deleteTenant(id);
            response.put("success", true);
            response.put("message", "租户删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除租户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
