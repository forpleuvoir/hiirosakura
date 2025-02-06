package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList
import moe.forpleuvoir.nebula.serialization.base.SerializeElement

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