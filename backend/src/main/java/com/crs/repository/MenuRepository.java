package com.crs.repository;

import com.crs.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MenuRepository 数据访问层 (Repository) 接口
 * 
 * <p>本核心模块自动生成详细注释。主要负责【MenuRepository】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：单一职责原则，提供 MenuRepository 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    
    List<Menu> findBySystemTypeAndStatusOrderBySortOrderAsc(String systemType, Menu.Status status);
    
    List<Menu> findByParentIdAndSystemTypeAndStatusOrderBySortOrderAsc(Integer parentId, String systemType, Menu.Status status);
    
    List<Menu> findByParentCodeAndSystemTypeAndStatusOrderBySortOrderAsc(String parentCode, String systemType, Menu.Status status);
    
    Optional<Menu> findByMenuCode(String menuCode);
    
    List<Menu> findByMenuCodeContainingAndMenuNameContainingAndStatus(String menuCode, String menuName, Menu.Status status);
}
