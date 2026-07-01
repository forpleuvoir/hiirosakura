package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configList

context(group: ConfigGroup)
fun configBlockInfoMatcherList(name: String, defaultValue: List<BlockInfoMatcher>) =
    configList(name, defaultValue, BlockInfoMatcher)
