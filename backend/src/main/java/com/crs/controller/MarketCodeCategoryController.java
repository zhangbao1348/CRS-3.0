package com.crs.controller;

import com.crs.entity.MarketCodeCategory;
import com.crs.service.MarketCodeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MarketCodeCategoryController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【MarketCodeCategoryController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 MarketCodeCategoryController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/market-code-categories")
public class MarketCodeCategoryController {

    @Autowired
    private MarketCodeCategoryService marketCodeCategoryService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllMarketCodeCategories() {
        try {
            List<Map<String, Object>> categories = marketCodeCategoryService.getAllMarketCodeCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketCodeCategory> getMarketCodeCategoryById(@PathVariable Integer id) {
        try {
            MarketCodeCategory category = marketCodeCategoryService.getMarketCodeCategoryById(id);
            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<MarketCodeCategory> createMarketCodeCategory(@RequestBody MarketCodeCategory marketCodeCategory) {
        try {
            MarketCodeCategory created = marketCodeCategoryService.createMarketCodeCategory(marketCodeCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarketCodeCategory> updateMarketCodeCategory(@PathVariable Integer id, @RequestBody MarketCodeCategory marketCodeCategory) {
        try {
            marketCodeCategory.setId(id);
            MarketCodeCategory updated = marketCodeCategoryService.updateMarketCodeCategory(marketCodeCategory);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarketCodeCategory(@PathVariable Integer id) {
        try {
            marketCodeCategoryService.deleteMarketCodeCategory(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(@RequestParam String code, @RequestParam(required = false) Integer id) {
        try {
            boolean isUnique = marketCodeCategoryService.isCodeUnique(code, id);
            return ResponseEntity.ok(Map.of("unique", isUnique));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
