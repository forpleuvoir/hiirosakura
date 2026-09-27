# HiiroSakura 26.2 升级整理

> 状态：**进行中**（P0 / P0.5 / P1 / P2 机械层 / 图标层已落地；调用点语义层待做）
> 事实来源为两个仓库当前检出源码与本地 Maven 制品，逐条标注依据；标「待确认」的项在动手前必须复核。
> 上游基准：IbukiGourd `1.0.0-alpha`（[`26.2`] 分支，MC 26.2）。升级方式、构建写法一律照 IbukiGourd 的做法。

## 进度

| 阶段 | 状态 | 落地内容 |
|---|---|---|
| P0 构建底座 | ✅ | `libs.versions.toml`（MC 26.2 / IG `-26.2:1.0.0-alpha` / Loom `1.17.19` / Fabric API `0.157.0+26.2` / Loader `0.19.3` / ModMenu `20.0.1` / ModDev `2.0.143` / NeoForm `26.2-2` / NeoForge `26.2.0.59`，新增 nebula `0.4.0`、compose-minecraft `0.1.0`）、`settings.gradle.kts` foojay `1.0.0`、`common` 补 `compileOnlyApi(nebula)` + `compileOnly(composeMinecraft.common)`、版本号 `4.0.0+alpha` |
| P0.5 Loom/根脚本 | ✅ | `fabric` 的 runs DSL 迁移（`displayName`/`generateRunConfig`/`runDirectory`/`programArguments`/`sourceSet`）、删 `mixin { defaultRefmapName }`（refmap 字段按上游保留）、根脚本聚合发布任务名修正（`publishModPublicationTo…`）、`modJar/<mc>/<version>`、publishing 本地仓库改 `mavenLocal()`、删除死文件 `pack.meta` |
| P1 机械替换 | ✅ | 124 文件：`ui.preset`/`ui.toast`/`ui.util.toComposeColor`/`ui.icon.Icons` 等包迁移与改名；IG 图标扩展属性 import 清理与用法改名；nebula `serialization.yml`→`yaml` |
| P2 material3 机械层 | ✅ | 97 文件：material3 组件 import → sokitsu 对应物（含 47 个星号导入的按需展开）；`MaterialTheme.colorScheme` → `SokitsuTheme.colorScheme`；`Card`/`ElevatedCard` → `Surface`（18 处）；`HorizontalDivider`/`VerticalDivider` 同名迁移；`MaterialTheme.typography` 6 档 → `SokitsuTheme.typography` 4 档（29 处）；tooltip 族 `plainTooltip{}`→`tooltip{}`（32 文件） |
| P2c 输入区包装层 | ✅ | 重写 `ui/widget/OutlinedLabelBox.kt`（20 文件的枢纽）为「标签在框上方 + sokitsu `Surface`」；新增 `LabeledFieldDefaults` 顶替 material3 的 `OutlinedTextFieldDefaults.contentPadding`（17 文件重指向）；清理 `TextFieldColors` / `TextFieldLabelPosition` 的声明与实参（29 文件 85 行）+ 14 处悬挂转发 |
| P6a Selector 层 | ✅ | 新增 `ui/widget/SelectorCompat.kt`（旧签名 `StringSelector` / `EnumSelector` 复刻在新版 `ui.selector.Selector` 上）；9 文件导入重指；12 处直调旧泛型 `Selector` 的包装改写 |
| P6b 文本框层 | ✅ | 新增 `ui/widget/ValueTextField.kt`；30 处 `OutlinedTextField` → sokitsu `TextField`（22 文件） |
| P6c 杂项组件 | ✅ | `ui/widget/SegmentedButtonCompat.kt`（16 处调用点不动）；`AssistChip`(16) → `FlatButton`；`FloatingActionButton`(8) / `OutlinedButton`(2) → `Button`；`ButtonDefaults.textButtonColors`(3) → `FlatButtonDefaults.colors`；`MaterialTheme.shapes`(16) → `RectangleShape`；`Checkbox`(1) → `Icons.Checked/Unchecked`；`KeybindAssistChip`(3) → `ui.keybind.KeybindSetButton`、`ColorAssistChip`(6) → `ui.colorpicker.ColorPickButton` |
| P3 屏幕/导航、P5 配置 wrapper、P6 其余 | ⏳ | 见下方「剩余手工量」 |

## 0. 结论速览

| 项 | 结论 |
|---|---|
| 升级性质 | **破坏性**：IG 从 `0.11.1+alpha` 到 `1.0.0-alpha` 属 UI 渲染栈整体重写（自研 `compose-minecraft` 取代 Compose Desktop/Skia/Material3），同时 MC 26.1.2 → 26.2 |
| 上游自身改动量 | IG 两个分支间 `310 新增 / 109 删除 / 68 修改 / 14 改名`（`git diff --name-status -M 26.1.2 26.2`） |
| 本项目受影响面 | 212 个文件 import `moe.forpleuvoir.ibukigourd.*`；其中 143 个 import `...ui.*`；97 个文件直接用 `androidx.compose.material3`（该库在新体系中**整体不存在**） |
| 代码体量 | `common/fabric/neoforge` 共 295 个 `.kt` + 25 个 `.java`；`ui/` 122 文件 14910 行，`functional/` 107 文件 16350 行 |
| 最贵的三块 | ① material3 → sokitsu 组件替换（97 文件）② 38 个图标名中 28 个是自建 `ImageVector`，须重制为 `.aseprite` 素材 ③ 7 个自定义配置 wrapper 依赖的上游编辑浮层 API 已删除且无公共扩展点 |
| 最便宜的部分 | 文本 DSL 与 i18n **几乎不改**（见 §3.5）；`ToastHandler.showContent/show`、`ui.util.{Keyed,values,rememberKeyedList,copyValue}`、6 个 configwrapper 符号仅需改 import |
| 前置条件 | 新制品只发布了 mavenLocal（§1.3）；`maven.forpleuvoir.moe` 在当前环境不可达 |

