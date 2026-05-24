package com.crs.repository;

import com.crs.entity.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 接口日志仓库接口
 * 用于接口日志数据的CRUD操作
 */
@Repository
public interface ApiLogRepository extends JpaRepository<ApiLog, Integer> {
    
    /**
     * 根据预订ID查询接口日志
     * @param reservationId 预订ID
     * @return 接口日志列表
     */
    List<ApiLog> findByReservationId(Integer reservationId);

    /**
     * 根据预订ID查询接口日志，按创建时间倒序
     * @param reservationId 预订ID
     * @return 接口日志列表
     */
    List<ApiLog> findByReservationIdOrderByCreatedAtDesc(Integer reservationId);
}
