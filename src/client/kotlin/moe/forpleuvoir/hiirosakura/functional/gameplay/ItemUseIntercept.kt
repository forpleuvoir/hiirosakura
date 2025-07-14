package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.matcher.ConfigStringBlockInfoItemStackPairMapWrapper
import moe.forpleuvoir.hiirosakura.config.items.matcher.block
import moe.forpleuvoir.hiirosakura.config.items.matcher.blockInfoItemStackMap
import moe.forpleuvoir.hiirosakura.config.items.matcher.item
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.userdata.setGuiWrapper
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import net.minecraft.item.ItemStack
import kotlin.time.TimeSource

object ItemUseIntercept : ModConfigContainer("item_use_intercept") {

    val enabled by keyBindBoolean("enable", false)

    val matcher by blockInfoItemStackMap("matcher")
        .setGuiWrapper { config, modifier ->
            ConfigStringBlockInfoItemStackPairMapWrapper(
                config, modifier,
                keyTableName = HSLang.name,
                blockInfoTableName = HSLang.targetBlock,
                itemStackTableName = HSLang.handheldItem,
            )
        }

    private var mark = TimeSource.Monotonic.markNow()

    @JvmStatic
    fun canUse(blockInfo: BlockInfo, itemStack: ItemStack): Boolean {
        if (!enabled.value) return true
        return !matcher.any {
            (it.value.block.match(blockInfo) && it.value.item.match(itemStack)).apply {
                if (this && mark.elapsedNow() > Toast.SHORT_DURATION) {
                    mark = TimeSource.Monotonic.markNow()
                    Toast.showToast(InlineStyleText(HSLang.itemUseIntercepted(it.key).plainText))
                }
            }
        }
    }
}

