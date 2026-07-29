# AGENTS.md

> 为参与 HiiroSakura 开发的 AI 编程助手提供项目上下文与协作约定。
> 开始工作前以当前检出的源码、资源和构建配置为准；本文件与仓库事实不一致时，应先核实并同步修正文档。

## 项目定位

HiiroSakura 是使用 Kotlin 开发的 Minecraft 客户端增强模组，同时支持 Fabric 和 NeoForge。共享业务、Compose UI、公共 Mixin 与资源集中在 `common`，加载器入口、平台事件适配、平台服务实现和加载器专属资源分别位于 `fabric` 与 `neoforge`。

项目以 IbukiGourd 作为主要前置，使用其 Compose UI、配置、事件、输入、国际化、平台服务和任务调度能力。IbukiGourd 进一步依赖 Nebula，因此 `moe.forpleuvoir.ibukigourd.*` 与 `moe.forpleuvoir.nebula.*` 中的类型通常不属于本仓库源码。遇到不确定的 API、生命周期、线程或序列化语义时，必须核对本项目锁定版本的上游源码，不凭印象补写接口。

本模组以客户端行为为边界。除非现有实现明确涉及联机协议，否则不要引入要求服务端安装 HiiroSakura 的功能。

## 技术基线

版本号的权威来源是 `gradle.properties` 与 `gradle/libs.versions.toml`。开始涉及依赖、映射或构建的工作前应重新读取，不能只依赖本表。

| 项目 | 当前配置 |
|---|---|
| HiiroSakura | `3.5.3+beta` |
| Kotlin / Java | Kotlin `2.4.0` / Java `25` |
| Minecraft | `26.1.2` |
| Fabric | Loader `0.19.2`、API `0.146.1+26.1.2`、Loom `1.16-SNAPSHOT` |
| Fabric Language Kotlin | `1.13.12+kotlin.2.4.0` |
| NeoForge | `26.1.2.22-beta`、ModDev `2.0.141`、NeoForm `26.1.2-1` |
| Kotlin for Forge | `6.3.0` |
| Mixin | `0.8.5`、MixinExtras `0.5.3` |
| IbukiGourd | `0.11.1+alpha` |
| 脚本引擎 | Apache Commons JEXL `3.6.2` |

不要在无关任务中升级依赖。涉及 Minecraft、Kotlin、加载器或 IbukiGourd 升级时，同时检查映射、Mixin 注入点、平台 API、资源元数据、依赖打包方式和两端构建。

## 工程结构

```text
├── common/
│   ├── src/main/kotlin/moe/forpleuvoir/hiirosakura/  # 共享业务与 Compose UI
│   ├── src/main/java/moe/forpleuvoir/hiirosakura/mixin/ # 共享 Mixin
│   ├── src/main/resources/                            # 共享资源、Mixin 配置、AT/AW
│   └── src/devOnly/                                   # 开发环境代码
├── fabric/                                            # Fabric 入口、适配与专属资源
├── neoforge/                                          # NeoForge 入口、适配与专属资源
├── buildSrc/src/main/groovy/                          # 多加载器约定插件
├── gradle/libs.versions.toml                          # 版本目录
├── gradle.properties                                 # 模组元数据与版本
├── settings.gradle.kts
└── build.gradle.kts
```

- 跨加载器业务默认放在 `common`。
- 真实的平台差异沿用现有 `platform` / `platform.services` 抽象，不在共享业务里散布加载器判断。
- `common` 通过可消费 configuration 向加载器模块提供 Kotlin、Java 和资源源目录。修改源集拼接前，先阅读三个模块的构建脚本及 `buildSrc` 约定插件。
- JEXL 在 Fabric 侧通过 `include`、NeoForge 侧通过 `jarJar` 打包。新增需随模组分发的依赖时，分别核对两端策略。
- 根构建脚本会生成 `hiirosakura.info.toml`；它是构建派生文件，不要手工维护。
- `modJar/`、`runs/`、各模块 `build/`、`.gradle/`、`.idea/` 与 `.kotlin/` 属于本地产物，不应提交。

## 主要源码区域

`common/src/main/kotlin/moe/forpleuvoir/hiirosakura/` 下的主要职责如下。此表只用于导航，实际修改前仍须追踪调用关系。

