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
    List<RatePlan> findByHotelIdAndStatus(Integer hotelId, RatePlan.Status status);
    
    /**
     * 根据酒店ID和价格计划代码查询价格计划
     * @param hotelId 酒店ID
     * @param rateCode 价格计划代码
     * @return 价格计划信息
     */
    Optional<RatePlan> findByHotelIdAndRateCode(Integer hotelId, String rateCode);
    
    /**
     * 根据价格计划类型查询价格计划列表
     * @param type 价格计划类型
     * @return 价格计划列表
     */
    List<RatePlan> findByType(String type);
    
    /**
     * 根据父级价格计划代码查询价格计划列表
     * @param parentRateCode 父级价格计划代码
     * @return 价格计划列表
     */
    List<RatePlan> findByParentRateCode(String parentRateCode);
    
    /**
     * 根据市场码ID查询价格计划列表
     * @param marketCodeId 市场码ID
     * @return 价格计划列表
     */
    List<RatePlan> findByMarketCodeId(Integer marketCodeId);
    
    /**
     * 根据渠道码ID查询价格计划列表
     * @param channelCodeId 渠道码ID
     * @return 价格计划列表
     */
    List<RatePlan> findByChannelCodeId(Integer channelCodeId);
    
    /**
     * 根据状态查询价格计划
     * @param status 状态
     * @return 价格计划列表
     */
    List<RatePlan> findByStatus(RatePlan.Status status);
    
    /**
     * 检查酒店内价格计划代码是否存在
     * @param hotelId 酒店ID
     * @param rateCode 价格计划代码
     * @return 是否存在
     */
    boolean existsByHotelIdAndRateCode(Integer hotelId, String rateCode);
}
