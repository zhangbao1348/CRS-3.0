package com.crs.repository;

import com.crs.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    
    List<Menu> findBySystemTypeAndStatusOrderBySortOrderAsc(String systemType, Menu.Status status);
    
    List<Menu> findByParentIdAndSystemTypeAndStatusOrderBySortOrderAsc(Integer parentId, String systemType, Menu.Status status);
    
    List<Menu> findByParentCodeAndSystemTypeAndStatusOrderBySortOrderAsc(String parentCode, String systemType, Menu.Status status);
    
    Optional<Menu> findByMenuCode(String menuCode);
    
    List<Menu> findByMenuCodeContainingAndMenuNameContainingAndStatus(String menuCode, String menuName, Menu.Status status);
}