| 路径 | 职责 |
|---|---|
| `HiiroSakura.kt` | 公共模组常量、日志与通用初始化入口 |
| `HiiroSakuraClient.kt` | 客户端初始化与生命周期注册 |
| `HSLang.kt`、`lang/` | 项目语言键入口及各功能语言项 |
| `command/` | 客户端命令 |
| `common/` | 数据管理等跨功能共享设施 |
| `config/` | `HSConfig`、功能配置和自定义配置项 |
| `functional/chataddons/` | 聊天过滤与聊天增强 |
| `functional/customradialmenu/` | 自定义径向菜单的数据、管理和 UI |
| `functional/event/` | HiiroSakura 的事件模型、事件实例和管理功能 |
| `functional/executor/` | 通用执行器抽象 |
| `functional/gameplay/` | 滑翔、自动切换、保护和交互增强等玩法功能 |
| `functional/itemeditor/` | 物品管理、Data Component 包装和编辑界面 |
| `functional/misc/` | 匹配器及独立辅助功能 |
| `functional/renderaddons/` | 客户端渲染附加功能 |
| `functional/script/` | JEXL 引擎、脚本 API、反混淆包装与脚本管理 |
| `functional/task/` | 任务模型、执行器实现、管理和 UI |
| `input/` | 本项目的输入模拟功能；不是 IbukiGourd 输入框架的实现 |
| `platform/`、`platform.services/` | 加载器无关的服务接口与访问入口 |
| `render/` | 项目自有渲染管线与渲染类型扩展 |
| `ui/` | 主界面、图标、通用 Compose 组件和配置 wrapper |
| `util/` | Minecraft、序列化、Data Component、数学等辅助设施 |

修改功能前应按需检查：直接调用方、数据模型、配置项、codec、持久化管理器、UI、事件订阅者、平台入口、资源及相关 Mixin。不要根据目录名或单个类推测完整行为。

## 入口与平台边界

- 公共入口为 `HiiroSakura` 与 `HiiroSakuraClient`；新增初始化逻辑前先检查现有初始化列表和生命周期注册方式。
- Fabric 入口位于 `fabric/src/main/kotlin/moe/forpleuvoir/hiirosakura/`，具体声明以 `fabric.mod.json` 为准。
- NeoForge 源码存在历史包名拼写 `moe.forpeluvoir...`。不要根据公共包名猜测或顺手改名；修改入口、元数据或 ServiceLoader 注册时逐项核对实际路径。
- Fabric 的资源重载、事件桥接等加载器能力放在 Fabric 模块；NeoForge 对应实现放在 NeoForge 模块。
- 公共功能优先使用 IbukiGourd 的生命周期事件与 `Initializable` 模式，不另建平行初始化总线。
- 调整平台服务时同时检查接口、两端实现、`META-INF/services` 注册和全部消费方。

## IntelliJ IDEA MCP 与验证

执行编译、构建、代码检查或运行配置前，先检查当前环境是否提供 IntelliJ IDEA / JetBrains MCP，并枚举其实际能力，例如项目模型、Gradle 任务、编译、问题检查和运行配置。

- 不假定 MCP 的固定工具名。
- IDEA MCP 能覆盖目标时优先使用，以复用 IDE 已导入的项目模型与环境。
- 不使用 `ps`、系统进程列表或类似方式探测 IDEA 或 Gradle 导入状态。
- 只有在 IDEA MCP 未提供、明确不可用或不能覆盖目标任务时，才回退到仓库根目录的 Gradle Wrapper。
- 先执行覆盖改动范围的最小检查，再按风险扩大验证。
- 交付时说明实际采用的验证方式；回退到 Wrapper 时简述原因。

Wrapper 常用任务：

```bash
# Linux / macOS
./gradlew :common:compileKotlin
./gradlew :fabric:build
./gradlew :neoforge:build
./gradlew build
./gradlew buildAllModJar

# Windows cmd.exe
gradlew.bat :common:compileKotlin
gradlew.bat :fabric:build
gradlew.bat :neoforge:build
gradlew.bat build
gradlew.bat buildAllModJar
```

- 公共 Kotlin 改动至少验证 `common` 编译；涉及平台、资源、Mixin 或 Minecraft API 时验证相应加载器；高风险公共改动验证两端。
- `buildAllModJar` 依赖 Fabric 与 NeoForge 的 `jar`，并将重命名后的产物复制到 `modJar/<minecraft-version>/`。
- `src/devOnly` 只用于开发环境验证，不代表存在完整自动化测试覆盖。
- 编译成功不等于 Compose 交互、Mixin、序列化或游戏内行为已经验证。只报告实际执行过的检查。
- 发布任务会产生外部影响，只有用户明确要求并确认目标仓库与版本后才能执行。

## Kotlin 与 Compose

- 遵循目标文件周边的命名、格式、导入顺序、可见性和注释密度，不做无关格式化。
- 新代码放入职责匹配的现有包，避免无必要的新顶层包。
- 优先复用项目已有的 manager、扩展函数与 `Initializable` 模式，但不要为了形式一致滥用单例。
- UI 使用 IbukiGourd 提供的 JetBrains Compose、Material 3、场景宿主、平台上下文与 preset 组件，不建立另一套原生 `GuiGraphics` 页面管线。
- 业务状态由合适的上层或管理器持有；局部瞬时状态使用 `remember`；派生状态从现有状态计算。需要响应式集合时沿用相邻代码中的 Compose snapshot state 模式。
- 颜色与交互反馈从 `MaterialTheme.colorScheme` 或组件默认 colors 推导；改动交互组件时检查 selected、hovered、focused、pressed 与 disabled 状态。
- 用户可见文案使用 `HSLang`、`lang/` 下对应语言对象或 IbukiGourd 语言项，不在 Composable 中硬编码。
- Minecraft 对象和 Compose snapshot 状态的修改必须遵守现有线程与生命周期约定，不从后台线程直接更新客户端主线程状态。

