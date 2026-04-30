## graphify

This project has a graphify knowledge graph at graphify-out/.

Rules:
- Before answering architecture or codebase questions, read graphify-out/GRAPH_REPORT.md for god nodes and community structure
- If graphify-out/wiki/index.md exists, navigate it instead of reading raw files
- After modifying code files in this session, run `graphify update .` to keep the graph current (AST-only, no API cost)

## 开发工作流规则

所有的修改都必须遵循以下流程：
1. **需求分析**：首先对用户提出的修改需求进行详细分析。
2. **需求确认**：在分析完成后，必须先与用户确认需求是否准确。
3. **技术设计**：需求确认后，方可进行技术设计。
4. **代码编写**：技术设计完成后，最后进行代码编写。

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

**2. 根目录补充文档（待逐步梳理与迁移）**
- `CRS系统功能文档.md`
- `供应商产品质量体系PRD.md`
- `集团CRS核心要求.MD`
> **注意**：建议后续修改或新增功能时，将上述根目录的文档按模块拆分并逐步迁移至 `.kiro/specs/prd/` 目录下。


