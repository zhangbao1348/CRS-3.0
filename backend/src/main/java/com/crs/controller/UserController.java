package com.crs.controller;

import com.crs.entity.Role;
import com.crs.entity.Tenant;
import com.crs.entity.User;
import com.crs.entity.UserRole;
import com.crs.repository.RoleRepository;
import com.crs.repository.TenantRepository;
import com.crs.repository.UserRoleRepository;
import com.crs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(required = false) Integer tenantId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users;
            if (tenantId != null) {
                users = userService.getUsersByTenantId(tenantId);
            } else {
                users = userService.getAllUsers();
            }
            
            List<Map<String, Object>> userDataList = users.stream().map(user -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("tenantId", user.getTenantId());
                userData.put("username", user.getUsername());
                userData.put("name", user.getName());
                userData.put("email", user.getEmail());
                userData.put("phone", user.getPhone());
                userData.put("avatar", user.getAvatar());
                userData.put("status", user.getStatus());
                userData.put("lastLoginTime", user.getLastLoginTime());
                userData.put("lastLoginIp", user.getLastLoginIp());
                userData.put("createdAt", user.getCreatedAt());
                userData.put("updatedAt", user.getUpdatedAt());
                
                if (user.getTenantId() != null) {
                    Optional<Tenant> tenant = tenantRepository.findById(user.getTenantId());
                    tenant.ifPresent(t -> userData.put("tenantName", t.getTenantName()));
                } else {
                    userData.put("tenantName", "平台");
                }
                
                List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
                List<Integer> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());
                userData.put("roleIds", roleIds);
                
                List<String> roleNames = new ArrayList<>();
                for (Integer roleId : roleIds) {
                    Optional<Role> role = roleRepository.findById(roleId);
                    role.ifPresent(r -> roleNames.add(r.getRoleName()));
                }
                userData.put("roleNames", roleNames);
                
                return userData;
            }).collect(Collectors.toList());
            
            response.put("success", true);
            response.put("data", userDataList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("tenantId", user.getTenantId());
                userData.put("username", user.getUsername());
                userData.put("name", user.getName());
                userData.put("email", user.getEmail());
                userData.put("phone", user.getPhone());
                userData.put("avatar", user.getAvatar());
                userData.put("status", user.getStatus());
                
                if (user.getTenantId() != null) {
                    Optional<Tenant> tenant = tenantRepository.findById(user.getTenantId());
                    tenant.ifPresent(t -> userData.put("tenantName", t.getTenantName()));
                } else {
                    userData.put("tenantName", "平台");
                }
                
                List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
                List<Integer> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());
                userData.put("roleIds", roleIds);
                
                response.put("success", true);
                response.put("data", userData);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = new User();
            user.setUsername((String) request.get("username"));
            user.setPassword((String) request.get("password"));
            user.setName((String) request.get("name"));
            user.setEmail((String) request.get("email"));
            user.setPhone((String) request.get("phone"));
            
            Object tenantIdObj = request.get("tenantId");
            if (tenantIdObj != null) {
                user.setTenantId((Integer) tenantIdObj);
            }
            
            @SuppressWarnings("unchecked")
            List<Integer> roleIds = (List<Integer>) request.get("roleIds");
            
            User createdUser = userService.createUser(user, roleIds);
            response.put("success", true);
            response.put("data", createdUser);
            response.put("message", "用户创建成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建用户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = new User();
            user.setUsername((String) request.get("username"));
            user.setName((String) request.get("name"));
            user.setEmail((String) request.get("email"));
            user.setPhone((String) request.get("phone"));
            
            Object tenantIdObj = request.get("tenantId");
            if (tenantIdObj != null) {
                user.setTenantId((Integer) tenantIdObj);
            } else if (request.containsKey("tenantId")) {
                user.setTenantId(null);
            }
            
            @SuppressWarnings("unchecked")
            List<Integer> roleIds = (List<Integer>) request.get("roleIds");
            
            User updatedUser = userService.updateUser(id, user, roleIds);
            if (updatedUser != null) {
                response.put("success", true);
                response.put("data", updatedUser);
                response.put("message", "用户更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新用户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.deleteUser(id);
            response.put("success", true);
            response.put("message", "用户删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除用户失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String statusStr = request.get("status");
            User.Status status = User.Status.valueOf(statusStr);
            User updatedUser = userService.updateUserStatus(id, status);
            if (updatedUser != null) {
                response.put("success", true);
                response.put("data", updatedUser);
                response.put("message", "用户状态更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新用户状态失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String newPassword = request.get("password");
            User updatedUser = userService.resetPassword(id, newPassword);
            if (updatedUser != null) {
                response.put("success", true);
                response.put("message", "密码重置成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "重置密码失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
