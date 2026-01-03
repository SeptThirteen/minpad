# MinPad - 数字键盘快捷操作工具

一个基于Java原生组件开发的Windows快捷键工具，通过数字键盘快速执行常用操作。

## ✨ 功能特性

- ✅ **全局键盘监听**：监听数字键盘（NumPad）按键，完全拦截防止其他应用接收
- ✅ **物理键盘布局界面**：可视化的数字键盘编辑界面，直观配置快捷键
- ✅ **系统托盘运行**：最小化到系统托盘，不占用任务栏
- ✅ **可自定义配置**：通过GUI界面配置每个按键的操作
- ✅ **执行任意程序**：可以启动任何Windows程序或命令
- ✅ **组合键映射**：支持模拟组合键如 `ctrl+shift+a`、`alt+tab` 等
- ✅ **长按连续触发**：NumPad +/- 长按时连续调节音量，松开立即停止
- ✅ **音量控制**：内置 NumPad +/- 快速调节系统音量
- ✅ **媒体控制**：NumPad Enter 默认实现播放/暂停功能
- ✅ **小数点支持**：NumPad . 也可自定义配置
- ✅ **单实例运行**：防止同时运行多个实例，重复启动会提示
- ✅ **配置持久化**：自动保存所有设置，下次启动自动加载
- ✅ **中文支持**：完整的中文界面和菜单
- ✅ **跨平台打包**：支持 JAR、EXE 等多种格式

## 📦 快速开始

### 方案 1：Windows 可执行程序（推荐）

**无需安装 Java，开箱即用！**

