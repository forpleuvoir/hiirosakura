# 事件订阅

任务管理器解决的是「到点做什么」，事件订阅解决的是「**发生了什么的时候**做什么」：进入服务器、收到消息、破坏方块、播放某个音效……都能成为触发条件，触发后自动发指令、发消息、跑脚本或安排一个 Tick 任务。

**什么时候用**：进服务器自动打一声招呼、死亡时记录坐标、听到特定音效时提醒自己、屏蔽掉某类消息。

入口：**H + S → 事件订阅管理器**。

---

## 新建一条订阅

1. 点工具栏的「订阅事件」。
2. 填**名称**（脚本里可以按这个名字启停它）。
3. 选**事件类型**：下拉列表里是全部 26 种事件，把鼠标停在某一项上会显示该事件的说明（触发时机、有哪些属性、能不能取消）。
4. 选**执行器类型**并填内容。
5. 保存。列表里每行都有启用开关，可以随时单独启停；也能拖动排序、按事件类型过滤、编辑或删除（删除会二次确认）。

---

## 四种执行器

| 类型 | 效果 |
|---|---|
| **指令** | 触发时发送该指令（不以 `/` 开头会自动补上） |
| **消息** | 触发时原样发送该消息 |
| **脚本** | 触发时执行脚本，并把这次事件的上下文放进变量 `eventContext` |
| **Tick任务** | 触发时**添加**一个 Tick 任务（不是立刻执行一次），任务自身的延迟、周期、次数照常生效 |

「Tick任务」里的任务模型和任务管理器一致，它的内层执行器同样可以选脚本——那种情况下脚本里也能读到 `eventContext`。适合「事件发生后再延迟几秒做点什么」。

---

## 在脚本里读事件属性

事件的所有信息都挂在 `eventContext` 上，用属性名读取。例如：

```javascript
// 破坏方块时提示砍了什么
toast("挖掉了 " + eventContext.blockState.getType() + "，方向 " + eventContext.direction);

// 收到消息时看看是谁发的
if(eventContext.uuid != null){ logger("来自 " + eventContext.uuid + " 的消息"); }

// 播放音效时打印音效 ID
logger(eventContext.sound.getId());
```

