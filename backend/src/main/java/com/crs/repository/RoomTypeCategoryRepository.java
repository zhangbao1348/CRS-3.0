package com.crs.repository;

import com.crs.entity.RoomTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeCategoryRepository extends JpaRepository<RoomTypeCategory, Integer> {
    
    List<RoomTypeCategory> findByTenantId(Integer tenantId);
    
    List<RoomTypeCategory> findByGroupId(Integer groupId);
    
    List<RoomTypeCategory> findByGroupIdAndStatus(Integer groupId, String status);
    
    Optional<RoomTypeCategory> findByGroupIdAndCategoryCode(Integer groupId, String categoryCode);
    
    boolean existsByGroupIdAndCategoryCode(Integer groupId, String categoryCode);
    
    List<RoomTypeCategory> findByGroupIdOrderBySortOrderAsc(Integer groupId);
    
    List<RoomTypeCategory> findByTenantIdOrderBySortOrderAsc(Integer tenantId);
    
    Optional<RoomTypeCategory> findByTenantIdAndCategoryCode(Integer tenantId, String categoryCode);
    
    boolean existsByTenantIdAndCategoryCode(Integer tenantId, String categoryCode);
    
    List<RoomTypeCategory> findByTenantIdAndStatus(Integer tenantId, String status);

    List<RoomTypeCategory> findByGroupCode(String groupCode);

    List<RoomTypeCategory> findByGroupCodeAndStatus(String groupCode, String status);

    Optional<RoomTypeCategory> findByGroupCodeAndCategoryCode(String groupCode, String categoryCode);

    boolean existsByGroupCodeAndCategoryCode(String groupCode, String categoryCode);

    List<RoomTypeCategory> findByGroupCodeOrderBySortOrderAsc(String groupCode);
}