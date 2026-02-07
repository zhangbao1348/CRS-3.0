package com.crs.service;

import com.crs.entity.ChannelCode;
import com.crs.repository.ChannelCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 渠道码服务类
 * 用于处理渠道码相关的业务逻辑
 */
@Service
public class ChannelCodeService {
    
    private final ChannelCodeRepository channelCodeRepository;
    
    public ChannelCodeService(ChannelCodeRepository channelCodeRepository) {
        this.channelCodeRepository = channelCodeRepository;
    }
    
    /**
     * 获取所有渠道码列表
     * @return 渠道码列表
     */
    public List<ChannelCode> getAllChannelCodes() {
        return channelCodeRepository.findAll();
    }
    
    /**
     * 根据ID获取渠道码详情
     * @param id 渠道码ID
     * @return 渠道码详情
     */
    public Optional<ChannelCode> getChannelCodeById(Integer id) {
        return channelCodeRepository.findById(id);
    }
    
    /**
     * 根据代码获取渠道码详情
     * @param code 渠道码代码
     * @return 渠道码详情
     */
    public Optional<ChannelCode> getChannelCodeByCode(String code) {
        return channelCodeRepository.findByCode(code);
    }
    
    /**
     * 根据名称搜索渠道码
     * @param name 渠道码名称
     * @return 渠道码列表
     */
    public List<ChannelCode> searchChannelCodesByName(String name) {
        return channelCodeRepository.findByNameContaining(name);
    }
    
    /**
     * 根据状态获取渠道码列表
     * @param status 状态
     * @return 渠道码列表
     */
    public List<ChannelCode> getChannelCodesByStatus(ChannelCode.Status status) {
        return channelCodeRepository.findByStatus(status);
    }
    
    /**
     * 创建渠道码
     * @param channelCode 渠道码信息
     * @return 创建的渠道码信息
     */
    public ChannelCode createChannelCode(ChannelCode channelCode) {
        // 检查代码是否已存在
        if (channelCodeRepository.existsByCode(channelCode.getCode())) {
            throw new RuntimeException("Channel code already exists");
        }
        
        return channelCodeRepository.save(channelCode);
    }
    
    /**
     * 更新渠道码
     * @param id 渠道码ID
     * @param channelCode 渠道码信息
     * @return 更新后的渠道码信息
     */
    public ChannelCode updateChannelCode(Integer id, ChannelCode channelCode) {
        ChannelCode existingChannelCode = channelCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel code not found"));
        
        // 检查代码是否已存在（如果代码有变化）
        if (!existingChannelCode.getCode().equals(channelCode.getCode()) && 
            channelCodeRepository.existsByCode(channelCode.getCode())) {
            throw new RuntimeException("Channel code already exists");
        }
        
        existingChannelCode.setCode(channelCode.getCode());
        existingChannelCode.setName(channelCode.getName());
        existingChannelCode.setDescription(channelCode.getDescription());
        existingChannelCode.setStatus(channelCode.getStatus());
        
        return channelCodeRepository.save(existingChannelCode);
    }
    
    /**
     * 删除渠道码
     * @param id 渠道码ID
     */
    public void deleteChannelCode(Integer id) {
        if (!channelCodeRepository.existsById(id)) {
            throw new RuntimeException("Channel code not found");
        }
        
        channelCodeRepository.deleteById(id);
    }
    
    /**
     * 检查渠道码是否存在
     * @param code 渠道码代码
     * @return 是否存在
     */
    public boolean existsByCode(String code) {
        return channelCodeRepository.existsByCode(code);
    }
    
    /**
     * 批量创建渠道码
     * @param channelCodes 渠道码列表
     * @return 创建的渠道码列表
     */
    public List<ChannelCode> createBatchChannelCodes(List<ChannelCode> channelCodes) {
        // 检查代码是否已存在
        for (ChannelCode channelCode : channelCodes) {
            if (channelCodeRepository.existsByCode(channelCode.getCode())) {
                throw new RuntimeException("Channel code already exists: " + channelCode.getCode());
            }
        }
        return channelCodeRepository.saveAll(channelCodes);
    }
    
    /**
     * 批量更新渠道码状态
     * @param ids 渠道码ID列表
     * @param status 状态
     * @return 更新的渠道码数量
     */
    public int batchUpdateChannelCodeStatus(List<Integer> ids, ChannelCode.Status status) {
        List<ChannelCode> channelCodes = channelCodeRepository.findAllById(ids);
        channelCodes.forEach(channelCode -> {
            channelCode.setStatus(status);
        });
        channelCodeRepository.saveAll(channelCodes);
        return channelCodes.size();
    }
    
    /**
     * 获取活跃的渠道码列表
     * @return 活跃的渠道码列表
     */
    public List<ChannelCode> getActiveChannelCodes() {
        return channelCodeRepository.findByStatus(ChannelCode.Status.active);
    }
    
    /**
     * 导入渠道码
     * @param channelCodes 渠道码列表
     * @return 导入结果
     */
    public String importChannelCodes(List<ChannelCode> channelCodes) {
        int successCount = 0;
        int failedCount = 0;
        StringBuilder failedReasons = new StringBuilder();
        
        for (ChannelCode channelCode : channelCodes) {
            try {
                if (!channelCodeRepository.existsByCode(channelCode.getCode())) {
                    channelCodeRepository.save(channelCode);
                    successCount++;
                } else {
                    failedCount++;
                    failedReasons.append("Channel code already exists: " + channelCode.getCode() + "\n");
                }
            } catch (Exception e) {
                failedCount++;
                failedReasons.append("Failed to import channel code " + channelCode.getCode() + ": " + e.getMessage() + "\n");
            }
        }
        
        return "Imported " + successCount + " channel codes, failed " + failedCount + "\n" + failedReasons.toString();
    }
}

