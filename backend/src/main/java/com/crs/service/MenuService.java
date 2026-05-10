package com.crs.service;

import com.crs.entity.Menu;
import java.util.List;
import java.util.Optional;

/**
 * MenuService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【MenuService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 MenuService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
public interface MenuService {
    
    List<Menu> getAllMenus();
    
    List<Menu> getAllMenus(String menuCode, String menuName, Menu.Status status);
    
    List<Menu> getMenusBySystemType(String systemType);
    
    List<Menu> getActiveMenusBySystemType(String systemType);
    
    List<Menu> getMenusByParentIdAndSystemType(Integer parentId, String systemType);
    
    List<Menu> getMenusByParentCodeAndSystemType(String parentCode, String systemType);
    
    Optional<Menu> getMenuById(Integer id);
    
    Optional<Menu> getMenuByMenuCode(String menuCode);
    
    Menu createMenu(Menu menu);
    
    Menu updateMenu(Integer id, Menu menu);
    
    void deleteMenu(Integer id);
}
