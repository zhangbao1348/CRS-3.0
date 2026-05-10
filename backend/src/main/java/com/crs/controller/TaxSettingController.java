package com.crs.controller;

import com.crs.entity.TaxSetting;
import com.crs.repository.TaxSettingRepository;
import com.crs.service.TaxSettingService;
import com.crs.util.CodeValidator;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TaxSettingController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【TaxSettingController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 TaxSettingController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/tax-settings")
@CrossOrigin(origins = "*")
public class TaxSettingController {
    
    @Autowired
    private TaxSettingService taxSettingService;
    
    @Autowired
    private TaxSettingRepository taxSettingRepository;
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            tenantId = 1;
        }
        return tenantId;
    }
    
    @GetMapping
    public ResponseEntity<List<TaxSetting>> getAllTaxSettings() {
        try {
            Integer tenantId = getCurrentTenantId();
            List<TaxSetting> taxSettings = taxSettingService.getAllTaxSettings(tenantId);
            return ResponseEntity.ok(taxSettings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaxSetting> getTaxSettingById(@PathVariable Integer id) {
        try {
            Integer tenantId = getCurrentTenantId();
            Optional<TaxSetting> taxSetting = taxSettingService.getById(tenantId, id);
            if (taxSetting.isPresent()) {
                return ResponseEntity.ok(taxSetting.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createTaxSetting(@RequestBody TaxSetting taxSetting) {
        try {
            Integer tenantId = getCurrentTenantId();
            if (taxSetting.getTaxCode() != null && !CodeValidator.isValid(taxSetting.getTaxCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            TaxSetting created = taxSettingService.create(tenantId, taxSetting);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaxSetting(@PathVariable Integer id, @RequestBody TaxSetting taxSetting) {
        try {
            Integer tenantId = getCurrentTenantId();
            if (taxSetting.getTaxCode() != null && !CodeValidator.isValid(taxSetting.getTaxCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            TaxSetting updated = taxSettingService.update(tenantId, id, taxSetting);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTaxSetting(@PathVariable Integer id) {
        try {
            Integer tenantId = getCurrentTenantId();
            taxSettingService.delete(tenantId, id);
            return ResponseEntity.ok(Map.of("message", "税费设置删除成功"));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/batch")
    public ResponseEntity<List<TaxSetting>> batchCreateTaxSettings(
            @RequestParam(required = false) Integer tenantId,
            @RequestBody List<TaxSetting> taxSettings) {
        try {
            if (tenantId == null) {
                tenantId = getCurrentTenantId();
            }
            List<TaxSetting> createdTaxSettings = taxSettingService.batchCreateTaxSettings(tenantId, taxSettings);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTaxSettings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/init-default/{tenantId}")
    public ResponseEntity<List<TaxSetting>> initDefaultTaxSettingsForTenant(@PathVariable Integer tenantId) {
        try {
            List<TaxSetting> defaultTaxSettings = taxSettingService.initDefaultTaxSettingsForTenant(tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(defaultTaxSettings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // ===== CODE-based endpoints =====
    
    /**
     * 根据税率编码获取税率设置
     * @param code 税率编码
     * @return 税率设置
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<TaxSetting> getTaxSettingByCode(@PathVariable String code) {
        try {
            Integer tenantId = getCurrentTenantId();
            TaxSetting taxSetting = taxSettingRepository.findByTenantIdAndTaxCode(tenantId, code);
            if (taxSetting == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(taxSetting);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * 根据税率编码更新税率设置
     * @param code 税率编码
     * @param taxSetting 税率设置
     * @return 更新后的税率设置
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<TaxSetting> updateTaxSettingByCode(@PathVariable String code, @RequestBody TaxSetting taxSetting) {
        try {
            Integer tenantId = getCurrentTenantId();
            TaxSetting existing = taxSettingRepository.findByTenantIdAndTaxCode(tenantId, code);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            TaxSetting updated = taxSettingService.update(tenantId, existing.getId(), taxSetting);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
