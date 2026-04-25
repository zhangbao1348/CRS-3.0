package com.crs.controller;

import com.crs.entity.Package;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.PackageRepository;
import com.crs.service.PackageService;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 包价控制器
 * 提供包价管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/packages")
public class PackageController {
    
    @Autowired
    private PackageService packageService;
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;
    
    // 默认租户ID
    private static final Integer DEFAULT_TENANT_ID = 1;
    
    /**
     * 获取所有包价列表
     * @param tenantId 租户ID（可选）
     * @return 包价列表
     */
    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages(
            @RequestParam(required = false) Integer tenantId) {
        Integer actualTenantId = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
        List<Package> packages = packageService.getAllPackages(actualTenantId);
        return ResponseEntity.ok(packages);
    }
    
    /**
     * 根据ID获取包价详情
     * @param id 包价ID
     * @return 包价详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPackageById(@PathVariable Integer id) {
        Optional<Package> pkg = packageService.getPackageById(id);
        if (pkg.isPresent()) {
            return ResponseEntity.ok(pkg.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("包价不存在");
        }
    }
    
    /**
     * 根据代码获取包价
     * @param code 包价代码
     * @param tenantId 租户ID（可选）
     * @return 包价详情
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getPackageByCode(
            @PathVariable String code,
            @RequestParam(required = false) Integer tenantId) {
        Integer actualTenantId = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
        Optional<Package> pkg = packageService.getPackageByCode(actualTenantId, code);
        if (pkg.isPresent()) {
            return ResponseEntity.ok(pkg.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("包价不存在");
        }
    }
    
    /**
     * 创建新包价
     * @param pkg 包价信息
     * @param tenantId 租户ID（可选）
     * @return 创建的包价
     */
    @PostMapping
    public ResponseEntity<?> createPackage(
            @RequestBody Package pkg,
            @RequestParam(required = false) Integer tenantId) {
        try {
            if (pkg.getCode() != null && !CodeValidator.isValid(pkg.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            Integer actualTenantId = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
            Package createdPackage = packageService.createPackage(actualTenantId, pkg);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPackage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新包价
     * @param id 包价ID
     * @param pkg 包价信息
     * @return 更新后的包价
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePackage(@PathVariable Integer id, @RequestBody Package pkg) {
        try {
            if (pkg.getCode() != null && !CodeValidator.isValid(pkg.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            Package updatedPackage = packageService.updatePackage(id, pkg);
            return ResponseEntity.ok(updatedPackage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 删除包价
     * @param id 包价ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable Integer id) {
        try {
            // 检查是否被房价码引用（packages是JSON字段，包含包价代码）
            Optional<Package> pkgOpt = packageService.getPackageById(id);
            if (pkgOpt.isPresent()) {
                String packageCode = pkgOpt.get().getCode();
                long refCount = groupRateCodeRepository.countByPackagesContaining(packageCode);
                if (refCount > 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "该包价已被 " + refCount + " 个房价码引用，无法删除"));
                }
            }
            packageService.deletePackage(id);
            return ResponseEntity.ok("包价删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 搜索包价
     * @param params 搜索参数
     * @param tenantId 租户ID（可选）
     * @return 包价列表
     */
    @PostMapping("/search")
    public ResponseEntity<List<Package>> searchPackages(
            @RequestBody Map<String, String> params,
            @RequestParam(required = false) Integer tenantId) {
        String name = params.get("name");
        String type = params.get("type");
        String status = params.get("status");
        
        Integer actualTenantId = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
        List<Package> packages;
        if (name != null && !name.isEmpty()) {
            packages = packageService.searchPackagesByName(actualTenantId, name);
        } else if (type != null && !type.isEmpty()) {
            packages = packageService.searchPackagesByType(actualTenantId, type);
        } else if (status != null && !status.isEmpty()) {
            try {
                Package.Status packageStatus = Package.Status.valueOf(status);
                packages = packageService.searchPackagesByStatus(actualTenantId, packageStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            packages = packageService.getAllPackages(actualTenantId);
        }
        
        return ResponseEntity.ok(packages);
    }
    
    /**
     * 检查包价代码是否存在
     * @param code 包价代码
     * @param tenantId 租户ID（可选）
     * @return 是否存在
     */
    @GetMapping("/check/code/{code}")
    public ResponseEntity<Map<String, Boolean>> checkCodeExists(
            @PathVariable String code,
            @RequestParam(required = false) Integer tenantId) {
        Integer actualTenantId = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
        boolean exists = packageService.existsByCode(actualTenantId, code);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    // ===== CODE-based endpoints =====
    
    /**
     * 根据包价代码更新包价
     * @param code 包价代码
     * @param pkg 包价信息
     * @param tenantId 租户ID（可选）
     * @return 更新后的包价
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<?> updatePackageByCode(
            @PathVariable String code,
            @RequestBody Package pkg,
            @RequestParam(required = false) Integer tenantId) {
        Integer actualTenantId = tenantId != null ? tenantId : DEFAULT_TENANT_ID;
        Optional<Package> existing = packageRepository.findByTenantIdAndCode(actualTenantId, code);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("包价不存在");
        }
        try {
            Package updatedPackage = packageService.updatePackage(existing.get().getId(), pkg);
            return ResponseEntity.ok(updatedPackage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}