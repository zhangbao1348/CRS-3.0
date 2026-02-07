package com.crs.repository;

import com.crs.entity.ChannelCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 渠道码仓库接口
 * 用于渠道码数据的CRUD操作
 */
@Repository
public interface ChannelCodeRepository extends JpaRepository<ChannelCode, Integer> {
    
    /**
     * 根据渠道码代码查询渠道码
     * @param code 渠道码代码
     * @return 渠道码信息
     */
    Optional<ChannelCode> findByCode(String code);
    
    /**
     * 根据渠道码名称查询渠道码
     * @param name 渠道码名称
     * @return 渠道码列表
     */
    List<ChannelCode> findByNameContaining(String name);
    
    /**
     * 根据状态查询渠道码
     * @param status 状态
     * @return 渠道码列表
     */
    List<ChannelCode> findByStatus(ChannelCode.Status status);
    
    /**
     * 检查渠道码代码是否存在
     * @param code 渠道码代码
     * @return 是否存在
     */
    boolean existsByCode(String code);
}
