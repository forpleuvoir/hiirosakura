# **HiiroSakura Mod**

<img src = "doc/img/logo.png" width ="256" alt="icon">

![版本](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fforpleuvoir%2Fhiirosakura%2Frefs%2Fheads%2F1.21.4%2Fgradle%2Flibs.versions.toml&query=%24.versions.modVersion&label=%E7%89%88%E6%9C%AC)

**HiiroSakura** 是一个致力于提升 Minecraft 游戏体验的多功能模组，提供玩法增强、聊天功能扩展、渲染信息优化以及灵活的配置管理界面。

## 📦 **前置模组**

<img src="https://github.com/forpleuvoir/ibuki_gourd/blob/dev/doc/logo.png?raw=true" width="16"/>[IbukiGourd](https://modrinth.com/mod/ibukigourd)

![ibukigourd](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fraw.githubusercontent.com%2Fforpleuvoir%2Fhiirosakura%2Frefs%2Fheads%2F1.21.4%2Fsrc%2Fmain%2Fresources%2Ffabric.mod.json&query=%24.depends.ibukigourd&label=ibukigourd)

模组所有功能都为默认关闭

**打开模组设置界面**：
  - 按下快捷键 **H + S**。
  - 在界面中调整模组设置，启用或禁用不同功能。

## ✨ **主要功能**

### **1.渲染修改**
- 总是渲染(屏障,光源)方块
- 禁用文本混淆效果
- 禁用侧边计分板
- 伽马值覆盖
- TNT,苦力怕引信渲染
- 手持物品额外信息渲染
- 掉落物信息渲染


### **2. 游戏玩法增强**
- 自动播种
- 物品使用拦截
- 方块破坏保护
- 自动切换胸甲鞘翅
- 自动复活
- 音效过滤
- 切换客户端相机实体

### **3. 聊天功能扩展**
- **消息过滤**
- **消息注入**
- **聊天气泡**

## **🌟 自定义任务**
- 支持发送消息,指令,以及编写`JavaScript`脚本自定义任务。
- 通过快捷键或者任务执行界面快速执行任务。

## **📌 事件订阅**
提供对多类型事件的订阅支持：
  - **玩家事件**（聊天、进入游戏、登出等）
  - **世界事件**（方块破坏等）

事件支持多种触发动作:
  - **发送消息**
  - **发送指令**
  - **执行脚本**
  - **执行自定义任务**

### 🌟 **享受更优雅的 Minecraft 游戏体验吧！**

## 鸣谢

> [IntelliJ IDEA](https://zh.wikipedia.org/zh-hans/IntelliJ_IDEA) 是一个在各个方面都最大程度地提高开发人员的生产力的
> IDE，适用于 JVM 平台语言。

特别感谢 [JetBrains](https://www.jetbrains.com)
为开源项目提供免费的 [IntelliJ IDEA](https://www.jetbrains.com/idea/?from=mirai) 等 IDE 的授权  
[<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="200"/>](https://www.jetbrains.com)