## 1. 事实基线

### 1.1 版本与源码位置

| 角色 | 位置 | 版本 / MC |
|---|---|---|
| 被升级方 | `hiirosakura`（本仓，分支 `multiloader`） | `3.6.0+alpha` / MC `26.1.2` |
| 旧上游基线 | `../ibuki_gourd` 本地分支 **`26.1.2`**（`7f2da700`） | `0.11.1+alpha` / MC `26.1.2` |
| 新上游 | `../ibuki_gourd` 分支 **`26.2`** | `1.0.0-alpha` / MC `26.2` |
| 新 UI 运行时 | `../Compose-Minecraft` | `0.1.0` |

- **旧签名一律取 `git -C ../ibuki_gourd show 26.1.2:<path>`**。`../ibuki_gourd_old` 是 `0.10.4+beta`（`gui/` 架构），早于 `ui/` 一代，用它做 diff 会产生大量假阳性。
- 新上游说明：`../ibuki_gourd/doc/manual/`（12 章开发者手册）、`CHANGE_LOG.md` 的 `v1.0.0-alpha` 条目、`AGENTS.md`。
- 新运行时说明：`../Compose-Minecraft/PLATFORM_MIGRATION_GUIDE.md`、`docs/compose-upgrade-guide.md`。

### 1.2 本项目工作区状态

`git status` 显示 242 个文件 modified，但 **`git diff -w` 为空**（`git diff --numstat` 每项增删行数相等）→ 全是 **CRLF 行尾噪声，无内容改动**。不要把这些当作待提交改动，也不要在升级中顺手「修复」行尾。

### 1.3 依赖制品可用性（已实测）

本地 Maven（`C:\Users\forpl\.m2\repository\moe\forpleuvoir\`，即 `mavenLocal()`）已发布：

| 制品 | 版本 |
|---|---|
| `ibukigourd-common-26.2` / `-fabric-26.2` / `-neoforge-26.2` | `1.0.0-alpha` |
| `compose_minecraft-common-26.2` / `-fabric-26.2` / `-neoforge-26.2` | `0.1.0` |
| `nebula` / `nebula-config` / `nebula-serialization` / `nebula-event` | `0.4.0` |
| `aseprite` / `reorderable` | `1.0.0-alpha`（IG 加载器构件的传递依赖） |

- `ibukigourd-fabric-26.2:1.0.0-alpha` 的 POM 依赖：`nebula 0.4.0`、`aseprite`、`reorderable`、`compose_minecraft-fabric-26.2 0.1.0`、`kotlin-stdlib 2.4.0`、`fabric-loader 0.19.3`、`fabric-api 0.157.0+26.2`、`fabric-language-kotlin`、`modmenu 20.0.1`。
- `ibukigourd-common-26.2` 的 POM **只声明 `nebula`**：`compose-minecraft` 在上游 `common` 是 `compileOnly`，**不会传递**给本项目（§2.2）。
- 网络：本环境 `https://maven.forpleuvoir.moe/` 不可达（`curl` 返回 `000`），`repo.maven.apache.org` 可达（`200`）→ 升级期间的解析依赖只能靠 `mavenLocal()` 或本地缓存。

### 1.4 MC 26.2 侧的关键 API 变化（影响本项目）

- `Minecraft.setScreen(Screen)` 与公开字段 `Minecraft.screen` **在 26.2 已移除**；屏幕状态移到 `net.minecraft.client.gui.Gui`：`Gui.screen()` / `Gui.setScreen(...)`，`Minecraft` 侧入口为 `setScreenAndShow(Screen)`。（依据：`../vanilla/vanilla-26.2-2-merged/net/minecraft/client/gui/Gui.java:218,222`、`.../Minecraft.java:2184`，对比 `vanilla-26.1.2-1-sources/.../Minecraft.java:365,1126`）
- 本项目对 `setScreen` / `.screen` 的**直接引用为 0 处**（屏幕全部经 IG 的 `ui.openComposeScreen` / `ui.closeScreen` 间接完成），所以这一条主要由 IG 消化；风险转移到 IG 提供的屏幕入口签名（§3.3）。
- 其余 MC API 增量（渲染、Data Component、Mixin 注入点等）未逐项盘点，**待编译暴露**。

## 2. 依赖与构建（P0，可独立验证）

### 2.1 版本差异

