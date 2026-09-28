# 构建排障记录

## `Could not resolve net.minecraft:minecraft-merged-<hash>:<mc版本>`

### 现象

`:fabric:compileKotlin` 失败：

```text
> Could not resolve all files for configuration ':fabric:compileClasspath'.
   > Could not resolve net.minecraft:minecraft-merged-458b96759e:26.2.
     Required by: project ':fabric'
      > Could not get resource 'https://maven.forpleuvoir.moe/snapshots/net/minecraft/...pom'.
         > Could not GET '...'
            > Read timed out
```

在 IDEA 里表现为「正在下载 minecraft-merged-xxx.pom」卡住 1 分 40 秒左右，那个时长是在等远端仓库超时。

该构件不会存在于任何远端仓库：它由 Fabric Loom 生成，只发布到本地文件仓库
`<root>/.gradle/loom-cache/minecraftMaven`（`LoomFiles.getLocalMinecraftRepo()`；
artifactId 为 `minecraft-merged-` 拼接 `MinecraftJarProcessorManager#getJarHash()`
的 10 位 sha1，版本号就是 Minecraft 版本）。

### 原因

Gradle 按**仓库声明顺序**逐仓库查找模块，且**只要某个仓库在查找该模块时发生 IO
错误（连接被拒、读超时），这个模块的解析就立即失败**，不会继续 fallback 到后面的
仓库。

`buildSrc/src/main/groovy/multiloader-common.gradle` 中声明的
`https://maven.forpleuvoir.moe/snapshots` 原先没有内容过滤；又因为插件应用顺序
（`multiloader-loader` 先于 `fabric-loom`），它排在 Loom 追加的
`LoomLocalMinecraft` 之前。于是这台服务器一旦变慢或不可达，
`net.minecraft:minecraft-merged-*` 在轮到本地仓库之前就先失败了。

本地仓库里 pom/jar 是否齐全不影响结论：Gradle 根本没走到那一步。

### 修复

给该仓库加内容过滤，让它只参与 `moe.forpleuvoir` 的查找：

```groovy
    maven {
        name = 'Forpleuvoir'
        url = 'https://maven.forpleuvoir.moe/snapshots'
        // 该仓库只提供 moe.forpleuvoir 下的构件
        content { includeGroupAndSubgroups('moe.forpleuvoir') }
    }
```

### 排查要点

- 完整报错在 Gradle daemon 日志里：`<GRADLE_USER_HOME>/daemon/<gradle版本>/daemon-*.out.log`。
- `Could not resolve: net.minecraft:...`（没有 `>` 前缀、通常连续三行）是 IDE 模型解析的
  宽松提示，此时构建仍可能 `BUILD SUCCESSFUL`；不能用构建成功的结论判断该依赖已经解析正常。
- 「本地仓库有文件」和「Gradle 会用到它」是两件事：用最小工程直接声明该 file 仓库并解析
  同一坐标可以验证构件本身没问题。
- 复现方式：最小工程按 `[不可达的 maven, Loom 本地 file 仓库]` 顺序声明仓库，依赖
  `net.minecraft:minecraft-merged-<hash>:<版本>`，报错文本与真实构建一致；给第一个仓库加上
  上述内容过滤后立即解析成功。
- 临时绕行：`--offline`。离线模式不访问远端仓库，本地 file 仓库照常可用。
- 同类隐患：任何**可能挂起**的仓库都会挡住排在它后面的仓库；只要该仓库只提供特定分组的
  构件，就应该用 `content {}` 或 `exclusiveContent` 限定范围。
