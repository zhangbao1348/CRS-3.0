package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

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
