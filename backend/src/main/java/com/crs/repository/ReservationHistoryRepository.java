package com.crs.repository;

import com.crs.entity.ReservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预订操作历史仓库接口
 * 用于预订操作历史数据的CRUD操作
 */
@Repository
public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Integer> {
    
    /**
     * 根据预订ID查询操作历史，按操作时间倒序
     * @param reservationId 预订ID
     * @return 操作历史列表
     */
    List<ReservationHistory> findByReservationIdOrderByOperationTimeDesc(Integer reservationId);
}
