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
import com.crs.repository.ApiLogRepository;
import com.crs.repository.ReservationHistoryRepository;
import com.crs.repository.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * ApiLoggingAspect 切面类 (Aspect)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【ApiLoggingAspect】相关的常量定义或切面逻辑。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循项目规范，提供统一的系统枚举或切面增强功能。</li>
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.crs.controller.Open*Controller.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (attributes != null) ? attributes.getRequest() : null;
        
        TenantChannel channel = (request != null) ? (TenantChannel) request.getAttribute("openApiChannel") : null;
        
        String url = (request != null) ? request.getRequestURI() : "UNKNOWN";
        String method = (request != null) ? request.getMethod() : "UNKNOWN";
        String channelCode = (channel != null) ? channel.getChannelCode() : "UNKNOWN";
        
        Object result = null;
        String errorMessage = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            errorMessage = e.getClass().getName() + ": " + e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            saveLog(request, channel, url, method, channelCode, joinPoint.getArgs(), result, errorMessage, duration);
        }
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
