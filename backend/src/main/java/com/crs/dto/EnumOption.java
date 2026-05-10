package com.crs.dto;

/**
 * EnumOption 数据传输对象 (DTO)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【EnumOption】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 EnumOption 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public class EnumOption {
    private String value;
    private String label;
    
    public EnumOption() {
    }
    
    public EnumOption(String value, String label) {
        this.value = value;
        this.label = label;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
}
