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

@Repository
public interface GroupRoomTypeHotelRepository extends JpaRepository<GroupRoomTypeHotel, Integer> {
    
    /**
     * 根据集团房型ID获取关联列表
     * @param groupRoomTypeId 集团房型ID
     * @return 关联列表
     */
    List<GroupRoomTypeHotel> findByGroupRoomTypeId(Integer groupRoomTypeId);
    
    /**
     * 根据酒店ID获取关联列表
     * @param hotelId 酒店ID
     * @return 关联列表
     */
    List<GroupRoomTypeHotel> findByHotelId(Integer hotelId);
    
    /**
     * 根据集团房型ID和酒店ID获取关联信息
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     * @return 关联信息
     */
    Optional<GroupRoomTypeHotel> findByGroupRoomTypeIdAndHotelId(Integer groupRoomTypeId, Integer hotelId);
    
    /**
     * 根据集团房型ID和分配状态获取关联列表
     * @param groupRoomTypeId 集团房型ID
     * @param allocated 是否分配
     * @return 关联列表
     */
    List<GroupRoomTypeHotel> findByGroupRoomTypeIdAndAllocated(Integer groupRoomTypeId, Boolean allocated);
    
    /**
     * 检查集团房型和酒店的关联是否存在
     * @param groupRoomTypeId 集团房型ID
     * @param hotelId 酒店ID
     * @return 是否存在
     */
    boolean existsByGroupRoomTypeIdAndHotelId(Integer groupRoomTypeId, Integer hotelId);
    
    /**
     * 根据集团房型ID删除所有关联
     * @param groupRoomTypeId 集团房型ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupRoomTypeHotel grth WHERE grth.groupRoomTypeId = :groupRoomTypeId")
    void deleteByGroupRoomTypeId(@Param("groupRoomTypeId") Integer groupRoomTypeId);

    List<GroupRoomTypeHotel> findByGroupRoomTypeCode(String groupRoomTypeCode);

    List<GroupRoomTypeHotel> findByHotelCode(String hotelCode);

    Optional<GroupRoomTypeHotel> findByGroupRoomTypeCodeAndHotelCode(String groupRoomTypeCode, String hotelCode);

    List<GroupRoomTypeHotel> findByGroupRoomTypeCodeAndAllocated(String groupRoomTypeCode, Boolean allocated);

    boolean existsByGroupRoomTypeCodeAndHotelCode(String groupRoomTypeCode, String hotelCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupRoomTypeHotel grth WHERE grth.groupRoomTypeCode = :groupRoomTypeCode")
    void deleteByGroupRoomTypeCode(@Param("groupRoomTypeCode") String groupRoomTypeCode);
}
