package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleServerConfig
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configMap
context(group: ConfigGroup)
fun configChatBubbleServerConfigMap(name: String, defaultValue: Map<String, ChatBubbleServerConfig>) =
    configMap(name, defaultValue, ChatBubbleServerConfig)

//------------ UI Wrapper ------------\\
