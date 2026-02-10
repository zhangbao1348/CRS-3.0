package com.crs.controller;

import com.crs.entity.SourceCode;
import com.crs.service.SourceCodeService;
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

    /**
     * 获取所有来源码（树形结构）
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllSourceCodes() {
        try {
            List<Map<String, Object>> treeData = sourceCodeService.getAllSourceCodesAsTree();
            return ResponseEntity.ok(treeData);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 创建来源码
     */
    @PostMapping
    public ResponseEntity<SourceCode> createSourceCode(@RequestBody SourceCode sourceCode) {
        try {
            SourceCode createdSourceCode = sourceCodeService.createSourceCode(sourceCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSourceCode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 更新来源码
     */
    @PutMapping("/{id}")
    public ResponseEntity<SourceCode> updateSourceCode(@PathVariable Integer id, @RequestBody SourceCode sourceCode) {
        try {
            sourceCode.setId(id);
            SourceCode updatedSourceCode = sourceCodeService.updateSourceCode(sourceCode);
            if (updatedSourceCode != null) {
                return ResponseEntity.ok(updatedSourceCode);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 删除来源码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSourceCode(@PathVariable Integer id) {
        try {
            sourceCodeService.deleteSourceCode(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 检查来源码CODE是否唯一
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(@RequestParam String code, @RequestParam(required = false) Integer id) {
        try {
            boolean isUnique = sourceCodeService.isCodeUnique(code, id);
            return ResponseEntity.ok(Map.of("unique", isUnique));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}