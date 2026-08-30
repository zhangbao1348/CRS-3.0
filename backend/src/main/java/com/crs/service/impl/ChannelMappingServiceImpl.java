package com.crs.service.impl;

import com.crs.entity.ChannelHotelMapping;
import com.crs.entity.ChannelRoomTypeMapping;
import com.crs.entity.ChannelRateCodeMapping;
import com.crs.repository.ChannelHotelMappingRepository;
import com.crs.repository.ChannelRoomTypeMappingRepository;
import com.crs.repository.ChannelRateCodeMappingRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.HotelRoomTypeRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.repository.TenantChannelRepository;
import com.crs.service.ChannelMappingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 渠道映射服务实现类
 * 提供渠道酒店/房型/房价映射管理的业务逻辑处理
 */
@Service
@Transactional
public class ChannelMappingServiceImpl implements ChannelMappingService {
    
    private final ChannelHotelMappingRepository hotelMappingRepository;
    private final ChannelRoomTypeMappingRepository roomTypeMappingRepository;
    private final ChannelRateCodeMappingRepository rateCodeMappingRepository;
    private final HotelRepository hotelRepository;
    private final TenantChannelRepository tenantChannelRepository;
    private final HotelRoomTypeRepository hotelRoomTypeRepository;
    private final RatePlanRepository ratePlanRepository;

    public ChannelMappingServiceImpl(ChannelHotelMappingRepository hotelMappingRepository,
                                     ChannelRoomTypeMappingRepository roomTypeMappingRepository,
                                     ChannelRateCodeMappingRepository rateCodeMappingRepository,
                                     HotelRepository hotelRepository,
                                     TenantChannelRepository tenantChannelRepository,
                                     HotelRoomTypeRepository hotelRoomTypeRepository,
                                     RatePlanRepository ratePlanRepository) {
        this.hotelMappingRepository = hotelMappingRepository;
        this.roomTypeMappingRepository = roomTypeMappingRepository;
        this.rateCodeMappingRepository = rateCodeMappingRepository;
        this.hotelRepository = hotelRepository;
        this.tenantChannelRepository = tenantChannelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.ratePlanRepository = ratePlanRepository;
    }
    
    private void validateTenantAccess(String channelCode, String hotelCode) {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }
        