| 项 | 现值 | 目标值 |
|---|---|---|
| `minecraft` | `26.1.2` | `26.2` |
| `minecraftRange` | `[26.1,26.2)` | `[26.2,26.3)` |
| `fabricApi` | `0.146.1+26.1.2` | `0.157.0+26.2` |
| `fabricLoader` | `0.19.2` | `0.19.3` |
| `fabricLoom` | `1.16-SNAPSHOT` | `1.17.19`（固定版） |
| `modMenu` | `18.0.0-beta.1` | `20.0.1` |
| `neoforgedModDev` | `2.0.141` | `2.0.143` |
| `neoForm` | `26.1.2-1` | `26.2-2` |
| `neoforge` | `26.1.2.22-beta` | `26.2.0.59` |
| `ibukigourd` 坐标 | `ibukigourd-*-26.1.2:0.11.1+alpha` | `ibukigourd-*-26.2:1.0.0-alpha` |
| `nebula` | 未声明（靠 IG 传递 `0.3.18`） | 随 IG 传递为 `0.4.0`（**含一个包改名，见 §3.5**）；建议显式声明（§2.2） |
| `composeMinecraft` | 未声明 | `0.1.0`（`common` 侧 `compileOnly`，§2.2） |
| `kotlin` / `java` / `kotlinx*` / `fabricKotlin` / `forgeKotlin` / `mixin` / `mixinExtras` | — | **不变** |
| `jexl` `3.6.2` + `logging` + `bundles.jexl` | — | 本项目独有，**保留** |
| `foojay-resolver-convention` | `0.8.0`（`settings.gradle.kts`） | `1.0.0`（对齐 IG） |
| mod 版本号 | `3.6.0+alpha` | **待定**（§5 决策点 1） |

### 2.2 依赖声明方式

上游手册 `doc/manual/01-接入与依赖.md` 的接入口径：

- 两个加载器都用**普通 `implementation`**（Loom 1.17 起无 remap，`modImplementation` 已不存在）。本项目 `fabric/build.gradle.kts:24-35` 现状已经是这个写法，**只需换版本**。
- `nebula` / `compose-minecraft` / `reorderable` / `aseprite` 已被上游加载器构件内嵌（Fabric `include`、NeoForge `jarJar`），**消费方不需要再声明**。
  - 上游已把 `aseprite`（`.ase` 解析）与 `reorderable`（`sh.calvin.reorderable`，拖拽排序）两个独立模块**并入 `common` 源码**（`ibuki_gourd` 仓库的 `common/src/main/kotlin/{moe/forpleuvoir/ibukigourd/asetools,sh/calvin/reorderable}/`，测试并入 `common/src/test/`）。因此这两者不再出现在对外 POM，也**不再需要消费方声明或排除**：本项目直接 `import sh.calvin.reorderable.*` 的 17 个文件随 `ibukigourd-common` / `ibukigourd-fabric` 的类一起编译。
  - 注意：`mavenLocal` 里旧的 `1.0.0-alpha` 制品仍是改动前的 POM（带 `moe.forpleuvoir.ibukigourd:aseprite|reorderable` 两个解析不到的坐标），需在上游重新 `publishModToLocalRepository` 后本项目才能解析。
- 例外：上游 `common` 对 `compose-minecraft` 用 `compileOnly`（不传递），而本项目 `common` 有 1570 处 `androidx.compose.*` 引用 → **`common` 必须自行 `compileOnly(libs.composeMinecraft.common)`**；`nebula` 同理建议 `compileOnlyApi(libs.nebula)`（353 处引用）。
- `fabric` / `neoforge` 源码 0 处 compose 引用 → 不需要额外声明。
- 本项目 jexl 的 `include`（Fabric）/ `jarJar`（NeoForge）在新构建下**仍然可行、写法不变**（IG 在同版本 Loom/ModDev 下对 `nebula` 用同款）。
- 元数据：本项目 `fabric.mod.json` / `neoforge.mods.toml` 的版本字段全部是 `${...}` 占位符，改版本表即生效；`custom.ibukigourd.package` **早已声明**，无需新增。
  - 是否追加 `compose_minecraft` 的运行时 `depends`：CMP 指南建议声明（`PLATFORM_MIGRATION_GUIDE.md` §2.2/2.3），但 IG 构件已内嵌该 mod 的类 —— **待确认**，倾向「不重复嵌入、仅声明依赖」。

### 2.3 构建脚本迁移项

