package com.crs.repository;

import com.crs.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 房型数据访问接口 (RoomTypeRepository)
 * 
 * <p>提供对 {@link RoomType} 实体的数据库交互能力。支持基于酒店、集团以及房型特征的多维度查询。</p>
 */
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    /** 获取指定租户下的所有房型 */
    List<RoomType> findByTenantId(Integer tenantId);

    /** 获取指定租户下特定状态的房型 */
    List<RoomType> findByTenantIdAndStatus(Integer tenantId, RoomType.Status status);
    
    List<RoomType> findByTenantIdAndHotelCodeAndNameContaining(Integer tenantId, String hotelCode, String name);
    
    
    List<RoomType> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    Optional<RoomType> findByTenantIdAndHotelCodeAndCode(Integer tenantId, String hotelCode, String code);

    List<RoomType> findByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, RoomType.Status status);

    boolean existsByTenantIdAndHotelCodeAndCode(Integer tenantId, String hotelCode, String code);

    List<RoomType> findByTenantIdAndGroupRoomTypeCode(Integer tenantId, String groupRoomTypeCode);
}
