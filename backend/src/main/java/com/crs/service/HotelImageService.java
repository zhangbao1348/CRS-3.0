package com.crs.service;

import com.crs.entity.HotelImage;
import com.crs.repository.HotelImageRepository;
import com.crs.repository.HotelRepository;
import com.crs.util.TenantContext;
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

    private final HotelImageRepository hotelImageRepository;
    private final HotelRepository hotelRepository;

    public HotelImageService(
            HotelImageRepository hotelImageRepository,
            HotelRepository hotelRepository) {
        this.hotelImageRepository = hotelImageRepository;
        this.hotelRepository = hotelRepository;
    }

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context missing");
        }
        return tenantId;
    }
    
    public HotelImage createImage(HotelImage image) {
        Integer tenantId = getCurrentTenantId();
        hotelRepository.findByHotelCodeAndTenantId(image.getHotelCode(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found or access denied"));
        image.setTenantId(tenantId);
        return hotelImageRepository.save(image);
    }
    
    public HotelImage updateImage(HotelImage image) {
        Integer tenantId = getCurrentTenantId();
        HotelImage existing = hotelImageRepository.findByIdAndTenantId(image.getId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found or access denied"));
        if (image.getHotelCode() != null && !image.getHotelCode().equals(existing.getHotelCode())) {
            throw new IllegalArgumentException("不允许变更图片所属酒店");
        }
        if (image.getImageType() != null) existing.setImageType(image.getImageType());
        if (image.getImagePath() != null) existing.setImagePath(image.getImagePath());
        if (image.getImageName() != null) existing.setImageName(image.getImageName());
        if (image.getDescription() != null) existing.setDescription(image.getDescription());
        if (image.getSortOrder() != null) existing.setSortOrder(image.getSortOrder());
        return hotelImageRepository.save(existing);
    }
    
    public void deleteImage(Integer id) {
        HotelImage existing = hotelImageRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Image not found or access denied"));
        hotelImageRepository.delete(existing);
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
        return hotelImageRepository.findByIdAndTenantId(id, getCurrentTenantId()).orElse(null);
    }
}
