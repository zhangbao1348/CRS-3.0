package com.crs.service;

import com.crs.entity.HotelRoomTypeAllocation;
import com.crs.repository.HotelRoomTypeAllocationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * HotelRoomTypeAllocationService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【HotelRoomTypeAllocationService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/09-系统设置.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 HotelRoomTypeAllocationService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class HotelRoomTypeAllocationService {
    
    private final HotelRoomTypeAllocationRepository hotelRoomTypeAllocationRepository;

    public HotelRoomTypeAllocationService(HotelRoomTypeAllocationRepository hotelRoomTypeAllocationRepository) {
        this.hotelRoomTypeAllocationRepository = hotelRoomTypeAllocationRepository;
    }

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    // =====================================================================
    // 合规方法：使用 hotelCode / roomTypeCode 作为关联条件（符合CODE关联规范）
    // =====================================================================

    /**
     * 根据 hotelCode 获取分配列表
     * 关联查询原则：tenantId + hotelCode
     */
    public List<HotelRoomTypeAllocation> getAllocationsByHotelCode(String hotelCode) {
        return hotelRoomTypeAllocationRepository.findByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }

    /**
     * 根据 hotelCode 和 roomTypeCode 获取单条分配
     * 关联查询原则：tenantId + hotelCode + roomTypeCode
     */
    public HotelRoomTypeAllocation getAllocationByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode) {
        return hotelRoomTypeAllocationRepository.findByTenantIdAndHotelCodeAndRoomTypeCode(getCurrentTenantId(), hotelCode, roomTypeCode);
    }

    /**
     * 根据 hotelCode 获取已分配的房型列表
     */
    public List<HotelRoomTypeAllocation> getAllocatedRoomTypesByHotelCode(String hotelCode) {
        return hotelRoomTypeAllocationRepository.findByTenantIdAndHotelCodeAndAllocated(getCurrentTenantId(), hotelCode, true);
    }

    /**
     * 根据 hotelCode 删除所有分配
     */
    public void deleteAllocationsByHotelCode(String hotelCode) {
        hotelRoomTypeAllocationRepository.deleteByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }

    public HotelRoomTypeAllocation createAllocation(HotelRoomTypeAllocation allocation) {
        allocation.setTenantId(getCurrentTenantId());
        return hotelRoomTypeAllocationRepository.save(allocation);
    }
    
    public HotelRoomTypeAllocation updateAllocation(HotelRoomTypeAllocation allocation) {
        HotelRoomTypeAllocation existing = hotelRoomTypeAllocationRepository
                .findByIdAndTenantId(allocation.getId(), getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("酒店房型分配不存在或无权访问"));
        existing.setAllocated(allocation.getAllocated());
        existing.setRoomInfoEditable(allocation.getRoomInfoEditable());
        return hotelRoomTypeAllocationRepository.save(existing);
    }
    
    public void deleteAllocation(Integer id) {
        HotelRoomTypeAllocation existing = hotelRoomTypeAllocationRepository
                .findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("酒店房型分配不存在或无权访问"));
        hotelRoomTypeAllocationRepository.delete(existing);
    }
}
