package com.crs.controller;

import com.crs.entity.CancellationPolicy;
import com.crs.repository.CancellationPolicyRepository;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.service.CancellationPolicyService;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 取消政策控制器
 * 提供取消政策管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/cancellation-policies")
public class CancellationPolicyController {
    
    @Autowired
    private CancellationPolicyService cancellationPolicyService;
    
    @Autowired
    private CancellationPolicyRepository cancellationPolicyRepository;
    
    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;
    
    // 默认租户ID
    private static final Integer DEFAULT_TENANT_ID = 1;
    
    /**
     * 获取所有取消政策
     * @param tenantId 租户ID（可选）
     * @return 取消政策列表
     */
    @GetMapping
    public ResponseEntity<List<CancellationPolicy>> getAllPolicies(
            @RequestParam(required = false) Integer tenantId) {
        Integer actualTenantId = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
        List<CancellationPolicy> policies = cancellationPolicyService.getByTenantId(actualTenantId);
        return ResponseEntity.ok(policies);
    }
    
    /**
     * 根据ID获取取消政策
     * @param id 政策ID
     * @return 取消政策
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPolicyById(@PathVariable Integer id) {
        Optional<CancellationPolicy> policy = cancellationPolicyService.getById(id);
        if (policy.isPresent()) {
            return ResponseEntity.ok(policy.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("取消政策不存在");
        }
    }
    
    /**
     * 创建取消政策
     * @param policy 取消政策
     * @param tenantId 租户ID（可选）
     * @return 创建的取消政策
     */
    @PostMapping
    public ResponseEntity<?> createPolicy(
            @RequestBody CancellationPolicy policy,
            @RequestParam(required = false) Integer tenantId) {
        try {
            if (policy.getCode() != null && !CodeValidator.isValid(policy.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            Integer actualTenantId = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
            CancellationPolicy created = cancellationPolicyService.create(actualTenantId, policy);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新取消政策
     * @param id 政策ID
     * @param policy 取消政策
     * @return 更新后的取消政策
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePolicy(@PathVariable Integer id, @RequestBody CancellationPolicy policy) {
        try {
            if (policy.getCode() != null && !CodeValidator.isValid(policy.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            CancellationPolicy updated = cancellationPolicyService.update(id, policy);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 删除取消政策
     * @param id 政策ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePolicy(@PathVariable Integer id) {
        try {
            // 检查是否被房价码引用
            Optional<CancellationPolicy> policyOpt = cancellationPolicyService.getById(id);
            if (policyOpt.isPresent()) {
                long refCount = groupRateCodeRepository.countByCancellationRule(policyOpt.get().getCode());
                if (refCount > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "该取消政策已被 " + refCount + " 个房价码引用，无法删除"));
                }
            }
            cancellationPolicyService.delete(id);
            return ResponseEntity.ok("取消政策删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 检查取消政策代码是否唯一
     * @param code 代码
     * @param tenantId 租户ID
     * @param id 排除的ID（可选）
     * @return 是否唯一
     */
    // @GetMapping("/check-code")
    // public ResponseEntity<Boolean> checkCodeUnique(
    //         @RequestParam String code,
    //         @RequestParam Integer tenantId,
    //         @RequestParam(required = false) Integer id) {
    //     boolean isUnique = cancellationPolicyService.isCodeUnique(code, tenantId, id);
    //     return ResponseEntity.ok(isUnique);
    // }
    
    // ===== CODE-based endpoints =====
    
    /**
     * 根据政策代码更新取消政策
     * @param code 政策代码
     * @param policy 取消政策
     * @return 更新后的取消政策
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<?> updatePolicyByCode(@PathVariable String code, @RequestBody CancellationPolicy policy) {
        Integer tenantId = com.crs.util.TenantContext.getTenantId() != null ? com.crs.util.TenantContext.getTenantId() : DEFAULT_TENANT_ID;
        CancellationPolicy existing = cancellationPolicyRepository.findByTenantIdAndCode(tenantId, code);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("取消政策不存在");
        }
        try {
            CancellationPolicy updated = cancellationPolicyService.update(existing.getId(), policy);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
