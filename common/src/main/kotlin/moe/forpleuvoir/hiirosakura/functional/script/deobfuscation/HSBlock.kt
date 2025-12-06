package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3ic

/**
 * HSBlock 类用于封装 Minecraft 方块对象，并提供相关的辅助方法。
 *
 * @property block 被封装的 Minecraft 方块对象。
 */
class HSBlock(internal val block: Block) {

    /**
     * 获取方块类型的字符串表示形式。
     * 通过 `Registries.BLOCK` 注册表获取当前方块的唯一标识符，
     * 并将其转换为字符串。
     *
     * @return 当前方块类型的字符串表示形式。
     */
    fun getType() = block.key.toString()

    fun isWaterloggable() = block is SimpleWaterloggedBlock

    fun isCrop() = block is CropBlock

    fun isFluid() = block is LiquidBlock

}

/**
 * HSBlockState 类用于封装 Minecraft 方块状态 (BlockState) 对象，提供对方块状态相关属性与方法的访问。
 *
 * @property blockState 被封装的方块状态对象。
 */
class HSBlockState(internal val blockState: BlockState) {

    /**
     * 获取封装在 HSBlock 实例中的方块对象。
     *
     * @return 表示方块对象的 HSBlock 实例。
     */
    fun getBlock() = HSBlock(blockState.block)

    /**
     * @see HSBlock.getType
     */
    fun getType() = getBlock().getType()

    /**
     * 获取当前方块状态的属性映射。
     *
     * 此方法将方块状态的属性键值对转换为字符串形式的映射。
     * 每个属性的键为属性的名称，而对应的值为属性值的字符串表示形式。
     *
     * @return 表示方块状态属性的键值对映射。
     */
    fun getProperty() = buildMap<String, String> {
        blockState.values.forEach {
            put(it.key.name, Util.getPropertyName(it.key, it.value))
        }
    }

    /**
     * 根据指定的键获取当前方块状态的属性值。
     *
     * 此方法从方块状态的属性映射中查找指定键对应的属性值。
     *
     * @param key 指定的属性键，表示需要获取值的属性名称。
     * @return 对应属性键的属性值，如果未找到对应的值，返回 null。
     */
    fun getProperty(key: String) = getProperty()[key]

    /**
     * 获取当前方块状态关联的标签列表。
     *
     * 该方法提取与当前方块状态关联的所有标签，并将其转换为字符串形式的列表。
     *
     * @return 表示标签的字符串列表。
     */
    fun getTags(): List<String> = blockState.tags.map { it.location.toString() }.toList()

    /**
     * 检查当前方块状态是否包含指定的标签。
     *
     * @param tag 待检查的标签字符串。
     * @return 如果当前方块状态包含指定标签，返回 `true`；否则返回 `false`。
     */
    fun hasTags(tag: String): Boolean = blockState.tags.anyMatch { it.location.toString() == tag }

    /**
     * 获取方块的亮度等级（表示方块发出的光强度）。
     *
     * @return 表示方块亮度的整数值。
     */
    fun getLight() = blockState.lightEmission

    /**
     * 检查方块是否具有侧面透明属性，这决定了方块与光照和可视性的交互方式。
     *
     * @return 如果方块具有侧面透明属性，返回 `true`，否则返回 `false`。
     */
    fun hasSidedTransparency() = blockState.useShapeForLightOcclusion()

    /**
     * 判断方块是否被视为空气类型。
     *
     * @return 如果方块是空气类型，返回 `true`，否则返回 `false`。
     */
    fun isAir() = blockState.isAir

    /**
     * 判断方块是否可燃或可以被点燃。
     *
     * @return 如果方块是可燃的，返回 `true`，否则返回 `false`。
     */
    fun isBurnable() = blockState.ignitedByLava()

    /**
     * 判断方块是否是液体类型（例如水或熔岩）。
     *
     * @return 如果方块是液体，返回 `true`，否则返回 `false`。
     */
    fun isLiquid() = blockState.liquid()

    /**
     * 检查方块是否为实心，这意味着它是不透明且无法穿过的。
     *
     * @return 如果方块是实心的，返回 `true`，否则返回 `false`。
     */
    fun isSolid() = blockState.isSolid

    /**
     * 获取指定坐标处方块的地图颜色，并将颜色的 alpha 通道设置为 255。
     *
     * @param x 方块的 x 坐标。
     * @param y 方块的 y 坐标。
     * @param z 方块的 z 坐标。
     * @return 表示地图颜色的 Color 对象。
     */
    fun getMapColor(x: Int, y: Int, z: Int) = Color(blockState.getMapColor(mc.level!!, BlockPos(x, y, z)).col).alpha(255)

    /**
     * 获取指定 `Vector3ic` 指定位置的方块地图颜色。
     *
     * @param vector3i 表示方块位置的三维整型向量。
     * @return 表示地图颜色的 Color 对象。
     */
    fun getMapColor(vector3i: Vector3ic) = getMapColor(vector3i.x(), vector3i.y(), vector3i.z())

    /**
     * 获取方块的硬度，用于确定破坏方块所需的时间。
     *
     * @return 表示方块硬度的浮点值。
     */
    fun getHardness() = blockState.getDestroySpeed(mc.level!!, BlockPos.ZERO)

    /**
     * 检查是否需要工具来有效地采集或破坏该方块。
     *
     * @return 如果需要工具，返回 `true`，否则返回 `false`。
     */
    fun isToolRequired() = blockState.requiresCorrectToolForDrops()

    /**
     * 判断方块是否为不透明，这表示光线不能通过方块。
     *
     * @return 如果方块是不透明的，返回 `true`，否则返回 `false`。
     */
    fun isOpaque() = blockState.canOcclude()

    /**
     * 获取方块的不透明度等级，用于显示该方块阻挡光线的程度。
     *
     * @return 表示方块不透明度的整数值。
     */
    fun getOpacity() = blockState.lightBlock
}