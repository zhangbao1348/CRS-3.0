package com.crs.repository;

import com.crs.entity.HotelRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 酒店房型仓库接口
 * 用于处理酒店房型表的数据库操作
 */
@Repository
public interface HotelRoomTypeRepository extends JpaRepository<HotelRoomType, Integer> {
    
    /**
     * 根据酒店ID获取房型列表
     * @param hotelId 酒店ID
     * @return 房型列表
     */
    List<HotelRoomType> findByHotelId(Integer hotelId);
    
    /**
     * 根据集团房型ID获取房型列表
     * @param groupRoomTypeId 集团房型ID
     * @return 房型列表
     */
    List<HotelRoomType> findByGroupRoomTypeId(Integer groupRoomTypeId);
    
    /**
     * 根据酒店ID和房型代码获取房型
     * @param hotelId 酒店ID
     * @param roomTypeCode 房型代码
     * @return 房型信息
     */
    Optional<HotelRoomType> findByHotelIdAndRoomTypeCode(Integer hotelId, String roomTypeCode);
    
    /**
     * 根据酒店ID和状态获取房型列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 房型列表
     */
    List<HotelRoomType> findByHotelIdAndStatus(Integer hotelId, String status);
    
    /**
     * 检查酒店房型代码是否存在
     * @param hotelId 酒店ID
     * @param roomTypeCode 房型代码
     * @return 是否存在
     */
    boolean existsByHotelIdAndRoomTypeCode(Integer hotelId, String roomTypeCode);
    
    /**
     * 根据集团房型ID和酒店ID查询房型
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     * @return 房型信息
     */
    Optional<HotelRoomType> findByGroupRoomTypeIdAndHotelId(Integer groupRoomTypeId, Integer hotelId);
    
    /**
     * 根据酒店ID按排序字段升序查询
     * @param hotelId 酒店ID
     * @return 房型列表
     */
    List<HotelRoomType> findByHotelIdOrderBySortOrderAsc(Integer hotelId);
    
    /**
     * 统计某个酒店的房型数量
     * @param hotelId 酒店ID
     * @return 房型数量
     */
    long countByHotelId(Integer hotelId);
    
    /**
     * 根据房型大类ID查询酒店房型列表
     * @param roomTypeCategoryId 房型大类ID
     * @return 房型列表
     */
    List<HotelRoomType> findByRoomTypeCategoryId(Integer roomTypeCategoryId);
}
