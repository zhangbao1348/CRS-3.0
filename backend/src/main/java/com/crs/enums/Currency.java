package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Currency 枚举类 (Enum)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【Currency】相关的常量定义或切面逻辑。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循项目规范，提供统一的系统枚举或切面增强功能。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public enum Currency {
    PERCENT("%", "%"),
    CNY("CNY", "元"),
    USD("USD", "美元"),
    EUR("EUR", "欧元"),
    GBP("GBP", "英镑"),
    JPY("JPY", "日元");
    
    private final String code;
    private final String label;
    
    Currency(String code, String label) {
        this.code = code;
        this.label = label;
    }
    
    public String getCode() {
        return code;
    }
    
    @JsonValue
    public String getLabel() {
        return label;
    }
    
    public static Currency fromCode(String code) {
        for (Currency currency : values()) {
            if (currency.code.equals(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency code: " + code);
    }
}
