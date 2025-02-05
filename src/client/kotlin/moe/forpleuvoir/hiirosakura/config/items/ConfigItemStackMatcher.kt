package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.serialization.base.SerializeElement

class ConfigItemStackMatcher(
    override val key: String,
    override val defaultValue: ItemStackMatcher
) : ConfigBase<ItemStackMatcher, ConfigItemStackMatcher>() {

    override var configValue: ItemStackMatcher = defaultValue.clone()

    override fun serialization(): SerializeElement = getValue().serialization()

    override fun deserialization(serializeElement: SerializeElement) {
        setValue(ItemStackMatcher(MultiMatcher.MatchMode.AllMatch).apply { deserialization(serializeElement) })
    }

}