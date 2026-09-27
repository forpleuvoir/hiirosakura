package moe.forpleuvoir.hiirosakura.functional.chataddons

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.config.item.configPairList
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.ui.configwrapper.PairListConfigWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.uiWrapper
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.ConfigSerde
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.hiirosakura.ui.configwrapper.StringPairListConfigWrapper

object ChatInjectHandler : ConfigGroup("chat_inject") {

    val enabled by configToggleKeybind("enable", false)

    val injectMapping by configPairList("inject_mapping", listOf(".*" to "#{message}"), ConfigSerde.of(Codec.string), ConfigSerde.of(Codec.string))
        .uiWrapper { config ->
            StringPairListConfigWrapper(
                config,
                firstHead = { Text(component = HSLang.Chat.injectExp) },
                addFirstLabel = { Text(component = HSLang.Chat.injectExp) },
                secondHead = { Text(component = HSLang.Chat.injectRegex) },
                addSecondLabel = { Text(component = HSLang.Chat.injectRegex) },
            )
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