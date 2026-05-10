package com.crs.repository;

import com.crs.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 房型数据访问接口 (RoomTypeRepository)
 * 
 * <p>提供对 {@link RoomType} 实体的数据库交互能力。支持基于酒店、集团以及房型特征的多维度查询。</p>
 */
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    
    /**
     * 获取指定酒店下的所有房型。
     * 
     * @param hotelId 酒店 ID
     * @return 房型列表
     */
    List<RoomType> findByHotelId(Integer hotelId);
    
    /**
     * 获取指定酒店下特定售卖状态的房型。
     * 
     * @param hotelId 酒店 ID
     * @param status 状态
     * @return 房型列表
     */
    List<RoomType> findByHotelIdAndStatus(Integer hotelId, RoomType.Status status);
    
    /**
     * 在指定酒店内，根据房型代码查找房型。
     * 
     * @param hotelId 酒店 ID
     * @param code 房型代码
     * @return 房型实体的 Optional 对象
     */
    Optional<RoomType> findByHotelIdAndCode(Integer hotelId, String code);
    
    /**
     * 查找所有关联了特定集团标准房型的本地房型。
     * 
     * @param groupRoomTypeId 集团房型 ID
     * @return 房型列表
     */
    List<RoomType> findByGroupRoomTypeId(Integer groupRoomTypeId);
    
    /**
     * 根据房型名称进行模糊搜索。
     * 
     * @param name 名称关键字
     * @return 匹配的房型列表
     */
    List<RoomType> findByNameContaining(String name);
    
    /**
     * 全局查找特定状态的房型。
     * 
     * @param status 状态
     * @return 房型列表
     */
    List<RoomType> findByStatus(RoomType.Status status);
    
    /**
     * 校验酒店内是否存在重复的房型代码。
     * 
     * @param hotelId 酒店 ID
     * @param code 待校验的代码
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByHotelIdAndCode(Integer hotelId, String code);
    
    /**
     * 根据酒店外部编码获取房型列表。
     * 
     * @param hotelCode 酒店外部编码
     * @return 房型列表
     */
    List<RoomType> findByHotelCode(String hotelCode);

    /**
     * 根据酒店外部编码和房型代码查找房型。
     * 
     * @param hotelCode 酒店外部编码
     * @param code 房型代码
     * @return 房型实体的 Optional 对象
     */
    Optional<RoomType> findByHotelCodeAndCode(String hotelCode, String code);

    /**
     * 根据酒店外部编码获取特定状态的房型。
     * 
     * @param hotelCode 酒店外部编码
     * @param status 状态
     * @return 房型列表
     */
    List<RoomType> findByHotelCodeAndStatus(String hotelCode, RoomType.Status status);

    /**
     * 校验在特定酒店编码下是否存在该房型代码。
     * 
     * @param hotelCode 酒店外部编码
     * @param code 房型代码
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByHotelCodeAndCode(String hotelCode, String code);

    /**
     * 根据集团房型编码获取关联的所有本地房型。
     * 
     * @param groupRoomTypeCode 集团房型编码
     * @return 房型列表
     */
    List<RoomType> findByGroupRoomTypeCode(String groupRoomTypeCode);
}

