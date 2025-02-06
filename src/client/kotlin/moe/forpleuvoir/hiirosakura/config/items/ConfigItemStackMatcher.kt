package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList
import moe.forpleuvoir.nebula.serialization.base.SerializeElement

class ConfigItemStackMatcher(
    override val key: String,
    override val defaultValue: ItemStackMatcher
) : ConfigBase<ItemStackMatcher, ConfigItemStackMatcher>() {

    override var configValue: ItemStackMatcher = defaultValue.clone()

    override fun serialization(): SerializeElement = getValue().serialization()

    override fun deserialization(serializeElement: SerializeElement) {
        setValue(ItemStackMatcher.deserialization(serializeElement))
    }

}

fun ConfigContainer.itemStackMatcher(
    key: String,
    defaultValue: ItemStackMatcher
) = addConfig(ConfigItemStackMatcher(key, defaultValue))

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