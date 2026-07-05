package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.matcher.block
import moe.forpleuvoir.hiirosakura.config.items.matcher.configBlockInfoItemStackMap
import moe.forpleuvoir.hiirosakura.config.items.matcher.item
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler
import moe.forpleuvoir.nebula.config.ConfigGroup
import net.minecraft.world.item.ItemStack

object ItemUseIntercept : ConfigGroup("item_use_intercept") {

    val enabled by configToggleKeybind("enable", false)

    val matcher by configBlockInfoItemStackMap("matcher", emptyMap())
//        .uiWrapper { config ->
//            ConfigStringBlockInfoItemStackPairMapWrapper(
//                config, modifier,
//                keyTableName = HSLang.name,
//                blockInfoTableName = HSLang.targetBlock,
//                itemStackTableName = HSLang.handheldItem,
//            )
//        }


    @JvmStatic
    fun canUse(blockInfo: BlockInfo, itemStack: ItemStack): Boolean {
        if (!enabled.enabled) return true
        return !matcher.any {
            (it.value.block.match(blockInfo) && it.value.item.match(itemStack)).apply {
                ToastHandler.showContent { Text(InlineStyleText(HSLang.Gameplay.itemUseIntercepted(it.key).plainText)) }
            }
        }
    }
}

