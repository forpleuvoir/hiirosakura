package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.itemStackMatcherMap
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import net.minecraft.item.ItemStack
import kotlin.time.TimeSource

object ItemUseIntercept : ModConfigContainer("item_use_intercept") {

    val enabled by keyBindBoolean("enable", false)

    val matcher by itemStackMatcherMap("matcher")

    @JvmStatic
    fun canUse(itemStack: ItemStack): Boolean {
        if (!enabled.value) return true
        return !matcher.any {
            it.value.match(itemStack).apply {
                if (this && mark.elapsedNow() > Toast.SHORT_DURATION) {
                    mark = TimeSource.Monotonic.markNow()
                    Toast.showToast(HSLang.itemUseIntercepted(it.key))
                }
            }
        }
    }
}

private var mark = TimeSource.Monotonic.markNow()
