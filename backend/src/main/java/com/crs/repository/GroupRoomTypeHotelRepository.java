package com.crs.repository;

import com.crs.entity.GroupRoomTypeHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 集团房型和酒店关联仓库接口
 * 用于处理集团房型和酒店关联表的数据库操作
 */
@Repository
public interface GroupRoomTypeHotelRepository extends JpaRepository<GroupRoomTypeHotel, Integer> {
    
    /**
     * 根据集团房型ID获取关联列表
     * @param groupRoomTypeId 集团房型ID
     * @return 关联列表
     */
    List<GroupRoomTypeHotel> findByGroupRoomTypeId(Integer groupRoomTypeId);
    
    /**
     * 根据酒店ID获取关联列表
     * @param hotelId 酒店ID
     * @return 关联列表
     */
    List<GroupRoomTypeHotel> findByHotelId(Integer hotelId);
    
    /**
     * 根据集团房型ID和酒店ID获取关联信息
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     * @return 关联信息
     */
    Optional<GroupRoomTypeHotel> findByGroupRoomTypeIdAndHotelId(Integer groupRoomTypeId, Integer hotelId);
    
    /**
     * 根据集团房型ID和分配状态获取关联列表
     * @param groupRoomTypeId 集团房型ID
     * @param allocated 是否分配
     * @return 关联列表
     */
    List<GroupRoomTypeHotel> findByGroupRoomTypeIdAndAllocated(Integer groupRoomTypeId, Boolean allocated);
    
    /**
     * 检查集团房型和酒店的关联是否存在
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     * @return 是否存在
     */
    boolean existsByGroupRoomTypeIdAndHotelId(Integer groupRoomTypeId, Integer hotelId);
}
