# 🌳 MinPad 分支管理 - 快速参考

## 当前分支结构

```
main (v1.1.0) ← 正式版发布分支
  ↑
  └─ develop ← 你的开发分支 (当前活跃)
```

## 日常工作流程（3 步）

### 1️⃣ 开始工作
```bash
git checkout develop
git pull origin develop
```

### 2️⃣ 提交代码
```bash
git add .
git commit -m "功能描述"
git push origin develop
```

### 3️⃣ 版本发布（稳定时）
```bash
git checkout main
git merge develop
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin main --tags
```

## 常用命令

| 命令 | 说明 |
|------|------|
| `git branch -a` | 查看所有分支 |
| `git status` | 查看当前状态 |
| `git log --oneline -5` | 查看最新 5 个提交 |
| `git checkout develop` | 切换到开发分支 |
| `git checkout main` | 切换到发布分支 |

## 规则

- ✅ **develop**：日常开发工作
- ✅ **main**：仅发布稳定版本
- ✅ 频繁提交，保持分支整洁
- ✅ 发布前确保所有测试通过

## 查看完整文档

见 [BRANCH_STRATEGY.md](BRANCH_STRATEGY.md)
