package com.crs.repository;

import com.crs.entity.ChannelPublishRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ChannelPublishRecordRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【ChannelPublishRecordRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/13-渠道管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 ChannelPublishRecordRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface ChannelPublishRecordRepository extends JpaRepository<ChannelPublishRecord, Integer> {

    List<ChannelPublishRecord> findByTenantIdAndHotelCodeAndChannelCodeAndStatus(
            Integer tenantId, String hotelCode, String channelCode, String status);

    boolean existsByTenantIdAndHotelCodeAndChannelCodeAndRateCodeAndRoomTypeCode(
            Integer tenantId, String hotelCode, String channelCode, String rateCode, String roomTypeCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChannelPublishRecord r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode AND r.channelCode = :channelCode AND r.rateCode = :rateCode AND r.roomTypeCode = :roomTypeCode")
    void deleteByKey(@Param("tenantId") Integer tenantId, @Param("hotelCode") String hotelCode,
                     @Param("channelCode") String channelCode, @Param("rateCode") String rateCode,
                     @Param("roomTypeCode") String roomTypeCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChannelPublishRecord r WHERE r.tenantId = :tenantId AND r.hotelCode = :hotelCode AND r.channelCode = :channelCode AND r.rateCode = :rateCode")
    void deleteByRateCode(@Param("tenantId") Integer tenantId, @Param("hotelCode") String hotelCode,
                          @Param("channelCode") String channelCode, @Param("rateCode") String rateCode);
}