1. [下载最新 Release](https://github.com/SeptThirteen/minpad/releases)
2. 下载 `MinPad-portable.zip` 
3. 解压后双击 `MinPad\MinPad.exe` 即可运行

### 方案 2：JAR 文件

需要 Java 11 或更高版本：

```bash
java -jar minpad.jar
```

## 🎮 数字键盘布局

```
    /    |  *  | -
 7  | 8  |  9  | +
 4  | 5  |  6  |
 1  | 2  |  3  | Enter
    0   | .   |
```

| 按键 | 默认功能 | 可自定义 |
|------|--------|--------|
| NumPad 0 | 未配置 | ✅ |
| NumPad 1-9 | 各种应用启动 | ✅ |
| NumPad + | **增加音量** | ✅ |
| NumPad - | **减少音量** | ✅ |
| NumPad * | 未配置 | ✅ |
| NumPad / | 未配置 | ✅ |
| NumPad Enter | **播放/暂停** | ✅ |
| NumPad . | 未配置 | ✅ |

## 🖥️ 系统要求

| 方案 | 要求 |
|-----|------|
| **EXE 可执行程序** | Windows 7/8/10/11（内嵌 JRE） |
| **JAR 文件** | Windows + Java 11+ |
| **源代码编译** | JDK 17+ + Maven 3.6+ |

## 🚀 安装与运行

### 使用 EXE（推荐）

1. 下载 `MinPad-portable.zip`
2. 解压到任意目录
3. 双击 `MinPad/MinPad.exe` 运行

### 使用 JAR

```bash
# 前提：已安装 Java 11+
java -jar minpad.jar
```

### 开机自启动（可选）

Windows 10/11：
1. 按 `Win + R` → 输入 `shell:startup` → 回车
2. 创建 `MinPad.exe` 或 `minpad.jar` 的快捷方式到此文件夹

## 📖 使用说明

### 配置快捷键

1. **右键点击托盘图标** → 选择"设置快捷键..."
2. **查看物理键盘布局**：界面展示真实的数字键盘排列
3. **双击按键编辑**：
   - 未配置的按键显示灰色
   - 已配置的按键显示深蓝色
   - 悬停查看完整配置信息

4. **填写配置信息**（至少填一项）：
   - **名称**：操作的描述（必填）
   - **命令**：程序路径（可选，如 `notepad.exe`）
   - **参数**：程序参数（可选）
   - **组合键**：键盘组合（可选，如 `ctrl+s`）

#### 支持的组合键格式

```
修饰键: ctrl/control, shift, alt, win/windows
字母: a-z
数字: 0-9
特殊键: space, enter, tab, esc, backspace, delete, insert
方向键: left, right, up, down
符号: comma(,), period(.), semicolon(;), slash(/), backslash(\)
函数键: f1-f12
分隔符: 使用 + 连接

示例: ctrl+s, shift+f5, alt+tab, ctrl+shift+comma
```

### 配置持久化

- **位置**：`C:\Users\[用户名]\.minpad\config.json`
- **自动保存**：编辑快捷键时立即保存
- **自动加载**：应用启动时自动加载配置

### 配置示例

#### 打开网站
- 名称：打开百度
- 命令：`explorer.exe`
- 参数：`https://www.baidu.com`

#### 快速复制
- 名称：快速复制
- 组合键：`ctrl+c`

#### 打开文件夹
- 名称：打开文档
- 命令：`explorer.exe`
- 参数：`C:\Users\[用户名]\Documents`

## 🛠️ 项目构建

### 使用 Maven 构建 JAR

```bash
mvn clean package -DskipTests
# 输出: target/minpad.jar
```

### 使用 jpackage 生成 EXE（需要 JDK 17+）

```bash
# 1. 先构建 JAR
mvn clean package -DskipTests

# 2. 生成 Windows 可执行程序
jpackage --input target `
  --name MinPad `
  --main-jar minpad.jar `
  --main-class com.minpad.Main `
  --type app-image `
  --dest target/dist `
  --vendor SeptThirteen `
  --app-version 1.1.2

# 输出: target/dist/MinPad/MinPad.exe
```

### 使用 GitHub Actions 自动构建

本项目配置了 GitHub Actions 工作流：

- **build-jar**：每次推送自动构建 JAR
- **build-exe**：每次推送自动构建 Windows EXE  
- **release**：推送版本标签时自动发布到 Release

## 📦 打包和发布

详见 [PACKAGING_GUIDE.md](PACKAGING_GUIDE.md) 了解更多打包选项和指南。

## 🔧 技术栈

- **Java 17+**：核心开发语言（支持 jpackage）
- **Swing**：原生 GUI 界面，支持完整中文
- **GridBagLayout**：物理键盘布局渲染
- **JNativeHook 2.2.2**：全局键盘钩子监听库
- **JNA 5.14.0 + jna-platform**：Windows API 调用
- **Gson 2.10.1**：JSON 配置管理
- **Maven Shade**：Uber JAR 打包
- **jpackage**：Windows 可执行程序生成

## 📁 项目结构

```
minpad/
├── src/main/java/com/minpad/
│   ├── Main.java                      # 主入口 + 单实例锁
│   ├── KeyboardHook.java              # 低级键盘钩子（核心）
│   ├── NumPadListener.java            # 高级键盘监听器（备用）
│   ├── ActionExecutor.java            # 快捷操作执行器
│   ├── AudioVolumeController.java     # 音量和媒体控制
│   ├── KeyCombinationController.java  # 组合键模拟器
│   ├── ConfigManager.java             # JSON 配置管理
│   ├── SystemTrayManager.java         # 系统托盘
│   ├── SettingsDialog.java            # 可视化设置界面（物理键盘布局）
│   ├── SingleInstanceLock.java        # 单实例锁机制
│   └── IconFactory.java               # 图标生成
├── .github/workflows/
│   └── build.yml                      # GitHub Actions 自动构建
├── pom.xml                            # Maven 配置（Shade + jpackage）
├── PACKAGING_GUIDE.md                 # 打包指南
└── README.md                          # 项目文档
```

## ❓ 常见问题

**Q: 我没有安装 Java，能运行 EXE 版本吗？**  
A: 可以！EXE 版本包含了内置的 Java 运行时环境，完全不需要预先安装 Java。直接下载 .exe 文件，第一次运行时会自动安装到本地。

**Q: JAR 版本需要什么 Java 版本？**  
A: JAR 版本需要 Java 11 或更高版本。可以从 [adoptium.net](https://adoptium.net/) 或 [oracle.com](https://www.oracle.com/java/technologies/downloads/) 下载。

**Q: 启动时提示"应用程序已在运行"是什么意思？**  
A: 这是单实例锁机制在工作。应用只允许同时运行一个实例，防止冲突和重复执行快捷命令。如果看到这个提示但没看到应用界面，可能是窗口最小化到了系统托盘。

**Q: 数字键盘 NumLock 需要打开吗？**  
A: 不需要。应用会全局监听 NumPad 按键事件，无论 NumLock 状态如何都能工作。

**Q: 无法监听键盘或权限不足？**  
A: Windows 系统可能需要管理员权限才能使用全局键盘钩子。请尝试以管理员身份运行程序。

**Q: 如何自定义 NumPad 按键的功能？**  
A: 右键点击系统托盘中的 minpad 图标，选择"设置快捷键..."，会打开可视化设置界面。点击任意按钮可编辑其功能。设置后立即生效，无需重启。

**Q: 修改配置后需要重启应用吗？**  
A: 不需要！配置保存后会立即生效。

**Q: 支持组合键吗？**  
A: ✅ **完全支持！** 可以在配置中填写 `ctrl+shift+a`、`alt+tab`、`shift+f5` 等组合键。

**Q: 可以同时使用命令和组合键吗？**  
A: 不可以。程序会优先执行组合键，若同时设置了两者，命令会被忽略。建议只配置其中一项。

**Q: 配置文件保存在哪里？**  
A: 配置文件保存在用户目录：`%USERPROFILE%\.minpad\config.json`（即 `C:\Users\[用户名]\.minpad\config.json`）。可以手动编辑此文件进行高级配置。

**Q: 如何重置为默认配置？**  
A: 在设置对话框中，点击特定按键的"重置"按钮可恢复其默认功能。要完全重置所有配置，可以删除 `~/.minpad/config.json` 文件，重启应用即可。

**Q: 可以手动编辑配置文件吗？**  
A: 可以，配置文件是标准的 JSON 格式，支持手动编辑。但建议通过应用的图形界面修改，避免格式错误。手动修改后需要重启应用才能生效。

**Q: 配置在什么时候保存？**  
A: 配置在两个时机自动保存：
- **编辑时立即保存**：点击设置对话框的"确定"按钮后，修改立即保存到文件
- **应用关闭时保存**：应用正常退出时再次保存一遍

**Q: 能否录制键盘宏（长按序列）？**  
A: 暂不支持宏录制。目前只能为每个 NumPad 按键设置单一操作。

**Q: NumPad 长按是否支持连续触发？**  
A: 支持！NumPad +/- 长按时会每 120ms 连续调节音量，松开按键立即停止。

**Q: 怎么完全卸载应用？**  
A: 
- **EXE 版本**：使用"控制面板 > 程序和功能"找到 minpad 卸载即可
- **JAR 版本**：删除 jar 文件和 `~/.minpad/` 目录即可

**Q: 支持其他操作系统吗？**  
A: 当前只支持 Windows（7/8/10/11）。因为使用了 Windows API 和 JNA-Platform 进行键盘拦截。

**Q: 应用占用多少系统资源？**  
A: 应用内存占用约 200-300MB（包含嵌入式 JRE），CPU 占用很低（大部分时间 <1%）。

## 📝 更新日志

### v1.1.2 (当前版本) - 2024-01-XX
**核心重点：可视化设置界面 + 单实例保护 + 小数点支持**

- ✨ **物理键盘布局可视化**：
  - SettingsDialog 现在显示真实的 NumPad 布局（4x4 网格）
  - 按钮颜色反映配置状态（深蓝 #2962FF=已配置，灰色 #BDBDBD=未配置）
  - HTML 双行按钮显示（按键标签 + 功能名称）
  - GridBagLayout 精确模拟物理键盘尺寸（0 键占 2 列，+/Enter 占 2 行）
- 🔒 **单实例运行保护**：
  - FileLock 文件锁机制防止重复启动
  - 用户二次启动时弹出同步对话框提示
  - 应用退出时自动释放锁文件
- ➕ **NumPad 小数点支持**：
  - 新增小数点键（.）的快捷操作配置
  - 可在设置界面直接编辑小数点的功能
- 🎨 **UI 改进**：
  - 按钮文本显示优化（两行 HTML 格式，更清晰可读）
  - 按钮颜色反馈更直观，一目了然
  - 设置界面响应更快

### v1.1.1 - 2024-01-XX
**增强稳定性**

- 🔒 引入 SingleInstanceLock 机制，防止内存泄漏和冲突
- 🛠️ 提升应用稳定性和内存使用效率
- 📊 改进资源管理和性能

### v1.1.0 - 2024-01-XX  
**配置持久化**

- 💾 **配置文件自动保存**：所有自定义设置保存到 `~/.minpad/config.json`
- 🔄 **启动自动加载**：应用启动时自动加载上次保存的配置
- 📋 **配置备份**：支持手动导出/导入配置文件
- 🔧 **增强配置管理**：支持 JSON 格式，易于备份和恢复

### v1.0.0 - 2024-01-XX
**首个稳定版本**

- 🎉 **核心功能完整**：
  - ⌨️ 支持 NumPad 16 个按键全部自定义
  - 🌐 支持打开网址
  - 🚀 支持启动应用程序  
  - 🎹 支持音量和媒体控制
  - 🔗 支持模拟组合键（Shift/Ctrl/Alt/Win）
  - 🖱️ 支持光标移动和鼠标点击
- 🎨 **系统集成**：
  - 系统托盘集成，最小化不占用任务栏
  - GUI 设置界面，无需编写代码
  - 完整中文界面支持
- 🔌 **技术架构**：
  - 双层键盘监听（低级钩子 + 高级监听器）
  - Windows API 集成（JNA 调用）
  - 定时器机制支持长按连续触发

---

## 📄 许可证

本项目采用 **MIT License** 开源许可证，详见 [LICENSE](LICENSE) 文件。

您可以自由使用、修改和分发本项目代码，但需要保留原作者信息。

## 🙌 贡献指南

欢迎提交 Issue 和 Pull Request！

- **报告 Bug**：在 GitHub Issues 中详细描述问题（包括错误信息、系统版本、复现步骤）
- **功能建议**：欢迎新功能提议和改进建议
- **代码贡献**：Fork 本仓库，在 develop 分支创建新分支，完成后提交 Pull Request

## 👨‍💻 关于作者

**SeptThirteen** - Java 开发者，致力于开发实用的 Windows 工具

- 📧 联系方式：通过 GitHub Issues 反馈
- 🔗 GitHub：https://github.com/SeptThirteen
- 💬 讨论：欢迎在 Issues 中提出问题和建议

## 📧 联系与反馈

如有问题、建议或遇到 Bug，欢迎通过以下方式反馈：

1. **GitHub Issues**：在本仓库 [Issues](https://github.com/SeptThirteen/minpad/issues) 页面提交
2. **讨论区**：使用 [Discussions](https://github.com/SeptThirteen/minpad/discussions) 进行讨论
3. **Pull Requests**：欢迎提交代码改进

---

<div align="center">

**Made with ❤️ by SeptThirteen**

如果觉得这个项目有帮助，请给个 ⭐ Star！

</div>