| 文件 | 改动 |
|---|---|
| `fabric/build.gradle.kts:46-73` | Loom runs API 迁移：删 `mixin { defaultRefmapName.set(...) }`；`configName`→`displayName`；`ideConfigGenerated(true)`→`generateRunConfig = true`；`runDir("runs/client")`→`runDirectory.set(File("runs/client"))`；`programArgs(...)`→`programArguments.addAll(...)`；`source(sourceSets["devOnly"])`→`sourceSet = "devOnly"`。保留 `accessWidenerPath` 与 `loaderAttribute` 列表 |
| ↑ 实测佐证 | 换成 Loom `1.17.19` 后跑一次 Gradle 配置期，控制台逐条报出上述 API 的 deprecation（`fabric/build.gradle.kts:59,60,63,64,68,69,70`），并额外给出：**`The mixin annotation is no longer enabled by default, you should remove any loom.mixin configuration. If you wish to continue to use the mixin AP you can set useLegacyMixinAp = true.`** → 删 `mixin {}` 块之外，还要决定 refmap 怎么办（见 §6） |
| 根 `build.gradle.kts` | `buildAllModJar` 输出目录对齐上游为 `modJar/<mcVersion>/<version>`；**聚合发布任务名当前是坏的**（`dependsOn(":common:publishMavenJavaPublicationTo…")` 与实际 publication 名 `mod` 不匹配），照上游改为 `publishModPublicationTo…` |
| `buildSrc/.../multiloader-common.gradle` | 保留 `ibukigourd_version` expand 属性（`fabric.mod.json` / `neoforge.mods.toml` 依赖它）；可选对齐：Modrinth 改 `exclusiveContent`、publishing 本地仓库改 `mavenLocal()` |
| `common/src/main/resources/pack.meta` | 文件名拼错且内容不是合法 JSON、也不被 `filesMatching` 匹配 → **死文件，建议删除**（上游无对应文件） |
| 编译参数 | 与上游逐字相同（`-jvm-default=enable` / `-Xcollection-literals` / `-Xexplicit-context-arguments`），**无需补任何参数**。注意：上游多处文档写的 `-Xcontext-parameters` 在构建脚本里并不存在，实际开关就是 `-Xexplicit-context-arguments` |
| Mixin 元数据 | `compatibilityLevel`（common `JAVA_18` / 两端 `JAVA_21`）、`minVersion 0.8`、`defaultRequire 1`、`classTweaker v1 official`、AT CFG 格式均与上游一致，**格式无需改**；条目本身是否仍有效待编译暴露 |

## 3. 破坏面盘点

### 3.1 上游已整体删除的东西

`ui.preset/**`、`ui.icon/**`（含 `defaults` / `filled`）、`ui.platformcontext/**`、`ui.skia/**`、`ui.toast/**`（迁包）、`mod.ui/**`（抽屉式 `ModScreen`）、`androidx.compose.material3`（连同 MaterialKolor / Skia / Compose Desktop 离屏渲染）。

### 3.2 组件替换与屏幕（工作量最大）

| 旧 | 新 | 性质 | 项目内文件数 |
|---|---|---|---|
| `androidx.compose.material3.*` | `ui/sokitsu/*` + `ui/sokitsu/theme/*` | 删除/替换 | **97**（93 main + 4 devOnly） |
| `MaterialTheme.colorScheme.*` | `SokitsuTheme.colorScheme` / `LocalColorScheme` | 改名（槽位 1:1 为主，`tertiary`/`secondaryContainer`/`surfaceContainer*`/`outlineVariant` 无对应） | 44 |
| `MaterialTheme.typography.*`（6 档） | `SokitsuTheme.typography.{title,subtitle,body,button}`（4 档） | 收缩 | — |
| `MaterialTheme.shapes.*` | 无（圆角由组件 meta + 九宫格素材决定） | 删除 | 20 处 |
| `IbukiGourdTheme` | `SokitsuTheme` | 改名+签名变 | 9 |
| `IGCompositionLocalProvider` | 无（场景根已注入平台 locals） | 删除 | 7 |
| `MinecraftClipboard` | `LocalClipboard.current`（CMP 已接通） | 删除+替代 | 3 |
| `LocalSkiaSurface` / `SkiaSurface.postRender` | CMP `MinecraftRenderPlugin` + `MinecraftRenderPlugins.register` | 删除+替代 | 4 |
| `ui.openComposeScreen(...)` | `SokitsuScreen.open(...)` / `create(...)` | 签名变（`renderParent`→`renderParentScreen`；`shouldRenderLevel`→`disableWorldRender` **语义取反**） | 7 |
| `ui.openComposePopupScreen` / `ui.closeScreen` / `Screen.open()` | 无 → `FlexibleDialog`/`AlertDialog`、`mc.gui.setScreen(parent)` | 删除 | 2 / 4 / 1 |
| `mod.ui.ModScreen` + `DrawerItem`/`DrawerHeader`/`LocalDrawerItemSelected` | `ui/ModScreen.kt` 的 `ModScreen(tabs=…, state=…)` + `ModScreenTab`/`ModScreenState` | **重设计**：抽屉 → 页签，抽屉概念无替代 | 1（但 `ui/HiiroSakuraScreen.kt` 是最大单点改动） |
| `ui.preset.Text` | `ui/sokitsu/Text`（3 重载，样式参数改为 `style: Style`） | **必须改调用点**（旧的 `fontWeight`/`textAlign`/… 全删） | 59 |
| `ui.preset.modifier.plainTooltip` / `tooltip` | `ui/sokitsu/tooltip/Tooltip.kt` 的 `Modifier.tooltip` / `basicTooltip` | 改名+签名变 | 30 / 2 |
| `PlainTooltip` / `fadeScaleTooltip` / `TipBox` | 无 | 删除 | 2 / 2 / 5 |
| `ui.preset.Selector` | `ui/selector/Selector.kt`（单选/多选） | **必须改**：Material3 参数全删，`searchFilter` 的 lambda 参数顺序反转 | 5 |
| `EnumSelector` / `StringSelector` | 无（用泛型 `Selector<T>`） | 删除 | 6 / 3 |
| `Int/Long/Float/DoubleField` | `ui/sokitsu/NumberField.kt` | 必须改：`range`→`valueRange`、`textStyle` 由 `TextStyle`→`Style` | 12 |
| `NumberFieldStyle` / `LocalNumberFieldStyle` | 无 | 删除 | 3 / 3 |
| `ItemIcon` / `ItemIconVanilla` / `LocalItemIconVanillaSize` | `ui/item/ItemIcon.kt` + `ItemIconDefaults.size` | 签名变（第 1 参 `item`→`stack`，删 `imageSize`/`countModifier`/`countAlignment`） | 5 / 1 / 1 |
| `DragHandle` | `ui/editdialog/DragHandle.kt` | **接收者+语义全变**（列表项拖拽手柄 → 通用拖拽按钮） | 6 |
| `RemoveButton` / `RemoveConfirmButton` | `ui/editdialog/RemoveConfirmButton.kt` | 必须改（前两参顺序对调 / `action`→`onConfirm`） | 6 |
| `rememberFabVisibilityByScroll` | `rememberFabScrollVisibility` + `Modifier.fabScrollVisibility` | 改名+返回类型拆分 | 10 |
| `rememberKeyedList` | 同名，返回 `KeyedListState<T>` | **签名变**：无 `set` 运算符 / `values()` / `add(Keyed)` | 17 |
| `ui.util.{Keyed,values,copyValue}` | 同包同签名 | **仅 import** | 26 / 19 / 12 |
| `ui.toast.ToastHandler.showContent/show` | `ui/sokitsu/toast/ToastHandler.kt` | **逐参一致，仅 import** | 12 |
| `ui.util.toComposeColor` / `toNebulaColor` | `moe.forpleuvoir.ibukigourd.util`（`ColorConvert.kt`） | 换包（属性→函数） | 8 / 1 |
| `render/extension/**`（`IGTexture`/`TextureInfo`/`Corner`/`push*`） | 同包同签名 | **保留**（仅 `AnchorPosition` 由内嵌 `value class` 变独立 `enum class`，常量名不变） | 8 |
| 组件级缺口（无 1:1 对应） | `AssistChip`、`NavigationRail(+Item)`、`SegmentedButton(+Defaults)`、`ElevatedCard`、`OutlinedTextFieldDefaults`、`ButtonDefaults` 等 | 需挑选替身或自绘 | 待逐文件确认 |

