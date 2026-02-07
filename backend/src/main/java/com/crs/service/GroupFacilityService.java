package com.crs.service;

import com.crs.entity.GroupFacility;
import com.crs.repository.GroupFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GroupFacilityService {
    
    @Autowired
    private GroupFacilityRepository groupFacilityRepository;
    
    /**
     * 获取所有集团设施
     * @return 设施列表
     */
    public List<GroupFacility> getAllFacilities() {
        return groupFacilityRepository.findAll();
    }
    
    /**
     * 根据设施类型查询设施
     * @param facilityType 设施类型
     * @return 设施列表
     */
    public List<GroupFacility> getFacilitiesByType(String facilityType) {
        return groupFacilityRepository.findByFacilityType(facilityType);
    }
    
    /**
     * 根据设施状态查询设施
     * @param available 设施状态
     * @return 设施列表
     */
    public List<GroupFacility> getFacilitiesByStatus(Boolean available) {
        return groupFacilityRepository.findByAvailable(available);
    }
    
    /**
     * 根据ID获取设施
     * @param id 设施ID
     * @return 设施对象
     */
    public GroupFacility getFacilityById(Integer id) {
        return groupFacilityRepository.findById(id).orElse(null);
    }
    
    /**
     * 创建设施
     * @param facility 设施对象
     * @return 创建的设施对象
     */
    public GroupFacility createFacility(GroupFacility facility) {
        return groupFacilityRepository.save(facility);
    }
    
    /**
     * 更新设施
     * @param facility 设施对象
     * @return 更新后的设施对象
     */
    public GroupFacility updateFacility(GroupFacility facility) {
        return groupFacilityRepository.save(facility);
    }
    
    /**
     * 删除设施
     * @param id 设施ID
     */
    public void deleteFacility(Integer id) {
        groupFacilityRepository.deleteById(id);
    }
    
    /**
     * 根据设施代码查询设施
     * @param facilityCode 设施代码
     * @return 设施对象
     */
    public GroupFacility getFacilityByCode(String facilityCode) {
        return groupFacilityRepository.findByFacilityCode(facilityCode);
    }
}
