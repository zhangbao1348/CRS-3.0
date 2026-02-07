package com.crs.service;

import com.crs.entity.GroupRateCode;
import com.crs.repository.GroupRateCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 集团房价码服务类
 * 提供集团房价码的业务逻辑操作
 */
@Service
public class GroupRateCodeService {
    
    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;
    
    /**
     * 获取所有集团房价码
     * @return 集团房价码列表
     */
    public List<GroupRateCode> getAllGroupRateCodes() {
        return groupRateCodeRepository.findAll();
    }
    
    /**
     * 根据ID获取集团房价码
     * @param id 集团房价码ID
     * @return 集团房价码对象
     */
    public GroupRateCode getGroupRateCodeById(Integer id) {
        return groupRateCodeRepository.findById(id).orElse(null);
    }
    
    /**
     * 根据集团ID获取集团房价码列表
     * @param groupId 集团ID
     * @return 集团房价码列表
     */
    public List<GroupRateCode> getGroupRateCodesByGroupId(Integer groupId) {
        return groupRateCodeRepository.findByGroupId(groupId);
    }
    
    /**
     * 根据房价码代码获取集团房价码
     * @param rateCode 房价码代码
     * @return 集团房价码对象
     */
    public GroupRateCode getGroupRateCodeByRateCode(String rateCode) {
        return groupRateCodeRepository.findByRateCode(rateCode);
    }
    
    /**
     * 创建集团房价码
     * @param groupRateCode 集团房价码对象
     * @return 创建的集团房价码对象
     */
    @Transactional
    public GroupRateCode createGroupRateCode(GroupRateCode groupRateCode) {
        // 检查房价码代码是否已存在
        if (groupRateCodeRepository.findByRateCode(groupRateCode.getRateCode()) != null) {
            throw new IllegalArgumentException("房价码代码已存在");
        }
        
        return groupRateCodeRepository.save(groupRateCode);
    }
    
    /**
     * 更新集团房价码
     * @param id 集团房价码ID
     * @param groupRateCode 集团房价码对象
     * @return 更新后的集团房价码对象
     */
    @Transactional
    public GroupRateCode updateGroupRateCode(Integer id, GroupRateCode groupRateCode) {
        GroupRateCode existingRateCode = groupRateCodeRepository.findById(id).orElse(null);
        if (existingRateCode == null) {
            throw new IllegalArgumentException("集团房价码不存在");
        }
        
        // 检查房价码代码是否已被其他记录使用
        GroupRateCode existingByRateCode = groupRateCodeRepository.findByRateCode(groupRateCode.getRateCode());
        if (existingByRateCode != null && !existingByRateCode.getId().equals(id)) {
            throw new IllegalArgumentException("房价码代码已存在");
        }
        
        // 更新字段
        existingRateCode.setRateCode(groupRateCode.getRateCode());
        existingRateCode.setRateName(groupRateCode.getRateName());
        existingRateCode.setDescription(groupRateCode.getDescription());
        existingRateCode.setStatus(groupRateCode.getStatus());
        
        return groupRateCodeRepository.save(existingRateCode);
    }
    
    /**
     * 删除集团房价码
     * @param id 集团房价码ID
     */
    @Transactional
    public void deleteGroupRateCode(Integer id) {
        if (!groupRateCodeRepository.existsById(id)) {
            throw new IllegalArgumentException("集团房价码不存在");
        }
        groupRateCodeRepository.deleteById(id);
    }
    
    /**
     * 根据集团ID和状态获取集团房价码列表
     * @param groupId 集团ID
     * @param status 状态
     * @return 集团房价码列表
     */
    public List<GroupRateCode> getGroupRateCodesByGroupIdAndStatus(Integer groupId, String status) {
        return groupRateCodeRepository.findByGroupIdAndStatus(groupId, status);
    }
    
    /**
     * 启用集团房价码
     * @param id 集团房价码ID
     * @return 启用后的集团房价码对象
     */
    @Transactional
    public GroupRateCode enableGroupRateCode(Integer id) {
        GroupRateCode rateCode = groupRateCodeRepository.findById(id).orElse(null);
        if (rateCode == null) {
            throw new IllegalArgumentException("集团房价码不存在");
        }
        rateCode.setStatus("active");
        return groupRateCodeRepository.save(rateCode);
    }
    
    /**
     * 停用集团房价码
     * @param id 集团房价码ID
     * @return 停用后的集团房价码对象
     */
    @Transactional
    public GroupRateCode disableGroupRateCode(Integer id) {
        GroupRateCode rateCode = groupRateCodeRepository.findById(id).orElse(null);
        if (rateCode == null) {
            throw new IllegalArgumentException("集团房价码不存在");
        }
        rateCode.setStatus("inactive");
        return groupRateCodeRepository.save(rateCode);
    }
}
