package com.crs.repository;

import com.crs.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * 操作日志仓库接口
 * 用于操作日志的数据库操作
 */
public interface OperationLogRepository extends JpaRepository<OperationLog, Integer> {

    /**
     * 根据类型查询操作日志
     * @param type 操作类型
     * @return 操作日志列表
     */
    List<OperationLog> findByType(String type);

    /**
     * 根据时间范围查询操作日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    List<OperationLog> findByTimeBetween(Date startTime, Date endTime);

    /**
     * 根据类型和时间范围查询操作日志
     * @param type 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    List<OperationLog> findByTypeAndTimeBetween(String type, Date startTime, Date endTime);

    /**
     * 根据操作人查询操作日志
     * @param operator 操作人
     * @return 操作日志列表
     */
    List<OperationLog> findByOperator(String operator);

    /**
     * 根据操作人、类型和时间范围查询操作日志
     * @param operator 操作人
     * @param type 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    List<OperationLog> findByOperatorAndTypeAndTimeBetween(String operator, String type, Date startTime, Date endTime);

    /**
     * 查询最新的操作日志
     * @param limit 限制数量
     * @return 操作日志列表
     */
    @Query(value = "SELECT * FROM operation_logs ORDER BY time DESC LIMIT :limit", nativeQuery = true)
    List<OperationLog> findLatestLogs(@Param("limit") int limit);
}
