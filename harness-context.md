# Harness Context - XReport

> ⭐ **AI 必读上下文** - 本文件是数字员工的记忆中枢
>
> **重要提示**：每次任务执行前，必须先完整阅读本文件！

---

## 项目信息

| 属性 | 值 |
|------|-----|
| 项目名称 | XReport |
| 项目路径 | `D:\project\aicoding\XReport` |
| 项目类型 | Vue3 + SpringBoot (Maven多模块) |
| 技术栈 | Vue3 + Vite + Luckysheet + ElementPlus / SpringBoot + MyBatis-Plus + Druid |
| 最后同步时间 | 2026-04-15 18:45:00 |
| 同步模式 | full |

---

## 项目概述

**XReport** 是一个拖拽式可视化报表设计器，支持：
- 拖拽式报表设计（Luckysheet）
- JDBC多数据源 + API数据源
- 数据集管理 + SQL查询聚合计算
- 报表预览导出（Excel/PDF/Word）
- 仪表盘 + 权限系统

---

## 技术架构

### 前端（frontend/）
```
frontend/src/
├── api/               # API 请求模块
├── router/           # Vue Router 配置
├── views/
│   ├── report/       # 报表设计器（Designer.vue）、列表（List.vue）、预览（Preview.vue）
│   ├── dashboard/     # 仪表盘
│   └── share/        # 分享功能
├── App.vue + main.js
└── src/assets
```

### 后端（backend/）- Maven 多模块
```
backend/
├── xreport-web/          # Controller 层
│   └── src/main/java/com/xreport/
│       ├── config/       # JwtAuthenticationFilter, RestTemplateConfig, SecurityConfig
│       ├── controller/
│       │   ├── auth/         # AuthController
│       │   ├── dataset/      # DatasetController
│       │   ├── datasource/   # DatasourceController
│       │   ├── report/       # ReportTplController, ReportTplSheetController, ReportRenderController, LuckysheetController
│       │   └── system/       # SysUserController, SysRoleController, SysDeptController, SysMenuController
│       └── exception/    # GlobalExceptionHandler
├── xreport-service/       # Service 层
│   └── src/main/java/com/xreport/service/
│       ├── auth/          # IAuthService + Impl
│       ├── dataset/        # IDatasetService, IDatasetQueryEngine + Impl
│       └── report/        # IReportTplService, IReportDatasourceService, IReportRenderService + Impl
├── xreport-mapper/        # Mapper 层（MyBatis-Plus）
│   └── src/main/java/com/xreport/mapper/
│       ├── SysUserMapper, SysRoleMapper, SysDeptMapper, SysMenuMapper, SysUserRoleMapper, SysRoleMenuMapper
│       └── report/        # ReportTplMapper, ReportTplSheetMapper, ReportDatasetMapper, ReportDatasourceMapper, LuckysheetReportCellMapper
├── xreport-pojo/          # POJO / DTO
│   └── src/main/java/com/xreport/pojo/
│       ├── dto/           # DatasourceDTO, ApiDatasourceDTO, DatasetDTO, AggregationRequest, ReportDatasetDto, ReportTplDto...
│       ├── entity/         # (实体类)
│       └── vo/            # (返回值对象)
├── xreport-common/        # 公共模块
├── src/                   # (公共资源)
├── docs/                  # 任务文档（PHASE_X_task.md, PHASE_X_STATUS.md）
├── sql/                   # 数据库脚本
├── scripts/               # 辅助脚本
└── docker/                # Docker 配置
```

---

## 开发阶段

| Phase | 内容 | 状态 | 完成时间 |
|-------|------|------|----------|
| Phase 0 | 项目初始化（Maven多模块，SpringBoot 3 + JDK17） | ✅ | 2026-04-14 |
| Phase 1 | 系统管理（用户/角色/部门/菜单 CRUD + JWT） | ✅ | 2026-04-14 |
| Phase 2 | 报表模板管理（Luckysheet 编辑 + CRUD） | ✅ | 2026-04-15 |
| Phase 3 | 数据源管理（JDBC多数据库 + API数据源） | ✅ | 2026-04-15 |
| Phase 4 | 数据集管理（SQL查询 + 聚合计算引擎） | ✅ | 2026-04-15 |
| Phase 5 | 报表预览导出（Excel/PDF/Word） | ✅ | 2026-04-15 |
| Phase 6 | Word模板生成（poi-tl） | ✅ | 2026-04-15 |
| Phase 7 | 仪表盘（Echarts） | ✅ | 2026-04-15 |
| Phase 8 | 权限系统 + 分享功能 | ✅ | 2026-04-15 |

---

## API 接口一览

### 认证
- `POST /api/auth/login` - 登录

