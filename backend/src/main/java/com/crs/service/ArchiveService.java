package com.crs.service;

import com.crs.entity.Archive;

import java.util.List;
import java.util.Optional;

/**
 * 档案服务接口
 * 用于档案的业务逻辑处理
 */
public interface ArchiveService {
    
    /**
     * 获取所有档案
     * @return 档案列表
     */
    List<Archive> getAllArchives();
    
    /**
     * 根据ID获取档案
     * @param id 档案ID
     * @return 档案
     */
    Optional<Archive> getById(Integer id);
    
    /**
     * 创建档案
     * @param archive 档案
     * @return 创建的档案
     */
    Archive create(Archive archive);
    
    /**
     * 更新档案
     * @param id 档案ID
     * @param archive 档案
     * @return 更新后的档案
     */
    Archive update(Integer id, Archive archive);
    
    /**
     * 删除档案
     * @param id 档案ID
     */
    void delete(Integer id);
}
