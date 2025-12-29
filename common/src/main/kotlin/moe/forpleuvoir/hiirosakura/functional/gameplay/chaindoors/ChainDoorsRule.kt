package moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.world.level.block.Blocks

data class ChainDoorsRule(
    val originDoor: BlockInfoMatcher,
    val chainDoor: BlockInfoMatcher,
    val keyToggleMode: Boolean,
    val strategy: ChainStrategy
) : Serializable {

    companion object : Deserializer<ChainDoorsRule> {

        val MOB_INTERACTABLE_DOORS = ChainDoorsRule(
            BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Tag("minecraft:mob_interactable_doors")),
            BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Tag("minecraft:mob_interactable_doors")),
            false,
            ChainStrategy.Neighborhood.DEFAULT
        )

        val TRAPDOORS = ChainDoorsRule(
            BlockInfoMatcher(
                CompositeMatcher.MatchMode.AllMatch,
                BlockInfoMatchEntry.Tag("minecraft:trapdoors"),
                BlockInfoMatchEntry.Block(Blocks.IRON_TRAPDOOR, MatchEntry.MatchMode.Exclude)
            ),
            BlockInfoMatcher(
                CompositeMatcher.MatchMode.AllMatch,
                BlockInfoMatchEntry.Tag("minecraft:trapdoors"),
                BlockInfoMatchEntry.Block(Blocks.IRON_TRAPDOOR, MatchEntry.MatchMode.Exclude)
            ),
            true,
            ChainStrategy.Recursive.DEFAULT
        )

        override fun deserialization(serializeElement: SerializeElement): ChainDoorsRule =
            serializeElement.checkType<SerializeObject, ChainDoorsRule> {
                ChainDoorsRule(
                    originDoor = BlockInfoMatcher.deserialization(it["origin_door"]!!),
                    chainDoor = BlockInfoMatcher.deserialization(it["chain_door"]!!),
                    keyToggleMode = it["key_toggle_mode"]!!.asBoolean,
                    strategy = ChainStrategy.deserialization(it["strategy"]!!),
                )
            }.getOrThrow()
    }

    override fun serialization(): SerializeElement = serializeObject {
        "origin_door" to originDoor.serialization()
        "chain_door" to chainDoor.serialization()
        "key_toggle_mode" to keyToggleMode
        "strategy" to strategy.serialization()
    }

}