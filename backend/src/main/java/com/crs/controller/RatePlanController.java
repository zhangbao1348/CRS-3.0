package com.crs.controller;

import com.crs.entity.GroupRateCode;
import com.crs.entity.HotelRateCodeAllocation;
import com.crs.entity.Hotel;
import com.crs.entity.RatePlan;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.HotelRateCodeAllocationRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.util.CodeValidator;
import com.crs.util.TenantContext;
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
  
  @Autowired
  private HotelRepository hotelRepository;

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RatePlanController.class);
  
  private Integer getCurrentTenantId() {
      Integer tenantId = TenantContext.getTenantId();
      return tenantId != null ? tenantId : 1;
  }
  
  private boolean validateHotelTenant(String hotelCode) {
      return hotelRepository.findByHotelCodeAndTenantId(hotelCode, getCurrentTenantId()).isPresent();
  }

  private boolean isBlank(String value) {
      return value == null || value.trim().isEmpty();
  }

  private String pickString(String primary, String fallback) {
      return !isBlank(primary) ? primary : fallback;
  }

  private Integer pickInteger(Integer primary, Integer fallback) {
      return primary != null ? primary : fallback;
  }

  private Double pickDouble(Double primary, Double fallback) {
      return primary != null ? primary : fallback;
  }

  private Boolean pickBoolean(Boolean primary, Boolean fallback) {
      return primary != null ? primary : fallback;
  }

  private Map<String, Object> buildRatePlanDetailData(RatePlan ratePlan) {
      GroupRateCode groupRateCode = null;
      if (!isBlank(ratePlan.getSourceGroupRateCode())) {
          groupRateCode = groupRateCodeRepository.findByRateCodeAndGroupId(ratePlan.getSourceGroupRateCode(), getCurrentTenantId());
      }

      Map<String, Object> data = new HashMap<>();
      data.put("id", ratePlan.getId());
      data.put("tenantId", ratePlan.getTenantId());
      data.put("hotelId", ratePlan.getHotelId());
      data.put("hotelCode", ratePlan.getHotelCode());
      data.put("sourceGroupRateCode", ratePlan.getSourceGroupRateCode());

      data.put("rateCode", pickString(ratePlan.getRateCode(), groupRateCode != null ? groupRateCode.getRateCode() : null));
      data.put("rateName", pickString(ratePlan.getRateName(), groupRateCode != null ? groupRateCode.getRateName() : null));
      data.put("description", pickString(ratePlan.getDescription(), groupRateCode != null ? groupRateCode.getDescription() : null));

      data.put("rateCategory", pickString(ratePlan.getRateCategory(), groupRateCode != null ? groupRateCode.getRateCategory() : null));
      data.put("marketCode", pickString(ratePlan.getMarketCode(), groupRateCode != null ? groupRateCode.getMarketCode() : null));
      data.put("sourceCode", pickString(ratePlan.getSourceCode(), groupRateCode != null ? groupRateCode.getSourceCode() : null));

      data.put("rateType", pickString(ratePlan.getRateType(), groupRateCode != null ? groupRateCode.getRateType() : null));
      data.put("parentRateCode", pickString(ratePlan.getParentRateCode(), groupRateCode != null ? groupRateCode.getParentRateCode() : null));
      data.put("derivativeLevel", pickString(ratePlan.getDerivativeLevel(), groupRateCode != null ? groupRateCode.getDerivativeLevel() : null));

      data.put("discount", pickDouble(ratePlan.getDiscount(), groupRateCode != null ? groupRateCode.getDiscount() : null));
      data.put("rounding", pickString(ratePlan.getRounding(), groupRateCode != null ? groupRateCode.getRounding() : null));

      data.put("guaranteeRule", pickString(ratePlan.getGuaranteeRule(), groupRateCode != null ? groupRateCode.getGuaranteeRule() : null));
      data.put("cancellationRule", pickString(ratePlan.getCancellationRule(), groupRateCode != null ? groupRateCode.getCancellationRule() : null));

      data.put("couponRule", pickString(ratePlan.getCouponRule(), groupRateCode != null ? groupRateCode.getCouponRule() : null));
      data.put("promotionRule", pickString(ratePlan.getPromotionRule(), groupRateCode != null ? groupRateCode.getPromotionRule() : null));
      data.put("allowPoints", pickBoolean(ratePlan.getAllowPoints(), groupRateCode != null ? groupRateCode.getAllowPoints() : null));
      data.put("pointsType", pickString(ratePlan.getPointsType(), groupRateCode != null ? groupRateCode.getPointsType() : null));
      data.put("pointsValue", pickDouble(ratePlan.getPointsValue(), groupRateCode != null ? groupRateCode.getPointsValue() : null));

      data.put("applicableRoomTypes", pickString(ratePlan.getApplicableRoomTypes(), groupRateCode != null ? groupRateCode.getApplicableRoomTypes() : null));
      data.put("packages", pickString(ratePlan.getPackages(), groupRateCode != null ? groupRateCode.getPackages() : null));
      data.put("personalMembership", pickString(ratePlan.getPersonalMembership(), groupRateCode != null ? groupRateCode.getPersonalMembership() : null));
      data.put("companyMembership", pickString(ratePlan.getCompanyMembership(), groupRateCode != null ? groupRateCode.getCompanyMembership() : null));

      data.put("advanceBookingMin", pickInteger(ratePlan.getAdvanceBookingMin(), groupRateCode != null ? groupRateCode.getAdvanceBookingMin() : null));
      data.put("advanceBookingMax", pickInteger(ratePlan.getAdvanceBookingMax(), groupRateCode != null ? groupRateCode.getAdvanceBookingMax() : null));
      data.put("minimumStayMin", pickInteger(ratePlan.getMinimumStayMin(), groupRateCode != null ? groupRateCode.getMinimumStayMin() : null));
      data.put("minimumStayMax", pickInteger(ratePlan.getMinimumStayMax(), groupRateCode != null ? groupRateCode.getMinimumStayMax() : null));
      data.put("bookingStartTime", pickString(ratePlan.getBookingStartTime(), groupRateCode != null ? groupRateCode.getBookingStartTime() : null));
      data.put("bookingEndTime", pickString(ratePlan.getBookingEndTime(), groupRateCode != null ? groupRateCode.getBookingEndTime() : null));
      data.put("checkinStartTime", pickString(ratePlan.getCheckinStartTime(), groupRateCode != null ? groupRateCode.getCheckinStartTime() : null));
      data.put("checkinEndTime", pickString(ratePlan.getCheckinEndTime(), groupRateCode != null ? groupRateCode.getCheckinEndTime() : null));

      data.put("roomTypeDiffCode", ratePlan.getRoomTypeDiffCode());
      data.put("personDiffCode", ratePlan.getPersonDiffCode());

      data.put("status", pickString(ratePlan.getStatus(), groupRateCode != null ? groupRateCode.getStatus() : null));
      data.put("createdAt", ratePlan.getCreatedAt());
      data.put("updatedAt", ratePlan.getUpdatedAt());

      return data;
  }
    
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
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<RatePlan> ratePlanOpt = ratePlanRepository.findById(id);
            if (!ratePlanOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "价格计划不存在");
                return ResponseEntity.badRequest().body(response);
            }

            RatePlan ratePlan = ratePlanOpt.get();
            if (!isBlank(ratePlan.getHotelCode()) && !validateHotelTenant(ratePlan.getHotelCode())) {
                response.put("success", false);
                response.put("message", "无权访问该酒店数据");
                return ResponseEntity.status(403).body(response);
            }

            response.put("success", true);
            response.put("data", buildRatePlanDetailData(ratePlan));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取价格计划失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
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
            
            if (ratePlan.getHotelId() == null && ratePlan.getHotelCode() != null && !ratePlan.getHotelCode().isEmpty()) {
                Optional<Hotel> hotelOpt = hotelRepository.findByHotelCodeAndTenantId(ratePlan.getHotelCode(), getCurrentTenantId());
                if (hotelOpt.isEmpty()) {
                    return ResponseEntity.status(403).body(Map.of("error", "无权操作该酒店数据"));
                }
                ratePlan.setHotelId(hotelOpt.get().getId());
            }
            
            if (ratePlan.getHotelId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "缺少酒店信息(hotelId或hotelCode)"));
            }
            
            ratePlan.setTenantId(getCurrentTenantId());
            
            if (ratePlanRepository.existsByHotelIdAndRateCode(ratePlan.getHotelId(), ratePlan.getRateCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", "价格计划代码在该酒店内已存在"));
            }
            if (groupRateCodeRepository.findByRateCodeAndGroupId(ratePlan.getRateCode(), getCurrentTenantId()) != null) {
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
            
            if (ratePlan.getHotelId() == null && ratePlan.getHotelCode() != null && !ratePlan.getHotelCode().isEmpty()) {
                Optional<Hotel> hotelOpt = hotelRepository.findByHotelCodeAndTenantId(ratePlan.getHotelCode(), getCurrentTenantId());
                if (hotelOpt.isPresent()) {
                    ratePlan.setHotelId(hotelOpt.get().getId());
                } else {
                    ratePlan.setHotelId(existingRatePlan.getHotelId());
                }
            }
            
            if (ratePlan.getHotelId() == null) {
                ratePlan.setHotelId(existingRatePlan.getHotelId());
            }
            
            Optional<RatePlan> duplicateOpt = ratePlanRepository.findByHotelIdAndRateCode(
                    ratePlan.getHotelId(), ratePlan.getRateCode());
            if (duplicateOpt.isPresent() && !duplicateOpt.get().getId().equals(id)) {
                return ResponseEntity.badRequest().body("价格计划代码在该酒店内已存在");
            }
            
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
      
      if (ratePlan.getSourceGroupRateCode() == null) {
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

    @GetMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<Map<String, Object>> getRatePlansByHotelCode(
            @PathVariable String hotelCode,
            @RequestParam(required = false) String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!validateHotelTenant(hotelCode)) {
                response.put("success", false);
                response.put("message", "无权访问该酒店数据");
                return ResponseEntity.status(403).body(response);
            }
            List<RatePlan> ratePlans;
            if (status != null) {
                ratePlans = ratePlanRepository.findByHotelCodeAndStatus(hotelCode, status);
            } else {
                ratePlans = ratePlanRepository.findByHotelCode(hotelCode);
            }
            response.put("success", true);
            response.put("data", ratePlans);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/by-code/hotel/{hotelCode}/rate-code/{rateCode}")
    public ResponseEntity<Map<String, Object>> getRatePlanByHotelCodeAndRateCode(
            @PathVariable String hotelCode, @PathVariable String rateCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!validateHotelTenant(hotelCode)) {
                response.put("success", false);
                response.put("message", "无权访问该酒店数据");
                return ResponseEntity.status(403).body(response);
            }
            var ratePlan = ratePlanRepository.findByHotelCodeAndRateCode(hotelCode, rateCode)
                    .orElseThrow(() -> new RuntimeException("Rate plan not found"));
            response.put("success", true);
            response.put("data", buildRatePlanDetailData(ratePlan));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
