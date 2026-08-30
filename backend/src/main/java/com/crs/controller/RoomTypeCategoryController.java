package com.crs.controller;

import com.crs.entity.RoomTypeCategory;
import com.crs.repository.GroupRoomTypeRepository;
import com.crs.service.RoomTypeCategoryService;
import com.crs.util.CodeValidator;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RoomTypeCategoryController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RoomTypeCategoryController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/12-房型管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RoomTypeCategoryController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/room-type-categories")
@CrossOrigin(origins = "*")
public class RoomTypeCategoryController {
    
    private final RoomTypeCategoryService roomTypeCategoryService;
    private final GroupRoomTypeRepository groupRoomTypeRepository;

    @Autowired
    public RoomTypeCategoryController(RoomTypeCategoryService roomTypeCategoryService, GroupRoomTypeRepository groupRoomTypeRepository) {
        this.roomTypeCategoryService = roomTypeCategoryService;
        this.groupRoomTypeRepository = groupRoomTypeRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getRoomTypeCategories() {
        try {
            List<RoomTypeCategory> categories = roomTypeCategoryService.getAllRoomTypeCategories();
            List<Map<String, Object>> result = categories.stream().map(category -> {
                Map<String, Object> item = new HashMap<>();
                item.put("key", category.getId().toString());
                item.put("title", category.getCategoryName());
                item.put("code", category.getCategoryCode());
                item.put("id", category.getId());
                return item;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(
            @RequestParam String code,
            @RequestParam(required = false) Integer id) {
        boolean isUnique = roomTypeCategoryService.isCodeUnique(code, id);
        return ResponseEntity.ok(Map.of("unique", isUnique));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomTypeCategoryById(@PathVariable Integer id) {
        try {
            RoomTypeCategory category = roomTypeCategoryService.getRoomTypeCategoryById(id);
            if (category == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "房型大类不存在或无权访问"));
            }
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getRoomTypeCategoryByCode(@PathVariable String code) {
        try {
            RoomTypeCategory category = roomTypeCategoryService.getRoomTypeCategoryByCode(code);
            if (category == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "房型大类不存在或无权访问"));
            }
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<RoomTypeCategory>> getActiveRoomTypeCategories() {
        List<RoomTypeCategory> categories = roomTypeCategoryService.getActiveRoomTypeCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/group/current")
    public ResponseEntity<List<Map<String, Object>>> getRoomTypeCategoriesByCurrentTenant() {
        try {
            List<RoomTypeCategory> categories = roomTypeCategoryService.getRoomTypeCategoriesByGroupIdAndStatus("active");
            List<Map<String, Object>> result = categories.stream().map(category -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", category.getId());
                item.put("categoryName", category.getCategoryName());
                item.put("categoryCode", category.getCategoryCode());
                return item;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 兼容性接口：根据 groupId 获取房型大类（实际使用当前登录租户 ID）
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Map<String, Object>>> getRoomTypeCategoriesByGroupId(@PathVariable Integer groupId) {
        // 忽略路径中的 groupId，直接使用当前租户上下文
        return getRoomTypeCategoriesByCurrentTenant();
    }
    
    @PostMapping
    public ResponseEntity<?> createRoomTypeCategory(@RequestBody RoomTypeCategory roomTypeCategory) {
        try {
            if (roomTypeCategory.getCategoryCode() == null || roomTypeCategory.getCategoryName() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "房型大类代码和名称为必填项"));
            }
            if (!CodeValidator.isValid(roomTypeCategory.getCategoryCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            if (!roomTypeCategoryService.isCodeUnique(roomTypeCategory.getCategoryCode(), null)) {
                return ResponseEntity.badRequest().body(Map.of("error", "房型大类代码已存在"));
            }
            RoomTypeCategory createdCategory = roomTypeCategoryService.createRoomTypeCategory(roomTypeCategory);
            Map<String, Object> result = new HashMap<>();
            result.put("key", createdCategory.getId().toString());
            result.put("title", createdCategory.getCategoryName());
            result.put("code", createdCategory.getCategoryCode());
            result.put("id", createdCategory.getId());
            result.put("name", createdCategory.getCategoryName());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoomTypeCategory(@PathVariable Integer id, @RequestBody RoomTypeCategory roomTypeCategory) {
        try {
            if (roomTypeCategory.getCategoryCode() == null || roomTypeCategory.getCategoryName() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "房型大类代码和名称为必填项"));
            }
            if (!CodeValidator.isValid(roomTypeCategory.getCategoryCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            if (!roomTypeCategoryService.isCodeUnique(roomTypeCategory.getCategoryCode(), id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "房型大类代码已存在"));
            }
            roomTypeCategory.setId(id);
            RoomTypeCategory updatedCategory = roomTypeCategoryService.updateRoomTypeCategory(roomTypeCategory);
            if (updatedCategory != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("key", updatedCategory.getId().toString());
                result.put("title", updatedCategory.getCategoryName());
                result.put("code", updatedCategory.getCategoryCode());
                result.put("id", updatedCategory.getId());
                result.put("name", updatedCategory.getCategoryName());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "房型大类不存在或无权访问"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoomTypeCategory(@PathVariable Integer id) {
        try {
            RoomTypeCategory existing = roomTypeCategoryService.getRoomTypeCategoryById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "房型大类不存在或无权访问"));
            }
            // 检查是否被集团房型引用
            Integer tenantId = TenantContext.getTenantId();
            long refCount = groupRoomTypeRepository.countByGroupIdAndRoomTypeCategoryId(tenantId, id);
            if (refCount > 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "该房型大类已被 " + refCount + " 个集团房型引用，无法删除"));
            }
            roomTypeCategoryService.deleteRoomTypeCategory(id);
            return ResponseEntity.ok(Map.of("message", "房型大类删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableRoomTypeCategory(@PathVariable Integer id) {
        try {
            RoomTypeCategory enabledCategory = roomTypeCategoryService.enableRoomTypeCategory(id);
            if (enabledCategory != null) {
                return ResponseEntity.ok(enabledCategory);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "房型大类不存在或无权访问"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableRoomTypeCategory(@PathVariable Integer id) {
        try {
            RoomTypeCategory disabledCategory = roomTypeCategoryService.disableRoomTypeCategory(id);
            if (disabledCategory != null) {
                return ResponseEntity.ok(disabledCategory);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "房型大类不存在或无权访问"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
