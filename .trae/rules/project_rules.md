# CRS项目规则指南

## 1. 项目概述

CRS（Central Reservation System）是一个酒店中央预订系统，用于管理酒店的预订、库存、价格等核心业务。本项目采用前后端分离架构，前端使用React和Ant Design构建，后端使用Spring Boot和JPA实现。

### 1.1 项目目标

- 提供完整的酒店中央预订系统功能
- 支持集团化管理，包括多酒店、多房型、多价格体系
- 提供直观、易用的用户界面
- 确保系统的稳定性、安全性和可扩展性

### 1.2 项目总体原则（业务关联与查询）

1. **禁止使用 ID 做业务关联/关联查询**：任何跨表、跨模块的业务关联与查询不允许依赖自增 ID。
2. **标准查询上下文**：以 **租户ID + 酒店CODE** 作为酒店维度的主查询上下文。
3. **精确查询规则**：需要定位具体业务对象时，使用 **租户ID + 酒店CODE + 具体CODE**（例如 rateCode、roomTypeCode、channelCode 等）。
4. **例外约定**：仅系统内部关系表/历史表允许使用 ID（如 user_roles、role_menus、reservation_history），但不应作为对外业务查询条件。

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

6. **路径规范**：
   - **绝对禁止在前端页面中使用绝对路径**（如 `http://localhost:11111/api/xxx`）
   - 所有 API 调用必须通过 `src/utils/api.js` 中定义的统一接口
   - 所有资源引用使用相对路径
   - 环境相关的配置统一在配置文件中管理

7. **注释规范**：
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

## 6. 版本控制

### 6.1 Git 规范

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

## 7. 开发流程

### 核心原则：需求驱动开发

**任何代码变更，必须严格遵循以下顺序：**

```
需求变更 → 设计方案 → 研发实现
```

**禁止跳过任何步骤直接编写代码。**

### 文档更新原则

**每次涉及到逻辑和代码的变更，必须更新以下两个文件：**

1. **需求文档** (`/.kiro/specs/requirements.md`)：更新业务规则、功能描述和交互逻辑
2. **技术方案文档** (`/.kiro/specs/design.md`)：更新技术实现、API设计和架构变更

**禁止在未更新文档的情况下直接修改代码。**

---

#### 第一步：需求变更

所有功能新增、修改、删除，必须先更新需求文档：

- 文档位置：`.kiro/specs/requirements.md`
- 变更内容包括：功能描述、业务规则、字段定义、交互逻辑
- 需求文档必须经过确认后，方可进入下一步

**需求文档变更格式：**
- 新增功能：在对应模块章节中添加描述
- 修改功能：更新对应章节，注明变更内容
- 删除功能：标记为"已废弃"并说明原因

---

#### 第二步：设计方案

需求确认后，制定技术设计方案，包括：

- **前端设计**：页面结构、组件设计、交互流程、状态管理
- **后端设计**：API 接口定义、数据库变更（如有）、服务层逻辑
- **影响评估**：评估对现有功能的影响范围

设计方案需在开始编码前完成，并经过确认。

---

#### 第三步：研发实现

设计方案确认后，按照以下规范进行开发：

1. 创建功能分支：`git checkout -b feature/xxx`
2. 按设计方案实现功能
3. 运行代码检查：`npm run lint`（前端）/ `mvn compile`（后端）
4. 更新功能分析文档（`功能分析/` 目录）
5. 提交代码：`git commit -m "[feat] 实现xxx功能"`

---

#### 违规处理

- 发现未经需求文档更新直接修改代码的情况，需补充需求文档后方可继续
- 发现未经设计方案直接实现的情况，需补充设计说明

---

#### 快速参考

| 变更类型 | 需求文档 | 设计方案 | 直接编码 |
|---------|---------|---------|---------|
| 新增功能 | ✅ 必须 | ✅ 必须 | ❌ 禁止 |
| 修改功能 | ✅ 必须 | ✅ 必须 | ❌ 禁止 |
| 删除功能 | ✅ 必须 | ✅ 必须 | ❌ 禁止 |
| Bug 修复 | ✅ 必须 | 视复杂度 | ❌ 禁止 |
| 样式调整 | 可选 | 可选 | 允许 |

---

#### 需求文档编写规范

需求文档必须遵循以下格式，以便 AI 理解和自动生成测试用例：

##### 用户故事格式
```
### US-XXX-NN：功能标题
**作为** [角色]，**我希望** [操作]，**以便** [目的]。
```

