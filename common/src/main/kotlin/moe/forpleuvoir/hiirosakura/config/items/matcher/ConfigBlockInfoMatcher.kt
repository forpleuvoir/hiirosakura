package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.config

context(group: ConfigGroup)
fun configBlockInfoMatcher(name: String, defaultValue: BlockInfoMatcher) =
    config(name, defaultValue, BlockInfoMatcher)

//------------ UI Wrapper ------------\\
