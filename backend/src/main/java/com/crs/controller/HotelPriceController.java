package com.crs.controller;

import com.crs.entity.Hotel;
import com.crs.entity.HotelPrice;
import com.crs.entity.HotelPriceLog;
import com.crs.entity.RatePlan;
import com.crs.entity.RoomType;
import com.crs.repository.HotelPriceLogRepository;
import com.crs.repository.HotelPriceRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.repository.RoomTypeRepository;
import com.crs.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 酒店价格控制器
 */
@RestController
@RequestMapping("/api/hotel-prices")
public class HotelPriceController {

    @Autowired
    private HotelPriceRepository hotelPriceRepository;

    @Autowired
    private HotelPriceLogRepository hotelPriceLogRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RatePlanRepository ratePlanRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HotelPriceController.class);

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    private String getOperatorName() {
        return "系统用户";
    }

    private String decodeOperatorName(String encoded) {
        try {
            return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encoded;
        }
    }

    /**
     * 查询酒店价格（按日期范围），包含已删除的价格记录
     */
    @GetMapping
    public ResponseEntity<?> getHotelPrices(
            @RequestParam String hotelCode,
            @RequestParam(required = false) String rateCode,
            @RequestParam(required = false) String roomTypeCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Integer tenantId = getCurrentTenantId();
        List<HotelPrice> prices;

        if (rateCode != null && roomTypeCode != null && startDate != null && endDate != null) {
            prices = hotelPriceRepository.findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDateBetween(
                    tenantId, hotelCode, rateCode, roomTypeCode, startDate, endDate);
        } else if (rateCode != null && startDate != null && endDate != null) {
            prices = hotelPriceRepository.findByTenantIdAndHotelCodeAndRateCodeAndPriceDateBetween(
                    tenantId, hotelCode, rateCode, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            prices = hotelPriceRepository.findByTenantIdAndHotelCodeAndPriceDateBetween(
                    tenantId, hotelCode, startDate, endDate);
        } else if (rateCode != null) {
            prices = hotelPriceRepository.findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, rateCode);
        } else {
            prices = hotelPriceRepository.findByTenantIdAndHotelCode(tenantId, hotelCode);
        }

        return ResponseEntity.ok(Map.of("success", true, "data", prices));
    }

    /**
     * 保存或更新单条价格
     */
    @PostMapping
    public ResponseEntity<?> saveHotelPrice(
            @RequestBody HotelPrice hotelPrice,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "%E7%B3%BB%E7%BB%9F%E7%94%A8%E6%88%B7") String rawOperatorName) {
        try {
            String operatorName = decodeOperatorName(rawOperatorName);
            Integer tenantId = getCurrentTenantId();
            hotelPrice.setTenantId(tenantId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Optional<HotelPrice> existing = hotelPriceRepository
                    .findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDate(
                            tenantId, hotelPrice.getHotelCode(), hotelPrice.getRateCode(),
                            hotelPrice.getRoomTypeCode(), hotelPrice.getPriceDate());

            String opType;
            String oldPriceStr = null;
            String newPriceStr;

            if (existing.isPresent()) {
                HotelPrice existingPrice = existing.get();
                oldPriceStr = existingPrice.getPriceWithTax() != null ? existingPrice.getPriceWithTax().toString() : null;
                existingPrice.setPriceWithTax(hotelPrice.getPriceWithTax());
                existingPrice.setPriceWithoutTax(hotelPrice.getPriceWithoutTax());
                existingPrice.setStatus(hotelPrice.getStatus() != null ? hotelPrice.getStatus() : "active");
                hotelPriceRepository.save(existingPrice);
                opType = "inactive".equals(existingPrice.getStatus()) ? "delete" : "update";
                newPriceStr = hotelPrice.getPriceWithTax() != null ? hotelPrice.getPriceWithTax().toString() : null;
            } else {
                hotelPrice.setStatus(hotelPrice.getStatus() != null ? hotelPrice.getStatus() : "active");
                hotelPriceRepository.save(hotelPrice);
                opType = "create";
                newPriceStr = hotelPrice.getPriceWithTax() != null ? hotelPrice.getPriceWithTax().toString() : null;
            }

            // 记录日志
            String dateStr = sdf.format(hotelPrice.getPriceDate());
            String detail = String.format(
                    "[{\"roomTypeCode\":\"%s\",\"dates\":[\"%s\"],\"oldPrice\":%s,\"newPrice\":%s}]",
                    hotelPrice.getRoomTypeCode(), dateStr,
                    oldPriceStr != null ? "\"" + oldPriceStr + "\"" : "null",
                    "delete".equals(opType) ? "null" : (newPriceStr != null ? "\"" + newPriceStr + "\"" : "null"));

            HotelPriceLog log = new HotelPriceLog();
            log.setTenantId(tenantId);
            log.setHotelCode(hotelPrice.getHotelCode());
            log.setRateCode(hotelPrice.getRateCode());
            log.setOperatorName(operatorName);
            log.setOperationType(opType);
            log.setStartDate(hotelPrice.getPriceDate());
            log.setEndDate(hotelPrice.getPriceDate());
            log.setDetail(detail);
            hotelPriceLogRepository.save(log);

            // 级联计算衍生价格
            if (!"delete".equals(opType)) {
                cascadeDerivativePrices(tenantId, hotelPrice.getHotelCode(), hotelPrice.getRateCode(),
                        hotelPrice.getRoomTypeCode(), hotelPrice.getPriceDate(), hotelPrice.getPriceWithTax());
            } else {
                cascadeDerivativePrices(tenantId, hotelPrice.getHotelCode(), hotelPrice.getRateCode(),
                        hotelPrice.getRoomTypeCode(), hotelPrice.getPriceDate(), null);
            }

            return ResponseEntity.ok(Map.of("success", true, "data", hotelPrice));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 批量保存价格
     */
    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<?> batchSaveHotelPrices(
            @RequestBody List<HotelPrice> prices,
            @RequestHeader(value = "X-Operator-Name", defaultValue = "%E7%B3%BB%E7%BB%9F%E7%94%A8%E6%88%B7") String rawOperatorName) {
        try {
            String operatorName = decodeOperatorName(rawOperatorName);
            Integer tenantId = getCurrentTenantId();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int savedCount = 0;

            // 按操作类型分组收集日志明细
            // key: roomTypeCode, value: { dates, oldPrices, newPrice, isDelete }
            Map<String, List<Map<String, Object>>> updateDetails = new LinkedHashMap<>();
            Map<String, List<Map<String, Object>>> deleteDetails = new LinkedHashMap<>();
            String hotelCode = null;
            String rateCode = null;
            Date minDate = null;
            Date maxDate = null;

            for (HotelPrice price : prices) {
                price.setTenantId(tenantId);
                if (hotelCode == null) hotelCode = price.getHotelCode();
                if (rateCode == null) rateCode = price.getRateCode();

                if (minDate == null || price.getPriceDate().before(minDate)) minDate = price.getPriceDate();
                if (maxDate == null || price.getPriceDate().after(maxDate)) maxDate = price.getPriceDate();

                boolean isDelete = "inactive".equals(price.getStatus());

                Optional<HotelPrice> existing = hotelPriceRepository
                        .findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDate(
                                tenantId, price.getHotelCode(), price.getRateCode(),
                                price.getRoomTypeCode(), price.getPriceDate());

                String oldPriceStr = null;
                if (existing.isPresent()) {
                    HotelPrice existingPrice = existing.get();
                    oldPriceStr = existingPrice.getPriceWithTax() != null ? existingPrice.getPriceWithTax().toString() : null;
                    if (isDelete) {
                        existingPrice.setStatus("inactive");
                        existingPrice.setPriceWithTax(BigDecimal.ZERO);
                    } else {
                        existingPrice.setPriceWithTax(price.getPriceWithTax());
                        existingPrice.setPriceWithoutTax(price.getPriceWithoutTax());
                        existingPrice.setStatus("active");
                    }
                    hotelPriceRepository.save(existingPrice);
                } else if (!isDelete) {
                    price.setStatus("active");
                    hotelPriceRepository.save(price);
                }
                savedCount++;

                // 收集日志明细
                String dateStr = sdf.format(price.getPriceDate());
                Map<String, Object> entry = new HashMap<>();
                entry.put("date", dateStr);
                entry.put("oldPrice", oldPriceStr);
                entry.put("newPrice", isDelete ? null : (price.getPriceWithTax() != null ? price.getPriceWithTax().toString() : null));

                Map<String, List<Map<String, Object>>> targetMap = isDelete ? deleteDetails : updateDetails;
                targetMap.computeIfAbsent(price.getRoomTypeCode(), k -> new ArrayList<>()).add(entry);
            }

            // 写入日志 - 更新操作
            if (!updateDetails.isEmpty() && hotelCode != null && rateCode != null) {
                StringBuilder detailJson = new StringBuilder("[");
                boolean first = true;
                for (Map.Entry<String, List<Map<String, Object>>> e : updateDetails.entrySet()) {
                    if (!first) detailJson.append(",");
                    first = false;
                    List<String> dates = new ArrayList<>();
                    String newPrice = null;
                    for (Map<String, Object> d : e.getValue()) {
                        dates.add("\"" + d.get("date") + "\"");
                        if (d.get("newPrice") != null) newPrice = (String) d.get("newPrice");
                    }
                    detailJson.append(String.format("{\"roomTypeCode\":\"%s\",\"dates\":[%s],\"newPrice\":%s}",
                            e.getKey(), String.join(",", dates),
                            newPrice != null ? "\"" + newPrice + "\"" : "null"));
                }
                detailJson.append("]");

                HotelPriceLog log = new HotelPriceLog();
                log.setTenantId(tenantId);
                log.setHotelCode(hotelCode);
                log.setRateCode(rateCode);
                log.setOperatorName(operatorName);
                log.setOperationType("batch_update");
                log.setStartDate(minDate);
                log.setEndDate(maxDate);
                log.setDetail(detailJson.toString());
                hotelPriceLogRepository.save(log);
            }

            // 写入日志 - 删除操作
            if (!deleteDetails.isEmpty() && hotelCode != null && rateCode != null) {
                StringBuilder detailJson = new StringBuilder("[");
                boolean first = true;
                for (Map.Entry<String, List<Map<String, Object>>> e : deleteDetails.entrySet()) {
                    if (!first) detailJson.append(",");
                    first = false;
                    List<String> dates = new ArrayList<>();
                    for (Map<String, Object> d : e.getValue()) {
                        dates.add("\"" + d.get("date") + "\"");
                    }
                    detailJson.append(String.format("{\"roomTypeCode\":\"%s\",\"dates\":[%s]}",
                            e.getKey(), String.join(",", dates)));
                }
                detailJson.append("]");

                HotelPriceLog log = new HotelPriceLog();
                log.setTenantId(tenantId);
                log.setHotelCode(hotelCode);
                log.setRateCode(rateCode);
                log.setOperatorName(operatorName);
                log.setOperationType("batch_delete");
                log.setStartDate(minDate);
                log.setEndDate(maxDate);
                log.setDetail(detailJson.toString());
                hotelPriceLogRepository.save(log);
            }

            // 级联计算衍生价格 - 对每条变更的价格触发级联
            for (HotelPrice price : prices) {
                boolean isDelete = "inactive".equals(price.getStatus());
                BigDecimal cascadePrice = isDelete ? null : price.getPriceWithTax();
                cascadeDerivativePrices(tenantId, price.getHotelCode(), price.getRateCode(),
                        price.getRoomTypeCode(), price.getPriceDate(), cascadePrice);
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功，共 " + savedCount + " 条"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 查询价格操作日志
     */
    @GetMapping("/logs")
    public ResponseEntity<?> getPriceLogs(
            @RequestParam String hotelCode,
            @RequestParam(required = false) String rateCode) {
        Integer tenantId = getCurrentTenantId();
        List<HotelPriceLog> logs;
        if (rateCode != null && !rateCode.isEmpty()) {
            logs = hotelPriceLogRepository.findByTenantIdAndHotelCodeAndRateCodeOrderByOperationTimeDesc(
                    tenantId, hotelCode, rateCode);
        } else {
            logs = hotelPriceLogRepository.findByTenantIdAndHotelCodeOrderByOperationTimeDesc(
                    tenantId, hotelCode);
        }
        return ResponseEntity.ok(Map.of("success", true, "data", logs));
    }

    /**
     * 价格查询专用接口 - 获取价格查询页面所需的完整数据
     */
    @GetMapping("/query")
    public ResponseEntity<?> getPriceQueryData(
            @RequestParam String hotelCode,
            @RequestParam(required = false) String rateCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            Integer tenantId = getCurrentTenantId();
            
            // 1. 先通过 hotelCode+tenantId 获取 hotelId
            Hotel hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId).orElse(null);
            if (hotel == null) {
                return ResponseEntity.ok(Map.of("success", true, "data", Map.of(
                    "ratePlans", Collections.emptyList(),
                    "roomTypes", Collections.emptyList(),
                    "prices", Collections.emptyList()
                )));
            }
            Integer hotelId = hotel.getId();
            
            // 2. 获取该酒店的所有价格计划
            List<RatePlan> ratePlans = ratePlanRepository.findByTenantIdAndHotelCode(tenantId, hotelCode);
            
            // 3. 获取该酒店的所有房型
            List<RoomType> roomTypes = roomTypeRepository.findByTenantIdAndHotelCode(tenantId, hotelCode);
            
            // 4. 获取价格数据
            List<HotelPrice> prices;
            if (rateCode != null && !rateCode.isEmpty() && startDate != null && endDate != null) {
                prices = hotelPriceRepository.findByTenantIdAndHotelCodeAndRateCodeAndPriceDateBetween(
                        tenantId, hotelCode, rateCode, startDate, endDate);
            } else if (startDate != null && endDate != null) {
                prices = hotelPriceRepository.findByTenantIdAndHotelCodeAndPriceDateBetween(
                        tenantId, hotelCode, startDate, endDate);
            } else if (rateCode != null && !rateCode.isEmpty()) {
                prices = hotelPriceRepository.findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, rateCode);
            } else {
                prices = hotelPriceRepository.findByTenantIdAndHotelCode(tenantId, hotelCode);
            }
            
            // 5. 构建响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("ratePlans", ratePlans);
            result.put("roomTypes", roomTypes);
            result.put("prices", prices);
            
            return ResponseEntity.ok(Map.of("success", true, "data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 级联计算衍生价格
     * 当基础房价码的价格变更时，自动计算所有一级衍生码的价格
     * 当一级衍生码的价格变更时，自动计算所有二级衍生码的价格
     * @param tenantId 租户ID
     * @param hotelCode 酒店CODE
     * @param rateCode 被修改价格的房价码CODE
     * @param roomTypeCode 房型CODE
     * @param priceDate 日期
     * @param newPrice 新价格（null表示删除）
     */
    private void cascadeDerivativePrices(Integer tenantId, String hotelCode, String rateCode,
                                          String roomTypeCode, Date priceDate, BigDecimal newPrice) {
        try {
            // 查找以当前房价码为父级的所有 active 衍生价格计划
            List<RatePlan> childPlans = ratePlanRepository
                    .findByTenantIdAndHotelCodeAndParentRateCodeAndStatus(tenantId, hotelCode, rateCode, "active");
            
            if (childPlans.isEmpty()) return;
            
            for (RatePlan child : childPlans) {
                Double discount = child.getDiscount();
                if (discount == null || discount <= 0) continue;
                
                String childRateCode = child.getRateCode();
                String rounding = child.getRounding();
                
                if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) == 0) {
                    // 父级价格被删除，衍生价格也标记为删除
                    Optional<HotelPrice> childPriceOpt = hotelPriceRepository
                            .findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDate(
                                    tenantId, hotelCode, childRateCode, roomTypeCode, priceDate);
                    if (childPriceOpt.isPresent()) {
                        HotelPrice childPrice = childPriceOpt.get();
                        childPrice.setStatus("inactive");
                        childPrice.setPriceWithTax(BigDecimal.ZERO);
                        hotelPriceRepository.save(childPrice);
                    }
                    // 继续级联到下一级
                    cascadeDerivativePrices(tenantId, hotelCode, childRateCode, roomTypeCode, priceDate, null);
                } else {
                    // 计算衍生价格
                    BigDecimal discountRate = BigDecimal.valueOf(discount).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    BigDecimal derivativeAmount = newPrice.multiply(discountRate);
                    
                    if ("floor".equals(rounding)) {
                        derivativeAmount = derivativeAmount.setScale(0, RoundingMode.FLOOR);
                    } else if ("ceil".equals(rounding)) {
                        derivativeAmount = derivativeAmount.setScale(0, RoundingMode.CEILING);
                    } else {
                        derivativeAmount = derivativeAmount.setScale(2, RoundingMode.HALF_UP);
                    }
                    
                    Optional<HotelPrice> childPriceOpt = hotelPriceRepository
                            .findByTenantIdAndHotelCodeAndRateCodeAndRoomTypeCodeAndPriceDate(
                                    tenantId, hotelCode, childRateCode, roomTypeCode, priceDate);
                    
                    HotelPrice childPrice;
                    if (childPriceOpt.isPresent()) {
                        childPrice = childPriceOpt.get();
                        childPrice.setPriceWithTax(derivativeAmount);
                        childPrice.setStatus("active");
                    } else {
                        childPrice = new HotelPrice();
                        childPrice.setTenantId(tenantId);
                        childPrice.setHotelCode(hotelCode);
                        childPrice.setRateCode(childRateCode);
                        childPrice.setRoomTypeCode(roomTypeCode);
                        childPrice.setPriceDate(priceDate);
                        childPrice.setPriceWithTax(derivativeAmount);
                        childPrice.setStatus("active");
                    }
                    hotelPriceRepository.save(childPrice);
                    
                    logger.info("级联计算: {} -> {} 房型{} 日期{} 价格{}",
                            rateCode, childRateCode, roomTypeCode, priceDate, derivativeAmount);
                    
                    // 继续级联到下一级（二级衍生码）
                    cascadeDerivativePrices(tenantId, hotelCode, childRateCode, roomTypeCode, priceDate, derivativeAmount);
                }
            }
        } catch (Exception e) {
            logger.error("级联计算衍生价格失败: {}", e.getMessage(), e);
        }
    }
}
