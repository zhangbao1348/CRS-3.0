package com.crs.controller;

import com.crs.entity.RateType;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.service.RateTypeService;
import com.crs.util.CodeValidator;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RateTypeController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RateTypeController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/10-价格计划管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RateTypeController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/rate-types")
@CrossOrigin(origins = "*")
public class RateTypeController {
    
    @Autowired
    private RateTypeService rateTypeService;

    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRateTypes() {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer tenantId = getCurrentTenantId();
            List<RateType> rateTypes = rateTypeService.getAllRateTypes(tenantId);
            response.put("success", true);
            response.put("data", rateTypes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取房价大类列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getRateTypeById(@PathVariable Integer id) {
        try {
            Integer tenantId = getCurrentTenantId();
            RateType rateType = rateTypeService.getRateTypeById(tenantId, id);
            if (rateType == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "房价大类不存在"));
            }
            return ResponseEntity.ok(rateType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getRateTypeByCode(@PathVariable String code) {
        try {
            Integer tenantId = getCurrentTenantId();
            RateType rateType = rateTypeService.getRateTypeByCode(tenantId, code);
            if (rateType == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "房价大类不存在"));
            }
            return ResponseEntity.ok(rateType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<RateType>> getActiveRateTypes() {
        Integer tenantId = getCurrentTenantId();
        List<RateType> rateTypes = rateTypeService.getActiveRateTypes(tenantId);
        return ResponseEntity.ok(rateTypes);
    }
    
    @PostMapping
    public ResponseEntity<?> createRateType(@RequestBody RateType rateType) {
        try {
            Integer tenantId = getCurrentTenantId();
            if (rateType.getCode() != null && !CodeValidator.isValid(rateType.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            if (!rateTypeService.isCodeUnique(tenantId, rateType.getCode(), null)) {
                return ResponseEntity.badRequest().body(Map.of("error", "编码已存在"));
            }
            RateType createdRateType = rateTypeService.createRateType(tenantId, rateType);
            return ResponseEntity.ok(createdRateType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRateType(@PathVariable Integer id, @RequestBody RateType rateType) {
        try {
            Integer tenantId = getCurrentTenantId();
            if (rateType.getCode() != null && !CodeValidator.isValid(rateType.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            if (!rateTypeService.isCodeUnique(tenantId, rateType.getCode(), id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "编码已存在"));
            }
            rateType.setId(id);
            RateType updatedRateType = rateTypeService.updateRateType(tenantId, rateType);
            if (updatedRateType != null) {
                return ResponseEntity.ok(updatedRateType);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "房价大类不存在"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRateType(@PathVariable Integer id) {
        try {
            Integer tenantId = getCurrentTenantId();
            // 检查是否被房价码引用
            RateType existing = rateTypeService.getRateTypeById(tenantId, id);
            if (existing != null) {
                long refCount = groupRateCodeRepository.countByRateCategory(existing.getCode());
                if (refCount > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "该房价大类已被 " + refCount + " 个房价码引用，无法删除"));
                }
            }
            rateTypeService.deleteRateType(tenantId, id);
            return ResponseEntity.ok(Map.of("message", "房价大类删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<RateType>> batchCreateRateTypes(
            @RequestParam(required = false) Integer tenantId,
            @RequestBody List<RateType> rateTypes) {
        try {
            if (tenantId == null) {
                tenantId = getCurrentTenantId();
            }
            List<RateType> createdRateTypes = rateTypeService.batchCreateRateTypes(tenantId, rateTypes);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRateTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/init-default/{tenantId}")
    public ResponseEntity<List<RateType>> initDefaultRateTypesForTenant(@PathVariable Integer tenantId) {
        try {
            List<RateType> defaultRateTypes = rateTypeService.initDefaultRateTypesForTenant(tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(defaultRateTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
