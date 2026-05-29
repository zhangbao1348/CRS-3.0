package com.crs.service.inventory;

import java.util.List;

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

    /**
     * 房控日历独立的范围日计算机制
     * 不受订单级超卖熔断限制，无条件返回每日计算扣减明细，保证大盘日历展现的可用数与预订算力模型绝对一致。
     *
     * @param context 可用性查询上下文
     * @return 每日可用库存明细列表
     */
    List<AvailabilityResult.DailyAvailability> checkDailyRangeAvailability(AvailabilityContext context);

    void deductInventory(InventoryDeductionContext context);

    void releaseInventory(InventoryReleaseContext context);
}
