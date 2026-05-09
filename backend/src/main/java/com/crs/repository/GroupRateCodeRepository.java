package com.crs.repository;

import com.crs.entity.GroupRateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRateCodeRepository extends JpaRepository<GroupRateCode, Integer>, JpaSpecificationExecutor<GroupRateCode> {

    List<GroupRateCode> findByGroupId(Integer groupId);

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

    long countByParentRateCode(String parentRateCode);

    List<GroupRateCode> findByGroupIdAndDerivativeLevel(Integer groupId, String derivativeLevel);

    List<GroupRateCode> findByParentRateCode(String parentRateCode);

    List<GroupRateCode> findByGroupCode(String groupCode);

    List<GroupRateCode> findByGroupCodeAndStatus(String groupCode, String status);

    GroupRateCode findByGroupCodeAndRateCode(String groupCode, String rateCode);

    List<GroupRateCode> findByGroupCodeAndStatusAndDerivativeLevel(String groupCode, String status, String derivativeLevel);

    List<GroupRateCode> findByGroupCodeAndDerivativeLevel(String groupCode, String derivativeLevel);

    long countByGroupCode(String groupCode);
}
