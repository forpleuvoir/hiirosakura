package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configMap

context(group: ConfigGroup)
fun configBlockInfoMatcherMap(name: String, defaultValue: Map<String,BlockInfoMatcher>) =
    configMap(name, defaultValue, BlockInfoMatcher)


//------------ UI Wrapper ------------\\