### 3.3 配置界面包装器

| 旧 | 新 | 性质 | 文件数 |
|---|---|---|---|
| `uiWrapper { }` / `UIWrappers` / `ConfigRowWrapper` / `ConfigManagerWrapper` / `ConfigUIWrapper` / `asState` / `ConfigsWrapper` | 同名（部分换文件） | 保留（`ConfigRowWrapper.entrySize` 删除、`spacing` 变 `@Composable get()`） | 1–7 |
| `ListConfigWrapperDefaults.{RowWrapper,EditDialog}` | `ConfigListWrapper(config, modifier, contentColumnWidth)` | 删除+替代 | 3 |
| `MapConfigWrapperDefaults.{RowWrapper,EditDialog}` + `MapEntry` | `ConfigMapWrapper(config, modifier, keyColumnWidth, valueColumnWidth)` | 删除+替代 | 4 / 5 |
| `StringListConfigWrapper` | 无 | 删除 | 1 |
| `StringPairListConfigWrapper` | `PairListConfigWrapper` | 改名 | 1 |

- **风险最高的点**：本项目有 **7 个自定义配置 wrapper**（`ItemStackMatcher` / `BlockInfoMatcher` / `AutoReplant.Entry` / `ChainDoorsRule` / `SoundEvent` / `BlockInfoItemStackPair` / `ChatBubbleServerConfig`）。它们不是自己画整行整浮层，而是**复用上游浮层骨架 + 注入自己的元素控件**：旧版 `MapConfigWrapperDefaults.EditDialog` / `ListConfigWrapperDefaults.EditDialog` 的 `content` 参数可直接拿到 `SnapshotStateList<Keyed<MapEntry<K,V>>>` 自绘表格列（本项目就是这么接 `BlockInfoMatcherDisplayerInnerEditor` 等编辑器的）。
- **新旧扩展点对照（重要，别误判为"上游没给扩展接口"）**：
  - **仍在**（行级，且 KDoc 明写是对外扩展点）：`UIWrappers.register(谓词/类型, strict, wrapper)`、`UIWrappers.registerCheckValueType(值类型, strict, wrapper)`、单节点 `configNode.uiWrapper { }`（`ui/configwrapper/ConfigUIWrapper.kt`）；行骨架 `ConfigRowWrapper`、状态 `asState/asDerivedState/asDefaultState`；浮层积木 `ui/editdialog/{EditDialogContent,EditDialogContentList,DragHandle,RemoveConfirmButton}` 与 `FlexibleDialog` 也都是公开的。
  - **没了**：列表 / 映射**内建浮层里的元素控件注入点**。新 `ConfigMapWrapper(config, modifier, keyColumnWidth, valueColumnWidth)` / `ConfigListWrapper(config, modifier, contentColumnWidth)` **没有 content 参数**，值列硬编码走 `internal fun ConfigElementEditor`（按运行时类型 `when` 分支，未列出类型回落 `Text(toString())`）；`ConfigListEditDialog` 也是 `internal`；`MapEntry` 被换成内建的 `Pair<String, Any>`。
  - 结论：7 个 wrapper **不是"无处可挂"**，而是**不能再借上游的浮层**——要么用公开积木（`FlexibleDialog` + `EditDialogContent(ContentList)` + `rememberKeyedList`）自建一份共同的列表/映射编辑浮层，7 个 wrapper 各自接入既有 InnerEditor；要么在上游把元素编辑器做成注册表（见 §5 决策点 4）。工作量量级：一份公共浮层 + 7 处接线，而非 7 份从零设计。

