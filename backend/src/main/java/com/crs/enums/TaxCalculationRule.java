package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * TaxCalculationRule 枚举类 (Enum)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【TaxCalculationRule】相关的常量定义或切面逻辑。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循项目规范，提供统一的系统枚举或切面增强功能。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public enum TaxCalculationRule {
    INCLUSIVE("inclusive", "价内税（房价已含税）"),
    EXCLUSIVE("exclusive", "价外税（房价不含税，税费额外收取）");
    
    private final String code;
    private final String label;
    
    TaxCalculationRule(String code, String label) {
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
    
    public static TaxCalculationRule fromCode(String code) {
        for (TaxCalculationRule rule : values()) {
            if (rule.code.equals(code)) {
                return rule;
            }
        }
        throw new IllegalArgumentException("Unknown tax calculation rule code: " + code);
    }
}
