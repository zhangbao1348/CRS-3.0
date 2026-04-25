package com.crs.util;

/**
 * 租户上下文工具类
 * 用于在当前请求线程中存储和获取当前租户信息
 */
public class TenantContext {

    private static final ThreadLocal<Integer> TENANT_ID_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前租户ID
     */
    public static void setTenantId(Integer tenantId) {
        TENANT_ID_HOLDER.set(tenantId);
    }

    /**
     * 获取当前租户ID
     */
    public static Integer getTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    /**
     * 清除当前租户ID
     */
    public static void clear() {
        TENANT_ID_HOLDER.remove();
    }

    /**
     * 检查是否有租户ID
     */
    public static boolean hasTenantId() {
        return TENANT_ID_HOLDER.get() != null;
    }
}
