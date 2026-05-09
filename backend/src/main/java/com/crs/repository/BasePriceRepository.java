package com.crs.repository;

import com.crs.entity.BasePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

/**
 * 基础价格仓库接口
 * 用于基础价格数据的CRUD操作
 */
@Repository
public interface BasePriceRepository extends JpaRepository<BasePrice, Integer> {
    
    /**
     * 根据酒店ID查询基础价格列表
     * @param hotelId 酒店ID
     * @return 基础价格列表
     */
    List<BasePrice> findByHotelId(Integer hotelId);
    
    /**
     * 根据酒店ID和状态查询基础价格列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 基础价格列表
     */
    List<BasePrice> findByHotelIdAndStatus(Integer hotelId, BasePrice.Status status);
    
    /**
     * 根据酒店ID、价格类型ID和房型ID查询基础价格
     * @param hotelId 酒店ID
     * @param rateTypeId 价格类型ID
     * @param roomTypeId 房型ID
     * @return 基础价格列表
     */
    List<BasePrice> findByHotelIdAndRateTypeIdAndRoomTypeId(Integer hotelId, Integer rateTypeId, Integer roomTypeId);
    
    /**
     * 根据日期范围查询基础价格
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 基础价格列表
     */
    List<BasePrice> findByHotelIdAndDateBetween(Integer hotelId, Date startDate, Date endDate);
    
    /**
     * 根据酒店ID、价格类型ID、房型ID和日期查询基础价格
     * @param hotelId 酒店ID
     * @param rateTypeId 价格类型ID
     * @param roomTypeId 房型ID
     * @param date 日期
     * @return 基础价格信息
     */
    List<BasePrice> findByHotelIdAndRateTypeIdAndRoomTypeIdAndDate(Integer hotelId, Integer rateTypeId, Integer roomTypeId, Date date);
    
    /**
     * 根据状态查询基础价格
     * @param status 状态
     * @return 基础价格列表
     */
    List<BasePrice> findByStatus(BasePrice.Status status);

    List<BasePrice> findByHotelCode(String hotelCode);

    List<BasePrice> findByHotelCodeAndDateBetween(String hotelCode, Date startDate, Date endDate);

    List<BasePrice> findByHotelCodeAndRateTypeCodeAndRoomTypeCode(String hotelCode, String rateTypeCode, String roomTypeCode);

    List<BasePrice> findByHotelCodeAndRateTypeCodeAndRoomTypeCodeAndDate(String hotelCode, String rateTypeCode, String roomTypeCode, Date date);

    List<BasePrice> findByHotelCodeAndStatus(String hotelCode, BasePrice.Status status);
}
