package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.autoReplantMapEntryList
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.*
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

object AutoReplant : ModConfigContainer("auto_replant") {

    val enable by keyBindBoolean("enable", false)

    val mapping by autoReplantMapEntryList(
        "mapping",
        listOf(
            MapEntry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.WHEAT)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.WHEAT_SEEDS)),
                FARMLAND,
            ),
            MapEntry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.CARROTS)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.CARROT)),
                FARMLAND,
            ),
            MapEntry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.POTATOES)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.POTATO)),
                FARMLAND,
            ),
            MapEntry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.BEETROOTS)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.BEETROOT_SEEDS)),
                FARMLAND,
            ),
            MapEntry(
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.NETHER_WART)),
                ItemStackMatcher(CompositeMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.NETHER_WART)),
                BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.SOUL_SAND)),
            )
        )
    )

    val FARMLAND get() = BlockInfoMatcher(CompositeMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.FARMLAND))

    data class MapEntry(
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
    ) : Serializable {
        companion object : Deserializer<MapEntry> {
            override fun deserialization(serializeElement: SerializeElement): MapEntry {
                return serializeElement.checkType<SerializeObject, MapEntry> {
                    MapEntry(
                        targetBlock = BlockInfoMatcher.deserialization(it["target_block"]!!),
                        replantItem = ItemStackMatcher.deserialization(it["replant_item"]!!),
                        groundBlock = BlockInfoMatcher.deserialization(it["ground_block"]!!)
                    )
                }.getOrThrow()
            }
        }

        override fun serialization(): SerializeElement = serializeObject {
            "target_block" to targetBlock.serialization()
            "replant_item" to replantItem.serialization()
            "ground_block" to groundBlock.serialization()
        }
    }

    @JvmStatic
    fun onBreakBlock(blockState: BlockState, blockPos: BlockPos, player: LocalPlayer) {
        if (!enable.value) return
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