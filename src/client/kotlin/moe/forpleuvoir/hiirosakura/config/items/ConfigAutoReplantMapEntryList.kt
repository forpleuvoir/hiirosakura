package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList

class ConfigAutoReplantMapEntryList(
    key: String,
    defaultValue: List<AutoReplant.MapEntry>
) : ConfigList<AutoReplant.MapEntry>(
    key,
    defaultValue,
    {
        it.serialization()
    },
    {
        AutoReplant.MapEntry.deserialization(it)
    }
)

fun ConfigContainer.autoReplantMapEntryList(
    key: String,
    defaultValue: List<AutoReplant.MapEntry>
) = addConfig(ConfigAutoReplantMapEntryList(key, defaultValue))