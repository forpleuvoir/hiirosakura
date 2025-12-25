package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.matcher.ItemStackMatcherMapWrapper
import moe.forpleuvoir.hiirosakura.config.items.matcher.itemStackMatcherMap
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.userdata.setGuiWrapper
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import net.minecraft.world.item.ItemStack
import kotlin.time.TimeSource

object ItemDropIntercept : ModConfigContainer("item_drop_intercept") {

    val enabled by keyBindBoolean("enable", false)

    val matcher by itemStackMatcherMap("matcher")
        .setGuiWrapper { config, modifier ->
            ItemStackMatcherMapWrapper(
                config,
                modifier,
                keyTableName = HSLang.name,
                matcherTableName = HSLang.handheldItem,
            )
        }

    private var mark = TimeSource.Monotonic.markNow()

    @JvmStatic
    fun canDrop(itemStack: ItemStack): Boolean {
        if (!enabled.value) return true
        return !matcher.any { (key, matcher) ->
            matcher.match(itemStack).apply {
                if (this && mark.elapsedNow() > Toast.SHORT_DURATION) {
                    mark = TimeSource.Monotonic.markNow()
                    Toast.showToast(InlineStyleText(HSLang.itemDropIntercepted(key).plainText))
                }
            }
        }
    }
}

