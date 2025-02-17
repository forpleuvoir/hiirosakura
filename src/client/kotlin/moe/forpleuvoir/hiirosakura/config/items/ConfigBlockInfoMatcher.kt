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

class ConfigBlockInfoMatcher(
    override val key: String,
    override val defaultValue: BlockInfoMatcher
) : ConfigBase<BlockInfoMatcher, ConfigBlockInfoMatcher>() {

    override var configValue: BlockInfoMatcher = defaultValue.clone()

    override fun serialization(): SerializeElement = getValue().serialization()

    override fun deserialization(serializeElement: SerializeElement) {
        setValue(BlockInfoMatcher.deserialization(serializeElement))
    }

}

fun ConfigContainer.blockInfoMatcher(
    key: String,
    defaultValue: BlockInfoMatcher
) = addConfig(ConfigBlockInfoMatcher(key, defaultValue))


class ConfigBlockInfoMatcherList(
    key: String,
    defaultValue: List<BlockInfoMatcher> = emptyList()
) : ConfigList<BlockInfoMatcher>(
    key,
    defaultValue,
    {
        it.serialization()
    },
    {
        BlockInfoMatcher.deserialization(it)
    }
)

fun ConfigContainer.blockInfoMatcherList(
    key: String,
    defaultValue: List<BlockInfoMatcher> = emptyList()
) = addConfig(ConfigBlockInfoMatcherList(key, defaultValue))

class ConfigBlockInfoMatcherMap(
    key: String,
    defaultValue: Map<String, BlockInfoMatcher> = emptyMap()
) : ConfigStringKeyMap<BlockInfoMatcher>(
    key,
    defaultValue,
    {
        it.serialization()
    },
    {
        BlockInfoMatcher.deserialization(it)
    }
)

fun ConfigContainer.blockInfoMatcherMap(
    key: String,
    defaultValue: Map<String, BlockInfoMatcher> = emptyMap()
) = addConfig(ConfigBlockInfoMatcherMap(key, defaultValue))

typealias ItemStackBlockInfoPair = Pair<ItemStackMatcher, BlockInfoMatcher>

val ItemStackBlockInfoPair.item get() = first
val ItemStackBlockInfoPair.block get() = second

class ConfigStringItemStackBlockInfoPairMap(
    key: String,
    defaultValue: Map<String, ItemStackBlockInfoPair> = emptyMap()
) : ConfigStringKeyMap<ItemStackBlockInfoPair>(
    key,
    defaultValue,
    { (item, block) ->
        serializeObject {
            "item" to item.serialization()
            "block" to block.serialization()
        }
    }, {
        it.checkType<SerializeObject, ItemStackBlockInfoPair> { obj ->
            ItemStackMatcher.deserialization(obj["item"]!!) to BlockInfoMatcher.deserialization(obj["block"]!!)
        }.getOrThrow()
    }
)

fun ConfigContainer.itemStackBlockInfoMap(
    key: String,
    defaultValue: Map<String, ItemStackBlockInfoPair> = emptyMap()
) = addConfig(ConfigStringItemStackBlockInfoPairMap(key, defaultValue))