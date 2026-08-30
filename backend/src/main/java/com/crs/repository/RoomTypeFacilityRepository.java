package com.crs.repository;

import com.crs.entity.RoomTypeFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RoomTypeFacilityRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【RoomTypeFacilityRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/12-房型管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 RoomTypeFacilityRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface RoomTypeFacilityRepository extends JpaRepository<RoomTypeFacility, Integer> {

    List<RoomTypeFacility> findByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode);

    List<RoomTypeFacility> findByTenantIdAndHotelCodeAndRoomTypeCode(
            Integer tenantId, String hotelCode, String roomTypeCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomTypeFacility f WHERE f.hotelCode = :hotelCode AND f.roomTypeCode = :roomTypeCode")
    void deleteByHotelCodeAndRoomTypeCode(@Param("hotelCode") String hotelCode, @Param("roomTypeCode") String roomTypeCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomTypeFacility f WHERE f.tenantId = :tenantId AND f.hotelCode = :hotelCode AND f.roomTypeCode = :roomTypeCode")
    void deleteByTenantIdAndHotelCodeAndRoomTypeCode(
            @Param("tenantId") Integer tenantId,
            @Param("hotelCode") String hotelCode,
            @Param("roomTypeCode") String roomTypeCode);
}
