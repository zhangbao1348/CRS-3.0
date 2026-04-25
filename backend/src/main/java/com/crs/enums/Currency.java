package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

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
