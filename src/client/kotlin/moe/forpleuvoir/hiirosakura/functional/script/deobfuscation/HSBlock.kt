package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import org.joml.Vector3i
import org.joml.Vector3ic

class HSBlock(private val block: Block) {

    fun getType() = Registries.BLOCK.getId(block).toString()

}

class HSBlockState(private val blockState: BlockState) {

    fun getBlock() = HSBlock(blockState.block)

    fun getLuminance() = blockState.luminance

    fun hasSidedTransparency() = blockState.hasSidedTransparency()

    fun isAir() = blockState.isAir

    fun isBurnable() = blockState.isBurnable

    fun isLiquid() = blockState.isLiquid

    fun isSolid() = blockState.isSolid

    fun getMapColor(x: Int, y: Int, z: Int) = Color(blockState.getMapColor(mc.world, BlockPos(x, y, z)).color).alpha(255)

    fun getMapColor(vector3i: Vector3ic) = getMapColor(vector3i.x(), vector3i.y(), vector3i.z())

    fun getHardness() = blockState.getHardness(mc.world, BlockPos.ORIGIN)

    fun isToolRequired() = blockState.isToolRequired

    fun isOpaque() = blockState.isOpaque

    fun getOpacity() = blockState.opacity

}