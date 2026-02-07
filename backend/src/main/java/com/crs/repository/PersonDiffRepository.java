package com.crs.repository;

import com.crs.entity.PersonDiff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

/**
 * 人数差价仓库接口
 * 用于人数差价数据的CRUD操作
 */
@Repository
public interface PersonDiffRepository extends JpaRepository<PersonDiff, Integer> {
    
    /**
     * 根据差价体系ID查询人数差价列表
     * @param systemId 差价体系ID
     * @return 人数差价列表
     */
    List<PersonDiff> findBySystemId(Integer systemId);
    
    /**
     * 根据差价体系ID和状态查询人数差价列表
     * @param systemId 差价体系ID
     * @param status 状态
     * @return 人数差价列表
     */
    List<PersonDiff> findBySystemIdAndStatus(Integer systemId, PersonDiff.Status status);
    
    /**
     * 根据差价体系ID和人数类型查询人数差价
     * @param systemId 差价体系ID
     * @param personType 人数类型
     * @return 人数差价信息
     */
    List<PersonDiff> findBySystemIdAndPersonType(Integer systemId, String personType);
    
    /**
     * 根据日期范围查询人数差价
     * @param systemId 差价体系ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 人数差价列表
     */
    List<PersonDiff> findBySystemIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Integer systemId, Date endDate, Date startDate);
    
    /**
     * 根据状态查询人数差价
     * @param status 状态
     * @return 人数差价列表
     */
    List<PersonDiff> findByStatus(PersonDiff.Status status);
}
