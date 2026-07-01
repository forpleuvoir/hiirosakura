package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.ibukigourd.config.item.pair
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configMap
import moe.forpleuvoir.nebula.serialization.codec.Codec

typealias ItemStackBlockInfoPair = Pair<ItemStackMatcher, BlockInfoMatcher>

val ItemStackBlockInfoPairCodec = Codec.pair(ItemStackMatcher, BlockInfoMatcher)

val ItemStackBlockInfoPair.item get() = first
val ItemStackBlockInfoPair.block get() = second

context(group: ConfigGroup)
fun configItemStackBlockInfoMap(name: String, defaultValue: Map<String, ItemStackBlockInfoPair>) =
    configMap(name, defaultValue, ItemStackBlockInfoPairCodec)

//------------ UI Wrapper ------------\\

