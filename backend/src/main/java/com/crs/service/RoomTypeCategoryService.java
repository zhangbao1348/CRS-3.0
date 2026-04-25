package com.crs.service;

import com.crs.entity.RoomTypeCategory;
import com.crs.repository.RoomTypeCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomTypeCategoryService {
    
    private final RoomTypeCategoryRepository roomTypeCategoryRepository;
    
    public RoomTypeCategoryService(RoomTypeCategoryRepository roomTypeCategoryRepository) {
        this.roomTypeCategoryRepository = roomTypeCategoryRepository;
    }
    
    public List<RoomTypeCategory> getAllRoomTypeCategories() {
        return roomTypeCategoryRepository.findAll();
    }
    
    public List<RoomTypeCategory> getAllRoomTypeCategories(Integer tenantId) {
        return roomTypeCategoryRepository.findByTenantId(tenantId);
    }
    
    public RoomTypeCategory getRoomTypeCategoryById(Integer tenantId, Integer id) {
        RoomTypeCategory category = roomTypeCategoryRepository.findById(id).orElse(null);
        if (category != null && category.getTenantId() != null && category.getTenantId().equals(tenantId)) {
            return category;
        }
        return null;
    }
    
    public List<RoomTypeCategory> getRoomTypeCategoriesByGroupId(Integer groupId) {
        return roomTypeCategoryRepository.findByGroupIdOrderBySortOrderAsc(groupId);
    }
    
    public List<RoomTypeCategory> getRoomTypeCategoriesByGroupIdAndStatus(Integer groupId, String status) {
        return roomTypeCategoryRepository.findByGroupIdAndStatus(groupId, status);
    }
    
    public RoomTypeCategory getRoomTypeCategoryByCode(Integer tenantId, String categoryCode) {
        return roomTypeCategoryRepository.findByTenantIdAndCategoryCode(tenantId, categoryCode).orElse(null);
    }
    
    public List<RoomTypeCategory> getActiveRoomTypeCategories(Integer tenantId) {
        return roomTypeCategoryRepository.findByTenantIdAndStatus(tenantId, "active");
    }
    
    public RoomTypeCategory createRoomTypeCategory(Integer tenantId, RoomTypeCategory roomTypeCategory) {
        if (roomTypeCategoryRepository.existsByTenantIdAndCategoryCode(tenantId, roomTypeCategory.getCategoryCode())) {
            throw new RuntimeException("Category code already exists");
        }
        roomTypeCategory.setTenantId(tenantId);
        return roomTypeCategoryRepository.save(roomTypeCategory);
    }
    
    public RoomTypeCategory updateRoomTypeCategory(Integer tenantId, RoomTypeCategory roomTypeCategory) {
        RoomTypeCategory existing = getRoomTypeCategoryById(tenantId, roomTypeCategory.getId());
        if (existing != null) {
            if (!existing.getCategoryCode().equals(roomTypeCategory.getCategoryCode()) &&
                    roomTypeCategoryRepository.existsByTenantIdAndCategoryCode(tenantId, roomTypeCategory.getCategoryCode())) {
                throw new RuntimeException("Category code already exists");
            }
            roomTypeCategory.setTenantId(tenantId);
            return roomTypeCategoryRepository.save(roomTypeCategory);
        }
        return null;
    }
    
    public void deleteRoomTypeCategory(Integer tenantId, Integer id) {
        RoomTypeCategory existing = getRoomTypeCategoryById(tenantId, id);
        if (existing != null) {
            roomTypeCategoryRepository.deleteById(id);
        }
    }
    
    public RoomTypeCategory enableRoomTypeCategory(Integer tenantId, Integer id) {
        RoomTypeCategory category = getRoomTypeCategoryById(tenantId, id);
        if (category != null) {
            category.setStatus("active");
            return roomTypeCategoryRepository.save(category);
        }
        return null;
    }
    
    public RoomTypeCategory disableRoomTypeCategory(Integer tenantId, Integer id) {
        RoomTypeCategory category = getRoomTypeCategoryById(tenantId, id);
        if (category != null) {
            category.setStatus("inactive");
            return roomTypeCategoryRepository.save(category);
        }
        return null;
    }
    
    public boolean isCodeUnique(Integer tenantId, String code, Integer excludeId) {
        try {
            RoomTypeCategory existing = roomTypeCategoryRepository.findByTenantIdAndCategoryCode(tenantId, code).orElse(null);
            return existing == null || (excludeId != null && existing.getId().equals(excludeId));
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}