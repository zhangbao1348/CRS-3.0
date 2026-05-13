package com.crs.repository;

import com.crs.entity.GroupRoomType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 集团房型数据访问接口 (GroupRoomTypeRepository)
 * 
 * <p>提供对 {@link GroupRoomType} 实体的数据库交互能力。支持基于集团编码、分类、排序等多维度的查询与统计。</p>
 */
@Repository
public interface GroupRoomTypeRepository extends JpaRepository<GroupRoomType, Integer> {
    
    @EntityGraph(attributePaths = {"roomTypeCategory"})
    Optional<GroupRoomType> findById(Integer id);

    /**
     * 获取指定集团下的所有房型模板。
     * 
     * @param groupId 集团 ID
     * @return 集团房型列表
     */
    @EntityGraph(attributePaths = {"roomTypeCategory"})
    List<GroupRoomType> findByGroupId(Integer groupId);
    
    /**
     * 获取指定集团下特定状态的房型模板。
     * 
     * @param groupId 集团 ID
     * @param status 状态 (active/inactive)
     * @return 集团房型列表
     */
    @EntityGraph(attributePaths = {"roomTypeCategory"})
    List<GroupRoomType> findByGroupIdAndStatus(Integer groupId, String status);
    
    /**
     * 根据全局唯一的房型代码查找集团房型。
     * 
     * @param roomTypeCode 房型代码
     * @return 集团房型实体的 Optional 对象
     */
    Optional<GroupRoomType> findByRoomTypeCode(String roomTypeCode);
    
    /**
     * 在指定集团内，根据房型代码查找房型模板。
     * 
     * @param groupId 集团 ID
     * @param roomTypeCode 房型代码
     * @return 集团房型实体的 Optional 对象
     */
    @EntityGraph(attributePaths = {"roomTypeCategory"})
    Optional<GroupRoomType> findByGroupIdAndRoomTypeCode(Integer groupId, String roomTypeCode);
    
    /**
     * 根据房型模板名称模糊搜索。
     * 
     * @param roomTypeName 名称关键字
     * @return 匹配的房型列表
     */
    List<GroupRoomType> findByRoomTypeNameContaining(String roomTypeName);
    
    /**
     * 获取全系统中特定状态的集团房型。
     * 
     * @param status 状态
     * @return 集团房型列表
     */
    List<GroupRoomType> findByStatus(String status);
    
    /**
     * 校验全局房型代码是否存在。
     * 
     * @param roomTypeCode 房型代码
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByRoomTypeCode(String roomTypeCode);

    /**
     * 校验指定集团内房型代码是否存在。
     * 
     * @param groupId 集团 ID
     * @param roomTypeCode 房型代码
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByGroupIdAndRoomTypeCode(Integer groupId, String roomTypeCode);
    
    /**
     * 根据分类 ID 查找关联的集团房型。
     * 
     * @param roomTypeCategoryId 分类 ID
     * @return 集团房型列表
     */
    List<GroupRoomType> findByRoomTypeCategoryId(Integer roomTypeCategoryId);
    
    /**
     * 获取指定集团及特定分类下的房型模板。
     * 
     * @param groupId 集团 ID
     * @param roomTypeCategoryId 分类 ID
     * @return 集团房型列表
     */
    List<GroupRoomType> findByGroupIdAndRoomTypeCategoryId(Integer groupId, Integer roomTypeCategoryId);
    
    /**
     * 获取指定集团下的房型模板，并按排序号升序排列。
     * 
     * @param groupId 集团 ID
     * @return 排序后的房型列表
     */
    List<GroupRoomType> findByGroupIdOrderBySortOrderAsc(Integer groupId);
    
    /**
     * 统计指定集团下的房型模板总数。
     * 
     * @param groupId 集团 ID
     * @return 数量
     */
    long countByGroupId(Integer groupId);

    /**
     * 统计引用了特定分类 ID 的房型模板数量。
     * 用于在删除分类前的依赖检查。
     * 
     * @param roomTypeCategoryId 分类 ID
     * @return 数量
     */
    long countByRoomTypeCategoryId(Integer roomTypeCategoryId);

    /**
     * 根据集团外部编码获取房型列表。
     * 
     * @param groupCode 集团编码
     * @return 房型列表
     */
    List<GroupRoomType> findByGroupCode(String groupCode);

    /**
     * 根据集团外部编码获取特定状态的房型列表。
     * 
     * @param groupCode 集团编码
     * @param status 状态
     * @return 房型列表
     */
    List<GroupRoomType> findByGroupCodeAndStatus(String groupCode, String status);

    /**
     * 根据集团外部编码和房型代码查找模板。
     * 
     * @param groupCode 集团编码
     * @param roomTypeCode 房型代码
     * @return 包含模板的 Optional 对象
     */
    Optional<GroupRoomType> findByGroupCodeAndRoomTypeCode(String groupCode, String roomTypeCode);

    /**
     * 根据集团外部编码和分类编码查找房型。
     * 
     * @param groupCode 集团编码
     * @param roomTypeCategoryCode 分类编码
     * @return 房型列表
     */
    List<GroupRoomType> findByGroupCodeAndRoomTypeCategoryCode(String groupCode, String roomTypeCategoryCode);

    /**
     * 根据集团外部编码获取有序的房型列表。
     * 
     * @param groupCode 集团编码
     * @return 房型列表
     */
    List<GroupRoomType> findByGroupCodeOrderBySortOrderAsc(String groupCode);

    /**
     * 统计指定集团编码下的房型数量。
     * 
     * @param groupCode 集团编码
     * @return 数量
     */
    long countByGroupCode(String groupCode);

    /**
     * 根据分类编码获取关联的房型。
     * 
     * @param roomTypeCategoryCode 分类编码
     * @return 房型列表
     */
    List<GroupRoomType> findByRoomTypeCategoryCode(String roomTypeCategoryCode);

    /**
     * 统计引用了特定分类编码的房型数量。
     * 
     * @param roomTypeCategoryCode 分类编码
     * @return 数量
     */
    long countByRoomTypeCategoryCode(String roomTypeCategoryCode);
}

