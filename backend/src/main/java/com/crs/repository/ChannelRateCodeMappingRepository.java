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
    
    /** 获取指定租户下的所有房价映射 */
    List<ChannelRateCodeMapping> findByTenantId(Integer tenantId);

    /** 根据租户和渠道编码查询房价映射列表 */
    List<ChannelRateCodeMapping> findByTenantIdAndChannelCode(Integer tenantId, String channelCode);

    /** 根据租户和酒店编码查询房价映射列表 */
    List<ChannelRateCodeMapping> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /** 根据租户、渠道编码和酒店编码查询房价映射列表 */
    List<ChannelRateCodeMapping> findByTenantIdAndChannelCodeAndHotelCode(Integer tenantId, String channelCode, String hotelCode);

    /** 根据租户和房价码查询房价映射列表 */
    List<ChannelRateCodeMapping> findByTenantIdAndRateCode(Integer tenantId, String rateCode);
}
