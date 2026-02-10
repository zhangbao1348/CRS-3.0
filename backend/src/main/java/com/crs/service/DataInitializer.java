package com.crs.service;

import com.crs.entity.Hotel;
import com.crs.entity.Hotel.Status;
import com.crs.entity.User;
import com.crs.entity.GroupFacility;
import com.crs.entity.GroupRoomType;
import com.crs.entity.MarketCode;
import com.crs.repository.HotelRepository;
import com.crs.repository.UserRepository;
import com.crs.repository.GroupFacilityRepository;
import com.crs.repository.GroupRoomTypeRepository;
import com.crs.repository.MarketCodeRepository;
import com.crs.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据初始化服务
 * 在应用启动时初始化默认数据
 */
@Service
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final HotelRepository hotelRepository;
    private final GroupFacilityRepository groupFacilityRepository;
    private final GroupRoomTypeRepository groupRoomTypeRepository;
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    private MarketCodeRepository marketCodeRepository;
    
    public DataInitializer(UserRepository userRepository, PasswordUtil passwordUtil, HotelRepository hotelRepository, GroupFacilityRepository groupFacilityRepository, GroupRoomTypeRepository groupRoomTypeRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
        this.hotelRepository = hotelRepository;
        this.groupFacilityRepository = groupFacilityRepository;
        this.groupRoomTypeRepository = groupRoomTypeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("开始数据初始化...");
        // 初始化数据库表结构
        initializeDatabaseSchema();
        // 初始化集团设施数据
        initializeGroupFacilityData();
        // 初始化酒店数据
        initializeHotelData();
        // 初始化市场码数据
        initializeMarketCodes();
        log.info("数据初始化完成");
    }
    
    /**
     * 初始化数据库表结构
     */
    private void initializeDatabaseSchema() {
        log.info("开始初始化数据库表结构...");
        
        try {
            // 创建集团房型和酒店关联表
            String createGroupRoomTypeHotelTable = "CREATE TABLE IF NOT EXISTS group_room_type_hotel (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "group_room_type_id INT NOT NULL," +
                "hotel_id INT NOT NULL," +
                "allocated BOOLEAN DEFAULT FALSE," +
                "room_info_editable BOOLEAN DEFAULT FALSE," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "UNIQUE KEY uk_group_room_type_hotel (group_room_type_id, hotel_id)," +
                "FOREIGN KEY (group_room_type_id) REFERENCES group_room_types(id) ON DELETE CASCADE," +
                "FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团房型和酒店关联表';";
            
            // 创建酒店房型表
            String createHotelRoomTypesTable = "CREATE TABLE IF NOT EXISTS hotel_room_types (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "hotel_id INT NOT NULL," +
                "group_room_type_id INT," +
                "room_type_code VARCHAR(50) NOT NULL," +
                "room_type_name VARCHAR(100) NOT NULL," +
                "description TEXT," +
                "status ENUM('active', 'inactive') DEFAULT 'active'," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "UNIQUE KEY uk_hotel_room_code (hotel_id, room_type_code)," +
                "FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE," +
                "FOREIGN KEY (group_room_type_id) REFERENCES group_room_types(id) ON DELETE SET NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店房型表';";
            
            // 执行SQL语句
            jdbcTemplate.execute(createGroupRoomTypeHotelTable);
            jdbcTemplate.execute(createHotelRoomTypesTable);
            
            log.info("数据库表结构初始化完成");
        } catch (Exception e) {
            log.warn("数据库表结构初始化失败: {}", e.getMessage());
            // 继续执行其他初始化操作，不中断应用程序启动
        }
    }
    
    /**
     * 初始化集团设施数据
     */
    private void initializeGroupFacilityData() {
        log.info("开始初始化集团设施数据...");
        
        // 检查是否已有集团设施数据
        List<GroupFacility> existingFacilities = groupFacilityRepository.findAll();
        if (existingFacilities.isEmpty()) {
            log.info("数据库中不存在集团设施数据，开始初始化...");
            
            // 创建一些默认的集团设施
            GroupFacility wifi = new GroupFacility();
            wifi.setCode("WIFI");
            wifi.setName("免费WiFi");
            wifi.setDescription("酒店公共区域和客房内提供免费WiFi");
            wifi.setStatus("active");
            groupFacilityRepository.save(wifi);
            
            GroupFacility parking = new GroupFacility();
            parking.setCode("PARKING");
            parking.setName("免费停车");
            parking.setDescription("酒店提供免费停车位");
            parking.setStatus("active");
            groupFacilityRepository.save(parking);
            
            GroupFacility gym = new GroupFacility();
            gym.setCode("GYM");
            gym.setName("健身中心");
            gym.setDescription("酒店设有健身中心");
            gym.setStatus("active");
            groupFacilityRepository.save(gym);
            
            GroupFacility restaurant = new GroupFacility();
            restaurant.setCode("RESTAURANT");
            restaurant.setName("餐厅");
            restaurant.setDescription("酒店设有餐厅");
            restaurant.setStatus("active");
            groupFacilityRepository.save(restaurant);
            
            log.info("集团设施数据初始化完成");
        } else {
            log.info("数据库中已存在集团设施数据，跳过初始化");
        }
    }
    
    /**
     * 初始化酒店数据
     */
    private void initializeHotelData() {
        log.info("开始初始化酒店数据...");
        
        // 检查是否已有酒店数据
        List<Hotel> existingHotels = hotelRepository.findAll();
        if (existingHotels.isEmpty()) {
            log.info("数据库中不存在酒店数据，开始初始化...");
            
            // 创建一些默认的酒店
            Hotel hotel1 = new Hotel();
            hotel1.setHotelCode("HOTEL001");
            hotel1.setGroupId(1);
            hotel1.setChineseName("上海浦东国际机场亚朵酒店");
            hotel1.setEnglishName("Shanghai Pudong International Airport Hotel");
            hotel1.setStarRating("5");
            hotel1.setProvince("上海市");
            hotel1.setCity("上海市");
            hotel1.setAddress("上海市浦东新区祝桥镇华洲路1号");
            hotel1.setPhone("021-58888888");
            hotel1.setEmail("info@pudongairport.com");
            hotel1.setIntroduction("上海浦东国际机场亚朵酒店位于浦东国际机场附近，交通便利，环境舒适。");
            hotel1.setStatus(Status.active);
            hotelRepository.save(hotel1);
            
            Hotel hotel2 = new Hotel();
            hotel2.setHotelCode("HOTEL002");
            hotel2.setGroupId(1);
            hotel2.setChineseName("北京王府井希尔顿酒店");
            hotel2.setEnglishName("Beijing Wangfujing Hilton Hotel");
            hotel2.setStarRating("5");
            hotel2.setProvince("北京市");
            hotel2.setCity("北京市");
            hotel2.setAddress("北京市东城区王府井东街8号");
            hotel2.setPhone("010-58888888");
            hotel2.setEmail("info@wangfujing.com");
            hotel2.setIntroduction("北京王府井希尔顿酒店位于北京市中心，地理位置优越，是商务和休闲旅客的理想选择。");
            hotel2.setStatus(Status.active);
            hotelRepository.save(hotel2);
            
            log.info("酒店数据初始化完成");
        } else {
            log.info("数据库中已存在酒店数据，跳过初始化");
        }
    }
    
    /**
     * 初始化市场码数据
     */
    private void initializeMarketCodes() {
        log.info("开始初始化市场码数据...");
        
        try {
            // 检查是否已有市场码数据
            List<MarketCode> existingMarketCodes = marketCodeRepository.findAll();
            if (existingMarketCodes.isEmpty()) {
                log.info("数据库中不存在市场码数据，开始初始化...");
                
                // 创建线上市场
                MarketCode onlineMarket = new MarketCode();
                onlineMarket.setCode("ONLINE");
                onlineMarket.setName("线上市场");
                onlineMarket.setDescription("线上销售渠道");
                onlineMarket.setParentId(null);
                onlineMarket.setLevel(1);
                onlineMarket.setStatus(MarketCode.Status.active);
                marketCodeRepository.save(onlineMarket);
                
                log.info("市场码数据初始化完成");
            } else {
                log.info("数据库中已存在市场码数据，跳过初始化");
            }
        } catch (Exception e) {
            log.warn("初始化市场码数据失败: {}", e.getMessage());
        }
    }
    
    /**
     * 创建市场码
     */
    private MarketCode createMarketCode(String code, String name, String description, Integer parentId, int level) {
        MarketCode marketCode = new MarketCode();
        marketCode.setCode(code);
        marketCode.setName(name);
        marketCode.setDescription(description);
        marketCode.setParentId(parentId);
        marketCode.setLevel(level);
        marketCode.setStatus(MarketCode.Status.active);
        return marketCodeRepository.save(marketCode);
    }
}