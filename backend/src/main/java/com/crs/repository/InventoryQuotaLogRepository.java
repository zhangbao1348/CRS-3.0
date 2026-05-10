package com.crs.repository;

import com.crs.entity.InventoryQuotaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * InventoryQuotaLogRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【InventoryQuotaLogRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 InventoryQuotaLogRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface InventoryQuotaLogRepository extends JpaRepository<InventoryQuotaLog, Integer> {
    List<InventoryQuotaLog> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode);

    List<InventoryQuotaLog> findByTenantIdAndHotelCodeAndDimensionTypeOrderByOperationTimeDesc(
            Integer tenantId, String hotelCode, String dimensionType);
}
