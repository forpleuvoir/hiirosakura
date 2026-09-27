package moe.forpleuvoir.hiirosakura.functional.chataddons

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.ui.configwrapper.uiWrapper
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configList
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.hiirosakura.ui.configwrapper.StringListConfigWrapper

object ChatFilterHandler : ConfigGroup("chat_filter") {

    val enabled by configToggleKeybind("enable", false)

    val filterMapping by configList("filter_mapping", emptyList(), Codec.string)
        .uiWrapper { config ->
            StringListConfigWrapper(
                config,
                addContentLabel = {
                    Text(component = HSLang.Chat.filterExp)
                },
                contentHeader = {
                    Text(component = HSLang.Chat.filterExp)
                }
            )
        }

    @JvmStatic
    fun shouldFilter(message: Text): Boolean {
        if (!enabled.enabled) return false
        val string = message.string
        filterMapping.map { it.toRegex() }.forEach { regex ->
            if (string.matches(regex)) {
                return true
            }
        }
        return false
    }


}