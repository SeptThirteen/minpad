# MinPad 打包和发布指南

## 📦 **打包方案总览**

### 方案 1：jar 文件（当前）✅
```powershell
mvn clean package -DskipTests
# 输出: target/minpad.jar (~5MB)
# 要求: 用户需要安装 Java 11+
# 使用: java -jar minpad.jar
```

### 方案 2：jpackage（推荐）🔥
生成 Windows .exe 可执行程序，内嵌 Java 运行环境。

**前提条件：**
- JDK 17 或更高版本（内置 jpackage）
- Windows 操作系统

**生成 .exe:**
```powershell
# 首先构建 jar
mvn clean package -DskipTests

# 然后手动运行 jpackage（选项 1：基础）
jpackage --input target `
  --name MinPad `
  --main-jar minpad.jar `
  --main-class com.minpad.Main `
  --type exe `
  --dest target/dist `
  --vendor SeptThirteen `
  --app-version 1.1.2

# 或使用 Maven 运行（选项 2：Maven 集成）
mvn clean package -Djpackage=true -DskipTests
```

**输出：**
- `target/dist/MinPad-1.1.2.exe` (~150-200MB)
- 包含完整的 JRE，用户无需安装 Java
- 可直接点击运行

### 方案 3：MSI 安装程序
```powershell
jpackage --input target `
  --name MinPad `
  --main-jar minpad.jar `
  --main-class com.minpad.Main `
  --type msi `
  --dest target/dist `
  --vendor SeptThirteen `
  --app-version 1.1.2 `
  --win-per-user-install `
  --win-menu
```

**输出：**
- `target/dist/MinPad-1.1.2.msi` (~150-200MB)
- Windows 安装程序，支持卸载
- 更专业的用户体验

### 方案 4：launch4j（轻量级）
可选的第三方工具，生成 .exe 但文件较小。

```powershell
# 需要先安装 launch4j
# 或使用 Maven 插件
```

---

## 🚀 **推荐发布流程**

### 对于普通用户：
1. **发布 .exe** 文件（方案 2）
   - 用户直接下载 .exe
   - 双击即可运行，无需配置

2. **发布 .msi** 文件（方案 3）
   - 用户双击安装
   - 支持开始菜单快捷方式、卸载等

### 对于开发者/测试：
1. **发布 .jar** 文件（当前）
   - 文件小（~5MB）
   - 需要 Java 环境

---

## 📋 **各方案对比表**

| 特性 | jar | jpackage .exe | .msi | launch4j |
|-----|-----|--------------|------|----------|
| 文件大小 | ~5MB | ~150MB | ~150MB | ~50MB |
| 安装难度 | 中等（需要 Java） | 低（开箱即用） | 低（安装程序） | 低 |
| 用户体验 | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 网络依赖 | 否 | 否 | 否 | 可选 |
| 更新方式 | 手动 | 手动 | 手动 | 手动 |
| JRE 内嵌 | 否 | 是 | 是 | 否（可选）|

---

## 🔧 **快速开始：生成 .exe**

### 前提：确保有 JDK 17+
```powershell
java -version  # 检查 Java 版本
```

### 生成可执行程序：
```powershell
# 1. 编译和打包 jar
mvn clean package -DskipTests

# 2. 生成 .exe（需要 JDK 17+）
jpackage --input target `
  --name MinPad `
  --main-jar minpad.jar `
  --main-class com.minpad.Main `
  --type exe `
  --dest target/dist `
  --vendor SeptThirteen `
  --app-version 1.1.2 `
  --win-console
```

### 输出文件：
```
target/dist/MinPad-1.1.2.exe
```

用户可以直接运行这个 .exe 文件，无需安装任何东西！

---

## 💡 **建议**

1. **首先尝试 jpackage**：官方工具，集成度最好
2. **如果需要安装体验**：使用 .msi 格式
3. **如果需要最小体积**：发布 jar + 提供 JRE 下载链接

---

## 📝 **GitHub Release 发布清单**

推荐的发布结构：
```
MinPad v1.1.2
├── MinPad-1.1.2.exe          # 单文件可执行版（推荐）
├── MinPad-1.1.2.msi          # 安装程序版
├── minpad.jar                # Java 版本（开发者用）
├── README.md                 # 说明文档
└── CHANGELOG.md              # 更新日志
```

---

## ⚙️ **故障排除**

### jpackage 找不到？
```powershell
# 检查 JDK 版本
jpackage --version

# 如果报错，说明 Java 版本太低
# 需要升级到 JDK 17+
```

### 生成失败？
```powershell
# 添加详细日志
jpackage --input target `
  --name MinPad `
  --main-jar minpad.jar `
  --main-class com.minpad.Main `
  --type exe `
  --dest target/dist `
  --verbose
```

---

更新时间：2026-01-03
