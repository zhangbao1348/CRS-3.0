# CRS项目规则指南

## 1. 项目概述

CRS（Central Reservation System）是一个酒店中央预订系统，用于管理酒店的预订、库存、价格等核心业务。本项目采用前后端分离架构，前端使用React和Ant Design构建，后端使用Spring Boot和JPA实现。

### 1.1 项目目标

- 提供完整的酒店中央预订系统功能
- 支持集团化管理，包括多酒店、多房型、多价格体系
- 提供直观、易用的用户界面
- 确保系统的稳定性、安全性和可扩展性

## 2. 技术栈

### 2.1 前端技术栈

| 技术/库 | 版本 | 用途 |
|--------|------|------|
| React | 18.2.0 | 前端框架 |
| Ant Design | 5.12.0 | UI组件库 |
| React Router DOM | 6.21.0 | 路由管理 |
| Axios | 1.13.3 | HTTP客户端 |
| Dayjs | 1.11.19 | 日期时间处理 |
| Vite | 5.0.8 | 构建工具 |

### 2.2 后端技术栈

| 技术/库 | 版本 | 用途 |
|--------|------|------|
| Spring Boot | 3.2.0 | 后端框架 |
| Spring Data JPA | 3.2.0 | ORM框架 |
| Spring Security | 3.2.0 | 安全框架 |
| MySQL | 8.0.33 | 数据库 |
| JWT | 0.11.5 | 认证令牌 |
| Lombok | 1.18.30 | 代码简化工具 |

## 3. 目录结构

### 3.1 前端目录结构

```
src/
├── assets/            # 静态资源
│   └── images/        # 图片资源
├── components/        # 通用组件
│   ├── Layout/        # 布局组件
│   └── TreeManagement/ # 树状管理组件
├── pages/             # 页面组件
│   ├── ChannelManagement/ # 渠道管理
│   ├── GroupManagement/   # 集团管理
│   ├── HotelManagement/   # 酒店管理
│   ├── Inventory/         # 库存管理
│   └── RateManagement/    # 价格管理
├── App.jsx            # 应用入口组件
└── main.jsx           # 应用启动文件
```

### 3.2 后端目录结构

```
backend/src/main/java/com/crs/
├── config/            # 配置类
├── controller/        # 控制器
├── entity/            # 实体类
├── filter/            # 过滤器
├── repository/        # 数据访问层
├── service/           # 服务层
│   └── impl/          # 服务实现
├── util/              # 工具类
└── CrsApplication.java # 应用入口
```

### 3.3 数据库目录结构

```
database/
├── create_group_facilities.sql  # 创建集团设施表
├── create_hotel_images.sql      # 创建酒店图片表
├── create_market_codes.sql      # 创建市场码表
└── schema.sql                   # 数据库架构
```

## 4. 编码规范

### 4.1 前端编码规范

1. **文件命名**：使用 PascalCase 命名组件文件（如 `HotelManagement.jsx`），使用 camelCase 命名工具文件。

2. **组件命名**：组件名称使用 PascalCase（如 `HotelManagement`），与文件名保持一致。

3. **变量命名**：
   - 常量使用 UPPER_SNAKE_CASE（如 `MAX_ITEMS`）
   - 变量和函数使用 camelCase（如 `hotelName`）
   - 组件属性使用 camelCase（如 `hotelId`）

4. **代码风格**：
   - 使用 2 个空格进行缩进
   - 使用单引号 `'` 而非双引号 `"`
   - 大括号 `{}` 与代码在同一行
   - 箭头函数使用简洁语法（如 `() => {}`）

5. **导入顺序**：
   - 第三方库导入
   - 内部组件导入
   - 样式导入

6. **注释规范**：
   - 组件添加 JSDoc 注释
   - 复杂逻辑添加行内注释
   - 函数添加参数和返回值说明

### 4.2 后端编码规范

1. **文件命名**：使用 PascalCase 命名类文件（如 `HotelController.java`）。

2. **类命名**：
   - 实体类：使用 PascalCase（如 `Hotel`）
   - 控制器：使用 PascalCase 并以 `Controller` 结尾（如 `HotelController`）
   - 服务：使用 PascalCase 并以 `Service` 结尾（如 `HotelService`）
   - 仓库：使用 PascalCase 并以 `Repository` 结尾（如 `HotelRepository`）

