package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configMap

context(group: ConfigGroup)
fun configItemStackMatcherMap(name: String, defaultValue: Map<String, ItemStackMatcher>) =
    configMap(name, defaultValue, ItemStackMatcher)

