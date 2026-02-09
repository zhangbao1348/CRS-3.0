package com.crs.repository;

import com.crs.entity.ChannelCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道码仓库接口
 */
@Repository
public interface ChannelCodeRepository extends JpaRepository<ChannelCode, Integer> {

    /**
     * 根据父ID查询渠道码
     */
    List<ChannelCode> findByParentId(Integer parentId);

    /**
     * 根据CODE查询渠道码
     */
    ChannelCode findByCode(String code);
}
