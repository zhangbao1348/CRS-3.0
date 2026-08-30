package com.crs.service;

import com.crs.entity.HotelFacility;
import com.crs.repository.HotelFacilityRepository;
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
    
    private final HotelFacilityRepository hotelFacilityRepository;

    public HotelFacilityService(HotelFacilityRepository hotelFacilityRepository) {
        this.hotelFacilityRepository = hotelFacilityRepository;
    }

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }
    
    public HotelFacility createFacility(HotelFacility facility) {
        facility.setTenantId(getCurrentTenantId());
        return hotelFacilityRepository.save(facility);
    }
    
    public HotelFacility updateFacility(HotelFacility facility) {
        HotelFacility existing = hotelFacilityRepository
                .findByIdAndTenantId(facility.getId(), getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("酒店设施不存在或无权访问"));
        existing.setFacilityType(facility.getFacilityType());
        existing.setFacilityName(facility.getFacilityName());
        existing.setFacilityCode(facility.getFacilityCode());
        existing.setAvailable(facility.getAvailable());
        return hotelFacilityRepository.save(existing);
    }
    
    public void deleteFacility(Integer id) {
        HotelFacility existing = hotelFacilityRepository
                .findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("酒店设施不存在或无权访问"));
        hotelFacilityRepository.delete(existing);
    }
    
    public List<HotelFacility> getFacilitiesByHotelCode(String hotelCode) {
        return hotelFacilityRepository.findByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }

    public List<HotelFacility> getFacilitiesByHotelCodeAndType(String hotelCode, String facilityType) {
        return hotelFacilityRepository.findByTenantIdAndHotelCodeAndFacilityType(getCurrentTenantId(), hotelCode, facilityType);
    }

    public void deleteFacilitiesByHotelCode(String hotelCode) {
        hotelFacilityRepository.deleteByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }

    public List<HotelFacility> getAllFacilities() {
        return hotelFacilityRepository.findByTenantId(getCurrentTenantId());
    }
}
