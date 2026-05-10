package com.crs.controller;

import com.crs.entity.Menu;
import com.crs.entity.User;
import com.crs.service.PermissionService;
import com.crs.service.UserService;
import com.crs.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AuthController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【AuthController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 AuthController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            System.out.println("[DEBUG] Login request received:");
            System.out.println("[DEBUG] Username: " + username);
            System.out.println("[DEBUG] Password: " + password);
            System.out.println("[DEBUG] Request body: " + request.toString());

            Optional<User> userOpt = userService.getUserByUsername(username);
            if (!userOpt.isPresent()) {
                System.out.println("[DEBUG] User not found: " + username);
                response.put("success", false);
                response.put("message", "用户名或密码错误");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userOpt.get();
            System.out.println("[DEBUG] User found: " + user.getUsername());
            System.out.println("[DEBUG] Stored password hash: " + user.getPassword());
            System.out.println("[DEBUG] Attempting to match password: " + password);
            boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
            System.out.println("[DEBUG] Password match result: " + passwordMatch);
            if (!passwordMatch) {
                response.put("success", false);
                response.put("message", "用户名或密码错误");
                return ResponseEntity.badRequest().body(response);
            }

            if (user.getStatus() != User.Status.active) {
                response.put("success", false);
                response.put("message", "账户已被禁用");
                return ResponseEntity.badRequest().body(response);
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getTenantId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getTenantId());

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("tenantId", user.getTenantId());
            userData.put("username", user.getUsername());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("avatar", user.getAvatar());

            // 获取用户菜单
            List<Menu> menus = permissionService.getUserMenus(user.getId(), "crs");
            System.out.println("[DEBUG] User menus size: " + menus.size());
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", userData);
            data.put("token", token);
            data.put("refreshToken", refreshToken);
            data.put("menus", menus);
            
            response.put("success", true);
            response.put("data", data);
            response.put("message", "登录成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String refreshToken = request.get("refreshToken");
            String username = jwtUtil.extractUsername(refreshToken);
            if (jwtUtil.validateToken(refreshToken, username)) {
                Integer tenantId = jwtUtil.extractTenantId(refreshToken);
                String newToken = jwtUtil.generateToken(username, tenantId);
                response.put("success", true);
                response.put("token", newToken);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "无效的刷新令牌");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刷新令牌失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshTokenOld(@RequestBody Map<String, String> request) {
        return refreshToken(request);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = request.get("username");
            String password = request.get("password");
            String name = request.get("name");
            String email = request.get("email");

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setEmail(email);

            User createdUser = userService.createUser(user, null);
            response.put("success", true);
            response.put("data", createdUser);
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "注册失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/user/{userId}/menus/{systemType}")
    public ResponseEntity<Map<String, Object>> getUserMenus(
            @PathVariable Integer userId,
            @PathVariable String systemType) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Menu> menus = permissionService.getUserMenus(userId, systemType);
            response.put("success", true);
            response.put("data", menus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取菜单失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/user/{userId}/permissions")
    public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> permissions = permissionService.getUserPermissions(userId);
            response.put("success", true);
            response.put("data", permissions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取权限失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
