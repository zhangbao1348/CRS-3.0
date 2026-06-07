package com.crs.util;

import org.slf4j.MDC;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 全系统链路追踪上下文工具类 (TraceContext)
 * 
 * <p>本类负责管理和传递当前请求的链路追踪标识（traceId）以及在处理过程中记录的所有业务决策快照（DecisionSnapshot）。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *     <li>在 MDC 中存入 traceId，自动集成到 SLF4J 的标准日志输出中。</li>
 *     <li>利用 ThreadLocal 变量存储请求生命周期内产生的决策快照，供日志切面或拦截器在请求结束时统一落库。</li>
 *     <li>支持在异步线程或定时任务中手动传递追踪标识，保证日志的可串联性。</li>
 * </ul>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/16-数据及报表.md</li>
 * </ul>
 */
public class TraceContext {

    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 线程局部变量，用于在业务执行过程中动态收集决策快照。
     * 使用 LinkedHashMap 保持记录的插入顺序。
     */
    private static final ThreadLocal<Map<String, Object>> DECISIONS_HOLDER = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * 设置链路追踪唯一 ID。
     * 如果传入为空，则自动生成一个 32 位的无连字符 UUID。
     * 
     * @param traceId 外部传入的追踪 ID，可为 null
     */
    public static void setTraceId(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * 获取当前线程的链路追踪唯一 ID。
     * 如果当前线程尚未绑定追踪 ID，则会自动生成一个并绑定，确保 traceId 始终存在。
     * 
     * @return 当前的 traceId
     */
    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
            MDC.put(TRACE_ID_KEY, traceId);
        }
        return traceId;
    }

    /**
     * 记录一步业务决策快照（例如价格计算步骤、保底价触发、库存比对细节）。
     * 
     * @param key 决策点的标识名称，例如 "priceDerivation"
     * @param value 决策点的参数、中间值或结果对象，将被序列化为 JSON 存储
     */
    public static void recordDecision(String key, Object value) {
        DECISIONS_HOLDER.get().put(key, value);
    }

    /**
     * 获取当前线程内已收集的所有决策快照副本。
     * 
     * @return 决策快照 Map
     */
    public static Map<String, Object> getDecisions() {
        return new LinkedHashMap<>(DECISIONS_HOLDER.get());
    }

    /**
     * 清理当前线程的追踪标识和决策快照。
     * 必须在请求完成时（例如 Interceptor 里的 afterCompletion）显式调用，防止线程池场景下造成信息污染和内存泄漏。
     */
    public static void clear() {
        MDC.remove(TRACE_ID_KEY);
        DECISIONS_HOLDER.remove();
    }
}
