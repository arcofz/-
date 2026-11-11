# 数据库变更

- 新环境：执行 `database/schema.sql` 和 `database/seed.sql`。
- 已有环境：在本目录新建 `YYYYMMDD_说明.sql`，例如 `20261120_add_ai_fields.sql`。
- 不要修改已经执行过的迁移脚本；需要改就再加一个。
- 表数量、关系、约束有变化，同步更新 `docs/api.md` 和答辩 PPT 第 6、7 页。
