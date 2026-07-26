package moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainStrategy.Neighborhood.Shape.CUBE
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainStrategy.Neighborhood.Shape.SPHERE
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.enum
import moe.forpleuvoir.nebula.serialization.extensions.requireString
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DoorBlock
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs
import kotlin.math.pow

@Immutable
sealed class ChainStrategy(
    val type: String,
    val sameBlock: Boolean,
    val syncState: Boolean,
    limit: Int
) {

    val limit: Int = limit.coerceIn(1, MAX_LIMIT)

    companion object : Codec<ChainStrategy> {

        const val MAX_LIMIT = 32

        const val MAX_RADIUS = 5

        const val NEIGHBORHOOD_TYPE = "neighborhood"

        const val RECURSIVE_TYPE = "recursive"

        override fun deserialization(data: SerializeElement): Result<ChainStrategy> = DeserializationException.runCatching {
            data.checkType<SerializeObject, ChainStrategy> {
                when (it.requireString("type")) {
                    NEIGHBORHOOD_TYPE -> Neighborhood.deserialization(data).getOrThrow()
                    RECURSIVE_TYPE    -> Recursive.deserialization(data).getOrThrow()
                    else              -> throw IllegalArgumentException("Invalid chain strategy type: ${it["type"]}")
                }
            }
        }

        override fun serialization(target: ChainStrategy): SerializeElement = when (target) {
            is Neighborhood -> Neighborhood.serialization(target)
            is Recursive    -> Recursive.serialization(target)
        }

    }

    abstract fun collect(origin: BlockPos, level: Level, predicate: (BlockPos) -> Boolean): Sequence<BlockPos>

    @Immutable
    class Neighborhood(
        radius: Int,
        val shape: Shape,
        sameBlock: Boolean,
        syncState: Boolean,
        limit: Int
    ) : ChainStrategy(NEIGHBORHOOD_TYPE, sameBlock, syncState, limit) {
        val radius: Int = radius.coerceIn(1, MAX_RADIUS)

        companion object : Codec<Neighborhood> by Codec.create<Neighborhood>()
            .field(Neighborhood::type).default(NEIGHBORHOOD_TYPE).codec(Codec.string)
            .field(Neighborhood::radius).default(1).codec(Codec.int(1..MAX_RADIUS))
            .field(Neighborhood::shape).default(CUBE).codec(Codec.enum())
            .field(Neighborhood::sameBlock).default(true).codec(Codec.boolean)
            .field(Neighborhood::syncState).default(true).codec(Codec.boolean)
            .field(Neighborhood::limit).default(1).codec(Codec.int(1..MAX_LIMIT))
            .build({ _, radius, shape, sameBlock, syncState, limit -> Neighborhood(radius, shape, sameBlock, syncState, limit) }) {

            val DEFAULT = Neighborhood(1, CUBE, true, syncState = true, limit = 1)

            val text: Text get() = Text.translatable("${HiiroSakura.MOD_ID}.chain_doors.strategy.type.$NEIGHBORHOOD_TYPE")

            val hoverText: Text get() = Text.translatable("${HiiroSakura.MOD_ID}.chain_doors.strategy.type.$NEIGHBORHOOD_TYPE.comment")

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

        }

        enum class Shape {
            SPHERE, CUBE;

            companion object : Codec<Shape> by Codec.enum()
        }

        fun copy(
            radius: Int = this.radius,
            shape: Shape = this.shape,
            sameBlock: Boolean = this.sameBlock,
            syncState: Boolean = this.syncState,
            limit: Int = this.limit
        ): Neighborhood = Neighborhood(radius, shape, sameBlock, syncState, limit)

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

    }

    @Immutable
    class Recursive(
        sameBlock: Boolean,
        syncState: Boolean,
        limit: Int
    ) : ChainStrategy(RECURSIVE_TYPE, sameBlock, syncState, limit) {

        companion object : Codec<Recursive> by Codec.create<Recursive>()
            .field(Recursive::type).default("neighborhood").codec(Codec.string)
            .field(Recursive::sameBlock).default(true).codec(Codec.boolean)
            .field(Recursive::syncState).default(true).codec(Codec.boolean)
            .field(Recursive::limit).default(8).codec(Codec.int(1..MAX_LIMIT))
            .build({ _, sameBlock, syncState, limit -> Recursive(sameBlock, syncState, limit) }) {

            val DEFAULT = Recursive(sameBlock = true, syncState = true, limit = 8)

            val text: Text get() = Text.translatable("${HiiroSakura.MOD_ID}.chain_doors.strategy.type.$RECURSIVE_TYPE")

            val hoverText: Text get() = Text.translatable("${HiiroSakura.MOD_ID}.chain_doors.strategy.type.$RECURSIVE_TYPE.comment")

        }

        fun copy(
            sameBlock: Boolean = this.sameBlock,
            syncState: Boolean = this.syncState,
            limit: Int = this.limit
        ) = Recursive(sameBlock, syncState, limit)

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

    }
}



