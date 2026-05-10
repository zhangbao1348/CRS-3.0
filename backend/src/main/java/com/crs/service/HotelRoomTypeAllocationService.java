package com.crs.service;

import com.crs.entity.HotelRoomTypeAllocation;
import com.crs.repository.HotelRoomTypeAllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private HotelRoomTypeAllocationRepository hotelRoomTypeAllocationRepository;

    // =====================================================================
    // 合规方法：使用 hotelCode / roomTypeCode 作为关联条件（符合CODE关联规范）
    // =====================================================================

    /**
     * 根据 hotelCode 获取分配列表
     * 关联查询原则：tenantId + hotelCode
     */
    public List<HotelRoomTypeAllocation> getAllocationsByHotelCode(String hotelCode) {
        return hotelRoomTypeAllocationRepository.findByTenantIdAndHotelCode(com.crs.util.TenantContext.getTenantId(), hotelCode);
    }

    /**
     * 根据 hotelCode 和 roomTypeCode 获取单条分配
     * 关联查询原则：tenantId + hotelCode + roomTypeCode
     */
    public HotelRoomTypeAllocation getAllocationByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode) {
        return hotelRoomTypeAllocationRepository.findByTenantIdAndHotelCodeAndRoomTypeCode(com.crs.util.TenantContext.getTenantId(), hotelCode, roomTypeCode);
    }

    /**
     * 根据 hotelCode 获取已分配的房型列表
     */
    public List<HotelRoomTypeAllocation> getAllocatedRoomTypesByHotelCode(String hotelCode) {
        return hotelRoomTypeAllocationRepository.findByTenantIdAndHotelCodeAndAllocated(com.crs.util.TenantContext.getTenantId(), hotelCode, true);
    }

    /**
     * 根据 hotelCode 删除所有分配
     */
    public void deleteAllocationsByHotelCode(String hotelCode) {
        hotelRoomTypeAllocationRepository.deleteByTenantIdAndHotelCode(com.crs.util.TenantContext.getTenantId(), hotelCode);
    }

    // =====================================================================
    // 已废弃方法：使用 hotelId / roomTypeId（保留兼容，内部可重定向至Code版本）
    // =====================================================================

    /** @deprecated 请使用 getAllocationsByHotelCode(String hotelCode) */
    @Deprecated
    public List<HotelRoomTypeAllocation> getAllocationsByHotelId(Integer hotelId) {
        return hotelRoomTypeAllocationRepository.findByHotelId(hotelId);
    }
    
    /** @deprecated 请使用 getAllocationByHotelCodeAndRoomTypeCode(String, String) */
    @Deprecated
    public HotelRoomTypeAllocation getAllocationByHotelAndRoomType(Integer hotelId, Integer roomTypeId) {
        return hotelRoomTypeAllocationRepository.findByHotelIdAndRoomTypeId(hotelId, roomTypeId);
    }
    
    /** @deprecated 请使用 getAllocatedRoomTypesByHotelCode(String hotelCode) */
    @Deprecated
    public List<HotelRoomTypeAllocation> getAllocatedRoomTypesByHotelId(Integer hotelId) {
        return hotelRoomTypeAllocationRepository.findByHotelIdAndAllocated(hotelId, true);
    }
    
    public HotelRoomTypeAllocation createAllocation(HotelRoomTypeAllocation allocation) {
        return hotelRoomTypeAllocationRepository.save(allocation);
    }
    
    public HotelRoomTypeAllocation updateAllocation(HotelRoomTypeAllocation allocation) {
        return hotelRoomTypeAllocationRepository.save(allocation);
    }
    
    public void deleteAllocation(Integer id) {
        hotelRoomTypeAllocationRepository.deleteById(id);
    }

    /** @deprecated 请使用 deleteAllocationsByHotelCode(String hotelCode) */
    @Deprecated
    public void deleteAllocationsByHotelId(Integer hotelId) {
        hotelRoomTypeAllocationRepository.deleteByHotelId(hotelId);
    }
}