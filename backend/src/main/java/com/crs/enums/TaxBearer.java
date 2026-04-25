package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxBearer {
    GUEST("guest", "客人端承担"),
    HOTEL("hotel", "酒店经营者端承担");
    
    private final String code;
    private final String label;
    
    TaxBearer(String code, String label) {
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
    
    public static TaxBearer fromCode(String code) {
        for (TaxBearer bearer : values()) {
            if (bearer.code.equals(code)) {
                return bearer;
            }
        }
        throw new IllegalArgumentException("Unknown tax bearer code: " + code);
    }
}
