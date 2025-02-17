package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.block
import moe.forpleuvoir.hiirosakura.config.items.blockInfoItemStackMap
import moe.forpleuvoir.hiirosakura.config.items.item
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import net.minecraft.item.ItemStack
import kotlin.time.TimeSource

object ItemUseIntercept : ModConfigContainer("item_use_intercept") {

    val enabled by keyBindBoolean("enable", false)

    val matcher by blockInfoItemStackMap("matcher")

    @JvmStatic
    fun canUse(blockInfo: BlockInfo, itemStack: ItemStack): Boolean {
        if (!enabled.value) return true
        return !matcher.any {
            (it.value.block.match(blockInfo) && it.value.item.match(itemStack)).apply {
                if (this && mark.elapsedNow() > Toast.SHORT_DURATION) {
                    mark = TimeSource.Monotonic.markNow()
                    Toast.showToast(HSLang.itemUseIntercepted(it.key))
                }
            }
        }
    }
}

private var mark = TimeSource.Monotonic.markNow()
