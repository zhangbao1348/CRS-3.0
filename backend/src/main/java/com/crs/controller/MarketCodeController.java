package com.crs.controller;

import com.crs.entity.MarketCode;
import com.crs.service.MarketCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 市场码控制器
 * 提供市场码的CRUD操作API
 */
@RestController
@RequestMapping("/api/market-codes")
public class MarketCodeController {

    @Autowired
    private MarketCodeService marketCodeService;

    /**
     * 获取所有市场码（树形结构）
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllMarketCodes() {
        try {
            List<Map<String, Object>> treeData = marketCodeService.getAllMarketCodesAsTree();
            return ResponseEntity.ok(treeData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据父ID获取市场码
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<MarketCode>> getMarketCodesByParentId(@PathVariable Integer parentId) {
        try {
            List<MarketCode> marketCodes = marketCodeService.getMarketCodesByParentId(parentId);
            return ResponseEntity.ok(marketCodes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据ID获取市场码
     */
    @GetMapping("/{id}")
    public ResponseEntity<MarketCode> getMarketCodeById(@PathVariable Integer id) {
        try {
            MarketCode marketCode = marketCodeService.getMarketCodeById(id);
            if (marketCode != null) {
                return ResponseEntity.ok(marketCode);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 创建市场码
     */
    @PostMapping
    public ResponseEntity<MarketCode> createMarketCode(@RequestBody MarketCode marketCode) {
        try {
            MarketCode createdMarketCode = marketCodeService.createMarketCode(marketCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMarketCode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 更新市场码
     */
    @PutMapping("/{id}")
    public ResponseEntity<MarketCode> updateMarketCode(@PathVariable Integer id, @RequestBody MarketCode marketCode) {
        try {
            marketCode.setId(id);
            MarketCode updatedMarketCode = marketCodeService.updateMarketCode(marketCode);
            if (updatedMarketCode != null) {
                return ResponseEntity.ok(updatedMarketCode);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 删除市场码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarketCode(@PathVariable Integer id) {
        try {
            marketCodeService.deleteMarketCode(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 检查市场码CODE是否唯一
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(@RequestParam String code, @RequestParam(required = false) Integer id) {
        try {
            boolean isUnique = marketCodeService.isCodeUnique(code, id);
            return ResponseEntity.ok(Map.of("unique", isUnique));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
