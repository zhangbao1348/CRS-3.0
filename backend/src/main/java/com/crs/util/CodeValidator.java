package com.crs.util;

import java.util.regex.Pattern;

/**
 * 代码字段验证工具类 (CodeValidator)
 * 
 * <p>本类用于统一 CRS 系统中各类实体“编码/代码”(Code) 字段的格式校验逻辑。
 * 系统中如房型代码、价格码、渠道码等关键标识符均需遵循此统一格式标准，以确保数据库存储安全及 API 调用的稳定性。</p>
 * 
 * <p>校验规则：</p>
 * <ul>
 *     <li>只允许包含：大写英文字母 (A-Z)、小写英文字母 (a-z)、数字 (0-9) 以及下划线 (_)。</li>
 *     <li>不允许包含空格、特殊字符或中文字符。</li>
 * </ul>
 */
public class CodeValidator {

    /**
     * 代码匹配的正则表达式。
     * ^[A-Za-z0-9_]+$ 表示从头到尾必须由字母、数字或下划线组成。
     */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    /**
     * 统一的格式校验错误提示信息。
     */
    public static final String ERROR_MESSAGE = "代码只允许英文字母、数字和下划线";

    /**
     * 验证代码字段是否合法。
     * 
     * @param code 待验证的代码字符串
     * @return 如果代码符合正则规则且不为空，则返回 true；否则返回 false
     */
    public static boolean isValid(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        return CODE_PATTERN.matcher(code).matches();
    }

    /**
     * 验证代码字段，如果格式不合法则直接抛出业务异常。
     * 常用于 Controller 层或 Service 层的前置校验。
     * 
     * @param code 待验证的代码字符串
     * @param fieldName 业务字段名称（如 "房型代码"），用于拼接更友好的错误提示
     * @throws IllegalArgumentException 当代码格式非法时抛出此异常
     */
    public static void validate(String code, String fieldName) {
        if (code != null && !code.isEmpty() && !isValid(code)) {
            throw new IllegalArgumentException(fieldName + "格式不正确：" + ERROR_MESSAGE);
        }
    }
}

