package com.crs.service;

import com.crs.entity.Inventory;
import com.crs.repository.InventoryRepository;
import com.crs.util.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Date;

/**
 * 库存服务类
 * 用于处理库存相关的业务逻辑
 */
@Service
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }
    
    /**
     * 获取所有库存列表
     * @return 库存列表
     */
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findByTenantId(getCurrentTenantId());
    }
    
    /**
     * 根据ID获取库存
     * @param id 库存ID
     * @return 库存信息
     */
    public Optional<Inventory> getInventoryById(Integer id) {
        return inventoryRepository.findById(id)
                .filter(i -> i.getTenantId() != null && i.getTenantId().equals(getCurrentTenantId()));
    }

    /**
     * 根据酒店编码获取库存列表
     * @param hotelCode 酒店编码
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByHotelCode(String hotelCode) {
        return inventoryRepository.findByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }
    
    /**
     * 根据酒店编码、价格计划编码和房型编码获取库存列表
     * @param hotelCode 酒店编码
     * @param ratePlanCode 价格计划编码
     * @param roomTypeCode 房型编码
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByCode(String hotelCode, String ratePlanCode, String roomTypeCode) {
        return inventoryRepository.findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCode(getCurrentTenantId(), hotelCode, ratePlanCode, roomTypeCode);
    }
    
    /**
     * 根据日期范围获取库存
     * @param hotelCode 酒店编码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByDateRange(String hotelCode, Date startDate, Date endDate) {
        return inventoryRepository.findByTenantIdAndHotelCodeAndDateBetween(getCurrentTenantId(), hotelCode, startDate, endDate);
    }
    
    /**
     * 创建库存
     * @param inventory 库存信息
     * @return 创建的库存信息
     */
    public Inventory createInventory(Inventory inventory) {
        inventory.setTenantId(getCurrentTenantId());
        return inventoryRepository.save(inventory);
    }
    
    /**
     * 批量创建库存
     * @param inventories 库存列表
     * @return 创建的库存列表
     */
    public List<Inventory> createBatchInventories(List<Inventory> inventories) {
        Integer tenantId = getCurrentTenantId();
        inventories.forEach(i -> i.setTenantId(tenantId));
        return inventoryRepository.saveAll(inventories);
    }
    
    /**
     * 更新库存
     * @param id 库存ID
     * @param inventory 库存信息
     * @return 更新后的库存信息
     */
    public Inventory updateInventory(Integer id, Inventory inventory) {
        Inventory existingInventory = getInventoryById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found or access denied"));
        
        existingInventory.setHotelCode(inventory.getHotelCode());
        existingInventory.setRatePlanCode(inventory.getRatePlanCode());
        existingInventory.setRoomTypeCode(inventory.getRoomTypeCode());
        existingInventory.setChannelCode(inventory.getChannelCode());
        existingInventory.setDate(inventory.getDate());
        existingInventory.setAvailableRooms(inventory.getAvailableRooms());
        existingInventory.setAllocatedRooms(inventory.getAllocatedRooms());
        existingInventory.setStatus(inventory.getStatus());
        
        return inventoryRepository.save(existingInventory);
    }
    
    /**
     * 更新可用房间数量
     * @param id 库存ID
     * @param availableRooms 可用房间数量
     * @return 更新后的库存信息
     */
    public Inventory updateAvailableRooms(Integer id, Integer availableRooms) {
        Inventory existingInventory = getInventoryById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found or access denied"));
        
        existingInventory.setAvailableRooms(availableRooms);
        return inventoryRepository.save(existingInventory);
    }
    
    /**
     * 批量更新库存
     * @param hotelCode 酒店编码
     * @param ratePlanCode 价格计划编码
     * @param roomTypeCode 房型编码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param availableRooms 可用房间数量
     * @return 更新的库存数量
     */
    public int batchUpdateInventory(String hotelCode, String ratePlanCode, String roomTypeCode, Date startDate, Date endDate, Integer availableRooms) {
        List<Inventory> inventories = inventoryRepository.findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDateBetween(
                getCurrentTenantId(), hotelCode, ratePlanCode, roomTypeCode, startDate, endDate);
        
        inventories.forEach(inventory -> {
            inventory.setAvailableRooms(availableRooms);
        });
        
        inventoryRepository.saveAll(inventories);
        return inventories.size();
    }
    
    /**
     * 删除库存
     * @param id 库存ID
     */
    public void deleteInventory(Integer id) {
        Inventory existing = getInventoryById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found or access denied"));
        inventoryRepository.delete(existing);
    }
    
    /**
     * 检查库存是否充足
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param date 日期
     * @param requiredRooms 需要的房间数量
     * @return 是否充足
     */
    public boolean checkInventoryAvailability(String hotelCode, String ratePlanCode, String roomTypeCode, Date date, Integer requiredRooms) {
        Inventory inventory = inventoryRepository.findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDate(
                getCurrentTenantId(), hotelCode, ratePlanCode, roomTypeCode, date);
        
        return inventory != null && inventory.getAvailableRooms() >= requiredRooms;
    }
    
    /**
     * 预留库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param date 日期
     * @param reservedRooms 预留的房间数量
     * @return 是否成功
     */
    public boolean reserveInventory(String hotelCode, String ratePlanCode, String roomTypeCode, Date date, Integer reservedRooms) {
        Inventory inventory = inventoryRepository.findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDate(
                getCurrentTenantId(), hotelCode, ratePlanCode, roomTypeCode, date);
        
        if (inventory == null || inventory.getAvailableRooms() < reservedRooms) {
            return false;
        }
        
        inventory.setAvailableRooms(inventory.getAvailableRooms() - reservedRooms);
        inventory.setAllocatedRooms(inventory.getAllocatedRooms() + reservedRooms);
        inventoryRepository.save(inventory);
        
        return true;
    }
    
    /**
     * 释放预留库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param date 日期
     * @param releasedRooms 释放的房间数量
     * @return 是否成功
     */
    public boolean releaseInventory(String hotelCode, String ratePlanCode, String roomTypeCode, Date date, Integer releasedRooms) {
        Inventory inventory = inventoryRepository.findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndDate(
                getCurrentTenantId(), hotelCode, ratePlanCode, roomTypeCode, date);
        
        if (inventory == null || inventory.getAllocatedRooms() < releasedRooms) {
            return false;
        }
        
        inventory.setAvailableRooms(inventory.getAvailableRooms() + releasedRooms);
        inventory.setAllocatedRooms(inventory.getAllocatedRooms() - releasedRooms);
        inventoryRepository.save(inventory);
        
        return true;
    }
    
    /**
     * 根据渠道编码获取库存列表
     * @param channelCode 渠道编码
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByChannelCode(String channelCode) {
        return inventoryRepository.findByTenantId(getCurrentTenantId()).stream()
                .filter(i -> channelCode.equals(i.getChannelCode()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 根据酒店编码和渠道编码获取库存列表
     * @param hotelCode 酒店编码
     * @param channelCode 渠道编码
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByHotelCodeAndChannelCode(String hotelCode, String channelCode) {
        return inventoryRepository.findByTenantIdAndHotelCodeAndChannelCode(getCurrentTenantId(), hotelCode, channelCode);
    }
    
    /**
     * 根据酒店编码、渠道编码和日期范围获取库存
     * @param hotelCode 酒店编码
     * @param channelCode 渠道编码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByHotelCodeAndChannelCodeAndDateRange(String hotelCode, String channelCode, Date startDate, Date endDate) {
        return inventoryRepository.findByTenantIdAndHotelCodeAndChannelCodeAndDateBetween(getCurrentTenantId(), hotelCode, channelCode, startDate, endDate);
    }
    
    public boolean checkChannelInventoryAvailability(String hotelCode, String ratePlanCode, String roomTypeCode, String channelCode, Date date, Integer requiredRooms) {
        Inventory inventory = inventoryRepository.findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndChannelCodeAndDate(
                getCurrentTenantId(), hotelCode, ratePlanCode, roomTypeCode, channelCode, date);
        
        return inventory != null && inventory.getAvailableRooms() >= requiredRooms;
    }
    
    public boolean reserveChannelInventory(String hotelCode, String ratePlanCode, String roomTypeCode, String channelCode, Date date, Integer reservedRooms) {
        Inventory inventory = inventoryRepository.findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndChannelCodeAndDate(
                getCurrentTenantId(), hotelCode, ratePlanCode, roomTypeCode, channelCode, date);
        
        if (inventory == null || inventory.getAvailableRooms() < reservedRooms) {
            return false;
        }
        
        inventory.setAvailableRooms(inventory.getAvailableRooms() - reservedRooms);
        inventory.setAllocatedRooms(inventory.getAllocatedRooms() + reservedRooms);
        inventoryRepository.save(inventory);
        
        return true;
    }
    
    public boolean releaseChannelInventory(String hotelCode, String ratePlanCode, String roomTypeCode, String channelCode, Date date, Integer releasedRooms) {
        Inventory inventory = inventoryRepository.findByTenantIdAndHotelCodeAndRatePlanCodeAndRoomTypeCodeAndChannelCodeAndDate(
                getCurrentTenantId(), hotelCode, ratePlanCode, roomTypeCode, channelCode, date);
        
        if (inventory == null || inventory.getAllocatedRooms() < releasedRooms) {
            return false;
        }
        
        inventory.setAvailableRooms(inventory.getAvailableRooms() + releasedRooms);
        inventory.setAllocatedRooms(inventory.getAllocatedRooms() - releasedRooms);
        inventoryRepository.save(inventory);
        
        return true;
    }
    
    public int batchUpdateChannelInventory(String hotelCode, String ratePlanCode, String roomTypeCode, String channelCode, Date startDate, Date endDate, Integer availableRooms) {
        List<Inventory> inventories = inventoryRepository.findByTenantIdAndHotelCodeAndChannelCodeAndDateBetween(
                getCurrentTenantId(), hotelCode, channelCode, startDate, endDate);
        
        // 过滤出匹配价格计划和房型的库存
        inventories = inventories.stream()
                .filter(inventory -> ratePlanCode.equals(inventory.getRatePlanCode()) && roomTypeCode.equals(inventory.getRoomTypeCode()))
                .collect(java.util.stream.Collectors.toList());
        
        inventories.forEach(inventory -> {
            inventory.setAvailableRooms(availableRooms);
        });
        
        inventoryRepository.saveAll(inventories);
        return inventories.size();
    }
}

