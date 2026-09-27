package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.matcher.ItemStackMatcherMapConfigWrapper
import moe.forpleuvoir.hiirosakura.config.items.matcher.configItemStackMatcherMap
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.uiWrapper
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastStrategy
import moe.forpleuvoir.nebula.config.ConfigGroup
import net.minecraft.world.item.ItemStack

object ItemDropIntercept : ConfigGroup("item_drop_intercept") {

    val enabled by configToggleKeybind("enable", false)

    val matcher by configItemStackMatcherMap("matcher", emptyMap())
        .uiWrapper { config ->
            ItemStackMatcherMapConfigWrapper(
                config,
                keyHeader = { Text(component = HSLang.Common.name) },
                valueHeader = { Text(component = HSLang.ItemStackMatcher.handheldItem) },
            )
        }


    @JvmStatic
    fun canDrop(itemStack: ItemStack): Boolean {
        if (!enabled.enabled) return true
        return !matcher.any { (key, matcher) ->
            matcher.match(itemStack).apply {
                if (this) {
                    ToastHandler.showContent(strategy = ToastStrategy.Tagged.Refresh("hs:item_drop_intercept:$key")) {
                        Text(
                            InlineStyleText(
                                HSLang.Gameplay.itemDropIntercepted(
                                    key
                                ).plainText
                            )
                        )
                    }
                }
            }
        }
    }
}

