package com.crs.controller;

import com.crs.service.DictionaryItemService;
import com.crs.util.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dictionaries")
@CrossOrigin(origins = "*")
public class DictionaryController {

    private final DictionaryItemService dictionaryItemService;

    public DictionaryController(DictionaryItemService dictionaryItemService) {
        this.dictionaryItemService = dictionaryItemService;
    }

    @GetMapping("/{typeCode}/items")
    public ResponseEntity<?> getActiveItems(@PathVariable String typeCode) {
        try {
            return ResponseEntity.ok(dictionaryItemService.getActiveDictionaryOptions(getCurrentTenantId(), typeCode));
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
