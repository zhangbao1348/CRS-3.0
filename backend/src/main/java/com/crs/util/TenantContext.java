package com.crs.util;

/**
 * 租户上下文工具类 (TenantContext)
 * 
 * <p>本类是 CRS 系统多租户架构的核心组件之一。它利用 ThreadLocal 机制在单个请求的生命周期内
 * 安全地存储和传递当前登录用户的租户 ID (tenantId)。</p>
 * 
 * <p>主要用途：</p>
 * <ul>
 *     <li>在拦截器 (Interceptor) 中解析请求头或 Token 后设置租户 ID。</li>
 *     <li>在 Service 层或 Repository 层获取当前租户 ID，用于实现数据的自动隔离。</li>
 *     <li>在请求结束时由拦截器调用 clear() 方法，防止线程池场景下的内存泄漏。</li>
 * </ul>
 */
public class TenantContext {

    /**
     * 线程局部变量存储桶，用于保存当前线程关联的租户 ID。
     * Integer 类型，支持 null 值（代表超级管理员或未指定租户）。
     */
    private static final ThreadLocal<Integer> TENANT_ID_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前线程的租户 ID。
     * 通常在身份验证拦截器中，从 JWT 载荷或请求头 X-Tenant-Id 中提取并存入。
     * 
     * @param tenantId 租户唯一标识符
     */
    public static void setTenantId(Integer tenantId) {
        TENANT_ID_HOLDER.set(tenantId);
    }

    /**
     * 获取当前线程绑定的租户 ID。
     * 业务逻辑代码应始终通过此方法而非硬编码来获取租户上下文。
     * 
     * @return 当前租户 ID，如果没有设置则返回 null
     */
    public static Integer getTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    /**
     * 清除当前线程存储的租户 ID。
     * 必须在 HTTP 请求处理完成后调用（通常在 Interceptor 的 afterCompletion 方法中），
     * 以确保线程返回线程池后不会携带旧的租户信息，同时避免内存泄漏。
     */
    public static void clear() {
        TENANT_ID_HOLDER.remove();
    }

    /**
     * 判断当前上下文中是否已设置有效的租户 ID。
     * 
     * @return 如果租户 ID 不为 null 则返回 true
     */
    public static boolean hasTenantId() {
        return TENANT_ID_HOLDER.get() != null;
    }
}

