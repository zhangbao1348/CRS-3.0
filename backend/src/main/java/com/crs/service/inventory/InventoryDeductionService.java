package com.crs.service.inventory;

public interface InventoryDeductionService {

    AvailabilityResult checkAvailability(AvailabilityContext context);

    void deductInventory(InventoryDeductionContext context);

    void releaseInventory(InventoryReleaseContext context);
}
