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
    
    /**
     * 获取所有基础价格列表
     * @return 基础价格列表
     */
    public List<BasePrice> getAllBasePrices() {
        return basePriceRepository.findAll();
    }
    
    /**
     * 根据ID获取基础价格
     * @param id 基础价格ID
     * @return 基础价格信息
     */
    public Optional<BasePrice> getBasePriceById(Integer id) {
        return basePriceRepository.findById(id);
    }
    
    /**
     * 根据酒店ID获取基础价格列表
     * @param hotelId 酒店ID
     * @return 基础价格列表
     */
    public List<BasePrice> getBasePricesByHotelId(Integer hotelId) {
        return basePriceRepository.findByHotelId(hotelId);
    }
    
    /**
     * 根据酒店ID、价格类型ID和房型ID获取基础价格列表
     * @param hotelId 酒店ID
     * @param rateTypeId 价格类型ID
     * @param roomTypeId 房型ID
     * @return 基础价格列表
     */
    public List<BasePrice> getBasePricesByHotelIdAndRateTypeIdAndRoomTypeId(Integer hotelId, Integer rateTypeId, Integer roomTypeId) {
        return basePriceRepository.findByHotelIdAndRateTypeIdAndRoomTypeId(hotelId, rateTypeId, roomTypeId);
    }
    
    /**
     * 根据日期范围获取基础价格
     * @param hotelId 酒店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 基础价格列表
     */
    public List<BasePrice> getBasePricesByDateRange(Integer hotelId, Date startDate, Date endDate) {
        return basePriceRepository.findByHotelIdAndDateBetween(hotelId, startDate, endDate);
    }
    
    /**
     * 创建基础价格
     * @param basePrice 基础价格信息
     * @return 创建的基础价格信息
     */
    public BasePrice createBasePrice(BasePrice basePrice) {
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
        // 计算每个价格
        basePrices.forEach(basePrice -> {
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
        BasePrice existingBasePrice = basePriceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Base price not found"));
        
        // 计算价格
        basePrice.setPrice(calculatePrice(basePrice.getBasePrice()));
        
        existingBasePrice.setHotelId(basePrice.getHotelId());
        existingBasePrice.setRateTypeId(basePrice.getRateTypeId());
        existingBasePrice.setRoomTypeId(basePrice.getRoomTypeId());
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
        if (!basePriceRepository.existsById(id)) {
            throw new RuntimeException("Base price not found");
        }
        basePriceRepository.deleteById(id);
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
