package com.crs.controller;

import com.crs.entity.MarketCode;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.service.MarketCodeService;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market-codes")
public class MarketCodeController {

    @Autowired
    private MarketCodeService marketCodeService;

    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;

    private static final Integer DEFAULT_TENANT_ID = 1;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllMarketCodes() {
        try {
            List<Map<String, Object>> treeData = marketCodeService.getAllMarketCodesAsTree(DEFAULT_TENANT_ID);
            return ResponseEntity.ok(treeData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/third-level")
    public ResponseEntity<List<MarketCode>> getThirdLevelMarketCodes() {
        try {
            List<MarketCode> thirdLevelCodes = marketCodeService.getThirdLevelMarketCodes(DEFAULT_TENANT_ID);
            return ResponseEntity.ok(thirdLevelCodes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<MarketCode>> getMarketCodesByParentId(@PathVariable Integer parentId) {
        try {
            List<MarketCode> marketCodes = marketCodeService.getMarketCodesByParentId(DEFAULT_TENANT_ID, parentId);
            return ResponseEntity.ok(marketCodes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketCode> getMarketCodeById(@PathVariable Integer id) {
        try {
            MarketCode marketCode = marketCodeService.getMarketCodeById(DEFAULT_TENANT_ID, id);
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

    @PostMapping
    public ResponseEntity<?> createMarketCode(@RequestBody MarketCode marketCode) {
        try {
            if (marketCode.getCode() != null && !CodeValidator.isValid(marketCode.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            MarketCode createdMarketCode = marketCodeService.createMarketCode(DEFAULT_TENANT_ID, marketCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMarketCode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMarketCode(@PathVariable Integer id, @RequestBody MarketCode marketCode) {
        try {
            if (marketCode.getCode() != null && !CodeValidator.isValid(marketCode.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            marketCode.setId(id);
            MarketCode updatedMarketCode = marketCodeService.updateMarketCode(DEFAULT_TENANT_ID, marketCode);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMarketCode(@PathVariable Integer id) {
        try {
            // 检查是否被房价码引用
            MarketCode existing = marketCodeService.getMarketCodeById(DEFAULT_TENANT_ID, id);
            if (existing != null) {
                long refCount = groupRateCodeRepository.countByMarketCode(existing.getCode());
                if (refCount > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "该市场码已被 " + refCount + " 个房价码引用，无法删除"));
                }
            }
            marketCodeService.deleteMarketCode(DEFAULT_TENANT_ID, id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(@RequestParam String code, @RequestParam(required = false) Integer id) {
        try {
            boolean isUnique = marketCodeService.isCodeUnique(DEFAULT_TENANT_ID, code, id);
            return ResponseEntity.ok(Map.of("unique", isUnique));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
