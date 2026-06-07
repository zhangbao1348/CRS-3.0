package com.crs.repository;

import com.crs.entity.SystemTraceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 链路与决策追踪日志持久化层仓库接口 (SystemTraceLogRepository)
 * 
 * <p>本接口提供对 system_trace_logs 数据库表进行关联查询的方法。</p>
 */
@Repository
public interface SystemTraceLogRepository extends JpaRepository<SystemTraceLog, Integer> {

    /**
     * 根据链路 ID 查询追踪日志并按创建时间正序排序，方便按时序还原调用链路
     * 
     * @param traceId 链路追踪 ID
     * @return 追踪日志列表
     */
    List<SystemTraceLog> findByTraceIdOrderByCreatedAtAsc(String traceId);

    /**
     * 根据关联单据号（如 reservationCode）查询追踪日志并按创建时间正序排序
     * 
     * @param referenceCode 关联业务单据号
     * @return 追踪日志列表
     */
    List<SystemTraceLog> findByReferenceCodeOrderByCreatedAtAsc(String referenceCode);

    /**
     * 查询最新的 200 条追踪记录，用于控制台默认呈现
     * 
     * @return 追踪日志列表
     */
    List<SystemTraceLog> findTop200ByOrderByCreatedAtDesc();
}
