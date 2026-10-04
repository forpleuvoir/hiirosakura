# 脚本

脚本是 HiiroSakura 的「万能口袋」：任务、事件订阅、匹配器、物品信息渲染里都能塞一段脚本，用它做判断、读写数据、模拟按键、发消息。

脚本语言是 **JEXL**（写法接近 JavaScript），改完即生效，不需要编译。

---

## 在哪里写脚本

| 入口 | 可用变量 | 说明 |
|---|---|---|
| 聊天栏 `/hs:eval <脚本>` | `this`、`player`（进游戏后） | 临时执行一段脚本，调试最方便；脚本含空格时用引号包起来 |
| 任务 / 事件订阅，执行器类型选「脚本」 | 上面的 + `task`（任务调度时）、`eventContext`（事件触发时） | 最常用 |
| 匹配器的「匹配脚本」 | `itemStack` + `result`（物品）／`blockResult`、`blockState`、`blockPos` + `result`（方块）／`entity` + `result`（实体） | 调用 `result.set(true)` 表示匹配成功 |
| 物品信息渲染的「脚本」 | `itemStack`、`renderState`、`player` | 用 `renderState["条目名"] = true/false` 控制显示 |
| 任务管理器「设置 → 脚本公用库」 | 无（只用来定义函数） | 每次执行脚本前都会先跑它 |

所有入口共用**同一个**脚本引擎，因此公用库、全局数据、配置修改在哪儿都通用。

---

## 运行规则（写之前先知道这几条）

**1. JEXL 语法**

- 语句以 `;` 结尾，**最后一句的值就是返回值**（任务/事件里返回值会被丢弃）。
- `if/else` 是表达式形式，可以参与赋值。
- 变量第一次赋值即创建，不需要声明类型。

**2. 可用的类被限定在三个包内，但原版 record 是例外**

脚本能直接调用的类只有 `java.lang.*`、`java.util.*`、`moe.forpleuvoir.*`（本模组、IbukiGourd、Nebula）。对其它包里的对象：

- **record 可以按字段直接读**。脚本引擎给 record 装了属性访问钩子，所以原版的记录类能像普通对象一样读取字段——例如 `minecraft:fireworks` 组件对应的 `Fireworks`，它的字段就是 `flightDuration` 和 `explosions`；嵌套的 record 同样能逐层读下去。烟花快捷使用的默认匹配脚本就是靠这个判断「有没有爆炸效果」的。
- **非 record 的原版对象读不到成员**：读属性或字段会报 `undefined property`，调方法会安静地返回 `null`（不报错）。JOML 的向量（`Vector3d` 等）属于这一类。
- `java.lang.Object` 的方法始终可用（`toString()`、`equals()` 等）。
- 命名空间 `client`（Minecraft 实例）不是 record，所以它的方法调用不会成功。要访问游戏数据请用下面那些包装对象。

判断标准很简单：按字段名读得到就是 record，报 `undefined property` 就不是，改用 `toString()` 或包装对象的方法。

**3. 不能创建对象**

JEXL 没有 `new`，也不能把 `Class` 对象当构造器用。要新建包装对象只能通过上下文变量，或 `common:import("类名")` 拿到的类上的静态工厂。

**4. 不能把脚本函数当 Java 的函数式接口**

JEXL 的 lambda 是它自己的闭包，不能传给 Java 的 `Predicate`、`Runnable` 这类参数。因此 `common:delayLaunch`、`common:scheduleStartTick`、`player.swapItem(...)` 这几个方法从脚本里基本用不了。

**5. Kotlin 的默认参数对脚本无效**

脚本调用必须把参数**一个不落地传全**。例如 `common:attack` 的源码默认值是 1，但脚本里写 `common:attack()` 不生效，要写 `common:attack(1)`。

**6. 报错不会崩游戏**

出错时屏幕弹出红色提示（5 秒、最多 16 行）并写日志，**这一个脚本的后续语句全部跳过**（同一次执行里前面的语句已生效）。如果是按 tick 高频运行的任务，报错会反复弹，调试时先把周期调大。

---

## 一次执行都发生了什么

以「任务里的脚本」为例，实际顺序是：

1. 先执行模组自带的公共脚本（`assets/<命名空间>/script/*.jexl`，资源重载时重新读取）——你平时用的 `cmd()`、`msg()`、`toast()` 就是这里定义的；
2. 注入 `player`（当前玩家的包装对象，玩家为空时不注入）；
3. 执行「脚本公用库」里你写的内容；
4. 最后执行你的脚本。

