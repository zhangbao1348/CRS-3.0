package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxDeductible {
    YES("yes", "是"),
    NO("no", "否");
    
    private final String code;
    private final String label;
    
    TaxDeductible(String code, String label) {
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
    
    public static TaxDeductible fromCode(String code) {
        for (TaxDeductible deductible : values()) {
            if (deductible.code.equals(code)) {
                return deductible;
            }
        }
        throw new IllegalArgumentException("Unknown tax deductible code: " + code);
    }
}
