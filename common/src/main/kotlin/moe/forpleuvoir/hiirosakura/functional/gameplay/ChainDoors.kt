package moe.forpleuvoir.hiirosakura.functional.gameplay

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import moe.forpleuvoir.hiirosakura.config.items.chainDoorsMapEntryList
import moe.forpleuvoir.hiirosakura.functional.gameplay.ChainDoors.SearchMode.CUBE
import moe.forpleuvoir.hiirosakura.functional.gameplay.ChainDoors.SearchMode.SPHERE
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry.Block
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry.Tag
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher.MatchMode.AllMatch
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry.MatchMode.Exclude
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.config.item.impl.keyCode
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.enum
import moe.forpleuvoir.nebula.config.item.impl.int
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.BlockHitResult
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs
import kotlin.math.pow

object ChainDoors : ModConfigContainer("chain_doors") {

    private val logger = logger()

    private const val MAX_SUPPORTED_RADIUS = 5

    val enabled by keyBindBoolean("enable", false)

    val chainRadius by int("chain_radius", 1, 1, MAX_SUPPORTED_RADIUS)

    val searchMode by enum("search_mode", CUBE)

    val chainLimit by int("chain_limit", 8, 1, 32)

    val keyToggleMode by boolean("key_toggle_mode", false)

    val activationKey by keyCode("activation_key", Keyboard.LEFT_SHIFT)

    val chainSameBlock by boolean("chain_same_block", true)

    val chainSyncState by boolean("chain_sync_state", true)

    val mapping by chainDoorsMapEntryList(
        "mapping", listOf(
            MapEntry(),
            MapEntry(
                BlockInfoMatcher(AllMatch, Tag("minecraft:trapdoors"), Block(Blocks.IRON_TRAPDOOR, Exclude)),
                BlockInfoMatcher(AllMatch, Tag("minecraft:trapdoors"))
            )
        )
    )

    data class MapEntry(
        val clickedDoor: BlockInfoMatcher = BlockInfoMatcher(AllMatch, Tag("minecraft:mob_interactable_doors")),
        val linkedDoor: BlockInfoMatcher = BlockInfoMatcher(AllMatch, Tag("minecraft:mob_interactable_doors")),
    ) : Serializable {

        companion object : Deserializer<MapEntry> {
            override fun deserialization(serializeElement: SerializeElement): MapEntry =
                serializeElement.checkType<SerializeObject, MapEntry> {
                    MapEntry(
                        clickedDoor = BlockInfoMatcher.deserialization(it["clicked_door"]!!),
                        linkedDoor = BlockInfoMatcher.deserialization(it["linked_door"]!!)
                    )
                }.getOrThrow()
        }

        override fun serialization(): SerializeElement = serializeObject {
            "clicked_door" to clickedDoor.serialization()
            "linked_door" to linkedDoor.serialization()
        }

    }

    private var clicking: Boolean = false

    fun isEnabled(): Boolean =
        enabled.value && (keyToggleMode == InputHandler.wasKeyPressed(activationKey))


    @JvmStatic
    fun onClickedDoor(
        player: LocalPlayer,
        level: Level,
        hand: InteractionHand,
        blockResult: BlockHitResult,
        gameMode: MultiPlayerGameMode
    ) {
        if (!isEnabled() || clicking) return

        try {
            clicking = true

            val originPos = blockResult.blockPos
            val originState = level.getBlockState(originPos)
            // 获取当前门的状态,需要取反,因为当前门是已经被交互过的了
            val targetOldState = originState.getOptionalValue(DoorBlock.OPEN).getOrNull()?.let { !it }

            val mappingEntry = mapping.find {
                it.clickedDoor.match(BlockInfo(originState, originPos, blockResult.direction))
            } ?: return

            val linkedDoorMatcher = mappingEntry.linkedDoor

            val clickedSet = ObjectOpenHashSet<BlockPos>(((chainLimit * 2) / 0.75f).toInt() + 1)
            //将当前门和属于折扇门的另一半放进集合中
            clickedSet.add(originPos)
            originPos.getOtherDoorPart(originState)?.let { clickedSet.add(it) }

            originPos.getNearSorted(chainRadius, searchMode)
                .filter { nearbyPos ->
                    if (clickedSet.contains(nearbyPos)) return@filter false
                    val state = level.getBlockState(nearbyPos)

                    if (chainSameBlock && state.block != originState.block) return@filter false

                    if (chainSyncState && targetOldState != null) {
                        val currentOpen = state.getOptionalValue(DoorBlock.OPEN).getOrNull()
                        if (currentOpen != targetOldState) return@filter false
                    }

                    if (linkedDoorMatcher.match(BlockInfo(state, nearbyPos, blockResult.direction))) {
                        //将当前门和属于折扇门的另一半放进集合中
                        clickedSet.add(nearbyPos)
                        nearbyPos.getOtherDoorPart(state)?.let { clickedSet.add(it) }
                        return@filter true
                    }
                    false
                }
                .take(chainLimit)
                .forEach { targetPos ->
                    gameMode.useItemOn(
                        player,
                        hand,
                        BlockHitResult(targetPos.center, blockResult.direction, targetPos, false)
                    )
                }

        } catch (e: Exception) {
            logger.error(e)
        } finally {
            clicking = false
        }
    }


    private val SORTED_OFFSETS: List<BlockPos> = run {
        val offsets = ArrayList<BlockPos>((MAX_SUPPORTED_RADIUS * 2 + 1.0).pow(3).toInt()) // (5*2+1)^3 = 1331

        for (x in -MAX_SUPPORTED_RADIUS..MAX_SUPPORTED_RADIUS) {
            for (y in -MAX_SUPPORTED_RADIUS..MAX_SUPPORTED_RADIUS) {
                for (z in -MAX_SUPPORTED_RADIUS..MAX_SUPPORTED_RADIUS) {
                    if (x == 0 && y == 0 && z == 0) continue
                    offsets.add(BlockPos(x, y, z))
                }
            }
        }
        offsets.sortedBy { it.x * it.x + it.y * it.y + it.z * it.z }
    }

    enum class SearchMode {
        SPHERE, // 球形
        CUBE,    // 立方体
    }

    fun BlockPos.getNearSorted(distance: Int, mode: SearchMode): Sequence<BlockPos> {
        val dSqLimit = (distance * distance).toDouble()
        return SORTED_OFFSETS.asSequence()
            .filter { off ->
                when (mode) {
                    SPHERE -> (off.x * off.x + off.y * off.y + off.z * off.z) <= dSqLimit
                    CUBE   -> abs(off.x) <= distance && abs(off.y) <= distance && abs(off.z) <= distance
                }
            }
            .map { this.offset(it.x, it.y, it.z) }
    }

    private fun BlockPos.getOtherDoorPart(state: BlockState): BlockPos? {
        val half = state.getOptionalValue(DoorBlock.HALF).getOrNull() ?: return null
        val offset = if (half == DoubleBlockHalf.LOWER) 1 else -1
        return this.above(offset)
    }

}
