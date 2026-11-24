# 寻物雷达 API 契约

> 2026-11-24 已确认前后端契约。C / D 按本文档实现；改接口先改本文档，再改代码。

## 基础约定

- Base URL：`/api`
- 认证：`Authorization: Bearer <JWT>`
- 统一响应：`{ "code": 0, "message": "ok", "data": ... }`
- 成功 200，`code=0`；错误使用对应 HTTP 状态码，`code` 与 HTTP 状态一致，`data=null`
- 错误码：400 参数错误，401 未登录 / token 过期，403 无权限，404 不存在，409 冲突，500 服务器错误
- 登录密码错误返回 **400**，不要用 401；401 只用于未登录或 token 过期
- 字段命名：API 用 camelCase，数据库用 snake_case
- 时间格式：ISO-8601 带时区，如 `2026-11-24T15:00:00+08:00`

## 接口清单

| 模块 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 认证 | POST | `/auth/register` | 注册 |
| 认证 | POST | `/auth/login` | 登录，返回 `{ token, user }` |
| 认证 | GET | `/auth/me` | 当前用户 |
| 信息发布 | GET | `/items` | 列表；筛选 `type`、`categoryId`、`locationId`、`page`、`pageSize` |
| 信息发布 | POST | `/items` | 发布失物或拾物 |
| 信息发布 | GET | `/items/{id}` | 详情；登录时带 `myClaim` |
| 信息发布 | PUT | `/items/{id}` | 编辑，仅发布者 |
| 信息发布 | DELETE | `/items/{id}` | 软删除，仅发布者或管理员 |
| 匹配 | GET | `/items/{id}/matches` | 返回 Top 5 与分数、理由 |
| 匹配 | POST | `/matches/{id}/confirm` | 确认匹配，仅失主 / 管理员 |
| 匹配 | POST | `/matches/{id}/reject` | 拒绝匹配，仅失主 / 管理员 |
| 认领 | POST | `/claims` | 提交认领与证据 |
| 认领 | GET | `/claims/mine` | 我发起的 + 我收到的认领 |
| 认领 | POST | `/claims/{id}/approve` | 通过，可带交接信息 |
| 认领 | POST | `/claims/{id}/reject` | 驳回，body `{ reason }` 必填 |
| 交接 | POST | `/handovers` | 补登记交接 |
| 交接 | GET | `/handovers/{id}` | 交接详情，仅双方 / 管理员 |
| 通知 | GET | `/notifications` | 通知列表 |
| 通知 | POST | `/notifications/{id}/read` | 标记已读 |
| 基础数据 | GET | `/categories` | 分类列表，公开 |
| 基础数据 | GET | `/locations` | 地点列表，公开 |
| 上传 | POST | `/uploads/images` | 图片上传，返回 `{ url }` |
| 后台 | GET | `/admin/users` | 用户管理 |
| 后台 | GET | `/admin/categories` | 分类管理 |
| 后台 | GET | `/admin/locations` | 地点管理 |
| 后台 | GET | `/admin/stats` | 统计 |

## 已确认的契约

### 1. 列表、分页、筛选

- 所有列表统一返回 `{ list, total, page, pageSize }`。
- `GET /items` 筛选参数固定为 `type`、`categoryId`、`locationId`、`page`、`pageSize`。
- `GET /categories`、`GET /locations` 返回 `{ list: [...] }`。

### 2. 认领列表 `GET /claims/mine`

返回当前用户相关的两类记录：我发起的认领，以及我发布的物品收到的认领。

每条记录至少包含：

```json
{
  "id": 12,
  "itemId": 4,
  "itemTitle": "黑色雨伞",
  "itemType": "found",
  "claimantId": 3,
  "claimantName": "李四",
  "ownerId": 2,
  "ownerName": "张三",
  "evidence": "伞柄有蓝色挂绳",
  "status": "pending",
  "reason": null,
  "createdAt": "2026-11-24T15:00:00+08:00",
  "reviewedAt": null,
  "handoverId": null
}
```

只允许返回与当前用户相关的记录；支持 `role=all|claimant|owner`、`status`、`page`、`pageSize` 等可选参数，默认 `role=all`。

### 3. 审核通过与交接

`POST /claims/{id}/approve` 请求体可选：

```json
{
  "handTime": "2026-11-24T16:00:00+08:00",
  "place": "图书馆一楼服务台"
}
```

- **两个都传**：在一个事务里改 `claims.status=approved`、写 `handovers`、把 `items.status` 置为 `completed`、写通知；返回 `{ claim, handover }`。
- **两个都不传**：只改 `claims.status=approved` 并写通知，`items` 保持 `claimed`；返回 `{ claim, handover: null }`，之后用 `POST /handovers` 补登记。
- **只传一个**：400。
- 只有物品发布者或管理员可以操作；只允许 `pending` 状态，否则 409。

`POST /handovers` 用于补登记：只允许 `approved` 且没有 handover 的认领；事务里写 `handovers`、更新 `items.status=completed`、写通知；重复登记返回 409。

### 4. 联系方式可见性

`publisherContact` 的返回规则，列表和详情一致：

| 访问者 | 返回值 |
| --- | --- |
| 匿名 | `null` |
| 登录用户，非发布者，认领未通过 | 脱敏值，如 `138****2222` |
| 发布者本人、管理员、认领已通过的申请人 | 完整联系方式 |

必须由后端裁剪，不能依赖前端 `v-if` 隐藏。

