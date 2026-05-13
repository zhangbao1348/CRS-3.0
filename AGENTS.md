## graphify

This project has a graphify knowledge graph at graphify-out/.

Rules:
- Before answering architecture or codebase questions, read graphify-out/GRAPH_REPORT.md for god nodes and community structure
- If graphify-out/wiki/index.md exists, navigate it instead of reading raw files
- After modifying code files in this session, run `graphify update .` to keep the graph current (AST-only, no API cost)

## 开发工作流规则

所有的系统开发和代码修改都必须严格遵循以下 **五阶段标准操作流程 (SOP)**：

**第一阶段：环境感知与记忆同步 (Context Alignment)**
- **执行 Graphify 全量扫描**：扫描当前工程目录，更新知识图谱，特别识别模块间的依赖关系。
- **检索长期记忆**：搜索 Postgres 数据库中关于需求和技术选型的历史决策记录。

**第二阶段：方案设计与对齐 (Blueprint & Review)**
- **生成实现方案 (RFC)**：在修改代码前，先用中文列出修改逻辑、新增接口定义以及对现有图谱的影响。
- **检查冲突**：对比长期记忆中的现有编码规范，确认方案是否与之冲突。
- **等待确认**：方案输出后必须停止，等待用户的 `Proceed` 指令后再进行代码写入。

**第三阶段：高精度代码实施 (Precision Implementation)**
- **保持代码原子性**：每次修改需遵循单一职责原则，严禁删除现有的注释和文档字符串（Docstrings）。
- **实时同步图谱**：每完成一个核心模块的修改，自动更新本地 Graphify 索引。
- **注入元数据**：在新增的函数或类中，自动添加符合项目规范的注释，并注明关联模块。

**第四阶段：验证与持久化 (Validation & Persistence)**
- **执行静态检查**：代码写入后，立即运行项目定义的 Lint 或 Type Check 命令。
- **存储关键决策**：将核心逻辑、报错及解决方案作为“经验片段”存入 Postgres 长期记忆。
- **生成变更报告**：简要总结改动，提示对项目图谱产生的任何重大拓扑变化。

**第五阶段：文档同步**
- 如果发生了代码变更，必须同步更新 PRD 内容，并标记功能是否已经实现。

## 语言规则

- 所有的回复、思考过程、实施计划、任务列表和最终报告都必须使用**中文**。

## 文件存储规则

- **PRD 文档**：所有的 PRD 文档必须存储在 `.kiro/specs/prd` 文件夹下。

## PRD 文档索引与位置

为了便于项目开发和追溯，现有的需求与系统功能文档分布如下：

**1. 标准 PRD 目录（主干需求）**
- **位置**：`.kiro/specs/prd/`
- **核心文件索引**：
  - `00-SOW-功能清单.md`
  - `08-集团管理.md`
  - `09-系统设置.md`
  - `10-价格计划管理.md`
  - `11-库存管理.md`
  - `12-房型管理.md`
  - `13-渠道管理.md`
  - `实时查询接口.md`


> **注意**：建议后续修改或新增功能时，将上述根目录的文档按模块拆分并逐步迁移至 `.kiro/specs/prd/` 目录下。


