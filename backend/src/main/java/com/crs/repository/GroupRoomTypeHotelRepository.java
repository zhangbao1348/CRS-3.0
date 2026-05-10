package com.crs.repository;

import com.crs.entity.GroupRoomTypeHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * GroupRoomTypeHotelRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【GroupRoomTypeHotelRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/08-集团管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 GroupRoomTypeHotelRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface GroupRoomTypeHotelRepository extends JpaRepository<GroupRoomTypeHotel, Integer> {

    // =====================================================================
    // 合规方法：使用 tenantId + hotelCode（符合CODE关联规范）
    // 关联查询原则：tenantId + hotelCode 作为酒店维度主上下文
    // =====================================================================

    /**
     * 根据 tenantId + hotelCode 获取房型分配列表（推荐使用）
     * 防止跨租户数据泄露
     */
    List<GroupRoomTypeHotel> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /** 根据集团房型CODE + 酒店CODE 精确查询（推荐使用） */
    Optional<GroupRoomTypeHotel> findByGroupRoomTypeCodeAndHotelCode(String groupRoomTypeCode, String hotelCode);

    /** 根据集团房型CODE获取关联列表 */
    List<GroupRoomTypeHotel> findByGroupRoomTypeCode(String groupRoomTypeCode);

    /** 根据集团房型CODE + 分配状态查询 */
    List<GroupRoomTypeHotel> findByGroupRoomTypeCodeAndAllocated(String groupRoomTypeCode, Boolean allocated);

    /** 检查集团房型CODE + 酒店CODE 是否存在 */
    boolean existsByGroupRoomTypeCodeAndHotelCode(String groupRoomTypeCode, String hotelCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupRoomTypeHotel grth WHERE grth.groupRoomTypeCode = :groupRoomTypeCode")
    void deleteByGroupRoomTypeCode(@Param("groupRoomTypeCode") String groupRoomTypeCode);

    // =====================================================================
    // 旧 ID 方法（仅内部使用，禁止新代码使用）
    // =====================================================================

    /**
     * 根据集团房型ID获取关联列表
     * @param groupRoomTypeId 集团房型ID
     */
    List<GroupRoomTypeHotel> findByGroupRoomTypeId(Integer groupRoomTypeId);

    /**
     * @deprecated 请使用 findByTenantIdAndHotelCode(Integer, String)
     */
    @Deprecated
    List<GroupRoomTypeHotel> findByHotelId(Integer hotelId);

    /**
     * @deprecated 请使用基于 CODE 的查询方法
     */
    @Deprecated
    Optional<GroupRoomTypeHotel> findByGroupRoomTypeIdAndHotelId(Integer groupRoomTypeId, Integer hotelId);

    List<GroupRoomTypeHotel> findByGroupRoomTypeIdAndAllocated(Integer groupRoomTypeId, Boolean allocated);

    boolean existsByGroupRoomTypeIdAndHotelId(Integer groupRoomTypeId, Integer hotelId);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupRoomTypeHotel grth WHERE grth.groupRoomTypeId = :groupRoomTypeId")
    void deleteByGroupRoomTypeId(@Param("groupRoomTypeId") Integer groupRoomTypeId);

    /**
     * @deprecated 缺少 tenantId 约束，请使用 findByTenantIdAndHotelCode
     */
    @Deprecated
    List<GroupRoomTypeHotel> findByHotelCode(String hotelCode);
}

