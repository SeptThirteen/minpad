# MinPad - 数字键盘快捷操作工具

一个基于Java原生组件开发的Windows快捷键工具，通过数字键盘快速执行常用操作。

## 功能特性

- ✅ **全局键盘监听**：监听数字键盘（NumPad）按键
- ✅ **系统托盘运行**：最小化到系统托盘，不占用任务栏
- ✅ **可自定义配置**：通过GUI界面配置每个按键的操作
- ✅ **执行任意程序**：可以启动任何Windows程序或命令
- ✅ **轻量级**：使用Java Swing原生组件，无需额外依赖

## 默认快捷键

| 按键 | 功能 |
|------|------|
| NumPad 0 | 显示测试通知 |
| NumPad 1 | 打开记事本 |
| NumPad 2 | 打开计算器 |
| NumPad 3 | 打开浏览器 |
| NumPad 4 | 打开资源管理器 |
| NumPad 5 | 打开命令提示符 |
| NumPad 6-9 | 可自定义 |
| NumPad + | 增加音量（默认）/ 可自定义 |
| NumPad - | 减少音量（默认）/ 可自定义 |
| NumPad * / | 可自定义 |
| NumPad Enter | 可自定义 |

## 系统要求

- Windows 7/8/10/11
- Java 11 或更高版本
- Maven 3.6+ (用于构建)

## 构建与运行

### 1. 构建项目

```bash
mvn clean package
```

### 2. 运行程序

```bash
java -jar target/minpad-1.0.0.jar
```

或者直接双击生成的 JAR 文件。

### 3. 设置开机自启动（可选）

1. 按 `Win + R` 打开运行对话框
2. 输入 `shell:startup` 并回车
3. 将 `minpad-1.0.0.jar` 的快捷方式复制到打开的文件夹中

## 使用说明

### 配置快捷键

1. 右键点击系统托盘图标
2. 选择"设置快捷键"
3. 点击要配置的按键旁边的"编辑"按钮
4. 填写以下信息：
   - **名称**：操作的描述性名称
   - **命令**：要执行的程序路径（如 `notepad.exe`、`C:\Program Files\xxx\app.exe`）
   - **参数**：可选，传递给程序的参数
5. 对于 NumPad + 和 NumPad -，可以选择"重置"按钮恢复默认的音量控制功能

### 示例配置

#### 打开指定网站
- 名称：打开百度
- 命令：`explorer.exe`
- 参数：`https://www.baidu.com`

#### 打开指定文件夹
- 名称：打开工作目录
- 命令：`explorer.exe`
- 参数：`C:\Users\YourName\Documents`

#### 运行批处理脚本
- 名称：备份数据
- 命令：`C:\scripts\backup.bat`
- 参数：留空

## 技术栈

- **Java 11+**：核心开发语言
- **Swing**：GUI界面
- **JNativeHook**：全局键盘钩子库
- **Maven**：项目构建工具

## 项目结构

```
minpad/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── minpad/
│                   ├── Main.java                 # 主入口
│                   ├── NumPadListener.java       # 键盘监听器
│                   ├── ActionExecutor.java       # 动作执行器
│                   ├── SystemTrayManager.java    # 系统托盘管理
│                   └── SettingsDialog.java       # 设置对话框
├── pom.xml                                       # Maven配置
└── README.md                                     # 项目说明
```

## 常见问题

### Q: 程序无法启动？
A: 请确保已安装 Java 11 或更高版本。可以在命令行运行 `java -version` 检查。

### Q: 无法监听键盘？
A: Windows可能需要管理员权限才能监听全局键盘。请尝试以管理员身份运行。

### Q: 如何卸载？
A: 直接删除 JAR 文件即可。如果设置了开机自启动，记得删除启动文件夹中的快捷方式。

### Q: 支持组合键吗？
A: 当前版本只支持单个数字键盘按键。后续版本可能会添加组合键支持（如 Ctrl + NumPad 1）。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

如有问题或建议，欢迎通过 GitHub Issues 反馈。