补充两点：

- 由 **Tick 任务**调度时，`task` 会先被注入；`this` 始终是这个脚本执行器本身。
- 同一个脚本执行器**重复执行时变量会累积**：上一次脚本设过的变量，下一次仍然可见（`player` 在玩家为空时不会覆盖，可能残留旧值）。写脚本时不要依赖「变量一定是干净的」。

---

## 脚本公用库

两处内容每次都会先跑一遍：

1. 模组自带的 `common.jexl`（下面这些函数就是它提供的）；
2. 任务管理器「设置 → 脚本公用库」文本框（默认空）。

自带的简写函数：

| 函数 | 例子 | 说明 |
|---|---|---|
| `cmd(x)` | `cmd("/say hi")` | 发送指令 |
| `msg(x)` | `msg("hi")` | 发送聊天消息 |
| `toast(x)` | `toast("hi")` | 屏幕提示（支持 `&{...}` 内联样式） |
| `logger(x)` | `logger("hi")` | 写模组日志 |
| `notify(title, msg)` | `notify("标题","内容")` | 发送系统通知（等价于 `common:sendNotification("标题","内容")`） |
| `state(key)` | `if(state("auto_forward")){...}` | 开关翻转：以 `_hs_<key>` 为键存在全局数据里，首次返回 `true`，之后每调用一次取反——适合「一个键开关挂机」 |
| `move(dir, duration)` | `move("forward", 999999)` | 模拟移动；方向支持 `forward/w`、`back/s`、`left/a`、`right/d`，其它方向弹红色提示。**`duration` 必须传** |

想加自己的公共函数，直接写进「脚本公用库」，所有脚本都能调用。

---

## `common:` API 全表

### 消息与通知

| 方法 | 参数 | 说明 |
|---|---|---|
| `common:sendMessage(message)` | 文本 | 发送聊天消息；以 `/` 开头会被当作**指令**发送 |
| `common:sendCommand(command)` | 指令文本 | 发送指令，不以 `/` 开头会自动补上 |
| `common:toast(content)` | 任意对象 | 屏幕提示，内容先 `toString()` 再按 `&{...}` 内联样式解析 |
| `common:sendNotification(title, content)` | 标题、正文 | 系统级通知：优先系统托盘气泡，失败回退 Windows Toast |
| `common:logger(msg)` | 任意对象 | 写一条 info 日志 |

### 输入模拟

时长单位统一是**客户端 tick**（20 tick ≈ 1 秒），小于 1 会被钳到 1。按键事件在**每个 tick 结束时**推进：第 1 个 tick 按下，中间 tick 连发，最后一个 tick 松开。所以 `duration = 1` 的按键会跨 2 个 tick 完成按下与松开。

| 方法 | 参数 | 说明 |
|---|---|---|
| `common:keyPress(keyCode, duration)` | 键码（整数，键盘或鼠标码都行） | 模拟按键 |
| `common:keyPress(key, duration)` | 键名字符串，如 `"key.keyboard.a"`、`"key.mouse.0"` | 同上；键名非法会抛 `IllegalArgumentException` |
| `common:mousePress(mouseCode, duration)` | 鼠标键码：`0` 左、`1` 右、`2` 中、`3..7` 侧键 | 模拟鼠标按键；**传键盘码会抛异常** |
| `common:mousePress(mouse, duration)` | 键名字符串，如 `"key.mouse.0"` | 同上 |
| `common:onKey(keyCode, scancode, action, modifiers)` | 原始事件参数，`action`：`1` 按下 / `0` 松开 / `2` 重复 | 直接投递一次原始按键事件，不走上面的状态机 |
| `common:attack(duration)` / `common:use(duration)` / `common:pickItem(duration)` | tick 数 | 模拟攻击 / 使用 / 选取方块（按你当前的按键绑定取键；绑定被改到键盘键时可能报错） |
| `common:moveForward(duration)`、`moveBack`、`moveLeft`、`moveRight` | tick 数 | 模拟移动 |
| `common:jump(duration)`、`common:sneak(duration)`、`common:sprint(duration)` | tick 数 | 模拟跳跃 / 潜行 / 疾跑 |

### 数据与配置

