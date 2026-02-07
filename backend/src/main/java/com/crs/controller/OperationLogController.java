package com.crs.controller;

import com.crs.entity.OperationLog;
import com.crs.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 操作日志控制器
 * 提供操作日志管理的REST API端点
 */
@RestController
@RequestMapping("/api/operation-logs")
@CrossOrigin(origins = "*")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 获取所有操作日志
     * @return 操作日志列表
     */
    @GetMapping
    public ResponseEntity<List<OperationLog>> getAllOperationLogs() {
        List<OperationLog> logs = operationLogService.getAllLogs();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * 根据ID获取操作日志
     * @param id 日志ID
     * @return 操作日志详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<OperationLog> getOperationLogById(@PathVariable Integer id) {
        try {
            OperationLog log = operationLogService.getLogById(id);
            return new ResponseEntity<>(log, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据类型查询操作日志
     * @param type 操作类型
     * @return 操作日志列表
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<OperationLog>> getOperationLogsByType(@PathVariable String type) {
        List<OperationLog> logs = operationLogService.getLogsByType(type);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * 根据时间范围查询操作日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<OperationLog>> getOperationLogsByTimeRange(
            @RequestParam Date startTime,
            @RequestParam Date endTime) {
        List<OperationLog> logs = operationLogService.getLogsByTimeRange(startTime, endTime);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * 根据类型和时间范围查询操作日志
     * @param type 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    @GetMapping("/type-time-range")
    public ResponseEntity<List<OperationLog>> getOperationLogsByTypeAndTimeRange(
            @RequestParam String type,
            @RequestParam Date startTime,
            @RequestParam Date endTime) {
        List<OperationLog> logs = operationLogService.getLogsByTypeAndTimeRange(type, startTime, endTime);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * 根据操作人查询操作日志
     * @param operator 操作人
     * @return 操作日志列表
     */
    @GetMapping("/operator/{operator}")
    public ResponseEntity<List<OperationLog>> getOperationLogsByOperator(@PathVariable String operator) {
        List<OperationLog> logs = operationLogService.getLogsByOperator(operator);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * 获取最新的操作日志
     * @param limit 限制数量
     * @return 操作日志列表
     */
    @GetMapping("/latest")
    public ResponseEntity<List<OperationLog>> getLatestOperationLogs(@RequestParam(defaultValue = "50") int limit) {
        List<OperationLog> logs = operationLogService.getLatestLogs(limit);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * 删除操作日志
     * @param id 日志ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperationLog(@PathVariable Integer id) {
        try {
            operationLogService.deleteLog(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 批量删除操作日志
     * @param ids 日志ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteBatchOperationLogs(@RequestBody List<Integer> ids) {
        operationLogService.deleteBatchLogs(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
