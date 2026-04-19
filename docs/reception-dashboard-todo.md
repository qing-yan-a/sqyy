# 前台工作台 TODO List

## 背景
前台角色（RECEPTION）当前的 Dashboard 和管理员共用同一个模板，显示药品库存等无关信息。
需要给前台独立的工作台，聚焦挂号导诊核心职责。

## 涉及文件
- 后端：`HospitalPersistenceService.java`（新增统计方法）
- 后端：新建 `DashboardController.java`（前台专用接口）
- 前端：`DashboardView.vue`（前台独立模板）
- 前端：`api/index.ts`（新增接口调用）

---

## TODO 1 — 后端：新增前台统计接口方法
**文件**: `HospitalPersistenceService.java`
**内容**: 新增 `getReceptionDashboard()` 方法，返回：
- 今日概览：今日挂号数、待就诊数、已完成数
- 候诊队列：当前 PENDING 状态的就诊记录（含患者信息、科室、医生、排队号）
- 各科室排队情况：按科室分组统计今日挂号数/待就诊数/已完成数

## TODO 2 — 后端：新建 DashboardController
**文件**: 新建 `dashboard/controller/DashboardController.java`
**内容**:
- `GET /api/dashboard/reception` — 前台工作台数据，限制 RECEPTION/ADMIN 角色
- 调用 `HospitalPersistenceService.getReceptionDashboard()`

## TODO 3 — 前端：新增 API 调用
**文件**: `api/index.ts`
**内容**: 新增 `getReceptionDashboard()` 方法，调用后端接口

## TODO 4 — 前端：DashboardView 前台独立模板
**文件**: `DashboardView.vue`
**内容**:
- 在 `v-else` (管理员/前台) 前加 `v-else-if="isReception"` 判断
- 前台模板包含：
  - 4 个 StatCard（今日挂号、待就诊、已完成、今日患者数）
  - 候诊队列卡片（排队患者列表，显示号/姓名/科室/医生）
  - 各科室排队情况卡片（科室忙闲状态）
  - 快捷操作区（跳转挂号、患者检索）

## TODO 5 — 修复 createRegistration 科室硬编码问题
**文件**: `HospitalPersistenceService.java` 的 `createRegistration` 方法
**问题**: 当前硬编码 `department = "全科门诊"`，挂号时未根据医生所属科室设置
**内容**: 根据医生的 department 字段自动设置就诊科室

---

## 进度追踪
- [x] TODO 1 — 后端统计方法 ✅ `getReceptionDashboard()` 已添加
- [x] TODO 2 — 后端 Controller ✅ `DashboardController.java` 已创建
- [x] TODO 3 — 前端 API ✅ `getReceptionDashboard()` 已添加
- [x] TODO 4 — 前端 Dashboard 模板 ✅ 前台独立模板已完成（4卡片+候诊队列+科室情况+快捷操作）
- [x] TODO 5 — 修复科室硬编码 ✅ `createRegistration` 改为根据医生科室自动设置
