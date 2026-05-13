package com.crs.controller;

import com.crs.entity.GuaranteePolicy;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.GuaranteePolicyRepository;
import com.crs.service.GuaranteePolicyService;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 担保政策控制器
 * 提供担保政策管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/guarantee-policies")
public class GuaranteePolicyController {
    
    @Autowired
    private GuaranteePolicyService guaranteePolicyService;
    
    @Autowired
    private GuaranteePolicyRepository guaranteePolicyRepository;
    
    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;
    
    /**
     * 获取所有担保政策
     * @return 担保政策列表
     */
    @GetMapping
    public ResponseEntity<List<GuaranteePolicy>> getAllPolicies() {
        List<GuaranteePolicy> policies = guaranteePolicyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    /**
     * 兼容性接口：根据 groupId 获取所有担保政策（实际使用当前登录租户 ID）
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GuaranteePolicy>> getPoliciesByGroupId(@PathVariable Integer groupId) {
        // 忽略路径中的 groupId，直接使用当前租户上下文
        return getAllPolicies();
    }
    
    /**
     * 根据ID获取担保政策
     * @param id 政策ID
     * @return 担保政策
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPolicyById(@PathVariable Integer id) {
        return guaranteePolicyService.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("担保政策不存在或无权访问"));
    }
    
    /**
     * 创建担保政策
     * @param policy 担保政策
     * @return 创建的担保政策
     */
    @PostMapping
    public ResponseEntity<?> createPolicy(@RequestBody GuaranteePolicy policy) {
        try {
            if (policy.getCode() != null && !CodeValidator.isValid(policy.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            GuaranteePolicy created = guaranteePolicyService.create(policy);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新担保政策
     * @param id 政策ID
     * @param policy 担保政策
     * @return 更新后的担保政策
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePolicy(@PathVariable Integer id, @RequestBody GuaranteePolicy policy) {
        try {
            if (policy.getCode() != null && !CodeValidator.isValid(policy.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            GuaranteePolicy updated = guaranteePolicyService.update(id, policy);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 删除担保政策
     * @param id 政策ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePolicy(@PathVariable Integer id) {
        try {
            // 检查是否被房价码引用
            Optional<GuaranteePolicy> policyOpt = guaranteePolicyService.getById(id);
            if (policyOpt.isPresent()) {
                long refCount = groupRateCodeRepository.countByGuaranteeRule(policyOpt.get().getCode());
                if (refCount > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "该担保政策已被 " + refCount + " 个房价码引用，无法删除"));
                }
            }
            guaranteePolicyService.delete(id);
            return ResponseEntity.ok("担保政策删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // ===== CODE-based endpoints =====
    
    /**
     * 根据政策代码更新担保政策
     * @param code 政策代码
     * @param policy 担保政策
     * @return 更新后的担保政策
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<?> updatePolicyByCode(@PathVariable String code, @RequestBody GuaranteePolicy policy) {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("租户上下文丢失");
        }
        GuaranteePolicy existing = guaranteePolicyRepository.findByTenantIdAndCode(tenantId, code);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("担保政策不存在");
        }
        try {
            GuaranteePolicy updated = guaranteePolicyService.update(existing.getId(), policy);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