## 配置、持久化与兼容性

- 配置、任务、菜单和匹配器使用项目已有的 Nebula `Codec`、`SerializeElement` 及序列化扩展；实现新格式前先查找相邻 codec。
- 新增持久化字段时考虑旧数据缺字段、非法值、未知枚举值和反序列化失败。
- 加载失败时不要把部分解析结果当作完整成功状态发布；保存失败也不能静默改变内存状态的含义。
- 改变数据格式时在交付说明中明确向后兼容性、迁移策略和不可恢复边界。
- `functional/script` 暴露给脚本的名称、参数和返回值属于用户接口；修改前搜索全部注册点、调用点和示例。
- `functional/itemeditor` 的 Data Component wrapper 需要同时考虑注册、codec、默认值、编辑 UI 与预绑定链路。

## Mixin 与 Minecraft 钩子

- 修改注入点前核对当前 Minecraft 映射中的类、方法签名、注入顺序、局部变量和取消语义。
- 注入范围应尽可能窄，避免接管原版不相关状态维护。
- 同时检查功能消费者、两端 Mixin 配置、加载条件和平台专属 Mixin。
- 修改访问权限需求时一并检查 Fabric class tweaker 与 NeoForge access transformer。
- 不在文档中写死 Mixin 数量；以当前配置文件和源码为准。
- 交付时说明注入目的、是否取消原版逻辑，以及未进行的游戏内验证。

## 资源与国际化

- 共享资源放在 `common/src/main/resources`，加载器专属元数据放在对应模块。
- 新增或修改语言键时，除非明确要求，**只修改中文**（`zh_cn.json`），不碰其他语言文件。
- 已适配 Data Component 的功能（如 itemeditor）的翻译可独立放在 `assets/hiirosakura_item_editor/lang/` 命名空间下，与主模组解耦。
- 语言键沿用已有命名空间，并登记到项目现有语言对象；只删除经源码搜索确认不再使用的键。
- `processResources` 处理的模组元数据、Mixin 配置和 `pack.mcmeta` 中继续使用现有 `${...}` 占位符，不写死构建版本。
- 新增或移动资源时同时核对代码引用、资源路径大小写和两端打包结果。
- 事件类型的 i18n `.comment` 条目必须包含：触发时机描述、上下文类型(标注是否可取消)及全部属性的 `名称: 类型 - 说明`，使用 `\n` 换行。详细属性嵌套子属性时继续缩进。参考 `zh_cn.json` 中 `hiirosakura.event.type.sound_play.comment` 的格式。
  `var` 属性需标注 `(可修改)`，`val` 属性不标注。

## Git 与工作区

- 以当前检出的分支为工作分支。不要预设 `dev`、`main`、`multiloader` 或其他固定分支，也不要未经用户要求自行切换或新建分支。
- 只有用户明确要求时才执行 commit、push、创建 PR 或发布。
- 提交信息风格以当前仓库历史为准，不在本文中假定固定语言或前缀。
- 保留用户已有修改，不回退、覆盖或格式化不相关文件。
- 修改前先检查工作区状态；若目标文件已有用户改动，先理解并在其基础上工作。
- 不提交构建产物、运行目录、IDE 配置、缓存、日志或本地玩家数据。

## 工作原则

1. **仓库事实优先**：架构、行为和长期约定从当前源码、构建文件、资源、版本历史及匹配版本的上游资料归纳。历史对话只用于理解当前请求，不得被整理成仓库规则。
2. **先读后写**：修改前阅读目标文件、直接调用方、模型、配置、序列化与相邻实现，不能只根据文件名推测行为。
3. **公共实现优先**：跨加载器逻辑放在 `common`，真实平台差异通过现有服务接口实现。
4. **复用项目能力**：优先使用 IbukiGourd、Nebula 和本项目已有的 UI、事件、配置、codec 与工具组件。
5. **保持改动聚焦**：不顺带升级依赖、重构无关模块或清理无关代码。
6. **保护现有工作区**：保留用户已有修改，不回退、覆盖或格式化不相关文件。
7. **外部 API 必须核实**：依赖类型不在仓库内时读取本项目锁定版本的源码或官方资料，不凭记忆假设签名。
8. **文档随事实更新**：项目结构或技术基线变化时同步更新本文件，不添加只服务单次任务的设计决定。
9. **验证报告准确**：区分静态检查、编译、构建和游戏内验证，不声称未执行的检查已通过。
10. **外部操作需授权**：Git 写操作、发布及其他会改变远程状态的动作必须在用户明确要求的范围内执行。

## 交付说明

完成修改后应说明：

1. 改变了什么行为以及涉及哪些模块。
2. 实际使用的检查或构建方式及结果。
3. 未验证的运行时场景与剩余风险。
4. 配置格式、脚本 API、Mixin 或加载器差异的兼容性影响（如涉及）。
