# API 摘要

接口统一前缀：`/api`

## 认证

- `POST /api/auth/login`：登录
- `GET /api/auth/profile`：当前用户信息

## 患者

- `GET /api/patients`：患者列表
- `POST /api/patients`：新建患者

## 就诊

- `GET /api/visits`：就诊记录列表，可按 `patientId` 过滤
- `POST /api/visits`：新建就诊记录，附带诊断与处方
- `PUT /api/visits/{visitId}`：编辑就诊记录，自动回退旧处方库存并重算新库存
- `DELETE /api/visits/{visitId}`：删除就诊记录，并回退关联处方库存

## 药品与库存

- `GET /api/drugs`：药品列表
- `POST /api/drugs`：新建药品
- `GET /api/drugs/inventory/logs`：库存流水
- `POST /api/drugs/inventory/logs`：库存变更

## 用户与角色

- `GET /api/admin/users`：用户角色列表
- `GET /api/admin/users/roles`：角色列表（用于创建用户时选择）
- `POST /api/admin/users`：新建用户（ADMIN）
- `DELETE /api/admin/users/{userId}`：删除用户（ADMIN，admin 用户不可删除）

## 认证（续）

- `POST /api/auth/change-password`：修改当前用户密码（需登录，原密码 + 新密码）

## 搜索与时间轴

- `GET /api/search?q=关键字`：统一搜索
- `POST /api/search/sync`：手动触发 Elasticsearch 全量同步，仅 `ADMIN`
- `GET /api/timeline/patients/{patientId}`：患者病历时间轴

## 数据来源说明

- 患者、就诊、诊断、处方、药品、库存流水、用户角色均来自 MySQL
- 搜索结果来自 Elasticsearch 索引
- `POST /api/search/sync` 会按当前数据库数据重建索引

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```
