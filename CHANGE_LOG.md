v4.0.0+alpha
  - `Minecraft`版本更新至`26.2`（Fabric API `0.157.0+26.2` / Loader `0.19.3` / Loom `1.17.19`；NeoForge `26.2.0.59` / moddev `2.0.143`；ModMenu `20.0.1`）
  - `IbukiGourd`更新至`1.0.0-alpha`，UI 全面改用其 sokitsu 组件库与屏幕 / 面板 / 浮层骨架；渲染改用自研`compose-minecraft` `0.1.0`；`nebula`（`0.4.0`）随`IbukiGourd`传递提供，不再单独声明
  - 全面适配新 UI 骨架，包括：
    - 物品编辑器（组件编辑浮层、HolderSet 编辑浮层、取色器弹窗、物品列表页）
    - 任务管理器与事件订阅页
    - 自定义轮盘菜单页与快捷任务执行界面
    - 连锁开门、自动补种、方块→物品映射等配置浮层改卡片式
    - 聊天过滤、聊天注入、服务器配置、音效列表与匹配器映射改用列表 / 映射骨架
  - 物品编辑器：通用组件编辑器统一「类型标签 + 控件」与 key 标签排版，新增展示字段与开关控件；按住快速动作键点编辑 / 添加改为打开 JSON 编辑器；DataComponent 预绑定改两段式，修复动态注册表 Holder 未绑定崩溃
  - 新增多物品选择器`MultiItemSelector`与浮动添加按钮；物品选择器改用 IG 的`ItemIconButton`，网格格子按图标尺寸计算，修复切换分类卡死
  - 径向菜单新增 3D 倾斜（指针离中心越远、盘面越向指针方向倾斜），倾斜角度可配置；自定义轮盘菜单设置面板新增倾斜角度滑块；修正默认半径与滚轮翻页方向
  - 列表 key 迁移到 IG `KeyedListState` 并加入条目淡入动画；手写条件`Modifier`改用平台`thenIf`；图标默认值与硬编码视觉常量外提为可覆盖参数
  - 聊天气泡改用 sokitsu 图集渲染并支持文本样式；控件标签改为手动挂 tooltip
  - 修正世界纹理管线与引信内条长度
  - Mixin 适配 26.2：选中物品名注入补全方法描述符（`Hud.extractSelectedItemName` 的`(GuiGraphicsExtractor, int)`重载），修复启动即崩溃
  - 修复浮层字段顺序、烟花匹配与键冲突提示
  - 重构：图标目录改名`defaults`，`ItemWrapper.border`参数改名`hoverHighlight`；明确注释只描述当前代码

v3.6.0+alpha
  - `Minecraft`版本更新至`26.1.2`
  - 全面迁移至 Compose UI，包括：
    - 物品编辑器
    - 任务管理器
    - 匹配器编辑器（方块信息匹配器、物品匹配器）
    - 选择器组件（音效选择等）
    - ChatBubbleServerConfig 配置编辑
    - 代码编辑器
  - 新增 Compose 通用语法高亮系统
  - 新增事件管理 UI 及事件类型国际化
  - 新增自定义轮盘菜单管理页面及边框设置
  - 径向菜单组件接入快捷任务执行界面
  - 新增 OpenGameMenu 事件
  - 完善本地化并调整配置默认值
  - 修复了聊天气泡匹配玩家时可能导致的崩溃问题

v3.5.3+beta
 - 修复了聊天气泡匹配玩家时可能导致的崩溃问题
 - 物品编辑器新增对`Consumable`,`KineticWeapon`,`PiercingWeapon`,`Fireworks`,`FireworkExplosion`,`LodestoneTracker`,`DyeColor`组件的适配

v3.5.2+beta
 - 修改了轮盘菜单的渲染方式,由原来地渲染四边形改为圆形
 - 完善了i18n

v3.5.1+beta
 - 新增自定义轮盘菜单
 - 在脚本 API 中新增了键盘和鼠标输入模拟功能

v3.5.0+beta
 - `Minecraft`版本更新至`1.21.11`
 - 新增连锁开门功能
 - 物品编辑器新增组件适配
 - 脚本引擎由`nashorn`更换为`jexl`
 - 部分功能优化

v3.4.0+beta
 - `Minecraft`版本更新至`1.21.10`,并且支持`Neoforge`
 - 物品编辑器新增一些数据组件适配器