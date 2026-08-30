package com.crs.repository;

import com.crs.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 酒店数据访问接口 (HotelRepository)
 * 
 * <p>提供对 {@link Hotel} 实体的数据库交互能力。所有查询均遵循多租户隔离原则，优先通过 `tenantId` 过滤。</p>
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    /** 在租户维度下根据主键查询酒店。 */
    Optional<Hotel> findByIdAndTenantId(Integer id, Integer tenantId);

    /**
     * 在指定租户下根据酒店编码查找酒店。
     * 
     * @param hotelCode 酒店唯一编码
     * @param tenantId 租户 ID
     * @return 包含酒店实体的 Optional 对象
     */
    Optional<Hotel> findByHotelCodeAndTenantId(String hotelCode, Integer tenantId);
    
    /**
     * 获取指定租户下的所有酒店。
     * 
     * @param tenantId 租户 ID
     * @return 酒店列表
     */
    List<Hotel> findByTenantId(Integer tenantId);
    
    /**
     * 获取指定租户下特定运营状态的酒店。
     * 
     * @param tenantId 租户 ID
     * @param status 状态（如 active）
     * @return 酒店列表
     */
    List<Hotel> findByTenantIdAndStatus(Integer tenantId, Hotel.Status status);
    
    /**
     * 根据中文名称模糊匹配酒店（限租户内）。
     */
    List<Hotel> findByTenantIdAndChineseNameContaining(Integer tenantId, String chineseName);
    
    /**
     * 根据城市查找酒店（限租户内）。
     */
    List<Hotel> findByTenantIdAndCity(Integer tenantId, String city);
    
    /**
     * 校验在指定租户下是否存在该酒店编码。
     * 
     * @param hotelCode 酒店编码
     * @param tenantId 租户 ID
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByHotelCodeAndTenantId(String hotelCode, Integer tenantId);

    /**
     * 多维度组合过滤查询酒店。
     * 支持分页、城市过滤、关键字搜索以及白名单过滤。
     * 
     * @param tenantId 必填，租户 ID 隔离
     * @param status 必填，运营状态
     * @param city 可选，所在城市
     * @param keyword 可选，支持中文名或英文名模糊搜索
     * @param hotelIds 必填，当前用户有权访问的酒店 ID 集合（白名单）
     * @param pageable 分页参数
     * @return 分页后的酒店列表
     */
    @org.springframework.data.jpa.repository.Query("SELECT h FROM Hotel h WHERE h.tenantId = :tenantId AND h.status = :status " +
            "AND (:city IS NULL OR :city = '' OR h.city = :city) " +
            "AND (:keyword IS NULL OR :keyword = '' OR h.chineseName LIKE %:keyword% OR h.englishName LIKE %:keyword%) " +
            "AND h.hotelCode IN :hotelCodes")
    org.springframework.data.domain.Page<Hotel> findWithFilters(
            @org.springframework.data.repository.query.Param("tenantId") Integer tenantId,
            @org.springframework.data.repository.query.Param("status") Hotel.Status status,
            @org.springframework.data.repository.query.Param("city") String city,
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("hotelCodes") java.util.Collection<String> hotelCodes,
            org.springframework.data.domain.Pageable pageable);
}
