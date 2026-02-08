package com.crs.repository;

import com.crs.entity.GroupRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 集团房型仓库接口
 * 用于集团房型数据的CRUD操作
 */
@Repository
public interface GroupRoomTypeRepository extends JpaRepository<GroupRoomType, Integer> {
    
    /**
     * 根据集团ID查询集团房型列表
     * @param groupId 集团ID
     * @return 集团房型列表
     */
    List<GroupRoomType> findByGroupId(Integer groupId);
    
    /**
     * 根据集团ID和状态查询集团房型列表
     * @param groupId 集团ID
     * @param status 状态
     * @return 集团房型列表
     */
    List<GroupRoomType> findByGroupIdAndStatus(Integer groupId, String status);
    
    /**
     * 根据房型代码查询集团房型
     * @param roomTypeCode 房型代码
     * @return 集团房型信息
     */
    Optional<GroupRoomType> findByRoomTypeCode(String roomTypeCode);
    
    /**
     * 根据集团ID和房型代码查询集团房型
     * @param groupId 集团ID
     * @param roomTypeCode 房型代码
     * @return 集团房型信息
     */
    Optional<GroupRoomType> findByGroupIdAndRoomTypeCode(Integer groupId, String roomTypeCode);
    
    /**
     * 根据房型名称查询集团房型
     * @param roomTypeName 房型名称
     * @return 集团房型列表
     */
    List<GroupRoomType> findByRoomTypeNameContaining(String roomTypeName);
    
    /**
     * 根据状态查询集团房型
     * @param status 状态
     * @return 集团房型列表
     */
    List<GroupRoomType> findByStatus(String status);
    
    /**
     * 检查房型代码是否存在
     * @param roomTypeCode 房型代码
     * @return 是否存在
     */
    boolean existsByRoomTypeCode(String roomTypeCode);
}
