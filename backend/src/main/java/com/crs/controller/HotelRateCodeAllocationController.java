package com.crs.controller;

import com.crs.entity.HotelRateCodeAllocation;
import com.crs.entity.Hotel;
import com.crs.entity.GroupRateCode;
import com.crs.repository.HotelRateCodeAllocationRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.GroupRateCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    /**
     * 获取酒店的房价码分配列表
     * @param hotelId 酒店ID
     * @return 分配列表，包含 groupRateCodeId 和 hotelId
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Map<String, Object>>> getAllocationsByHotelId(@PathVariable Integer hotelId) {
        try {
            Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
            if (!hotelOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Hotel hotel = hotelOpt.get();
            String hotelCode = hotel.getHotelCode();

            List<HotelRateCodeAllocation> allocations = allocationRepository.findByHotelCode(hotelCode);

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

                GroupRateCode rateCode = groupRateCodeRepository.findByRateCode(allocation.getRateCode());
                if (rateCode != null) {
                    item.put("groupRateCodeId", rateCode.getId());
                }

                result.add(item);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
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
            Integer hotelId = (Integer) allocationData.get("hotelId");
            Integer rateCodeId = (Integer) allocationData.get("rateCodeId");

            Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
            Optional<GroupRateCode> rateCodeOpt = groupRateCodeRepository.findById(rateCodeId);

            if (!hotelOpt.isPresent() || !rateCodeOpt.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            Hotel hotel = hotelOpt.get();
            GroupRateCode rateCode = rateCodeOpt.get();

            HotelRateCodeAllocation allocation = new HotelRateCodeAllocation();
            allocation.setTenantId(hotel.getTenantId());
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
            e.printStackTrace();
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
            Optional<HotelRateCodeAllocation> existingOpt = allocationRepository.findById(id);
            if (!existingOpt.isPresent()) {
                return ResponseEntity.notFound().build();
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
            e.printStackTrace();
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
            Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
            if (!hotelOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Hotel hotel = hotelOpt.get();
            allocationRepository.deleteByHotelCode(hotel.getHotelCode());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
