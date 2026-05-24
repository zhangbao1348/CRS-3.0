package com.crs.repository;

import com.crs.entity.DictionaryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryTypeRepository extends JpaRepository<DictionaryType, Integer> {

    @Query("SELECT dt FROM DictionaryType dt " +
            "WHERE dt.tenantId = :tenantId " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(dt.typeCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(dt.typeName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY dt.sortOrder ASC, dt.id ASC")
    List<DictionaryType> searchByTenantId(@Param("tenantId") Integer tenantId,
                                          @Param("keyword") String keyword);

    Optional<DictionaryType> findByIdAndTenantId(Integer id, Integer tenantId);

    Optional<DictionaryType> findByTenantIdAndTypeCode(Integer tenantId, String typeCode);

    boolean existsByTenantIdAndTypeCode(Integer tenantId, String typeCode);
}
