package com.crs.repository;

import com.crs.entity.ChannelHotelMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道酒店映射仓库接口
 * 用于渠道酒店映射数据的CRUD操作
 */
@Repository
public interface ChannelHotelMappingRepository extends JpaRepository<ChannelHotelMapping, Integer> {
    
    /**
     * 根据渠道ID查询映射
     * @param channelId 渠道ID
     * @return 映射列表
     */
    List<ChannelHotelMapping> findByChannelId(Integer channelId);
    
    /**
     * 根据酒店ID查询映射
     * @param hotelId 酒店ID
     * @return 映射列表
     */
    List<ChannelHotelMapping> findByHotelId(Integer hotelId);
    
    /**
     * 根据渠道ID和酒店ID查询映射
     * @param channelId 渠道ID
     * @param hotelId 酒店ID
     * @return 映射列表
     */
    List<ChannelHotelMapping> findByChannelIdAndHotelId(Integer channelId, Integer hotelId);

    /**
     * 统计引用指定渠道ID的映射数量
     * @param channelId 渠道ID
     * @return 引用数量
     */
    long countByChannelId(Integer channelId);
}
