# 协作与提交规范

## 第一次使用

```bash
git clone https://github.com/arcofz/-.git xunwu-leida
cd xunwu-leida
git config user.name "你的名字"
git config user.email "你的 GitHub 邮箱"
```

仓库名是 `-`，克隆时一定要加目录名 `xunwu-leida`，否则会生成一个叫 `-` 的文件夹。

## 每次开发

```bash
git checkout main
git pull --rebase
git checkout -b feat/c-claim
# 写代码
git add .
git commit -m "feat: 认领审核接口"
git push -u origin feat/c-claim
```

推送后在 GitHub 开 Pull Request，目标分支选 `main`，按模板写清做了什么、怎么测的。不要直接往 `main` 推。

## 分支命名

- `feat/c-xxx`：C 的后端或数据库功能
- `feat/d-xxx`：D 的前端功能
- `fix/xxx`：修 bug
- `docs/xxx`：只改文档

一个分支只做一件事，不要顺手改无关文件。

## 提交信息

- `feat: 发布失物接口`
- `fix: 重复认领校验`
- `docs: 补充接口字段`
- `test: 认领流程用例`
- `chore: 更新依赖`

## C 上传什么

- `backend/src/main/java/com/xunwu/leida/` 下的 controller、service、mapper、entity、dto
- `backend/src/main/resources/mapper/*.xml`
- `database/schema.sql`，或 `database/migrations/` 下的变更脚本
- `docs/api.md` 的接口字段和错误码
- `src/test/java/` 下的测试
- Postman 集合放 `docs/postman/`

不要提交：`target/`、`application-local.yml`、真实密码、API Key。

## D 上传什么

- `frontend/src/views/`、`components/`、`api/`、`router/`、`stores/`
- `package.json` 和 `package-lock.json`（只有加依赖时才改）
- 测试用例放 `frontend/tests/` 或 `docs/test-cases.md`
- 页面截图、录屏发 QQ 群，不要塞进仓库；确实要放，放 `docs/evidence/` 并压缩

不要提交：`node_modules/`、`dist/`、`.env`、任何 Key。

## 数据库变更

- 新环境：执行 `database/schema.sql` 和 `database/seed.sql`
- 已有环境：在 `database/migrations/` 新建 `YYYYMMDD_说明.sql`，不要改已经执行过的脚本
- 表数量或关系变了，同步改 `docs/api.md` 和 PPT 第 6、7 页

## PR 检查

- [ ] 这个 PR 只做了一件事
- [ ] 接口或表结构变了，文档已同步
- [ ] 前端 `npm run build` 通过，或后端测试通过
- [ ] 没有提交密码、Key、`node_modules`、`target`
- [ ] PR 描述里写了怎么测
- [ ] 页面改动附了截图

## 群里日报

每天 21:00 前发一条：

```text
【C 进度 11-11】
昨天：
今天：
完成：
阻塞：
需要谁：
分支 / PR：
```

D 同样格式。卡住超过半天，直接说卡在哪，不要等到晚上。
