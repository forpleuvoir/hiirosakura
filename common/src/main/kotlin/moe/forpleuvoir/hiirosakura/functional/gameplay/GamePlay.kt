package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoors
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean

object GamePlay : ModConfigContainer("gameplay") {

    val autoRebirth by keyBindBoolean("auto_rebirth", false)

    //TODO (不知道为什么添加的需求暂时不考虑实现)槽位锁定物品,优先将指定物品移动到指定槽位,不匹配的物品默认不占用指定槽位,只在物品栏已满的情况下占用,或者给开关强制不允许占用,多余的物品直接丢出
    //TODO 方块放置辅助,tweakeroo已有的功能,仅提供快捷访问的方式
    //TODO 方块破坏辅助,tweakeroo已有的功能,仅提供快捷访问的方式
    //TODO 添加槽位切换功能,预期按下快捷键切换槽位.长按快捷键鼠标滚轮选择槽位.并且提供配置选择可以启用的槽位,以及添加物品匹配器过滤不想切换的物品.
    //TODO 自动丢弃物品
    //TODO 附近实体查看器
    //TODO 过滤没有本地化的声音事件在字幕中显示(或者是过滤指定的或者非指定声音字幕)
    //TODO 对于tweakeroo已有的功能,或许考虑提供脚本访问的修改的方法,允许脚本修改tweakeroo的配置

    init {
        addConfig(ChainDoors)
        addConfig(ItemUseIntercept)
        addConfig(ItemDropIntercept)
        addConfig(BlockBreakProtection)
        addConfig(Gliding)
        addConfig(AutoSwitchElytra)
        addConfig(AutoReplant)
        addConfig(CameraSwitcher)
        addConfig(SoundEventFilter)
    }

}