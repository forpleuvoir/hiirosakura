package moe.forpleuvoir.hiirosakura.functional.chataddons

import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleHandler
import moe.forpleuvoir.nebula.config.ConfigGroup

object ChatConfig : ConfigGroup("chat") {

    init {
        addConfig(ChatInjectHandler)
        addConfig(ChatFilterHandler)
        addConfig(ChatBubbleHandler)
    }

}