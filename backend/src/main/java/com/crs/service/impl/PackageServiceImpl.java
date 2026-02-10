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
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }
    
    @Override
    public Optional<Package> getPackageById(Integer id) {
        return packageRepository.findById(id);
    }
    
    @Override
    public Optional<Package> getPackageByCode(String code) {
        return packageRepository.findByCode(code);
    }
    
    @Override
    public Package createPackage(Package pkg) {
        // 检查代码是否已存在
        if (packageRepository.existsByCode(pkg.getCode())) {
            throw new IllegalArgumentException("包价代码已存在");
        }
        return packageRepository.save(pkg);
    }
    
    @Override
    public Package updatePackage(Integer id, Package pkg) {
        // 检查包价是否存在
        Optional<Package> existingPackage = packageRepository.findById(id);
        if (!existingPackage.isPresent()) {
            throw new IllegalArgumentException("包价不存在");
        }
        
        // 检查代码是否已被其他包价使用
        Optional<Package> packageByCode = packageRepository.findByCode(pkg.getCode());
        if (packageByCode.isPresent() && !packageByCode.get().getId().equals(id)) {
            throw new IllegalArgumentException("包价代码已被使用");
        }
        
        // 更新包价信息
        pkg.setId(id);
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
    public List<Package> searchPackagesByName(String name) {
        return packageRepository.findByNameContaining(name);
    }
    
    @Override
    public List<Package> searchPackagesByType(String type) {
        // 这里可以根据需要实现按类型搜索
        // 暂时返回所有包价
        return packageRepository.findAll();
    }
    
    @Override
    public List<Package> searchPackagesByStatus(Package.Status status) {
        return packageRepository.findByStatus(status);
    }
    
    @Override
    public boolean existsByCode(String code) {
        return packageRepository.existsByCode(code);
    }
}