| 方法 | 参数 | 说明 |
|---|---|---|
| `common:getGlobalData(key)` | 键 | 读跨脚本共享的全局数据；没有时返回 `null` |
| `common:setGlobalData(key, value)` | 键、任意非空值 | 写全局数据。**只在本次游戏进程内有效，不会保存**，也没有删除接口 |
| `common:getConfig(key)` | 配置路径 | 返回**配置项对象本身**（不是值），需要再调 `.getValue()` 才拿到值；找不到返回 `null` |
| `common:setConfig(key, value)` | 配置路径、新值 | 修改配置。成功提示「修改设置[x = y]成功」，写入失败提示「修改设置[x = y]失败:原因」，路径不存在提示「未找到配置[x]」 |

**配置路径怎么写**：就是配置文件里的点分路径，**不含根名**。

- 例：`gameplay.chain_doors.enable` 对应 `gameplay → chain_doors → enable`；
- 例：`gameplay.gliding.quick_use_firework_rocket__when_gliding`（注意两个下划线）；
- 任务管理器那一组的分组名本身就叫 `hiirosakura.data.task_manager`，所以它的脚本路径是 `hiirosakura.data.task_manager.script_common_lib`。

值的类型要和配置项一致，类型不符会走到「写入失败」分支。

### 事件、菜单与其它

| 方法 | 参数 | 说明 |
|---|---|---|
| `common:enableEvent(name, enable)` | 订阅名、开关 | 按名称启用 / 禁用事件订阅，立即生效；找不到时提示「未找到事件[x]」 |
| `common:openMenu(menuKey)` | 菜单名 | 打开指定的自定义径向菜单；名字不存在时什么都不发生 |
| `common:import(className)` | 全限定类名 | 用模组类加载器加载类，返回 `Class` 对象；类不存在会抛异常 |
| `common:editItem()` | — | 打开物品编辑器，初始内容为你主手拿着的物品。确认后：创造模式把结果写入当前选中的快捷栏槽位并同步给服务端；其它模式把 `/give` 指令复制到剪贴板并弹出提示。主手为空时不会打开界面 |
| `common:delayLaunch(ms, action)` | 毫秒、动作 | 延迟执行——但**动作参数是 Java 的 `Runnable`，脚本传不了**（见「运行规则 4」） |
| `common:scheduleStartTick(delay, runner)` / `common:scheduleEndTick(delay, runner)` | 延迟（tick）、动作 | 在 `delay` 个 tick 之后的 tick 开始 / 结束执行一次。⚠️ `runner` 是 Java `Runnable`，脚本传不了（见「运行规则 4」），实际要延后做事请用「事件订阅 + Tick任务」或任务自带的延迟字段 |

想在延迟后做事，用「事件订阅 + Tick任务 执行器」，或者用任务本身的「延迟」字段。

---

## 脚本里能操作的游戏对象

脚本不能直接调 Minecraft 的方法，所有游戏数据都通过下面这些**包装对象**访问。它们的方法返回值也都是包装对象或字符串，不会把原版对象直接交给你。

### 从哪来

| 变量 | 类型 | 什么时候有 |
|---|---|---|
| `player` | `MainPlayer` | 在游戏中执行任何脚本时（注意：物品信息渲染脚本里的 `player` 是**原版玩家对象**，不是 record，读不到它的成员，别用） |
| `itemStack` | `HSItemStack` | 物品匹配脚本、物品信息渲染脚本 |
| `entity` | `HSEntity`（按实体类型分派，可能是 `HSLivingEntity` / `HSPlayerEntity` / `MainPlayer`） | 实体匹配脚本 |
| `blockState` / `blockResult` / `blockPos` | `HSBlockState` / `HSBlockHitResult` / 向量 | 方块匹配脚本 |
| `eventContext.*` | 见 [事件文档](event.md) | 事件脚本，例如 `break_block` 的 `eventContext.blockState` 就是 `HSBlockState` |

### `player`：HSEntity 家族

继承链：`HSEntity` → `HSLivingEntity` → `HSPlayerEntity` → `MainPlayer`。下面只列每一层**新增**的方法，上层方法在子类上同样可用。

**HSEntity（通用实体）**

