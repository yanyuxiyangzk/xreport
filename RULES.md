# 编码规则

> 本文件定义数字员工的编码规范和核心规则
> AI 在执行开发任务时必须严格遵守

---

## 1. 代码质量规则

### 1.1 必须遵循

- [ ] 所有代码必须通过 ESLint / TSC 类型检查
- [ ] 必须有单元测试覆盖核心逻辑
- [ ] 禁止提交有 `console.error` 或未捕获异常的代码
- [ ] 禁止提交硬编码的敏感信息（API Key、密码等）

### 1.2 Git 提交规范

```
<type>(<scope>): <subject>

type: feat | fix | docs | style | refactor | test | chore
scope: 模块或功能名称
subject: 简短描述（不超过 50 字）
```

---

## 2. 开发流程规则

### 2.1 任务执行

1. 读取 `harness-context.md` 获取项目上下文
2. 任务执行前确认需求理解正确
3. 任务完成后更新 `harness-context.md`
4. 记录成功/失败模式到 `corrections/` 目录

### 2.2 反馈闭环

- [ ] 任务成功后：执行 `self_improve_ops.py learn`
- [ ] 任务失败后：执行 `self_improve_ops.py reflect`，分析原因
- [ ] 每次任务后：执行 `sync_to_harness.py --mode incremental`

---

## 3. 安全规则

### 3.1 禁止行为

- ❌ 禁止将代码发送到外部服务（除必需的 API 调用）
- ❌ 禁止在代码中硬编码凭据
- ❌ 禁止跳过安全扫描
- ❌ 禁止强制覆盖主分支

---

## 4. 架构约束

### 4.1 模块化

- [ ] 每个模块职责单一
- [ ] 模块间通过接口通信，禁止直接引用内部实现
- [ ] 公共 API 必须有文档注释

### 4.2 错误处理

- [ ] 所有异步操作必须 try-catch
- [ ] 错误必须记录日志（包含上下文）
- [ ] 用户可见的错误必须有友好提示

---

## 5. Karpathy 编码原则（来自 Andrej Karpathy）

> 解决 LLM 编码四大通病：错误假设、管理混乱、过度复杂、副作用修改

### 5.1 Think Before Coding（编码前思考）

- **不确定就问，别猜** — If uncertain, ask rather than guess
- **遇到歧义呈现多个选项** — Don't pick silently when ambiguity exists
- **该push back就push back** — If a simpler approach exists, say so

### 5.2 Simplicity First（简单优先）

- **不做 speculative coding** — 不为"可能用到"写代码
- **不为单一使用场景写抽象** — No abstractions for single-use code

### 5.3 Surgical Changes（精准手术刀）

- **只改必须改的** — Don't "improve" adjacent code
- **不"顺手"优化相邻代码** — Don't refactor things that aren't broken
- **匹配现有风格** — Match existing style, even if you'd do it differently

---

_本文件由 sync_to_harness.py 自动生成_