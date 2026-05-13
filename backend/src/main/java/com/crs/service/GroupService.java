package com.crs.service;

import com.crs.entity.Group;
import com.crs.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 集团服务类
 * 用于处理集团相关的业务逻辑
 */
@Service
public class GroupService {
    
    private final GroupRepository groupRepository;
    
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }
    
    /**
     * 获取当前租户对应的集团
     * @return 集团列表（仅包含当前租户集团）
     */
    public List<Group> getAllGroups() {
        Integer tenantId = getCurrentTenantId();
        return groupRepository.findById(tenantId)
                .map(java.util.List::of)
                .orElse(java.util.Collections.emptyList());
    }
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 根据ID获取集团
     * @param id 集团ID
     * @return 集团信息
     */
    public Optional<Group> getGroupById(Integer id) {
        if (!id.equals(getCurrentTenantId())) {
            return Optional.empty();
        }
        return groupRepository.findById(id);
    }
    
    /**
     * 根据集团代码获取集团
     * @param groupCode 集团代码
     * @return 集团信息
     */
    public Optional<Group> getGroupByCode(String groupCode) {
        return groupRepository.findByGroupCode(groupCode)
                .filter(g -> g.getId().equals(getCurrentTenantId()));
    }
    
    /**
     * 创建集团
     * @param group 集团信息
     * @return 创建的集团信息
     */
    public Group createGroup(Group group) {
        // 创建集团通常是超级管理员权限，此处简单校验
        if (groupRepository.existsByGroupCode(group.getGroupCode())) {
            throw new RuntimeException("Group code already exists");
        }
        return groupRepository.save(group);
    }
    
    /**
     * 更新集团
     * @param id 集团ID
     * @param group 集团信息
     * @return 更新后的集团信息
     */
    public Group updateGroup(Integer id, Group group) {
        if (!id.equals(getCurrentTenantId())) {
            throw new RuntimeException("Access denied");
        }
        
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // 如果集团代码变更，检查新代码是否已存在
        if (!existingGroup.getGroupCode().equals(group.getGroupCode()) && 
                groupRepository.existsByGroupCode(group.getGroupCode())) {
            throw new RuntimeException("Group code already exists");
        }
        
        existingGroup.setGroupCode(group.getGroupCode());
        existingGroup.setGroupName(group.getGroupName());
        existingGroup.setDescription(group.getDescription());
        existingGroup.setStatus(group.getStatus());
        
        return groupRepository.save(existingGroup);
    }
    
    /**
     * 删除集团
     * @param id 集团ID
     */
    public void deleteGroup(Integer id) {
        if (!id.equals(getCurrentTenantId())) {
            throw new RuntimeException("Access denied");
        }
        if (!groupRepository.existsById(id)) {
            throw new RuntimeException("Group not found");
        }
        groupRepository.deleteById(id);
    }
    
    /**
     * 根据状态获取集团列表
     * @param status 状态
     * @return 集团列表
     */
    public List<Group> getGroupsByStatus(Group.Status status) {
        return groupRepository.findById(getCurrentTenantId())
                .filter(g -> g.getStatus() == status)
                .map(java.util.List::of)
                .orElse(java.util.Collections.emptyList());
    }
    
    /**
     * 根据集团名称搜索集团
     * @param groupName 集团名称
     * @return 集团列表
     */
    public List<Group> searchGroupsByName(String groupName) {
        return groupRepository.findByGroupNameContaining(groupName).stream()
                .filter(g -> g.getId().equals(getCurrentTenantId()))
                .collect(java.util.stream.Collectors.toList());
    }
}
