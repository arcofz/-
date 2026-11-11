# 寻物雷达 · 校园失物智能匹配与认领平台

数据库课程设计项目代码库。前后端分离，数据库设计是重点。

## 技术栈

- 前端：Vue 3、Vite、Element Plus、Pinia、Axios
- 后端：Java 21、Spring Boot 3、MyBatis、JWT
- 数据库：PostgreSQL 16
- 构建：Maven、npm

## 目录

- `backend/`：Spring Boot 后端
- `frontend/`：Vue 3 前端
- `database/`：建表、种子数据、AI 扩展脚本
- `docs/`：接口契约

## 核心设计

- 角色：游客、学生、管理员
- 流程：发布 → 匹配 → 通知 → 认领审核 → 交接归档
- 8 张表：users、categories、locations、items、matches、claims、handovers、notifications
- 匹配权重：分类 40、地点 25、时间 20、文本 15
- 认领通过：claims 状态 + handovers + notifications 在同一个事务里

## 快速开始

### 1. 数据库

```bash
createdb xunwu_leida
psql -d xunwu_leida -f database/schema.sql
psql -d xunwu_leida -f database/seed.sql
# 可选：AI 识图字段
# psql -d xunwu_leida -f database/ai_extension.sql
```

### 2. 后端

需要 JDK 21、Maven 3.9+（或 IDEA 自带的 Maven）。

```bash
cd backend
# 设置 DB_URL、DB_USER、DB_PASSWORD、JWT_SECRET
mvn spring-boot:run
```

健康检查：`GET http://localhost:8080/api/health`

### 3. 前端

```bash
cd frontend
npm install
npm run dev
```

默认地址 `http://localhost:5173`，`/api` 代理到 8080。

## 分支与提交

- `main` 保持可运行，不直接开发。
- 功能分支：`feat/match`、`fix/claim-transaction`。
- 提交信息：`feat: 发布失物接口`、`fix: 重复认领校验`。
- 合并前至少自测主流程。

完整流程、分工提交内容和 PR 检查见 `CONTRIBUTING.md`。

## 文档

- 接口契约：`docs/api.md`
- 建表脚本：`database/schema.sql`
- AI 扩展：`database/ai_extension.sql`

## 说明

课件、讲稿、任务指导不在本仓库，避免大文件。
