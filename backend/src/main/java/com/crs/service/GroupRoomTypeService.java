package com.crs.service;

import com.crs.entity.GroupRoomType;
import com.crs.repository.GroupRoomTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 集团房型服务类
 * 用于处理集团房型相关的业务逻辑
 */
@Service
public class GroupRoomTypeService {
    
    private final GroupRoomTypeRepository groupRoomTypeRepository;
    
    public GroupRoomTypeService(GroupRoomTypeRepository groupRoomTypeRepository) {
        this.groupRoomTypeRepository = groupRoomTypeRepository;
    }
    
    /**
     * 获取所有集团房型列表
     * @return 集团房型列表
     */
    public List<GroupRoomType> getAllGroupRoomTypes() {
        return groupRoomTypeRepository.findAll();
    }
    
    /**
     * 根据ID获取集团房型
     * @param id 集团房型ID
     * @return 集团房型信息
     */
    public Optional<GroupRoomType> getGroupRoomTypeById(Integer id) {
        return groupRoomTypeRepository.findById(id);
    }
    
    /**
     * 根据集团ID获取集团房型列表
     * @param groupId 集团ID
     * @return 集团房型列表
     */
    public List<GroupRoomType> getGroupRoomTypesByGroupId(Integer groupId) {
        return groupRoomTypeRepository.findByGroupId(groupId);
    }
    
    /**
     * 根据房型代码获取集团房型
     * @param roomTypeCode 房型代码
     * @return 集团房型信息
     */
    public Optional<GroupRoomType> getGroupRoomTypeByCode(String roomTypeCode) {
        return groupRoomTypeRepository.findByRoomTypeCode(roomTypeCode);
    }
    
    /**
     * 创建集团房型
     * @param groupRoomType 集团房型信息
     * @return 创建的集团房型信息
     */
    public GroupRoomType createGroupRoomType(GroupRoomType groupRoomType) {
        // 检查房型代码是否已存在
        if (groupRoomTypeRepository.existsByRoomTypeCode(groupRoomType.getRoomTypeCode())) {
            throw new RuntimeException("Room type code already exists");
        }
        return groupRoomTypeRepository.save(groupRoomType);
    }
    
    /**
     * 更新集团房型
     * @param id 集团房型ID
     * @param groupRoomType 集团房型信息
     * @return 更新后的集团房型信息
     */
    public GroupRoomType updateGroupRoomType(Integer id, GroupRoomType groupRoomType) {
        GroupRoomType existingGroupRoomType = groupRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found"));
        
        // 如果房型代码变更，检查新代码是否已存在
        if (!existingGroupRoomType.getRoomTypeCode().equals(groupRoomType.getRoomTypeCode()) && 
                groupRoomTypeRepository.existsByRoomTypeCode(groupRoomType.getRoomTypeCode())) {
            throw new RuntimeException("Room type code already exists");
        }
        
        existingGroupRoomType.setGroupId(groupRoomType.getGroupId());
        existingGroupRoomType.setRoomTypeCode(groupRoomType.getRoomTypeCode());
        existingGroupRoomType.setRoomTypeName(groupRoomType.getRoomTypeName());
        existingGroupRoomType.setDescription(groupRoomType.getDescription());
        existingGroupRoomType.setStatus(groupRoomType.getStatus());
        
        return groupRoomTypeRepository.save(existingGroupRoomType);
    }
    
    /**
     * 启用集团房型
     * @param id 集团房型ID
     * @return 启用后的集团房型信息
     */
    public GroupRoomType enableGroupRoomType(Integer id) {
        GroupRoomType groupRoomType = groupRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found"));
        groupRoomType.setStatus("active");
        return groupRoomTypeRepository.save(groupRoomType);
    }
    
    /**
     * 停用集团房型
     * @param id 集团房型ID
     * @return 停用后的集团房型信息
     */
    public GroupRoomType disableGroupRoomType(Integer id) {
        GroupRoomType groupRoomType = groupRoomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group room type not found"));
        groupRoomType.setStatus("inactive");
        return groupRoomTypeRepository.save(groupRoomType);
    }
    
    /**
     * 删除集团房型
     * @param id 集团房型ID
     */
    public void deleteGroupRoomType(Integer id) {
        if (!groupRoomTypeRepository.existsById(id)) {
            throw new RuntimeException("Group room type not found");
        }
        groupRoomTypeRepository.deleteById(id);
    }
    
    /**
     * 根据状态获取集团房型列表
     * @param status 状态
     * @return 集团房型列表
     */
    public List<GroupRoomType> getGroupRoomTypesByStatus(String status) {
        return groupRoomTypeRepository.findByStatus(status);
    }
}
