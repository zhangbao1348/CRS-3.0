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
     * 获取所有包价列表
     * @return 包价列表
     */
    List<Package> getAllPackages();
    
    /**
     * 根据ID获取包价详情
     * @param id 包价ID
     * @return 包价详情
     */
    Optional<Package> getPackageById(Integer id);
    
    /**
     * 根据代码获取包价
     * @param code 包价代码
     * @return 包价详情
     */
    Optional<Package> getPackageByCode(String code);
    
    /**
     * 创建新包价
     * @param pkg 包价信息
     * @return 创建的包价
     */
    Package createPackage(Package pkg);
    
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
     * 根据名称搜索包价
     * @param name 包价名称
     * @return 包价列表
     */
    List<Package> searchPackagesByName(String name);
    
    /**
     * 根据类型搜索包价
     * @param type 包价类型
     * @return 包价列表
     */
    List<Package> searchPackagesByType(String type);
    
    /**
     * 根据状态搜索包价
     * @param status 包价状态
     * @return 包价列表
     */
    List<Package> searchPackagesByStatus(Package.Status status);

    /**
     * 组合条件搜索包价
     * @param name 包价名称，支持模糊匹配
     * @param code 包价代码，支持模糊匹配
     * @param type 包价类型，精确匹配
     * @param status 包价状态，精确匹配
     * @return 包价列表
     */
    List<Package> searchPackages(String name, String code, String type, Package.Status status);
    
    /**
     * 检查租户内包价代码是否存在
     * @param code 包价代码
     * @return 是否存在
     */
    boolean existsByCode(String code);
}
