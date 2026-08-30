package com.crs.repository;

import com.crs.entity.RoomTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RoomTypeCategoryRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【RoomTypeCategoryRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/12-房型管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 RoomTypeCategoryRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface RoomTypeCategoryRepository extends JpaRepository<RoomTypeCategory, Integer> {

    /** 按主键与租户双重约束查询。 */
    Optional<RoomTypeCategory> findByIdAndTenantId(Integer id, Integer tenantId);
    
    List<RoomTypeCategory> findByTenantId(Integer tenantId);
    
    List<RoomTypeCategory> findByGroupId(Integer groupId);
    
    List<RoomTypeCategory> findByGroupIdAndStatus(Integer groupId, String status);
    
    Optional<RoomTypeCategory> findByGroupIdAndCategoryCode(Integer groupId, String categoryCode);
    
    boolean existsByGroupIdAndCategoryCode(Integer groupId, String categoryCode);
    
    List<RoomTypeCategory> findByGroupIdOrderBySortOrderAsc(Integer groupId);
    
    List<RoomTypeCategory> findByTenantIdOrderBySortOrderAsc(Integer tenantId);
    
    Optional<RoomTypeCategory> findByTenantIdAndCategoryCode(Integer tenantId, String categoryCode);
    
    boolean existsByTenantIdAndCategoryCode(Integer tenantId, String categoryCode);
    
    List<RoomTypeCategory> findByTenantIdAndStatus(Integer tenantId, String status);

    List<RoomTypeCategory> findByGroupCode(String groupCode);

    List<RoomTypeCategory> findByGroupCodeAndStatus(String groupCode, String status);

    Optional<RoomTypeCategory> findByGroupCodeAndCategoryCode(String groupCode, String categoryCode);

    boolean existsByGroupCodeAndCategoryCode(String groupCode, String categoryCode);

    List<RoomTypeCategory> findByGroupCodeOrderBySortOrderAsc(String groupCode);
}
