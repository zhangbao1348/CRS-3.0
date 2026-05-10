package com.crs.service.inventory;

/**
 * InventoryDeductionService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【InventoryDeductionService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 InventoryDeductionService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public interface InventoryDeductionService {

    AvailabilityResult checkAvailability(AvailabilityContext context);

    void deductInventory(InventoryDeductionContext context);

    void releaseInventory(InventoryReleaseContext context);
}
