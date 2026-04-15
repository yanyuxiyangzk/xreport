# OpenClawHarness 规则执行检查报告

**项目**: XReport
**检查时间**: 2026-04-16 07:15:02
**整体结果**: WARN

## 检查点详情

| 检查点 | 状态 | 说明 |
|--------|------|------|
| CP1 初始化 | ✅ PASS | harness-context.md 存在 (8071 bytes) |
| CP2 两层分离 | ✅ PASS | 两层分离检查通过，未发现绕过 harness_run 的记录 |
| CP3 learn实质 | ✅ PASS | 全部 14 个 corrections 内容实质 |
| CP4 sync同步 | ⚠️ WARN | sync 上次运行: 11.8小时前，建议检查 |
| CP5 图谱更新 | ✅ PASS | 图谱已更新（0天前） |
| CP6 单元测试 | ✅ PASS | 最近3个 corrections 中 3 个提到测试 |

## 违规汇总

**⚠️ 警告（1项）- 建议检查**：
  - [CP4] sync同步: sync 上次运行: 11.8小时前，建议检查