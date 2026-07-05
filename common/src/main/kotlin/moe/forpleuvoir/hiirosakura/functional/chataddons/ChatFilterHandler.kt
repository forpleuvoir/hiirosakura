package moe.forpleuvoir.hiirosakura.functional.chataddons

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.ui.configwrapper.StringListConfigWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.uiWrapper
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configList
import moe.forpleuvoir.nebula.serialization.codec.Codec

object ChatFilterHandler : ConfigGroup("chat_filter") {

    val enabled by configToggleKeybind("enable", false)

    val filterMapping by configList("filter_mapping", emptyList(), Codec.string)
        .uiWrapper { config ->
            StringListConfigWrapper(config, {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(HSLang.Chat.filterExp)
                }
            })
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