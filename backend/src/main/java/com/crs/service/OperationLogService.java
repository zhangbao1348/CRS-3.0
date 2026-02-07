package com.crs.service;

import com.crs.entity.OperationLog;
import com.crs.repository.OperationLogRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 操作日志服务类
 * 用于处理操作日志的业务逻辑
 */
@Service
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * 记录操作日志
     * @param operator 操作人
     * @param type 操作类型
     * @param action 动作
     * @param details 详细内容
     * @return 创建的操作日志
     */
    public OperationLog recordLog(String operator, String type, String action, String details) {
        OperationLog log = new OperationLog();
        log.setOperator(operator);
        log.setTime(new Date());
        log.setType(type);
        log.setAction(action);
        log.setDetails(details);
        return operationLogRepository.save(log);
    }

    /**
     * 获取所有操作日志
     * @return 操作日志列表
     */
    public List<OperationLog> getAllLogs() {
        return operationLogRepository.findAll();
    }

    /**
     * 根据ID获取操作日志
     * @param id 日志ID
     * @return 操作日志
     */
    public OperationLog getLogById(Integer id) {
        return operationLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operation log not found"));
    }

    /**
     * 根据类型查询操作日志
     * @param type 操作类型
     * @return 操作日志列表
     */
    public List<OperationLog> getLogsByType(String type) {
        return operationLogRepository.findByType(type);
    }

    /**
     * 根据时间范围查询操作日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    public List<OperationLog> getLogsByTimeRange(Date startTime, Date endTime) {
        return operationLogRepository.findByTimeBetween(startTime, endTime);
    }

    /**
     * 根据类型和时间范围查询操作日志
     * @param type 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    public List<OperationLog> getLogsByTypeAndTimeRange(String type, Date startTime, Date endTime) {
        return operationLogRepository.findByTypeAndTimeBetween(type, startTime, endTime);
    }

    /**
     * 根据操作人查询操作日志
     * @param operator 操作人
     * @return 操作日志列表
     */
    public List<OperationLog> getLogsByOperator(String operator) {
        return operationLogRepository.findByOperator(operator);
    }

    /**
     * 根据操作人、类型和时间范围查询操作日志
     * @param operator 操作人
     * @param type 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    public List<OperationLog> getLogsByOperatorAndTypeAndTimeRange(String operator, String type, Date startTime, Date endTime) {
        return operationLogRepository.findByOperatorAndTypeAndTimeBetween(operator, type, startTime, endTime);
    }

    /**
     * 获取最新的操作日志
     * @param limit 限制数量
     * @return 操作日志列表
     */
    public List<OperationLog> getLatestLogs(int limit) {
        return operationLogRepository.findLatestLogs(limit);
    }

    /**
     * 删除操作日志
     * @param id 日志ID
     */
    public void deleteLog(Integer id) {
        operationLogRepository.deleteById(id);
    }

    /**
     * 批量删除操作日志
     * @param ids 日志ID列表
     */
    public void deleteBatchLogs(List<Integer> ids) {
        operationLogRepository.deleteAllById(ids);
    }
}
