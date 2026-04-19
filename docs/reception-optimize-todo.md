# 前台及患者/就诊模块优化 TODO

## TODO 1 — 前台可编辑患者信息
- 后端：`assertCanEditPatient` 添加 RECEPTION 权限
- 前端：PatientView 操作列对 RECEPTION 显示编辑按钮

## TODO 2 — 挂号弹窗显示医生排队数
- 后端：新增接口或扩展 getDoctors 返回排队数
- 前端：挂号弹窗医生下拉显示排队人数

## TODO 3 — 候诊队列加取消挂号
- 前端：DashboardView 候诊队列加取消按钮
- 调用已有的 cancelRegistration 后端接口（需要确认员工端是否有此接口）

## TODO 4 — 患者列表分页 + UI 优化
- PatientView 加 el-pagination
- 重新设计界面布局

## TODO 5 — 就诊记录分页 + UI 优化
- VisitView 加 el-pagination
- 重新设计界面布局
