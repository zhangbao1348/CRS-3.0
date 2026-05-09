package com.crs.repository;

import com.crs.entity.PersonDiffSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 人数差价体系仓库接口
 * 用于人数差价体系数据的CRUD操作
 */
@Repository
public interface PersonDiffSystemRepository extends JpaRepository<PersonDiffSystem, Integer> {
    
    /**
     * 根据酒店ID查询人数差价体系列表
     * @param hotelId 酒店ID
     * @return 人数差价体系列表
     */
    List<PersonDiffSystem> findByHotelId(Integer hotelId);
    
    /**
     * 根据酒店ID和状态查询人数差价体系列表
     * @param hotelId 酒店ID
     * @param status 状态
     * @return 人数差价体系列表
     */
    List<PersonDiffSystem> findByHotelIdAndStatus(Integer hotelId, PersonDiffSystem.Status status);
    
    /**
     * 根据酒店ID和名称查询人数差价体系
     * @param hotelId 酒店ID
     * @param name 体系名称
     * @return 人数差价体系信息
     */
    Optional<PersonDiffSystem> findByHotelIdAndName(Integer hotelId, String name);
    
    /**
     * 根据状态查询人数差价体系
     * @param status 状态
     * @return 人数差价体系列表
     */
    List<PersonDiffSystem> findByStatus(PersonDiffSystem.Status status);

    List<PersonDiffSystem> findByHotelCode(String hotelCode);

    List<PersonDiffSystem> findByHotelCodeAndStatus(String hotelCode, PersonDiffSystem.Status status);

    Optional<PersonDiffSystem> findByHotelCodeAndName(String hotelCode, String name);
}
