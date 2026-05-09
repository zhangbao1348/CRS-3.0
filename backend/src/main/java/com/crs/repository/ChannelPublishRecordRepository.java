package com.crs.repository;

import com.crs.entity.ChannelPublishRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
