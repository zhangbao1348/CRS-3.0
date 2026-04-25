package com.crs.controller;

import com.crs.entity.MarketCodeCategory;
import com.crs.service.MarketCodeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market-code-categories")
public class MarketCodeCategoryController {

    @Autowired
    private MarketCodeCategoryService marketCodeCategoryService;

    private static final Integer DEFAULT_TENANT_ID = 1;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllMarketCodeCategories() {
        try {
            List<Map<String, Object>> categories = marketCodeCategoryService.getAllMarketCodeCategories(DEFAULT_TENANT_ID);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketCodeCategory> getMarketCodeCategoryById(@PathVariable Integer id) {
        try {
            MarketCodeCategory category = marketCodeCategoryService.getMarketCodeCategoryById(DEFAULT_TENANT_ID, id);
            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<MarketCodeCategory> createMarketCodeCategory(@RequestBody MarketCodeCategory marketCodeCategory) {
        try {
            MarketCodeCategory created = marketCodeCategoryService.createMarketCodeCategory(DEFAULT_TENANT_ID, marketCodeCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarketCodeCategory> updateMarketCodeCategory(@PathVariable Integer id, @RequestBody MarketCodeCategory marketCodeCategory) {
        try {
            marketCodeCategory.setId(id);
            MarketCodeCategory updated = marketCodeCategoryService.updateMarketCodeCategory(DEFAULT_TENANT_ID, marketCodeCategory);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarketCodeCategory(@PathVariable Integer id) {
        try {
            marketCodeCategoryService.deleteMarketCodeCategory(DEFAULT_TENANT_ID, id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(@RequestParam String code, @RequestParam(required = false) Integer id) {
        try {
            boolean isUnique = marketCodeCategoryService.isCodeUnique(DEFAULT_TENANT_ID, code, id);
            return ResponseEntity.ok(Map.of("unique", isUnique));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
