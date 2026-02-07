package com.crs.repository;

import com.crs.entity.MarketCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 市场码仓库接口
 * 用于市场码数据的CRUD操作
 */
@Repository
public interface MarketCodeRepository extends JpaRepository<MarketCode, Integer> {
    
    /**
     * 根据市场码代码查询市场码
     * @param code 市场码代码
     * @return 市场码信息
     */
    Optional<MarketCode> findByCode(String code);
    
    /**
     * 根据市场码名称查询市场码
     * @param name 市场码名称
     * @return 市场码列表
     */
    List<MarketCode> findByNameContaining(String name);
    
    /**
     * 根据状态查询市场码
     * @param status 状态
     * @return 市场码列表
     */
    List<MarketCode> findByStatus(MarketCode.Status status);
    
    /**
     * 检查市场码代码是否存在
     * @param code 市场码代码
     * @return 是否存在
     */
    boolean existsByCode(String code);
}
