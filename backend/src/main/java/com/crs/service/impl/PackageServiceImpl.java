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
        Integer currentTenantId = getCurrentTenantId();
        return packageRepository.findById(id)
                .filter(p -> p.getTenantId() != null && p.getTenantId().equals(currentTenantId));
    }
    
    @Override
    public Optional<Package> getPackageByCode(String code) {
        return packageRepository.findByTenantIdAndCode(getCurrentTenantId(), code);
    }
    
    @Override
    public Package createPackage(Package pkg) {
        Integer currentTenantId = getCurrentTenantId();
        // 检查租户内代码是否已存在
        if (packageRepository.existsByTenantIdAndCode(currentTenantId, pkg.getCode())) {
            throw new IllegalArgumentException("包价代码已存在");
        }
        pkg.setTenantId(currentTenantId);
        return packageRepository.save(pkg);
    }
    
    @Override
    public Package updatePackage(Integer id, Package pkg) {
        Integer tenantId = getCurrentTenantId();
        // 验证所有权
        getPackageById(id)
                .orElseThrow(() -> new IllegalArgumentException("包价不存在或无权访问"));
        
        // 检查代码是否已被同一租户内的其他包价使用
        Optional<Package> packageByCode = packageRepository.findByTenantIdAndCode(tenantId, pkg.getCode());
        if (packageByCode.isPresent() && !packageByCode.get().getId().equals(id)) {
            throw new IllegalArgumentException("包价代码已被使用");
        }
        
        // 更新包价信息
        pkg.setId(id);
        pkg.setTenantId(tenantId);
        return packageRepository.save(pkg);
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
}
