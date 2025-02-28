package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean

object GamePlay : ModConfigContainer("gameplay") {

    val autoRebirth by keyBindBoolean("auto_rebirth", false)

    init {
        addConfig(ItemUseIntercept)
        addConfig(BlockBreakProtection)
        addConfig(Gliding)
        addConfig(AutoSwitchElytra)
        addConfig(AutoReplant)
        addConfig(CameraSwitcher)
        addConfig(SoundEventFilter)
    }

}