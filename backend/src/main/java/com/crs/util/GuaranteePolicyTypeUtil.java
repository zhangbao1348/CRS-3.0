package com.crs.util;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 担保政策类型编码工具。
 *
 * <p>统一处理担保政策类型的规则化编码、历史中文值兼容及展示标签映射。</p>
 */
public final class GuaranteePolicyTypeUtil {

    public static final String TYPE_NONE = "none";
    public static final String TYPE_CREDIT_CARD = "credit_card";
    public static final String TYPE_PREPAID = "prepaid";
    public static final String TYPE_COMPANY = "company";
    public static final String TYPE_THIRD_PARTY = "third_party";
    public static final String TYPE_SPECIAL = "special";

    private static final Map<String, String> TYPE_MAPPING = Map.ofEntries(
            Map.entry(TYPE_NONE, TYPE_NONE),
            Map.entry("无担保", TYPE_NONE),
            Map.entry(TYPE_CREDIT_CARD, TYPE_CREDIT_CARD),
            Map.entry("信用卡", TYPE_CREDIT_CARD),
            Map.entry("creditcard", TYPE_CREDIT_CARD),
            Map.entry(TYPE_PREPAID, TYPE_PREPAID),
            Map.entry("prepay", TYPE_PREPAID),
            Map.entry("预付", TYPE_PREPAID),
            Map.entry(TYPE_COMPANY, TYPE_COMPANY),
            Map.entry("公司", TYPE_COMPANY),
            Map.entry(TYPE_THIRD_PARTY, TYPE_THIRD_PARTY),
            Map.entry("thirdparty", TYPE_THIRD_PARTY),
            Map.entry("第三方", TYPE_THIRD_PARTY),
            Map.entry(TYPE_SPECIAL, TYPE_SPECIAL),
            Map.entry("特殊", TYPE_SPECIAL)
    );

    private static final Map<String, String> TYPE_LABELS = Map.of(
            TYPE_NONE, "无担保",
            TYPE_CREDIT_CARD, "信用卡",
            TYPE_PREPAID, "预付",
            TYPE_COMPANY, "公司",
            TYPE_THIRD_PARTY, "第三方",
            TYPE_SPECIAL, "特殊"
    );

    private static final Set<String> SUPPORTED_TYPES = Set.of(
            TYPE_NONE,
            TYPE_CREDIT_CARD,
            TYPE_PREPAID,
            TYPE_COMPANY,
            TYPE_THIRD_PARTY,
            TYPE_SPECIAL
    );

    private GuaranteePolicyTypeUtil() {
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
        return TYPE_MAPPING.getOrDefault(lowerCased, TYPE_MAPPING.getOrDefault(trimmed, trimmed));
    }

    public static boolean isSupportedType(String rawType) {
        String normalizedType = normalizeType(rawType);
        return normalizedType != null && SUPPORTED_TYPES.contains(normalizedType);
    }

    public static boolean isCreditCardType(String rawType) {
        return TYPE_CREDIT_CARD.equals(normalizeType(rawType));
    }

    public static boolean isPrepaidType(String rawType) {
        return TYPE_PREPAID.equals(normalizeType(rawType));
    }

    public static String toDisplayLabel(String rawType) {
        String normalizedType = normalizeType(rawType);
        if (normalizedType == null) {
            return "-";
        }
        return TYPE_LABELS.getOrDefault(normalizedType, rawType);
    }
}
