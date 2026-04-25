package com.crs.service.impl;

import com.crs.entity.Archive;
import com.crs.repository.ArchiveRepository;
import com.crs.service.ArchiveService;
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
    
    @Override
    public List<Archive> getAllArchives() {
        return archiveRepository.findAll();
    }
    
    @Override
    public Optional<Archive> getById(Integer id) {
        return archiveRepository.findById(id);
    }
    
    @Override
    public Archive create(Archive archive) {
        return archiveRepository.save(archive);
    }
    
    @Override
    public Archive update(Integer id, Archive archive) {
        // 检查档案是否存在
        if (!archiveRepository.existsById(id)) {
            throw new IllegalArgumentException("档案不存在");
        }
        archive.setId(id);
        return archiveRepository.save(archive);
    }
    
    @Override
    public void delete(Integer id) {
        if (!archiveRepository.existsById(id)) {
            throw new IllegalArgumentException("档案不存在");
        }
        archiveRepository.deleteById(id);
    }
}