        if (hotelCode != null) {
            hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Access denied or hotel not found for code: " + hotelCode));
        }
        if (channelCode != null) {
            com.crs.entity.TenantChannel tc = tenantChannelRepository.findByTenantIdAndChannelCode(tenantId, channelCode);
            if (tc == null) {
                throw new RuntimeException("Access denied or channel not found for code: " + channelCode);
            }
        }
    }
    
    // ===== 酒店映射 =====
    
    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    // ===== 酒店映射 =====
    
    @Override
    public List<ChannelHotelMapping> getHotelMappings(Integer channelId, Integer hotelId) {
        // 兼容性方法：默认返回当前租户下的全量映射
        return hotelMappingRepository.findByTenantId(getCurrentTenantId());
    }

    @Override
    public List<ChannelHotelMapping> getHotelMappingsByCode(String channelCode, String hotelCode) {
        validateTenantAccess(channelCode, hotelCode);
        Integer tenantId = getCurrentTenantId();
        if (channelCode != null && hotelCode != null) {
            return hotelMappingRepository.findByTenantIdAndChannelCodeAndHotelCode(tenantId, channelCode, hotelCode);
        } else if (channelCode != null) {
            return hotelMappingRepository.findByTenantIdAndChannelCode(tenantId, channelCode);
        } else if (hotelCode != null) {
            return hotelMappingRepository.findByTenantIdAndHotelCode(tenantId, hotelCode);
        }
        return hotelMappingRepository.findByTenantId(tenantId);
    }
    
    @Override
    public ChannelHotelMapping createHotelMapping(ChannelHotelMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelHotelMapping target = new ChannelHotelMapping();
        copyHotelMapping(mapping, target, tenantId, null);
        return hotelMappingRepository.save(target);
    }
    
    @Override
    public ChannelHotelMapping updateHotelMapping(Integer id, ChannelHotelMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelHotelMapping existing = hotelMappingRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("酒店映射不存在或无权访问"));
        
        copyHotelMapping(mapping, existing, tenantId, id);
        return hotelMappingRepository.save(existing);
    }
    
    @Override
    public void deleteHotelMapping(Integer id) {
        ChannelHotelMapping existing = hotelMappingRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("酒店映射不存在或无权访问"));
        
        hotelMappingRepository.delete(existing);
    }
    
    @Override
    public ChannelHotelMapping toggleHotelMappingStatus(Integer id) {
        ChannelHotelMapping mapping = hotelMappingRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("酒店映射不存在或无权访问"));
        
        mapping.setStatus("active".equals(mapping.getStatus()) ? "inactive" : "active");
        return hotelMappingRepository.save(mapping);
    }
    
    // ===== 房型映射 =====
    
    @Override
    public List<ChannelRoomTypeMapping> getRoomTypeMappings(Integer channelId, Integer hotelId) {
        return roomTypeMappingRepository.findByTenantId(getCurrentTenantId());
    }

    @Override
    public List<ChannelRoomTypeMapping> getRoomTypeMappingsByCode(String channelCode, String hotelCode) {
        validateTenantAccess(channelCode, hotelCode);
        Integer tenantId = getCurrentTenantId();
        if (channelCode != null && hotelCode != null) {
            return roomTypeMappingRepository.findByTenantIdAndChannelCodeAndHotelCode(tenantId, channelCode, hotelCode);
        } else if (channelCode != null) {
            return roomTypeMappingRepository.findByTenantIdAndChannelCode(tenantId, channelCode);
        } else if (hotelCode != null) {
            return roomTypeMappingRepository.findByTenantIdAndHotelCode(tenantId, hotelCode);
        }
        return roomTypeMappingRepository.findByTenantId(tenantId);
    }
    
    @Override
    public ChannelRoomTypeMapping createRoomTypeMapping(ChannelRoomTypeMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelRoomTypeMapping target = new ChannelRoomTypeMapping();
        copyRoomTypeMapping(mapping, target, tenantId, null);
        return roomTypeMappingRepository.save(target);
    }
    
    @Override
    public ChannelRoomTypeMapping updateRoomTypeMapping(Integer id, ChannelRoomTypeMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelRoomTypeMapping existing = roomTypeMappingRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("房型映射不存在或无权访问"));
                
        copyRoomTypeMapping(mapping, existing, tenantId, id);
        return roomTypeMappingRepository.save(existing);
    }
    
    @Override
    public void deleteRoomTypeMapping(Integer id) {
        ChannelRoomTypeMapping existing = roomTypeMappingRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("房型映射不存在或无权访问"));
        
        roomTypeMappingRepository.delete(existing);
    }
    
    @Override
    public ChannelRoomTypeMapping toggleRoomTypeMappingStatus(Integer id) {
        ChannelRoomTypeMapping mapping = roomTypeMappingRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("房型映射不存在或无权访问"));
        
        mapping.setStatus("active".equals(mapping.getStatus()) ? "inactive" : "active");
        return roomTypeMappingRepository.save(mapping);
    }
    
    // ===== 房价映射 =====
    
    @Override
    public List<ChannelRateCodeMapping> getRateCodeMappings(Integer channelId, Integer hotelId) {
        return rateCodeMappingRepository.findByTenantId(getCurrentTenantId());
    }

    @Override
    public List<ChannelRateCodeMapping> getRateCodeMappingsByCode(String channelCode, String hotelCode) {
        validateTenantAccess(channelCode, hotelCode);
        Integer tenantId = getCurrentTenantId();
        if (channelCode != null && hotelCode != null) {
            return rateCodeMappingRepository.findByTenantIdAndChannelCodeAndHotelCode(tenantId, channelCode, hotelCode);
        } else if (channelCode != null) {
            return rateCodeMappingRepository.findByTenantIdAndChannelCode(tenantId, channelCode);
        } else if (hotelCode != null) {
            return rateCodeMappingRepository.findByTenantIdAndHotelCode(tenantId, hotelCode);
        }
        return rateCodeMappingRepository.findByTenantId(tenantId);
    }
    
    @Override
    public ChannelRateCodeMapping createRateCodeMapping(ChannelRateCodeMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelRateCodeMapping target = new ChannelRateCodeMapping();
        copyRateCodeMapping(mapping, target, tenantId, null);
        return rateCodeMappingRepository.save(target);
    }
    
    @Override
    public ChannelRateCodeMapping updateRateCodeMapping(Integer id, ChannelRateCodeMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelRateCodeMapping existing = rateCodeMappingRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("房价映射不存在或无权访问"));
        
        copyRateCodeMapping(mapping, existing, tenantId, id);
        return rateCodeMappingRepository.save(existing);
    }
    
    @Override
    public void deleteRateCodeMapping(Integer id) {
        ChannelRateCodeMapping existing = rateCodeMappingRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("房价映射不存在或无权访问"));
        
        rateCodeMappingRepository.delete(existing);
    }
    
    @Override
    public ChannelRateCodeMapping toggleRateCodeMappingStatus(Integer id) {
        ChannelRateCodeMapping mapping = rateCodeMappingRepository.findByIdAndTenantId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("房价映射不存在或无权访问"));
        
        mapping.setStatus("active".equals(mapping.getStatus()) ? "inactive" : "active");
        return rateCodeMappingRepository.save(mapping);
    }

    /** 解析可信渠道、酒店并对白名单字段进行复制。 */
    private void copyHotelMapping(ChannelHotelMapping source, ChannelHotelMapping target,
                                  Integer tenantId, Integer currentId) {
        requireText(source.getChannelHotelCode(), "渠道酒店代码不能为空");
        var references = resolveReferences(source.getChannelCode(), source.getHotelCode(), tenantId);
        boolean duplicate = hotelMappingRepository
                .findByTenantIdAndChannelCodeAndHotelCode(tenantId, source.getChannelCode(), source.getHotelCode())
                .stream().anyMatch(item -> !item.getId().equals(currentId));
        if (duplicate) {
            throw new IllegalArgumentException("该渠道与酒店的映射已存在");
        }
        target.setTenantId(tenantId);
        target.setChannelId(references.channel().getId());
        target.setChannelCode(references.channel().getChannelCode());
        target.setChannelName(references.channel().getChannelName());
        target.setHotelId(references.hotel().getId());
        target.setHotelCode(references.hotel().getHotelCode());
        target.setHotelName(references.hotel().getChineseName());
        target.setChannelHotelCode(source.getChannelHotelCode().trim());
        target.setStatus(normalizeStatus(source.getStatus()));
    }

    /** 房型来源字段由租户内酒店房型主数据回填，客户端只能提供渠道侧映射。 */
    private void copyRoomTypeMapping(ChannelRoomTypeMapping source, ChannelRoomTypeMapping target,
                                     Integer tenantId, Integer currentId) {
        requireText(source.getChannelRoomTypeCode(), "渠道房型代码不能为空");
        resolveReferences(source.getChannelCode(), source.getHotelCode(), tenantId);
        var roomType = hotelRoomTypeRepository
                .findByTenantIdAndHotelCodeAndRoomTypeCode(tenantId, source.getHotelCode(), source.getRoomTypeCode())
                .orElseThrow(() -> new IllegalArgumentException("酒店房型不存在或无权访问"));
        boolean duplicate = roomTypeMappingRepository
                .findByTenantIdAndChannelCodeAndHotelCodeAndRoomTypeCode(
                        tenantId, source.getChannelCode(), source.getHotelCode(), source.getRoomTypeCode())
                .stream().anyMatch(item -> !item.getId().equals(currentId));
        if (duplicate) {
            throw new IllegalArgumentException("该渠道、酒店和房型的映射已存在");
        }
        var references = resolveReferences(source.getChannelCode(), source.getHotelCode(), tenantId);
        target.setTenantId(tenantId);
        target.setChannelId(references.channel().getId());
        target.setHotelId(references.hotel().getId());
        target.setRoomTypeId(roomType.getId());
        target.setChannelCode(references.channel().getChannelCode());
        target.setChannelName(references.channel().getChannelName());
        target.setHotelCode(references.hotel().getHotelCode());
        target.setHotelName(references.hotel().getChineseName());
        target.setRoomTypeCode(roomType.getRoomTypeCode());
        target.setRoomTypeName(roomType.getRoomTypeName());
        target.setChannelRoomTypeCode(source.getChannelRoomTypeCode().trim());
        target.setChannelRoomTypeName(source.getChannelRoomTypeName());
        target.setStatus(normalizeStatus(source.getStatus()));
    }

    /** 房价来源字段由价格计划主数据回填，避免客户端伪造名称或跨酒店引用。 */
    private void copyRateCodeMapping(ChannelRateCodeMapping source, ChannelRateCodeMapping target,
                                     Integer tenantId, Integer currentId) {
        requireText(source.getChannelRateCode(), "渠道房价代码不能为空");
        resolveReferences(source.getChannelCode(), source.getHotelCode(), tenantId);
        var ratePlan = ratePlanRepository
                .findByTenantIdAndHotelCodeAndRateCode(tenantId, source.getHotelCode(), source.getRateCode())
                .orElseThrow(() -> new IllegalArgumentException("价格计划不存在或无权访问"));
        boolean duplicate = rateCodeMappingRepository
                .findByTenantIdAndChannelCodeAndHotelCodeAndRateCode(
                        tenantId, source.getChannelCode(), source.getHotelCode(), source.getRateCode())
                .stream().anyMatch(item -> !item.getId().equals(currentId));
        if (duplicate) {
            throw new IllegalArgumentException("该渠道、酒店和房价码的映射已存在");
        }
        var references = resolveReferences(source.getChannelCode(), source.getHotelCode(), tenantId);
        target.setTenantId(tenantId);
        target.setChannelId(references.channel().getId());
        target.setHotelId(references.hotel().getId());
        target.setRateCodeId(ratePlan.getId());
        target.setChannelCode(references.channel().getChannelCode());
        target.setChannelName(references.channel().getChannelName());
        target.setHotelCode(references.hotel().getHotelCode());
        target.setHotelName(references.hotel().getChineseName());
        target.setRateCode(ratePlan.getRateCode());
        target.setRateCodeName(ratePlan.getRateName());
        target.setChannelRateCode(source.getChannelRateCode().trim());
        target.setChannelRateName(source.getChannelRateName());
        target.setMarkup(source.getMarkup());
        target.setStatus(normalizeStatus(source.getStatus()));
    }

    private MappingReferences resolveReferences(String channelCode, String hotelCode, Integer tenantId) {
        requireText(channelCode, "渠道代码不能为空");
        requireText(hotelCode, "酒店代码不能为空");
        var channel = tenantChannelRepository.findByTenantIdAndChannelCode(tenantId, channelCode);
        if (channel == null) {
            throw new IllegalArgumentException("渠道不存在或无权访问");
        }
        var hotel = hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("酒店不存在或无权访问"));
        return new MappingReferences(channel, hotel);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "active";
        }
        if (!"active".equals(status) && !"inactive".equals(status)) {
            throw new IllegalArgumentException("映射状态不合法");
        }
        return status;
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private record MappingReferences(com.crs.entity.TenantChannel channel, com.crs.entity.Hotel hotel) {
    }
}
