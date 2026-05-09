package com.crs.repository;

import com.crs.entity.ChannelRateCodeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道房价映射仓库接口
 * 用于渠道房价映射数据的CRUD操作
 */
@Repository
public interface ChannelRateCodeMappingRepository extends JpaRepository<ChannelRateCodeMapping, Integer> {
    
    /**
     * 根据渠道ID查询映射
     * @param channelId 渠道ID
     * @return 映射列表
     */
    List<ChannelRateCodeMapping> findByChannelId(Integer channelId);
    
    /**
     * 根据酒店ID查询映射
     * @param hotelId 酒店ID
     * @return 映射列表
     */
    List<ChannelRateCodeMapping> findByHotelId(Integer hotelId);
    
    /**
     * 根据渠道ID和酒店ID查询映射
     * @param channelId 渠道ID
     * @param hotelId 酒店ID
     * @return 映射列表
     */
    List<ChannelRateCodeMapping> findByChannelIdAndHotelId(Integer channelId, Integer hotelId);

    List<ChannelRateCodeMapping> findByChannelCode(String channelCode);
    List<ChannelRateCodeMapping> findByHotelCode(String hotelCode);
    List<ChannelRateCodeMapping> findByChannelCodeAndHotelCode(String channelCode, String hotelCode);
    List<ChannelRateCodeMapping> findByRateCode(String rateCode);
}
