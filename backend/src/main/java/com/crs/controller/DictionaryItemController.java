package com.crs.controller;

import com.crs.entity.DictionaryItem;
import com.crs.service.DictionaryItemService;
import com.crs.util.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dictionary-items")
@CrossOrigin(origins = "*")
public class DictionaryItemController {

    private final DictionaryItemService dictionaryItemService;

    public DictionaryItemController(DictionaryItemService dictionaryItemService) {
        this.dictionaryItemService = dictionaryItemService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDictionaryItems(@RequestParam String typeCode,
                                                                  @RequestParam(required = false) String keyword) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("data", dictionaryItemService.getDictionaryItems(getCurrentTenantId(), typeCode, keyword));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取字典项列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createDictionaryItem(@RequestBody DictionaryItem dictionaryItem) {
        try {
            return ResponseEntity.ok(dictionaryItemService.createDictionaryItem(getCurrentTenantId(), dictionaryItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDictionaryItem(@PathVariable Integer id, @RequestBody DictionaryItem dictionaryItem) {
        try {
            return ResponseEntity.ok(dictionaryItemService.updateDictionaryItem(getCurrentTenantId(), id, dictionaryItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDictionaryItem(@PathVariable Integer id) {
        try {
            dictionaryItemService.deleteDictionaryItem(getCurrentTenantId(), id);
            return ResponseEntity.ok(Map.of("message", "字典项删除成功"));
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
