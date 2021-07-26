# 关于

配置界面快捷键默认`H+S`
基本上是重写了之前的suika mod 大体使用不会有太大区别 基于 [Fabric](https://fabricmc.net/ "Fabric") 编写
贴出代码基本都是 [Fabric](https://fabricmc.net/ "Fabric") 开发环境下的 如果代码不是很长的话 尽量贴一下
前置Mod[Malilib](https://github.com/maruohon/malilib "Malilib")

------------

# 指令

客户端指令都是以`hs:`开头的 一般来说指令中用`<>`围起来的部分是一个参数，具体描述格式还没想好

## gamma

`/hs:gmma <value>`
`value(double)` 0~100的数字，可以是小数点 修改游戏伽马值（其实就是亮度） 具体实现代码大概如下

```java
MinecraftClient.getInstance().options.gamma=value;
```

不知道怎么改回默认值的话 在游戏设置里调一下亮度就行了

## config & data

一般来说用不到的指令 游戏中修改了配置或者数据文件，用reload重新加载
`/hs:config save` 保存配置到文件
`/hs:config reload `从文件中读取配置

`/hs:data save` 保存数据到文件
`/hs:data reload` 从配置文件中读取数据

## qcms

快捷聊天消息发送
`/hs:qcms add <remark> <messageStr>` 添加一条消息（备注，消息内容） 参数都是字符串
`/hs:qcms remove <remark>` 删除一条备注为`<remark>`的消息
`/hs:qcms rename <remark> <newRemark>` 重命名(备注)一条消息
`/hs:qcms reset <remark> <newValue>` 重新设置一条消息的内容
`/hs:qcms show` 显示已添加的快捷消息

## tooltip

`/hs:tooltip add <itemStack>(可空) <tip>`  为`<itemStack>`物品添加一条工具提示`<tip>`
> `<itemStack>`参数填入为空时会选择玩家主手上的物品添加
`&`字符可以作为格式字符 在给玩家头颅添加时需要输出NBT标签 例`/hs:tooltip add minecraft:player_head{"SkullOwner":"dhwuia"} "傻子"`

`/hs:tooltip remove <itemStack>(可空) <index>`
> 删除`<itemStack>`物品下标为`<index>`的一条工具提示
`<itemStack>`参数填入为空时会选择玩家主手上的物品 数组下标是从`0`开始计算的
`<index> `为`-1`时 删除该物品的所有工具提示

## scmr

如果是单人存档 这个指令无效
`/hs:scmr set <regex>`
> 为当前服务器的聊天消息设置正则过滤

`/hs:scmr remove`
> 移除当前服务器的正则过滤

## sce

切换相机实体到其他客户端玩家实体
`/hs:sce <playerName>`
> 一般使用提示的参数

设置光标所指的实体为目标实体
`/hs:sce setTarget`
> Tweakeroo开启自由相机(FreeCamera)时，依然是以玩家本体所指的实体为目标

将相机实体切换为目标实体
`/hs:sec switchToTarget`


------------

# 特性

特性就是特性

## 关闭游戏自带教程

RT没什么用但还是加了而且不给开关（非常任性） 通过`Mixin`注入`MinecraftClient`的构造方法

```java
@Inject(method = "<init>", at = @At("RETURN"))
public void init(CallbackInfo ci){
        tutorialManager.setStep(TutorialStep.NONE);
        }
```

## 创造模式鼠标中键获取玩家头颅

RT依然是没什么用但还是加了并且依旧默认开启不能关闭 通过`Mixin`注入`Entity`的`getPickBlockStack`方法

```java
@Inject(method = "getPickBlockStack", at = @At("RETURN"), cancellable = true)
public void getPickBlockStack(CallbackInfoReturnable<ItemStack> returnable){
        if(this.type.equals(EntityType.PLAYER)){
        returnable.setReturnValue(PlayerHeadUtil.getPlayerHead(getEntityName()));
        }
        }
```

其中` PlayerHeadUtil.getPlayerHead(String playerName)`[参见](#通过玩家名称获取玩家头)

## 聊天显示(气泡)

通过正则过滤来获取玩家名和消息内容
![](https://suika.forpleuvoir.com/wp-content/uploads/2021/06/2021-07-18_13.23.23.png)
在服务器时，通过指令 `/hs:scmr set "regex"` 设置正则表达式 匹配内容必须要有 `name`以及`message`分组 例如原版的消息正则表达式为
`(<(?<name>(.*))>)\s(?<message>.*)`

## 掉落物显示物品名称以及数量

![](https://suika.forpleuvoir.com/wp-content/uploads/2021/06/2021-06-13_17.03.07.png)
距离相机实体一定距离内会渲染，之后会改成可以自定义的距离 通过`Mixin`注入`ItemEntityRenderer`的`render`方法 相关提交

## TNT显示剩余爆炸时间

![](https://suika.forpleuvoir.com/wp-content/uploads/2021/06/2021-06-13_17.03.22.png)

## 切换物品时会显示附魔

![](https://suika.forpleuvoir.com/wp-content/uploads/2021/06/2021-06-13_16.01.42.png)

## 自动复活

开启后死亡会跳过死亡画面直接复活

## 快捷聊天消息发送

懒得写介绍了 默认快捷键 添加一条打开试一试 等级`（Integer）`为排序等级
> 值越小排序越靠前
> 为空时不参与排序 且会排在已排序的消息之后

![](https://suika.forpleuvoir.com/wp-content/uploads/2021/06/7HTK7SWOGVVK0RBVO.png)
![](https://suika.forpleuvoir.com/wp-content/uploads/2021/06/ZT80PAI@R25RQMJ7B2J.png)

## 聊天消息过滤

开启后 会根据设置的正则表达式匹配服务器发来的消息 匹配成功则不显示该条消息

## 关闭计分板渲染

RT

## 事件系统&定时任务

### 事件系统

#### 指令

`/hs:event subscribe <evnet> <timeTask>`
订阅事件
> event(String):事件名
> timeTask(NbtPath):定时任务json字符串

`/hs:event unsubscribe <event> <name>`
退订事件
> event(String):事件名
> name(String):定时任务名

#### 事件

事件属性可以用`${event.属性名}`来获取属性值

例:`/hs:event subscribe OnGameJoin {startTime:50,name:"加入游戏事件1",executor:{type:sendChatMessage,message:"加入了地址为${event.address}的服务器"}}`

该指令订阅了`OnGameJoin`事件，会在加入本地服务器(本地服务器地址为:127.0.0.1)50客户端tick之后发送消息:`加入了地址为127.0.0.1的服务器`

##### OnGameJoin

加入游戏时触发 需要注意的是群组服内切换其他子服务器也是会触发该事件的

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  name | String  | true  | 服务器名 |
|  address | String  |  true | 服务器IP地址 |

##### OnDisconnected

当从服务器断开连接 不包括主动退出服务器

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  name | String  | true  | 服务器名 |
|  address | String  |  true | 服务器IP地址 |

#### 定时任务

作为指令参数是NbtPath格式

基本上是json5格式 内部会转换成json对象解析

时间单位都是`客户端 tick`(客户端正常一秒为20tick)

|  属性名 | 类型  | 必填  | 默认值|描述|
| ------------ | ------------ | ------------ | ------------ | ------------ |
|name|String|true|-|任务名主键 不能以`#`开头,同事件同时只能存在一个同名任务|
|startTime|int|false|0|第一次执行等待时间|
|cycles|int|false|1|循环次数|
|cyclesTime|int|false|0|第一次执行之后 每次循环之间的时间间隔|
|executor|object|true|-|执行器|

例:
```json5
{
  startTime: 50,
  cycles: 10,
  cyclesTime: 20,
  name: "定时任务",
  executor: {
    type: "sendChatMessage",
    message: "消息发送执行器"
  }
}
```
50tick之后发送消息`"消息发送执行器"` 循环十次 每次间隔20tick

##### 执行器

依然是json对象

|  属性名 | 类型  | 必填  | 默认值|描述|
| ------------ | ------------ | ------------ | ------------ | ------------ |
|type|String|true|-|执行器类型|

不同的type有相应的执行器对象 属性也不一样

例:

```json5
{
  type: "sendChatMessage",
  message: "消息发送执行器"
}
```

###### sendChatMessage

|  属性名 | 类型  | 必填  | 默认值|描述|
| ------------ | ------------ | ------------ | ------------ | ------------ |
|type|String|true|sendChatMessage|发送聊天消息|
|message|String|true|-|发送的消息内容|

###### joinServer

|  属性名 | 类型  | 必填  | 默认值|描述|
| ------------ | ------------ | ------------ | ------------ | ------------ |
|type|String|true|joinServer|加入一个服务器|
|address|String|true|-|加入的服务器IP地址|

------------

# 相关代码

<a id="通过玩家名称获取玩家头"></a>

## 通过玩家名称获取玩家头

```java
/**
 * 通过玩家名称获取头颅
 * @param playerName 玩家名称
 * @return {@link ItemStack} {@link Items#PLAYER_HEAD}
 */
public static ItemStack getPlayerHead(String playerName){
        ItemStack stack=new ItemStack(Items.PLAYER_HEAD);
        NbtCompound tag=new NbtCompound();
        tag.put("SkullOwner",NbtString.of(playerName));
        stack.setTag(tag);
        return stack;
        }
```