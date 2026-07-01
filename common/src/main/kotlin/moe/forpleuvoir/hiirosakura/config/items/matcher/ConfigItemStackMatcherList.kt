package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configList

context(group: ConfigGroup)
fun configItemStackMatcherList(name: String, defaultValue: List<ItemStackMatcher>) =
    configList(name, defaultValue, ItemStackMatcher)

