package com.crs.repository;

import com.crs.entity.ChannelHotelMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 渠道酒店映射仓库接口
 * 用于渠道酒店映射数据的CRUD操作
 */
@Repository
public interface ChannelHotelMappingRepository extends JpaRepository<ChannelHotelMapping, Integer> {

    /** 按主键与租户双重约束查询。 */
    Optional<ChannelHotelMapping> findByIdAndTenantId(Integer id, Integer tenantId);
    
    /** 获取指定租户下的所有酒店映射 */
    List<ChannelHotelMapping> findByTenantId(Integer tenantId);

    /** 根据租户和渠道编码查询酒店映射列表 */
    List<ChannelHotelMapping> findByTenantIdAndChannelCode(Integer tenantId, String channelCode);

    /** 根据租户和酒店编码查询映射列表 */
    List<ChannelHotelMapping> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /** 根据租户、渠道编码和酒店编码精确查找映射记录 */
    List<ChannelHotelMapping> findByTenantIdAndChannelCodeAndHotelCode(Integer tenantId, String channelCode, String hotelCode);

    /** 统计特定租户和渠道下的映射数量 */
    long countByTenantIdAndChannelCode(Integer tenantId, String channelCode);
}
