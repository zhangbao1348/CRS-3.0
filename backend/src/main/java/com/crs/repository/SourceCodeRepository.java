package com.crs.repository;

import com.crs.entity.SourceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 来源码仓库接口
 */
@Repository
public interface SourceCodeRepository extends JpaRepository<SourceCode, Integer> {

    /**
     * 根据父ID查询来源码
     */
    List<SourceCode> findByParentId(Integer parentId);

    /**
     * 根据CODE查询来源码
     */
    SourceCode findByCode(String code);
}