### 5. 驳回理由

`POST /claims/{id}/reject` 请求体：

```json
{ "reason": "证据不足以证明物品归属" }
```

- `reason` 必填，1–200 字，缺少或超长返回 400。
- `claims` 表增加 `reason TEXT`，同时写入通知内容。
- 需要同步修改 `database/schema.sql`，并新增迁移脚本。
- 返回的 claim 对象包含 `reason`。

### 6. 交接详情 `GET /handovers/{id}`

必须登录，只允许认领人、物品发布者、管理员访问，其他用户 403。

返回字段：

```json
{
  "id": 1,
  "claimId": 1,
  "itemId": 6,
  "itemTitle": "钥匙串",
  "itemType": "found",
  "ownerId": 2,
  "ownerName": "张三",
  "ownerContact": "13811112222",
  "claimantId": 4,
  "claimantName": "王五",
  "claimantContact": "13855556666",
  "handTime": "2026-11-20T16:00:00+08:00",
  "place": "图书馆一楼服务台",
  "createdAt": "2026-11-20T15:30:00+08:00"
}
```

### 7. 物品详情的 `myClaim`

`GET /items/{id}` 对登录用户返回当前用户对该物品的认领状态：

```json
"myClaim": {
  "id": 12,
  "status": "pending",
  "createdAt": "2026-11-24T15:00:00+08:00"
}
```

没有认领或匿名访问时为 `null`。前端据此显示「已申请」，不能只靠页面内临时状态。

### 8. 匹配操作权限

`POST /matches/{id}/confirm`、`POST /matches/{id}/reject` 只允许**失主**（lost item 的发布者）或管理员操作；拾物发布者只能查看，不能确认或拒绝。后端必须强制。

### 9. 图片上传

`POST /uploads/images`：登录后可用，`multipart/form-data`，字段名 `file`。

- 只接受图片类型，大小不超过 2MB。
- 返回 `{ "url": "https://..." }`。
- 发布页拿到 `url` 后，随 `POST /items` 的 `imageUrl` 字段提交。

## 实现分工

### C 需要实现

- `claims.reason` 字段及迁移脚本。
- `GET /claims/mine` 返回两类记录和完整字段。
- `approve` 支持可选交接信息，并保证一个事务。
- `reject` 接收并保存 `reason`。
- 联系方式裁剪和 `myClaim` 字段。
- `GET /handovers/{id}` 权限和双方字段。
- 公开分类/地点接口、图片上传接口。
- 匹配确认/拒绝权限。

### D 需要跟进

- ItemList 筛选参数改为 `categoryId`、`locationId`。
- Claims 审批时把 `handTime`、`place` 一起传给 approve，不再单独调 handovers（补登记场景除外）。
- ItemDetail 用 `myClaim` 初始化「已申请」按钮。
- `http.js` 错误分支补 `error.code = error.response?.data?.code ?? error.response?.status`。
- Mock 同步新的响应字段、权限和联系方式规则。

## 约定

- 改接口先改本文档，再改代码。
- 每个接口至少自测：正常、参数错误、未登录、无权限。
- 表结构或接口字段变化，同步更新本文档和答辩 PPT 第 6、7 页。

## 实现说明

> 2026-11-24 C 端实现补充，不改动上文已确认的契约条款。

### 数据库新列与迁移

- `items.image_url VARCHAR(512)`：发布/编辑时随 `imageUrl` 提交，列表与详情原样返回。
- `claims.reason TEXT`：驳回理由，`approve` 时为 `null`。
- 两列已同步进 `database/schema.sql`；存量库执行迁移脚本 `database/migrations/20261124_add_item_image_and_claim_reason.sql`（`ADD COLUMN IF NOT EXISTS`，可重复执行）。

### 匹配分数与 reasons

- 权重：分类一致 +40；同一地点 +25（不同地点但同一栋楼 +15）；时间差 ≤24h +20、≤72h +15、≤7 天 +10、≤30 天 +5；文本相似度 ×15。总分封顶 100，四舍五入保留 2 位小数。
- 文本相似度：`title + description` 合并后，中文按二元组、英文/数字按单词切分，取 Dice 系数（`2×|交集| / (|A|+|B|)`）。
- `GET /items/{id}/matches` 返回 Top 5，`reasons` 为中文短句数组，格式示例：`"分类一致 +40"`、`"同一地点 +25"`、`"同一栋楼 +15"`、`"时间相差 3 小时 +20"`（不足 1 小时按分钟、不足 24 小时按小时、其余按天）、`"文本相似度 0.62 +9.30"`。
- 结果 upsert 进 `matches`（唯一键 `lost_item_id + found_item_id`），已存在则刷新分数并把状态重置为 `pending`。
- 候选排除自身、已软删、`cancelled` / `completed` 的物品，只取相反 `type`。

### 图片本地存储与 URL 规则

- 存储目录：`app.upload.dir`（默认 `uploads`，相对 backend 工作目录），可用环境变量 `UPLOAD_DIR` 覆盖。
- 文件名：`UUID.扩展名`（白名单 jpg/jpeg/png/gif/webp），不使用原始文件名。
- 访问路径：`/uploads/{filename}`，由后端静态资源映射直接对外提供。
- 返回 URL：`{UPLOAD_PUBLIC_BASE_URL}/uploads/{filename}`；`UPLOAD_PUBLIC_BASE_URL` 为空时返回相对路径 `/uploads/{filename}`。
