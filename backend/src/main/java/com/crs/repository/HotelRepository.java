package com.crs.repository;

import com.crs.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 酒店仓库接口
 * 用于酒店数据的CRUD操作
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    
    /**
     * 根据酒店代码查询酒店
     * @param hotelCode 酒店代码
     * @return 酒店信息
     */
    Optional<Hotel> findByHotelCode(String hotelCode);
    
    /**
     * 根据租户ID查询酒店列表
     * @param tenantId 租户ID
     * @return 酒店列表
     */
    List<Hotel> findByTenantId(Integer tenantId);
    
    /**
     * 根据租户ID和状态查询酒店列表
     * @param tenantId 租户ID
     * @param status 状态
     * @return 酒店列表
     */
    List<Hotel> findByTenantIdAndStatus(Integer tenantId, Hotel.Status status);
    
    /**
     * 根据酒店名称查询酒店
     * @param chineseName 中文名称
     * @return 酒店列表
     */
    List<Hotel> findByChineseNameContaining(String chineseName);
    
    /**
     * 根据城市查询酒店
     * @param city 城市
     * @return 酒店列表
     */
    List<Hotel> findByCity(String city);
    
    /**
     * 根据状态查询酒店
     * @param status 状态
     * @return 酒店列表
     */
    List<Hotel> findByStatus(Hotel.Status status);
    
    /**
     * 检查酒店代码是否存在
     * @param hotelCode 酒店代码
     * @return 是否存在
     */
    boolean existsByHotelCode(String hotelCode);
}