### 3.4 图标与素材

> **已定方案（P1 期间核实后调整）**：**不需要重制 `.aseprite` 素材**。compose-minecraft 已完整移植 `androidx.compose.ui.graphics.vector`（`ImageVector` / `VectorPainter` / `rememberVectorPainter` / `PathParser`），也提供 `Image(painter, …)` 与 `ColorFilter.tint(…)`。因此自有图标继续用现有 `ImageVector` 源码渲染，只补一个薄组件即可。

- 已新增 `ui/icon/HSIcons.kt`（自有矢量图标宿主对象）与 `ui/icon/VectorIcon.kt`（`VectorIcon(image, contentDescription, modifier, tint)`，内部 `rememberVectorPainter` + `Image`）。
- 自有图标从「挂在 IbukiGourd 的 `Icons` 上」改为挂在 `HSIcons` 上：**新版 `Icons` 是 sokitsu 精灵对象，成员会遮蔽同名扩展属性**（实测冲突名：`Help`），继续共用命名空间会静默取到精灵、类型不匹配。`filled` 变体改为 `HSIcons.XFilled`。
- IbukiGourd 侧的精灵（`Icons.Add`/`Icons.Edit`/… 共 38 个 `.aseprite` id）仍按名字引用，机制不变：素材放 `assets/<ns>/texture/sokitsu/<atlasId>/`，定义放 `assets/<ns>/sokitsu_atlas/`，缺失时静默渲染为空。
- 旧 `ui.icon.defaults.*` / `ui.icon.filled.*`（IG 的 Material Symbols 扩展属性）整体删除，用到的名字按上表改用精灵 id（`Icons.EditNote`→`Icons.Edit`、`Settings`→`Setting`、`ContentCopy`→`Copy`、`KeyboardArrowRight`→`ArrowRight`）。
- `.aseprite` 与主题 meta（`sokitsu_meta.json`）仍属可选资源工作：只在需要自定义像素倍率/配色时才做。

### 3.5 文本、i18n、事件、配置项（低风险）

以正确基线（`26.1.2` = `0.11.1+alpha`）diff，**`text/**` 与 `lang/**` 只改了 2 个文件**（`text/Texts.kt`、`text/style/StyleExtensions.kt`），其余逐字节未变：

- 删除 `MutableText.withColor/withShadowColor(androidx.compose.ui.graphics.Color)`：本项目 3 处调用均传 `Int`（走原版重载），**不受影响**。
- `Style.color` → `Style.nebulaColor`：本项目那两处 `text.style.color` 实际解析到 MC 成员 `Style.getColor()`，**影响为 0**。
- `ui.util.toComposeColor/toNebulaColor` → `moe.forpleuvoir.ibukigourd.util`：**9 个文件需改 import**（属颜色工具，非文本）。
- `IGLang` / `ThemeLang` / `lang/` 语言键：本项目 58 个文件用 `IGLang`，被引用的成员新增版全部保留；被删除的 16 个 IG 语言键本项目无引用；自建 `HSLang` + `hiirosakura` 命名空间的模式与上游约定一致 → **i18n 零改动**。
- `config` / `event` / `input` / `task` / `command` / `util` 目录在两个分支间的差异极小（`git diff --name-status -M 26.1.2 26.2 -- <这些目录>` 仅 13 条：新增 `ConfigEnum.kt`、`ColorConvert.kt`、`ColorContrast.kt`、`UnitCodec.kt`、`util/math/easing/*`；修改 `ConfigKeyBind.kt`、`input/KeyCode.kt`、`input/KeyEnvironment.kt`、`util/ClientMisc.kt`、`codec/IdentifierCodec.kt`）→ 本项目现有的 `context(ConfigGroup)` 配置项写法、事件订阅、`TickTask`、指令 DSL **基本不动**。`CHANGE_LOG.md` 的 `v1.0.0-alpha` 条目自述覆盖「自 v0.11.0 之后的全部改动」，混入了 `0.11.1` 已发布的内容，**不能用它判断增量**。

### 3.6 唯一确定的编译级破坏：nebula 包改名（必须改）

nebula `0.3.18 → 0.4.0` 把包 `moe.forpleuvoir.nebula.serialization.yml` 改名为 `...serialization.yaml`（已核验：`v0.3.18` 树里是 `yml/`，`v0.4.0` 树里是 `yaml/`）。

- 本项目失效文件 **2 个**：`common/src/main/kotlin/.../ui/syntaxhighlight/YamlSyntaxLanguage.kt:4`、`common/src/main/kotlin/.../ui/widget/FormatImportExportButton.kt:44`（均 import `serialization.yml.YamlDialect`）。
- 除该改名外，nebula 该版本区间只动 YAML 编解码器（`v0.3.18..v0.4.0` 共 9 个文件），`codec.Codec` / `SerializeElement` / `ConfigGroup` / `ConfigItem` 等**零改动**。
- 注意该依赖随 IG 传递进来，不属于「顺带升级依赖」的范畴（§2.1）。

## 4. 建议执行顺序（每步可独立验证）

