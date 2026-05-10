package com.crs.repository;

import com.crs.entity.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 酒店图片数据访问接口 (HotelImageRepository)
 * 
 * <p>提供对 {@link HotelImage} 实体的数据库操作能力。支持基于酒店编码的多维度图片检索与排序。</p>
 * 
 * <p>规范要求：</p>
 * <ul>
 *     <li>**安全隔离**：所有查询与删除操作必须包含 `tenantId`，严禁跨租户操作。</li>
 *     <li>**展示优先**：检索结果通常需要按 `sortOrder` 进行升序排列。</li>
 * </ul>
 */
public interface HotelImageRepository extends JpaRepository<HotelImage, Integer> {
    
    // =====================================================================
    // 合规方法：必须包含 tenantId（符合多租户隔离规范）
    // =====================================================================

    /**
     * 获取指定酒店下的所有图片。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @return 图片列表
     */
    List<HotelImage> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /**
     * 获取指定酒店下、特定类型的图片。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @param imageType 图片类型 (如 exterior)
     * @return 图片列表
     */
    List<HotelImage> findByTenantIdAndHotelCodeAndImageType(Integer tenantId, String hotelCode, String imageType);

    /**
     * 获取指定酒店下的有序图片列表。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     * @return 按 sortOrder 升序排列的图片列表
     */
    List<HotelImage> findByTenantIdAndHotelCodeOrderBySortOrderAsc(Integer tenantId, String hotelCode);

    /**
     * 安全删除：仅删除属于该租户的特定酒店图片记录。
     * 
     * @param tenantId 租户 ID
     * @param hotelCode 酒店编码
     */
    void deleteByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    // =====================================================================
    // 已废弃方法：缺少 tenantId 约束（存在跨租户数据风险，禁止新代码使用）
    // =====================================================================

    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    List<HotelImage> findByHotelCode(String hotelCode);

    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndHotelCodeAndImageType(Integer, String, String)} 
     */
    @Deprecated
    List<HotelImage> findByHotelCodeAndImageType(String hotelCode, String imageType);

    /** 
     * @deprecated 请改用 {@link #findByTenantIdAndHotelCodeOrderBySortOrderAsc(Integer, String)} 
     */
    @Deprecated
    List<HotelImage> findByHotelCodeOrderBySortOrderAsc(String hotelCode);

    /** 
     * @deprecated 越权风险高。请改用 {@link #deleteByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    void deleteByHotelCode(String hotelCode);

    // =====================================================================
    // 已废弃方法：使用 hotelId（仅作兼容保留，禁止新代码使用）
    // =====================================================================

    /** 
     * @deprecated 内部 ID 关联已过时。请改用 {@link #findByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    List<HotelImage> findByHotelId(Integer hotelId);
    
    /** 
     * @deprecated 内部 ID 关联已过时。请改用 {@link #findByTenantIdAndHotelCodeAndImageType(Integer, String, String)} 
     */
    @Deprecated
    List<HotelImage> findByHotelIdAndImageType(Integer hotelId, String imageType);
    
    /** 
     * @deprecated 内部 ID 关联已过时。请改用 {@link #findByTenantIdAndHotelCodeOrderBySortOrderAsc(Integer, String)} 
     */
    @Deprecated
    List<HotelImage> findByHotelIdOrderBySortOrderAsc(Integer hotelId);
    
    /** 
     * @deprecated 内部 ID 操作已过时。请改用 {@link #deleteByTenantIdAndHotelCode(Integer, String)} 
     */
    @Deprecated
    void deleteByHotelId(Integer hotelId);
}