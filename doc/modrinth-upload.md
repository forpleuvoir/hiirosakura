# 上传 Modrinth

构建产物通过 [Minotaur](https://github.com/modrinth/minotaur) Gradle 插件上传，不再需要手动打开网页填写表单。

## 一次性准备：令牌

1. 打开 <https://modrinth.com/settings/personal-access-tokens> 新建个人访问令牌。
2. 勾选权限 `READ_PROJECT` 与 `CREATE_VERSION`；如果还要用 `modrinthSyncBody` 同步项目简介，再勾选 `WRITE_PROJECT`。
3. 令牌以 `mrp_` 开头。二选一存放，**不要写进本仓库**：
   - 环境变量 `MODRINTH_TOKEN`（推荐，对所有项目生效）
   - 用户级 `%USERPROFILE%\.gradle\gradle.properties` 里加一行 `modrinth.token=mrp_xxx`

两者都存在时，Gradle 属性优先。

## 更新日志

仓库维护两份，内容对应同一个版本：

| 文件 | 语言 | 用途 |
|---|---|---|
| `CHANGE_LOG.md` | 中文 | 仓库与中文发布渠道 |
| `CHANGE_LOG-ENG.md` | 英文 | Modrinth 上传 |

上传取英文条目；英文文件里缺该版本条目时回退中文条目并在构建日志告警。两份都缺时上传的更新日志是「`CHANGE_LOG-ENG.md` 中没有 v<version` 条目」这样的占位提示。

## 上传

```bash
gradlew.bat publishModToModrinth
```

等价的单加载器任务：`:fabric:modrinth`、`:neoforge:modrinth`（任务组 `publishing`，会自动先跑 `assemble`）。

上传字段的来源：

| 字段 | 取值 |
|---|---|
| 项目 | 固定 `hiirosakura` |
| 版本号 | `gradle.properties` 的 `version` |
| 版本名 | `<version> <加载器> <MC 版本>`，例如 `4.1.0+alpha Fabric 26.3` |
| 发布渠道 | 版本号含 `alpha` → alpha，含 `beta` → beta，否则 release |
| 游戏版本 | `libs.versions.toml` 的 `minecraft` |
| 加载器 | 模块名（`fabric` / `neoforge`） |
| 文件 | `jar` 任务产物 |
| 更新日志 | `CHANGE_LOG-ENG.md` 中 `v<version>` 对应的条目 |
| 依赖 | `ibukigourd`，版本见下 |

## 依赖的版本固定

Modrinth 的版本级依赖要填的是「版本 ID」而不是版本号字符串，构建时通过公开 API 现查：

1. 先把 `gradle.properties` 的 `ibukigourd` 版本号（与 `libs.versions.toml` 中 `ibukigourd` 一致地）换成目标版本。
2. 请求 `https://api.modrinth.com/v2/project/ibukigourd/version`，按「版本号 + 当前模块的加载器」匹配唯一记录，取它的 `id`，写成 `VersionDependency`。
3. 匹配不到时（上游还没在 Modrinth 发布该版本、或只发了另一个加载器）回退成「依赖 `ibukigourd`，任意版本」，并在构建日志给出告警。
4. 需要绕过查询时用 `-Pmodrinth.ibukigourdVersionId=<版本 ID>`（例如 Modrinth 上还是草稿、公开 API 查不到）。

同一版本号在 Modrinth 上通常会按加载器存在多条记录，所以匹配必须带加载器，否则可能把 Fabric 的 IbukiGourd 钉到 NeoForge 的产物上。

## 可选：同步项目简介

`doc/modrinth/en.md` 是英文简介原文。勾选了 `WRITE_PROJECT` 后可以：

```bash
gradlew.bat :fabric:modrinthSyncBody
```

该任务读取 `modrinth { syncBodyFrom }`，当前未配置，需要时再补上文件路径。

## 已知边界

- `modrinth` 任务不兼容 Gradle 配置缓存（插件侧声明的限制），构建时若开启配置缓存需要排除该任务。
- 上传是外部写操作：同一个版本号重复执行会新建一条版本记录，发布前先确认版本号已经改好。
- 想做成打 tag 自动发布，可以在 GitHub Actions 里跑同样的 `publishModToModrinth`，但当前依赖 `maven.forpleuvoir.moe` 快照仓库里的 IbukiGourd 与 compose-minecraft，CI 环境是否能解析这些坐标尚未验证。
