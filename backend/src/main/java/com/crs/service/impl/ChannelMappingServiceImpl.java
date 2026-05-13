package com.crs.service.impl;

import com.crs.entity.ChannelHotelMapping;
import com.crs.entity.ChannelRoomTypeMapping;
import com.crs.entity.ChannelRateCodeMapping;
import com.crs.repository.ChannelHotelMappingRepository;
import com.crs.repository.ChannelRoomTypeMappingRepository;
import com.crs.repository.ChannelRateCodeMappingRepository;
import com.crs.service.ChannelMappingService;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private ChannelHotelMappingRepository hotelMappingRepository;
    
    @Autowired
    private ChannelRoomTypeMappingRepository roomTypeMappingRepository;
    
    @Autowired
    private ChannelRateCodeMappingRepository rateCodeMappingRepository;
    
    @Autowired
    private com.crs.repository.HotelRepository hotelRepository;
    
    @Autowired
    private com.crs.repository.TenantChannelRepository tenantChannelRepository;
    
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
        mapping.setTenantId(getCurrentTenantId());
        return hotelMappingRepository.save(mapping);
    }
    
    @Override
    public ChannelHotelMapping updateHotelMapping(Integer id, ChannelHotelMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelHotelMapping existing = hotelMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("酒店映射不存在或无权访问"));
        
        mapping.setId(id);
        mapping.setTenantId(tenantId);
        return hotelMappingRepository.save(mapping);
    }
    
    @Override
    public void deleteHotelMapping(Integer id) {
        ChannelHotelMapping existing = hotelMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(getCurrentTenantId()))
                .orElseThrow(() -> new IllegalArgumentException("酒店映射不存在或无权访问"));
        
        hotelMappingRepository.delete(existing);
    }
    
    @Override
    public ChannelHotelMapping toggleHotelMappingStatus(Integer id) {
        ChannelHotelMapping mapping = hotelMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(getCurrentTenantId()))
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
        mapping.setTenantId(getCurrentTenantId());
        return roomTypeMappingRepository.save(mapping);
    }
    
    @Override
    public ChannelRoomTypeMapping updateRoomTypeMapping(Integer id, ChannelRoomTypeMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelRoomTypeMapping existing = roomTypeMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("房型映射不存在或无权访问"));
                
        mapping.setId(id);
        mapping.setTenantId(tenantId);
        return roomTypeMappingRepository.save(mapping);
    }
    
    @Override
    public void deleteRoomTypeMapping(Integer id) {
        ChannelRoomTypeMapping existing = roomTypeMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(getCurrentTenantId()))
                .orElseThrow(() -> new IllegalArgumentException("房型映射不存在或无权访问"));
        
        roomTypeMappingRepository.delete(existing);
    }
    
    @Override
    public ChannelRoomTypeMapping toggleRoomTypeMappingStatus(Integer id) {
        ChannelRoomTypeMapping mapping = roomTypeMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(getCurrentTenantId()))
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
        mapping.setTenantId(getCurrentTenantId());
        return rateCodeMappingRepository.save(mapping);
    }
    
    @Override
    public ChannelRateCodeMapping updateRateCodeMapping(Integer id, ChannelRateCodeMapping mapping) {
        Integer tenantId = getCurrentTenantId();
        ChannelRateCodeMapping existing = rateCodeMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("房价映射不存在或无权访问"));
        
        mapping.setId(id);
        mapping.setTenantId(tenantId);
        return rateCodeMappingRepository.save(mapping);
    }
    
    @Override
    public void deleteRateCodeMapping(Integer id) {
        ChannelRateCodeMapping existing = rateCodeMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(getCurrentTenantId()))
                .orElseThrow(() -> new IllegalArgumentException("房价映射不存在或无权访问"));
        
        rateCodeMappingRepository.delete(existing);
    }
    
    @Override
    public ChannelRateCodeMapping toggleRateCodeMappingStatus(Integer id) {
        ChannelRateCodeMapping mapping = rateCodeMappingRepository.findById(id)
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(getCurrentTenantId()))
                .orElseThrow(() -> new IllegalArgumentException("房价映射不存在或无权访问"));
        
        mapping.setStatus("active".equals(mapping.getStatus()) ? "inactive" : "active");
        return rateCodeMappingRepository.save(mapping);
    }
}
