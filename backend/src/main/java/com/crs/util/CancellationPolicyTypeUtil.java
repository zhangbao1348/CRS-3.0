package com.crs.util;

import java.util.Locale;
import java.util.Map;

/**
 * 取消政策类型归一化工具。
 *
 * <p>开放接口统一使用规则编码，但当前管理端与历史数据中仍存在中文类型值，
 * 因此在开放接口查询、取消校验等链路上需要兼容两种口径。</p>
 */
public final class CancellationPolicyTypeUtil {

    public static final String TYPE_FREE = "free";
    public static final String TYPE_LIMITED = "limited";
    public static final String TYPE_NON_REFUNDABLE = "non_refundable";
    public static final String TYPE_SPECIAL = "special";

    private static final Map<String, String> TYPE_MAPPING = Map.ofEntries(
            Map.entry(TYPE_FREE, TYPE_FREE),
            Map.entry("freecancel", TYPE_FREE),
            Map.entry("免费取消", TYPE_FREE),
            Map.entry(TYPE_LIMITED, TYPE_LIMITED),
            Map.entry("timed", TYPE_LIMITED),
            Map.entry("advancecancel", TYPE_LIMITED),
            Map.entry("提前取消", TYPE_LIMITED),
            Map.entry("限时扣费", TYPE_LIMITED),
            Map.entry("部分费用", TYPE_LIMITED),
            Map.entry(TYPE_NON_REFUNDABLE, TYPE_NON_REFUNDABLE),
            Map.entry("non-refundable", TYPE_NON_REFUNDABLE),
            Map.entry("nonrefundable", TYPE_NON_REFUNDABLE),
            Map.entry("不可取消", TYPE_NON_REFUNDABLE),
            Map.entry(TYPE_SPECIAL, TYPE_SPECIAL),
            Map.entry("specialcancel", TYPE_SPECIAL),
            Map.entry("特殊取消", TYPE_SPECIAL)
    );

    private CancellationPolicyTypeUtil() {
    }

    public static String normalizeType(String rawType) {
        if (rawType == null) {
            return null;
        }
        String trimmed = rawType.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String lowerCased = trimmed.toLowerCase(Locale.ROOT);
        String compact = lowerCased.replace("_", "").replace("-", "").replace(" ", "");
        if (TYPE_MAPPING.containsKey(lowerCased)) {
            return TYPE_MAPPING.get(lowerCased);
        }
        if (TYPE_MAPPING.containsKey(compact)) {
            return TYPE_MAPPING.get(compact);
        }
        return TYPE_MAPPING.getOrDefault(trimmed, trimmed);
    }

    public static boolean isFreeType(String rawType) {
        return TYPE_FREE.equals(normalizeType(rawType));
    }

    public static boolean isLimitedType(String rawType) {
        return TYPE_LIMITED.equals(normalizeType(rawType));
    }

    public static boolean isNonRefundableType(String rawType) {
        return TYPE_NON_REFUNDABLE.equals(normalizeType(rawType));
    }
}
