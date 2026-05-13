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
    
    /** 获取指定租户下的所有房型映射 */
    List<ChannelRoomTypeMapping> findByTenantId(Integer tenantId);

    /** 根据租户和渠道编码查询房型映射列表 */
    List<ChannelRoomTypeMapping> findByTenantIdAndChannelCode(Integer tenantId, String channelCode);

    /** 根据租户和酒店编码查询房型映射列表 */
    List<ChannelRoomTypeMapping> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /** 根据租户、渠道编码和酒店编码查询房型映射列表 */
    List<ChannelRoomTypeMapping> findByTenantIdAndChannelCodeAndHotelCode(Integer tenantId, String channelCode, String hotelCode);

    /** 根据租户和房型代码查询房型映射列表 */
    List<ChannelRoomTypeMapping> findByTenantIdAndRoomTypeCode(Integer tenantId, String roomTypeCode);
}
