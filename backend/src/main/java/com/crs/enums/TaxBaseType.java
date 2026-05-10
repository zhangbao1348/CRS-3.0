package com.crs.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * TaxBaseType 枚举类 (Enum)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【TaxBaseType】相关的常量定义或切面逻辑。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循项目规范，提供统一的系统枚举或切面增强功能。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
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
