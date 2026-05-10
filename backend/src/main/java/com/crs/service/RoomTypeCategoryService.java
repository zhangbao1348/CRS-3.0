package com.crs.service;

import com.crs.entity.RoomTypeCategory;
import com.crs.repository.RoomTypeCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * RoomTypeCategoryService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【RoomTypeCategoryService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/12-房型管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 RoomTypeCategoryService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
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