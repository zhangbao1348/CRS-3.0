package com.crs.service;

import com.crs.entity.GroupFacility;
import com.crs.repository.GroupFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * GroupFacilityService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【GroupFacilityService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/08-集团管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 GroupFacilityService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
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
        validateFacility(facility);
        if (groupFacilityRepository.findByFacilityCode(facility.getFacilityCode()) != null) {
            throw new IllegalArgumentException("该设施代码已存在");
        }
        facility.setId(null);
        return groupFacilityRepository.save(facility);
    }
    
    /**
     * 更新设施
     * @param facility 设施对象
     * @return 更新后的设施对象
     */
    public GroupFacility updateFacility(GroupFacility facility) {
        GroupFacility existing = groupFacilityRepository.findById(facility.getId())
                .orElseThrow(() -> new IllegalArgumentException("设施不存在"));
        if (facility.getFacilityCode() != null
                && !existing.getFacilityCode().equals(facility.getFacilityCode())) {
            throw new IllegalArgumentException("设施代码保存后不可修改");
        }
        facility.setFacilityCode(existing.getFacilityCode());
        validateFacility(facility);
        existing.setScope(facility.getScope());
        existing.setFacilityType(facility.getFacilityType());
        existing.setFacilityName(facility.getFacilityName());
        existing.setDescription(facility.getDescription());
        existing.setAvailable(facility.getAvailable());
        return groupFacilityRepository.save(existing);
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

    /** 校验设施录入的必填字段与枚举范围。 */
    private void validateFacility(GroupFacility facility) {
        if (facility.getFacilityCode() == null || facility.getFacilityCode().isBlank()
                || facility.getFacilityName() == null || facility.getFacilityName().isBlank()
                || facility.getFacilityType() == null || facility.getFacilityType().isBlank()) {
            throw new IllegalArgumentException("设施代码、名称和分类为必填项");
        }
        if (!("hotel".equals(facility.getScope()) || "room_type".equals(facility.getScope()))) {
            throw new IllegalArgumentException("设施适用范围无效");
        }
        if (facility.getAvailable() == null) {
            facility.setAvailable(true);
        }
    }
}