| 方法 | 返回 | 说明 |
|---|---|---|
| `getName()` | 字符串 | 实体名（玩家名） |
| `getDisplayName()` | 字符串 | 显示名，含队伍前缀等样式后的纯文本 |
| `getPos()` / `getEyePos()` | 向量 | 世界坐标 / 眼睛位置，单位方块。**只能 `toString()`** |
| `getRotation()` | 向量 | x = 俯仰角、y = 偏航角（度）。同样只能 `toString()` |
| `getPitch()` / `getYaw()` | 小数 | 俯仰角 / 偏航角，单位度 |
| `getSpeed()` | 小数 | 瞬时速度，即本 tick 位移向量的长度，单位方块/tick |
| `getFlyDistance()` | 小数 | 累计移动距离（原版 `flyDist`），单位方块，不随时间衰减 |
| `getUuid()` | 字符串 | 实体 UUID |
| `getId()` | 整数 | 实体网络 ID |
| `getType()` | 字符串 | 实体注册名，`命名空间:路径` 形式，如 `"minecraft:zombie"` |
| `getWorld()` | 字符串 | 维度 ID，如 `"minecraft:overworld"` |
| `isFireImmune()` / `isOnFire()` | 布尔 | 是否免疫火焰 / 是否正在燃烧 |
| `isTouchingWaterOrRain()` / `isSwimming()` | 布尔 | 是否接触水或雨 / 是否在游泳 |
| `isSneaking()` / `isAlive()` / `isInvulnerable()` | 布尔 | 是否潜行 / 存活 / 无敌 |
| `isInvisible()` | 布尔 | 是否隐身 |
| `vanilla` | 原版实体 | 底层对象，读得到但用不了（见运行规则 2） |

**HSLivingEntity（生物）**

| 方法 | 返回 | 说明 |
|---|---|---|
| `getMaxHealth()` / `getHealth()` | 小数 | 生命上限 / 当前生命，单位「点」（20 点 = 10 颗心） |
| `getHealthPercent()` | 小数 | 生命百分比，`0.0 ~ 1.0` |
| `getMainHandStack()` / `getOffHandStack()` | `HSItemStack` | 主手 / 副手物品（空手时数量为 0） |
| `isBaby()` | 布尔 | 是否幼年 |

**HSPlayerEntity（玩家）**

| 方法 | 返回 | 说明 |
|---|---|---|
| `isCreative()` / `isSpectator()` | 布尔 | 创造 / 旁观模式 |
| `getExperienceLevel()` | 整数 | 经验等级 |
| `getTotalExperience()` | 整数 | 累计总经验 |
| `getExperienceProgress()` | 小数 | 当前升级进度，`0.0 ~ 1.0` |

**MainPlayer（自己）**

| 方法 | 返回 | 说明 |
|---|---|---|
| `getHitResult()` | `HSHitResult` 或 `null` | 准星命中结果，按命中对象自动是方块 / 实体子类 |
| `getHitBlock()` | `HSBlockHitResult` 或 `null` | 只有命中方块时才有值 |
| `getHitEntity()` | `HSEntityHitResult` 或 `null` | 只有命中实体时才有值 |
| `swapItem(predicate)` | — | 按条件把物品栏里的物品换到手部。⚠️ 参数是 Java `Predicate`，脚本传不了 |

### `itemStack`：HSItemStack

| 方法 | 返回 | 说明 |
|---|---|---|
| `getCount()` / `getMaxCount()` | 整数 | 数量 / 最大堆叠数 |
| `isStackable()` | 布尔 | 是否可堆叠 |
| `getItem()` | 字符串 | 物品注册名，`命名空间:路径` 形式，如 `"minecraft:diamond_sword"` |
| `getName()` / `getItemName()` | 字符串 | 显示名（会带自定义名称）/ 基础物品名 |
| `getTags()` | 字符串列表 | 物品标签 ID，如 `"minecraft:planks"` |
| `hasTag(tag)` | 布尔 | 是否带该标签。**必须写全 `命名空间:路径`**，精确相等，不支持 `!` 取反 |
| `hasComponent(id)` | 布尔 | 是否带该数据组件，如 `"minecraft:damage"`。**ID 未注册会抛异常** |
| `getComponent(id)` | 组件值或 `null` | 组件值；未注册返回 `null`，ID 写法非法会抛异常。返回的是原版对象：**record 形式的组件可以按字段读**（例如 `minecraft:fireworks` 的 `flightDuration`、`explosions`），其它类型通常只能 `toString()` |
| `isDamageable()` | 布尔 | 是否有耐久 |
| `getDurability()` / `getMaxDurability()` | 整数 | **已损耗值** / 耐久上限（不可损坏物品为 0） |
| `getDamagePercent()` | 小数 | 损耗比例 `0.0 ~ 1.0`；不可损坏物品返回 `0` |
| `getRarity()` | 字符串 | `"common"` / `"uncommon"` / `"rare"` / `"epic"` |
| `isEnchantable()` | 布尔 | 是否可附魔 |
| `isShovel()` / `isHoe()` / `isAxe()` / `isShield()` / `isBlock()` | 布尔 | 物品类别判断 |
| `getEnchantments()` / `getStoredEnchantments()` | 字符串列表 | 附魔 / 附魔书内附魔的显示名。名称需要注册表，因此**要先进世界**才读得到，不在世界里时返回空列表 |
| `vanilla` | 原版物品堆 | 读得到但用不了 |