### 系统管理
- `GET/POST/PUT/DELETE /api/system/users` - 用户管理
- `GET/POST/PUT/DELETE /api/system/roles` - 角色管理
- `GET/POST/PUT/DELETE /api/system/depts` - 部门管理
- `GET/POST/PUT/DELETE /api/system/menus` - 菜单管理

### 报表模板
- `GET/POST/PUT/DELETE /api/report/tpls` - 报表模板 CRUD
- `GET/PUT /api/report/tpls/{id}/sheets` - Sheet 管理
- `POST /api/report/tpls/{id}/save` - 保存 Luckysheet 内容
- `POST /api/report/render` - 渲染报表

### 数据源
- `GET/POST/PUT/DELETE /api/datasources` - 数据源 CRUD
- `POST /api/datasources/test/jdbc` - 测试 JDBC 连接
- `POST /api/datasources/test/api` - 测试 API 连接
- `POST /api/datasources/query/jdbc` - 执行 JDBC SQL 查询
- `POST /api/datasources/query/api` - 执行 API 查询

### 数据集
- `GET/POST/PUT/DELETE /api/datasets` - 数据集 CRUD
- `POST /api/datasets/aggregate` - 聚合计算

### 导出
- `POST /api/export/excel` - 导出 Excel
- `POST /api/export/pdf` - 导出 PDF
- `POST /api/export/word` - 导出 Word

---

## 知识图谱摘要

图谱位置：`graphify-out/graph.json`
图谱统计：355 节点，20 边

**关键组件**：
- `ReportTplController` → 报表模板 CRUD
- `ReportDatasourceService` → 数据源服务（JDBC + API）
- `DatasetQueryEngine` → SQL 聚合计算引擎
- `LuckysheetController` → Luckysheet 保存/渲染

---

## 编码规则（RULES.md 摘要）

- UTF-8 编码（所有文件）
- Spring Boot 3 + JDK 17
- MyBatis-Plus 走 spring-boot3-starter
- JUnit 5 测试
- BCrypt 密码加密（Spring Security Crypto）
- JWT 认证（jjwt）
- Druid 连接池

---

## 任务历史

| 时间 | 任务 | 状态 | 持续 |
|------|------|------|------|
| 2026-04-14 | Phase 0-1 项目初始化 + 系统管理 | ✅ success | - |
| 2026-04-15 13:36 | Phase 2 报表模板管理 | ✅ success | 873s |
| 2026-04-15 13:50 | Phase 3 数据源管理 | ✅ success | 287s |
| 2026-04-15 13:55 | Phase 4 数据集管理 | ✅ success | 378s |
| 2026-04-15 14:01 | Phase 5 报表预览导出 | ✅ success | 404s |
| 2026-04-15 14:08 | Phase 6 Word模板生成 | ✅ success | 588s |
| 2026-04-15 14:18 | Phase 7 仪表盘 | ✅ success | 472s |
| 2026-04-15 14:26 | Phase 8 权限系统 + 分享 | ✅ success | 469s |

---

## 待开发 / 待完善

- [ ] 前端 Build 验证（`npm run build`）
- [ ] 单元测试覆盖率 > 60%（Vitest）
- [ ] 前后端联调（API 对接）
- [ ] Docker 部署
- [ ] 多数据源同时查询优化

---

## OpenClawHarness 执行状态（2026-04-15）

| 检查点 | 状态 | 说明 |
|--------|------|------|
| CP1 初始化 | ✅ PASS | harness-context.md 存在 |
| CP2 两层分离 | ✅ PASS | 未发现绕过 harness_run 的记录 |
| CP3 learn实质 | ✅ PASS | corrections 内容实质 |
| CP4 sync同步 | ⚠️ WARN | 无 .last_sync 标记 |
| CP5 图谱更新 | ✅ PASS | 图谱已生成（355节点） |
| CP6 单元测试 | ✅ PASS | 有测试提及 |

---

_本文件由妙妙于 2026-04-15 18:45 手动更新_
_更新内容：同步 XReport Phase 3-8 真实项目结构、API 接口、任务历史_


## 反馈统计

| 类型 | 数量 |
|------|------|
| 成功 | 11 |
| 失败 | 0 |
| 笔记 | 0 |
| 规则 | 0 |

**成功率**: 100.0%

### 最近反馈

- [success] 2026-04-15_phase-3_success.md (2026-04-15 18:20)
- [success] 2026-04-15_14-34-22_3e6e634a.md (2026-04-15 18:20)
- [success] 2026-04-15_14-26-28_7c88830f.md (2026-04-15 18:20)
- [success] 2026-04-15_14-18-31_e2e94336.md (2026-04-15 18:20)
- [success] 2026-04-15_14-08-38_9ac4e982.md (2026-04-15 18:20)
