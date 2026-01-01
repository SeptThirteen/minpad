# MinPad - Git 分支管理策略

## 分支结构

MinPad 项目采用以下分支管理策略：

### 1. **main 分支** (正式版发布分支)
- **用途**：仅用于正式版本的发布
- **特性**：
  - 只包含经过充分测试和验证的代码
  - 代表生产环境就绪的版本
  - 每个 commit 都对应一个发布版本
- **操作**：
  - 不在此分支上直接开发
  - 仅通过从 develop 分支的合并请求（merge）来更新
  - 每次发布时打上版本标签（tag）

### 2. **develop 分支** (开发分支)
- **用途**：日常开发工作在这个分支进行
- **特性**：
  - 包含最新的开发代码
  - 可能包含未完成或正在测试的功能
  - 作为 feature 分支的基础
- **操作**：
  - 日常开发、新功能添加都在这个分支上工作
  - 定期从 develop 合并到 main（发布新版本时）

## 工作流程

### 日常开发流程

```bash
# 1. 确保在 develop 分支上
git checkout develop

# 2. 更新最新代码
git pull origin develop

# 3. 创建功能分支（可选，对于大功能推荐）
git checkout -b feature/功能名称

# 4. 进行开发工作
# ... 修改代码 ...

# 5. 提交更改
git add .
git commit -m "描述你的改动"

# 6. 推送到远程
git push origin feature/功能名称

# 7. 完成后，合并回 develop
git checkout develop
git merge feature/功能名称
git push origin develop

# 8. 删除功能分支（可选）
git branch -d feature/功能名称
```

### 发布新版本流程

```bash
# 1. 确保 develop 分支已推送所有更改
git checkout develop
git push origin develop

# 2. 切换到 main 分支
git checkout main

# 3. 合并 develop 分支的内容
git merge develop

# 4. 打上版本标签
git tag -a v1.2.0 -m "Release version 1.2.0"

# 5. 推送到远程
git push origin main
git push origin v1.2.0

# 6. 生成发布版本（编译 JAR 等）
mvn clean package
```

## 当前状态

```
分支情况：
  main    - 正式版发布分支（当前最新：v1.1.0）
  develop - 开发分支（活跃，包含最新开发代码）
  
当前工作分支：develop
```

## 命令速查

### 查看分支
```bash
git branch              # 查看本地分支
git branch -a           # 查看所有分支（包括远程）
git branch -v           # 查看分支及其最后一次提交
```

### 切换分支
```bash
git checkout develop    # 切换到 develop 分支
git checkout main       # 切换到 main 分支
```

### 创建分支
```bash
git branch feature/新功能      # 创建功能分支
git checkout -b feature/新功能  # 创建并切换到新分支
```

### 合并分支
```bash
git merge develop       # 合并 develop 分支到当前分支
git merge --no-ff develop  # 创建合并提交（推荐）
```

### 删除分支
```bash
git branch -d feature/功能名称     # 删除本地分支
git push origin --delete feature/功能名称  # 删除远程分支
```

### 标签操作
```bash
git tag                 # 查看所有标签
git tag -a v1.1.0 -m "Release 1.1.0"  # 创建带说明的标签
git push origin v1.1.0  # 推送标签
```

## 最佳实践

1. **经常合并**：定期将 develop 分支的更改合并到 main
2. **保持 main 稳定**：只在确认所有测试通过后才合并到 main
3. **使用功能分支**：对于大功能，创建独立的功能分支开发
4. **清晰的提交信息**：使用有意义的 commit 信息
5. **打标签**：每次发布新版本都打上版本标签

## 版本管理

### 版本号格式：v主版本.次版本.修订版本

```
v1.0.0 - 初始发布版本
v1.1.0 - 添加配置持久化功能
v1.2.0 - （未来的版本）
```

### 发布检查清单

- [ ] 所有功能已完成并测试
- [ ] 文档已更新
- [ ] 编译成功，无错误
- [ ] 测试通过
- [ ] 更新 README.md 中的版本号
- [ ] 更新 pom.xml 中的版本号
- [ ] 创建发布标签
- [ ] 生成 JAR 文件

## 分支历史示意

```
main       ✓ v1.1.0 (当前发布版本)
           |
           |--- develop (开发分支，包含最新代码)
                  |
                  |--- feature/xxx (可选的功能分支)
```

## 常见问题

### Q: 如何获取最新的开发代码？
A: 
```bash
git checkout develop
git pull origin develop
```

### Q: 我在 develop 分支做了一些实验性改动，想回到原始状态
A:
```bash
git checkout develop
git reset --hard origin/develop
```

### Q: 我需要修复一个 bug，应该在哪个分支上做？
A: 如果 bug 在生产版本中存在，创建 `bugfix/xxx` 分支从 main 分支出来；如果只在开发版本中存在，在 develop 分支上修复。

### Q: 如何将某个功能分支的改动同步到 develop？
A:
```bash
git checkout develop
git merge feature/功能名称
git push origin develop
```

---

## 总结

**开发工作流程简化版：**
1. 每天工作前：`git checkout develop && git pull origin develop`
2. 进行开发和修改
3. 完成后：`git add . && git commit -m "描述" && git push origin develop`
4. 版本发布时：从 develop 合并到 main，并打标签

这样可以确保开发工作和正式版本分离，main 分支始终保持稳定状态！
