package com.crs.repository;

import com.crs.entity.RoomTypeDiff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;
import java.util.Optional;

/**
 * 房型差价仓库接口
 * 用于房型差价数据的CRUD操作
 */
@Repository
public interface RoomTypeDiffRepository extends JpaRepository<RoomTypeDiff, Integer> {

    /** 按主键与租户双重约束查询。 */
    Optional<RoomTypeDiff> findByIdAndTenantId(Integer id, Integer tenantId);
    
    /**
     * 根据差价体系ID查询房型差价列表
     * @param systemId 差价体系ID
     * @return 房型差价列表
     */
    List<RoomTypeDiff> findBySystemId(Integer systemId);

    List<RoomTypeDiff> findBySystemIdAndTenantId(Integer systemId, Integer tenantId);
    
    /**
     * 根据差价体系ID和状态查询房型差价列表
     * @param systemId 差价体系ID
     * @param status 状态
     * @return 房型差价列表
     */
    List<RoomTypeDiff> findBySystemIdAndStatus(Integer systemId, RoomTypeDiff.Status status);

    List<RoomTypeDiff> findBySystemIdAndTenantIdAndStatus(Integer systemId, Integer tenantId, RoomTypeDiff.Status status);
    
    /**
     * 根据差价体系ID和房型ID查询房型差价
     * @param systemId 差价体系ID
     * @param roomTypeId 房型ID
     * @return 房型差价信息
     */
    List<RoomTypeDiff> findBySystemIdAndRoomTypeId(Integer systemId, Integer roomTypeId);
    
    /**
     * 根据日期范围查询房型差价
     * @param systemId 差价体系ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 房型差价列表
     */
    List<RoomTypeDiff> findBySystemIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Integer systemId, Date endDate, Date startDate);
    
    /**
     * 根据状态查询房型差价
     * @param status 状态
     * @return 房型差价列表
     */
    List<RoomTypeDiff> findByStatus(RoomTypeDiff.Status status);
    
    /**
     * 根据差价体系ID删除房型差价
     * @param systemId 差价体系ID
     */
    void deleteBySystemId(Integer systemId);

    List<RoomTypeDiff> findBySystemCode(String systemCode);

    List<RoomTypeDiff> findBySystemCodeAndStatus(String systemCode, RoomTypeDiff.Status status);

    List<RoomTypeDiff> findBySystemCodeAndRoomTypeCode(String systemCode, String roomTypeCode);

    List<RoomTypeDiff> findByTenantId(Integer tenantId);

    void deleteBySystemCode(String systemCode);
}
