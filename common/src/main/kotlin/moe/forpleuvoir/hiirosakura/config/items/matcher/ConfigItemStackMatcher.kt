package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.config

context(group: ConfigGroup)
fun configItemStackMatcher(name: String, defaultValue: ItemStackMatcher) = config(name, defaultValue, ItemStackMatcher)

//------------ UI Wrapper ------------\\

