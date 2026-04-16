# XReport 文档

> XReport - 拖拽式在线报表平台

## 文档目录

### 基础文档
- [1.项目介绍](./1.项目介绍.md) - 产品简介、核心特性、技术选型
- [2.常见问题](./2.常见问题.md) - FAQ
- [基础/技术架构](./基础/技术架构.md) - 系统架构、模块设计、安全设计
- [基础/部署指南](./基础/部署指南.md) - 环境要求、Docker 部署、Nginx 配置

### 功能模块
- [系统管理/系统管理](./功能模块/系统管理/系统管理.md) - 用户、角色、部门、菜单管理、JWT认证
- [报表/报表设计器](./功能模块/报表/报表设计器.md) - 拖拽式报表设计、图表、导出
- [报表/数据源管理](./功能模块/报表/数据源管理.md) - 数据库连接、多数据源
- [报表/数据集管理](./功能模块/报表/数据集管理.md) - SQL 查询、参数绑定

## 项目状态

| Phase | 内容 | 状态 |
|-------|------|------|
| Phase 0 | 项目骨架、技术架构 | ✅ 完成 |
| Phase 1 | 系统管理（用户/角色/部门/菜单 + JWT） | ✅ 完成 |
| Phase 2 | 报表设计器（Luckysheet 集成） | ✅ 完成 |
| Phase 3 | 数据源管理（JDBC 多数据库） | 🚧 开发中 |
| Phase 4 | 报表执行与导出 | 📋 规划中 |
| Phase 5 | 图表系统 | 📋 规划中 |
| Phase 6 | 多人协作（规划中） | 📋 规划中 |

## 技术栈

**前端：** React 18 + TypeScript + Vite + Ant Design 5 + Luckysheet + Chart.js

**后端：** Spring Boot 3.2.6 + JDK 17 + MyBatis-Plus + Spring Security + JWT

**数据库：** MySQL 5.7+ / PostgreSQL 12+

## 源码

- GitHub：https://github.com/yanyuxiyangzk/xreport
- 项目路径：`D:\project\aicoding\XReport`

## License

MIT License
