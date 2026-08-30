# 历史 SQL 脚本

本目录及上级 `database/` 中的 SQL 是 Flyway 接入前形成的历史初始化/迁移脚本，仅保留审计与人工排查用途，不会被应用启动自动执行。

自 Flyway V1 baseline 起，所有新增结构变更必须使用 `backend/src/main/resources/db/migration/V{版本}__{说明}.sql`，不得修改已经执行过的版本文件，也不得把未版本化 SQL 放回 Flyway 扫描目录。

`migration_drop_redundant_ids.sql` 从旧的 `db/` 位置迁入此处，因为它包含破坏性删列操作，不能作为首次 Flyway migration 自动执行。
