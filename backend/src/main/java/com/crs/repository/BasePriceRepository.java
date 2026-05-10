package com.crs.repository;

import com.crs.entity.BasePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

/**
 * 基础价格数据访问接口 (BasePriceRepository)
 * 
 * <p>提供对 {@link BasePrice} 实体的数据库交互能力。支持基于酒店、价格类型、房型以及日期的多维度价格检索。</p>
 */
@Repository
public interface BasePriceRepository extends JpaRepository<BasePrice, Integer> {
    
    /**
     * 获取指定酒店下的所有价格记录。
     * 
     * @param hotelId 酒店 ID
     * @return 基础价格列表
     */
    List<BasePrice> findByHotelId(Integer hotelId);
    
    /**
     * 获取指定酒店下特定状态的价格记录。
     * 
     * @param hotelId 酒店 ID
     * @param status 状态
     * @return 基础价格列表
     */
    List<BasePrice> findByHotelIdAndStatus(Integer hotelId, BasePrice.Status status);
    
    /**
     * 获取指定酒店、价格类型及房型下的所有历史/未来价格记录。
     * 
     * @param hotelId 酒店 ID
     * @param rateTypeId 价格类型 ID
     * @param roomTypeId 房型 ID
     * @return 基础价格列表
     */
    List<BasePrice> findByHotelIdAndRateTypeIdAndRoomTypeId(Integer hotelId, Integer rateTypeId, Integer roomTypeId);
    
    /**
     * 查询指定酒店在特定日期范围内的所有价格。
     * 
     * @param hotelId 酒店 ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 基础价格列表
     */
    List<BasePrice> findByHotelIdAndDateBetween(Integer hotelId, Date startDate, Date endDate);
    
    /**
     * 精确查询特定日期、特定房型及价格类型的记录。
     * 
     * @param hotelId 酒店 ID
     * @param rateTypeId 价格类型 ID
     * @param roomTypeId 房型 ID
     * @param date 日期
     * @return 匹配的价格记录列表（通常为单条或空）
     */
    List<BasePrice> findByHotelIdAndRateTypeIdAndRoomTypeIdAndDate(Integer hotelId, Integer rateTypeId, Integer roomTypeId, Date date);
    
    /**
     * 全局查找特定状态的价格记录。
     * 
     * @param status 状态
     * @return 基础价格列表
     */
    List<BasePrice> findByStatus(BasePrice.Status status);

    /**
     * 根据酒店外部编码查询。
     */
    List<BasePrice> findByHotelCode(String hotelCode);

    /**
     * 根据酒店外部编码和日期范围查询。
     */
    List<BasePrice> findByHotelCodeAndDateBetween(String hotelCode, Date startDate, Date endDate);

    /**
     * 根据编码组合（酒店、价格类型、房型）查询。
     */
    List<BasePrice> findByHotelCodeAndRateTypeCodeAndRoomTypeCode(String hotelCode, String rateTypeCode, String roomTypeCode);

    /**
     * 根据编码组合及精确日期查询。
     */
    List<BasePrice> findByHotelCodeAndRateTypeCodeAndRoomTypeCodeAndDate(String hotelCode, String rateTypeCode, String roomTypeCode, Date date);

    /**
     * 根据酒店编码和状态查询。
     */
    List<BasePrice> findByHotelCodeAndStatus(String hotelCode, BasePrice.Status status);
}

