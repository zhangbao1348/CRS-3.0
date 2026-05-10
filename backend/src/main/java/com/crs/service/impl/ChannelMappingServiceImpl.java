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
    
    @Override
    public List<ChannelHotelMapping> getHotelMappings(Integer channelId, Integer hotelId) {
        if (channelId != null && hotelId != null) {
            return hotelMappingRepository.findByChannelIdAndHotelId(channelId, hotelId);
        } else if (channelId != null) {
            return hotelMappingRepository.findByChannelId(channelId);
        } else if (hotelId != null) {
            return hotelMappingRepository.findByHotelId(hotelId);
        }
        return hotelMappingRepository.findAll();
    }

    @Override
    public List<ChannelHotelMapping> getHotelMappingsByCode(String channelCode, String hotelCode) {
        validateTenantAccess(channelCode, hotelCode);
        if (channelCode != null && hotelCode != null) {
            return hotelMappingRepository.findByChannelCodeAndHotelCode(channelCode, hotelCode);
        } else if (channelCode != null) {
            return hotelMappingRepository.findByChannelCode(channelCode);
        } else if (hotelCode != null) {
            return hotelMappingRepository.findByHotelCode(hotelCode);
        }
        // 如果都为空，需要考虑是否应该返回当前租户下的所有映射，但目前映射表没有 tenantId，直接 findAll 会泄露数据。
        // 为了安全起见，如果不传参数，就不返回任何数据或报错
        throw new IllegalArgumentException("必须提供 channelCode 或 hotelCode");
    }
    
    @Override
    public ChannelHotelMapping createHotelMapping(ChannelHotelMapping mapping) {
        return hotelMappingRepository.save(mapping);
    }
    
    @Override
    public ChannelHotelMapping updateHotelMapping(Integer id, ChannelHotelMapping mapping) {
        if (!hotelMappingRepository.existsById(id)) {
            throw new IllegalArgumentException("酒店映射不存在");
        }
        mapping.setId(id);
        return hotelMappingRepository.save(mapping);
    }
    
    @Override
    public void deleteHotelMapping(Integer id) {
        if (!hotelMappingRepository.existsById(id)) {
            throw new IllegalArgumentException("酒店映射不存在");
        }
        hotelMappingRepository.deleteById(id);
    }
    
    @Override
    public ChannelHotelMapping toggleHotelMappingStatus(Integer id) {
        Optional<ChannelHotelMapping> opt = hotelMappingRepository.findById(id);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("酒店映射不存在");
        }
        ChannelHotelMapping mapping = opt.get();
        mapping.setStatus("active".equals(mapping.getStatus()) ? "inactive" : "active");
        return hotelMappingRepository.save(mapping);
    }
    
    // ===== 房型映射 =====
    
    @Override
    public List<ChannelRoomTypeMapping> getRoomTypeMappings(Integer channelId, Integer hotelId) {
        if (channelId != null && hotelId != null) {
            return roomTypeMappingRepository.findByChannelIdAndHotelId(channelId, hotelId);
        } else if (channelId != null) {
            return roomTypeMappingRepository.findByChannelId(channelId);
        } else if (hotelId != null) {
            return roomTypeMappingRepository.findByHotelId(hotelId);
        }
        return roomTypeMappingRepository.findAll();
    }

    @Override
    public List<ChannelRoomTypeMapping> getRoomTypeMappingsByCode(String channelCode, String hotelCode) {
        validateTenantAccess(channelCode, hotelCode);
        if (channelCode != null && hotelCode != null) {
            return roomTypeMappingRepository.findByChannelCodeAndHotelCode(channelCode, hotelCode);
        } else if (channelCode != null) {
            return roomTypeMappingRepository.findByChannelCode(channelCode);
        } else if (hotelCode != null) {
            return roomTypeMappingRepository.findByHotelCode(hotelCode);
        }
        throw new IllegalArgumentException("必须提供 channelCode 或 hotelCode");
    }
    
    @Override
    public ChannelRoomTypeMapping createRoomTypeMapping(ChannelRoomTypeMapping mapping) {
        return roomTypeMappingRepository.save(mapping);
    }
    
    @Override
    public ChannelRoomTypeMapping updateRoomTypeMapping(Integer id, ChannelRoomTypeMapping mapping) {
        if (!roomTypeMappingRepository.existsById(id)) {
            throw new IllegalArgumentException("房型映射不存在");
        }
        mapping.setId(id);
        return roomTypeMappingRepository.save(mapping);
    }
    
    @Override
    public void deleteRoomTypeMapping(Integer id) {
        if (!roomTypeMappingRepository.existsById(id)) {
            throw new IllegalArgumentException("房型映射不存在");
        }
        roomTypeMappingRepository.deleteById(id);
    }
    
    @Override
    public ChannelRoomTypeMapping toggleRoomTypeMappingStatus(Integer id) {
        Optional<ChannelRoomTypeMapping> opt = roomTypeMappingRepository.findById(id);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("房型映射不存在");
        }
        ChannelRoomTypeMapping mapping = opt.get();
        mapping.setStatus("active".equals(mapping.getStatus()) ? "inactive" : "active");
        return roomTypeMappingRepository.save(mapping);
    }
    
    // ===== 房价映射 =====
    
    @Override
    public List<ChannelRateCodeMapping> getRateCodeMappings(Integer channelId, Integer hotelId) {
        if (channelId != null && hotelId != null) {
            return rateCodeMappingRepository.findByChannelIdAndHotelId(channelId, hotelId);
        } else if (channelId != null) {
            return rateCodeMappingRepository.findByChannelId(channelId);
        } else if (hotelId != null) {
            return rateCodeMappingRepository.findByHotelId(hotelId);
        }
        return rateCodeMappingRepository.findAll();
    }

    @Override
    public List<ChannelRateCodeMapping> getRateCodeMappingsByCode(String channelCode, String hotelCode) {
        validateTenantAccess(channelCode, hotelCode);
        if (channelCode != null && hotelCode != null) {
            return rateCodeMappingRepository.findByChannelCodeAndHotelCode(channelCode, hotelCode);
        } else if (channelCode != null) {
            return rateCodeMappingRepository.findByChannelCode(channelCode);
        } else if (hotelCode != null) {
            return rateCodeMappingRepository.findByHotelCode(hotelCode);
        }
        throw new IllegalArgumentException("必须提供 channelCode 或 hotelCode");
    }
    
    @Override
    public ChannelRateCodeMapping createRateCodeMapping(ChannelRateCodeMapping mapping) {
        return rateCodeMappingRepository.save(mapping);
    }
    
    @Override
    public ChannelRateCodeMapping updateRateCodeMapping(Integer id, ChannelRateCodeMapping mapping) {
        if (!rateCodeMappingRepository.existsById(id)) {
            throw new IllegalArgumentException("房价映射不存在");
        }
        mapping.setId(id);
        return rateCodeMappingRepository.save(mapping);
    }
    
    @Override
    public void deleteRateCodeMapping(Integer id) {
        if (!rateCodeMappingRepository.existsById(id)) {
            throw new IllegalArgumentException("房价映射不存在");
        }
        rateCodeMappingRepository.deleteById(id);
    }
    
    @Override
    public ChannelRateCodeMapping toggleRateCodeMappingStatus(Integer id) {
        Optional<ChannelRateCodeMapping> opt = rateCodeMappingRepository.findById(id);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("房价映射不存在");
        }
        ChannelRateCodeMapping mapping = opt.get();
        mapping.setStatus("active".equals(mapping.getStatus()) ? "inactive" : "active");
        return rateCodeMappingRepository.save(mapping);
    }
}
