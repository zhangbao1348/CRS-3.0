package com.crs.repository;

import com.crs.entity.PmsInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * PmsInventoryRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【PmsInventoryRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 PmsInventoryRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface PmsInventoryRepository extends JpaRepository<PmsInventory, Integer> {

    List<PmsInventory> findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDateBetween(
            Integer tenantId, String hotelCode, String roomTypeCode, Date startDate, Date endDate);

    List<PmsInventory> findByTenantIdAndHotelCodeAndInventoryDateBetween(
            Integer tenantId, String hotelCode, Date startDate, Date endDate);

    Optional<PmsInventory> findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDate(
            Integer tenantId, String hotelCode, String roomTypeCode, Date inventoryDate);

    /**
     * 锁定酒店某日全部房型库存，使酒店总库存校验与房型扣减处于同一串行化边界。
     */
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PmsInventory p WHERE p.tenantId = :tenantId " +
            "AND p.hotelCode = :hotelCode AND p.inventoryDate = :inventoryDate " +
            "ORDER BY p.roomTypeCode")
    List<PmsInventory> findHotelDateInventoryForUpdate(
            @Param("tenantId") Integer tenantId,
            @Param("hotelCode") String hotelCode,
            @Param("inventoryDate") Date inventoryDate);
}
