package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList

class ConfigItemStackMatcherList(
    key: String,
    defaultValue: List<ItemStackMatcher> = emptyList()
) : ConfigList<ItemStackMatcher>(
    key,
    defaultValue,
    {
        it.serialization()
    },
    {
        ItemStackMatcher.deserialization(it)
    }
)

fun ConfigContainer.itemStackMatcherList(
    key: String,
    defaultValue: List<ItemStackMatcher> = emptyList()
) = addConfig(ConfigItemStackMatcherList(key, defaultValue))
