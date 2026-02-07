package com.crs.repository;

import com.crs.entity.GroupRateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 集团房价码仓库接口
 * 提供集团房价码的CRUD操作
 */
@Repository
public interface GroupRateCodeRepository extends JpaRepository<GroupRateCode, Integer>, JpaSpecificationExecutor<GroupRateCode> {
    
    /**
     * 根据集团ID查询集团房价码列表
     * @param groupId 集团ID
     * @return 集团房价码列表
     */
    List<GroupRateCode> findByGroupId(Integer groupId);
    
    /**
     * 根据房价码代码查询集团房价码
     * @param rateCode 房价码代码
     * @return 集团房价码对象
     */
    GroupRateCode findByRateCode(String rateCode);
    
    /**
     * 根据集团ID和状态查询集团房价码列表
     * @param groupId 集团ID
     * @param status 状态
     * @return 集团房价码列表
     */
    List<GroupRateCode> findByGroupIdAndStatus(Integer groupId, String status);
    
    /**
     * 根据房价码代码和集团ID查询集团房价码
     * @param rateCode 房价码代码
     * @param groupId 集团ID
     * @return 集团房价码对象
     */
    GroupRateCode findByRateCodeAndGroupId(String rateCode, Integer groupId);
}
