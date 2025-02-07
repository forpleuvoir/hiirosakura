package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.autoReplantMapEntryList
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant.Config.mapping
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.item.impl.stringMap
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

object AutoReplant {

    object Config : ModConfigContainer("auto_replant") {

        val enable by keyBindBoolean("enable", false)

        val mapping by autoReplantMapEntryList(
            "mapping",
            listOf(
                MapEntry(
                    BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.WHEAT)),
                    ItemStackMatcher(MultiMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.WHEAT_SEEDS)),
                    FARMLAND,
                ),
                MapEntry(
                    BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.CARROTS)),
                    ItemStackMatcher(MultiMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.CARROT)),
                    FARMLAND,
                ),
                MapEntry(
                    BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.POTATOES)),
                    ItemStackMatcher(MultiMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.POTATO)),
                    FARMLAND,
                ),
                MapEntry(
                    BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.BEETROOTS)),
                    ItemStackMatcher(MultiMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.BEETROOT_SEEDS)),
                    FARMLAND,
                ),
                MapEntry(
                    BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.NETHER_WART)),
                    ItemStackMatcher(MultiMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.NETHER_WART)),
                    BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.SOUL_SAND)),
                )
            )
        )
    }

    val FARMLAND get() = BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.FARMLAND))

    data class MapEntry(
        val targetBlock: BlockInfoMatcher = BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch, BlockInfoMatchEntry.Block(Blocks.WHEAT)),
        val replantItem: ItemStackMatcher = ItemStackMatcher(MultiMatcher.MatchMode.AllMatch, ItemStackMatchEntry.Item(Items.WHEAT_SEEDS)),
        val groundBlock: BlockInfoMatcher = FARMLAND,
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
    fun onBreakBlock(blockState: BlockState, blockPos: BlockPos, player: ClientPlayerEntity) {
        if (!Config.enable.value) return
        mapping
            .find { entry ->
                entry.targetBlock.match(BlockInfo(blockState, blockPos))
                        && entry.groundBlock.match(BlockInfo(player.world.getBlockState(blockPos.down()), blockPos.down()))
            }
            ?.let { entry ->
                val vec3d = Vec3d(blockPos.x.toDouble(), blockPos.y.toDouble() - 1, blockPos.z.toDouble())
                val pos = BlockPos.ofFloored(player.world.worldBorder.clamp(vec3d))
                //如果副手有对应物品则使用副手交互
                if (entry.replantItem.match(player.offHandStack)) {
                    mc.scheduleStartTick { _, _ ->
                        mc.interactionManager!!.interactBlock(player, Hand.OFF_HAND, BlockHitResult(vec3d, Direction.UP, pos, false))
                    }
                }
                //如果主手有对应物品则直接使用主手交互
                else if (entry.replantItem.match(player.mainHandStack)) {
                    mc.scheduleStartTick { _, _ ->
                        mc.interactionManager!!.interactBlock(player, Hand.MAIN_HAND, BlockHitResult(vec3d, Direction.UP, pos, false))
                    }
                }
                //从背包中寻找匹配的物品
                else {
                    val index = player.swapSlotWithHotbar(entry.replantItem)
                    if (index < 0) return
                    mc.scheduleStartTick { _, _ ->
                        mc.interactionManager!!.interactBlock(player, Hand.MAIN_HAND, BlockHitResult(vec3d, Direction.UP, pos, false))
                        player.swapSlotWithHotbar(index)
                    }
                }
            }
    }

}