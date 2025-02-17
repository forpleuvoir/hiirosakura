package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringKeyMap
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

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

class ConfigItemStackMatcherMap(
    key: String,
    defaultValue: Map<String, ItemStackMatcher> = emptyMap()
) : ConfigStringKeyMap<ItemStackMatcher>(
    key,
    defaultValue,
    {
        it.serialization()
    },
    {
        ItemStackMatcher.deserialization(it)
    }
)

fun ConfigContainer.itemStackMatcherMap(
    key: String,
    defaultValue: Map<String, ItemStackMatcher> = emptyMap()
) = addConfig(ConfigItemStackMatcherMap(key, defaultValue))


typealias BlockInfoItemStackPair = Pair<BlockInfoMatcher, ItemStackMatcher>

val BlockInfoItemStackPair.block get() = first
val BlockInfoItemStackPair.item get() = second

class ConfigStringBlockInfoItemStackPairMap(
    key: String,
    defaultValue: Map<String, BlockInfoItemStackPair> = emptyMap()
) : ConfigStringKeyMap<BlockInfoItemStackPair>(
    key,
    defaultValue,
    { (block, item) ->
        serializeObject {
            "block" to block.serialization()
            "item" to item.serialization()
        }
    }, {
        it.checkType<SerializeObject, BlockInfoItemStackPair> { obj ->
            BlockInfoMatcher.deserialization(obj["block"]!!) to ItemStackMatcher.deserialization(obj["item"]!!)
        }.getOrThrow()
    }
)

fun ConfigContainer.blockInfoItemStackMap(
    key: String,
    defaultValue: Map<String, BlockInfoItemStackPair> = emptyMap()
) = addConfig(ConfigStringBlockInfoItemStackPairMap(key, defaultValue))