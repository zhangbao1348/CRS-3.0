package com.crs.service;

import com.crs.entity.ChannelHotelMapping;
import com.crs.entity.ChannelRoomTypeMapping;
import com.crs.entity.ChannelRateCodeMapping;

import java.util.List;
import java.util.Optional;

/**
 * 渠道映射服务接口
 * 用于渠道酒店/房型/房价映射的业务逻辑处理
 */
public interface ChannelMappingService {
    
    // ===== 酒店映射 =====
    
    /**
     * 获取酒店映射列表
     * @param channelId 渠道ID（可选）
     * @param hotelId 酒店ID（可选）
     * @return 酒店映射列表
     */
    List<ChannelHotelMapping> getHotelMappings(Integer channelId, Integer hotelId);
    List<ChannelHotelMapping> getHotelMappingsByCode(String channelCode, String hotelCode);
    
    /**
     * 创建酒店映射
     * @param mapping 酒店映射
     * @return 创建的酒店映射
     */
    ChannelHotelMapping createHotelMapping(ChannelHotelMapping mapping);
    
    /**
     * 更新酒店映射
     * @param id 映射ID
     * @param mapping 酒店映射
     * @return 更新后的酒店映射
     */
    ChannelHotelMapping updateHotelMapping(Integer id, ChannelHotelMapping mapping);
    
    /**
     * 删除酒店映射
     * @param id 映射ID
     */
    void deleteHotelMapping(Integer id);
    
    /**
     * 切换酒店映射状态
     * @param id 映射ID
     * @return 更新后的酒店映射
     */
    ChannelHotelMapping toggleHotelMappingStatus(Integer id);
    
    // ===== 房型映射 =====
    
    /**
     * 获取房型映射列表
     * @param channelId 渠道ID（可选）
     * @param hotelId 酒店ID（可选）
     * @return 房型映射列表
     */
    List<ChannelRoomTypeMapping> getRoomTypeMappings(Integer channelId, Integer hotelId);
    List<ChannelRoomTypeMapping> getRoomTypeMappingsByCode(String channelCode, String hotelCode);
    
    /**
     * 创建房型映射
     * @param mapping 房型映射
     * @return 创建的房型映射
     */
    ChannelRoomTypeMapping createRoomTypeMapping(ChannelRoomTypeMapping mapping);
    
    /**
     * 更新房型映射
     * @param id 映射ID
     * @param mapping 房型映射
     * @return 更新后的房型映射
     */
    ChannelRoomTypeMapping updateRoomTypeMapping(Integer id, ChannelRoomTypeMapping mapping);
    
    /**
     * 删除房型映射
     * @param id 映射ID
     */
    void deleteRoomTypeMapping(Integer id);
    
    /**
     * 切换房型映射状态
     * @param id 映射ID
     * @return 更新后的房型映射
     */
    ChannelRoomTypeMapping toggleRoomTypeMappingStatus(Integer id);
    
    // ===== 房价映射 =====
    
    /**
     * 获取房价映射列表
     * @param channelId 渠道ID（可选）
     * @param hotelId 酒店ID（可选）
     * @return 房价映射列表
     */
    List<ChannelRateCodeMapping> getRateCodeMappings(Integer channelId, Integer hotelId);
    List<ChannelRateCodeMapping> getRateCodeMappingsByCode(String channelCode, String hotelCode);
    
    /**
     * 创建房价映射
     * @param mapping 房价映射
     * @return 创建的房价映射
     */
    ChannelRateCodeMapping createRateCodeMapping(ChannelRateCodeMapping mapping);
    
    /**
     * 更新房价映射
     * @param id 映射ID
     * @param mapping 房价映射
     * @return 更新后的房价映射
     */
    ChannelRateCodeMapping updateRateCodeMapping(Integer id, ChannelRateCodeMapping mapping);
    
    /**
     * 删除房价映射
     * @param id 映射ID
     */
    void deleteRateCodeMapping(Integer id);
    
    /**
     * 切换房价映射状态
     * @param id 映射ID
     * @return 更新后的房价映射
     */
    ChannelRateCodeMapping toggleRateCodeMappingStatus(Integer id);
}
