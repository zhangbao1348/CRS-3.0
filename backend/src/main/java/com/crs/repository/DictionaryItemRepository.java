package com.crs.repository;

import com.crs.entity.DictionaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryItemRepository extends JpaRepository<DictionaryItem, Integer> {

    @Query("SELECT di FROM DictionaryItem di " +
            "WHERE di.tenantId = :tenantId " +
            "AND di.typeCode = :typeCode " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(di.itemCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(di.itemName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY di.sortOrder ASC, di.id ASC")
    List<DictionaryItem> searchByTenantIdAndTypeCode(@Param("tenantId") Integer tenantId,
                                                     @Param("typeCode") String typeCode,
                                                     @Param("keyword") String keyword);

    Optional<DictionaryItem> findByIdAndTenantId(Integer id, Integer tenantId);

    List<DictionaryItem> findByTenantIdAndTypeCodeAndStatusOrderBySortOrderAscIdAsc(Integer tenantId,
                                                                                     String typeCode,
                                                                                     DictionaryItem.Status status);

    boolean existsByTenantIdAndTypeCode(Integer tenantId, String typeCode);

    boolean existsByTenantIdAndTypeCodeAndItemCode(Integer tenantId, String typeCode, String itemCode);

    boolean existsByTenantIdAndTypeCodeAndItemCodeAndIdNot(Integer tenantId,
                                                           String typeCode,
                                                           String itemCode,
                                                           Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE DictionaryItem di SET di.isDefault = false " +
            "WHERE di.tenantId = :tenantId " +
            "AND di.typeCode = :typeCode " +
            "AND (:excludeId IS NULL OR di.id <> :excludeId)")
    void clearDefaultFlag(@Param("tenantId") Integer tenantId,
                          @Param("typeCode") String typeCode,
                          @Param("excludeId") Integer excludeId);
}
