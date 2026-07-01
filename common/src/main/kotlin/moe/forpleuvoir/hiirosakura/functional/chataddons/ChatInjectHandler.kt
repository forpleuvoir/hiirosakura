package moe.forpleuvoir.hiirosakura.functional.chataddons

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.config.item.configPairList
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.ui.configwrapper.StringPairListConfigWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.uiWrapper
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.ConfigSerde
import moe.forpleuvoir.nebula.serialization.codec.Codec

object ChatInjectHandler : ConfigGroup("chat_inject") {

    val enabled by configToggleKeybind("enable", false)

    val injectMapping by configPairList("inject_mapping", listOf(".*" to "#{message}"), ConfigSerde.of(Codec.string), ConfigSerde.of(Codec.string))
        .uiWrapper { config ->
            StringPairListConfigWrapper(config, { Text(HSLang.chatInjectRegex) }, { Text(HSLang.chatInjectExp) })
        }

    private const val PLACEHOLDERS = "#{message}"

    @JvmStatic
    fun handle(message: String): String {
        if (!enabled.enabled) return message
        injectMapping.map { it.first.toRegex() to it.second }
            .forEach { (regex, exp) ->
                if (message.matches(regex)) {
                    return exp.replace(PLACEHOLDERS, message)
                }
            }
        return message
    }

}