# 社区医院信息平台 MVP

当前版本已经完成 MySQL 持久化，采用前后端分离的模块化单体结构：

- `frontend/`：Vue 3 + Vite + Element Plus + Pinia + Vue Router
- `backend/`：Spring Boot + Spring Security + MyBatis-Plus + Elasticsearch
- `sql/`：MySQL 初始化脚本
- `docs/`：接口与架构说明

## 模块划分

后端按业务模块拆分：

- `common`：统一返回体、异常处理
- `auth`：登录、JWT、角色鉴权
- `patient`：患者档案
- `visit`：就诊记录、诊断、处方
- `drug`：药品与库存流水
- `timeline`：病历时间轴
- `search`：统一检索
- `admin`：用户与角色视图

## MVP 范围

按以下顺序完成：

1. 患者管理
2. 就诊记录
3. 药品库存
4. 权限控制
5. 搜索与时间轴

## 数据与搜索说明

- 业务数据当前持久化到本机 MySQL `community_hospital`
- 初始化脚本为 `sql/init.sql`
- 搜索当前默认走 Elasticsearch
- 可调用 `POST /api/search/sync` 手动触发全量索引同步

## 启动方式

### 方式一：Docker Compose（推荐，适合远程部署）

一键启动整套环境（MySQL + Elasticsearch + 后端 + 前端）：

```bash
# 复制环境变量模板（可选，使用默认值可跳过）
cp .env.example .env

# 启动
docker compose up -d

# 查看日志
docker compose logs -f
```

启动完成后访问 `http://localhost`，前端与后端已由 Nginx 统一代理。

环境变量（`.env`）说明：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| MYSQL_ROOT_PASSWORD | 123456 | MySQL root 密码 |
| MYSQL_USER | app | 后端使用的数据库用户 |
| MYSQL_PASSWORD | 123456 | 数据库用户密码 |
| ES_PASSWORD | changeme | Elasticsearch elastic 用户密码 |

### 方式二：本地开发

#### 1. 初始化数据库

确保本机 MySQL 已启动，并存在可用账号。默认配置：

- 数据库：`community_hospital`
- 用户名：`root`
- 密码：`123456`

导入初始化脚本：

```bash
mysql -uroot -p123456 --default-character-set=utf8mb4 -e "source E:/cursorworkspace/sqyy/sql/init.sql"
```

#### 2. 前端

```bash
cd frontend
pnpm install
pnpm dev
```

#### 3. 后端

```bash
cd backend
mvn spring-boot:run
```

默认前端访问 `http://localhost:5173`，后端访问 `http://localhost:8080`。

Elasticsearch 需按 `backend/src/main/resources/application.yml` 中的配置启动本机实例。

---

演示账号：

- `admin / 123456`
- `doctor / 123456`
- `pharmacist / 123456`
- `reception / 123456`
