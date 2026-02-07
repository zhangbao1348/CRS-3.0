# CRS系统Java后端框架设计方案

## 一、技术选型

### 核心框架
- **语言**: Java 11+
- **Web框架**: Spring Boot 3.0+
- **数据库**: MySQL 8.0+
- **ORM**: Spring Data JPA
- **认证**: Spring Security + JWT
- **API风格**: RESTful API
- **构建工具**: Maven 3.8+

### 依赖库
- `spring-boot-starter-web`: Web支持
- `spring-boot-starter-data-jpa`: JPA支持
- `spring-boot-starter-security`: 安全框架
- `spring-boot-starter-validation`: 数据验证
- `spring-boot-starter-test`: 测试支持
- `mysql-connector-java`: MySQL驱动
- `jjwt`: JWT实现
- `lombok`: 代码简化
- `springdoc-openapi-starter-webmvc-ui`: API文档
- `spring-boot-devtools`: 开发工具

## 二、架构设计

### 目录结构
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── crs/
│   │   │           ├── CrsApplication.java       # 应用主类
│   │   │           ├── config/                    # 配置类
│   │   │           │   ├── SecurityConfig.java    # 安全配置
│   │   │           │   ├── JwtConfig.java         # JWT配置
│   │   │           │   ├── DatabaseConfig.java    # 数据库配置
│   │   │           │   └── CorsConfig.java        # 跨域配置
│   │   │           ├── controller/                # 控制器层
│   │   │           │   ├── AuthController.java    # 认证控制器
│   │   │           │   ├── GroupController.java   # 集团控制器
│   │   │           │   ├── HotelController.java   # 酒店控制器
│   │   │           │   ├── RoomTypeController.java # 房型控制器
│   │   │           │   ├── RateController.java    # 价格控制器
│   │   │           │   ├── InventoryController.java # 库存控制器
│   │   │           │   ├── ReservationController.java # 预订控制器
│   │   │           │   └── ChannelController.java # 渠道控制器
│   │   │           ├── service/                   # 服务层
│   │   │           │   ├── AuthService.java       # 认证服务
│   │   │           │   ├── GroupService.java      # 集团服务
│   │   │           │   ├── HotelService.java      # 酒店服务
│   │   │           │   ├── RoomTypeService.java   # 房型服务
│   │   │           │   ├── RateService.java       # 价格服务
│   │   │           │   ├── InventoryService.java  # 库存服务
│   │   │           │   ├── ReservationService.java # 预订服务
│   │   │           │   └── ChannelService.java    # 渠道服务
│   │   │           ├── repository/                # 数据访问层
│   │   │           │   ├── GroupRepository.java   # 集团仓库
│   │   │           │   ├── HotelRepository.java   # 酒店仓库
│   │   │           │   ├── RoomTypeRepository.java # 房型仓库
│   │   │           │   ├── RateRepository.java    # 价格仓库
│   │   │           │   ├── InventoryRepository.java # 库存仓库
│   │   │           │   ├── ReservationRepository.java # 预订仓库
│   │   │           │   └── ChannelRepository.java # 渠道仓库
│   │   │           ├── entity/                    # 实体类
│   │   │           │   ├── Group.java             # 集团实体
│   │   │           │   ├── Hotel.java             # 酒店实体
│   │   │           │   ├── RoomType.java          # 房型实体
│   │   │           │   ├── Rate.java              # 价格实体
│   │   │           │   ├── Inventory.java         # 库存实体
│   │   │           │   ├── Reservation.java       # 预订实体
│   │   │           │   └── Channel.java           # 渠道实体
│   │   │           ├── dto/                       # 数据传输对象
│   │   │           │   ├── AuthDto.java           # 认证DTO
│   │   │           │   ├── GroupDto.java          # 集团DTO
│   │   │           │   ├── HotelDto.java          # 酒店DTO
│   │   │           │   ├── RoomTypeDto.java       # 房型DTO
│   │   │           │   ├── RateDto.java           # 价格DTO
│   │   │           │   ├── InventoryDto.java      # 库存DTO
│   │   │           │   ├── ReservationDto.java    # 预订DTO
│   │   │           │   └── ChannelDto.java        # 渠道DTO
│   │   │           ├── exception/                 # 异常处理
│   │   │           │   ├── GlobalExceptionHandler.java # 全局异常处理器
│   │   │           │   └── CustomException.java   # 自定义异常
│   │   │           ├── filter/                    # 过滤器
│   │   │           │   └── JwtFilter.java         # JWT过滤器
│   │   │           └── util/                      # 工具类
│   │   │               ├── JwtUtil.java           # JWT工具
│   │   │               ├── PasswordUtil.java      # 密码工具
│   │   │               └── ValidationUtil.java    # 验证工具
│   │   └── resources/
│   │       ├── application.properties             # 应用配置
│   │       ├── application-dev.properties         # 开发环境配置
│   │       ├── application-prod.properties        # 生产环境配置
│   │       └── static/                            # 静态资源
│   └── test/
│       └── java/
│           └── com/
│               └── crs/
│                   └── test/                      # 测试类
├── pom.xml                                        # Maven配置
└── .gitignore                                     # Git忽略文件
```

## 三、核心功能模块设计

### 1. 认证模块
- 用户登录/注销
- JWT token生成和验证
- 基于角色的权限控制
- 密码加密和验证

### 2. 集团管理模块
- 集团信息CRUD
- 酒店信息CRUD
- 集团与酒店的关联管理

### 3. 基础数据管理模块
- 房型管理（集团级和酒店级）
- 房价码管理
- 市场码、渠道码、来源码管理
- 税率设置管理
- 包价管理

### 4. 价格管理模块
- 基础价格管理
- 房型差价体系管理
- 人数差价体系管理
- 价格计划管理
- 价格计算逻辑

### 5. 库存管理模块
- 房间库存管理
- 库存日历管理
- 库存分配管理

### 6. 预订管理模块
- 预订列表查询
- 预订状态管理
- 预订详情管理

### 7. 渠道管理模块
- 渠道列表管理
- 渠道映射管理

## 四、API接口设计

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户注销
- `GET /api/auth/me` - 获取当前用户信息

### 集团管理接口
- `GET /api/groups` - 获取集团列表
- `POST /api/groups` - 创建集团
- `GET /api/groups/{id}` - 获取集团详情
- `PUT /api/groups/{id}` - 更新集团信息
- `DELETE /api/groups/{id}` - 删除集团
- `GET /api/groups/{id}/hotels` - 获取集团下的酒店列表

### 酒店管理接口
- `GET /api/hotels` - 获取酒店列表
- `POST /api/hotels` - 创建酒店
- `GET /api/hotels/{id}` - 获取酒店详情
- `PUT /api/hotels/{id}` - 更新酒店信息
- `DELETE /api/hotels/{id}` - 删除酒店

### 价格管理接口
- `GET /api/rates/base-prices` - 获取基础价格列表
- `POST /api/rates/base-prices` - 创建基础价格
- `PUT /api/rates/base-prices/{id}` - 更新基础价格
- `GET /api/rates/room-type-diffs` - 获取房型差价列表
- `POST /api/rates/room-type-diffs` - 创建房型差价
- `PUT /api/rates/room-type-diffs/{id}` - 更新房型差价
- `GET /api/rates/person-diffs` - 获取人数差价列表
- `POST /api/rates/person-diffs` - 创建人数差价
- `PUT /api/rates/person-diffs/{id}` - 更新人数差价
- `GET /api/rates/rate-plans` - 获取价格计划列表
- `POST /api/rates/rate-plans` - 创建价格计划
- `PUT /api/rates/rate-plans/{id}` - 更新价格计划

### 库存管理接口
- `GET /api/inventory` - 获取库存列表
- `POST /api/inventory` - 创建库存记录
- `PUT /api/inventory/{id}` - 更新库存记录
- `GET /api/inventory/calendar` - 获取库存日历

### 预订管理接口
- `GET /api/reservations` - 获取预订列表
- `GET /api/reservations/{id}` - 获取预订详情
- `PUT /api/reservations/{id}` - 更新预订状态

### 渠道管理接口
- `GET /api/channels` - 获取渠道列表
- `POST /api/channels` - 创建渠道
- `PUT /api/channels/{id}` - 更新渠道
- `GET /api/channel-mappings` - 获取渠道映射列表
- `POST /api/channel-mappings` - 创建渠道映射
- `PUT /api/channel-mappings/{id}` - 更新渠道映射

## 五、数据库设计

### 数据库连接
- 使用Spring Data JPA管理数据库连接
- 配置数据库连接池（HikariCP）
- 支持事务管理

### 数据模型
- 基于现有的数据库表结构创建JPA实体
- 实现实体间的关联关系
- 添加必要的索引以提高查询性能
- 使用Lombok简化实体类代码

## 六、部署与集成方案

### 开发环境
- 使用Spring Boot内置Tomcat服务器
- 配置热重载
- 启用详细错误信息

### 生产环境
- 打包为WAR文件部署到Tomcat服务器
- 或使用Docker容器化部署
- 配置Nginx作为反向代理
- 启用HTTPS
- 配置日志系统

### 集成方案
- 与前端React应用集成
- 提供API文档（使用SpringDoc OpenAPI）
- 支持CI/CD流程

## 七、性能优化策略

1. **数据库优化**
   - 添加适当的索引
   - 使用HikariCP连接池
   - 优化SQL查询
   - 实现分页查询

2. **缓存策略**
   - 使用Spring Cache缓存热点数据
   - 集成Redis缓存价格计算结果
   - 缓存库存状态

3. **API优化**
   - 实现RESTful API最佳实践
   - 支持数据过滤和排序
   - 优化响应数据结构

4. **代码优化**
   - 使用Stream API和Lambda表达式
   - 实现异步处理耗时操作
   - 优化价格计算逻辑
   - 减少数据库查询次数

## 八、安全性考虑

1. **认证与授权**
   - 实现JWT认证
   - 基于角色的权限控制
   - 密码加密存储（使用BCrypt）

2. **输入验证**
   - 使用Spring Validation对所有API输入进行验证
   - 防止SQL注入攻击
   - 防止XSS攻击
   - 防止CSRF攻击

3. **数据保护**
   - 敏感数据加密
   - 数据库备份策略
   - 防止数据泄露

4. **API安全**
   - 实现API速率限制
   - 监控异常访问
   - 配置安全头

## 九、开发计划

1. **搭建基础框架**
   - 创建Spring Boot项目
   - 配置依赖项
   - 实现数据库连接
   - 配置安全框架

2. **实现核心功能**
   - 认证模块
   - 集团管理模块
   - 基础数据管理模块

3. **实现业务功能**
   - 价格管理模块
   - 库存管理模块
   - 预订管理模块
   - 渠道管理模块

4. **集成与测试**
   - 与前端集成
   - 编写单元测试和集成测试
   - 性能测试

5. **部署与上线**
   - 配置生产环境
   - 部署应用
   - 监控与维护

## 十、技术优势

1. **成熟稳定**
   - Java语言成熟可靠
   - Spring Boot生态系统完善
   - 适合企业级应用开发

2. **高性能**
   - JVM优化
   - 多线程支持
   - 适合处理并发请求

3. **安全可靠**
   - Spring Security提供全面的安全解决方案
   - JWT认证机制安全高效
   - 完善的异常处理

4. **易于维护**
   - 清晰的代码结构
   - 依赖注入简化代码
   - 完善的文档支持

5. **扩展性强**
   - Spring Boot模块化设计
   - 支持微服务架构（可选）
   - 丰富的第三方库支持

此Java后端框架设计方案完全基于现有的前端代码和数据库结构，能够很好地支持酒店中央预订系统(CRS)的所有功能模块。