1. **P0 构建底座**：改 `gradle/libs.versions.toml`（§2.1 全部）、`settings.gradle.kts` 的 foojay、`common` 补 `compileOnly(libs.composeMinecraft.common)`（+ 可选 `compileOnlyApi(libs.nebula)`）、IG 三处坐标换 `-26.2`。验证：Gradle 配置期通过 + 依赖能解析出 26.2 制品。
2. **P0.5 Loom runs API + 根脚本**（§2.3）。验证：`:fabric:tasks` / `tasks --all` 通过。
3. **P1 机械替换**：import 迁移（`ui.preset`/`ui.icon`/`ui.toast`/`ui.util`/`ui.platformcontext`/`ui.skia`/`mod.ui` 的包路径与改名项）＋ nebula `serialization.yml` → `serialization.yaml`（2 文件，§3.6）。验证：`:common:compileKotlin` 报错数下降。
4. **P2 material3 → sokitsu**（97 文件，含 typography/shapes 收敛）。
5. **P3 屏幕与导航**：`HiiroSakuraScreen` 抽屉→页签、`SokitsuScreen.open/create`、`mc.gui.setScreen`、`MinecraftRenderPlugin` 替代 Skia。
6. **P4 图标素材**：38 个名字落素材 + 本项目图集定义。
7. **P5 配置 wrapper 重写**（7 个自定义 + 2 个上游默认替换）。
8. **P6 MC 26.2 侧**：编译暴露的 MC API / Mixin 注入点 / AT-AW 条目修正。
9. **P7 两端构建与游戏内验证**：`:fabric:build`、`:neoforge:build`、`buildAllModJar` → `modJar/26.2/…`，再进游戏跑 `common/src/devOnly` 的测试屏。

验证方式优先按 `AGENTS.md`：先查 IDEA MCP 能力，不可用再回退 `gradlew.bat`。

## 5. 需要先拍板的决策点

1. **mod 版本号**：破坏性升级，`3.6.0+alpha` → ？（上游同类升级是 `0.11.1+alpha` → `1.0.0-alpha`）。
2. **图标方案**：28 个自建图标是重制为 `.aseprite`（与 sokitsu 一致、但要画素材），还是改用 CMP 自绘（省素材、但脱离组件体系）。
3. **抽屉导航**：`HiiroSakuraScreen` 现为抽屉式，新体系只有页签条 → 接受页签化，还是自绘保留抽屉观感。
4. **列表 / 映射元素控件怎么接**：上游的行级扩展点（`UIWrappers` / `uiWrapper`）仍在，缺的只是**内建浮层里的元素控件注入点**。两条路：(a) 本项目用公开积木自建一份公共的列表 / 映射编辑浮层，7 个 wrapper 各自接线既有 InnerEditor；(b) 在上游把 `ConfigElementEditor` 开成注册表（按值类型注册元素控件），本项目只接线不再重写。上游即你本人维护，(b) 一次投入、后续新增自定义配置类型不再重演。
5. **是否顺带上游文档修正**：`doc/manual/01-接入与依赖.md` 的示例版本仍是 `0.11.1+alpha`（应为 `1.0.0-alpha`）；多处文档写 `-Xcontext-parameters` 而构建脚本里实际是 `-Xexplicit-context-arguments`。

## 6. 待确认清单

- [ ] 是否需要在元数据追加 `compose_minecraft` 运行时依赖（IG 已内嵌其类，重复嵌入 vs 仅声明）。
- [ ] Mixin AP 默认关闭后 refmap 怎么处理：`useLegacyMixinAp = true`，还是去掉 `*.mixins.json` 里的 `refmap` 字段（上游 26.2 的 json 里仍留着该字段）。
- [ ] mavenLocal 里的 `1.0.0-alpha` 制品与 `ibuki_gourd` 仓库最新提交是否一致。IG 自己的 FAQ 就写明「构件从 `mavenLocal()` 解析 —— 未发布时仍解析到旧构件」，若编译报出的符号与仓库源码不符，先怀疑制品滞后。
- [ ] 新版 `SimpleAlertDialog` / `FlexibleDialog` / `ColorPicker` / `fabVisibilityAnimation` / `FabVisibilityState` 的完整参数尾部与枚举项（本次只读到旧签名对照部分）。
- [ ] `ComposeSceneWarmup` / `MinecraftClipboard` 是否迁入 `compose-minecraft`。
- [ ] MC 26.2 侧除屏幕 API 外的增量（渲染 / Data Component / Mixin 注入点）——待编译暴露。
- [ ] `common/src/devOnly` 的 5 个测试屏与 `fabric` 的 dev run 配置在新 Loom runs API 下的写法。

## 7. 本次整理未做的事

未修改任何源码或构建脚本（只新增本文件）；未执行任何 Gradle / IDE 构建；未做游戏内验证。所有结论均为静态源码比对（新旧 IG 源码、CMP 源码与手册、vanilla 26.1.2 与 26.2 反编译源）与本地制品核验。

---

## 8. 迁移执行记录（编译清零）

### 8.1 结果与方法

| 阶段 | `common` 编译 ERROR | 说明 |
|---|---|---|
| 初次真实编译 | 1743 | 用 IDEA MCP 的 `build_project` 取 Kotlin 编译器错误 |
| 修复过程中最低点 | 0（`common` 全量 lint 复核）/ 3（devOnly，随后修掉） | 见下 |

