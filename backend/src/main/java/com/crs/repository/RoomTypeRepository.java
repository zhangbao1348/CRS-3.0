package com.crs.repository;

import com.crs.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 房型仓库接口
 * 用于酒店房型数据的CRUD操作
 */
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    
    /**
     * 根据酒店ID查询房型列表
     * @param hotelId 酒店ID
     * @return 房型列表
     */
    List<RoomType> findByHotelId(Integer hotelId);
    
    /**
     * 根据酒店ID和状态查询房型列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 房型列表
     */
    List<RoomType> findByHotelIdAndStatus(Integer hotelId, RoomType.Status status);
    
    /**
     * 根据酒店ID和房型代码查询房型
     * @param hotelId 酒店ID
     * @param code 房型代码
     * @return 房型信息
     */
    Optional<RoomType> findByHotelIdAndCode(Integer hotelId, String code);
    
    /**
     * 根据集团房型ID查询房型列表
     * @param groupRoomTypeId 集团房型ID
     * @return 房型列表
     */
    List<RoomType> findByGroupRoomTypeId(Integer groupRoomTypeId);
    
    /**
     * 根据房型名称查询房型
     * @param name 房型名称
     * @return 房型列表
     */
    List<RoomType> findByNameContaining(String name);
    
    /**
     * 根据状态查询房型
     * @param status 状态
     * @return 房型列表
     */
    List<RoomType> findByStatus(RoomType.Status status);
    
    /**
     * 检查酒店内房型代码是否存在
     * @param hotelId 酒店ID
     * @param code 房型代码
     * @return 是否存在
     */
    boolean existsByHotelIdAndCode(Integer hotelId, String code);
    
    /**
     * 根据酒店代码查询房型列表
     * @param hotelCode 酒店代码
     * @return 房型列表
     */
    List<RoomType> findByHotelCode(String hotelCode);
}
