package com.crs.service;

import com.crs.entity.Package;
import java.util.List;
import java.util.Optional;

/**
 * 包价服务接口
 * 用于包价的业务逻辑处理
 */
public interface PackageService {
    
    /**
     * 根据租户ID获取所有包价列表
     * @param tenantId 租户ID
     * @return 包价列表
     */
    List<Package> getAllPackages(Integer tenantId);
    
    /**
     * 根据ID获取包价详情
     * @param id 包价ID
     * @return 包价详情
     */
    Optional<Package> getPackageById(Integer id);
    
    /**
     * 根据租户ID和代码获取包价
     * @param tenantId 租户ID
     * @param code 包价代码
     * @return 包价详情
     */
    Optional<Package> getPackageByCode(Integer tenantId, String code);
    
    /**
     * 创建新包价
     * @param tenantId 租户ID
     * @param pkg 包价信息
     * @return 创建的包价
     */
    Package createPackage(Integer tenantId, Package pkg);
    
    /**
     * 更新包价
     * @param id 包价ID
     * @param pkg 包价信息
     * @return 更新后的包价
     */
    Package updatePackage(Integer id, Package pkg);
    
    /**
     * 删除包价
     * @param id 包价ID
     */
    void deletePackage(Integer id);
    
    /**
     * 根据租户ID和名称搜索包价
     * @param tenantId 租户ID
     * @param name 包价名称
     * @return 包价列表
     */
    List<Package> searchPackagesByName(Integer tenantId, String name);
    
    /**
     * 根据租户ID和类型搜索包价
     * @param tenantId 租户ID
     * @param type 包价类型
     * @return 包价列表
     */
    List<Package> searchPackagesByType(Integer tenantId, String type);
    
    /**
     * 根据租户ID和状态搜索包价
     * @param tenantId 租户ID
     * @param status 包价状态
     * @return 包价列表
     */
    List<Package> searchPackagesByStatus(Integer tenantId, Package.Status status);
    
    /**
     * 检查租户内包价代码是否存在
     * @param tenantId 租户ID
     * @param code 包价代码
     * @return 是否存在
     */
    boolean existsByCode(Integer tenantId, String code);
    
    // 保留向后兼容的方法
    
    /**
     * 获取所有包价列表（已废弃，使用getAllPackages(Integer tenantId)代替）
     * @return 包价列表
     */
    @Deprecated
    List<Package> getAllPackages();
    
    /**
     * 根据代码获取包价（已废弃，使用getPackageByCode(Integer tenantId, String code)代替）
     * @param code 包价代码
     * @return 包价详情
     */
    @Deprecated
    Optional<Package> getPackageByCode(String code);
    
    /**
     * 创建新包价（已废弃，使用createPackage(Integer tenantId, Package pkg)代替）
     * @param pkg 包价信息
     * @return 创建的包价
     */
    @Deprecated
    Package createPackage(Package pkg);
    
    /**
     * 根据名称搜索包价（已废弃，使用searchPackagesByName(Integer tenantId, String name)代替）
     * @param name 包价名称
     * @return 包价列表
     */
    @Deprecated
    List<Package> searchPackagesByName(String name);
    
    /**
     * 根据类型搜索包价（已废弃，使用searchPackagesByType(Integer tenantId, String type)代替）
     * @param type 包价类型
     * @return 包价列表
     */
    @Deprecated
    List<Package> searchPackagesByType(String type);
    
    /**
     * 根据状态搜索包价（已废弃，使用searchPackagesByStatus(Integer tenantId, Package.Status status)代替）
     * @param status 包价状态
     * @return 包价列表
     */
    @Deprecated
    List<Package> searchPackagesByStatus(Package.Status status);
    
    /**
     * 检查包价代码是否存在（已废弃，使用existsByCode(Integer tenantId, String code)代替）
     * @param code 包价代码
     * @return 是否存在
     */
    @Deprecated
    boolean existsByCode(String code);
}