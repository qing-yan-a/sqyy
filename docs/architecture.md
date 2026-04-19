# 第一版技术栈与目录

## 技术栈

- 前端：Vue 3、Vite、TypeScript、Element Plus、Pinia、Vue Router、Axios、ECharts
- 后端：Spring Boot、Spring Security、MyBatis-Plus、JWT、MySQL、Elasticsearch
- 部署：前后端分离，后端保持模块化单体
- 持久化：核心业务数据落库到 MySQL，搜索索引写入 Elasticsearch

## 项目目录

```text
sqyy/
├─ frontend/
├─ backend/
├─ sql/
└─ docs/
```

## 后端模块结构

```text
backend/src/main/java/com/sqyy/hospital/
├─ admin/
├─ auth/
├─ common/
├─ config/
├─ drug/
├─ model/
├─ patient/
├─ persistence/
├─ search/
├─ service/
├─ timeline/
└─ visit/
```

## 前端模块结构

```text
frontend/src/
├─ api/
├─ components/
├─ layout/
├─ router/
├─ stores/
├─ types/
└─ views/
```

## 第一版交付重点

- 登录与角色鉴权
- 患者建档与列表查询
- 就诊记录、诊断、处方录入
- 药品维护、库存调整、流水查看
- 用户角色概览
- 统一搜索与病历时间轴展示

## 当前后端数据流

- 控制器通过 `service` 层执行业务逻辑
- `persistence/entity` 与 `persistence/mapper` 负责 MySQL 表映射
- `model/HospitalModels` 统一承载接口返回模型和命令对象
- `search` 模块从数据库服务读取患者、就诊、药品数据并同步到 Elasticsearch
- `timeline` 模块直接聚合数据库中的患者、就诊、诊断、处方数据
