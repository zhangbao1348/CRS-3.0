package com.crs.service;

import com.crs.entity.BasePrice;
import com.crs.repository.BasePriceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Date;

/**
 * 基础价格服务类
 * 用于处理基础价格相关的业务逻辑
 */
@Service
public class BasePriceService {
    
    private final BasePriceRepository basePriceRepository;
    
    public BasePriceService(BasePriceRepository basePriceRepository) {
        this.basePriceRepository = basePriceRepository;
    }
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 获取所有基础价格列表
     * @return 基础价格列表
     */
    public List<BasePrice> getAllBasePrices() {
        return basePriceRepository.findByTenantId(getCurrentTenantId());
    }
    
    /**
     * 根据ID获取基础价格
     * @param id 基础价格ID
     * @return 基础价格信息
     */
    public Optional<BasePrice> getBasePriceById(Integer id) {
        return basePriceRepository.findByIdAndTenantId(id, getCurrentTenantId());
    }
    
    /**
     * 根据酒店编码获取基础价格列表
     * @param hotelCode 酒店编码
     * @return 基础价格列表
     */
    public List<BasePrice> getBasePricesByHotelCode(String hotelCode) {
        return basePriceRepository.findByTenantIdAndHotelCode(getCurrentTenantId(), hotelCode);
    }
    
    /**
     * 根据酒店编码、价格类型编码和房型编码获取基础价格列表
     * @param hotelCode 酒店编码
     * @param rateTypeCode 价格类型编码
     * @param roomTypeCode 房型编码
     * @return 基础价格列表
     */
    public List<BasePrice> getBasePricesByCode(String hotelCode, String rateTypeCode, String roomTypeCode) {
        return basePriceRepository.findByTenantIdAndHotelCodeAndRateTypeCodeAndRoomTypeCode(getCurrentTenantId(), hotelCode, rateTypeCode, roomTypeCode);
    }
    
    /**
     * 根据日期范围获取基础价格
     * @param hotelCode 酒店编码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 基础价格列表
     */
    public List<BasePrice> getBasePricesByDateRange(String hotelCode, Date startDate, Date endDate) {
        return basePriceRepository.findByTenantIdAndHotelCodeAndDateBetween(getCurrentTenantId(), hotelCode, startDate, endDate);
    }
    
    /**
     * 创建基础价格
     * @param basePrice 基础价格信息
     * @return 创建的基础价格信息
     */
    public BasePrice createBasePrice(BasePrice basePrice) {
        basePrice.setTenantId(getCurrentTenantId());
        // 计算价格
        basePrice.setPrice(calculatePrice(basePrice.getBasePrice()));
        return basePriceRepository.save(basePrice);
    }
    
    /**
     * 批量创建基础价格
     * @param basePrices 基础价格列表
     * @return 创建的基础价格列表
     */
    public List<BasePrice> createBatchBasePrices(List<BasePrice> basePrices) {
        Integer tenantId = getCurrentTenantId();
        // 计算每个价格
        basePrices.forEach(basePrice -> {
            basePrice.setTenantId(tenantId);
            basePrice.setPrice(calculatePrice(basePrice.getBasePrice()));
        });
        return basePriceRepository.saveAll(basePrices);
    }
    
    /**
     * 更新基础价格
     * @param id 基础价格ID
     * @param basePrice 基础价格信息
     * @return 更新后的基础价格信息
     */
    public BasePrice updateBasePrice(Integer id, BasePrice basePrice) {
        BasePrice existingBasePrice = getBasePriceById(id)
                .orElseThrow(() -> new RuntimeException("Base price not found or access denied"));
        
        // 计算价格
        basePrice.setPrice(calculatePrice(basePrice.getBasePrice()));
        
        existingBasePrice.setHotelCode(basePrice.getHotelCode());
        existingBasePrice.setRateTypeCode(basePrice.getRateTypeCode());
        existingBasePrice.setRoomTypeCode(basePrice.getRoomTypeCode());
        existingBasePrice.setBasePrice(basePrice.getBasePrice());
        existingBasePrice.setPrice(basePrice.getPrice());
        existingBasePrice.setDate(basePrice.getDate());
        existingBasePrice.setStatus(basePrice.getStatus());
        
        return basePriceRepository.save(existingBasePrice);
    }
    
    /**
     * 删除基础价格
     * @param id 基础价格ID
     */
    public void deleteBasePrice(Integer id) {
        BasePrice existing = getBasePriceById(id)
                .orElseThrow(() -> new RuntimeException("Base price not found or access denied"));
        basePriceRepository.delete(existing);
    }
    
    /**
     * 计算价格
     * @param basePrice 基准价格
     * @return 计算后的价格
     */
    private Double calculatePrice(Double basePrice) {
        // 这里可以添加价格计算逻辑，例如添加税费、服务费等
        // 暂时直接返回基准价格
        return basePrice;
    }
}
