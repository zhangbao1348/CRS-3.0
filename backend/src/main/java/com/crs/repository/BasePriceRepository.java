package com.crs.repository;

import com.crs.entity.BasePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;
import java.util.Optional;

/**
 * 基础价格数据访问接口 (BasePriceRepository)
 * 
 * <p>提供对 {@link BasePrice} 实体的数据库交互能力。支持基于酒店、价格类型、房型以及日期的多维度价格检索。</p>
 */
@Repository
public interface BasePriceRepository extends JpaRepository<BasePrice, Integer> {
    
    // 业务关联已统一切换为基于业务编码 (Code) 进行检索。
    // 请优先使用下方的 ByCode 系列方法。

    /** 根据租户和酒店外部编码查询 */
    List<BasePrice> findByTenantIdAndHotelCode(Integer tenantId, String hotelCode);

    /** 根据租户和酒店编码及状态查询 */
    List<BasePrice> findByTenantIdAndHotelCodeAndStatus(Integer tenantId, String hotelCode, BasePrice.Status status);

    /** 根据租户和酒店编码安全查询特定日期范围内的价格记录 */
    List<BasePrice> findByTenantIdAndHotelCodeAndDateBetween(Integer tenantId, String hotelCode, Date startDate, Date endDate);

    /** 根据编码组合（租户、酒店、价格类型、房型）查询 */
    List<BasePrice> findByTenantIdAndHotelCodeAndRateTypeCodeAndRoomTypeCode(Integer tenantId, String hotelCode, String rateTypeCode, String roomTypeCode);

    /** 根据租户ID查询全量基础价格 */
    List<BasePrice> findByTenantId(Integer tenantId);

    /** 根据编码组合及精确日期查询 */
    Optional<BasePrice> findByTenantIdAndHotelCodeAndRateTypeCodeAndRoomTypeCodeAndDate(Integer tenantId, String hotelCode, String rateTypeCode, String roomTypeCode, Date date);
}

