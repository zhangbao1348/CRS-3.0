package com.crs.aspect;

import com.crs.entity.ApiLog;
import com.crs.entity.TenantChannel;
import com.crs.repository.ApiLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class ApiLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingAspect.class);

    @Autowired
    private ApiLogRepository apiLogRepository;

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
            saveLog(url, method, channelCode, joinPoint.getArgs(), result, errorMessage, duration);
        }
    }

    private void saveLog(String url, String method, String channelCode, Object[] args, Object response, String error, long duration) {
        try {
            ApiLog log = new ApiLog();
            
            // 封装请求元数据
            Map<String, Object> meta = new HashMap<>();
            meta.put("url", url);
            meta.put("method", method);
            meta.put("channel", channelCode);
            meta.put("duration", duration + "ms");
            meta.put("args", args);
            
            log.setRequestBody(objectMapper.writeValueAsString(meta));
            
            if (response != null) {
                log.setResponseBody(objectMapper.writeValueAsString(response));
            }
            
            log.setErrorMessage(error);
            log.setCreatedAt(new Date());
            
            apiLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Failed to save API log", e);
        }
    }
}
