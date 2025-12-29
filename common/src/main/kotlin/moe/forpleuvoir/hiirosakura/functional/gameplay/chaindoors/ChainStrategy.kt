package moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainStrategy.Neighborhood.Shape.CUBE
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainStrategy.Neighborhood.Shape.SPHERE
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.SerializeObjectBuilder
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DoorBlock
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs
import kotlin.math.pow

sealed class ChainStrategy(
    val type: String,
    val sameBlock: Boolean,
    val syncState: Boolean,
    limit: Int
) : Serializable {

    val limit: Int = limit.coerceIn(1, MAX_LIMIT)

    companion object : Deserializer<ChainStrategy> {
        const val MAX_LIMIT = 32
        override fun deserialization(serializeElement: SerializeElement): ChainStrategy =
            serializeElement.checkType<SerializeObject, ChainStrategy> {
                when (it["type"]!!.asString) {
                    Neighborhood.TYPE -> Neighborhood.deserialization(serializeElement)
                    Recursive.TYPE    -> Recursive.deserialization(serializeElement)
                    else              -> throw IllegalArgumentException("Invalid chain strategy type: ${it["type"]}")
                }
            }.getOrThrow()

    }

    abstract fun SerializeObjectBuilder.strategySerialization()

    override fun serialization(): SerializeElement = serializeObject {
        "type" to type
        "same_block" to sameBlock
        "sync_state" to syncState
        "limit" to limit
        strategySerialization()
    }

    abstract fun collect(origin: BlockPos, level: Level, predicate: (BlockPos) -> Boolean): Sequence<BlockPos>

    class Neighborhood(
        radius: Int,
        val shape: Shape,
        sameBlock: Boolean,
        syncState: Boolean,
        limit: Int
    ) : ChainStrategy(TYPE, sameBlock, syncState, limit) {
        val radius: Int = radius.coerceIn(1, MAX_RADIUS)

        companion object : Deserializer<Neighborhood> {

            val DEFAULT = Neighborhood(1, CUBE, true, syncState = true, limit = 1)

            const val TYPE = "neighborhood"

            val text: Text get() = Text.translatable("${HiiroSakura.MOD_ID}.chain_doors.strategy.type.$TYPE")

            val hoverText: Text get() = Text.translatable("${HiiroSakura.MOD_ID}.chain_doors.strategy.type.$TYPE.comment")

            const val MAX_RADIUS = 5

            private val OFFSET_CACHE: List<BlockPos> = run {
                val offsets = ArrayList<BlockPos>((MAX_RADIUS * 2 + 1.0).pow(3).toInt()) // (5*2+1)^3 = 1331
                for (x in -MAX_RADIUS..MAX_RADIUS) {
                    for (y in -MAX_RADIUS..MAX_RADIUS) {
                        for (z in -MAX_RADIUS..MAX_RADIUS) {
                            if (x == 0 && y == 0 && z == 0) continue
                            offsets.add(BlockPos(x, y, z))
                        }
                    }
                }
                offsets.sortedBy { it.x * it.x + it.y * it.y + it.z * it.z }
            }

            override fun deserialization(serializeElement: SerializeElement): Neighborhood =
                serializeElement.checkType<SerializeObject, Neighborhood> {
                    Neighborhood(
                        it["radius"]!!.asInt,
                        Shape.valueOf(it["shape"]!!.asString),
                        it["same_block"]!!.asBoolean,
                        it["sync_state"]!!.asBoolean,
                        it["limit"]!!.asInt,
                    )
                }.getOrThrow()
        }

        enum class Shape { SPHERE, CUBE }

        override fun collect(origin: BlockPos, level: Level, predicate: (BlockPos) -> Boolean): Sequence<BlockPos> {
            val dSqLimit = (radius * radius).toDouble()
            val originState = level.getBlockState(origin)
            val targetRequiredState = originState.getOptionalValue(DoorBlock.OPEN).getOrNull()?.let { !it }

            return OFFSET_CACHE.asSequence()
                .filter { off ->
                    when (shape) {
                        SPHERE -> (off.x * off.x + off.y * off.y + off.z * off.z) <= dSqLimit
                        CUBE   -> abs(off.x) <= radius && abs(off.y) <= radius && abs(off.z) <= radius
                    }
                }
                .map { origin.offset(it.x, it.y, it.z) }
                .filter { pos ->
                    val state = level.getBlockState(pos)
                    if (sameBlock && state.block != originState.block) return@filter false
                    if (syncState && targetRequiredState != null) {
                        val currentOpen = state.getOptionalValue(DoorBlock.OPEN).getOrNull()
                        if (currentOpen != targetRequiredState) return@filter false
                    }
                    predicate(pos)
                }
                .take(limit)
        }

        override fun SerializeObjectBuilder.strategySerialization() {
            "radius" to radius
            "shape" to shape
        }

    }

    class Recursive(
        sameBlock: Boolean,
        syncState: Boolean,
        limit: Int
    ) : ChainStrategy(TYPE, sameBlock, syncState, limit) {
        companion object : Deserializer<Recursive> {

            val DEFAULT = Recursive(sameBlock = true, syncState = true, limit = 8)

            const val TYPE = "recursive"

            val text: Text get() = Text.translatable("${HiiroSakura.MOD_ID}.chain_doors.strategy.type.$TYPE")

            val hoverText: Text get() = Text.translatable("${HiiroSakura.MOD_ID}.chain_doors.strategy.type.$TYPE.comment")

            override fun deserialization(serializeElement: SerializeElement): Recursive =
                serializeElement.checkType<SerializeObject, Recursive> {
                    Recursive(
                        it["same_block"]!!.asBoolean,
                        it["sync_state"]!!.asBoolean,
                        it["limit"]!!.asInt
                    )
                }.getOrThrow()
        }

        override fun collect(origin: BlockPos, level: Level, predicate: (BlockPos) -> Boolean): Sequence<BlockPos> = sequence {
            val visited = ObjectOpenHashSet<BlockPos>(limit * 2)
            val queue: Deque<BlockPos> = ArrayDeque()

            val originState = level.getBlockState(origin)
            val targetRequiredState = originState.getOptionalValue(DoorBlock.OPEN).getOrNull()?.let { !it }

            queue.add(origin)
            visited.add(origin)
            ChainDoors.getOtherPart(origin, originState)?.let { if (visited.add(it)) queue.add(it) }

            var foundCount = 0
            while (queue.isNotEmpty() && foundCount < limit) {
                val current = queue.poll()

                for (dir in Direction.entries) {
                    val neighbor = current.relative(dir)

                    if (!visited.contains(neighbor)) {
                        val state = level.getBlockState(neighbor)

                        val isMatch = predicate(neighbor)
                        val isSameBlockMatch = !sameBlock || state.block == originState.block

                        if (isMatch && isSameBlockMatch) {
                            visited.add(neighbor)
                            queue.add(neighbor)
                            ChainDoors.getOtherPart(neighbor, state)?.let { if (visited.add(it)) queue.add(it) }
                            val currentOpen = state.getOptionalValue(DoorBlock.OPEN).getOrNull()
                            val isStateSyncMatch = !syncState || (targetRequiredState != null && currentOpen == targetRequiredState)
                            if (isStateSyncMatch) {
                                yield(neighbor)
                                foundCount++
                                if (foundCount >= limit) return@sequence
                            }
                        } else {
                            visited.add(neighbor)
                        }
                    }
                }
            }
        }

        override fun SerializeObjectBuilder.strategySerialization() {}

    }
}



