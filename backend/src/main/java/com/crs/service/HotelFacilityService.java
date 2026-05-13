package com.crs.service;

import com.crs.entity.HotelFacility;
import com.crs.repository.HotelFacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * HotelFacilityService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【HotelFacilityService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/09-系统设置.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 HotelFacilityService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class HotelFacilityService {
    
    @Autowired
    private HotelFacilityRepository hotelFacilityRepository;
    
    public HotelFacility createFacility(HotelFacility facility) {
        if (facility.getTenantId() == null) {
            facility.setTenantId(com.crs.util.TenantContext.getTenantId());
        }
        return hotelFacilityRepository.save(facility);
    }
    
    public HotelFacility updateFacility(HotelFacility facility) {
        return hotelFacilityRepository.save(facility);
    }
    
    public void deleteFacility(Integer id) {
        hotelFacilityRepository.deleteById(id);
    }
    
    public List<HotelFacility> getFacilitiesByHotelCode(String hotelCode) {
        return hotelFacilityRepository.findByTenantIdAndHotelCode(com.crs.util.TenantContext.getTenantId(), hotelCode);
    }

    public List<HotelFacility> getFacilitiesByHotelCodeAndType(String hotelCode, String facilityType) {
        return hotelFacilityRepository.findByTenantIdAndHotelCodeAndFacilityType(com.crs.util.TenantContext.getTenantId(), hotelCode, facilityType);
    }

    public void deleteFacilitiesByHotelCode(String hotelCode) {
        hotelFacilityRepository.deleteByTenantIdAndHotelCode(com.crs.util.TenantContext.getTenantId(), hotelCode);
    }

    public List<HotelFacility> getAllFacilities() {
        return hotelFacilityRepository.findAll();
    }
}