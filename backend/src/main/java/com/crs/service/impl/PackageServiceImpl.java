package com.crs.service.impl;

import com.crs.entity.Package;
import com.crs.repository.PackageRepository;
import com.crs.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 包价服务实现类
 * 提供包价管理的业务逻辑处理
 */
@Service
@Transactional
public class PackageServiceImpl implements PackageService {
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Override
    public List<Package> getAllPackages(Integer tenantId) {
        return packageRepository.findByTenantId(tenantId);
    }
    
    @Override
    public Optional<Package> getPackageById(Integer id) {
        return packageRepository.findById(id);
    }
    
    @Override
    public Optional<Package> getPackageByCode(Integer tenantId, String code) {
        return packageRepository.findByTenantIdAndCode(tenantId, code);
    }
    
    @Override
    public Package createPackage(Integer tenantId, Package pkg) {
        // 检查租户内代码是否已存在
        if (packageRepository.existsByTenantIdAndCode(tenantId, pkg.getCode())) {
            throw new IllegalArgumentException("包价代码已存在");
        }
        pkg.setTenantId(tenantId);
        return packageRepository.save(pkg);
    }
    
    @Override
    public Package updatePackage(Integer id, Package pkg) {
        // 检查包价是否存在
        Optional<Package> existingPackage = packageRepository.findById(id);
        if (!existingPackage.isPresent()) {
            throw new IllegalArgumentException("包价不存在");
        }
        
        // 检查代码是否已被同一租户内的其他包价使用
        Integer tenantId = existingPackage.get().getTenantId();
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
        // 检查包价是否存在
        if (!packageRepository.existsById(id)) {
            throw new IllegalArgumentException("包价不存在");
        }
        packageRepository.deleteById(id);
    }
    
    @Override
    public List<Package> searchPackagesByName(Integer tenantId, String name) {
        return packageRepository.findByTenantIdAndNameContaining(tenantId, name);
    }
    
    @Override
    public List<Package> searchPackagesByType(Integer tenantId, String type) {
        return packageRepository.findByTenantIdAndType(tenantId, type);
    }
    
    @Override
    public List<Package> searchPackagesByStatus(Integer tenantId, Package.Status status) {
        return packageRepository.findByTenantIdAndStatus(tenantId, status);
    }
    
    @Override
    public boolean existsByCode(Integer tenantId, String code) {
        return packageRepository.existsByTenantIdAndCode(tenantId, code);
    }
    
    // 保留向后兼容的方法实现
    
    @Override
    @Deprecated
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }
    
    @Override
    @Deprecated
    public Optional<Package> getPackageByCode(String code) {
        return packageRepository.findByCode(code);
    }
    
    @Override
    @Deprecated
    public Package createPackage(Package pkg) {
        // 检查代码是否已存在
        if (packageRepository.existsByCode(pkg.getCode())) {
            throw new IllegalArgumentException("包价代码已存在");
        }
        return packageRepository.save(pkg);
    }
    
    @Override
    @Deprecated
    public List<Package> searchPackagesByName(String name) {
        return packageRepository.findByNameContaining(name);
    }
    
    @Override
    @Deprecated
    public List<Package> searchPackagesByType(String type) {
        return packageRepository.findAll();
    }
    
    @Override
    @Deprecated
    public List<Package> searchPackagesByStatus(Package.Status status) {
        return packageRepository.findByStatus(status);
    }
    
    @Override
    @Deprecated
    public boolean existsByCode(String code) {
        return packageRepository.existsByCode(code);
    }
}