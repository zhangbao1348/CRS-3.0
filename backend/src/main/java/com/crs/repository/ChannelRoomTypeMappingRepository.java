package com.crs.repository;

import com.crs.entity.ChannelRoomTypeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道房型映射仓库接口
 * 用于渠道房型映射数据的CRUD操作
 */
@Repository
public interface ChannelRoomTypeMappingRepository extends JpaRepository<ChannelRoomTypeMapping, Integer> {
    
    /**
     * 根据渠道ID查询映射
     * @param channelId 渠道ID
     * @return 映射列表
     */
    List<ChannelRoomTypeMapping> findByChannelId(Integer channelId);
    
    /**
     * 根据酒店ID查询映射
     * @param hotelId 酒店ID
     * @return 映射列表
     */
    List<ChannelRoomTypeMapping> findByHotelId(Integer hotelId);
    
    /**
     * 根据渠道ID和酒店ID查询映射
     * @param channelId 渠道ID
     * @param hotelId 酒店ID
     * @return 映射列表
     */
    List<ChannelRoomTypeMapping> findByChannelIdAndHotelId(Integer channelId, Integer hotelId);

    List<ChannelRoomTypeMapping> findByChannelCode(String channelCode);
    List<ChannelRoomTypeMapping> findByHotelCode(String hotelCode);
    List<ChannelRoomTypeMapping> findByChannelCodeAndHotelCode(String channelCode, String hotelCode);
    List<ChannelRoomTypeMapping> findByRoomTypeCode(String roomTypeCode);
}
