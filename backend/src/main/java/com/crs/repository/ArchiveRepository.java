package com.crs.repository;

import com.crs.entity.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 档案仓库接口
 * 用于档案数据的CRUD操作
 */
@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Integer> {
    
    /**
     * 根据集团ID查询档案
     * @param groupId 集团ID
     * @return 档案列表
     */
    List<Archive> findByGroupId(Integer groupId);
    
    /**
     * 根据类型查询档案
     * @param type 档案类型
     * @return 档案列表
     */
    List<Archive> findByType(String type);
    
    /**
     * 检查档案名称是否存在
     * @param name 档案名称
     * @return 是否存在
     */
    boolean existsByName(String name);
}
