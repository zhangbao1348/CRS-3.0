package com.crs.service.impl;

import com.crs.entity.Menu;
import com.crs.repository.MenuRepository;
import com.crs.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Override
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }
    
    @Override
    public List<Menu> getAllMenus(String menuCode, String menuName, Menu.Status status) {
        if (menuCode == null) menuCode = "";
        if (menuName == null) menuName = "";
        return menuRepository.findByMenuCodeContainingAndMenuNameContainingAndStatus(menuCode, menuName, status);
    }
    
    @Override
    public List<Menu> getMenusBySystemType(String systemType) {
        return menuRepository.findBySystemTypeAndStatusOrderBySortOrderAsc(systemType, Menu.Status.active);
    }
    
    @Override
    public List<Menu> getActiveMenusBySystemType(String systemType) {
        return menuRepository.findBySystemTypeAndStatusOrderBySortOrderAsc(systemType, Menu.Status.active);
    }
    
    @Override
    public List<Menu> getMenusByParentIdAndSystemType(Integer parentId, String systemType) {
        return menuRepository.findByParentIdAndSystemTypeAndStatusOrderBySortOrderAsc(parentId, systemType, Menu.Status.active);
    }
    
    @Override
    public List<Menu> getMenusByParentCodeAndSystemType(String parentCode, String systemType) {
        return menuRepository.findByParentCodeAndSystemTypeAndStatusOrderBySortOrderAsc(parentCode, systemType, Menu.Status.active);
    }
    
    @Override
    public Optional<Menu> getMenuById(Integer id) {
        return menuRepository.findById(id);
    }
    
    @Override
    public Optional<Menu> getMenuByMenuCode(String menuCode) {
        return menuRepository.findByMenuCode(menuCode);
    }
    
    @Override
    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }
    
    @Override
    public Menu updateMenu(Integer id, Menu menu) {
        return menuRepository.findById(id).map(existingMenu -> {
            if (menu.getParentId() != null) {
                existingMenu.setParentId(menu.getParentId());
            }
            if (menu.getMenuCode() != null) {
                existingMenu.setMenuCode(menu.getMenuCode());
            }
            if (menu.getMenuName() != null) {
                existingMenu.setMenuName(menu.getMenuName());
            }
            if (menu.getMenuType() != null) {
                existingMenu.setMenuType(menu.getMenuType());
            }
            if (menu.getPath() != null) {
                existingMenu.setPath(menu.getPath());
            }
            if (menu.getComponent() != null) {
                existingMenu.setComponent(menu.getComponent());
            }
            if (menu.getIcon() != null) {
                existingMenu.setIcon(menu.getIcon());
            }
            if (menu.getSortOrder() != null) {
                existingMenu.setSortOrder(menu.getSortOrder());
            }
            if (menu.getStatus() != null) {
                existingMenu.setStatus(menu.getStatus());
            }
            if (menu.getPermission() != null) {
                existingMenu.setPermission(menu.getPermission());
            }
            if (menu.getSystemType() != null) {
                existingMenu.setSystemType(menu.getSystemType());
            }
            if (menu.getRemark() != null) {
                existingMenu.setRemark(menu.getRemark());
            }
            return menuRepository.save(existingMenu);
        }).orElse(null);
    }
    
    @Override
    public void deleteMenu(Integer id) {
        menuRepository.deleteById(id);
    }
}
