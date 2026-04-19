# Elasticsearch 接入说明

## 当前策略

为了不阻塞第一版开发，系统默认使用：

- `app.search.engine: memory`
- `app.search.elasticsearch.enabled: false`

这样即使本机未启动 Elasticsearch，项目也能正常运行。

## 已完成的 ES 骨架

- 后端已加入 Elasticsearch 依赖
- 已抽象 `SearchService` / `SearchSyncService`
- 已定义统一索引文档 `SearchDocument`
- 已实现 `ElasticsearchSearchService`
- 已提供启动自动同步开关与手动同步接口 `POST /api/search/sync`

## 启用方式

修改 `backend/src/main/resources/application.yml`：

```yml
app:
  search:
    engine: elasticsearch
    auto-sync-on-startup: true
    elasticsearch:
      enabled: true

spring:
  elasticsearch:
    uris:
      - http://localhost:9200
```

## 当前索引划分

- `community-hospital-patient`
- `community-hospital-visit`
- `community-hospital-drug`

## 同步策略

- 患者新增后触发全量同步
- 就诊新增后触发全量同步
- 药品新增、库存变更后触发全量同步

后续如果切换到真实数据库层，可以再把“全量同步”优化成“按实体增量同步”。
