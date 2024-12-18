package moe.forpleuvoir.hiirosakura.functional.chataddons

import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleHandler
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer

object ChatConfig : ModConfigContainer("chat") {

    init {
        addConfig(ChatInjectHandler.Config)
        addConfig(ChatFilterHandler.Config)
        addConfig(ChatBubbleHandler.Config)
    }


}