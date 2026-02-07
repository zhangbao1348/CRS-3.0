package com.crs.repository;

import com.crs.entity.SourceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 来源码仓库接口
 * 用于来源码数据的CRUD操作
 */
@Repository
public interface SourceCodeRepository extends JpaRepository<SourceCode, Integer> {
    
    /**
     * 根据来源码代码查询来源码
     * @param code 来源码代码
     * @return 来源码信息
     */
    Optional<SourceCode> findByCode(String code);
    
    /**
     * 根据来源码名称查询来源码
     * @param name 来源码名称
     * @return 来源码列表
     */
    List<SourceCode> findByNameContaining(String name);
    
    /**
     * 根据状态查询来源码
     * @param status 状态
     * @return 来源码列表
     */
    List<SourceCode> findByStatus(SourceCode.Status status);
    
    /**
     * 检查来源码代码是否存在
     * @param code 来源码代码
     * @return 是否存在
     */
    boolean existsByCode(String code);
}