### `blockState`：HSBlockState 与 HSBlock

| 方法 | 返回 | 说明 |
|---|---|---|
| `getType()` | 字符串 | 方块 ID，如 `"minecraft:stone"` |
| `getBlock()` | `HSBlock` | 方块类型对象 |
| `getProperty()` | 映射 | 全部方块属性的「名字 → 取值」，如 `facing` → `north` |
| `getProperty(key)` | 字符串或 `null` | 取单个属性；名字大小写敏感 |
| `getTags()` | 字符串列表 | 方块标签 ID |
| `hasTags(tag)` | 布尔 | 是否带该标签，精确相等，要写全 `命名空间:路径` |
| `isAir()` / `isLiquid()` / `isSolid()` / `isBurnable()` | 布尔 | 是否空气 / 液体 / 实心 / 可燃 |
| `isOpaque()` / `hasSidedTransparency()` | 布尔 | 是否遮挡光线 / 是否使用形状遮挡光线 |
| `getLight()` | 整数 | 自发光等级 `0 ~ 15` |
| `getOpacity()` | 整数 | 减光等级 `0 ~ 15` |
| `getHardness()` | 小数 | 破坏硬度（`-1` 表示不可破坏）。⚠️ **需要已进入世界**，且固定按坐标 `0,0,0` 计算 |
| `isToolRequired()` | 布尔 | 是否需要正确工具才能掉落 |
| `getMapColor(...)` | 颜色 | ⚠️ 当前实现脚本**调不到**（方法名被编译器改写） |

`HSBlock` 上另有 `getType()`（方块 ID）、`isWaterlogged()`、`isCrop()`、`isFluid()` 三个类型判断。

### 命中结果：HSHitResult 家族

基类 `HSHitResult`：`getType()`（`"MISS"` / `"BLOCK"` / `"ENTITY"`）、`getPos()`（向量，只能 `toString()`）、`isBlock()`、`isEntity()`。

**HSBlockHitResult（命中方块）**

| 方法 | 返回 | 说明 |
|---|---|---|
| `getBlockState()` / `getBlock()` | `HSBlockState` / `HSBlock`，可能为 `null` | 命中的方块与状态；未进入世界时为 `null` |
| `getBlockPos()` | 向量 | 方块坐标。⚠️ 只能 `toString()`，读不出分量 |
| `getSide()` | 字符串 | 命中面，**大写**：`"UP"` / `"DOWN"` / `"NORTH"` / `"SOUTH"` / `"WEST"` / `"EAST"` |
| `isInside()` / `isWorldBorderHit()` | 布尔 | 命中点是否在方块内部 / 是否命中世界边界 |
| `offset(direction, i)` | `HSBlockState` | 取沿指定方向偏移 `i` 格的方块状态，方向名大小写不敏感；方向名非法或未进入世界会抛异常 |

**HSEntityHitResult（命中实体）**

| 方法 | 返回 | 说明 |
|---|---|---|
| `getEntity()` | `HSEntity`（按类型分派） | 拿到的可能是 `HSLivingEntity` / `HSPlayerEntity` / `MainPlayer`，可以直接用 `getHealth()`、`isCreative()` 这些子类方法 |

### 音效：HSSoundInstance

只在 `sound_play` 事件的 `eventContext.sound` 里出现。

