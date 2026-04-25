package com.crs.repository;

import com.crs.entity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 价格计划仓库接口
 * 用于价格计划数据的CRUD操作
 */
@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Integer> {
    
    /**
     * 根据酒店ID查询价格计划列表
     * @param hotelId 酒店ID
     * @return 价格计划列表
     */
    List<RatePlan> findByHotelId(Integer hotelId);
    
    /**
     * 根据酒店ID和状态查询价格计划列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 价格计划列表
     */
    List<RatePlan> findByHotelIdAndStatus(Integer hotelId, String status);
    
    /**
     * 根据酒店ID和价格计划代码查询价格计划
     * @param hotelId 酒店ID
     * @param rateCode 价格计划代码
     * @return 价格计划信息
     */
    Optional<RatePlan> findByHotelIdAndRateCode(Integer hotelId, String rateCode);
    
    /**
     * 根据集团房价码ID查询价格计划列表
     * @param sourceGroupRateCodeId 集团房价码ID
     * @return 价格计划列表
     */
    List<RatePlan> findBySourceGroupRateCodeId(Integer sourceGroupRateCodeId);
    
    /**
     * 检查酒店内价格计划代码是否存在
     * @param hotelId 酒店ID
     * @param rateCode 价格计划代码
     * @return 是否存在
     */
    boolean existsByHotelIdAndRateCode(Integer hotelId, String rateCode);
    
    /**
     * 检查酒店内价格计划代码是否存在（排除指定ID）
     * @param hotelId 酒店ID
     * @param rateCode 价格计划代码
     * @param id 排除的价格计划ID
     * @return 是否存在
     */
    boolean existsByHotelIdAndRateCodeAndIdNot(Integer hotelId, String rateCode, Integer id);
    
    /**
     * 根据酒店代码查询价格计划列表
     * @param hotelCode 酒店代码
     * @return 价格计划列表
     */
    List<RatePlan> findByHotelCode(String hotelCode);
    
    /**
     * 根据酒店代码和父级房价码代码查询衍生价格计划
     */
    List<RatePlan> findByHotelCodeAndParentRateCodeAndStatus(String hotelCode, String parentRateCode, String status);
}
