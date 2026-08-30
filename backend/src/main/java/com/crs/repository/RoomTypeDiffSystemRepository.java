package com.crs.repository;

import com.crs.entity.RoomTypeDiffSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 房型差价体系仓库接口
 * 用于房型差价体系数据的CRUD操作
 */
@Repository
public interface RoomTypeDiffSystemRepository extends JpaRepository<RoomTypeDiffSystem, Integer> {

    /** 按主键与租户双重约束查询。 */
    Optional<RoomTypeDiffSystem> findByIdAndTenantId(Integer id, Integer tenantId);
    
    /** 获取租户下的房型差价体系 */
    List<RoomTypeDiffSystem> findByTenantId(Integer tenantId);

    /** 根据租户和状态查询房型差价体系 */
    List<RoomTypeDiffSystem> findByTenantIdAndStatus(Integer tenantId, RoomTypeDiffSystem.Status status);

    /** 根据租户和酒店编码查询 */
    List<RoomTypeDiffSystem> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /** 根据租户、酒店编码和状态查询 */
    List<RoomTypeDiffSystem> findByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, RoomTypeDiffSystem.Status status);

    /** 根据租户、酒店编码和名称查找 */
    Optional<RoomTypeDiffSystem> findByTenantIdAndHotelCodeAndName(Integer tenantId, String hotelCode, String name);
}
