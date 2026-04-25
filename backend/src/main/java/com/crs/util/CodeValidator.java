package com.crs.util;

import java.util.regex.Pattern;

/**
 * 代码字段验证工具类
 * 所有"代码"类字段统一使用此工具进行格式校验
 * 规则：只允许英文字母、数字和下划线
 */
public class CodeValidator {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    public static final String ERROR_MESSAGE = "代码只允许英文字母、数字和下划线";

    /**
     * 验证代码字段是否合法
     * @param code 待验证的代码
     * @return true=合法, false=不合法
     */
    public static boolean isValid(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        return CODE_PATTERN.matcher(code).matches();
    }

    /**
     * 验证代码字段，不合法时抛出 IllegalArgumentException
     * @param code 待验证的代码
     * @param fieldName 字段名称（用于错误提示）
     */
    public static void validate(String code, String fieldName) {
        if (code != null && !code.isEmpty() && !isValid(code)) {
            throw new IllegalArgumentException(fieldName + ERROR_MESSAGE);
        }
    }
}
