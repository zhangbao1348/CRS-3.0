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
     * 获取所有集团列表
     * @return 集团列表
     */
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }
    
    /**
     * 根据ID获取集团
     * @param id 集团ID
     * @return 集团信息
     */
    public Optional<Group> getGroupById(Integer id) {
        return groupRepository.findById(id);
    }
    
    /**
     * 根据集团代码获取集团
     * @param groupCode 集团代码
     * @return 集团信息
     */
    public Optional<Group> getGroupByCode(String groupCode) {
        return groupRepository.findByGroupCode(groupCode);
    }
    
    /**
     * 创建集团
     * @param group 集团信息
     * @return 创建的集团信息
     */
    public Group createGroup(Group group) {
        // 检查集团代码是否已存在
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
        return groupRepository.findByStatus(status);
    }
    
    /**
     * 根据集团名称搜索集团
     * @param groupName 集团名称
     * @return 集团列表
     */
    public List<Group> searchGroupsByName(String groupName) {
        return groupRepository.findByGroupNameContaining(groupName);
    }
}
