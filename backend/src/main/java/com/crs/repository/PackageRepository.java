package com.crs.repository;

import com.crs.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 包价仓库接口
 * 用于包价数据的CRUD操作
 */
@Repository
public interface PackageRepository extends JpaRepository<Package, Integer> {
    
    /**
     * 根据包价代码查询包价
     * @param code 包价代码
     * @return 包价信息
     */
    Optional<Package> findByCode(String code);
    
    /**
     * 根据包价名称查询包价
     * @param name 包价名称
     * @return 包价列表
     */
    List<Package> findByNameContaining(String name);
    
    /**
     * 根据状态查询包价
     * @param status 状态
     * @return 包价列表
     */
    List<Package> findByStatus(Package.Status status);
    
    /**
     * 检查包价代码是否存在
     * @param code 包价代码
     * @return 是否存在
     */
    boolean existsByCode(String code);
}
