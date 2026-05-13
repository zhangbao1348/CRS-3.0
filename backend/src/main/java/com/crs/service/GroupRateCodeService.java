package com.crs.service;

import com.crs.entity.GroupRateCode;
import com.crs.entity.RatePlan;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.util.TenantContext;
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
        return groupRateCodeRepository.findByGroupId(getCurrentTenantId());
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
        
        // 强制使用当前租户上下文
        List<GroupRateCode> rateCodes = groupRateCodeRepository.findByGroupId(getCurrentTenantId());
        
        return rateCodes.stream()
                .filter(rc -> (name == null || name.isEmpty() || 
                    (rc.getRateName() != null && rc.getRateName().contains(name))))
                .filter(rc -> (code == null || code.isEmpty() || 
                    (rc.getRateCode() != null && rc.getRateCode().contains(code))))
                .filter(rc -> (rateCategory == null || rateCategory.isEmpty() || 
                    (rc.getRateCategory() != null && rc.getRateCategory().equals(rateCategory))))
                .filter(rc -> (marketCode == null || marketCode.isEmpty() || 
                    (rc.getMarketCode() != null && rc.getMarketCode().equals(marketCode))))
                .filter(rc -> (sourceCode == null || sourceCode.isEmpty() || 
                    (rc.getSourceCode() != null && rc.getSourceCode().equals(sourceCode))))
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
    /**
     * 根据ID获取集团房价码（带租户隔离校验）
     * @param id 集团房价码ID
     * @return 集团房价码对象
     */
    public GroupRateCode getGroupRateCodeById(Integer id) {
        return groupRateCodeRepository.findById(id)
                .filter(rc -> rc.getGroupId() != null && rc.getGroupId().equals(getCurrentTenantId()))
                .orElse(null);
    }
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 根据集团ID获取集团房价码列表
     * @param groupId 集团ID
     * @return 集团房价码列表
     */
    public List<GroupRateCode> getGroupRateCodesByGroupId(Integer groupId) {
        // 外部传入 groupId 时，通常需要二次校验当前租户是否有权访问该 groupId
        // 此处简化处理：优先使用当前上下文租户
        return groupRateCodeRepository.findByGroupId(getCurrentTenantId());
    }
    
    /**
     * 根据房价码代码获取集团房价码
     * @param rateCode 房价码代码
     * @return 集团房价码对象
     */
    public GroupRateCode getGroupRateCodeByRateCode(String rateCode) {
        return groupRateCodeRepository.findByRateCodeAndGroupId(rateCode, getCurrentTenantId());
    }
    
    /**
     * 创建集团房价码
     * @param groupRateCode 集团房价码对象
     * @return 创建的集团房价码对象
     */
    @Transactional
    public GroupRateCode createGroupRateCode(GroupRateCode groupRateCode) {
        Integer tenantId = getCurrentTenantId();
        groupRateCode.setGroupId(tenantId);
        
        if (groupRateCodeRepository.findByRateCodeAndGroupId(groupRateCode.getRateCode(), tenantId) != null) {
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
        GroupRateCode existingRateCode = getGroupRateCodeById(id);
        if (existingRateCode == null) {
            throw new IllegalArgumentException("集团房价码不存在或无权访问");
        }
        
        // 检查房价码代码是否已被其他记录使用
        GroupRateCode existingByRateCode = groupRateCodeRepository.findByRateCodeAndGroupId(groupRateCode.getRateCode(), existingRateCode.getGroupId());
        if (existingByRateCode != null && !existingByRateCode.getId().equals(id)) {
            throw new IllegalArgumentException("房价码代码已存在");
        }
        
        // 更新所有字段
        existingRateCode.setRateCode(groupRateCode.getRateCode());
        existingRateCode.setRateName(groupRateCode.getRateName());
        existingRateCode.setDescription(groupRateCode.getDescription());
        existingRateCode.setStatus(groupRateCode.getStatus());
        existingRateCode.setRateCategory(groupRateCode.getRateCategory());
        existingRateCode.setMarketCode(groupRateCode.getMarketCode());
        existingRateCode.setSourceCode(groupRateCode.getSourceCode());
        existingRateCode.setRateType(groupRateCode.getRateType());
        existingRateCode.setParentRateCode(groupRateCode.getParentRateCode());
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
        GroupRateCode existing = getGroupRateCodeById(id);
        if (existing == null) {
            throw new IllegalArgumentException("集团房价码不存在或无权访问");
        }
        groupRateCodeRepository.delete(existing);
    }
    
    /**
     * 根据集团ID和状态获取集团房价码列表
     * @param groupId 集团ID
     * @param status 状态
     * @return 集团房价码列表
     */
    public List<GroupRateCode> getGroupRateCodesByGroupIdAndStatus(Integer groupId, String status) {
        return groupRateCodeRepository.findByGroupIdAndStatus(getCurrentTenantId(), status);
    }
    
    /**
     * 启用集团房价码
     * @param id 集团房价码ID
     * @return 启用后的集团房价码对象
     */
    @Transactional
    public GroupRateCode enableGroupRateCode(Integer id) {
        GroupRateCode rateCode = getGroupRateCodeById(id);
        if (rateCode == null) {
            throw new IllegalArgumentException("集团房价码不存在或无权访问");
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
        GroupRateCode rateCode = getGroupRateCodeById(id);
        if (rateCode == null) {
            throw new IllegalArgumentException("集团房价码不存在或无权访问");
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
        Integer tenantId = getCurrentTenantId();
        
        // 根据目标衍生层级查询对应的父级房价码（按derivativeLevel过滤）
        if ("basic".equals(targetDerivativeLevel)) {
            // 如果要创建一级衍生房价码，父级是基础房价码 (derivativeLevel = 'basic')
            rateCodes = groupRateCodeRepository.findByGroupIdAndStatusAndDerivativeLevel(tenantId, "active", "basic");
        } else if ("level1".equals(targetDerivativeLevel)) {
            // 如果要创建二级衍生房价码，父级是一级衍生房价码 (derivativeLevel = 'level1')
            rateCodes = groupRateCodeRepository.findByGroupIdAndStatusAndDerivativeLevel(tenantId, "active", "level1");
        } else {
            // 默认返回所有启用的房价码
            rateCodes = groupRateCodeRepository.findByGroupIdAndStatus(tenantId, "active");
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
    public long countChildDerivatives(String parentRateCode) {
        return groupRateCodeRepository.countByGroupIdAndParentRateCode(getCurrentTenantId(), parentRateCode);
    }
    
    public List<GroupRateCode> getChildDerivatives(String parentRateCode) {
        return groupRateCodeRepository.findByGroupIdAndParentRateCode(getCurrentTenantId(), parentRateCode);
    }
    
    /**
     * 递归停用集团房价码及其所有子衍生码
     * 同时停用所有已下发到酒店的价格计划
     * @param id 集团房价码ID
     * @return 停用后的集团房价码对象
     */
    @Transactional
    public GroupRateCode disableGroupRateCodeCascade(Integer id) {
        GroupRateCode rateCode = getGroupRateCodeById(id);
        if (rateCode == null) {
            throw new IllegalArgumentException("集团房价码不存在或无权访问");
        }
        rateCode.setStatus("inactive");
        groupRateCodeRepository.save(rateCode);
        
        // 停用所有已下发到酒店的价格计划
        List<RatePlan> hotelRatePlans = ratePlanRepository.findByTenantIdAndSourceGroupRateCode(rateCode.getGroupId(), rateCode.getRateCode());
        for (RatePlan plan : hotelRatePlans) {
            if ("active".equals(plan.getStatus())) {
                plan.setStatus("inactive");
                ratePlanRepository.save(plan);
            }
        }
        
        // 递归停用所有子衍生码
        List<GroupRateCode> children = groupRateCodeRepository.findByGroupIdAndParentRateCode(rateCode.getGroupId(), rateCode.getRateCode());
        for (GroupRateCode child : children) {
            if ("active".equals(child.getStatus())) {
                disableGroupRateCodeCascade(child.getId());
            }
        }
        
        return rateCode;
    }
}
