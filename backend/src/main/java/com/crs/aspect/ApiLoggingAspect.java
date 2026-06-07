package com.crs.aspect;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.crs.entity.ApiLog;
import com.crs.entity.ReservationHistory;
import com.crs.entity.TenantChannel;
import com.crs.entity.SystemTraceLog;
import com.crs.repository.ApiLogRepository;
import com.crs.repository.ReservationHistoryRepository;
import com.crs.repository.ReservationRepository;
import com.crs.repository.SystemTraceLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * ApiLoggingAspect 切面类 (Aspect)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理全链路追踪日志以及向原有 api_logs 写入接口日志的切面逻辑。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/16-数据及报表.md</li>
 *     <li>**模块职责**：拦截控制器请求，自动生成 traceId 并将正常与异常的业务决策快照持久化。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Aspect
@Component
public class ApiLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingAspect.class);

    @Autowired
    private ApiLogRepository apiLogRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationHistoryRepository reservationHistoryRepository;

    @Autowired
    private SystemTraceLogRepository systemTraceLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.crs.controller..*Controller.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (attributes != null) ? attributes.getRequest() : null;
        
        TenantChannel channel = (request != null) ? (TenantChannel) request.getAttribute("openApiChannel") : null;
        
        String url = (request != null) ? request.getRequestURI() : "UNKNOWN";
        String method = (request != null) ? request.getMethod() : "UNKNOWN";
        String channelCode = (channel != null) ? channel.getChannelCode() : "UNKNOWN";
        
        Object result = null;
        Throwable exception = null;
        String errorMessage = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            errorMessage = e.getClass().getName() + ": " + e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            
            // 兼容性逻辑：仅针对 Open API 的接口调用向原 api_logs 写入基础日志
            if (url != null && url.startsWith("/api/open/")) {
                saveLog(request, channel, url, method, channelCode, joinPoint.getArgs(), result, errorMessage, duration);
            }
            
            // 系统全链路决策追踪日志记录
            saveSystemTraceLog(joinPoint, result, exception, duration);
        }
    }

    private void saveSystemTraceLog(ProceedingJoinPoint joinPoint, Object response, Throwable exception, long duration) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (attributes != null) ? attributes.getRequest() : null;

            String url = (request != null) ? request.getRequestURI() : "UNKNOWN";
            String method = (request != null) ? request.getMethod() : "UNKNOWN";
            
            SystemTraceLog traceLog = new SystemTraceLog();
            traceLog.setTraceId(com.crs.util.TraceContext.getTraceId());
            traceLog.setCreatedAt(new Date());

            // 设置来源类型
            if (url != null && url.startsWith("/api/open/")) {
                traceLog.setSourceType("OPEN_API");
            } else {
                traceLog.setSourceType("INTERNAL_API");
            }

            // 设置操作名称 (ClassName.methodName)
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            traceLog.setOperationName(className + "." + methodName);

            // 获取中间业务逻辑中收集的所有决策数据
            Map<String, Object> decisions = com.crs.util.TraceContext.getDecisions();
            
            // 自动提取关联单据号 (例如 reservationCode)
            String refCode = resolveReferenceCode(request, joinPoint.getArgs(), response, decisions);
            traceLog.setReferenceCode(refCode);

            // 处理状态与异常诊断信息
            if (exception != null) {
                traceLog.setStatus("ERROR");
                traceLog.setErrorClass(exception.getClass().getName());
                traceLog.setErrorMethod(methodName);
                
                StackTraceElement[] stack = exception.getStackTrace();
                if (stack != null && stack.length > 0) {
                    for (StackTraceElement ste : stack) {
                        if (ste.getClassName().startsWith("com.crs.")) {
                            traceLog.setErrorClass(ste.getClassName());
                            traceLog.setErrorMethod(ste.getMethodName());
                            traceLog.setErrorLine(ste.getLineNumber());
                            break;
                        }
                    }
                }
                
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                exception.printStackTrace(pw);
                traceLog.setErrorStack(sw.toString());
            } else {
                // 若返回 ResponseEntity，根据其 HTTP 状态码来判断是否被规则拦截 (如 409 Mismatch)
                boolean isBlocked = false;
                if (response instanceof ResponseEntity<?> entity) {
                    int statusCode = entity.getStatusCode().value();
                    if (statusCode == 409) {
                        isBlocked = true;
                    }
                }
                
                if (isBlocked) {
                    traceLog.setStatus("BLOCKED_BY_RULE");
                } else {
                    traceLog.setStatus("SUCCESS");
                }
            }

            // 自动匹配关联的 PRD 路径
            traceLog.setRelatedPrdLink(resolvePrdLink(className, url));

            // 封装完整的决策快照数据包 (DecisionSnapshot)
            Map<String, Object> finalSnapshot = new HashMap<>();
            finalSnapshot.put("decisions", decisions);
            finalSnapshot.put("duration", duration + "ms");
            finalSnapshot.put("url", url);
            finalSnapshot.put("method", method);
            finalSnapshot.put("args", sanitizeArgs(joinPoint.getArgs()));
            if (response != null) {
                finalSnapshot.put("response", response);
            }
            
            traceLog.setDecisionSnapshot(objectMapper.writeValueAsString(finalSnapshot));
            
            systemTraceLogRepository.save(traceLog);
        } catch (Exception e) {
            logger.error("Failed to save system trace log", e);
        }
    }

    private String resolveReferenceCode(HttpServletRequest request, Object[] args, Object response, Map<String, Object> decisions) {
        if (decisions != null && decisions.containsKey("reservationCode")) {
            return String.valueOf(decisions.get("reservationCode"));
        }
        
        if (request != null) {
            String uri = request.getRequestURI();
            if (uri != null) {
                if (uri.startsWith("/api/open/reservations/")) {
                    String[] parts = uri.split("/");
                    if (parts.length >= 5) {
                        String code = parts[4];
                        if (code != null && !code.equals("pay") && !code.equals("cancel")) {
                            return code;
                        }
                    }
                }
                if (uri.startsWith("/api/reservations/")) {
                    String[] parts = uri.split("/");
                    if (parts.length >= 4) {
                        String code = parts[3];
                        if (code != null && !code.equals("pay") && !code.equals("cancel")) {
                            return code;
                        }
                    }
                }
            }
        }
        
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof Map<?, ?> map) {
                    if (map.containsKey("reservationCode")) {
                        return String.valueOf(map.get("reservationCode"));
                    }
                }
            }
        }
        
        if (response != null) {
            Object body = response instanceof ResponseEntity<?> entity ? entity.getBody() : response;
            if (body instanceof Map<?, ?> responseMap) {
                if (responseMap.containsKey("reservationCode")) {
                    return String.valueOf(responseMap.get("reservationCode"));
                }
                Object data = responseMap.get("data");
                if (data instanceof Map<?, ?> dataMap) {
                    if (dataMap.containsKey("reservationCode")) {
                        return String.valueOf(dataMap.get("reservationCode"));
                    }
                }
            }
        }
        
        return null;
    }

    private String resolvePrdLink(String className, String url) {
        if (url == null) url = "";
        if (className == null) className = "";
        
        if (className.contains("Reservation") || url.contains("reservation")) {
            return ".kiro/specs/prd/14-订单管理.md";
        }
        if (className.contains("Price") || url.contains("price") || url.contains("rate")) {
            return ".kiro/specs/prd/10-价格计划管理.md";
        }
        if (className.contains("Inventory") || className.contains("RoomStatus") || url.contains("inventory") || url.contains("room-status")) {
            return ".kiro/specs/prd/11-库存管理.md";
        }
        if (className.contains("Channel") || url.contains("channel")) {
            return ".kiro/specs/prd/13-渠道管理.md";
        }
        return ".kiro/specs/prd/00-SOW-功能清单.md";
    }

    private void saveLog(
            HttpServletRequest request,
            TenantChannel channel,
            String url,
            String method,
            String channelCode,
            Object[] args,
            Object response,
            String error,
            long duration) {
        try {
            ApiLog log = new ApiLog();
            Integer reservationId = resolveReservationId(request, channel, response);
            
            // 封装请求元数据
            Map<String, Object> meta = new HashMap<>();
            meta.put("url", url);
            meta.put("method", method);
            meta.put("channel", channelCode);
            meta.put("duration", duration + "ms");
            meta.put("args", sanitizeArgs(args));
            
            log.setRequestBody(objectMapper.writeValueAsString(meta));
            log.setReservationId(reservationId);
            
            if (response != null) {
                log.setResponseBody(objectMapper.writeValueAsString(response));
            }
            
            log.setErrorMessage(error);
            log.setCreatedAt(new Date());
            
            ApiLog savedLog = apiLogRepository.save(log);
            linkReservationHistory(request, reservationId, savedLog.getId());
        } catch (Exception e) {
            logger.error("Failed to save API log", e);
        }
    }

    private Integer resolveReservationId(HttpServletRequest request, TenantChannel channel, Object response) {
        Integer reservationId = extractReservationIdFromResponse(response);
        if (reservationId != null) {
            return reservationId;
        }
        if (request == null || channel == null || request.getRequestURI() == null) {
            return null;
        }
        String reservationCode = extractReservationCodeFromRequestUri(request.getRequestURI());
        if (reservationCode == null || reservationCode.isBlank()) {
            return null;
        }
        return reservationRepository.findByTenantIdAndReservationCode(channel.getTenantId(), reservationCode)
                .map(reservation -> reservation.getId())
                .orElse(null);
    }

    private Integer extractReservationIdFromResponse(Object response) {
        Object body = response instanceof ResponseEntity<?> entity ? entity.getBody() : response;
        if (!(body instanceof Map<?, ?> responseMap)) {
            return null;
        }
        Integer directReservationId = toInteger(responseMap.get("reservationId"));
        if (directReservationId != null) {
            return directReservationId;
        }
        Object data = responseMap.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            return toInteger(dataMap.get("reservationId"));
        }
        return null;
    }

    private Integer toInteger(Object value) {
        if (value instanceof Integer integerValue) {
            return integerValue;
        }
        if (value instanceof Number numberValue) {
            return numberValue.intValue();
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String extractReservationCodeFromRequestUri(String uri) {
        if (uri == null || !uri.startsWith("/api/open/reservations/")) {
            return null;
        }
        String[] parts = uri.split("/");
        if (parts.length < 5) {
            return null;
        }
        return parts[4];
    }

    private void linkReservationHistory(HttpServletRequest request, Integer reservationId, Integer logId) {
        if (request == null || reservationId == null || logId == null) {
            return;
        }
        String action = resolveHistoryAction(request.getRequestURI(), request.getMethod());
        if (action == null) {
            return;
        }
        reservationHistoryRepository
                .findFirstByReservationIdAndActionOrderByOperationTimeDesc(reservationId, action)
                .ifPresent(history -> updateHistoryLogId(history, logId));
    }

    private String resolveHistoryAction(String uri, String method) {
        if ("POST".equalsIgnoreCase(method) && "/api/open/reservations".equals(uri)) {
            return "CREATE";
        }
        if ("POST".equalsIgnoreCase(method)
                && uri != null
                && uri.startsWith("/api/open/reservations/")
                && uri.endsWith("/cancel")) {
            return "CANCEL";
        }
        return null;
    }

    private void updateHistoryLogId(ReservationHistory history, Integer logId) {
        if (history == null || history.getLogId() != null) {
            return;
        }
        history.setLogId(logId);
        reservationHistoryRepository.save(history);
    }

    private List<Object> sanitizeArgs(Object[] args) {
        List<Object> sanitizedArgs = new ArrayList<>();
        if (args == null || args.length == 0) {
            return sanitizedArgs;
        }
        for (Object arg : args) {
            Object sanitizedValue = sanitizeValue(arg);
            if (sanitizedValue != null) {
                sanitizedArgs.add(sanitizedValue);
            }
        }
        return sanitizedArgs;
    }

    private Object sanitizeValue(Object value) {
        if (value == null) {
            return null;
        }
        if (isInfrastructureObject(value)) {
            return null;
        }
        if (value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof Enum<?>
                || value instanceof Date) {
            return value;
        }
        if (value instanceof Map<?, ?> mapValue) {
            Map<String, Object> sanitizedMap = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                Object sanitizedEntryValue = sanitizeValue(entry.getValue());
                if (sanitizedEntryValue != null) {
                    sanitizedMap.put(String.valueOf(entry.getKey()), sanitizedEntryValue);
                }
            }
            return sanitizedMap;
        }
        if (value instanceof Collection<?> collectionValue) {
            List<Object> sanitizedList = new ArrayList<>();
            for (Object item : collectionValue) {
                Object sanitizedItem = sanitizeValue(item);
                if (sanitizedItem != null) {
                    sanitizedList.add(sanitizedItem);
                }
            }
            return sanitizedList;
        }
        if (value.getClass().isArray()) {
            List<Object> sanitizedList = new ArrayList<>();
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object sanitizedItem = sanitizeValue(Array.get(value, i));
                if (sanitizedItem != null) {
                    sanitizedList.add(sanitizedItem);
                }
            }
            return sanitizedList;
        }
        try {
            return objectMapper.convertValue(value, Object.class);
        } catch (IllegalArgumentException ignored) {
            return String.valueOf(value);
        }
    }

    private boolean isInfrastructureObject(Object value) {
        return value instanceof ServletRequest
                || value instanceof ServletResponse
                || value instanceof MultipartFile
                || value instanceof BindingResult;
    }
}
