package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxSettlementRule {
    PREPAID("prepaid", "预订时预付（平台代收代缴）"),
    CHECKIN("checkin", "入住时前台现付（酒店代扣代缴）"),
    SELF_REPORT("self_report", "酒店按月自行申报缴纳（仅经营者端）");
    
    private final String code;
    private final String label;
    
    TaxSettlementRule(String code, String label) {
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
    
    public static TaxSettlementRule fromCode(String code) {
        for (TaxSettlementRule rule : values()) {
            if (rule.code.equals(code)) {
                return rule;
            }
        }
        throw new IllegalArgumentException("Unknown tax settlement rule code: " + code);
    }
}
