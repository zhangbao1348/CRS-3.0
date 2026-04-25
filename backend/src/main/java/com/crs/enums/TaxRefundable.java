package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxRefundable {
    FULL("full", "全额可退"),
    PARTIAL("partial", "部分可退"),
    NONE("none", "不可退");
    
    private final String code;
    private final String label;
    
    TaxRefundable(String code, String label) {
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
    
    public static TaxRefundable fromCode(String code) {
        for (TaxRefundable refundable : values()) {
            if (refundable.code.equals(code)) {
                return refundable;
            }
        }
        throw new IllegalArgumentException("Unknown tax refundable code: " + code);
    }
}
