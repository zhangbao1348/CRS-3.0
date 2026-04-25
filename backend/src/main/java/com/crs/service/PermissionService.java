package com.crs.service;

import com.crs.entity.Menu;
import java.util.List;

public interface PermissionService {
    
    List<Menu> getUserMenus(Integer userId, String systemType);
    
    List<String> getUserPermissions(Integer userId);
    
    boolean hasPermission(Integer userId, String permission);
}
