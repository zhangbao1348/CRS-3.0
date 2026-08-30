package com.crs.controller;

import com.crs.entity.MarketCode;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.service.MarketCodeService;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MarketCodeController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【MarketCodeController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 MarketCodeController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/market-codes")
public class MarketCodeController {

    @Autowired
    private MarketCodeService marketCodeService;

    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;

    @Autowired
    private RatePlanRepository ratePlanRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllMarketCodes() {
        try {
            List<Map<String, Object>> treeData = marketCodeService.getAllMarketCodesAsTree();
            return ResponseEntity.ok(treeData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 兼容性接口：根据 groupId 获取所有市场码（实际使用当前登录租户 ID）
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Map<String, Object>>> getMarketCodesByGroupId(@PathVariable Integer groupId) {
        // 忽略路径中的 groupId，直接使用当前租户上下文
        return getAllMarketCodes();
    }

    @GetMapping("/third-level")
    public ResponseEntity<List<MarketCode>> getThirdLevelMarketCodes() {
        try {
            List<MarketCode> thirdLevelCodes = marketCodeService.getThirdLevelMarketCodes();
            return ResponseEntity.ok(thirdLevelCodes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<MarketCode>> getMarketCodesByParentId(@PathVariable Integer parentId) {
        try {
            List<MarketCode> marketCodes = marketCodeService.getMarketCodesByParentId(parentId);
            return ResponseEntity.ok(marketCodes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createMarketCode(@RequestBody MarketCode marketCode) {
        try {
            if (marketCode.getCode() == null || marketCode.getName() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "市场码编码和名称为必填项"));
            }
            if (!CodeValidator.isValid(marketCode.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            if (!marketCodeService.isCodeUnique(marketCode.getCode(), null)) {
                return ResponseEntity.badRequest().body(Map.of("error", "市场码编码已存在"));
            }
            MarketCode createdMarketCode = marketCodeService.createMarketCode(marketCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMarketCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMarketCode(@PathVariable Integer id, @RequestBody MarketCode marketCode) {
        try {
            if (marketCode.getCode() != null && !CodeValidator.isValid(marketCode.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            if (!marketCodeService.isCodeUnique(marketCode.getCode(), id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "市场码编码已存在"));
            }
            marketCode.setId(id);
            MarketCode updatedMarketCode = marketCodeService.updateMarketCode(marketCode);
            if (updatedMarketCode != null) {
                return ResponseEntity.ok(updatedMarketCode);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMarketCode(@PathVariable Integer id) {
        try {
            // 检查是否被房价码引用
            MarketCode existing = marketCodeService.getMarketCodeById(id);
            if (existing != null) {
                long refCount = countSubtreeReferences(existing);
                if (refCount > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "该市场码或其子节点已被 " + refCount + " 个房价码引用，无法删除"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "市场码不存在"));
            }
            marketCodeService.deleteMarketCode(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(@RequestParam String code, @RequestParam(required = false) Integer id) {
        try {
            boolean isUnique = marketCodeService.isCodeUnique(code, id);
            return ResponseEntity.ok(Map.of("unique", isUnique));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /** 同时检查父节点与所有子节点，防止递归删除绕过引用约束。 */
    private long countSubtreeReferences(MarketCode node) {
        Integer tenantId = getCurrentTenantId();
        long count = groupRateCodeRepository.countByGroupIdAndMarketCode(tenantId, node.getCode())
                + ratePlanRepository.countByTenantIdAndMarketCode(tenantId, node.getCode());
        for (MarketCode child : marketCodeService.getMarketCodesByParentId(node.getId())) {
            count += countSubtreeReferences(child);
        }
        return count;
    }
}
