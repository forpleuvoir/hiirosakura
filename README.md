# 关于

<img src ="http://hiirosakura.forpleuvoir.com/icon.png?e=1627280071&token=Vp-SO-HDOpiEverdl1CWRPScr7kDaZdAsXobRmg4:4fsGO4OgRKG1p_ik-FeYFcKoBUI=" width="200">

配置界面快捷键默认`H+S`

基本上是重写了之前的suika mod 大体使用不会有太大区别 

基于 [Fabric](https://fabricmc.net/ "Fabric") 编写

贴出代码基本都是 [Fabric](https://fabricmc.net/ "Fabric") 

开发环境下的 如果代码不是很长的话 尽量贴一下

前置Mod[Malilib](https://github.com/maruohon/malilib "Malilib")

------------

# 指令

客户端指令都是以`hs:`开头的 一般来说指令中用`<>`围起来的部分是一个参数，具体描述格式还没想好

## gamma

`/hs:gmma <value>`
`value(double)` 0~100的数字，可以是小数点 修改游戏伽马值（其实就是亮度） 具体实现代码大概如下

```
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

`/hs:tooltip add <itemStack>(可空) <tip>` 
> 为`<itemStack>`物品添加一条工具提示`<tip>`
> 
> `<itemStack>`参数填入为空时会选择玩家主手上的物品添加 
> 
>`&`字符可以作为格式字符 在给玩家头颅添加时需要输出NBT标签
> 
> 例`/hs:tooltip add minecraft:player_head{"SkullOwner":"dhwuia"} "傻子"`

`/hs:tooltip remove <itemStack>(可空) <index>` 移除
> 删除`<itemStack>`物品下标为`<index>`的一条工具提示
> 
>`<itemStack>`参数填入为空时会选择玩家主手上的物品 数组下标是从`0`开始计算的
> 
>`<index> `为`-1`时 删除该物品的所有工具提示

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

```
@Inject(method = "<init>", at = @At("RETURN"))
public void init(CallbackInfo ci){
    tutorialManager.setStep(TutorialStep.NONE);
}
```

## 创造模式鼠标中键获取玩家头颅

RT依然是没什么用但还是加了并且依旧默认开启不能关闭 通过`Mixin`注入`Entity`的`getPickBlockStack`方法

```
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

![](http://hiirosakura.forpleuvoir.com/%E8%81%8A%E5%A4%A9%E6%98%BE%E7%A4%BA.png?e=1627280071&token=Vp-SO-HDOpiEverdl1CWRPScr7kDaZdAsXobRmg4:_66dnhVNyQPhKQiXomwK1sAN8hQ=)
在服务器时，通过指令 `/hs:scmr set "regex"` 设置正则表达式 

匹配内容必须要有 `name`以及`message`分组 例如原版的消息正则表达式为
`(<(?<name>(.*))>)\s(?<message>.*)`

## 掉落物显示物品名称以及数量

![](http://hiirosakura.forpleuvoir.com/%E6%8E%89%E8%90%BD%E7%89%A9%E5%93%81%E5%90%8D%E6%98%BE%E7%A4%BA.png?e=1627280071&token=Vp-SO-HDOpiEverdl1CWRPScr7kDaZdAsXobRmg4:LsPcVn2-BrSKWXF-h4GprN3bgEs=)

距离相机实体一定距离内会渲染，之后会改成可以自定义的距离 通过`Mixin`注入`ItemEntityRenderer`的`render`方法 

## TNT显示剩余爆炸时间

![](http://hiirosakura.forpleuvoir.com/TNT%E7%88%86%E7%82%B8%E4%BA%8B%E4%BB%B6%E6%98%BE%E7%A4%BA.png?e=1627280071&token=Vp-SO-HDOpiEverdl1CWRPScr7kDaZdAsXobRmg4:Snmo1jocmwNwNQQggQsDUqMf-nA=)

## 切换物品时会显示附魔

![](http://hiirosakura.forpleuvoir.com/%E5%88%87%E6%8D%A2%E7%89%A9%E5%93%81%E6%98%BE%E7%A4%BA%E9%99%84%E9%AD%94.png?e=1627280071&token=Vp-SO-HDOpiEverdl1CWRPScr7kDaZdAsXobRmg4:a579YlcZmMW_ZR2yWtdGxMDfJEI=)

## 自动复活

开启后死亡会跳过死亡画面直接复活

## 快捷聊天消息发送

懒得写介绍了 默认快捷键 添加一条打开试一试 等级`（Integer）`为排序等级
> 值越小排序越靠前
> 为空时不参与排序 且会排在已排序的消息之后

![](http://hiirosakura.forpleuvoir.com/%E5%BF%AB%E6%8D%B7%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF%E5%8F%91%E9%80%81%E6%B7%BB%E5%8A%A0.png?e=1627280071&token=Vp-SO-HDOpiEverdl1CWRPScr7kDaZdAsXobRmg4:cVVggdmo3YYG9WvNj9KS_aWV_ns=)
![](http://hiirosakura.forpleuvoir.com/%E5%BF%AB%E6%8D%B7%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF%E5%8F%91%E9%80%81.png?e=1627280071&token=Vp-SO-HDOpiEverdl1CWRPScr7kDaZdAsXobRmg4:1w2-eIkIQPl5bLHUSaS25xiMsEo=)

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

##### onGameJoin

加入游戏时触发 需要注意的是群组服内切换其他子服务器也是会触发该事件的

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  name | String  | true  | 服务器名 |
|  address | String  |  true | 服务器IP地址 |

##### onServerJoin

加入服务器时触发 群组服内切换不会触发

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  name | String  | false  | 服务器名 |
|  address | String  |  false | 服务器IP地址 |

##### onDisconnected

当从服务器断开连接 不包括主动退出服务器

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  name | String  | true  | 服务器名 |
|  address | String  |  true | 服务器IP地址 |
|  title | String  | true  | 断开连接界面标题 |
|  reason | String  |  true | 断开连接原因 |

##### onDisconnect

当从服务器断开连接 包括主动退出服务器

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  name | String  | true  | 服务器名 |
|  address | String  |  true | 服务器IP地址 |

##### onMessage

受到消息时触发

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  message | String  | false  | 收到的消息文本 |
|  type | int  |  false | 消息类型(0:聊天消息,1:系统消息,2:游戏信息) |

##### onPlayerTick

客户端玩家tick事件 每一次tick都会触发

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  player | ClientPlayerEntity  | false  | 客户端玩家(不推荐使用该对象) |

##### onDeath

客服端玩家死亡触发

|  属性名 | 类型  | 可能为空  | 描述|
| ------------ | ------------ | ------------ | ------------ |
|  showsDeathScreen | boolean  | false  | 是否显示死亡界面 |
|  damageSource | String  |  true | 最近受到的伤害类型 |
|  attacker | String  |  true | 最近受到的伤害的攻击者名称(不是很准确) |

##### doAttack

玩家攻击时触发

##### doItemPick

玩家按下鼠标中键时触发

##### doItemUse

玩家使用物品时触发

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
|executor|String|true|-|执行器,javaScript脚本|

例:
```json5
{
  startTime: 50,
  cycles: 10,
  cyclesTime: 20,
  name: "定时任务",
  executor: "$sendMessage('消息发送执行器');"
}
```
50tick之后发送消息`"消息发送执行器"` 循环十次 每次间隔20tick

##### 执行器

javaScript脚本

提供以下内置对象

###### $event

可能为空

订阅事件时 会提供对应的事件对象

例：如果为`onServerJoin`事件,获取`address`属性可以通过一下方法

>$event.address

###### $hs
提供一部分对外开放的接口

例:
>$hs.doAttack();
> 
>$hs.sendChatMessage("114514");

| 方法 | 参数 |描述 | 
| ------------ | ------------ | ------------ | 
| doAttack() | - |客户端玩家攻击一次|
| doItemUse() | - |客户端玩家使用物品一次|
| doItemPick() | - |模拟客户端玩家按下鼠标中间|
|forward(Int tick)|按住的持续时间(客户端tick)|模拟按下方向前进按键|
|back(Int tick)|按住的持续时间(客户端tick)|模拟按下方向后退按键|
|left(Int tick)|按住的持续时间(客户端tick)|模拟按下方向左按键|
|right(Int tick)|按住的持续时间(客户端tick)|模拟按下方向右按键|
|jump(Int tick)|按住的持续时间(客户端tick)|模拟按下跳跃按键|
|sneak(Int tick)|按住的持续时间(客户端tick)|模拟按下潜行按键|
|joinServer(String address,int maxConnect)|加入的服务器IP地址,单次退出调用该方法的最大次数|加入服务器|
|sendChatMessage(String message)|消息文本|发送聊天消息|

###### $task

属性

| 属性名 | 类型 |
| ------------ | ------------ | 
|data|TimeTaskData|

TimeTaskData

| 方法 | 返回值类型 |返回值 |描述 |
| ------------ | ------------ | ------------ | ------------| 
|startTime()|Integer|第一次执行等待时间|-|
|cycles()|Integer|循环执行次数|时间单位:客户端tick|
|cyclesTime()|Integer|每次循环之间的时间间隔|时间单位:客户端tick|
|name()|String|任务名|-|


方法

| 方法 | 参数 |返回值类型 |返回值|描述 |
| ------------ | ------------ | ------------ | ------------| ------------| 
|getName()|-|String|任务名|-|
|getCounter()|-|Integer|当前执行次数|-|
|isOver()|-|boolean|任务是否已执行完成|所有次数已执行完毕|



------------

# 相关代码

## 通过玩家名称获取玩家头

```
/**
 * 通过玩家名称获取头颅
 * @param playerName 玩家名称
 * @return {@link ItemStack} {@link Items#PLAYER_HEAD}
 */
public static ItemStack getPlayerHead(String playerName){
    ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
    NbtCompound tag = new NbtCompound();
    tag.put("SkullOwner",NbtString.of(playerName));
    stack.setNbt(tag);
    return stack;
}
```