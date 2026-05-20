# BMAD (Breakthrough Method for Agile AI-Driven Development)

本项目采用 BMAD 方法论进行开发。BMAD 旨在通过结构化的 AI 代理协作、文档驱动的流程和持续的记忆反馈，提升 AI 驱动软件开发的可靠性和质量。

## 核心流程：五阶段 SOP

1.  **环境感知 (Context Alignment)**: 通过 `graphify` 扫描和 `memory` 检索，确保 AI 代理具备完整的上下文。
2.  **方案设计 (Blueprint & Review)**: 采用 RFC 机制，在编码前进行详细的技术设计和风险评估。
3.  **高精度实施 (Precision Implementation)**: 遵循原子化修改原则，注入元数据，保持代码与文档的一致性。
4.  **验证与持久化 (Validation & Persistence)**: 运行自动化测试，并将成功/失败的经验存入长期记忆。
5.  **文档同步 (Doc Sync)**: 确保 PRD、设计文档和代码实时同步。

## 角色矩阵

- **Analyst (分析师)**: 负责需求挖掘、竞品分析和 PRD 编写。
- **Architect (架构师)**: 负责系统设计、数据库建模和技术选型。
- **Developer (开发者)**: 负责代码实现、代码质量和单元测试。
- **QA (质量保障)**: 负责自动化测试、质量门禁和回归验证。

## 长期记忆 (.kiro/memory)

- `decisions/`: 架构和业务的核心决策点。
- `lessons/`: 报错模式、修复方案和最佳实践。
- `context/`: 迭代过程中的关键状态快照。

---

> 每一个字符的变更，都应有据可查；每一次报错的修复，都应成为永恒的知识。
