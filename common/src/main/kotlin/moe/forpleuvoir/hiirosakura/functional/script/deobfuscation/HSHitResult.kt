@file:Suppress("unused")

package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.hiirosakura.util.math.toVector
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.Direction
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult

/**
 * HSHitResult 类表示一次命中检测结果的通用封装。
 *
 * 该类提供了访问命中位置以及命中类型的基本方法，支持不同类型的命中结果（例如方块命中和实体命中）的进一步扩展。
 *
 * @property result 内部封装的 Minecraft 命中结果 (HitResult) 对象。
 * @constructor 接受一个类型为 `HitResult` 的参数，用于初始化命中结果。
 */
open class HSHitResult(internal open val result: HitResult) {

    companion object {
        /**
         * 根据给定的 `HitResult` 对象创建相应类型的 `HSHitResult` 实例。
         *
         * @param result 一个 `HitResult` 对象，它表示游戏中的一次射线检测结果。
         *               可以是 `BlockHitResult`（表示射线命中方块的结果）、
         *               `EntityHitResult`（表示射线命中实体的结果）或其他类型的结果。
         *
         * @return 如果 `result` 是 `BlockHitResult`，返回 `HSBlockHitResult`；
         *         如果 `result` 是 `EntityHitResult`，返回 `HSEntityHitResult`；
         *         否则返回通用的 `HSHitResult` 对象。
         */
        @JvmStatic
        fun fromHitResult(result: HitResult) = when (result) {
            is BlockHitResult  -> HSBlockHitResult(result)
            is EntityHitResult -> HSEntityHitResult(result)
            else               -> HSHitResult(result)
        }
    }

    /**
     * 获取当前命中结果的类型字符串。
     *
     * 该方法返回表示 `HitResult` 类型的字符串，默认返回 `MISS`，表示未命中状态。
     *
     * @return 当前命中结果类型的字符串表示。
     */
    open fun getType() = HitResult.Type.MISS.name

    /**
     * 获取命中结果的位置并将其转换为三维向量表示形式。
     *
     * 此方法从字段 `result` 中提取命中位置 (`pos`)，
     * 并通过调用 `toVector` 方法将其转换为 `Vector3dc` 类型的对象，
     * 以便于更方便地进行三维空间相关操作。
     *
     * @return 表示命中位置的 `Vector3dc` 对象。
     */
    fun getPos() = result.location.toVector()

    /**
     * 判断当前命中对象是否为实体。
     *
     * @return 如果命中的是实体，返回 `true`；否则返回 `false`。
     */
    open fun isEntity(): Boolean = false

    /**
     * 判断是否为方块类型的碰撞结果。
     *
     * @return 如果是方块类型的碰撞结果，返回 `true`；否则返回 `false`。
     */
    open fun isBlock(): Boolean = false

}

/**
 * 表示一个实体命中的结果。
 * 此类继承自 HSHitResult，用于封装 Minecraft 中与实体相关的命中结果。
 *
 * @property result 被包装的原始 EntityHitResult 对象。
 */
class HSEntityHitResult(override val result: EntityHitResult) : HSHitResult(result) {

    /**
     * 检查当前命中结果是否是实体类型。
     *
     * @return 如果命中类型是实体，返回 `true`，否则返回 `false`。
     */
    override fun isEntity(): Boolean = true

    /**
     * 获取当前命中结果类型的名称。
     *
     * @return 命中结果类型的字符串表示形式。
     */
    override fun getType(): String = result.type.name

    /**
     * 表示命中的实体对象。
     * 将传入的实体命中结果中的实体封装为 HSEntity 类型。
     */
    private val entity = HSEntity(result.entity)

    /**
     * 返回一个封装的实体（Entity）的实例。
     *
     * 此方法提供对 `entity` 属性的访问，用于获取与当前实体命中结果相关联的实体对象。
     *
     * @return 表示封装实体的 `HSEntity` 对象。
     */
    fun getEntity() = entity

}

/**
 * HSBlockHitResult 类是对 BlockHitResult 对象的封装，提供了一系列用于获取方块击中信息的实用方法。
 *
 * 构造该类时需要传入原始的 BlockHitResult 实例，并通过拓展功能进一步处理 Minecraft 中方块的交互与属性访问。
 */
class HSBlockHitResult(override val result: BlockHitResult) : HSHitResult(result) {

    /**
     * 判断当前命中结果是否为方块。
     *
     * @return 如果命中结果为方块，返回 `true`。
     */
    override fun isBlock(): Boolean = true

    /**
     * 获取当前方块命中的位置，并将其转换为三维整型向量表示形式。
     *
     * @return 三维整型向量，表示命中的方块位置。
     */
    fun getBlockPos() = result.blockPos.toVector()

    /**
     * 获取当前方块交互的面（如顶部、底部、侧面等）的名称。
     *
     * @return 一个字符串，表示方块交互面（例如：UP、DOWN、NORTH、SOUTH、WEST、EAST）。
     */
    fun getSide(): String = result.direction.name

    /**
     * 返回命中结果的类型名称。
     *
     * @return 表示命中结果类型的字符串，例如 "BLOCK" 或其他可能的类型名称。
     */
    override fun getType(): String = result.type.name

    /**
     * 检查当前方块是否位于方块内的逻辑。
     *
     * @return 如果当前方块位于方块内，返回 `true`；否则返回 `false`。
     */
    fun isInside() = result.isInside

    /**
     * 判断命中方块是否靠近世界边界。
     *
     * @return 如果命中方块靠近世界边界，则返回 `true`，否则返回 `false`。
     */
    fun isWorldBorderHit() = result.isWorldBorderHit

    /**
     * 获取当前命中结果所对应位置的方块状态。
     *
     * 该方法尝试从当前世界对象中获取指定方块位置的方块状态，并将其封装为 HSBlockState 对象。
     * 如果无法获得方块状态（例如当前世界对象为 `null`），则返回 `null`。
     *
     * @return 表示方块状态的 HSBlockState 对象，或 `null` 如果无法获取。
     */
    fun getBlockState() = mc.level?.getBlockState(result.blockPos)?.let { HSBlockState(it) }

    /**
     * 返回与当前命中结果位置相关联的方块实例。
     *
     * 此方法首先检索命中结果位置对应的方块状态（BlockState），
     * 然后获取封装在 HSBlock 实例中的具体方块对象。
     *
     * @return HSBlock 实例，表示相关联的方块对象；如果方块状态不可用，则返回 null。
     */
    fun getBlock() = getBlockState()?.getBlock()

    /**
     * 根据指定的方向和偏移量获取目标方块的状态。
     *
     * @param direction 方块偏移的方向（如 "north", "south", "east", "west", "up", "down"）。
     *                  该参数不区分大小写。
     * @param i 偏移量的距离，表示沿指定方向移动的方块数。
     * @return 表示目标方块状态的 BlockState 对象。
     */
    fun offset(direction: String, i: Int): HSBlockState {
        return HSBlockState(mc.level!!.getBlockState(result.blockPos.relative(Direction.byName(direction.lowercase())!!)))
    }

}