package com.crs.controller;

import com.crs.dto.EnumOption;
import com.crs.enums.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EnumController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【EnumController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 EnumController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/enums")
@CrossOrigin(origins = "*")
public class EnumController {
    
    @GetMapping("/tax-bearer")
    public List<EnumOption> getTaxBearerOptions() {
        List<EnumOption> options = new ArrayList<>();
        for (TaxBearer bearer : TaxBearer.values()) {
            options.add(new EnumOption(bearer.getCode(), bearer.getLabel()));
        }
        return options;
    }
    
    @GetMapping("/tax-base-type")
    public List<EnumOption> getTaxBaseTypeOptions() {
        List<EnumOption> options = new ArrayList<>();
        for (TaxBaseType type : TaxBaseType.values()) {
            options.add(new EnumOption(type.getCode(), type.getLabel()));
        }
        return options;
    }
    
    @GetMapping("/tax-calculation-rule")
    public List<EnumOption> getTaxCalculationRuleOptions() {
        List<EnumOption> options = new ArrayList<>();
        for (TaxCalculationRule rule : TaxCalculationRule.values()) {
            options.add(new EnumOption(rule.getCode(), rule.getLabel()));
        }
        return options;
    }
    
    @GetMapping("/tax-deductible")
    public List<EnumOption> getTaxDeductibleOptions() {
        List<EnumOption> options = new ArrayList<>();
        for (TaxDeductible deductible : TaxDeductible.values()) {
            options.add(new EnumOption(deductible.getCode(), deductible.getLabel()));
        }
        return options;
    }
    
    @GetMapping("/tax-refundable")
    public List<EnumOption> getTaxRefundableOptions() {
        List<EnumOption> options = new ArrayList<>();
        for (TaxRefundable refundable : TaxRefundable.values()) {
            options.add(new EnumOption(refundable.getCode(), refundable.getLabel()));
        }
        return options;
    }
    
    @GetMapping("/tax-settlement-rule")
    public List<EnumOption> getTaxSettlementRuleOptions() {
        List<EnumOption> options = new ArrayList<>();
        for (TaxSettlementRule rule : TaxSettlementRule.values()) {
            options.add(new EnumOption(rule.getCode(), rule.getLabel()));
        }
        return options;
    }
    
    @GetMapping("/common-status")
    public List<EnumOption> getCommonStatusOptions() {
        List<EnumOption> options = new ArrayList<>();
        for (CommonStatus status : CommonStatus.values()) {
            options.add(new EnumOption(status.getCode(), status.getLabel()));
        }
        return options;
    }
    
    @GetMapping("/currency")
    public List<EnumOption> getCurrencyOptions() {
        List<EnumOption> options = new ArrayList<>();
        for (Currency currency : Currency.values()) {
            options.add(new EnumOption(currency.getCode(), currency.getLabel()));
        }
        return options;
    }
    
    @GetMapping("/all")
    public Map<String, List<EnumOption>> getAllEnums() {
        Map<String, List<EnumOption>> allEnums = new HashMap<>();
        allEnums.put("taxBearer", getTaxBearerOptions());
        allEnums.put("taxBaseType", getTaxBaseTypeOptions());
        allEnums.put("taxCalculationRule", getTaxCalculationRuleOptions());
        allEnums.put("taxDeductible", getTaxDeductibleOptions());
        allEnums.put("taxRefundable", getTaxRefundableOptions());
        allEnums.put("taxSettlementRule", getTaxSettlementRuleOptions());
        allEnums.put("commonStatus", getCommonStatusOptions());
        allEnums.put("currency", getCurrencyOptions());
        return allEnums;
    }
}
