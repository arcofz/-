# 寻物雷达 API 契约

- Base URL：`/api`
- 认证：`Authorization: Bearer <JWT>`
- 统一响应：`{ "code": 0, "message": "ok", "data": ... }`
- 错误码：0 成功，400 参数错误，401 未登录，403 无权限，404 不存在，409 冲突，500 服务器错误

## 接口清单

| 模块 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 认证 | POST | `/auth/register` | 注册 |
| 认证 | POST | `/auth/login` | 登录，返回 JWT |
| 认证 | GET | `/auth/me` | 当前用户 |
| 信息发布 | GET | `/items` | 列表，支持 type/category/location 筛选 |
| 信息发布 | POST | `/items` | 发布失物或拾物 |
| 信息发布 | GET | `/items/{id}` | 详情 |
| 信息发布 | PUT | `/items/{id}` | 编辑，仅发布者 |
| 信息发布 | DELETE | `/items/{id}` | 软删除，仅发布者或管理员 |
| 匹配 | GET | `/items/{id}/matches` | 返回 Top 5 与分数、理由 |
| 匹配 | POST | `/matches/{id}/confirm` | 确认匹配 |
| 匹配 | POST | `/matches/{id}/reject` | 拒绝匹配 |
| 认领 | POST | `/claims` | 提交认领与证据 |
| 认领 | GET | `/claims/mine` | 我的认领 |
| 认领 | POST | `/claims/{id}/approve` | 发布者通过 |
| 认领 | POST | `/claims/{id}/reject` | 发布者驳回 |
| 交接 | POST | `/handovers` | 登记交接 |
| 交接 | GET | `/handovers/{id}` | 交接详情 |
| 通知 | GET | `/notifications` | 通知列表 |
| 通知 | POST | `/notifications/{id}/read` | 标记已读 |
| 后台 | GET | `/admin/users` | 用户管理 |
| 后台 | GET | `/admin/categories` | 分类管理 |
| 后台 | GET | `/admin/locations` | 地点管理 |
| 后台 | GET | `/admin/stats` | 统计 |

## 约定

- 字段命名用 camelCase；数据库用 snake_case。
- 改接口先改本文档，再改代码。
- 每个接口至少自测：正常、参数错误、未登录、无权限。
