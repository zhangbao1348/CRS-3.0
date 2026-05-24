package com.crs.controller;

import com.crs.entity.DictionaryType;
import com.crs.service.DictionaryTypeService;
import com.crs.util.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dictionary-types")
@CrossOrigin(origins = "*")
public class DictionaryTypeController {

    private final DictionaryTypeService dictionaryTypeService;

    public DictionaryTypeController(DictionaryTypeService dictionaryTypeService) {
        this.dictionaryTypeService = dictionaryTypeService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDictionaryTypes(@RequestParam(required = false) String keyword) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("data", dictionaryTypeService.getDictionaryTypes(getCurrentTenantId(), keyword));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取字典类型列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createDictionaryType(@RequestBody DictionaryType dictionaryType) {
        try {
            return ResponseEntity.ok(dictionaryTypeService.createDictionaryType(getCurrentTenantId(), dictionaryType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDictionaryType(@PathVariable Integer id, @RequestBody DictionaryType dictionaryType) {
        try {
            return ResponseEntity.ok(dictionaryTypeService.updateDictionaryType(getCurrentTenantId(), id, dictionaryType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDictionaryType(@PathVariable Integer id) {
        try {
            dictionaryTypeService.deleteDictionaryType(getCurrentTenantId(), id);
            return ResponseEntity.ok(Map.of("message", "字典类型删除成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant context missing");
        }
        return tenantId;
    }
}
