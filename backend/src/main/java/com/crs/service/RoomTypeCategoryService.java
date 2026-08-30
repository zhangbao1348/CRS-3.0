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
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    public List<RoomTypeCategory> getAllRoomTypeCategories() {
        return roomTypeCategoryRepository.findByTenantId(getCurrentTenantId());
    }
    
    public RoomTypeCategory getRoomTypeCategoryById(Integer id) {
        return roomTypeCategoryRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElse(null);
    }
    
    public List<RoomTypeCategory> getRoomTypeCategoriesByGroupIdAndStatus(String status) {
        return roomTypeCategoryRepository.findByGroupIdAndStatus(getCurrentTenantId(), status);
    }
    
    public RoomTypeCategory getRoomTypeCategoryByCode(String categoryCode) {
        return roomTypeCategoryRepository.findByTenantIdAndCategoryCode(getCurrentTenantId(), categoryCode).orElse(null);
    }
    
    public List<RoomTypeCategory> getActiveRoomTypeCategories() {
        return roomTypeCategoryRepository.findByTenantIdAndStatus(getCurrentTenantId(), "active");
    }
    
    public RoomTypeCategory createRoomTypeCategory(RoomTypeCategory roomTypeCategory) {
        Integer currentTenantId = getCurrentTenantId();
        if (roomTypeCategoryRepository.existsByTenantIdAndCategoryCode(currentTenantId, roomTypeCategory.getCategoryCode())) {
            throw new RuntimeException("Category code already exists");
        }
        roomTypeCategory.setId(null);
        roomTypeCategory.setTenantId(currentTenantId);
        roomTypeCategory.setGroupId(currentTenantId);
        return roomTypeCategoryRepository.save(roomTypeCategory);
    }
    
    public RoomTypeCategory updateRoomTypeCategory(RoomTypeCategory roomTypeCategory) {
        Integer currentTenantId = getCurrentTenantId();
        RoomTypeCategory existing = getRoomTypeCategoryById(roomTypeCategory.getId());
        if (existing != null) {
            if (!existing.getCategoryCode().equals(roomTypeCategory.getCategoryCode()) &&
                    roomTypeCategoryRepository.existsByTenantIdAndCategoryCode(currentTenantId, roomTypeCategory.getCategoryCode())) {
                throw new RuntimeException("Category code already exists");
            }
            existing.setCategoryCode(roomTypeCategory.getCategoryCode());
            existing.setCategoryName(roomTypeCategory.getCategoryName());
            if (roomTypeCategory.getSortOrder() != null) {
                existing.setSortOrder(roomTypeCategory.getSortOrder());
            }
            if (roomTypeCategory.getStatus() != null) {
                existing.setStatus(roomTypeCategory.getStatus());
            }
            return roomTypeCategoryRepository.save(existing);
        }
        return null;
    }
    
    public void deleteRoomTypeCategory(Integer id) {
        RoomTypeCategory existing = getRoomTypeCategoryById(id);
        if (existing != null) {
            roomTypeCategoryRepository.delete(existing);
        }
    }
    
    public RoomTypeCategory enableRoomTypeCategory(Integer id) {
        RoomTypeCategory category = getRoomTypeCategoryById(id);
        if (category != null) {
            category.setStatus("active");
            return roomTypeCategoryRepository.save(category);
        }
        return null;
    }
    
    public RoomTypeCategory disableRoomTypeCategory(Integer id) {
        RoomTypeCategory category = getRoomTypeCategoryById(id);
        if (category != null) {
            category.setStatus("inactive");
            return roomTypeCategoryRepository.save(category);
        }
        return null;
    }
    
    public boolean isCodeUnique(String code, Integer excludeId) {
        RoomTypeCategory existing = roomTypeCategoryRepository.findByTenantIdAndCategoryCode(getCurrentTenantId(), code).orElse(null);
        return existing == null || (excludeId != null && existing.getId().equals(excludeId));
    }
}
