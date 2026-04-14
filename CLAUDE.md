# CLAUDE.md - XReport 报表设计器

> ⭐ **Claude Code 启动时自动读取此文件**
> 每次 Claude Code 运行命令时，会从当前目录向上查找 CLAUDE.md
> 这是 OpenClawHarness 数字员工的"灵魂手册"

---

## 项目概述

XReport 是一个**拖拽式可视化报表设计器**，技术栈：
- React 18 + TypeScript + Vite
- Chart.js（图表渲染）
- jsPDF + xlsx（导出）
- localStorage（持久化）

---

## 编码原则（⭐ 必读，4大通病解决方案）

### 1. Think Before Coding（编码前思考）
- [ ] 我理解需求吗？不确定就问，别猜
- [ ] 有多个方案？我选哪个？
- [ ] 这是最简单的实现吗？

### 2. Simplicity First（简单优先）
- 不做 speculative coding（不为"可能用到"写代码）
- 不为单一使用场景写抽象

### 3. Surgical Changes（精准手术刀）
- 只改必须改的，不"顺手"优化相邻代码
- 匹配现有代码风格

### 4. Goal-Driven Execution（目标驱动）
- 定义成功标准再动手
- 完成时验证标准

### 自问检查清单（每次任务前在脑子里过一遍，不需要停下来问用户）：
1. 需求理解正确吗？
2. 最简单的方案是什么？
3. 改动范围多大？
4. 怎么验证成功？
5. 我遵守了 RULES.md 吗？

---

## 项目结构

```
src/
├── components/
│   ├── designer/        # 报表设计器（拖拽画布）
│   ├── renderer/         # 报表渲染器（预览）
│   ├── exporter/         # 导出器（PDF/Excel/CSV）
│   └── home/             # 报表列表
├── hooks/                # React hooks（useHistory, useReportList）
├── types/                # TypeScript 类型定义
├── App.tsx               # 主应用
├── App.css               # 全局样式
└── main.tsx             # 入口
```

---

## 开发工作流（必须遵循）

### 1. 任务开始
```bash
# 读取 harness-context.md（当前项目上下文）
# 读取 RULES.md（编码规则）
# 读取 CLAUDE.md（本文件，编码原则）
```

### 2. 任务执行
- 必须通过 `npm run build` 验证编译通过
- TypeScript 类型错误必须修复
- ESLint 错误必须修复

### 3. 任务完成
- [ ] 运行 `npm run build` 确认编译通过
- [ ] 验证功能符合需求
- [ ] 如果需要，调用 sync_to_harness.py 更新上下文

### 4. Git 提交
```
feat(<scope>): <subject>
feat(designer): add undo/redo for widget operations
```

---

## 技术债务 & 待办

- [ ] 单元测试（Vitest）覆盖率 > 60%
- [ ] API 数据源真实接入
- [ ] 报表模板系统
- [ ] 多人协作（v2.0）

---

## 关键文件

| 文件 | 用途 |
|------|------|
| `src/types/index.ts` | 所有 TypeScript 类型定义 |
| `src/components/designer/ReportDesigner.tsx` | 设计器核心，组件最多 |
| `src/components/renderer/ReportRenderer.tsx` | Chart.js 渲染 |
| `src/hooks/useHistory.ts` | 撤销/重做逻辑 |
| `src/hooks/useReportList.ts` | 报表列表管理 |

---

_本文件由 openclawharness-init-project.py 自动生成_
_最后更新：2026-04-14 21:43:57_