| 方法 | 返回 | 说明 |
|---|---|---|
| `getId()` | 字符串 | 音效 ID，如 `"minecraft:entity.pig.ambient"` |
| `getCategory()` | 字符串 | 音效分类，**大写**：`"MASTER"`、`"MUSIC"`、`"RECORDS"`、`"WEATHER"`、`"BLOCKS"`、`"HOSTILE"`、`"NEUTRAL"`、`"PLAYERS"`、`"AMBIENT"`、`"VOICE"`、`"UI"` |
| `getVolume()` / `getPitch()` | 小数 | 音量 / 音调。音效尚未解析时原版取值会抛异常，这里回落到 `1.0` |
| `getX()` / `getY()` / `getZ()` / `getPos()` | 小数 / 向量 | 音源坐标，单位方块 |
| `getDelay()` | 整数 | 播放延迟，单位 tick |
| `isLooping()` / `isRelative()` | 布尔 | 是否循环 / 坐标是否相对玩家 |
| `getAttenuation()` | 字符串 | 衰减类型：`"NONE"` / `"LINEAR"` |
| `canPlay()` | 布尔 | 是否允许播放 |
| `canStartSilent()` | 布尔 | 音量为 0 时是否也允许开始播放 |

---

## 几个完整例子

**物品信息里按内容隐藏某些行**

```javascript
if(itemStack.getCount() < 16){ renderState["count"] = false; }
```

**匹配器里表示匹配成功**

```javascript
// 物品匹配器
if(itemStack.hasTag("minecraft:planks")){ result.set(true); }

// 方块匹配器：只匹配上半砖
if(blockState.getProperty("type") == "top"){ result.set(true); }
```

**读取原版 record 组件的字段（烟花）**

组件是 record 时可以直接按字段读，下面这段就是「烟花」匹配器默认脚本的判断部分：

```javascript
fireworks = itemStack.getComponent("minecraft:fireworks");
if (fireworks != null) {
    // 有飞行时间、且没有任何爆炸效果 → 纯加速烟花
    if (fireworks.flightDuration > 0 && (fireworks.explosions == null || fireworks.explosions.size() < 1)) {
        result.set(true);
    }
}
```

**攻击事件里读取目标血量**

```javascript
// 命中结果会自动是子类型，getEntity() 也按实体类型分派，所以生物能直接取血量
if(eventContext.hitResult.isEntity()){
    toast("目标血量：" + eventContext.hitResult.getEntity().getHealth());
}
```

**事件触发时提示音效来源**

```
toast("&{#FFAA00}" + eventContext.sound.getId());
```

**一个键开关持续前进（任务 + 快捷键）**

```javascript
if(state("auto_forward")){
    msg("自动前进：开");
    move("forward", 999999);
}
```

**死亡时记录并提示坐标**

```
toast("死亡位置：" + player.getPos());
```

---

## 已知的坑（汇总）

| 现象 | 原因 / 对策 |
|---|---|
| `client.xxx` 调用没反应 | Minecraft 实例不是 record，它的方法调用会静默返回 `null`；改用包装对象 |
| `player.getPos().x()` 拿不到值 | JOML 向量不是 record，读不出分量，只能 `toString()` 或改用 `getPitch()` 这类返回小数的接口 |
| 读原版对象的字段报 `undefined property` | 该对象不是 record；只有 record 能按字段读，其它原版类只能 `toString()`，或改用包装对象提供的方法 |
| `getComponent("minecraft:xxx")` 读字段报错 | 这个组件不是 record 类型；record 形式的组件（如 `minecraft:fireworks`）才能按字段读 |
| `vanilla` 上什么都不能调 | 如果目标不是 record，就只能用 `java.lang.Object` 的方法（`toString()` 等） |
| `common:attack()` 不生效 | 脚本必须传全参数，写 `common:attack(1)` |
| `common:delayLaunch(...)` / `scheduleStartTick(...)` 报错 | 第 2 个参数是 Java `Runnable`，脚本传不了；改用「事件订阅 + Tick任务」或任务的延迟字段 |
| `player.swapItem(...)` 报错 / 无效果 | 参数是 Java `Predicate`，脚本传不了 |
| 脚本里上一次的变量还在 | 同一个执行器复用上下文；变量不会自动清理 |
| 坐标类接口在没进世界时报错 | 多个方法需要客户端已进入世界（例如 `getHardness()`、`offset()`） |
| `getEnchantments()` 返回空列表 | 附魔显示名需要注册表，先进入世界再读 |
| `entity.sInvisible()` / `sound.canplay()` 报找不到方法 | 这两个旧名字是拼写错误，已经删掉；分别改用 `isInvisible()` / `canPlay()` |
