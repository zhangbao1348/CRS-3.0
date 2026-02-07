package com.crs.repository;

import com.crs.entity.RoomTypeDiffSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 房型差价体系仓库接口
 * 用于房型差价体系数据的CRUD操作
 */
@Repository
public interface RoomTypeDiffSystemRepository extends JpaRepository<RoomTypeDiffSystem, Integer> {
    
    /**
     * 根据酒店ID查询房型差价体系列表
     * @param hotelId 酒店ID
     * @return 房型差价体系列表
     */
    List<RoomTypeDiffSystem> findByHotelId(Integer hotelId);
    
    /**
     * 根据酒店ID和状态查询房型差价体系列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 房型差价体系列表
     */
    List<RoomTypeDiffSystem> findByHotelIdAndStatus(Integer hotelId, RoomTypeDiffSystem.Status status);
    
    /**
     * 根据酒店ID和名称查询房型差价体系
     * @param hotelId 酒店ID
     * @param name 体系名称
     * @return 房型差价体系信息
     */
    Optional<RoomTypeDiffSystem> findByHotelIdAndName(Integer hotelId, String name);
    
    /**
     * 根据状态查询房型差价体系
     * @param status 状态
     * @return 房型差价体系列表
     */
    List<RoomTypeDiffSystem> findByStatus(RoomTypeDiffSystem.Status status);
}
