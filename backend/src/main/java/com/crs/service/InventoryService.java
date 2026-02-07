package com.crs.service;

import com.crs.entity.Inventory;
import com.crs.repository.InventoryRepository;
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
    
    /**
     * 获取所有库存列表
     * @return 库存列表
     */
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }
    
    /**
     * 根据ID获取库存
     * @param id 库存ID
     * @return 库存信息
     */
    public Optional<Inventory> getInventoryById(Integer id) {
        return inventoryRepository.findById(id);
    }
    
    /**
     * 根据酒店ID获取库存列表
     * @param hotelId 酒店ID
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByHotelId(Integer hotelId) {
        return inventoryRepository.findByHotelId(hotelId);
    }
    
    /**
     * 根据酒店ID、价格计划ID和房型ID获取库存列表
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByHotelIdAndRatePlanIdAndRoomTypeId(Integer hotelId, Integer ratePlanId, Integer roomTypeId) {
        return inventoryRepository.findByHotelIdAndRatePlanIdAndRoomTypeId(hotelId, ratePlanId, roomTypeId);
    }
    
    /**
     * 根据日期范围获取库存
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByDateRange(Integer hotelId, Date startDate, Date endDate) {
        return inventoryRepository.findByHotelIdAndDateBetween(hotelId, startDate, endDate);
    }
    
    /**
     * 创建库存
     * @param inventory 库存信息
     * @return 创建的库存信息
     */
    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
    
    /**
     * 批量创建库存
     * @param inventories 库存列表
     * @return 创建的库存列表
     */
    public List<Inventory> createBatchInventories(List<Inventory> inventories) {
        return inventoryRepository.saveAll(inventories);
    }
    
    /**
     * 更新库存
     * @param id 库存ID
     * @param inventory 库存信息
     * @return 更新后的库存信息
     */
    public Inventory updateInventory(Integer id, Inventory inventory) {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        existingInventory.setHotelId(inventory.getHotelId());
        existingInventory.setRatePlanId(inventory.getRatePlanId());
        existingInventory.setRoomTypeId(inventory.getRoomTypeId());
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
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        existingInventory.setAvailableRooms(availableRooms);
        return inventoryRepository.save(existingInventory);
    }
    
    /**
     * 批量更新库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param availableRooms 可用房间数量
     * @return 更新的库存数量
     */
    public int batchUpdateInventory(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Date startDate, Date endDate, Integer availableRooms) {
        List<Inventory> inventories = inventoryRepository.findByHotelIdAndRatePlanIdAndRoomTypeIdAndDateBetween(
                hotelId, ratePlanId, roomTypeId, startDate, endDate);
        
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
        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Inventory not found");
        }
        inventoryRepository.deleteById(id);
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
    public boolean checkInventoryAvailability(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Date date, Integer requiredRooms) {
        Inventory inventory = inventoryRepository.findByHotelIdAndRatePlanIdAndRoomTypeIdAndDate(
                hotelId, ratePlanId, roomTypeId, date);
        
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
    public boolean reserveInventory(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Date date, Integer reservedRooms) {
        Inventory inventory = inventoryRepository.findByHotelIdAndRatePlanIdAndRoomTypeIdAndDate(
                hotelId, ratePlanId, roomTypeId, date);
        
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
    public boolean releaseInventory(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Date date, Integer releasedRooms) {
        Inventory inventory = inventoryRepository.findByHotelIdAndRatePlanIdAndRoomTypeIdAndDate(
                hotelId, ratePlanId, roomTypeId, date);
        
        if (inventory == null || inventory.getAllocatedRooms() < releasedRooms) {
            return false;
        }
        
        inventory.setAvailableRooms(inventory.getAvailableRooms() + releasedRooms);
        inventory.setAllocatedRooms(inventory.getAllocatedRooms() - releasedRooms);
        inventoryRepository.save(inventory);
        
        return true;
    }
    
    /**
     * 根据渠道ID获取库存列表
     * @param channelId 渠道ID
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByChannelId(Integer channelId) {
        return inventoryRepository.findByChannelId(channelId);
    }
    
    /**
     * 根据酒店ID和渠道ID获取库存列表
     * @param hotelId 酒店ID
     * @param channelId 渠道ID
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByHotelIdAndChannelId(Integer hotelId, Integer channelId) {
        return inventoryRepository.findByHotelIdAndChannelId(hotelId, channelId);
    }
    
    /**
     * 根据酒店ID、渠道ID和日期范围获取库存
     * @param hotelId 酒店ID
     * @param channelId 渠道ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 库存列表
     */
    public List<Inventory> getInventoriesByHotelIdAndChannelIdAndDateRange(Integer hotelId, Integer channelId, Date startDate, Date endDate) {
        return inventoryRepository.findByHotelIdAndChannelIdAndDateBetween(hotelId, channelId, startDate, endDate);
    }
    
    /**
     * 检查渠道库存是否充足
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param date 日期
     * @param requiredRooms 需要的房间数量
     * @return 是否充足
     */
    public boolean checkChannelInventoryAvailability(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Integer channelId, Date date, Integer requiredRooms) {
        Inventory inventory = inventoryRepository.findByHotelIdAndRatePlanIdAndRoomTypeIdAndChannelIdAndDate(
                hotelId, ratePlanId, roomTypeId, channelId, date);
        
        return inventory != null && inventory.getAvailableRooms() >= requiredRooms;
    }
    
    /**
     * 预留渠道库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param date 日期
     * @param reservedRooms 预留的房间数量
     * @return 是否成功
     */
    public boolean reserveChannelInventory(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Integer channelId, Date date, Integer reservedRooms) {
        Inventory inventory = inventoryRepository.findByHotelIdAndRatePlanIdAndRoomTypeIdAndChannelIdAndDate(
                hotelId, ratePlanId, roomTypeId, channelId, date);
        
        if (inventory == null || inventory.getAvailableRooms() < reservedRooms) {
            return false;
        }
        
        inventory.setAvailableRooms(inventory.getAvailableRooms() - reservedRooms);
        inventory.setAllocatedRooms(inventory.getAllocatedRooms() + reservedRooms);
        inventoryRepository.save(inventory);
        
        return true;
    }
    
    /**
     * 释放渠道预留库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param date 日期
     * @param releasedRooms 释放的房间数量
     * @return 是否成功
     */
    public boolean releaseChannelInventory(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Integer channelId, Date date, Integer releasedRooms) {
        Inventory inventory = inventoryRepository.findByHotelIdAndRatePlanIdAndRoomTypeIdAndChannelIdAndDate(
                hotelId, ratePlanId, roomTypeId, channelId, date);
        
        if (inventory == null || inventory.getAllocatedRooms() < releasedRooms) {
            return false;
        }
        
        inventory.setAvailableRooms(inventory.getAvailableRooms() + releasedRooms);
        inventory.setAllocatedRooms(inventory.getAllocatedRooms() - releasedRooms);
        inventoryRepository.save(inventory);
        
        return true;
    }
    
    /**
     * 批量更新渠道库存
     * @param hotelId 酒店ID
     * @param ratePlanId 价格计划ID
     * @param roomTypeId 房型ID
     * @param channelId 渠道ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param availableRooms 可用房间数量
     * @return 更新的库存数量
     */
    public int batchUpdateChannelInventory(Integer hotelId, Integer ratePlanId, Integer roomTypeId, Integer channelId, Date startDate, Date endDate, Integer availableRooms) {
        List<Inventory> inventories = inventoryRepository.findByHotelIdAndChannelIdAndDateBetween(
                hotelId, channelId, startDate, endDate);
        
        // 过滤出匹配价格计划和房型的库存
        inventories = inventories.stream()
                .filter(inventory -> inventory.getRatePlanId().equals(ratePlanId) && inventory.getRoomTypeId().equals(roomTypeId))
                .collect(java.util.stream.Collectors.toList());
        
        inventories.forEach(inventory -> {
            inventory.setAvailableRooms(availableRooms);
        });
        
        inventoryRepository.saveAll(inventories);
        return inventories.size();
    }
}

