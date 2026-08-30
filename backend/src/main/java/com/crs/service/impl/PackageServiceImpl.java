package com.crs.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crs.entity.Package;
import com.crs.repository.PackageRepository;
import com.crs.service.PackageService;

/**
 * 包价服务实现类
 * 提供包价管理的业务逻辑处理
 */
@Service
@Transactional
public class PackageServiceImpl implements PackageService {
    
    @Autowired
    private PackageRepository packageRepository;
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findByTenantId(getCurrentTenantId());
    }
    
    @Override
    public Optional<Package> getPackageById(Integer id) {
        return packageRepository.findByIdAndTenantId(id, getCurrentTenantId());
    }
    
    @Override
    public Optional<Package> getPackageByCode(String code) {
        return packageRepository.findByTenantIdAndCode(getCurrentTenantId(), code);
    }
    
    @Override
    public Package createPackage(Package pkg) {
        Integer currentTenantId = getCurrentTenantId();
        validatePackage(pkg);
        // 检查租户内代码是否已存在
        if (packageRepository.existsByTenantIdAndCode(currentTenantId, pkg.getCode())) {
            throw new IllegalArgumentException("该包价代码已存在");
        }
        pkg.setId(null);
        pkg.setTenantId(currentTenantId);
        if (pkg.getStatus() == null) {
            pkg.setStatus(Package.Status.active);
        }
        return packageRepository.save(pkg);
    }
    
    @Override
    public Package updatePackage(Integer id, Package pkg) {
        Integer tenantId = getCurrentTenantId();
        Package existing = getPackageById(id)
                .orElseThrow(() -> new IllegalArgumentException("包价不存在或无权访问"));

        if (pkg.getCode() != null && !existing.getCode().equals(pkg.getCode())) {
            throw new IllegalArgumentException("包价代码保存后不可修改");
        }

        pkg.setCode(existing.getCode());
        validatePackage(pkg);
        existing.setName(pkg.getName());
        existing.setDescription(pkg.getDescription());
        existing.setType(pkg.getType());
        existing.setQuantityType(pkg.getQuantityType());
        existing.setFixedQuantity(pkg.getFixedQuantity());
        existing.setFrequency(pkg.getFrequency());
        existing.setPriceType(pkg.getPriceType());
        existing.setFixedPrice(pkg.getFixedPrice());
        existing.setTaxIncluded(pkg.getTaxIncluded() == null ? false : pkg.getTaxIncluded());
        return packageRepository.save(existing);
    }
    
    @Override
    public void deletePackage(Integer id) {
        // 验证所有权
        Package existingPackage = getPackageById(id)
                .orElseThrow(() -> new IllegalArgumentException("包价不存在或无权访问"));
        
        packageRepository.delete(existingPackage);
    }
    
    @Override
    public List<Package> searchPackagesByName(String name) {
        return packageRepository.findByTenantIdAndNameContaining(getCurrentTenantId(), name);
    }
    
    @Override
    public List<Package> searchPackagesByType(String type) {
        return packageRepository.findByTenantIdAndType(getCurrentTenantId(), type);
    }
    
    @Override
    public List<Package> searchPackagesByStatus(Package.Status status) {
        return packageRepository.findByTenantIdAndStatus(getCurrentTenantId(), status);
    }

    @Override
    public List<Package> searchPackages(
            String keyword,
            String name,
            String code,
            String type,
            String frequency,
            String quantityType,
            Package.Status status) {
        return packageRepository.searchPackages(
                getCurrentTenantId(),
                keyword,
                name,
                code,
                type,
                frequency,
                quantityType,
                status);
    }
    
    @Override
    public boolean existsByCode(String code) {
        return packageRepository.existsByTenantIdAndCode(getCurrentTenantId(), code);
    }

    /** 验证包价的必填字段及条件字段，防止绕过页面提交无效数据。 */
    private void validatePackage(Package pkg) {
        if (pkg.getCode() == null || pkg.getCode().isBlank()) {
            throw new IllegalArgumentException("包价代码不能为空");
        }
        if (pkg.getName() == null || pkg.getName().isBlank()) {
            throw new IllegalArgumentException("包价名称不能为空");
        }
        if (pkg.getType() == null || pkg.getType().isBlank()
                || pkg.getFrequency() == null || pkg.getFrequency().isBlank()
                || pkg.getQuantityType() == null || pkg.getQuantityType().isBlank()) {
            throw new IllegalArgumentException("包价类型、发放频率和计数方式为必填项");
        }
        if (pkg.getFixedQuantity() == null || pkg.getFixedQuantity() < 1) {
            throw new IllegalArgumentException("份数必须是大于 0 的整数");
        }
        if (pkg.getFixedPrice() != null && pkg.getFixedPrice() < 0) {
            throw new IllegalArgumentException("价格不能为负数");
        }
        if (pkg.getPriceType() == null || pkg.getPriceType().isBlank()) {
            pkg.setPriceType("group");
        }
        if (!List.of("group", "daily", "hotel").contains(pkg.getPriceType())) {
            throw new IllegalArgumentException("计价方式无效");
        }
        if ("daily".equals(pkg.getPriceType())) {
            pkg.setFixedPrice(null);
        }
    }
}
