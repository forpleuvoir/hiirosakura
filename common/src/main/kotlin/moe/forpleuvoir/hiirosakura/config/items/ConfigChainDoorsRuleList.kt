package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoorsRule
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configList

context(group: ConfigGroup)
fun configChainDoorsRuleList(name: String, defaultValue: List<ChainDoorsRule>) = configList(name, defaultValue, ChainDoorsRule)

//------------ GUI Wrapper ------------\\
