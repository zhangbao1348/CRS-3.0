package com.crs.controller;

import com.crs.entity.SystemTraceLog;
import com.crs.repository.SystemTraceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 链路追踪与决策诊断接口控制器 (SystemTraceController)
 * 
 * <p>本类公开相关端点，支持前端异常数据（Error/Promise Rejection）的异步上报，
 * 并提供根据 traceId、reservationCode 检索全链路日志时序详情的方法。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/16-数据及报表.md</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/trace")
public class SystemTraceController {

    @Autowired
    private SystemTraceLogRepository systemTraceLogRepository;

    /**
     * 接收并保存前端异步上报的异常日志
     */
    @PostMapping("/report")
    public ResponseEntity<?> reportFrontendError(@RequestBody Map<String, Object> payload) {
        try {
            SystemTraceLog log = new SystemTraceLog();
            log.setTraceId(getString(payload, "traceId"));
            log.setSourceType("FRONTEND");
            log.setStatus("ERROR");
            log.setOperationName("Frontend.Exception");
            
            String message = getString(payload, "message");
            String filename = getString(payload, "filename");
            Integer lineno = getInteger(payload, "lineno");
            Integer colno = getInteger(payload, "colno");
            String stack = getString(payload, "stack");
            String url = getString(payload, "url");
            
            log.setErrorClass(filename != null && !filename.isBlank() ? filename : "UnknownFile");
            log.setErrorMethod("Line:" + lineno + ", Col:" + colno);
            log.setErrorLine(lineno);
            log.setErrorStack("URL: " + url + "\nMessage: " + message + "\nStack:\n" + stack);
            log.setCreatedAt(new Date());
            log.setRelatedPrdLink(".kiro/specs/prd/16-数据及报表.md");

            // 构建快照数据包存放客户端环境
            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("userAgent", getString(payload, "userAgent"));
            snapshot.put("url", url);
            snapshot.put("clientTime", new Date());
            
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            log.setDecisionSnapshot(mapper.writeValueAsString(snapshot));

            systemTraceLogRepository.save(log);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 查询链路追踪日志列表（支持根据 traceId 或 reservationCode 查询，默认返回最新 200 条）
     */
    @GetMapping("/logs")
    public ResponseEntity<?> getTraceLogs(
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String referenceCode) {
        
        List<SystemTraceLog> logs;
        if (traceId != null && !traceId.isBlank()) {
            logs = systemTraceLogRepository.findByTraceIdOrderByCreatedAtAsc(traceId.trim());
        } else if (referenceCode != null && !referenceCode.isBlank()) {
            logs = systemTraceLogRepository.findByReferenceCodeOrderByCreatedAtAsc(referenceCode.trim());
        } else {
            logs = systemTraceLogRepository.findTop200ByOrderByCreatedAtDesc();
        }
        return ResponseEntity.ok(logs);
    }

    /**
     * 查询单次调用的全链路时序明细
     */
    @GetMapping("/logs/{traceId}")
    public ResponseEntity<?> getTraceDetail(@PathVariable String traceId) {
        List<SystemTraceLog> logs = systemTraceLogRepository.findByTraceIdOrderByCreatedAtAsc(traceId);
        if (logs.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Trace log not found"));
        }
        return ResponseEntity.ok(logs);
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? String.valueOf(val) : null;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number num) {
            return num.intValue();
        }
        if (val instanceof String str) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
