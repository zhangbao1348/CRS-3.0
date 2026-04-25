package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxBaseType {
    NET_EXCLUDING_SERVICE("net_excluding_service", "不含服务费净房价"),
    INCLUDING_SERVICE("including_service", "含服务费房价"),
    ORDER_TOTAL("order_total", "订单总金额"),
    PER_PERSON_ROOM("per_person_room", "按人头/间夜定额"),
    PER_ROOM_NIGHT("per_room_night", "按人头/间夜定额"),
    PROPERTY_ASSESSMENT("property_assessment", "房产评估值");
    
    private final String code;
    private final String label;
    
    TaxBaseType(String code, String label) {
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
    
    public static TaxBaseType fromCode(String code) {
        for (TaxBaseType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown tax base type code: " + code);
    }
}
