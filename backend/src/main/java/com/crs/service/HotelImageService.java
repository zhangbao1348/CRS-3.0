package com.crs.service;

import com.crs.entity.HotelImage;
import com.crs.repository.HotelImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * HotelImageService 服务接口 (Service Interface)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【HotelImageService】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/09-系统设置.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 HotelImageService 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class HotelImageService {
    
    @Autowired
    private HotelImageRepository hotelImageRepository;
    
    public List<HotelImage> getImagesByHotelId(Integer hotelId) {
        return hotelImageRepository.findByHotelId(hotelId);
    }
    
    public List<HotelImage> getImagesByType(Integer hotelId, String imageType) {
        return hotelImageRepository.findByHotelIdAndImageType(hotelId, imageType);
    }
    
    public List<HotelImage> getImagesByHotelIdOrderBySort(Integer hotelId) {
        return hotelImageRepository.findByHotelIdOrderBySortOrderAsc(hotelId);
    }
    
    public HotelImage createImage(HotelImage image) {
        return hotelImageRepository.save(image);
    }
    
    public HotelImage updateImage(HotelImage image) {
        return hotelImageRepository.save(image);
    }
    
    public void deleteImage(Integer id) {
        hotelImageRepository.deleteById(id);
    }
    
    public void deleteImagesByHotelId(Integer hotelId) {
        hotelImageRepository.deleteByHotelId(hotelId);
    }
    
    public List<HotelImage> getImagesByHotelCode(String hotelCode) {
        return hotelImageRepository.findByTenantIdAndHotelCode(com.crs.util.TenantContext.getTenantId(), hotelCode);
    }

    public List<HotelImage> getImagesByHotelCodeAndType(String hotelCode, String imageType) {
        return hotelImageRepository.findByTenantIdAndHotelCodeAndImageType(com.crs.util.TenantContext.getTenantId(), hotelCode, imageType);
    }

    public List<HotelImage> getImagesByHotelCodeOrderBySort(String hotelCode) {
        return hotelImageRepository.findByTenantIdAndHotelCodeOrderBySortOrderAsc(com.crs.util.TenantContext.getTenantId(), hotelCode);
    }

    public void deleteImagesByHotelCode(String hotelCode) {
        hotelImageRepository.deleteByTenantIdAndHotelCode(com.crs.util.TenantContext.getTenantId(), hotelCode);
    }

    public HotelImage getImageById(Integer id) {
        return hotelImageRepository.findById(id).orElse(null);
    }
}