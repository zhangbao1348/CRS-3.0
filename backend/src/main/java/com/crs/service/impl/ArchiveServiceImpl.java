package com.crs.service.impl;

import com.crs.entity.Archive;
import com.crs.repository.ArchiveRepository;
import com.crs.service.ArchiveService;
import com.crs.util.TenantContext;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 档案服务实现类
 * 提供档案管理的业务逻辑处理
 */
@Service
@Transactional
public class ArchiveServiceImpl implements ArchiveService {
    
    @Autowired
    private ArchiveRepository archiveRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context missing");
        }
        return tenantId;
    }
    
    @Override
    public List<Archive> getAllArchives() {
        return archiveRepository.findByGroupId(getCurrentTenantId());
    }
    
    @Override
    public Optional<Archive> getById(Integer id) {
        return archiveRepository.findByIdAndGroupId(id, getCurrentTenantId());
    }
    
    @Override
    public Archive create(Archive archive) {
        Integer tenantId = getCurrentTenantId();
        validateArchive(archive);
        if (archiveRepository.existsByGroupIdAndArchiveId(tenantId, archive.getArchiveId())) {
            throw new IllegalArgumentException("该档案 ID 已存在");
        }
        archive.setId(null);
        archive.setGroupId(tenantId);
        return archiveRepository.save(archive);
    }
    
    @Override
    public Archive update(Integer id, Archive archive) {
        Integer tenantId = getCurrentTenantId();
        Archive existing = archiveRepository.findByIdAndGroupId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("档案不存在或无权访问"));
        if (archive.getArchiveId() != null && !existing.getArchiveId().equals(archive.getArchiveId())) {
            throw new IllegalArgumentException("档案 ID 保存后不可修改");
        }
        archive.setArchiveId(existing.getArchiveId());
        validateArchive(archive);
        existing.setName(archive.getName());
        existing.setType(archive.getType());
        existing.setBookingCode(archive.getBookingCode());
        existing.setContactName(archive.getContactName());
        existing.setContactPhone(archive.getContactPhone());
        existing.setAddress(archive.getAddress());
        existing.setRateCodes(archive.getRateCodes());
        existing.setStatus(archive.getStatus());
        return archiveRepository.save(existing);
    }
    
    @Override
    public void delete(Integer id) {
        Archive archive = archiveRepository.findByIdAndGroupId(id, getCurrentTenantId())
                .orElseThrow(() -> new IllegalArgumentException("档案不存在或无权访问"));
        archiveRepository.delete(archive);
    }

    /** 实施档案必填、编码与枚举规则，防止绕过页面提交无效数据。 */
    private void validateArchive(Archive archive) {
        if (!CodeValidator.isValid(archive.getArchiveId())) {
            throw new IllegalArgumentException("档案 ID 仅允许输入英文字母、数字和下划线");
        }
        if (archive.getName() == null || archive.getName().isBlank()) {
            throw new IllegalArgumentException("档案名称不能为空");
        }
        if (!("公司".equals(archive.getType()) || "旅行社".equals(archive.getType()))) {
            throw new IllegalArgumentException("档案类型无效");
        }
        if (archive.getStatus() == null || archive.getStatus().isBlank()) {
            archive.setStatus("启用");
        }
        if ("active".equals(archive.getStatus())) {
            archive.setStatus("启用");
        } else if ("inactive".equals(archive.getStatus())) {
            archive.setStatus("停用");
        }
        if (!("启用".equals(archive.getStatus()) || "停用".equals(archive.getStatus()))) {
            throw new IllegalArgumentException("档案状态无效");
        }
    }
}
