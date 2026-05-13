package com.crs.repository;

import com.crs.entity.HotelRoomTypeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 酒店房型分配数据访问接口 (HotelRoomTypeAllocationRepository)
 * 
 * <p>提供对 {@link HotelRoomTypeAllocation} 实体的数据库操作。负责管理集团标准房型在单店酒店层级的可见性与编辑权限。</p>
 * 
 * <p>规范要求：</p>
 * <ul>
 *     <li>**租户安全**：所有操作必须携带 `tenantId`，防止跨集团数据泄露或越权删除。</li>
 *     <li>**外部编码优先**：优先使用 `hotelCode` 和 `roomTypeCode` 进行业务定位。</li>
 * </ul>
 */
public interface HotelRoomTypeAllocationRepository extends JpaRepository<HotelRoomTypeAllocation, Integer> {

    // =====================================================================
    // 合规方法：必须包含 tenantId（符合多租户隔离规范）
    // =====================================================================

    /**
     * 获取指定租户下、某个酒店的所有房型分配记录。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @return 分配记录列表
     */
    List<HotelRoomTypeAllocation> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 在指定租户内，精确查找某个酒店对特定房型的分配权限。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @param roomTypeCode 房型代码
     * @return 分配记录
     */
    HotelRoomTypeAllocation findByTenantIdAndHotelCodeAndRoomTypeCode(Integer tenantId, String hotelCode, String roomTypeCode);

    /**
     * 获取指定酒店下、特定分配状态的房型列表。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @param allocated 是否已分配
     * @return 分配记录列表
     */
    List<HotelRoomTypeAllocation> findByTenantIdAndHotelCodeAndAllocated(Integer tenantId, String hotelCode, Boolean allocated);

    /**
     * 安全删除：仅删除属于该租户的特定酒店房型分配记录。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     */
    void deleteByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

}