各事件的可读属性见下面的[事件清单](#事件类型清单)。几点提醒：

- 属性名**大小写敏感**，必须和表里完全一致。
- 属性是包装对象时（例如 `blockState`），能调用的方法见 [脚本](script.md#脚本里能操作的游戏对象)。
- 「发送指令」「发送消息」两个事件的内容属性是**可修改**的：在脚本里直接赋值就会替换掉即将发送的内容，例如 `eventContext.message = eventContext.message + " ！";`。

---

## 在脚本里取消事件

「可取消」的事件可以在脚本里拦下来：**调用 `eventContext.cancel()`**，无参数。想先判断状态用 `eventContext.isCancelled()`（返回布尔）。

```javascript
// 屏蔽所有聊天消息的显示（消息仍会到达客户端，只是不进入聊天栏）
eventContext.cancel();

// 只在特定关键词时取消
if(eventContext.message.contains("广告")){ eventContext.cancel(); }
```

注意：

- 取消方式是**方法调用**，不是属性赋值（写成 `eventContext.cancel = true` 或 `setCancelled(true)` 都不会生效）。
- `cancel()` 首次调用返回 `true`，重复调用返回 `false`。
- 取消的后果有两层：**排在后面的其它订阅不会被执行**，并且**原版这次行为会被终止**（例如「键盘按下」被取消后，这个按键不再被游戏处理）。
- 不可取消的事件（客户端启动/停止、打开游戏菜单、Tick 开始/结束、各个游戏与服务器事件、死亡复活）调用 `cancel()` 会报错，因为这些事件的上下文是别的类型。

可取消的事件共 8 个：破坏方块、发送指令、发送消息、接收消息、玩家攻击、玩家选取、玩家使用、音效播放，外加输入类的 7 个（键盘按下/释放、鼠标按下/释放、滚动、移动、拖拽）——输入类事件由框架提供，同样支持 `cancel()`。

---

## 事件类型清单

共 26 种。属性列给出**名称**与**类型**；标「可改」的属性可以在脚本里赋值。

### 输入类（7 种，均可取消）

| 事件 | 触发时机 | 属性 |
|---|---|---|
| 键盘按下 | 键盘按键被按下 | `keyCode: KeyCode` 键码；`modifiers: Int` 修饰符；`isPressed: Boolean` 是否按下；`isRepeat: Boolean` 是否长按重复；`name: String` 按键名；`env: KeyEnvironment` 触发环境（游戏内 / 界面内 / 任意） |
| 键盘释放 | 键盘按键被释放 | 同上 |
| 鼠标按下 | 鼠标按键被按下 | `keyCode: KeyCode`；`modifiers: Int`；`isPressed: Boolean`；`name: String`；`env: KeyEnvironment`（**没有 `isRepeat`**） |
| 鼠标释放 | 鼠标按键被释放 | 同上 |
| 鼠标滚轮滚动 | 滚轮滚动 | `xoffset: Double`、`yoffset: Double` 滚动量（已按游戏设置缩放）；`env: KeyEnvironment` |
| 鼠标移动 | 光标位置变化 | `x: Double`、`y: Double` 光标位置（已按 GUI 缩放）；`env: KeyEnvironment` |
| 鼠标拖拽 | 按住鼠标键拖拽 | `keyCode: KeyCode`；`name: String`；`x: Double`、`y: Double`；`env: KeyEnvironment` |

上下文另有三个方法可用：`isShiftPressed()` / `isCtrlPressed()` / `isAltPressed()`，以及 `isReleased`。

### 客户端与 Tick（5 种，不可取消）

| 事件 | 触发时机 | 上下文 |
|---|---|---|
| 客户端启动 | 客户端启动时 | 客户端实例本身（没有额外属性） |
| 客户端停止 | 客户端停止时 | 同上 |
| 打开游戏菜单 | 打开游戏菜单时 | 同上 |
| Tick开始 | 客户端 Tick 开始时（原版逻辑执行前） | 同上 |
| Tick结束 | 客户端 Tick 结束时（原版逻辑执行后） | 同上 |

### 游戏与玩家（6 种，不可取消）

| 事件 | 触发时机 | 属性 |
|---|---|---|
| 加入服务器 | 成功连接到服务器时 | `serverName: String`、`serverAddress: String` |
| 进入游戏 | 进入游戏时 | `serverName: String`、`serverAddress: String` |
| 退出游戏 | 退出游戏时 | `serverName: String`、`serverAddress: String` |
| 断开连接 | 与服务器断开连接时 | `serverName: String`、`serverAddress: String`、`title: String` 断开标题、`reason: String` 断开原因 |
| 玩家死亡 | 玩家死亡时 | `message: String` 死亡消息 |
| 玩家复活 | 死亡后复活时 | 无属性 |

### 玩法类（8 种，全部可取消）

| 事件 | 触发时机 | 属性 |
|---|---|---|
| 破坏方块 | 客户端玩家正在破坏方块时 | `blockState: HSBlockState` 被破坏的方块状态；`direction: String` 破坏方向 |
| 发送指令 | 客户端玩家发送指令时 | `command: String` 指令内容（**可改**） |
| 发送消息 | 客户端玩家发送消息时 | `message: String` 消息内容（**可改**） |
| 接收消息 | 客户端接收到消息时 | `message: String` 消息内容；`uuid: String?` 发送者 UUID（可能为空） |
| 玩家攻击 | 攻击实体或其他玩家时 | `hitResult: HSHitResult` 命中结果 |
| 玩家选取 | 鼠标中键选取时 | `hitResult: HSHitResult` 命中结果 |
| 玩家使用 | 使用物品或与方块交互时 | `hitResult: HSHitResult` 命中结果；`itemStack: HSItemStack` 使用的物品 |
| 音效播放 | 音效播放时 | `sound: HSSoundInstance` 音效实例 |

命中结果类属性会自动是对应的子类型：命中方块时 `hitResult` 实际是 `HSBlockHitResult`，命中实体时是 `HSEntityHitResult`，可以用 `getType()` 得到 `"BLOCK"` / `"ENTITY"` / `"MISS"` 来区分。`HSEntityHitResult.getEntity()` 返回的包装对象同样按实体类型分派（玩家 / 生物 / 普通实体），可以直接调用 `getHealth()`、`isCreative()` 这类子类方法。

---

## 注意事项

- 订阅关掉后不会再被触发；重新打开会立刻恢复，不需要重启游戏。
- 事件类型如果写了未知的值（手改配置时容易发生），读取时会回退成「键盘按下」并记录日志，界面上不会有提示。
- 事件只在客户端触发，服务端不需要安装本模组。
- 「玩家使用」事件在每次交互时都会触发，脚本里做重活会影响手感，必要时先判断 `hitResult.getType()`。

---

## 配置项速查

| 字段 | 配置键 | 默认值 | 说明 |
|---|---|---|---|
| 名称 | `name` | 空 | 脚本里可用 `common:enableEvent("名称", true)` 启停 |
| 是否启用 | `enabled` | 开启 | — |
| 事件类型 | `event_type` | 键盘按下 | 共 26 种，见上表 |
| 执行器类型 | `executor_type` | 脚本 | 指令 / 消息 / 脚本 / Tick任务 |
| 执行器 | `executor` | 空 | 与类型对应的内容；Tick任务类型时是任务对象（含名称、延迟、周期、次数、执行时间、内层执行器） |

数据文件：`config/hiirosakura/data/event_manager.json`。