3. **变量命名**：
   - 常量使用 UPPER_SNAKE_CASE（如 `MAX_PAGE_SIZE`）
   - 变量和方法使用 camelCase（如 `hotelName`）
   - 私有字段使用 camelCase 并以 `_` 开头（如 `_hotelService`）

4. **代码风格**：
   - 使用 4 个空格进行缩进
   - 大括号 `{}` 与代码在同一行
   - 方法之间空一行
   - 类成员之间空一行

5. **注释规范**：
   - 类添加 Javadoc 注释
   - 方法添加 Javadoc 注释
   - 复杂逻辑添加行内注释

6. **包结构**：
   - 控制器：`com.crs.controller`
   - 实体：`com.crs.entity`
   - 服务：`com.crs.service`
   - 仓库：`com.crs.repository`
   - 配置：`com.crs.config`

## 5. 命名规范

### 5.1 前端命名规范

1. **组件名称**：使用 PascalCase，语义化描述组件功能（如 `HotelManagement`）。

2. **变量名称**：使用 camelCase，语义化描述变量用途（如 `hotelList`）。

3. **常量名称**：使用 UPPER_SNAKE_CASE，语义化描述常量含义（如 `API_BASE_URL`）。

4. **函数名称**：使用 camelCase，动词+宾语结构（如 `fetchHotels`）。

5. **路由名称**：使用 kebab-case（如 `/hotel-management`）。

### 5.2 后端命名规范

1. **类名称**：使用 PascalCase，语义化描述类功能（如 `HotelController`）。

2. **方法名称**：使用 camelCase，动词+宾语结构（如 `getHotelById`）。

3. **变量名称**：使用 camelCase，语义化描述变量用途（如 `hotelId`）。

4. **常量名称**：使用 UPPER_SNAKE_CASE，语义化描述常量含义（如 `MAX_PAGE_SIZE`）。

5. **包名称**：使用小写字母，语义化描述包功能（如 `controller`）。

6. **数据库表名**：使用 snake_case，复数形式（如 `hotels`）。

7. **数据库字段名**：使用 snake_case（如 `hotel_name`）。

## 6. 代码风格

### 6.1 前端代码风格

```jsx
// 组件定义
const HotelManagement = () => {
  // 状态管理
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(false)

  // 数据获取
  const fetchHotels = async () => {
    setLoading(true)
    try {
      const response = await axios.get('/api/hotels')
      setHotels(response.data)
    } catch (error) {
      console.error('加载酒店失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 渲染
  return (
    <div className="hotel-management">
      <h1>酒店管理</h1>
      {/* 组件内容 */}
    </div>
  )
}

export default HotelManagement
```

### 6.2 后端代码风格

```java
/**
 * 酒店控制器
 * 提供酒店的CRUD操作API
 */
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    /**
     * 获取所有酒店
     */
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        try {
            List<Hotel> hotels = hotelService.getAllHotels();
            return ResponseEntity.ok(hotels);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 其他方法...
}
```

## 7. 版本控制

### 7.1 Git 规范

1. **分支管理**：
   - `main`：主分支，用于生产环境
   - `develop`：开发分支，用于集成测试
   - `feature/*`：功能分支，用于开发新功能
   - `bugfix/*`：修复分支，用于修复bug

2. **提交信息规范**：
   - 提交信息格式：`[类型] 描述`
   - 类型包括：`feat`（新功能）、`fix`（修复）、`docs`（文档）、`style`（样式）、`refactor`（重构）、`test`（测试）、`chore`（构建/依赖）
   - 示例：`[feat] 实现酒店管理功能`

3. **提交频率**：
   - 每个功能模块完成后提交
   - 每个bug修复后提交
   - 避免一次性提交大量代码

4. **代码审查**：
   - 功能分支合并到 develop 前进行代码审查
   - 确保代码符合项目规范
   - 确保代码质量和安全性

## 8. 开发流程

### 8.1 前端开发流程

1. **环境搭建**：
   - 克隆代码仓库
   - 安装依赖：`npm install`
   - 启动开发服务器：`npm run dev`

