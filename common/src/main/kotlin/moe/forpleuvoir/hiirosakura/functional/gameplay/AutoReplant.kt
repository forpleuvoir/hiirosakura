package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.configAutoReplantEntryList
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.*
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

object AutoReplant : ConfigGroup("auto_replant") {

    val enable by configToggleKeybind("enable", false)

    val mapping by configAutoReplantEntryList(
        "mapping",
        listOf(
            Entry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.WHEAT)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.WHEAT_SEEDS)),
                FARMLAND,
            ),
            Entry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.CARROTS)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.CARROT)),
                FARMLAND,
            ),
            Entry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.POTATOES)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.POTATO)),
                FARMLAND,
            ),
            Entry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.BEETROOTS)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.BEETROOT_SEEDS)),
                FARMLAND,
            ),
            Entry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.NETHER_WART)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.NETHER_WART)),
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.SOUL_SAND)),
            )
        )
    )

    val FARMLAND get() = BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.FARMLAND))

    data class Entry(
        val targetBlock: BlockInfoMatcher = BlockInfoMatcher(
            CompositeMatcher.MatchMode.AllMatch,
            BlockInfoMatchEntry.Block(mc.targetBlock?.state?.block ?: Blocks.WHEAT)
        ),
        val replantItem: ItemStackMatcher = ItemStackMatcher(
            CompositeMatcher.MatchMode.AllMatch,
            ItemStackMatchEntry.Item(mc.player?.mainHandItem?.item ?: Items.WHEAT_SEEDS)
        ),
        val groundBlock: BlockInfoMatcher = BlockInfoMatcher(
            CompositeMatcher.MatchMode.AllMatch,
            BlockInfoMatchEntry.Block(mc.targetBlock?.let {
                mc.level!!.getBlockState(BlockPos(it.pos.x(), it.pos.y(), it.pos.z()).below()).block
            } ?: Blocks.FARMLAND)
        ),
    ) {
        companion object : Codec<Entry> by Codec.create<Entry>()
            .field<BlockInfoMatcher>("target_block").getter(Entry::targetBlock).codec(BlockInfoMatcher)
            .field<ItemStackMatcher>("replant_item").getter(Entry::replantItem).codec(ItemStackMatcher)
            .field<BlockInfoMatcher>("ground_block").getter(Entry::groundBlock).codec(BlockInfoMatcher)
            .build(::Entry)
    }

    @JvmStatic
    fun onBreakBlock(blockState: BlockState, blockPos: BlockPos, player: LocalPlayer) {
        if (!enable.enabled) return
        mapping
            .find { entry ->
                entry.targetBlock.match(BlockInfo(blockState, blockPos, Direction.EAST))
                        && entry.groundBlock.match(BlockInfo(player.level().getBlockState(blockPos.below()), blockPos.below(), Direction.EAST))
            }
            ?.let { entry ->
                val vec3d = Vec3(blockPos.x.toDouble(), blockPos.y.toDouble() - 1, blockPos.z.toDouble())
                val pos = BlockPos.containing(player.level().worldBorder.clampVec3ToBound(vec3d))
                //如果副手有对应物品则使用副手交互
                if (entry.replantItem.match(player.offhandItem)) {
                    mc.scheduleStartTick { _, _ ->
                        mc.gameMode!!.useItemOn(player, InteractionHand.OFF_HAND, BlockHitResult(vec3d, Direction.UP, pos, false))
                    }
                }
                //如果主手有对应物品则直接使用主手交互
                else if (entry.replantItem.match(player.mainHandItem)) {
                    mc.scheduleStartTick { _, _ ->
                        mc.gameMode!!.useItemOn(player, InteractionHand.MAIN_HAND, BlockHitResult(vec3d, Direction.UP, pos, false))
                    }
                }
                //从背包中寻找匹配的物品
                else {
                    val index = player.swapSlotWithHotbar(entry.replantItem)
                    if (index < 0) return
                    mc.scheduleStartTick { _, _ ->
                        mc.gameMode!!.useItemOn(player, InteractionHand.MAIN_HAND, BlockHitResult(vec3d, Direction.UP, pos, false))
                        player.swapSlotWithHotbar(index)
                    }
                }
            }
    }

}