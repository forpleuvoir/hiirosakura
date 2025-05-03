package moe.forpleuvoir.hiirosakura.functional.chataddons

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.userdata.setGuiWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.StringListConfigWrapper
import moe.forpleuvoir.ibukigourd.text.McText
import moe.forpleuvoir.nebula.config.item.impl.stringList

object ChatFilterHandler : ModConfigContainer("chat_filter") {

    val enabled by keyBindBoolean("enable", false)

    val filterMapping by stringList("filter_mapping", emptyList())
        .setGuiWrapper { config, modifier ->
            StringListConfigWrapper(config, modifier, HSLang.chatFilterExp)
        }

    @JvmStatic
    fun shouldFilter(message: McText): Boolean {
        if (!enabled.value) return false
        val string = message.string
        filterMapping.map { it.toRegex() }.forEach { regex ->
            if (string.matches(regex)) {
                return true
            }
        }
        return false
    }


}