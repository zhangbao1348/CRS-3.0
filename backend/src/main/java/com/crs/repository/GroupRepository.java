package com.crs.repository;

import com.crs.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 集团仓库接口
 * 用于集团数据的CRUD操作
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    
    /**
     * 根据集团代码查询集团
     * @param groupCode 集团代码
     * @return 集团信息
     */
    Optional<Group> findByGroupCode(String groupCode);
    
    /**
     * 根据集团名称查询集团
     * @param groupName 集团名称
     * @return 集团列表
     */
    List<Group> findByGroupNameContaining(String groupName);
    
    /**
     * 根据状态查询集团
     * @param status 状态
     * @return 集团列表
     */
    List<Group> findByStatus(Group.Status status);
    
    /**
     * 检查集团代码是否存在
     * @param groupCode 集团代码
     * @return 是否存在
     */
    boolean existsByGroupCode(String groupCode);
    
    /**
     * 根据ID和状态查询集团
     * @param id 集团ID
     * @param status 状态
     * @return 集团信息
     */
    Optional<Group> findByIdAndStatus(Integer id, Group.Status status);
}
