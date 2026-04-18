# OpenClawHarness 规则执行检查报告

**项目**: XReport
**检查时间**: 2026-04-18 08:15:08
**整体结果**: WARN

## 检查点详情

| 检查点 | 状态 | 说明 |
|--------|------|------|
| CP1 初始化 | ✅ PASS | harness-context.md 存在 (8853 bytes) |
| CP2 两层分离 | ✅ PASS | 两层分离检查通过，未发现绕过 harness_run 的记录 |
| CP3 learn实质 | ✅ PASS | 全部 14 个 corrections 内容实质（failure 已清理） |
| CP4 sync同步 | ⚠️ WARN | sync 上次运行: 19.9小时前，建议检查 |
| CP5 图谱更新 | ✅ PASS | 图谱已更新（2天前） |
| CP6 单元测试 | ✅ PASS | Java测试:28文件/280方法 (覆盖率26%), 前端测试:5文件 (覆盖率18%), Python测试:0文件/0函数 (覆盖率0%), Vitest:无 pytest:无 |

## 违规汇总

**⚠️ 警告（1项）- 建议检查**：
  - [CP4] sync同步: sync 上次运行: 19.9小时前，建议检查