2. **功能开发**：
   - 创建功能分支：`git checkout -b feature/xxx`
   - 开发新功能或修复bug
   - 运行代码检查：`npm run lint`
   - 提交代码：`git commit -m "[feat] 实现xxx功能"`
   - 推送代码：`git push origin feature/xxx`
   - 创建合并请求

3. **测试流程**：
   - 运行单元测试：`npm test`
   - 手动测试功能
   - 确保代码覆盖率

### 8.2 后端开发流程

1. **环境搭建**：
   - 克隆代码仓库
   - 配置数据库连接
   - 构建项目：`mvn clean install`
   - 启动开发服务器：`mvn spring-boot:run`

2. **功能开发**：
   - 创建功能分支：`git checkout -b feature/xxx`
   - 开发新功能或修复bug
   - 运行代码检查：`mvn compile`
   - 提交代码：`git commit -m "[feat] 实现xxx功能"`
   - 推送代码：`git push origin feature/xxx`
   - 创建合并请求

3. **测试流程**：
   - 运行单元测试：`mvn test`
   - 手动测试API
   - 确保代码质量

## 9. 部署流程

### 9.1 前端部署

1. **构建**：
   - 运行构建命令：`npm run build`
   - 生成静态文件到 `dist` 目录

2. **部署**：
   - 将 `dist` 目录部署到 Web 服务器
   - 配置 Nginx 或 Apache 服务器
   - 确保静态资源正确加载

### 9.2 后端部署

1. **构建**：
   - 运行构建命令：`mvn clean package`
   - 生成可执行 JAR 文件到 `target` 目录

2. **部署**：
   - 将 JAR 文件部署到服务器
   - 配置环境变量和数据库连接
   - 启动应用：`java -jar crs-backend-1.0.0.jar`
   - 配置反向代理（如 Nginx）

### 9.3 数据库部署

1. **初始化**：
   - 运行 `database/schema.sql` 创建数据库架构
   - 运行其他 SQL 文件初始化数据

2. **迁移**：
   - 每次 schema 变更时创建迁移脚本
   - 确保迁移脚本的版本控制

## 10. 最佳实践

### 10.1 前端最佳实践

1. **组件设计**：
   - 遵循单一职责原则
   - 使用函数组件和 Hooks
   - 合理使用 Context API 管理状态
   - 组件拆分合理，避免过大的组件

2. **状态管理**：
   - 组件内部状态使用 `useState`
   - 跨组件状态考虑使用 Context API 或状态管理库
   - 避免不必要的状态更新

3. **API 调用**：
   - 集中管理 API 调用
   - 使用 Axios 拦截器处理请求和响应
   - 实现错误处理和重试机制
   - 避免在组件渲染时直接调用 API

4. **性能优化**：
   - 使用 `React.memo` 优化组件渲染
   - 使用 `useCallback` 和 `useMemo` 缓存函数和计算结果
   - 懒加载组件：`React.lazy` 和 `Suspense`
   - 避免不必要的 DOM 操作

### 10.2 后端最佳实践

1. **架构设计**：
   - 遵循 RESTful API 设计规范
   - 采用分层架构：控制器 → 服务 → 仓库
   - 合理使用设计模式
   - 确保代码的可测试性

2. **数据库操作**：
   - 使用 JPA 进行数据库操作
   - 合理使用查询方法和 JPQL
   - 避免 N+1 查询问题
   - 确保事务的正确使用

3. **安全实践**：
   - 使用 Spring Security 进行认证和授权
   - 实现 JWT 令牌验证
   - 避免硬编码敏感信息
   - 确保密码加密存储

4. **性能优化**：
   - 合理使用缓存
   - 优化数据库查询
   - 避免不必要的计算
   - 确保 API 响应时间合理

### 10.3 团队协作最佳实践



2. **文档**：
   - 保持代码注释的更新
   - 编写 API 文档
   - 记录系统架构和设计决策
   - 维护项目配置和部署文档

3. **代码质量**：
   - 定期进行代码审查
   - 使用静态代码分析工具
   - 确保代码测试覆盖率
   - 持续集成和持续部署

## 11. 总结

本项目规则指南旨在确保 CRS 项目的代码质量、一致性和可维护性。所有团队成员应严格遵循本指南的规范和最佳实践，以确保项目的顺利开发和长期维护。

随着项目的发展和技术的演进，本指南将不断更新和完善，以适应新的需求和挑战。