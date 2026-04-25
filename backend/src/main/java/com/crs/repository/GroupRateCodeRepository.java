package com.crs.repository;

import com.crs.entity.GroupRateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    /**
     * 根据集团ID、状态和衍生层级查询集团房价码列表
     * @param groupId 集团ID
     * @param status 状态
     * @param derivativeLevel 衍生层级
     * @return 集团房价码列表
     */
    List<GroupRateCode> findByGroupIdAndStatusAndDerivativeLevel(Integer groupId, String status, String derivativeLevel);
    
    /**
     * 根据集团ID、状态和房价类型查询集团房价码列表
     * @param groupId 集团ID
     * @param status 状态
     * @param rateType 房价类型
     * @return 集团房价码列表
     */
    List<GroupRateCode> findByGroupIdAndStatusAndRateType(Integer groupId, String status, String rateType);

    /**
     * 统计引用指定市场码ID的房价码数量
     */
    long countByMarketCodeId(Integer marketCodeId);

    /**
     * 统计引用指定来源码ID的房价码数量
     */
    long countBySourceCodeId(Integer sourceCodeId);

    /**
     * 统计引用指定房价大类代码的房价码数量
     */
    long countByRateCategory(String rateCategory);

    /**
     * 统计引用指定担保规则代码的房价码数量
     */
    long countByGuaranteeRule(String guaranteeRule);

    /**
     * 统计引用指定取消规则代码的房价码数量
     */
    long countByCancellationRule(String cancellationRule);

    /**
     * 统计packages JSON字段中包含指定包价代码的房价码数量
     */
    @Query("SELECT COUNT(g) FROM GroupRateCode g WHERE g.packages LIKE %:packageCode%")
    long countByPackagesContaining(@Param("packageCode") String packageCode);

    /**
     * 根据父级房价码ID统计子衍生码数量
     * @param parentRateCodeId 父级房价码ID
     * @return 子衍生码数量
     */
    long countByParentRateCodeId(Integer parentRateCodeId);

    /**
     * 根据集团ID和衍生层级查询集团房价码列表
     * @param groupId 集团ID
     * @param derivativeLevel 衍生层级
     * @return 集团房价码列表
     */
    List<GroupRateCode> findByGroupIdAndDerivativeLevel(Integer groupId, String derivativeLevel);

    /**
     * 根据父级房价码ID查询子衍生码列表
     * @param parentRateCodeId 父级房价码ID
     * @return 子衍生码列表
     */
    List<GroupRateCode> findByParentRateCodeId(Integer parentRateCodeId);
}
