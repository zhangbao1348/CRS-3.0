package com.crs.service;

import com.crs.entity.Menu;
import java.util.List;
import java.util.Optional;

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
