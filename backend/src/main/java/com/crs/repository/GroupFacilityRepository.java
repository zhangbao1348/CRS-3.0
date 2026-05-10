package com.crs.repository;

import com.crs.entity.GroupFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * GroupFacilityRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【GroupFacilityRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/08-集团管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 GroupFacilityRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public interface GroupFacilityRepository extends JpaRepository<GroupFacility, Integer> {
    
    /**
     * 根据设施类型查询设施
     * @param facilityType 设施类型
     * @return 设施列表
     */
    List<GroupFacility> findByFacilityType(String facilityType);
    
    /**
     * 根据设施状态查询设施
     * @param available 设施状态
     * @return 设施列表
     */
    List<GroupFacility> findByAvailable(Boolean available);
    
    /**
     * 根据设施代码查询设施
     * @param facilityCode 设施代码
     * @return 设施对象
     */
    GroupFacility findByFacilityCode(String facilityCode);
    
    /**
     * 根据适用范围查询设施
     */
    List<GroupFacility> findByScope(String scope);
}
