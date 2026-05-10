package com.crs.service.impl;

import com.crs.entity.Menu;
import com.crs.repository.MenuRepository;
import com.crs.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * MenuServiceImpl 服务实现类 (Service Implementation)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【MenuServiceImpl】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 MenuServiceImpl 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
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
