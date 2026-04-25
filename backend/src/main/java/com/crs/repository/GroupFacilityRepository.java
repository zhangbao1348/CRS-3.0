package com.crs.repository;

import com.crs.entity.GroupFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

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
