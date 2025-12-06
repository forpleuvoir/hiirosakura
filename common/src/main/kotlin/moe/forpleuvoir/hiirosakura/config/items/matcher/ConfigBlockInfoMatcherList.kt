package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList

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