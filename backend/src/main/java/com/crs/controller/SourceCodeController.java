package com.crs.controller;

import com.crs.entity.SourceCode;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.service.SourceCodeService;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 来源码控制器
 * 提供来源码的CRUD操作API
 */
@RestController
@RequestMapping("/api/source-codes")
public class SourceCodeController {

    @Autowired
    private SourceCodeService sourceCodeService;

    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;

    /**
     * 获取所有来源码（树形结构）
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllSourceCodes() {
        try {
            List<Map<String, Object>> treeData = sourceCodeService.getAllSourceCodesAsTree();
            return ResponseEntity.ok(treeData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 兼容性接口：根据 groupId 获取所有来源码（实际使用当前登录租户 ID）
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Map<String, Object>>> getSourceCodesByGroupId(@PathVariable Integer groupId) {
        // 忽略路径中的 groupId，直接使用当前租户上下文
        return getAllSourceCodes();
    }

    /**
     * 获取第三级来源码
     */
    @GetMapping("/third-level")
    public ResponseEntity<List<SourceCode>> getThirdLevelSourceCodes() {
        try {
            List<SourceCode> thirdLevelCodes = sourceCodeService.getThirdLevelSourceCodes();
            return ResponseEntity.ok(thirdLevelCodes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据父ID获取来源码
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<SourceCode>> getSourceCodesByParentId(@PathVariable Integer parentId) {
        try {
            List<SourceCode> sourceCodes = sourceCodeService.getSourceCodesByParentId(parentId);
            return ResponseEntity.ok(sourceCodes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据ID获取来源码
     */
    @GetMapping("/{id}")
    public ResponseEntity<SourceCode> getSourceCodeById(@PathVariable Integer id) {
        try {
            SourceCode sourceCode = sourceCodeService.getSourceCodeById(id);
            if (sourceCode != null) {
                return ResponseEntity.ok(sourceCode);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 创建来源码
     */
    @PostMapping
    public ResponseEntity<?> createSourceCode(@RequestBody SourceCode sourceCode) {
        try {
            if (sourceCode.getCode() != null && !CodeValidator.isValid(sourceCode.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            SourceCode createdSourceCode = sourceCodeService.createSourceCode(sourceCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSourceCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 更新来源码
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSourceCode(@PathVariable Integer id, @RequestBody SourceCode sourceCode) {
        try {
            if (sourceCode.getCode() != null && !CodeValidator.isValid(sourceCode.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            sourceCode.setId(id);
            SourceCode updatedSourceCode = sourceCodeService.updateSourceCode(sourceCode);
            if (updatedSourceCode != null) {
                return ResponseEntity.ok(updatedSourceCode);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 删除来源码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSourceCode(@PathVariable Integer id) {
        try {
            // 检查是否被房价码引用
            SourceCode existing = sourceCodeService.getSourceCodeById(id);
            if (existing != null) {
                long refCount = groupRateCodeRepository.countBySourceCode(existing.getCode());
                if (refCount > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "该来源码已被 " + refCount + " 个房价码引用，无法删除"));
                }
            }
            sourceCodeService.deleteSourceCode(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 检查来源码CODE是否唯一
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(@RequestParam String code, 
                                                                   @RequestParam(required = false) Integer id) {
        try {
            boolean isUnique = sourceCodeService.isCodeUnique(code, id);
            return ResponseEntity.ok(Map.of("unique", isUnique));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}