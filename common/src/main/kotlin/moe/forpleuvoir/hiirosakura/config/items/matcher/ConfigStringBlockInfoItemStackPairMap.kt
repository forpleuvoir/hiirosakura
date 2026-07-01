package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.ibukigourd.config.item.pair
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configMap
import moe.forpleuvoir.nebula.serialization.codec.Codec

typealias BlockInfoItemStackPair = Pair<BlockInfoMatcher, ItemStackMatcher>

val BlockInfoItemStackPairCodec = Codec.pair(BlockInfoMatcher, ItemStackMatcher)

val BlockInfoItemStackPair.block get() = first
val BlockInfoItemStackPair.item get() = second

context(group: ConfigGroup)
fun configBlockInfoItemStackMap(name: String, defaultValue: Map<String, BlockInfoItemStackPair>) =
    configMap(name, defaultValue, BlockInfoItemStackPairCodec)

