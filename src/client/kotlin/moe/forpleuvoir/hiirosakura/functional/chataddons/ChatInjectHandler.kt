package moe.forpleuvoir.hiirosakura.functional.chataddons

import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.stringPairList

object ChatInjectHandler {

    object Config : ModConfigContainer("chat_inject") {

        val enabled by keyBindBoolean("enable", false)

        val injectMapping by stringPairList("inject_mapping", listOf(".*" to "#{message}"))

    }

    private const val PLACEHOLDERS = "#{message}"


    @JvmStatic
    fun handle(message: String): String {
        if (!Config.enabled.value) return message
        Config.injectMapping.map { it.first.toRegex() to it.second }
            .forEach { (regex, exp) ->
                if (message.matches(regex)) {
                    return exp.replace(PLACEHOLDERS, message)
                }
            }
        return message
    }

}