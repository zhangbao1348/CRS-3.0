package com.crs.repository;

import com.crs.entity.MarketCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 市场码仓库接口
 */
@Repository
public interface MarketCodeRepository extends JpaRepository<MarketCode, Integer> {

    /**
     * 根据父ID查询市场码
     */
    List<MarketCode> findByParentId(Integer parentId);

    /**
     * 根据CODE查询市场码
     */
    MarketCode findByCode(String code);
}
