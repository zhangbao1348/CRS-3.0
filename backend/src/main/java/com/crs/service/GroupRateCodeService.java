package com.crs.service;

import com.crs.entity.GroupRateCode;
import com.crs.entity.RatePlan;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.RatePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 集团房价码服务类
 * 提供集团房价码的业务逻辑操作
 */
@Service
public class GroupRateCodeService {
    
    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;
    
    @Autowired
    private RatePlanRepository ratePlanRepository;
    
    /**
     * 获取所有集团房价码
     * @return 集团房价码列表
     */
    public List<GroupRateCode> getAllGroupRateCodes() {
        return groupRateCodeRepository.findAll();
    }
    
    /**
     * 根据条件筛选集团房价码
     * @param name 房价码名称（可选）
     * @param code 房价码代码（可选）
     * @param rateCategory 房价类别（可选）
     * @param marketCode 市场码（可选）
     * @param sourceCode 来源码（可选）
     * @param type 类型（可选）
     * @param derivativeLevel 衍生层级（可选）
     * @param promotion 促销优惠（可选）
     * @param status 状态（可选）
     * @param rateClass 房价大类（可选）
     * @return 筛选后的集团房价码列表
     */
    public List<GroupRateCode> getGroupRateCodesByConditions(
            Integer groupId,
            String name,
            String code,
            String rateCategory,
            String marketCode,
            String sourceCode,
            String type,
            String derivativeLevel,
            String promotion,
            String status,
            String rateClass) {
        
        List<GroupRateCode> rateCodes;
        if (groupId != null) {
            rateCodes = groupRateCodeRepository.findByGroupId(groupId);
        } else {
            rateCodes = groupRateCodeRepository.findAll();
        }
        
        return rateCodes.stream()
                .filter(rc -> (name == null || name.isEmpty() || 
                    (rc.getRateName() != null && rc.getRateName().contains(name))))
                .filter(rc -> (code == null || code.isEmpty() || 
                    (rc.getRateCode() != null && rc.getRateCode().contains(code))))
                .filter(rc -> (rateCategory == null || rateCategory.isEmpty() || 
                    (rc.getRateCategory() != null && rc.getRateCategory().equals(rateCategory))))
                .filter(rc -> (marketCode == null || marketCode.isEmpty() || 
                    (rc.getMarketCodeId() != null && ("MARKET" + String.format("%02d", rc.getMarketCodeId())).equals(marketCode))))
                .filter(rc -> (sourceCode == null || sourceCode.isEmpty() || 
                    (rc.getSourceCodeId() != null && ("SOURCE" + String.format("%02d", rc.getSourceCodeId())).equals(sourceCode))))
                .filter(rc -> (type == null || type.isEmpty() || 
                    (rc.getRateType() != null && 
                    ((type.equals("基础房价码") && rc.getRateType().equals("basic")) || 
                     (type.equals("一级衍生码") && rc.getRateType().equals("level1")) || 
                     (type.equals("二级衍生码") && rc.getRateType().equals("level2")) || 
                     (type.equals("衍生房价码") && (rc.getRateType().equals("level1") || rc.getRateType().equals("level2") || rc.getRateType().equals("derivative")))))))
                .filter(rc -> (derivativeLevel == null || derivativeLevel.isEmpty() || 
                    (rc.getDerivativeLevel() != null && rc.getDerivativeLevel().equals(derivativeLevel))))
                .filter(rc -> (promotion == null || promotion.isEmpty() || 
                    (rc.getPromotionRule() != null && 
                    ((promotion.equals("不限制") && rc.getPromotionRule().equals("unlimited")) || 
                     (promotion.equals("限制部分优惠") && rc.getPromotionRule().equals("partial")) || 
                     (promotion.equals("不可用优惠") && rc.getPromotionRule().equals("none"))))))
                .filter(rc -> (status == null || status.isEmpty() || 
                    (rc.getStatus() != null && rc.getStatus().equals(status))))
                .filter(rc -> (rateClass == null || rateClass.isEmpty() || 
                    (rc.getRateCategory() != null && 
                    ((rateClass.equals("public") && (rc.getRateCategory().equals("公共价") || rc.getRateCategory().equals("public") || rc.getRateCategory().equals("RACK"))) || 
                     (rateClass.equals("agreement") && (rc.getRateCategory().equals("协议价") || rc.getRateCategory().equals("agreement"))) || 
                     (rateClass.equals("team") && (rc.getRateCategory().equals("团队价") || rc.getRateCategory().equals("team"))) || 
                     (rateClass.equals("member") && (rc.getRateCategory().equals("会员价") || rc.getRateCategory().equals("member"))) || 
                     (rateClass.equals("promotion") && (rc.getRateCategory().equals("促销价") || rc.getRateCategory().equals("promotion")))))))
                .collect(Collectors.toList());
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
        
        // 更新所有字段
        existingRateCode.setRateCode(groupRateCode.getRateCode());
        existingRateCode.setRateName(groupRateCode.getRateName());
        existingRateCode.setDescription(groupRateCode.getDescription());
        existingRateCode.setStatus(groupRateCode.getStatus());
        existingRateCode.setRateCategory(groupRateCode.getRateCategory());
        existingRateCode.setMarketCodeId(groupRateCode.getMarketCodeId());
        existingRateCode.setSourceCodeId(groupRateCode.getSourceCodeId());
        existingRateCode.setRateType(groupRateCode.getRateType());
        existingRateCode.setParentRateCodeId(groupRateCode.getParentRateCodeId());
        existingRateCode.setDerivativeLevel(groupRateCode.getDerivativeLevel());
        existingRateCode.setDiscount(groupRateCode.getDiscount());
        existingRateCode.setRounding(groupRateCode.getRounding());
        existingRateCode.setGuaranteeRule(groupRateCode.getGuaranteeRule());
        existingRateCode.setCancellationRule(groupRateCode.getCancellationRule());
        existingRateCode.setCouponRule(groupRateCode.getCouponRule());
        existingRateCode.setPromotionRule(groupRateCode.getPromotionRule());
        existingRateCode.setAllowPoints(groupRateCode.getAllowPoints());
        existingRateCode.setPointsType(groupRateCode.getPointsType());
        existingRateCode.setPointsValue(groupRateCode.getPointsValue());
        existingRateCode.setApplicableRoomTypes(groupRateCode.getApplicableRoomTypes());
        existingRateCode.setPackages(groupRateCode.getPackages());
        // 预订限制
        existingRateCode.setPersonalMembership(groupRateCode.getPersonalMembership());
        existingRateCode.setCompanyMembership(groupRateCode.getCompanyMembership());
        existingRateCode.setAdvanceBookingMin(groupRateCode.getAdvanceBookingMin());
        existingRateCode.setAdvanceBookingMax(groupRateCode.getAdvanceBookingMax());
        existingRateCode.setMinimumStayMin(groupRateCode.getMinimumStayMin());
        existingRateCode.setMinimumStayMax(groupRateCode.getMinimumStayMax());
        existingRateCode.setBookingStartTime(groupRateCode.getBookingStartTime());
        existingRateCode.setBookingEndTime(groupRateCode.getBookingEndTime());
        existingRateCode.setCheckinStartTime(groupRateCode.getCheckinStartTime());
        existingRateCode.setCheckinEndTime(groupRateCode.getCheckinEndTime());
        
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
    
    /**
     * 获取可选的父级房价码列表
     * @param groupId 集团ID
     * @param targetDerivativeLevel 目标衍生层级 (level1/level2)
     * @param excludeId 要排除的房价码ID（编辑时使用）
     * @return 可选的父级房价码列表
     */
    public List<GroupRateCode> getSelectableParentRateCodes(
            Integer groupId, 
            String targetDerivativeLevel,
            Integer excludeId) {
        
        List<GroupRateCode> rateCodes;
        
        // 根据目标衍生层级查询对应的父级房价码（按derivativeLevel过滤）
        if ("basic".equals(targetDerivativeLevel)) {
            // 如果要创建一级衍生房价码，父级是基础房价码 (derivativeLevel = 'basic')
            rateCodes = groupRateCodeRepository.findByGroupIdAndStatusAndDerivativeLevel(groupId, "active", "basic");
        } else if ("level1".equals(targetDerivativeLevel)) {
            // 如果要创建二级衍生房价码，父级是一级衍生房价码 (derivativeLevel = 'level1')
            rateCodes = groupRateCodeRepository.findByGroupIdAndStatusAndDerivativeLevel(groupId, "active", "level1");
        } else {
            // 默认返回所有启用的房价码
            rateCodes = groupRateCodeRepository.findByGroupIdAndStatus(groupId, "active");
        }
        
        // 过滤掉要排除的ID
        if (excludeId != null) {
            rateCodes = rateCodes.stream()
                .filter(rc -> !rc.getId().equals(excludeId))
                .collect(Collectors.toList());
        }
        
        return rateCodes;
    }
    
    /**
     * 统计指定房价码的子衍生码数量
     * @param parentRateCodeId 父级房价码ID
     * @return 子衍生码数量
     */
    public long countChildDerivatives(Integer parentRateCodeId) {
        return groupRateCodeRepository.countByParentRateCodeId(parentRateCodeId);
    }
    
    /**
     * 获取指定房价码的子衍生码列表
     * @param parentRateCodeId 父级房价码ID
     * @return 子衍生码列表
     */
    public List<GroupRateCode> getChildDerivatives(Integer parentRateCodeId) {
        return groupRateCodeRepository.findByParentRateCodeId(parentRateCodeId);
    }
    
    /**
     * 递归停用集团房价码及其所有子衍生码
     * 同时停用所有已下发到酒店的价格计划
     * @param id 集团房价码ID
     * @return 停用后的集团房价码对象
     */
    @Transactional
    public GroupRateCode disableGroupRateCodeCascade(Integer id) {
        GroupRateCode rateCode = groupRateCodeRepository.findById(id).orElse(null);
        if (rateCode == null) {
            throw new IllegalArgumentException("集团房价码不存在");
        }
        rateCode.setStatus("inactive");
        groupRateCodeRepository.save(rateCode);
        
        // 停用所有已下发到酒店的价格计划
        List<RatePlan> hotelRatePlans = ratePlanRepository.findBySourceGroupRateCodeId(id);
        for (RatePlan plan : hotelRatePlans) {
            if ("active".equals(plan.getStatus())) {
                plan.setStatus("inactive");
                ratePlanRepository.save(plan);
            }
        }
        
        // 递归停用所有子衍生码
        List<GroupRateCode> children = groupRateCodeRepository.findByParentRateCodeId(id);
        for (GroupRateCode child : children) {
            if ("active".equals(child.getStatus())) {
                disableGroupRateCodeCascade(child.getId());
            }
        }
        
        return rateCode;
    }
}
