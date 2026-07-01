package moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.minecraft.world.level.block.Blocks

data class ChainDoorsRule(
    val originDoor: BlockInfoMatcher,
    val chainDoor: BlockInfoMatcher,
    val keyToggleMode: Boolean,
    val strategy: ChainStrategy
) {

    companion object : Codec<ChainDoorsRule> by Codec.create<ChainDoorsRule>()
        .field<BlockInfoMatcher>("origin_door").getter(ChainDoorsRule::originDoor).codec(BlockInfoMatcher)
        .field<BlockInfoMatcher>("chain_door").getter(ChainDoorsRule::chainDoor).codec(BlockInfoMatcher)
        .field<Boolean>("key_toggle_mode").getter(ChainDoorsRule::keyToggleMode).codec(Codec.boolean)
        .field<ChainStrategy>("strategy").getter(ChainDoorsRule::strategy).codec(ChainStrategy)
        .build(::ChainDoorsRule) {

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

    }

}