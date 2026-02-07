package com.crs.service;

import com.crs.entity.Hotel;
import com.crs.entity.Hotel.Status;
import com.crs.entity.User;
import com.crs.entity.GroupFacility;
import com.crs.repository.HotelRepository;
import com.crs.repository.UserRepository;
import com.crs.repository.GroupFacilityRepository;
import com.crs.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据初始化器
 * 用于创建默认用户和测试数据
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final HotelRepository hotelRepository;
    private final GroupFacilityRepository groupFacilityRepository;
    
    public DataInitializer(UserRepository userRepository, PasswordUtil passwordUtil, HotelRepository hotelRepository, GroupFacilityRepository groupFacilityRepository) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
        this.hotelRepository = hotelRepository;
        this.groupFacilityRepository = groupFacilityRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("开始数据初始化...");
        // 初始化集团设施数据
        initializeGroupFacilityData();
        // 初始化酒店数据
        initializeHotelData();
        log.info("数据初始化完成");
    }
    
    /**
     * 初始化集团设施数据
     */
    private void initializeGroupFacilityData() {
        log.info("开始初始化集团设施数据...");
        
        try {
            // 检查是否已有设施数据
            if (groupFacilityRepository.count() > 0) {
                log.info("数据库中已存在集团设施数据，跳过初始化");
                return;
            }
            
            // 生成模拟设施数据
            List<GroupFacility> facilities = generateMockGroupFacilities();
            
            // 保存到数据库
            groupFacilityRepository.saveAll(facilities);
            
            log.info("集团设施数据初始化完成，共生成 {} 条数据", facilities.size());
        } catch (Exception e) {
            log.warn("集团设施数据初始化失败，可能是表结构尚未创建: {}", e.getMessage());
            // 继续执行其他初始化操作，不中断应用程序启动
        }
    }
    
    /**
     * 生成模拟集团设施数据
     * @return 设施列表
     */
    private List<GroupFacility> generateMockGroupFacilities() {
        List<GroupFacility> facilities = new ArrayList<>();
        
        // 交通服务设施
        facilities.add(createGroupFacility("transportation", "收费停车场", "PAID_PARKING", true, "提供收费停车场服务"));
        facilities.add(createGroupFacility("transportation", "免费停车场", "FREE_PARKING", true, "提供免费停车场服务"));
        facilities.add(createGroupFacility("transportation", "免费接送机", "FREE_SHUTTLE", true, "提供免费接送机服务"));
        facilities.add(createGroupFacility("transportation", "收费接送机", "PAID_SHUTTLE", true, "提供收费接送机服务"));
        facilities.add(createGroupFacility("transportation", "租车服务", "CAR_RENTAL", true, "提供租车服务"));
        
        // 餐饮服务设施
        facilities.add(createGroupFacility("dining", "自助早餐厅", "BUFFET_RESTAURANT", true, "提供自助早餐服务"));
        facilities.add(createGroupFacility("dining", "咖啡厅", "CAFE", true, "提供咖啡和简餐服务"));
        facilities.add(createGroupFacility("dining", "中餐厅", "CHINESE_RESTAURANT", true, "提供中餐服务"));
        facilities.add(createGroupFacility("dining", "西餐厅", "WESTERN_RESTAURANT", true, "提供西餐服务"));
        facilities.add(createGroupFacility("dining", "酒吧", "BAR", true, "提供酒吧服务"));
        facilities.add(createGroupFacility("dining", "24小时便利店", "CONVENIENCE_STORE", true, "提供24小时便利店服务"));
        
        // 清洁服务设施
        facilities.add(createGroupFacility("cleaning", "外送洗衣服务", "LAUNDRY_SERVICE", true, "提供外送洗衣服务"));
        facilities.add(createGroupFacility("cleaning", "干衣机", "DRYER", true, "提供干衣机服务"));
        facilities.add(createGroupFacility("cleaning", "熨斗/挂烫机", "IRON", true, "提供熨斗和挂烫机服务"));
        facilities.add(createGroupFacility("cleaning", "洗衣房", "LAUNDRY_ROOM", true, "提供洗衣房服务"));
        facilities.add(createGroupFacility("cleaning", "熨衣服务", "VALET_SERVICE", true, "提供熨衣服务"));
        facilities.add(createGroupFacility("cleaning", "洗衣服务", "WASHING_SERVICE", true, "提供洗衣服务"));
        
        // 其他服务设施
        facilities.add(createGroupFacility("other", "健身房", "GYM", true, "提供健身房服务"));
        facilities.add(createGroupFacility("other", "游泳池", "SWIMMING_POOL", true, "提供游泳池服务"));
        facilities.add(createGroupFacility("other", "SPA", "SPA", true, "提供SPA服务"));
        facilities.add(createGroupFacility("other", "会议室", "MEETING_ROOM", true, "提供会议室服务"));
        facilities.add(createGroupFacility("other", "商务中心", "BUSINESS_CENTER", true, "提供商务中心服务"));
        facilities.add(createGroupFacility("other", "行李寄存", "LUGGAGE_STORAGE", true, "提供行李寄存服务"));
        facilities.add(createGroupFacility("other", "叫醒服务", "WAKE_UP_SERVICE", true, "提供叫醒服务"));
        facilities.add(createGroupFacility("other", " concierge服务", "CONCIERGE", true, "提供 concierge服务"));
        
        return facilities;
    }
    
    /**
     * 创建集团设施对象
     * @param facilityType 设施类型
     * @param facilityName 设施名称
     * @param facilityCode 设施代码
     * @param available 是否可用
     * @param description 设施描述
     * @return 集团设施对象
     */
    private GroupFacility createGroupFacility(String facilityType, String facilityName, String facilityCode, Boolean available, String description) {
        GroupFacility facility = new GroupFacility();
        facility.setFacilityType(facilityType);
        facility.setFacilityName(facilityName);
        facility.setFacilityCode(facilityCode);
        facility.setAvailable(available);
        facility.setDescription(description);
        return facility;
    }
    
    /**
     * 初始化酒店模拟数据
     */
    private void initializeHotelData() {
        log.info("开始初始化酒店模拟数据...");
        
        // 检查是否已有酒店数据
        if (hotelRepository.count() > 0) {
            log.info("数据库中已存在酒店数据，跳过初始化");
            return;
        }
        
        // 生成模拟酒店数据
        List<Hotel> hotels = generateMockHotels();
        
        // 保存到数据库
        hotelRepository.saveAll(hotels);
        
        log.info("酒店模拟数据初始化完成，共生成 {} 条数据", hotels.size());
    }
    
    /**
     * 生成模拟酒店数据
     * @return 酒店列表
     */
    private List<Hotel> generateMockHotels() {
        List<Hotel> hotels = new ArrayList<>();
        
        // 生成5家不同的酒店
        hotels.add(createHotel(
            "HOTEL001",
            1,
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
            1,
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
            1,
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
            1,
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
            1,
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
}
