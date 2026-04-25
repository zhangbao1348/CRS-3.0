package com.crs.controller;

import com.crs.entity.HotelRateCodeAllocation;
import com.crs.entity.RatePlan;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.HotelRateCodeAllocationRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 价格计划控制器
 * 提供价格计划的RESTful API接口
 */
@RestController
@RequestMapping("/api/rate-plans")
public class RatePlanController {
    
    @Autowired
    private RatePlanRepository ratePlanRepository;
    
    @Autowired
    private HotelRateCodeAllocationRepository hotelRateCodeAllocationRepository;
    
    @Autowired
  private GroupRateCodeRepository groupRateCodeRepository;

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RatePlanController.class);
    
    /**
     * 获取价格计划列表
     * @param hotelId 酒店ID（可选）
     * @param name 价格计划名称（可选）
     * @param code 价格计划代码（可选）
     * @param rateCategory 价格类别（可选）
     * @param status 状态（可选）
     * @return 价格计划列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRatePlans(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String rateCategory,
            @RequestParam(required = false) String status) {
        List<RatePlan> ratePlans;
        
        if (hotelId != null) {
            if (status != null) {
                ratePlans = ratePlanRepository.findByHotelIdAndStatus(hotelId, status);
            } else {
                ratePlans = ratePlanRepository.findByHotelId(hotelId);
            }
        } else {
            ratePlans = ratePlanRepository.findAll();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", ratePlans);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据ID获取价格计划
     * @param id 价格计划ID
     * @return 价格计划对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRatePlanById(@PathVariable Integer id) {
        Optional<RatePlan> ratePlanOpt = ratePlanRepository.findById(id);
        if (ratePlanOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ratePlanOpt.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "价格计划不存在");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 创建价格计划
     * @param ratePlan 价格计划对象
     * @return 创建的价格计划对象
     */
    @PostMapping
    public ResponseEntity<?> createRatePlan(@RequestBody RatePlan ratePlan) {
        try {
            if (ratePlan.getRateCode() != null && !CodeValidator.isValid(ratePlan.getRateCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            // 检查酒店内价格计划代码是否重复
            if (ratePlanRepository.existsByHotelIdAndRateCode(ratePlan.getHotelId(), ratePlan.getRateCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", "价格计划代码在该酒店内已存在"));
            }
            // 检查集团是否已存在相同CODE的房价码
            if (groupRateCodeRepository.findByRateCode(ratePlan.getRateCode()) != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "此房价码集团已经存在，请更换房价码CODE"));
            }
            
            RatePlan createdRatePlan = ratePlanRepository.save(ratePlan);
            return ResponseEntity.ok(createdRatePlan);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建价格计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新价格计划
     * @param id 价格计划ID
     * @param ratePlan 价格计划对象
     * @return 更新后的价格计划对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRatePlan(@PathVariable Integer id, @RequestBody RatePlan ratePlan) {
        try {
            if (ratePlan.getRateCode() != null && !CodeValidator.isValid(ratePlan.getRateCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            Optional<RatePlan> existingOpt = ratePlanRepository.findById(id);
            if (!existingOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            RatePlan existingRatePlan = existingOpt.get();
            
            // 检查价格计划代码是否与其他记录重复
            Optional<RatePlan> duplicateOpt = ratePlanRepository.findByHotelIdAndRateCode(
                    ratePlan.getHotelId(), ratePlan.getRateCode());
            if (duplicateOpt.isPresent() && !duplicateOpt.get().getId().equals(id)) {
                return ResponseEntity.badRequest().body("价格计划代码在该酒店内已存在");
            }
            
            // 更新字段
            ratePlan.setId(id);
            ratePlan.setCreatedAt(existingRatePlan.getCreatedAt());
            
            RatePlan updatedRatePlan = ratePlanRepository.save(ratePlan);
            return ResponseEntity.ok(updatedRatePlan);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新价格计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除价格计划
     * @param id 价格计划ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRatePlan(@PathVariable Integer id) {
        try {
            if (!ratePlanRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            ratePlanRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除价格计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 启用价格计划
     * @param id 价格计划ID
     * @return 启用后的价格计划对象
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableRatePlan(@PathVariable Integer id) {
        try {
            Optional<RatePlan> ratePlanOpt = ratePlanRepository.findById(id);
            if (!ratePlanOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            RatePlan ratePlan = ratePlanOpt.get();
            ratePlan.setStatus("active");
            RatePlan updatedRatePlan = ratePlanRepository.save(ratePlan);
            
            return ResponseEntity.ok(updatedRatePlan);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("启用价格计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 停用价格计划
     * @param id 价格计划ID
     * @return 停用后的价格计划对象
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableRatePlan(@PathVariable Integer id) {
        try {
            Optional<RatePlan> ratePlanOpt = ratePlanRepository.findById(id);
            if (!ratePlanOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            RatePlan ratePlan = ratePlanOpt.get();
            ratePlan.setStatus("inactive");
            RatePlan updatedRatePlan = ratePlanRepository.save(ratePlan);
            
            return ResponseEntity.ok(updatedRatePlan);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("停用价格计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查价格计划代码是否唯一
     * @param code 价格计划代码
     * @param hotelId 酒店ID
     * @param excludeId 排除的价格计划ID（可选）
     * @return 检查结果
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Object>> checkRateCodeUnique(
            @RequestParam String code,
            @RequestParam Integer hotelId,
            @RequestParam(required = false) Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists;
            if (id != null) {
                exists = ratePlanRepository.existsByHotelIdAndRateCodeAndIdNot(hotelId, code, id);
            } else {
                exists = ratePlanRepository.existsByHotelIdAndRateCode(hotelId, code);
            }
            response.put("success", true);
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "检查价格计划代码失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
   * 获取价格计划的权限信息
   * 如果是集团下发的价格计划，返回对应的权限开关
   * 如果是酒店自建的价格计划，返回所有权限为true
   * @param id 价格计划ID
   * @return 权限信息
   */
  @GetMapping("/{id}/permissions")
  public ResponseEntity<Map<String, Object>> getRatePlanPermissions(@PathVariable Integer id) {
    Map<String, Object> response = new HashMap<>();
    try {
      Optional<RatePlan> ratePlanOpt = ratePlanRepository.findById(id);
      if (!ratePlanOpt.isPresent()) {
        response.put("success", false);
        response.put("message", "价格计划不存在");
        return ResponseEntity.badRequest().body(response);
      }
      
      RatePlan ratePlan = ratePlanOpt.get();
      Map<String, Object> permissions = new HashMap<>();
      
      if (ratePlan.getSourceGroupRateCodeId() == null) {
        // 酒店自建的价格计划，所有权限为true
        permissions.put("basicInfoEditable", true);
        permissions.put("priceInfoEditable", true);
        permissions.put("bookingLimitEditable", true);
        permissions.put("guaranteeRuleEditable", true);
        permissions.put("promotionEditable", true);
        permissions.put("isGroupDistributed", false);
      } else {
        // 集团下发的价格计划，查找分配记录
        HotelRateCodeAllocation allocation = hotelRateCodeAllocationRepository
            .findByHotelCodeAndRateCode(ratePlan.getHotelCode(), ratePlan.getRateCode());
        
        if (allocation != null) {
          permissions.put("basicInfoEditable", allocation.getBasicInfoEditable());
          permissions.put("priceInfoEditable", allocation.getPriceInfoEditable());
          permissions.put("bookingLimitEditable", allocation.getBookingLimitEditable());
          permissions.put("guaranteeRuleEditable", allocation.getGuaranteeRuleEditable());
          permissions.put("promotionEditable", allocation.getPromotionEditable());
        } else {
          // 找不到分配记录，默认不可编辑
          permissions.put("basicInfoEditable", false);
          permissions.put("priceInfoEditable", false);
          permissions.put("bookingLimitEditable", false);
          permissions.put("guaranteeRuleEditable", false);
          permissions.put("promotionEditable", false);
        }
        permissions.put("isGroupDistributed", true);
      }
      
      response.put("success", true);
      response.put("data", permissions);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "获取权限信息失败: " + e.getMessage());
      return ResponseEntity.internalServerError().body(response);
    }
  }

  /**
   * 获取可选的父级价格计划列表（用于衍生价格计划）
   * @param targetDerivativeLevel 目标衍生级别（可选）
   * @param excludeId 要排除的价格计划ID（可选）
   * @return 父级价格计划列表
   */
  @GetMapping("/selectable-parents")
  public ResponseEntity<List<RatePlan>> getSelectableParentRateCodes(
      @RequestParam(required = false) String targetDerivativeLevel,
      @RequestParam(required = false) Integer excludeId) {
    try {
      // 只获取基础价格计划作为父级
      List<RatePlan> parentRatePlans = ratePlanRepository.findAll().stream()
          .filter(rp -> "basic".equals(rp.getRateType()))
          .filter(rp -> !rp.getId().equals(excludeId))
          .collect(java.util.stream.Collectors.toList());
      
      return ResponseEntity.ok(parentRatePlans);
    } catch (Exception e) {
      logger.error("获取可选父级价格计划失败", e);
      return ResponseEntity.status(500).body(null);
    }
  }
}
