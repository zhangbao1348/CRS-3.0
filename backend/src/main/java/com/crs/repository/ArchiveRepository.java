package com.crs.repository;

import com.crs.entity.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 档案仓库接口
 * 用于档案数据的CRUD操作
 */
@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Integer> {

    /** 在集团（租户）维度下根据主键查询档案。 */
    Optional<Archive> findByIdAndGroupId(Integer id, Integer groupId);
    
    /**
     * 根据集团ID查询档案
     * @param groupId 集团ID
     * @return 档案列表
     */
    List<Archive> findByGroupId(Integer groupId);

    /** 集团内校验档案业务 ID 唯一性。 */
    boolean existsByGroupIdAndArchiveId(Integer groupId, String archiveId);
    
    /**
     * 根据类型查询档案
     * @param type 档案类型
     * @return 档案列表
     */
    List<Archive> findByType(String type);
    
    /**
     * 检查档案名称是否存在
     * @param name 档案名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据预订代码查询激活的档案
     * @param bookingCode 预订代码
     * @return 档案Optional
     */
    Optional<Archive> findByBookingCode(String bookingCode);

    /** 在集团（租户）维度下根据预订码查询档案。 */
    Optional<Archive> findByGroupIdAndBookingCode(Integer groupId, String bookingCode);
}
