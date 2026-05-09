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

    Optional<Hotel> findByHotelCodeAndTenantId(String hotelCode, Integer tenantId);
    
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
    
    boolean existsByHotelCodeAndTenantId(String hotelCode, Integer tenantId);

    @org.springframework.data.jpa.repository.Query("SELECT h FROM Hotel h WHERE h.tenantId = :tenantId AND h.status = :status " +
            "AND (:city IS NULL OR :city = '' OR h.city = :city) " +
            "AND (:keyword IS NULL OR :keyword = '' OR h.chineseName LIKE %:keyword% OR h.englishName LIKE %:keyword%) " +
            "AND h.id IN :hotelIds")
    org.springframework.data.domain.Page<Hotel> findWithFilters(
            @org.springframework.data.repository.query.Param("tenantId") Integer tenantId,
            @org.springframework.data.repository.query.Param("status") Hotel.Status status,
            @org.springframework.data.repository.query.Param("city") String city,
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("hotelIds") java.util.Collection<Integer> hotelIds,
            org.springframework.data.domain.Pageable pageable);
}
