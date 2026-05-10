package com.crs.repository;

import com.crs.entity.Overbooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * OverbookingRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【OverbookingRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 OverbookingRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface OverbookingRepository extends JpaRepository<Overbooking, Integer> {
    List<Overbooking> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDateBetween(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode,
            Date startDate, Date endDate);

    Optional<Overbooking> findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDate(
            Integer tenantId, String hotelCode, String dimensionType, String dimensionCode, Date overbookDate);
}
