package com.crs.service;

import com.crs.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    List<User> getAllUsers();
    
    List<User> getUsersByTenantId(Integer tenantId);
    
    Optional<User> getUserById(Integer id);
    
    Optional<User> getUserByUsername(String username);
    
    User createUser(User user, List<Integer> roleIds);
    
    User updateUser(Integer id, User user, List<Integer> roleIds);
    
    void deleteUser(Integer id);
    
    User updateUserStatus(Integer id, User.Status status);
    
    User resetPassword(Integer id, String newPassword);
}
