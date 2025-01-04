@file:Suppress("unused")

package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.hiirosakura.util.math.toVector
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult

open class HSHitResult(protected open val result: HitResult) {
    companion object {
        @JvmStatic
        fun fromHitResult(result: HitResult) = when (result) {
            is BlockHitResult  -> HSBlockHitResult(result)
            is EntityHitResult -> HSEntityHitResult(result)
            else               -> HSHitResult(result)
        }
    }

    open fun getType() = HitResult.Type.MISS.name

    fun getPos() = result.pos.toVector()

    open fun isEntity(): Boolean = false

    open fun isBlock(): Boolean = false

}

class HSEntityHitResult(override val result: EntityHitResult) : HSHitResult(result) {

    override fun isEntity(): Boolean = true

    override fun getType(): String = result.type.name

    private val entity = HSEntity(result.entity)

    fun getEntity() = entity

}

class HSBlockHitResult(override val result: BlockHitResult) : HSHitResult(result) {

    override fun isBlock(): Boolean = true

    fun getBlockPos() = result.blockPos.toVector()

    fun getSide(): String = result.side.name

    override fun getType(): String = result.type.name

    fun getInsideBlock() = result.isInsideBlock

    fun getAgainstWorldBorder() = result.isAgainstWorldBorder

    fun getBlockState() = mc.world?.getBlockState(result.blockPos)?.let { HSBlockState(it) }

    fun getBlock() = getBlockState()?.getBlock()

}