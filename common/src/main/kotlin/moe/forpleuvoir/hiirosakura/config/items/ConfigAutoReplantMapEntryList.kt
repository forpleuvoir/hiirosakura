package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configList

context(group: ConfigGroup)
fun configAutoReplantEntryList(name: String, defaultValue: List<AutoReplant.Entry>) = configList(name, defaultValue, AutoReplant.Entry)


//------------ UI Wrapper ------------\\


