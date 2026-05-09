package com.crs.repository;

import com.crs.entity.RoomTypeFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoomTypeFacilityRepository extends JpaRepository<RoomTypeFacility, Integer> {

    List<RoomTypeFacility> findByRoomTypeId(Integer roomTypeId);

    List<RoomTypeFacility> findByHotelIdAndRoomTypeId(Integer hotelId, Integer roomTypeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomTypeFacility f WHERE f.roomTypeId = :roomTypeId")
    void deleteByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);

    List<RoomTypeFacility> findByHotelCodeAndRoomTypeCode(String hotelCode, String roomTypeCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomTypeFacility f WHERE f.hotelCode = :hotelCode AND f.roomTypeCode = :roomTypeCode")
    void deleteByHotelCodeAndRoomTypeCode(@Param("hotelCode") String hotelCode, @Param("roomTypeCode") String roomTypeCode);
}
