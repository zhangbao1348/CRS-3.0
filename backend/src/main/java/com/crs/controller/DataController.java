package com.crs.controller;

import com.crs.entity.Group;
import com.crs.entity.Hotel;
import com.crs.entity.Hotel.Status;
import com.crs.repository.GroupRepository;
import com.crs.repository.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据控制器
 * 用于处理数据初始化和模拟数据生成请求
 */
@Slf4j
@RestController
@RequestMapping("/api/data")
public class DataController {
    
    private final HotelRepository hotelRepository;
    private final GroupRepository groupRepository;
    private final JdbcTemplate jdbcTemplate;
    
    public DataController(HotelRepository hotelRepository, GroupRepository groupRepository, DataSource dataSource) {
        this.hotelRepository = hotelRepository;
        this.groupRepository = groupRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    /**
     * 生成酒店模拟数据
     * @return 响应结果
     */
    @PostMapping("/generate-hotels")
    public ResponseEntity<?> generateHotelData() {
        try {
            // 先创建默认的group记录
            createDefaultGroup();
            
            // 检查是否已有酒店数据
            if (hotelRepository.count() > 0) {
                return ResponseEntity.ok(Map.of("message", "数据库中已存在酒店数据，跳过生成"));
            }
            
            // 生成模拟酒店数据
            List<Hotel> hotels = generateMockHotels();
            
            // 保存到数据库
            hotelRepository.saveAll(hotels);
            
            log.info("酒店模拟数据生成完成，共生成 {} 条数据", hotels.size());
            return ResponseEntity.ok(Map.of("message", "酒店模拟数据生成成功", "count", hotels.size()));
        } catch (Exception e) {
            log.error("生成酒店模拟数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 创建默认的group记录
     */
    private void createDefaultGroup() {
        // 检查是否已有group记录
        if (groupRepository.count() == 0) {
            Group group = new Group();
            group.setGroupCode("GROUP001");
            group.setGroupName("默认集团");
            group.setDescription("系统默认集团");
            group.setStatus(Group.Status.active);
            groupRepository.save(group);
            log.info("默认集团记录创建成功");
        }
    }
    
    /**
     * 生成模拟酒店数据
     * @return 酒店列表
     */
    private List<Hotel> generateMockHotels() {
        List<Hotel> hotels = new ArrayList<>();
        
        // 获取第一个group的ID
        Integer groupId = groupRepository.findAll().get(0).getId();
        
        // 生成5家不同的酒店
        hotels.add(createHotel(
            "HOTEL001",
            groupId,
            "上海浦东国际机场酒店",
            "Shanghai Pudong International Airport Hotel",
            "5",
            "上海市",
            "上海市",
            "上海市浦东新区机场大道888号",
            121.803473,
            31.197226,
            "021-58888888",
            "info@pudongairport.com",
            "上海浦东国际机场酒店位于浦东国际机场附近，交通便利，环境舒适，是商务出行和旅游的理想选择。酒店拥有各类客房，配备完善的设施和服务，为客人提供优质的住宿体验。"
        ));
        
        hotels.add(createHotel(
            "HOTEL002",
            groupId,
            "北京王府井大酒店",
            "Beijing Wangfujing Grand Hotel",
            "4",
            "北京市",
            "北京市",
            "北京市东城区王府井大街99号",
            116.407413,
            39.915068,
            "010-65555555",
            "info@wangfujing.com",
            "北京王府井大酒店位于北京市中心王府井商业区，周边购物、餐饮、娱乐设施齐全。酒店装修豪华，服务周到，为客人提供舒适的住宿环境和便捷的出行体验。"
        ));
        
        hotels.add(createHotel(
            "HOTEL003",
            groupId,
            "广州珠江新城酒店",
            "Guangzhou Zhujiang New Town Hotel",
            "5",
            "广东省",
            "广州市",
            "广州市天河区珠江新城冼村路28号",
            113.324873,
            23.120048,
            "020-87777777",
            "info@zhujiangnewtown.com",
            "广州珠江新城酒店位于广州市中心珠江新城商业区，毗邻珠江，视野开阔。酒店设施豪华，服务专业，是商务会议和休闲度假的绝佳选择。"
        ));
        
        hotels.add(createHotel(
            "HOTEL004",
            groupId,
            "杭州西湖度假酒店",
            "Hangzhou West Lake Resort Hotel",
            "4",
            "浙江省",
            "杭州市",
            "杭州市西湖区龙井路168号",
            120.149022,
            30.242958,
            "0571-86666666",
            "info@westlakeresort.com",
            "杭州西湖度假酒店位于风景秀丽的西湖边，环境优雅，空气清新。酒店设计融合了江南水乡特色，为客人提供宁静舒适的度假环境，是休闲旅游的理想选择。"
        ));
        
        hotels.add(createHotel(
            "HOTEL005",
            groupId,
            "深圳南山科技园酒店",
            "Shenzhen Nanshan Science Park Hotel",
            "4",
            "广东省",
            "深圳市",
            "深圳市南山区科技园高新南一道8号",
            113.941438,
            22.535347,
            "0755-26666666",
            "info@sciencepark.com",
            "深圳南山科技园酒店位于深圳市南山区科技园核心区域，周边高科技企业云集。酒店现代化设施齐全，服务高效，是商务人士的首选住宿场所。"
        ));
        
        return hotels;
    }
    
    /**
     * 创建酒店对象
     * @param hotelCode 酒店代码
     * @param groupId 集团ID
     * @param chineseName 中文名称
     * @param englishName 英文名称
     * @param starRating 星级
     * @param province 省份
     * @param city 城市
     * @param address 地址
     * @param longitude 经度
     * @param latitude 纬度
     * @param phone 电话
     * @param email 邮箱
     * @param introduction 简介
     * @return 酒店对象
     */
    private Hotel createHotel(String hotelCode, Integer groupId, String chineseName, String englishName,
                             String starRating, String province, String city, String address,
                             Double longitude, Double latitude, String phone, String email, String introduction) {
        Hotel hotel = new Hotel();
        hotel.setHotelCode(hotelCode);
        hotel.setGroupId(groupId);
        hotel.setChineseName(chineseName);
        hotel.setEnglishName(englishName);
        hotel.setStarRating(starRating);
        hotel.setProvince(province);
        hotel.setCity(city);
        hotel.setAddress(address);
        hotel.setLongitude(longitude);
        hotel.setLatitude(latitude);
        hotel.setPhone(phone);
        hotel.setEmail(email);
        hotel.setIntroduction(introduction);
        hotel.setStatus(Status.active);
        return hotel;
    }
    
    /**
     * 添加total_rooms字段到hotels表
     * @return 操作结果
     */
    @GetMapping("/add-total-rooms-column")
    public ResponseEntity<?> addTotalRoomsColumn() {
        try {
            // 执行SQL语句添加字段
            String sql = "ALTER TABLE hotels ADD COLUMN total_rooms INT";
            jdbcTemplate.execute(sql);
            
            log.info("成功添加total_rooms字段到hotels表");
            return ResponseEntity.ok(Map.of("message", "成功添加total_rooms字段到hotels表"));
        } catch (Exception e) {
            log.error("添加total_rooms字段失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}