##### 验收标准格式
```
**AC-XXX-NN-N：** [具体的可验证条件]
```

##### 条件交互标注
- 使用 `[条件] XXX=YYY时` 标注条件显示/隐藏的字段
- 条件字段隐藏时必须注明"隐藏并清空值"
- Switch 联动规则必须明确描述父子关系

##### 表单字段表格
每个表单必须包含完整的字段表格：
```
| 字段 | 必填 | 类型 | 说明 | 默认值 |
```

##### 测试用例生成友好性
- 每个 AC 必须是可独立验证的断言
- AC 中避免模糊描述（如"正确显示"），使用具体条件（如"状态标签颜色为绿色"）
- 条件交互的 AC 必须覆盖：条件满足时显示、条件不满足时隐藏、条件切换时值清空

### 7.1 前端开发流程

1. **环境搭建**：
   - 克隆代码仓库
   - 安装依赖：`npm install`
   - 启动开发服务器：`npm run dev`（端口 3001）

2. **方案确认**（适用于修改内容较大的情况）：
   - 制定技术方案，包括修改范围、技术实现方案、影响评估等
   - 经过项目负责人确认后，方可进入开发阶段

3. **功能开发**：
   - 创建功能分支：`git checkout -b feature/xxx`
   - 开发新功能或修复bug
   - 运行代码检查：`npm run lint`
   - 更新功能分析文档，确保文档与代码实现保持一致
   - 提交代码：`git commit -m "[feat] 实现xxx功能"`

4. **测试流程**：
   - 运行单元测试：`npm test`
   - 手动测试功能

### 7.2 后端开发流程

1. **环境搭建**：
   - 克隆代码仓库
   - 配置数据库连接
   - 构建项目：`mvn clean install`
   - 启动开发服务器：`mvn spring-boot:run`（端口 8080）

2. **功能开发**：
   - 创建功能分支：`git checkout -b feature/xxx`
   - 开发新功能或修复bug
   - 运行代码检查：`mvn compile`
   - 更新功能分析文档，确保文档与代码实现保持一致
   - 提交代码：`git commit -m "[feat] 实现xxx功能"`

3. **测试流程**：
   - 运行单元测试：`mvn test`
   - 手动测试API

### 7.3 终端使用规范

1. **终端管理**：
   - 每个任务使用独立的终端，避免混用不同功能的命令
   - 数据库操作（如查询、执行SQL脚本）应使用单独的终端
   - 前后端开发服务器应分别使用独立的终端

2. **数据库访问**：
   - 每次数据库操作应使用新的终端会话
   - 执行数据库脚本或查询时，确保终端环境正确配置
   - 数据库操作完成后，可关闭终端以保持环境整洁

3. **终端命令规范**：
   - 命令应清晰、完整，包含必要的参数
   - 复杂命令应在执行前确认正确性
   - 数据库密码等敏感信息不应硬编码在命令中

4. **终端输出管理**：
   - 定期清理终端输出，保持界面整洁
   - 重要的命令输出应保存或记录
   - 错误信息应及时分析和处理

### 7.4 服务器操作规范

**核心规则：所有服务器重启操作必须经过用户确认后方可执行**

1. **服务器启动/重启确认**：
   - **所有服务器操作**（包括但不限于：前端服务启动/重启、后端服务启动/重启、数据库服务重启等）必须先向用户说明操作目的、影响范围和预期结果
   - 在获得用户明确确认后，方可执行服务器操作
   - 紧急情况下的服务器操作（如服务崩溃需立即恢复），也需在操作后及时向用户说明情况

2. **操作说明内容要求**：
   - 操作类型：启动/重启/停止
   - 服务名称：前端/后端/数据库等
   - 操作原因：为什么需要执行该操作
   - 影响范围：该操作会影响哪些功能
   - 预期结果：操作后预期达到的效果
   - 预计耗时：大致需要多长时间

3. **确认方式**：
   - 使用 `AskUserQuestion` 工具进行正式确认
   - 确认后方可执行服务器操作命令

## 8. 部署信息

### 8.1 前端部署

1. **构建**：
   - 运行构建命令：`npm run build`
   - 生成静态文件到 `dist` 目录

2. **部署**：
   - 将 `dist` 目录部署到 Web 服务器
   - 配置 Nginx 服务器
   - 确保静态资源正确加载

### 8.2 远程服务器信息

| 配置项 | 值 |
|-------|-----|
| IP地址 | 117.72.168.234 |
| 操作系统 | CentOS |
| 用户名 | root |
| 密码 | Zhang2009@! |
| 部署内容 | 前端 |