**关键教训**：`build_project` 的 `timeout` 参数是**毫秒**。传 `300` 会被当成 0.3 秒，工具立刻返回 `timedOut: true` 且问题列表为空；传 `600000` 才能拿到完整编译器错误（含 `file` / `line` / `column`）。若某次构建只回报
`Build reported errors, but detailed error messages were not captured through build events.`，用 `lint_files`
（IDE 分析，与编译器错误一致）分批复核即可定位。

### 8.2 新增的本项目兼容层

上游 1.0.0-alpha 删掉了旧版 UI 骨架，本项目调用点分散、逐个改写成本高，因此按旧签名重建在 `common/src/main/kotlin/moe/forpleuvoir/hiirosakura/ui/` 下：

| 文件 | 内容 |
|---|---|
| `configwrapper/ConfigWrapperCompat.kt` | `ListConfigWrapperDefaults` / `MapConfigWrapperDefaults` / `MapEntry` / `ConfigRowWrapperCompat` / `rememberKeyedStateList`（旧版 `rememberKeyedList` 返回 `SnapshotStateList<Keyed<T>>` 的行为） |
| `configwrapper/StringListConfigWrappers.kt` | `StringListConfigWrapper` / `StringPairListConfigWrapper`（聊天过滤与注入的列表编辑器） |
| `compat/LegacyUiCompat.kt` | `NumberFieldStyle` / `LocalNumberFieldStyle` / `TipBox` / `BlitTexture`（含 `IGTexture` 重载）/ `closeScreen` / `openComposeScreen` |
| `compat/MaterialLeftovers.kt` | `OutlinedToggleButton` / `FilledTonalButton` / `FloatingActionButton` / `ElevatedCard` / `OutlinedCard` / `Card` / `RemoveButton` / `IntSlider` / `OutlinedTextFieldDefaults` / `LocalAutoExpandConfigGroupLimit` / `FloatingActionButtonMenu` / `FloatingActionButtonMenuItem` / `NavigationRail(Item)` / `openComposePopupScreen` / `ClipboardManager` + `rememberClipboardManager` |
| `widget/SelectorCompat.kt` 等既有兼容件 | selector / 分段按钮 / 带标签输入框 / 裁剪板等 |

### 8.3 这一轮遇到的 MC 26.2 断链（与上游 UI 无关）

- `EntityType.<常量>` → **`EntityTypes.<常量>`**（常量移到新的持有类）。
- `GameRenderer.mainCamera` 字段变 private，改用公共方法 **`mainCamera()`**。
- `BlockPos.center` 移除 → **`Vec3.atCenterOf(pos)`**。
- `RenderPipeline.builder`：`withSampler` → `withBindGroupLayout(BindGroupLayouts.SAMPLER0)`、`withVertexFormat(format, mode)` → `withVertexBinding(0, format)` + `withPrimitiveTopology(...)`，公共 snippet 常量不再公开（本项目 `HSRenderPipeline` 自建）。
- `RenderSetup.builder(...)`：无 `bufferSize`；`RenderSetupBuilder` 其余方法保留。
- `GuiGraphicsExtractor.setTooltipForNextFrame(...)` 仍在，但取用方式改为每帧的 `MinecraftRenderPlugins.currentGraphics`（旧 `LocalSkiaSurface.postRender` 路径消失）。
- `ResourceKey.identifier` / `GridCells.Fixed.count` 等可见性问题（后者在 CMP 里是 private，改为由交叉轴尺寸列表推列数）。

### 8.4 迁移中明确降级的地方（后续要补回）

1. 富文本预览（`componentwrapper/Text.kt`、`RichTextEditor.kt`）由 Skia 逐行绘制改为组件文本渲染，逐行摆放/默认色参数不再生效。
2. 旗帜图案预览由 Skia 位图裁剪改为 `Modifier.minecraftTexture(uv = …)`，底图混色用 `background` 表达。
3. `Text` 的 `autoSize` / `textAlign` / `textDecoration` / `letterSpacing` 等旧 preset 参数在 sokitsu 组件文本重载里不存在，已丢弃。
4. 材质/`@OptIn(ExperimentalMaterial3ExpressiveApi)`、m3 形状与高度（`shape` / `tonalElevation` / `shadowElevation`）参数移除。
5. `fabVisibilityAnimation(FabScrollVisibility)` 改为 `Modifier.fabScrollVisibility(...)`；`rememberFabScrollVisibility` 的 `shouldHide` 参数取消。

### 8.5 仍未完成的验证

- **fabric / neoforge 模块**：`ReloadListenerRegistry.kt` 与 `ModMenuImpl.kt` 报的是「无法访问 `net.minecraft.*`，请检查 module classpath」，属于**依赖解析**问题而非源码错误 —— 需要先把 IG 重新发布到 `mavenLocal`（`gradlew.bat publishModToLocalRepository`），否则 fabric 模块的 classpath 解析不到 IG，连带 MC 也解析不了。
- `common/src/devOnly` 的测试屏只做了静态修正，未进游戏验证。
- 渲染管线（`HSRenderPipeline` / `HSRenderType`）按 26.2 新 API 重写，但**未做画面验证**。
- 未执行 `:fabric:build` / `:neoforge:build` / `buildAllModJar`。
