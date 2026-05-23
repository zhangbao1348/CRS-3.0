package com.crs.controller;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crs.dto.PackageDailyPriceBatchRequest;
import com.crs.entity.Package;
import com.crs.entity.PackageDailyPrice;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.service.PackageDailyPriceService;
import com.crs.service.PackageService;
import com.crs.util.CodeValidator;

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
    private GroupRateCodeRepository groupRateCodeRepository;

    @Autowired
    private PackageDailyPriceService packageDailyPriceService;
    
    // 默认租户ID
    /**
     * 获取所有包价列表
     * @return 包价列表
     */
    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        List<Package> packages = packageService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    /**
     * 兼容性接口：根据 groupId 获取所有包价（实际使用当前登录租户 ID）
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Package>> getPackagesByGroupId(@PathVariable Integer groupId) {
        // 忽略路径中的 groupId，直接使用当前租户上下文
        return getAllPackages();
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "包价不存在或无权访问"));
        }
    }
    
    /**
     * 根据代码获取包价
     * @param code 包价代码
     * @return 包价详情
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getPackageByCode(@PathVariable String code) {
        Optional<Package> pkg = packageService.getPackageByCode(code);
        if (pkg.isPresent()) {
            return ResponseEntity.ok(pkg.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "包价不存在或无权访问"));
        }
    }

    /**
     * 查询酒店包价某月每日价格。
     *
     * @param code 包价代码
     * @param hotelCode 酒店编码
     * @param month 月份，格式 yyyy-MM
     * @return 每日价格列表
     */
    @GetMapping("/code/{code}/daily-prices")
    public ResponseEntity<?> getDailyPrices(
            @PathVariable String code,
            @org.springframework.web.bind.annotation.RequestParam String hotelCode,
            @org.springframework.web.bind.annotation.RequestParam String month) {
        try {
            YearMonth targetMonth = YearMonth.parse(month);
            List<PackageDailyPrice> prices = packageDailyPriceService.getDailyPrices(hotelCode, code, targetMonth);
            return ResponseEntity.ok(Map.of(
                    "hotelCode", hotelCode,
                    "packageCode", code,
                    "month", month,
                    "prices", prices.stream().map(this::toDailyPriceResponse).toList()
            ));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "月份格式无效，应为 yyyy-MM"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 批量保存酒店包价每日价格。
     *
     * @param code 包价代码
     * @param request 保存请求
     * @return 最新每日价格列表
     */
    @PostMapping("/code/{code}/daily-prices")
    public ResponseEntity<?> saveDailyPrices(
            @PathVariable String code,
            @RequestBody PackageDailyPriceBatchRequest request) {
        try {
            List<PackageDailyPrice> prices = packageDailyPriceService.saveDailyPrices(
                    request.getHotelCode(),
                    code,
                    request.getPrices());
            return ResponseEntity.ok(Map.of(
                    "hotelCode", request.getHotelCode(),
                    "packageCode", code,
                    "prices", prices.stream().map(this::toDailyPriceResponse).toList()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 创建新包价
     * @param pkg 包价信息
     * @return 创建的包价
     */
    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody Package pkg) {
        try {
            if (pkg.getCode() != null && !CodeValidator.isValid(pkg.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            Package createdPackage = packageService.createPackage(pkg);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPackage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
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
            return ResponseEntity.ok(Map.of("message", "包价删除成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 搜索包价
     * @param params 搜索参数
     * @return 包价列表
     */
    @PostMapping("/search")
    public ResponseEntity<List<Package>> searchPackages(@RequestBody Map<String, String> params) {
        String keyword = normalize(params.get("keyword"));
        String name = normalize(params.get("name"));
        String code = normalize(params.get("code"));
        String type = normalize(params.get("type"));
        String frequency = normalize(params.get("frequency"));
        String quantityType = normalize(params.get("quantityType"));
        String status = normalize(params.get("status"));

        Package.Status packageStatus = null;
        if (status != null) {
            try {
                packageStatus = Package.Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        List<Package> packages = packageService.searchPackages(
                keyword,
                name,
                code,
                type,
                frequency,
                quantityType,
                packageStatus);
        return ResponseEntity.ok(packages);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private Map<String, Object> toDailyPriceResponse(PackageDailyPrice price) {
        return Map.of(
                "priceDate", price.getPriceDate(),
                "salePrice", price.getSalePrice()
        );
    }
    
    /**
     * 检查包价代码是否存在
     * @param code 包价代码
     * @return 是否存在
     */
    @GetMapping("/check/code/{code}")
    public ResponseEntity<Map<String, Boolean>> checkCodeExists(@PathVariable String code) {
        boolean exists = packageService.existsByCode(code);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    // ===== CODE-based endpoints =====
    
    /**
     * 根据包价代码更新包价
     * @param code 包价代码
     * @param pkg 包价信息
     * @return 更新后的包价
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<?> updatePackageByCode(@PathVariable String code, @RequestBody Package pkg) {
        Optional<Package> existing = packageService.getPackageByCode(code);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "包价不存在或无权访问"));
        }
        try {
            Package updatedPackage = packageService.updatePackage(existing.get().getId(), pkg);
            return ResponseEntity.ok(updatedPackage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