### 8.3 后端部署

1. **构建**：
   - 运行构建命令：`mvn clean package`
   - 生成可执行 JAR 文件到 `target` 目录

2. **部署**：
   - 将 JAR 文件部署到服务器
   - 配置环境变量和数据库连接
   - 启动应用：`java -jar crs-backend-1.0.0.jar`
   - 配置反向代理（Nginx）

### 8.4 热部署配置

- **前端**：Vite HMR，`npm run dev` 启动后自动热更新
- **后端**：Spring Boot DevTools，`mvn spring-boot:run` 启动后代码变更自动重启

### 8.5 数据库部署

1. **初始化**：
   - 运行 SQL 脚本创建数据库架构：
     ```bash
     mysql -u root -p < backend/src/main/resources/database/init_xxx.sql
     ```
   - **数据库密码**：12345678

2. **迁移规范**：
   - **禁止**在代码中自动执行数据库初始化或迁移
   - 所有数据库操作必须**通过终端手动执行SQL脚本**
   - 迁移文件命名格式：`migration_YYYYMMDD_description.sql`
   - 存放路径：`backend/src/main/resources/database/`

3. **DatabaseMigration 类说明**：
   - 该类已**禁用自动执行**（注释掉了 `@Component` 注解）
   - 保留代码作为参考，不在应用启动时自动执行

4. **Repository 方法规范**：
   - 自定义删除/更新方法必须添加 `@Modifying` 和 `@Transactional` 注解
   - 使用 `@Query` 显式指定 JPQL 或 SQL 语句
   - 使用 `@Param` 绑定查询参数
   - 示例：
     ```java
     @Modifying
     @Transactional
     @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId")
     void deleteByUserId(@Param("userId") Integer userId);
     ```

5. **数据兼容性**：
   - 新增字段必须允许 NULL 或设置合理的默认值
   - 删除字段前需要确认没有代码在使用
   - 修改字段类型前需要考虑数据转换
   - 涉及数据迁移的操作需要先在测试环境验证

## 9. 最佳实践

### 9.1 前端最佳实践

1. **组件设计**：遵循单一职责原则，使用函数组件和 Hooks，合理使用 Context API 管理状态
2. **状态管理**：组件内部状态使用 `useState`，跨组件状态使用 Context API
3. **API 调用**：集中管理 API 调用，使用 Axios 拦截器处理请求和响应，避免在组件渲染时直接调用 API
4. **性能优化**：使用 `React.memo`、`useCallback`、`useMemo`，懒加载组件

### 9.2 后端最佳实践

1. **架构设计**：遵循 RESTful API 设计规范，采用分层架构（控制器 → 服务 → 仓库）
2. **数据库操作**：使用 JPA，避免 N+1 查询问题，确保事务的正确使用
3. **安全实践**：使用 Spring Security + JWT，密码 BCrypt 加密存储
4. **性能优化**：合理使用缓存，优化数据库查询

## 10. 前端页面保护规则

**当前阶段目标：在不修改前端页面的前提下，完成后端功能开发。**

- **禁止修改** `src/pages/` 下的任何页面组件文件
- **禁止修改** `src/components/` 下的任何组件文件
- **禁止修改** `src/router/index.jsx` 路由配置
- **禁止修改** `src/contexts/` 下的 Context 文件
- **禁止修改** `src/utils/api.js` 的已有接口定义
- **允许** 在 `src/utils/api.js` 中新增 API 方法（不修改已有方法）
- **允许** 修改 `src/utils/settings.js`
- **允许** 修改后端所有代码（`backend/` 目录）
- **允许** 新增数据库迁移脚本

**后端接口设计原则：**
- 后端 API 必须适配前端已有的调用方式和数据格式
- 响应数据结构必须与前端页面期望的格式一致
- 演示模式（DEMO_MODE）下前端使用 Mock 数据，后端接口需在非演示模式下提供相同结构的真实数据

## 11. 自动化测试

项目包含基于 Python + Playwright 的 UI 自动化测试工具，位于 `.kiro/skills/crs-testing/`。

```bash
# 安装依赖
pip install playwright
playwright install

# 运行所有模块测试
python .kiro/skills/crs-testing/test_runner.py --url http://localhost:3001

# 运行指定模块
python .kiro/skills/crs-testing/test_runner.py --modules login dashboard hotel_management

# 查看所有可用模块
python .kiro/skills/crs-testing/test_runner.py --list-modules
```
