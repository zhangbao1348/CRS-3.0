package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CommonStatus {
    ACTIVE("active", "启用"),
    INACTIVE("inactive", "停用");
    
    private final String code;
    private final String label;
    
    CommonStatus(String code, String label) {
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
    
    public static CommonStatus fromCode(String code) {
        for (CommonStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown common status code: " + code);
    }
}
