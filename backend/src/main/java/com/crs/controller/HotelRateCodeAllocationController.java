package com.crs.controller;

import com.crs.entity.HotelRateCodeAllocation;
import com.crs.entity.Hotel;
import com.crs.entity.GroupRateCode;
import com.crs.repository.HotelRateCodeAllocationRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * HotelRateCodeAllocationController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【HotelRateCodeAllocationController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/09-系统设置.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 HotelRateCodeAllocationController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/hotel-rate-code-allocations")
@CrossOrigin(origins = "*")
public class HotelRateCodeAllocationController {

    @Autowired
    private HotelRateCodeAllocationRepository allocationRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private GroupRateCodeRepository groupRateCodeRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    private boolean validateHotelTenant(String hotelCode) {
        return hotelRepository.findByHotelCodeAndTenantId(hotelCode, getCurrentTenantId()).isPresent();
    }

    /**
     * 获取酒店的房价码分配列表
     * @param hotelId 酒店ID
     * @return 分配列表，包含 groupRateCodeId 和 hotelId
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Map<String, Object>>> getAllocationsByHotelId(@PathVariable Integer hotelId) {
        try {
            Integer currentTenantId = getCurrentTenantId();
            Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId)
                    .filter(h -> h.getTenantId() != null && h.getTenantId().equals(currentTenantId));
            
            if (!hotelOpt.isPresent()) {
                return ResponseEntity.status(403).build();
            }
            Hotel hotel = hotelOpt.get();
            String hotelCode = hotel.getHotelCode();

            // 关联查询原则：必须使用 tenantId + hotelCode 双维度隔离，防止跨租户泄露
            List<HotelRateCodeAllocation> allocations = allocationRepository.findByTenantIdAndHotelCode(currentTenantId, hotelCode);

            List<Map<String, Object>> result = new ArrayList<>();
            for (HotelRateCodeAllocation allocation : allocations) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", allocation.getId());
                item.put("tenantId", allocation.getTenantId());
                item.put("hotelCode", allocation.getHotelCode());
                item.put("rateCode", allocation.getRateCode());
                item.put("allocated", allocation.getAllocated());
                item.put("basicInfoEditable", allocation.getBasicInfoEditable());
                item.put("priceInfoEditable", allocation.getPriceInfoEditable());
                item.put("bookingLimitEditable", allocation.getBookingLimitEditable());
                item.put("guaranteeRuleEditable", allocation.getGuaranteeRuleEditable());
                item.put("promotionEditable", allocation.getPromotionEditable());
                item.put("hotelId", hotelId);

                GroupRateCode rateCode = groupRateCodeRepository.findByRateCodeAndGroupId(allocation.getRateCode(), currentTenantId);
                if (rateCode != null) {
                    item.put("groupRateCodeId", rateCode.getId());
                }

                result.add(item);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 创建酒店房价码分配
     * @param allocationData 分配数据
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<HotelRateCodeAllocation> createAllocation(@RequestBody Map<String, Object> allocationData) {
        try {
            Integer currentTenantId = getCurrentTenantId();
            Integer hotelId = allocationData.get("hotelId") instanceof Number ? ((Number) allocationData.get("hotelId")).intValue() : null;
            String hotelCodeParam = allocationData.get("hotelCode") instanceof String ? (String) allocationData.get("hotelCode") : null;
            Integer rateCodeId = allocationData.get("rateCodeId") instanceof Number ? ((Number) allocationData.get("rateCodeId")).intValue() : null;

            Hotel hotel = null;
            if (hotelCodeParam != null && !hotelCodeParam.isEmpty()) {
                hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCodeParam, currentTenantId).orElse(null);
            }
            if (hotel == null && hotelId != null) {
                hotel = hotelRepository.findById(hotelId)
                        .filter(h -> h.getTenantId() != null && h.getTenantId().equals(currentTenantId))
                        .orElse(null);
            }

            if (hotel == null) {
                return ResponseEntity.status(403).build();
            }

            GroupRateCode rateCode = null;
            if (rateCodeId != null) {
                rateCode = groupRateCodeRepository.findById(rateCodeId)
                        .filter(rc -> rc.getGroupId() != null && rc.getGroupId().equals(currentTenantId))
                        .orElse(null);
            }
            if (rateCode == null) {
                return ResponseEntity.badRequest().build();
            }

            HotelRateCodeAllocation allocation = new HotelRateCodeAllocation();
            allocation.setTenantId(currentTenantId);
            allocation.setHotelCode(hotel.getHotelCode());
            allocation.setRateCode(rateCode.getRateCode());
            allocation.setAllocated((Boolean) allocationData.getOrDefault("allocated", false));
            allocation.setBasicInfoEditable((Boolean) allocationData.getOrDefault("basicInfoEditable", false));
            allocation.setPriceInfoEditable((Boolean) allocationData.getOrDefault("priceInfoEditable", false));
            allocation.setBookingLimitEditable((Boolean) allocationData.getOrDefault("bookingLimitEditable", false));
            allocation.setGuaranteeRuleEditable((Boolean) allocationData.getOrDefault("guaranteeRuleEditable", false));
            allocation.setPromotionEditable((Boolean) allocationData.getOrDefault("promotionEditable", false));

            HotelRateCodeAllocation saved = allocationRepository.save(allocation);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 更新酒店房价码分配
     * @param id 分配ID
     * @param allocationData 分配数据
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<HotelRateCodeAllocation> updateAllocation(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> allocationData) {
        try {
            Integer currentTenantId = getCurrentTenantId();
            Optional<HotelRateCodeAllocation> existingOpt = allocationRepository.findById(id)
                    .filter(a -> a.getTenantId() != null && a.getTenantId().equals(currentTenantId));
            
            if (!existingOpt.isPresent()) {
                return ResponseEntity.status(403).build();
            }

            HotelRateCodeAllocation allocation = existingOpt.get();

            if (allocationData.containsKey("allocated")) {
                allocation.setAllocated((Boolean) allocationData.get("allocated"));
            }
            if (allocationData.containsKey("basicInfoEditable")) {
                allocation.setBasicInfoEditable((Boolean) allocationData.get("basicInfoEditable"));
            }
            if (allocationData.containsKey("priceInfoEditable")) {
                allocation.setPriceInfoEditable((Boolean) allocationData.get("priceInfoEditable"));
            }
            if (allocationData.containsKey("bookingLimitEditable")) {
                allocation.setBookingLimitEditable((Boolean) allocationData.get("bookingLimitEditable"));
            }
            if (allocationData.containsKey("guaranteeRuleEditable")) {
                allocation.setGuaranteeRuleEditable((Boolean) allocationData.get("guaranteeRuleEditable"));
            }
            if (allocationData.containsKey("promotionEditable")) {
                allocation.setPromotionEditable((Boolean) allocationData.get("promotionEditable"));
            }

            HotelRateCodeAllocation saved = allocationRepository.save(allocation);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 删除酒店的所有房价码分配
     * @param hotelId 酒店ID
     * @return 删除结果
     */
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteAllocationsByHotelId(@PathVariable Integer hotelId) {
        try {
            Integer currentTenantId = getCurrentTenantId();
            Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId)
                    .filter(h -> h.getTenantId() != null && h.getTenantId().equals(currentTenantId));
            
            if (!hotelOpt.isPresent()) {
                return ResponseEntity.status(403).build();
            }

            Hotel hotel = hotelOpt.get();
            // 安全删除：必须带 tenantId 防止跨租户越权删除
            allocationRepository.deleteByTenantIdAndHotelCode(currentTenantId, hotel.getHotelCode());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<List<Map<String, Object>>> getAllocationsByHotelCode(@PathVariable String hotelCode) {
        try {
            Integer currentTenantId = getCurrentTenantId();
            if (!validateHotelTenant(hotelCode)) {
                return ResponseEntity.status(403).build();
            }

            // 关联查询原则：必须使用 tenantId + hotelCode 双维度隔离，防止跨租户泄露
            List<HotelRateCodeAllocation> allocations = allocationRepository.findByTenantIdAndHotelCode(currentTenantId, hotelCode);

            List<Map<String, Object>> result = new ArrayList<>();
            for (HotelRateCodeAllocation allocation : allocations) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", allocation.getId());
                item.put("tenantId", allocation.getTenantId());
                item.put("hotelCode", allocation.getHotelCode());
                item.put("rateCode", allocation.getRateCode());
                item.put("allocated", allocation.getAllocated());
                item.put("basicInfoEditable", allocation.getBasicInfoEditable());
                item.put("priceInfoEditable", allocation.getPriceInfoEditable());
                item.put("bookingLimitEditable", allocation.getBookingLimitEditable());
                item.put("guaranteeRuleEditable", allocation.getGuaranteeRuleEditable());
                item.put("promotionEditable", allocation.getPromotionEditable());

                GroupRateCode rateCode = groupRateCodeRepository.findByRateCodeAndGroupId(allocation.getRateCode(), currentTenantId);
                if (rateCode != null) {
                    item.put("groupRateCodeId", rateCode.getId());
                }

                result.add(item);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<Void> deleteAllocationsByHotelCode(@PathVariable String hotelCode) {
        try {
            Integer currentTenantId = getCurrentTenantId();
            if (!validateHotelTenant(hotelCode)) {
                return ResponseEntity.status(403).build();
            }

            // 关联查询原则：必须带 tenantId 防止跨租户越权删除
            allocationRepository.deleteByTenantIdAndHotelCode(currentTenantId, hotelCode);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
