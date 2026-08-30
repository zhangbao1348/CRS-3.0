package com.crs.controller;

import com.crs.entity.CancellationPolicy;
import com.crs.modules.policy.api.CancellationPolicyMapper;
import com.crs.modules.policy.api.CancellationPolicyRequest;
import com.crs.modules.policy.api.CancellationPolicyResponse;
import com.crs.service.CancellationPolicyService;
import com.crs.shared.api.ApiException;
import com.crs.util.CodeValidator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

/**
 * 取消政策控制器。
 *
 * <p>控制器只负责 HTTP 合同、参数校验和模型映射，租户隔离及引用校验由服务层完成。</p>
 */
@RestController
@RequestMapping("/api/cancellation-policies")
public class CancellationPolicyController {

    private final CancellationPolicyService cancellationPolicyService;
    private final CancellationPolicyMapper mapper;

    public CancellationPolicyController(CancellationPolicyService cancellationPolicyService,
                                        CancellationPolicyMapper mapper) {
        this.cancellationPolicyService = cancellationPolicyService;
        this.mapper = mapper;
    }

    /** 获取当前租户下的全部取消政策。 */
    @GetMapping
    public ResponseEntity<List<CancellationPolicyResponse>> getAllPolicies() {
        List<CancellationPolicyResponse> policies = cancellationPolicyService.getAllPolicies().stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(policies);
    }

    /** 兼容旧路径；路径 groupId 不参与授权，租户边界取自认证上下文。 */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<CancellationPolicyResponse>> getPoliciesByGroupId(@PathVariable Integer groupId) {
        return getAllPolicies();
    }

    /** 按 ID 查询当前租户政策。 */
    @GetMapping("/{id}")
    public ResponseEntity<CancellationPolicyResponse> getPolicyById(@PathVariable Integer id) {
        CancellationPolicy policy = cancellationPolicyService.getById(id)
                .orElseThrow(() -> ApiException.notFound(
                        "CANCELLATION_POLICY_NOT_FOUND", "取消政策不存在或无权访问"));
        return ResponseEntity.ok(mapper.toResponse(policy));
    }

    /** 创建取消政策。 */
    @PostMapping
    public ResponseEntity<CancellationPolicyResponse> createPolicy(
            @Valid @RequestBody CancellationPolicyRequest request) {
        validateCode(request.code());
        CancellationPolicy created = translateBusinessError(
                () -> cancellationPolicyService.create(mapper.toEntity(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    /** 更新取消政策。 */
    @PutMapping("/{id}")
    public ResponseEntity<CancellationPolicyResponse> updatePolicy(
            @PathVariable Integer id,
            @Valid @RequestBody CancellationPolicyRequest request) {
        validateCode(request.code());
        CancellationPolicy updated = translateBusinessError(
                () -> cancellationPolicyService.update(id, mapper.toEntity(request)));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    /** 删除未被当前租户房价码引用的取消政策。 */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePolicy(@PathVariable Integer id) {
        translateBusinessError(() -> {
            cancellationPolicyService.delete(id);
            return null;
        });
        return ResponseEntity.ok("取消政策删除成功");
    }

    /** 兼容按代码更新的旧接口。 */
    @PutMapping("/code/{code}")
    public ResponseEntity<CancellationPolicyResponse> updatePolicyByCode(
            @PathVariable String code,
            @Valid @RequestBody CancellationPolicyRequest request) {
        validateCode(request.code());
        CancellationPolicy existing = cancellationPolicyService.getByCode(code)
                .orElseThrow(() -> ApiException.notFound(
                        "CANCELLATION_POLICY_NOT_FOUND", "取消政策不存在"));
        CancellationPolicy updated = translateBusinessError(
                () -> cancellationPolicyService.update(existing.getId(), mapper.toEntity(request)));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    private void validateCode(String code) {
        if (!CodeValidator.isValid(code)) {
            throw ApiException.badRequest(
                    "INVALID_CANCELLATION_POLICY_CODE", CodeValidator.ERROR_MESSAGE);
        }
    }

    private <T> T translateBusinessError(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (IllegalArgumentException exception) {
            throw ApiException.badRequest(
                    "CANCELLATION_POLICY_OPERATION_REJECTED", exception.getMessage());
        }
    }
}
