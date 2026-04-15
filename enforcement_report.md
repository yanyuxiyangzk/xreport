# OpenClawHarness 规则执行检查报告

**项目**: XReport
**检查时间**: 2026-04-15 18:15:02
**整体结果**: WARN

## 检查点详情

| 检查点 | 状态 | 说明 |
|--------|------|------|
| CP1 初始化 | ✅ PASS | harness-context.md 存在 (5703 bytes) |
| CP2 两层分离 | ✅ PASS | 两层分离检查通过，未发现绕过 harness_run 的记录 |
| CP3 learn实质 | ✅ PASS | 全部 8 个 corrections 内容实质 |
| CP4 sync同步 | ⚠️ WARN | 无法确认 sync 运行状态（无 .last_sync 标记） |
| CP5 图谱更新 | ✅ PASS | 图谱已更新（0天前） |
| CP6 单元测试 | ⚠️ WARN | 最近 corrections 未提及测试覆盖（可选，建议但不强制） |

## 违规汇总

**⚠️ 警告（2项）- 建议检查**：
  - [CP4] sync同步: 无法确认 sync 运行状态（无 .last_sync 标记）
  - [CP6] 单元测试: 最近 corrections 未提及测试覆盖（可选，建议但不强制）