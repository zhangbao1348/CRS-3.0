package com.crs.repository;

import com.crs.entity.GroupRateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GroupRateCodeRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【GroupRateCodeRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/08-集团管理.md</li>
 *     <li>**模块职责**：单一职责原则，提供 GroupRateCodeRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface GroupRateCodeRepository extends JpaRepository<GroupRateCode, Integer>, JpaSpecificationExecutor<GroupRateCode> {

    List<GroupRateCode> findByGroupId(Integer groupId);

    /** @deprecated 存在安全隐患，请改用 findByRateCodeAndGroupId */
    @Deprecated
    GroupRateCode findByRateCode(String rateCode);

    List<GroupRateCode> findByGroupIdAndStatus(Integer groupId, String status);

    GroupRateCode findByRateCodeAndGroupId(String rateCode, Integer groupId);

    List<GroupRateCode> findByGroupIdAndStatusAndDerivativeLevel(Integer groupId, String status, String derivativeLevel);

    List<GroupRateCode> findByGroupIdAndStatusAndRateType(Integer groupId, String status, String rateType);

    long countByMarketCode(String marketCode);

    long countBySourceCode(String sourceCode);

    long countByRateCategory(String rateCategory);

    long countByGuaranteeRule(String guaranteeRule);

    long countByCancellationRule(String cancellationRule);

    @Query("SELECT COUNT(g) FROM GroupRateCode g WHERE g.packages LIKE %:packageCode%")
    long countByPackagesContaining(@Param("packageCode") String packageCode);

    long countByGroupIdAndParentRateCode(Integer groupId, String parentRateCode);

    List<GroupRateCode> findByGroupIdAndDerivativeLevel(Integer groupId, String derivativeLevel);

    List<GroupRateCode> findByGroupIdAndParentRateCode(Integer groupId, String parentRateCode);

    List<GroupRateCode> findByGroupCode(String groupCode);

    List<GroupRateCode> findByGroupCodeAndStatus(String groupCode, String status);

    GroupRateCode findByGroupCodeAndRateCode(String groupCode, String rateCode);

    List<GroupRateCode> findByGroupCodeAndStatusAndDerivativeLevel(String groupCode, String status, String derivativeLevel);

    List<GroupRateCode> findByGroupCodeAndDerivativeLevel(String groupCode, String derivativeLevel);

    long countByGroupCode(String groupCode);
}
