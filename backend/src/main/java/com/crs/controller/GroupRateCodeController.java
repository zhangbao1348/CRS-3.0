package com.crs.controller;

import com.crs.entity.GroupRateCode;
import com.crs.entity.Hotel;
import com.crs.entity.HotelPrice;
import com.crs.entity.HotelRateCodeAllocation;
import com.crs.entity.HotelRoomType;
import com.crs.entity.RatePlan;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.HotelPriceRepository;
import com.crs.repository.HotelRateCodeAllocationRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.service.GroupRateCodeService;
import com.crs.util.CodeValidator;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 集团房价码控制器
 * 提供集团房价码的RESTful API接口
 */
@RestController
@RequestMapping("/api/group-rate-codes")
public class GroupRateCodeController {

    @Autowired
    private GroupRateCodeService groupRateCodeService;

    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RatePlanRepository ratePlanRepository;

    @Autowired
    private HotelRateCodeAllocationRepository allocationRepository;

    @Autowired
    private HotelPriceRepository hotelPriceRepository;

    @Autowired
    private HotelRoomTypeRepository hotelRoomTypeRepository;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GroupRateCodeController.class);

    /**
     * 获取当前登录用户的租户ID
     * 
     * @return 租户ID
     */
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        logger.info("getCurrentTenantId() - TenantContext.getTenantId(): {}", tenantId);
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        logger.info("getCurrentTenantId() - 返回租户 ID: {}", tenantId);
        return tenantId;
    }

    /**
     * 获取所有集团房价码
     * 
     * @param name            房价码名称（可选）
     * @param code            房价码代码（可选）
     * @param rateCategory    房价类别（可选）
     * @param marketCode      市场码（可选）
     * @param sourceCode      来源码（可选）
     * @param type            类型（可选）
     * @param derivativeLevel 衍生层级（可选）
     * @param promotion       促销优惠（可选）
     * @param status          状态（可选）
     * @return 集团房价码列表
     */
    @GetMapping
    public ResponseEntity<List<GroupRateCode>> getAllGroupRateCodes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String rateCategory,
            @RequestParam(required = false) String marketCode,
            @RequestParam(required = false) String sourceCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String derivativeLevel,
            @RequestParam(required = false) String promotion,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String rateClass) {
        Integer tenantId = getCurrentTenantId();
        List<GroupRateCode> rateCodes = groupRateCodeService.getGroupRateCodesByConditions(
                tenantId, name, code, rateCategory, marketCode, sourceCode, type, derivativeLevel, promotion, status,
                rateClass);
        return ResponseEntity.ok(rateCodes);
    }

    /**
     * 根据ID获取集团房价码
     * 
     * @param id 集团房价码ID
     * @return 集团房价码对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupRateCode> getGroupRateCodeById(@PathVariable Integer id) {
        GroupRateCode rateCode = groupRateCodeService.getGroupRateCodeById(id);
        if (rateCode == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rateCode);
    }

    /**
     * 根据集团ID获取集团房价码列表
     * 
     * @param groupId 集团ID
     * @return 集团房价码列表
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GroupRateCode>> getGroupRateCodesByGroupId(@PathVariable Integer groupId) {
        List<GroupRateCode> rateCodes = groupRateCodeService.getGroupRateCodesByGroupId(groupId);
        return ResponseEntity.ok(rateCodes);
    }

    /**
     * 根据房价码代码获取集团房价码
     * 
     * @param rateCode 房价码代码
     * @return 集团房价码对象
     */
    @GetMapping("/code/{rateCode}")
    public ResponseEntity<GroupRateCode> getGroupRateCodeByRateCode(@PathVariable String rateCode) {
        GroupRateCode rateCodeObj = groupRateCodeService.getGroupRateCodeByRateCode(rateCode);
        if (rateCodeObj == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rateCodeObj);
    }

    /**
     * 创建集团房价码
     * 
     * @param groupRateCode 集团房价码对象
     * @return 创建的集团房价码对象
     */
    @PostMapping
    public ResponseEntity<?> createGroupRateCode(@RequestBody GroupRateCode groupRateCode) {
        try {
            if (groupRateCode.getRateCode() != null && !CodeValidator.isValid(groupRateCode.getRateCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            // 衍生码必填校验
            String rateType = groupRateCode.getRateType();
            if (rateType != null && !"basic".equals(rateType)) {
                if (groupRateCode.getParentRateCode() == null || groupRateCode.getParentRateCode().isBlank()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "衍生房价码必须选择父级房价码"));
                }
                if (groupRateCode.getDiscount() == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "衍生房价码必须填写折扣"));
                }
            }
            // 适用房型必选校验
            if (groupRateCode.getApplicableRoomTypes() == null || "[]".equals(groupRateCode.getApplicableRoomTypes())
                    || groupRateCode.getApplicableRoomTypes().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "请至少选择一个适用房型"));
            }
            GroupRateCode createdRateCode = groupRateCodeService.createGroupRateCode(groupRateCode);
            return ResponseEntity.ok(createdRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建集团房价码失败: " + e.getMessage());
        }
    }

    /**
     * 更新集团房价码
     * 
     * @param id            集团房价码ID
     * @param groupRateCode 集团房价码对象
     * @return 更新后的集团房价码对象，包含同步信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroupRateCode(@PathVariable Integer id, @RequestBody GroupRateCode groupRateCode) {
        try {
            if (groupRateCode.getRateCode() != null && !CodeValidator.isValid(groupRateCode.getRateCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            // 衍生码必填校验
            String rateType = groupRateCode.getRateType();
            if (rateType != null && !"basic".equals(rateType)) {
                if (groupRateCode.getParentRateCode() == null || groupRateCode.getParentRateCode().isBlank()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "衍生房价码必须选择父级房价码"));
                }
                if (groupRateCode.getDiscount() == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "衍生房价码必须填写折扣"));
                }
            }
            // 适用房型必选校验
            if (groupRateCode.getApplicableRoomTypes() == null || "[]".equals(groupRateCode.getApplicableRoomTypes())
                    || groupRateCode.getApplicableRoomTypes().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "请至少选择一个适用房型"));
            }
            GroupRateCode updatedRateCode = groupRateCodeService.updateGroupRateCode(id, groupRateCode);
            Integer tenantIdForSync = getCurrentTenantId();

            // 检查是否已下发到酒店，返回同步信息
            List<RatePlan> distributedPlans = ratePlanRepository
                    .findByTenantIdAndSourceGroupRateCode(tenantIdForSync, updatedRateCode.getRateCode());
            List<RatePlan> activePlans = distributedPlans.stream()
                    .filter(rp -> "active".equals(rp.getStatus()))
                    .collect(Collectors.toList());

            if (activePlans.isEmpty()) {
                return ResponseEntity.ok(updatedRateCode);
            }

            // 比较差异
            List<Map<String, Object>> affectedHotels = new ArrayList<>();
            for (RatePlan plan : activePlans) {
                List<String> diffFields = compareRateCodeWithPlan(updatedRateCode, plan);
                if (!diffFields.isEmpty()) {
                    Map<String, Object> hotelInfo = new HashMap<>();
                    // 使用 hotelCode 作为关联条件，符合CODE关联规范
                    String planHotelCode = plan.getHotelCode();
                    hotelInfo.put("hotelCode", planHotelCode);
                    Hotel hotel = (planHotelCode != null)
                            ? hotelRepository.findByHotelCodeAndTenantId(planHotelCode, tenantIdForSync).orElse(null)
                            : null;
                    hotelInfo.put("hotelName", hotel != null ? hotel.getChineseName() : "未知酒店");
                    hotelInfo.put("diffFields", diffFields);
                    affectedHotels.add(hotelInfo);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", updatedRateCode);
            response.put("syncRequired", !affectedHotels.isEmpty());
            response.put("affectedHotels", affectedHotels);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新集团房价码失败: " + e.getMessage());
        }
    }

    /**
     * 同步集团房价码到指定酒店
     * 
     * @param id      集团房价码ID
     * @param request 包含hotelIds的请求体
     * @return 操作结果
     */
    @PostMapping("/{id}/sync-to-hotels")
    @Transactional
    public ResponseEntity<?> syncToHotels(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            GroupRateCode groupRateCode = groupRateCodeService.getGroupRateCodeById(id);
            if (groupRateCode == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "集团房价码不存在"));
            }

            // 关联查询原则：使用 hotelCode 列表而非 hotelId 列表
            @SuppressWarnings("unchecked")
            List<String> hotelCodes = (List<String>) request.get("hotelCodes");
            
            if (hotelCodes == null || hotelCodes.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "请选择要同步的酒店"));
            }
            final List<String> finalHotelCodes = hotelCodes;
            Integer tenantId = getCurrentTenantId();

            int syncCount = 0;

            // 检查是否是衍生码且折扣/取整方式发生了变化
            String derivativeLevel = groupRateCode.getDerivativeLevel();
            boolean isDerivative = "level1".equals(derivativeLevel) || "level2".equals(derivativeLevel);

            for (String hotelCode : finalHotelCodes) {
                // 优先使用联合属性定位价格计划，确保各酒店下对应房价码能正确匹配，即使历史同步数据缺少关联标识
                Optional<RatePlan> planOpt = ratePlanRepository
                        .findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, groupRateCode.getRateCode());
                if (planOpt.isPresent()) {
                    RatePlan plan = planOpt.get();
                    if ("active".equals(plan.getStatus())) {
                        // 记录同步前的折扣和取整方式
                        Double oldDiscount = plan.getDiscount();
                        String oldRounding = plan.getRounding();

                        syncRatePlanFromGroupRateCode(plan, groupRateCode);
                        ratePlanRepository.save(plan);
                        syncCount++;

                        // 如果是衍生码且折扣或取整方式变了，重新计算该酒店的衍生价格
                        if (isDerivative && groupRateCode.getParentRateCode() != null
                                && (!Objects.equals(oldDiscount, plan.getDiscount())
                                        || !Objects.equals(oldRounding, plan.getRounding()))) {
                            recalculateDerivativePricesForHotel(tenantId, hotelCode, groupRateCode);
                            logger.info("同步时重新计算衍生价格: 酒店={}, 房价码={}, 折扣 {}->{}",
                                    hotelCode, groupRateCode.getRateCode(), oldDiscount, plan.getDiscount());
                        }
                    }
                }
            }

            return ResponseEntity.ok(Map.of("message", "同步成功，已更新 " + syncCount + " 个酒店的价格计划"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "同步失败: " + e.getMessage()));
        }
    }

    /**
     * 比较集团房价码与酒店价格计划的差异字段
     */
    private List<String> compareRateCodeWithPlan(GroupRateCode rateCode, RatePlan plan) {
        List<String> diffFields = new ArrayList<>();
        if (!Objects.equals(rateCode.getRateName(), plan.getRateName()))
            diffFields.add("rateName");
        if (!Objects.equals(rateCode.getRateCategory(), plan.getRateCategory()))
            diffFields.add("rateCategory");
        if (!Objects.equals(rateCode.getMarketCode(), plan.getMarketCode()))
            diffFields.add("marketCode");
        if (!Objects.equals(rateCode.getSourceCode(), plan.getSourceCode()))
            diffFields.add("sourceCode");
        if (!Objects.equals(rateCode.getGuaranteeRule(), plan.getGuaranteeRule()))
            diffFields.add("guaranteeRule");
        if (!Objects.equals(rateCode.getCancellationRule(), plan.getCancellationRule()))
            diffFields.add("cancellationRule");
        if (!Objects.equals(rateCode.getDiscount(), plan.getDiscount()))
            diffFields.add("discount");
        if (!Objects.equals(rateCode.getRounding(), plan.getRounding()))
            diffFields.add("rounding");
        if (!Objects.equals(rateCode.getCouponRule(), plan.getCouponRule()))
            diffFields.add("couponRule");
        if (!Objects.equals(rateCode.getPromotionRule(), plan.getPromotionRule()))
            diffFields.add("promotionRule");
        if (!Objects.equals(rateCode.getAllowPoints(), plan.getAllowPoints()))
            diffFields.add("allowPoints");
        if (!Objects.equals(rateCode.getPointsType(), plan.getPointsType()))
            diffFields.add("pointsType");
        if (!Objects.equals(rateCode.getPointsValue(), plan.getPointsValue()))
            diffFields.add("pointsValue");
        if (!Objects.equals(rateCode.getApplicableRoomTypes(), plan.getApplicableRoomTypes()))
            diffFields.add("applicableRoomTypes");
        if (!Objects.equals(rateCode.getPackages(), plan.getPackages()))
            diffFields.add("packages");
        if (!Objects.equals(rateCode.getDescription(), plan.getDescription()))
            diffFields.add("description");
        if (!Objects.equals(rateCode.getAdvanceBookingMin(), plan.getAdvanceBookingMin()))
            diffFields.add("advanceBookingMin");
        if (!Objects.equals(rateCode.getAdvanceBookingMax(), plan.getAdvanceBookingMax()))
            diffFields.add("advanceBookingMax");
        if (!Objects.equals(rateCode.getMinimumStayMin(), plan.getMinimumStayMin()))
            diffFields.add("minimumStayMin");
        if (!Objects.equals(rateCode.getMinimumStayMax(), plan.getMinimumStayMax()))
            diffFields.add("minimumStayMax");
        return diffFields;
    }

    /**
     * 将集团房价码数据同步到酒店价格计划
     */
    private void syncRatePlanFromGroupRateCode(RatePlan plan, GroupRateCode rateCode) {
        plan.setRateCode(rateCode.getRateCode());
        plan.setRateName(rateCode.getRateName());
        plan.setDescription(rateCode.getDescription());
        plan.setRateCategory(rateCode.getRateCategory());
        plan.setMarketCode(rateCode.getMarketCode());
        plan.setSourceCode(rateCode.getSourceCode());
        plan.setRateType(rateCode.getRateType());
        plan.setParentRateCode(rateCode.getParentRateCode());
        plan.setDerivativeLevel(rateCode.getDerivativeLevel());
        plan.setDiscount(rateCode.getDiscount());
        plan.setRounding(rateCode.getRounding());
        plan.setGuaranteeRule(rateCode.getGuaranteeRule());
        plan.setCancellationRule(rateCode.getCancellationRule());
        plan.setCouponRule(rateCode.getCouponRule());
        plan.setPromotionRule(rateCode.getPromotionRule());
        plan.setAllowPoints(rateCode.getAllowPoints());
        plan.setPointsType(rateCode.getPointsType());
        plan.setPointsValue(rateCode.getPointsValue());
        plan.setApplicableRoomTypes(rateCode.getApplicableRoomTypes());
        plan.setPackages(rateCode.getPackages());
        plan.setPersonalMembership(rateCode.getPersonalMembership());
        plan.setCompanyMembership(rateCode.getCompanyMembership());
        plan.setAdvanceBookingMin(rateCode.getAdvanceBookingMin());
        plan.setAdvanceBookingMax(rateCode.getAdvanceBookingMax());
        plan.setMinimumStayMin(rateCode.getMinimumStayMin());
        plan.setMinimumStayMax(rateCode.getMinimumStayMax());
        plan.setBookingStartTime(rateCode.getBookingStartTime());
        plan.setBookingEndTime(rateCode.getBookingEndTime());
        plan.setCheckinStartTime(rateCode.getCheckinStartTime());
        plan.setCheckinEndTime(rateCode.getCheckinEndTime());
        plan.setSourceGroupRateCode(rateCode.getRateCode());
    }

    /**
     * 重新计算某个酒店的衍生房价码价格（折扣/取整方式变更时调用）
     * 查找父级房价码在该酒店的所有价格，按新的折扣和取整方式重新计算
     */
    private void recalculateDerivativePricesForHotel(Integer tenantId, String hotelCode,
            GroupRateCode derivativeRateCode) {
        try {
            GroupRateCode parentRateCode = groupRateCodeRepository
                    .findByRateCodeAndGroupId(derivativeRateCode.getParentRateCode(), tenantId);
            if (parentRateCode == null)
                return;

            String parentRateCodeValue = parentRateCode.getRateCode();
            String derivativeRateCodeValue = derivativeRateCode.getRateCode();
            Double discount = derivativeRateCode.getDiscount();
            String rounding = derivativeRateCode.getRounding();

            if (discount == null || discount <= 0)
                return;

            List<HotelPrice> parentPrices = hotelPriceRepository
                    .findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, parentRateCodeValue)
                    .stream()
                    .filter(p -> "active".equals(p.getStatus()) && p.getPriceWithTax() != null)
                    .collect(java.util.stream.Collectors.toList());

            if (parentPrices.isEmpty())
                return;

            java.math.BigDecimal discountRate = java.math.BigDecimal.valueOf(discount)
                    .divide(java.math.BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);

            for (HotelPrice parentPrice : parentPrices) {
                java.math.BigDecimal derivativeAmount = parentPrice.getPriceWithTax().multiply(discountRate);

                if ("floor".equals(rounding)) {
                    derivativeAmount = derivativeAmount.setScale(0, java.math.RoundingMode.FLOOR);
                } else if ("ceil".equals(rounding)) {
                    derivativeAmount = derivativeAmount.setScale(0, java.math.RoundingMode.CEILING);
                } else {
                    derivativeAmount = derivativeAmount.setScale(2, java.math.RoundingMode.HALF_UP);
                }

                Optional<HotelPrice> existingOpt = hotelPriceRepository
                        .findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDate(
                                tenantId, hotelCode, derivativeRateCodeValue,
                                parentPrice.getRoomTypeCode(), parentPrice.getPriceDate());

                HotelPrice derivativePrice;
                if (existingOpt.isPresent()) {
                    derivativePrice = existingOpt.get();
                    derivativePrice.setPriceWithTax(derivativeAmount);
                    derivativePrice.setStatus("active");
                } else {
                    derivativePrice = new HotelPrice();
                    derivativePrice.setTenantId(tenantId);
                    derivativePrice.setHotelCode(hotelCode);
                    derivativePrice.setRateCode(derivativeRateCodeValue);
                    derivativePrice.setRoomTypeCode(parentPrice.getRoomTypeCode());
                    derivativePrice.setPriceDate(parentPrice.getPriceDate());
                    derivativePrice.setPriceWithTax(derivativeAmount);
                    derivativePrice.setStatus("active");
                }
                hotelPriceRepository.save(derivativePrice);
            }

            logger.info("重新计算衍生价格完成: 酒店={}, 房价码={}, 折扣={}%, 共{}条",
                    hotelCode, derivativeRateCodeValue, discount, parentPrices.size());
        } catch (Exception e) {
            logger.error("重新计算衍生价格失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 衍生房价码下发时，根据父级房价码的价格自动计算衍生价格
     * 查找父级房价码在该酒店的所有 active 价格，按折扣计算后写入衍生房价码的价格
     */
    private void calculateDerivativePrices(Integer tenantId, String hotelCode, GroupRateCode derivativeRateCode) {
        try {
            GroupRateCode parentRateCode = groupRateCodeRepository
                    .findByRateCodeAndGroupId(derivativeRateCode.getParentRateCode(), tenantId);
            if (parentRateCode == null)
                return;

            String parentRateCodeValue = parentRateCode.getRateCode();
            String derivativeRateCodeValue = derivativeRateCode.getRateCode();
            Double discount = derivativeRateCode.getDiscount();
            String rounding = derivativeRateCode.getRounding();

            if (discount == null || discount <= 0)
                return;

            List<HotelPrice> parentPrices = hotelPriceRepository
                    .findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, parentRateCodeValue)
                    .stream()
                    .filter(p -> "active".equals(p.getStatus()) && p.getPriceWithTax() != null)
                    .collect(Collectors.toList());

            if (parentPrices.isEmpty()) {
                logger.info("父级房价码 {} 在酒店 {} 无价格数据，跳过衍生价格计算", parentRateCodeValue, hotelCode);
                return;
            }

            BigDecimal discountRate = BigDecimal.valueOf(discount).divide(BigDecimal.valueOf(100), 4,
                    RoundingMode.HALF_UP);
            int savedCount = 0;

            for (HotelPrice parentPrice : parentPrices) {
                BigDecimal parentAmount = parentPrice.getPriceWithTax();
                BigDecimal derivativeAmount = parentAmount.multiply(discountRate);

                // 取整
                if ("floor".equals(rounding)) {
                    derivativeAmount = derivativeAmount.setScale(0, RoundingMode.FLOOR);
                } else if ("ceil".equals(rounding)) {
                    derivativeAmount = derivativeAmount.setScale(0, RoundingMode.CEILING);
                } else {
                    derivativeAmount = derivativeAmount.setScale(2, RoundingMode.HALF_UP);
                }

                // 查找或创建衍生价格记录
                Optional<HotelPrice> existingOpt = hotelPriceRepository
                        .findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDate(
                                tenantId, hotelCode, derivativeRateCodeValue,
                                parentPrice.getRoomTypeCode(), parentPrice.getPriceDate());

                HotelPrice derivativePrice;
                if (existingOpt.isPresent()) {
                    derivativePrice = existingOpt.get();
                    derivativePrice.setPriceWithTax(derivativeAmount);
                    derivativePrice.setStatus("active");
                } else {
                    derivativePrice = new HotelPrice();
                    derivativePrice.setTenantId(tenantId);
                    derivativePrice.setHotelCode(hotelCode);
                    derivativePrice.setRateCode(derivativeRateCodeValue);
                    derivativePrice.setRoomTypeCode(parentPrice.getRoomTypeCode());
                    derivativePrice.setPriceDate(parentPrice.getPriceDate());
                    derivativePrice.setPriceWithTax(derivativeAmount);
                    derivativePrice.setStatus("active");
                }
                hotelPriceRepository.save(derivativePrice);
                savedCount++;
            }

            logger.info("衍生房价码 {} 下发到酒店 {}，根据父级 {} 的 {} 条价格计算完成，折扣 {}%",
                    derivativeRateCodeValue, hotelCode, parentRateCodeValue, savedCount, discount);
        } catch (Exception e) {
            logger.error("计算衍生价格失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 删除集团房价码
     * 
     * @param id 集团房价码ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupRateCode(@PathVariable Integer id) {
        return ResponseEntity.badRequest().body(Map.of("error", "集团房价码原则上不允许物理删除，如需废弃请进行停用操作"));
    }

    /**
     * 启用集团房价码
     * 
     * @param id 集团房价码ID
     * @return 启用后的集团房价码对象
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableGroupRateCode(@PathVariable Integer id) {
        try {
            GroupRateCode enabledRateCode = groupRateCodeService.enableGroupRateCode(id);
            return ResponseEntity.ok(enabledRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("启用集团房价码失败: " + e.getMessage());
        }
    }

    /**
     * 停用集团房价码（级联停用子衍生码）
     * 
     * @param id 集团房价码ID
     * @return 停用后的集团房价码对象
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableGroupRateCode(@PathVariable Integer id) {
        try {
            GroupRateCode disabledRateCode = groupRateCodeService.disableGroupRateCodeCascade(id);
            return ResponseEntity.ok(disabledRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("停用集团房价码失败: " + e.getMessage());
        }
    }

    /**
     * 获取可选的父级房价码列表
     * 
     * @param targetDerivativeLevel 目标衍生层级 (level1/level2)
     * @param excludeId             要排除的房价码ID（编辑时使用）
     * @return 可选的父级房价码列表
     */
    @GetMapping("/selectable-parents")
    public ResponseEntity<List<GroupRateCode>> getSelectableParentRateCodes(
            @RequestParam(required = false) String targetDerivativeLevel,
            @RequestParam(required = false) Integer excludeId) {
        try {
            Integer groupId = getCurrentTenantId();
            logger.info("getSelectableParentRateCodes() - 使用租户ID: {}, 目标衍生层级: {}", groupId, targetDerivativeLevel);
            List<GroupRateCode> rateCodes = groupRateCodeService.getSelectableParentRateCodes(
                    groupId,
                    targetDerivativeLevel,
                    excludeId);
            logger.info("getSelectableParentRateCodes() - 返回 {} 个可选父级房价码", rateCodes.size());
            return ResponseEntity.ok(rateCodes);
        } catch (Exception e) {
            logger.error("getSelectableParentRateCodes() - 获取可选父级房价码失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取指定房价码的子衍生码数量
     * 
     * @param id 集团房价码ID
     * @return 子衍生码数量
     */
    @GetMapping("/{id}/child-count")
    public ResponseEntity<Map<String, Object>> getChildCount(@PathVariable Integer id) {
        try {
            GroupRateCode rateCode = groupRateCodeService.getGroupRateCodeById(id);
            if (rateCode == null)
                return ResponseEntity.notFound().build();
            long count = groupRateCodeService.countChildDerivatives(rateCode.getRateCode());
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取所有状态为active的集团房价码
     * 
     * @return 集团房价码列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<GroupRateCode>> getActiveGroupRateCodes() {
        try {
            Integer tenantId = getCurrentTenantId();
            List<GroupRateCode> rateCodes = groupRateCodeService.getGroupRateCodesByConditions(
                    tenantId, null, null, null, null, null, null, null, null, "active", null);
            return ResponseEntity.ok(rateCodes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据集团ID和状态获取集团房价码列表
     * 
     * @param groupId 集团ID
     * @param status  状态
     * @return 集团房价码列表
     */
    @GetMapping("/group/{groupId}/status/{status}")
    public ResponseEntity<List<GroupRateCode>> getGroupRateCodesByGroupIdAndStatus(
            @PathVariable Integer groupId,
            @PathVariable String status) {
        try {
            List<GroupRateCode> rateCodes = groupRateCodeService.getGroupRateCodesByGroupIdAndStatus(groupId, status);
            return ResponseEntity.ok(rateCodes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== 分配接口 =====

    /**
     * 获取集团房价码的酒店分配状态列表
     * 
     * @param id 集团房价码ID
     * @return 每个酒店的分配状态
     */
    @GetMapping("/{id}/allocations")
    public ResponseEntity<List<Map<String, Object>>> getAllocations(@PathVariable Integer id) {
        try {
            logger.info("=== getAllocations 开始执行 ===");
            logger.info("请求参数 id: {}", id);

            GroupRateCode groupRateCode = groupRateCodeService.getGroupRateCodeById(id);
            if (groupRateCode == null) {
                logger.error("集团房价码不存在, id: {}", id);
                return ResponseEntity.notFound().build();
            }
            logger.info("找到集团房价码: {}, rateCode: {}", groupRateCode.getId(), groupRateCode.getRateCode());

            Integer tenantId = getCurrentTenantId();
            String groupRateCodeValue = groupRateCode.getRateCode();

            // 获取集团下所有酒店
            logger.info("查询租户 {} 的酒店列表...", tenantId);
            List<Hotel> hotels = hotelRepository.findByTenantId(tenantId);
            logger.info("找到 {} 个酒店", hotels.size());
            for (Hotel hotel : hotels) {
                logger.info("  - 酒店: id={}, name={}, code={}", hotel.getId(), hotel.getChineseName(),
                        hotel.getHotelCode());
            }

            // 只查询当前租户和当前房价码的分配记录
            logger.info("查询租户 {} 和房价码 {} 的分配记录...", tenantId, groupRateCodeValue);
            List<HotelRateCodeAllocation> allocations = allocationRepository.findByTenantIdAndRateCode(tenantId,
                    groupRateCodeValue);
            logger.info("找到 {} 条分配记录", allocations.size());

            List<Map<String, Object>> result = new ArrayList<>();
            for (Hotel hotel : hotels) {
                Map<String, Object> item = new HashMap<>();
                item.put("hotelCode", hotel.getHotelCode());
                item.put("hotelName", hotel.getChineseName());

                // 查找该酒店对应的分配记录
                Optional<HotelRateCodeAllocation> allocationOpt = allocations.stream()
                        .filter(a -> a.getHotelCode().equals(hotel.getHotelCode()))
                        .findFirst();

                if (allocationOpt.isPresent()) {
                    HotelRateCodeAllocation allocation = allocationOpt.get();
                    item.put("allocated", allocation.getAllocated());
                    item.put("basicInfoEditable", allocation.getBasicInfoEditable());
                    item.put("priceInfoEditable", allocation.getPriceInfoEditable());
                    item.put("bookingLimitEditable", allocation.getBookingLimitEditable());
                    item.put("guaranteeRuleEditable", allocation.getGuaranteeRuleEditable());
                    item.put("promotionEditable", allocation.getPromotionEditable());
                } else {
                    item.put("allocated", false);
                    item.put("basicInfoEditable", false);
                    item.put("priceInfoEditable", false);
                    item.put("bookingLimitEditable", false);
                    item.put("guaranteeRuleEditable", false);
                    item.put("promotionEditable", false);
                }
                result.add(item);
            }
            logger.info("=== getAllocations 执行完成，返回 {} 条记录 ===", result.size());
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> item = result.get(i);
                logger.info("  [{}] hotelCode={}, hotelName={}, allocated={}",
                        i, item.get("hotelCode"), item.get("hotelName"), item.get("allocated"));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 保存集团房价码的酒店分配设置
     * 分配逻辑：
     * 1. allocated=true：在酒店价格计划表创建/激活一条与集团房价码相同的记录
     * 2. allocated=false：将酒店对应的价格计划记录设为 inactive（软删除）
     * 增强逻辑：
     * - 衍生码下发时检查父级房价码是否已下发到该酒店（Task 10）
     * - 重新启用已停用的分配时返回差异信息（Task 9）
     * 
     * @param id             集团房价码ID
     * @param allocationList 分配设置列表
     * @return 操作结果
     */
    @PostMapping("/{id}/allocate")
    @Transactional
    public ResponseEntity<?> allocate(
            @PathVariable Integer id,
            @RequestBody List<Map<String, Object>> allocationList) {
        try {
            GroupRateCode groupRateCode = groupRateCodeService.getGroupRateCodeById(id);
            if (groupRateCode == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "集团房价码不存在"));
            }
            Integer tenantId = getCurrentTenantId();

            // Task 10: 衍生码下发时检查父级房价码链（使用hotelCode，符合CODE关联规范）
            String derivativeLevel = groupRateCode.getDerivativeLevel();
            if ("level1".equals(derivativeLevel) || "level2".equals(derivativeLevel)) {
                // 获取当前已分配的酒店CODE集合
                List<HotelRateCodeAllocation> existingAllocations = allocationRepository
                        .findByTenantIdAndRateCode(tenantId, groupRateCode.getRateCode());
                Set<String> alreadyAllocatedHotelCodes = existingAllocations.stream()
                        .filter(a -> Boolean.TRUE.equals(a.getAllocated()))
                        .map(HotelRateCodeAllocation::getHotelCode)
                        .collect(Collectors.toSet());

                // 只检查新增分配的酒店（排除已分配的），使用 hotelCode 作为标识
                List<String> allocatingHotelCodes = allocationList.stream()
                        .filter(item -> Boolean.TRUE.equals(item.get("allocated")))
                        .map(item -> (String) item.get("hotelCode"))
                        .filter(hCode -> hCode != null && !alreadyAllocatedHotelCodes.contains(hCode))
                        .collect(Collectors.toList());

                if (!allocatingHotelCodes.isEmpty()) {
                    String parentCode = groupRateCode.getParentRateCode();
                    if (parentCode != null && !parentCode.isBlank()) {
                        GroupRateCode parentRateCode = groupRateCodeRepository.findByRateCodeAndGroupId(parentCode,
                                getCurrentTenantId());
                        if (parentRateCode != null) {
                            List<RatePlan> parentPlans = ratePlanRepository.findByTenantIdAndSourceGroupRateCode(tenantId, parentCode);

                            // 使用 hotelCode 匹配，而非 hotelId
                            for (String hCode : allocatingHotelCodes) {
                                boolean parentAllocatedToHotel = parentPlans.stream()
                                        .anyMatch(rp -> hCode.equals(rp.getHotelCode())
                                                && "active".equals(rp.getStatus()));

                                if (!parentAllocatedToHotel) {
                                    Hotel hotel = hotelRepository.findByHotelCodeAndTenantId(hCode, tenantId)
                                            .orElse(null);
                                    String hotelName = hotel != null ? hotel.getChineseName() : hCode;
                                    return ResponseEntity.badRequest().body(Map.of(
                                            "error", groupRateCode.getRateName() + " 下发到「" + hotelName
                                                    + "」失败，需要先下发父级房价码（" + parentRateCode.getRateName() + "）"));
                                }
                            }

                            if ("level2".equals(derivativeLevel) && parentRateCode.getParentRateCode() != null
                                    && !parentRateCode.getParentRateCode().isBlank()) {
                                String grandparentCode = parentRateCode.getParentRateCode();
                                List<RatePlan> grandparentPlans = ratePlanRepository
                                        .findByTenantIdAndSourceGroupRateCode(tenantId, grandparentCode);
                                GroupRateCode grandparentRateCode = groupRateCodeRepository
                                        .findByRateCodeAndGroupId(grandparentCode, getCurrentTenantId());

                                for (String hCode : allocatingHotelCodes) {
                                    boolean grandparentAllocated = grandparentPlans.stream()
                                            .anyMatch(rp -> hCode.equals(rp.getHotelCode())
                                                    && "active".equals(rp.getStatus()));

                                    if (!grandparentAllocated) {
                                        Hotel hotel = hotelRepository.findByHotelCodeAndTenantId(hCode, tenantId)
                                                .orElse(null);
                                        String hotelName = hotel != null ? hotel.getChineseName() : hCode;
                                        String grandparentName = grandparentRateCode != null
                                                ? grandparentRateCode.getRateName()
                                                : "基础房价码";
                                        return ResponseEntity.badRequest().body(Map.of(
                                                "error", groupRateCode.getRateName() + " 下发到「" + hotelName
                                                        + "」失败，需要先下发基础房价码（" + grandparentName + "）"));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Task 9: 收集重新启用时的差异信息
            List<Map<String, Object>> reallocationDiffs = new ArrayList<>();

            for (Map<String, Object> item : allocationList) {
                String hotelCodeParam = item.get("hotelCode") instanceof String ? (String) item.get("hotelCode") : null;

                if (hotelCodeParam == null || hotelCodeParam.isEmpty()) {
                    logger.warn("跳过无效的酒店: hotelCode={}", hotelCodeParam);
                    continue;
                }

                Hotel hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCodeParam, getCurrentTenantId())
                        .orElse(null);

                if (hotel == null) {
                    logger.warn("跳过无效的酒店: hotelCode={}", hotelCodeParam);
                    continue;
                }



                Boolean allocated = (Boolean) item.get("allocated");
                Boolean basicInfoEditable = (Boolean) item.getOrDefault("basicInfoEditable", false);
                Boolean priceInfoEditable = (Boolean) item.getOrDefault("priceInfoEditable", false);
                Boolean bookingLimitEditable = (Boolean) item.getOrDefault("bookingLimitEditable", false);
                Boolean guaranteeRuleEditable = (Boolean) item.getOrDefault("guaranteeRuleEditable", false);
                Boolean promotionEditable = (Boolean) item.getOrDefault("promotionEditable", false);

                String hotelCode = hotel.getHotelCode();
                String groupRateCodeValue = groupRateCode.getRateCode();

                if (Boolean.TRUE.equals(allocated)) {
                    // 下发：创建或更新酒店价格计划和分配记录
                    RatePlan ratePlan;

                    // 查找该酒店已有的价格计划（优先通过租户ID+酒店Code+价格计划Code进行唯一匹配，防止重复插入引发异常）
                    Optional<RatePlan> existingOpt = ratePlanRepository
                            .findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, groupRateCode.getRateCode());

                    if (existingOpt.isPresent()) {
                        ratePlan = existingOpt.get();

                        // Task 9: 如果是重新启用（inactive -> active），比较差异
                        if ("inactive".equals(ratePlan.getStatus())) {
                            List<String> diffFields = compareRateCodeWithPlan(groupRateCode, ratePlan);
                            if (!diffFields.isEmpty()) {
                                Map<String, Object> diffInfo = new HashMap<>();
                                diffInfo.put("hotelCode", hotel.getHotelCode());
                                diffInfo.put("hotelName", hotel.getChineseName());
                                diffInfo.put("diffFields", diffFields);
                                reallocationDiffs.add(diffInfo);
                            }
                        }

                        // 更新字段并激活
                        syncRatePlanFromGroupRateCode(ratePlan, groupRateCode);
                        ratePlan.setStatus("active");
                        ratePlan.setHotelCode(hotel.getHotelCode());
                        ratePlan.setTenantId(tenantId);
                    } else {
                        // 无记录：新建
                        ratePlan = new RatePlan();
                        syncRatePlanFromGroupRateCode(ratePlan, groupRateCode);
                        ratePlan.setStatus("active");
                        ratePlan.setHotelCode(hotel.getHotelCode());
                        ratePlan.setTenantId(tenantId);
                    }
                    ratePlanRepository.save(ratePlan);

                    // 衍生房价码下发时，根据父级房价码的价格自动计算衍生价格
                    String derivLevel = groupRateCode.getDerivativeLevel();
                    if (("level1".equals(derivLevel) || "level2".equals(derivLevel))
                            && groupRateCode.getParentRateCode() != null
                            && groupRateCode.getDiscount() != null) {
                        calculateDerivativePrices(tenantId, hotelCode, groupRateCode);
                    }

                    // 更新或创建分配权限记录
                    List<HotelRateCodeAllocation> allocations = allocationRepository
                            .findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, groupRateCodeValue);
                    HotelRateCodeAllocation allocation = allocations.isEmpty() ? null : allocations.get(0);
                    if (allocation == null) {
                        allocation = new HotelRateCodeAllocation();
                        allocation.setTenantId(tenantId);
                        allocation.setHotelCode(hotelCode);
                        allocation.setRateCode(groupRateCodeValue);
                    }
                    allocation.setAllocated(true);
                    allocation.setBasicInfoEditable(Boolean.TRUE.equals(basicInfoEditable));
                    allocation.setPriceInfoEditable(Boolean.TRUE.equals(priceInfoEditable));
                    allocation.setBookingLimitEditable(Boolean.TRUE.equals(bookingLimitEditable));
                    allocation.setGuaranteeRuleEditable(Boolean.TRUE.equals(guaranteeRuleEditable));
                    allocation.setPromotionEditable(Boolean.TRUE.equals(promotionEditable));
                    allocationRepository.save(allocation);

                } else {
                    // 回收：将酒店价格计划设为 inactive
                    List<HotelRateCodeAllocation> allocations = allocationRepository
                            .findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, groupRateCodeValue);
                    HotelRateCodeAllocation allocation = allocations.isEmpty() ? null : allocations.get(0);
                    if (allocation != null) {
                        // 查找该酒店的价格计划（使用 hotelCode，符合CODE关联规范）
                        List<RatePlan> ratePlans = ratePlanRepository
                                .findByTenantIdAndSourceGroupRateCode(tenantId, groupRateCode.getRateCode());
                        final String finalHotelCodeForRevoke = hotelCode;
                        Optional<RatePlan> ratePlanOpt = ratePlans.stream()
                                .filter(rp -> finalHotelCodeForRevoke.equals(rp.getHotelCode()))
                                .findFirst();

                        if (ratePlanOpt.isPresent()) {
                            RatePlan ratePlan = ratePlanOpt.get();
                            ratePlan.setStatus("inactive");
                            ratePlanRepository.save(ratePlan);
                        }

                        allocation.setAllocated(false);
                        allocation.setBasicInfoEditable(false);
                        allocation.setPriceInfoEditable(false);
                        allocation.setBookingLimitEditable(false);
                        allocation.setGuaranteeRuleEditable(false);
                        allocation.setPromotionEditable(false);
                        allocationRepository.save(allocation);
                    }
                }
            }

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("message", "分配设置保存成功");
            if (!reallocationDiffs.isEmpty()) {
                response.put("reallocationDiffs", reallocationDiffs);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "保存分配设置失败: " + e.getMessage()));
        }
    }

    // ===== CODE-based endpoints =====

    /**
     * 根据房价码代码更新集团房价码
     * 
     * @param code          房价码代码
     * @param groupRateCode 集团房价码对象
     * @return 更新后的集团房价码对象
     */
    @PutMapping("/code/{code}")
    public ResponseEntity<?> updateGroupRateCodeByCode(@PathVariable String code,
            @RequestBody GroupRateCode groupRateCode) {
        GroupRateCode existing = groupRateCodeRepository.findByRateCodeAndGroupId(code, getCurrentTenantId());
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            GroupRateCode updatedRateCode = groupRateCodeService.updateGroupRateCode(existing.getId(), groupRateCode);
            return ResponseEntity.ok(updatedRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新集团房价码失败: " + e.getMessage());
        }
    }

    /**
     * 根据房价码代码删除集团房价码
     * 
     * @param code 房价码代码
     * @return 响应结果
     */
    @DeleteMapping("/code/{code}")
    public ResponseEntity<?> deleteGroupRateCodeByCode(@PathVariable String code) {
        return ResponseEntity.badRequest().body(Map.of("error", "集团房价码原则上不允许物理删除，如需废弃请进行停用操作"));
    }

    /**
     * 根据房价码代码启用集团房价码
     * 
     * @param code 房价码代码
     * @return 启用后的集团房价码对象
     */
    @PutMapping("/code/{code}/enable")
    public ResponseEntity<?> enableGroupRateCodeByCode(@PathVariable String code) {
        GroupRateCode existing = groupRateCodeRepository.findByRateCodeAndGroupId(code, getCurrentTenantId());
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            GroupRateCode enabledRateCode = groupRateCodeService.enableGroupRateCode(existing.getId());
            return ResponseEntity.ok(enabledRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("启用集团房价码失败: " + e.getMessage());
        }
    }

    /**
     * 根据房价码代码停用集团房价码（级联停用子衍生码）
     * 
     * @param code 房价码代码
     * @return 停用后的集团房价码对象
     */
    @PutMapping("/code/{code}/disable")
    public ResponseEntity<?> disableGroupRateCodeByCode(@PathVariable String code) {
        GroupRateCode existing = groupRateCodeRepository.findByRateCodeAndGroupId(code, getCurrentTenantId());
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            GroupRateCode disabledRateCode = groupRateCodeService.disableGroupRateCodeCascade(existing.getId());
            return ResponseEntity.ok(disabledRateCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("停用集团房价码失败: " + e.getMessage());
        }
    }
}
