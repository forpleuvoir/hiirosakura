@file:Suppress("unused")

package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.hiirosakura.util.math.toVector
import moe.forpleuvoir.hiirosakura.util.swapSlotWithHotbar
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import java.util.function.Predicate

/**
 * HSEntity 类是对 Minecraft 实体对象的封装，为实体对象提供一系列访问方法和属性。
 * 如果需要处理不同类型的实体，该类及其子类提供了扩展和区分的方法。
 *
 * @property vanilla 被封装的 Minecraft 实体对象。
 */
open class HSEntity(open val vanilla: Entity) {

    companion object {
        @JvmStatic
        fun fromEntity(entity: Entity) = when (entity) {
            is LocalPlayer  -> MainPlayer(entity)
            is Player       -> HSPlayerEntity(entity)
            is LivingEntity -> HSLivingEntity(entity)
            else            -> HSEntity(entity)
        }
    }

    /**
     * 获取实体的名称。
     *
     * @return 表示实体名称的字符串。
     */
    fun getName(): String = vanilla.name.string

    /**
     * 获取实体的显示名。
     * 如果实体具有自定义的显示名称，将返回对应的字符串表示；
     * 否则返回 null。
     *
     * @return 表示实体显示名的字符串，或者当没有自定义显示名时返回 null。
     */
    fun getDisplayName(): String = vanilla.displayName.string

    /**
     * 获取实体的位置。
     *
     * 通过访问 `entity` 的 `pos` 属性获取实体的世界坐标，并将其转换为一个
     * 表示三维坐标的向量对象。
     *
     * @return 当前实体位置的三维向量表示。
     */
    fun getPos() = vanilla.position().toVector()

    /**
     * 获取实体客户端视角的旋转角度。
     *
     * 该方法通过返回一个二维向量来表示旋转信息，其中：
     * - x 表示俯仰角 (Pitch)
     * - y 表示偏航角 (Yaw)
     *
     * 返回的值是基于客户端的状态，通常在需要获取实体旋转角度的相关脚本逻辑中使用。
     *
     * @return 表示实体旋转角度的二维向量 (Vector2fc)，包含俯仰角和偏航角。
     */
    fun getRotation() = vanilla.rotationVector.toVector()

    /**
     * 获取实体的俯仰角度 (pitch)。
     * 俯仰角度是实体在垂直方向上的旋转角度，用于表示实体头部的上下移动程度。
     *
     * @return 实体的俯仰角度，以浮点值表示。
     */
    fun getPitch() = vanilla.xRot

    /**
     * 获取实体的偏航角（Yaw）。
     *
     * 偏航角用于定义实体的水平旋转角度，通常用来描述朝向。
     * 实体的偏航角以度为单位，范围为 -180 到 180。
     *
     * @return 实体的当前偏航角。
     */
    fun getYaw() = vanilla.yRot

    /**
     * 获取实体的眼睛位置并返回一个三维向量。
     *
     * 此方法通过调用实体的 `eyePos` 属性获取眼睛的坐标，并将其转换为 `Vector3dc` 类型。
     * 眼睛位置通常用于表示实体的视线起点或交互位置。
     *
     * @return 实体眼睛位置的三维向量表示。
     */
    fun getEyePos() = vanilla.eyePosition.toVector()

    /**
     * 获取实体的速度。
     *
     * 此方法返回当前实体的速度值，通常由 `entity.speed` 属性提供。
     * 速度是一个数值类型，表示实体的运动速率。
     *
     * @return 实体的速度值。
     */
    fun getSpeed() = vanilla.flyDist

    /**
     * 获取实体对象的 UUID 的字符串形式。
     * UUID（Universally Unique Identifier）是用于唯一标识实体对象的标识符。
     *
     * @return 实体对象的 UUID 的字符串表示。
     */
    fun getUuid(): String = vanilla.stringUUID

    /**
     * 获取实体的唯一标识符 (ID)。
     * 实体 ID 是一个用于表示和区分不同实体的整数值。
     *
     * @return 实体的唯一标识符。
     */
    fun getId() = vanilla.id

    /**
     * 获取实体类型的字符串表示形式。
     *
     * 该方法通过 `Registries.ENTITY_TYPE` 注册表获取当前实体的唯一标识符，
     * 并将其转换为字符串表示形式。通常用于标识实体的实际类型。
     *
     * @return 当前实体类型的字符串表示形式。
     */
    fun getType() = BuiltInRegistries.ENTITY_TYPE.getId(vanilla.type).toString()

    /**
     * 获取当前实体所在世界的唯一标识符。
     * 通过访问实体所在世界的注册键值并将其转换为字符串形式，
     * 返回该实体对应的世界的唯一标识符信息。
     *
     * @return 表示当前实体所在世界的唯一标识符字符串。
     */
    fun getWorld() = vanilla.level().dimension().identifier().toString()

    /**
     * 判断实体是否具有免疫火焰伤害的属性。
     *
     * 此方法检查封装实体的 `isFireImmune` 属性，用于确定该实体是否可以免疫火焰的伤害或影响。
     *
     * @return 如果实体免疫火焰伤害，返回 `true`，否则返回 `false`。
     */
    fun isFireImmune() = vanilla.fireImmune()

    /**
     * 检查实体是否处于燃烧状态。
     *
     * @return 如果实体当前正处于燃烧状态，返回 `true`，否则返回 `false`。
     */
    fun isOnFire() = vanilla.isOnFire

    /**
     * 检查当前实体是否正在接触水或正在被雨淋。
     *
     * 此方法通过实体对象的相关属性判断其是否处于水中
     * 或直接暴露在雨中。通常在需要判断实体环境影响的场景中使用，
     * 比如决定是否触发某些依赖水或雨的效果。
     *
     * @return 如果实体正在接触水或雨，返回 `true`；否则返回 `false`。
     */
    fun isTouchingWaterOrRain() = vanilla.isInWaterOrRain

    /**
     * 判断当前实体是否处于潜行状态。
     *
     * @return 如果实体正在潜行，返回 `true`；否则返回 `false`。
     */
    fun isSneaking() = vanilla.isShiftKeyDown

    /**
     * 检查实体是否正在游泳。
     *
     * 此方法用于确定当前实体是否处于游泳状态。游泳状态通常指实体在水中
     * 以游泳的姿势移动，而不是站立或普通行走状态。
     *
     * @return 如果实体正在游泳，返回 `true`，否则返回 `false`。
     */
    fun isSwimming() = vanilla.isSwimming

    /**
     * 判断当前实体是否存活。
     *
     * 此方法返回一个布尔值，用于表明当前实体的存活状态。如果实体存活，则返回 `true`；如果实体已死亡或不再存在，则返回 `false`。
     *
     * @return 一个布尔值，表示实体的存活状态。
     */
    fun isAlive() = vanilla.isAlive

    /**
     * 检查实体是否处于不可见状态。
     *
     * 此方法调用封装的 Minecraft 实体对象的 `isInvisible` 方法，
     * 用于判断实体当前是否为隐形状态。当实体被标记为隐形时，
     * 该实体不会在客户端渲染，但仍然存在于游戏世界中。
     *
     * @return 如果实体为隐形，则返回 `true`；否则返回 `false`。
     */
    fun sInvisible() = vanilla.isInvisible

    /**
     * 判断实体是否处于无敌状态。
     *
     * 此方法通过调用底层 Minecraft 实体对象的 `isInvulnerable` 方法来确定当前实体是否免疫任何形式的伤害。
     *
     * @return 如果实体为无敌状态，返回 `true`，否则返回 `false`。
     */
    fun isInvulnerable() = vanilla.isInvulnerable

}

/**
 * 代表一个活体实体的封装类。
 *
 * 此类继承自 `HSEntity`，用于扩展 `HSEntity` 的功能，专门处理活体实体的相关属性和操作。
 * 提供了一些实用方法来访问活体实体的健康状态、手持物品以及其他特有属性。
 */
open class HSLivingEntity(override val vanilla: LivingEntity) : HSEntity(vanilla) {

    /**
     * 获取实体的最大生命值。
     *
     * 此方法返回当前实体的最大生命值，用于表示该实体的生命上限。
     *
     * @return 当前实体的最大生命值。
     */
    fun getMaxHealth() = vanilla.maxHealth

    /**
     * 获取实体当前的生命值。
     *
     * 此方法返回与实体关联的当前生命值，该值表示实体的健康状态。
     *
     * @return 实体当前的生命值，通常以浮点数形式表示。
     */
    fun getHealth() = vanilla.health

    /**
     * 计算当前实体的生命值百分比。
     *
     * 此方法返回当前实体的生命值与其最大生命值之间的比值，用于表示实体生命值的百分比状态。
     *
     * @return 一个浮点值，表示当前实体生命值占最大生命值的比例。
     *         值的范围为 0.0 到 1.0，其中 1.0 代表满生命值，0.0 代表实体没有生命值。
     */
    fun getHealthPercent() = vanilla.health / vanilla.maxHealth

    /**
     * 获取实体当前主手持有物品堆的封装对象。
     *
     * 该方法通过访问实体的 `mainHandStack` 属性，并将其封装为 `HSItemStack` 对象以便进一步操作。
     *
     * @return 表示主手持有物品堆的 `HSItemStack` 对象。
     */
    fun getMainHandStack() = HSItemStack(vanilla.mainHandItem)

    /**
     * 获取当前实体副手中的物品堆。
     *
     * 此方法返回封装在 `HSItemStack` 类中的副手物品堆信息，
     * 可以通过 `HSItemStack` 提供的方法访问该物品堆的各种属性，如数量、物品名称、耐久度等。
     *
     * @return 表示副手物品堆的 `HSItemStack` 实例。
     */
    fun getOffHandStack() = HSItemStack(vanilla.offhandItem)

    /**
     * 判断实体是否为幼体。
     *
     * 该方法用于检查当前实例所代表的实体是否为幼体状态。
     *
     * @return 如果实体为幼体，返回 `true`；否则返回 `false`。
     */
    fun isBaby() = vanilla.isBaby

}

/**
 * 代表一个玩家实体的封装类。
 *
 * 此类继承自 `HSLivingEntity`，用于处理与 Minecraft 玩家实体相关的属性与方法。
 * 提供了一些实用方法来访问玩家的创造模式、旁观者模式状态以及与经验相关的属性。
 *
 * @param vanilla 玩家实体对象的封装。
 */
open class HSPlayerEntity(override val vanilla: Player) : HSLivingEntity(vanilla) {

    /**
     * 检查玩家是否处于创造模式。
     *
     * 该方法用于判断当前玩家是否启用了创造模式。
     *
     * @return 如果玩家处于创造模式，返回 `true`，否则返回 `false`。
     */
    fun isCreative() = vanilla.isCreative

    /**
     * 检查玩家是否处于旁观者模式。
     *
     * 玩家在旁观者模式下无法直接与环境互动，并且可以自由飞行穿越方块。
     *
     * @return 如果玩家处于旁观者模式，返回 `true`；否则返回 `false`。
     */
    fun isSpectator() = vanilla.isSpectator

    /**
     * 获取玩家当前的经验等级。
     *
     * 此方法返回封装玩家实体的实例的 `experienceLevel` 属性，
     * 即该玩家当前的经验等级。
     *
     * @return 玩家当前的经验等级，表示为整数值。
     */
    fun getExperienceLevel() = vanilla.experienceLevel

    /**
     * 获取玩家当前总经验值。
     *
     * 此方法返回与玩家相关联的 `totalExperience` 属性的值，
     * 代表玩家累计获得的所有经验值的总和。
     *
     * @return 当前玩家的总经验值，为一个整数值。
     */
    fun getTotalExperience() = vanilla.totalExperience

    /**
     * 获取玩家当前的经验进度。
     *
     * 此方法返回玩家当前经验积累的进度比例，其值为一个介于 0.0 和 1.0 之间的浮点数。
     * 这个值表示玩家当前经验条进度，相对于升级到下一个经验等级所需总经验的比例。
     *
     * @return 玩家当前经验进度的浮点值，范围为 [0.0, 1.0]。
     */
    fun getExperienceProgress() = vanilla.experienceProgress

}

/**
 * 主玩家类，用于封装并扩展 `HSPlayerEntity` 的功能。
 *
 * `MainPlayer` 提供了一系列用于操作和获取与玩家相关的游戏信息的功能。
 * 包括获取玩家准星命中的目标、方块或实体，以及根据条件函数交换玩家当前选择的物品。
 *
 * @param vanilla 表示当前客户端玩家实体的封装对象。
 */
class MainPlayer(override val vanilla: LocalPlayer) : HSPlayerEntity(vanilla) {

    /**
     * 获取当前玩家准星所指向的目标对象，并将其转换为 `HSHitResult` 实例。
     *
     * 该函数通过 `mc.crosshairTarget` 获取玩家准星所命中的目标，
     * 如果目标存在，则使用 `HSHitResult.fromHitResult` 方法将其转换为适当的 `HSHitResult` 实例。
     *
     * @return 如果命中目标，则返回对应的 `HSHitResult` 实例；否则返回 `null`。
     */
    fun getHitResult() = mc.hitResult?.let { HSHitResult.fromHitResult(it) }

    /**
     * 获取玩家准星命中的方块目标。
     *
     * 该方法判断玩家准星是否命中了目标，并检查该命中目标是否为方块类型。
     * 如果命中结果是方块类型，则将其封装为 `HSBlockHitResult` 并返回；否则返回 `null`。
     *
     * @return 如果准星命中的目标是方块类型，则返回对应的 `HSBlockHitResult` 实例；否则返回 `null`。
     */
    fun getHitBlock() = mc.hitResult?.let {
        if (it.type == HitResult.Type.BLOCK) {
            HSBlockHitResult(it as BlockHitResult)
        } else null
    }

    /**
     * 获取玩家准星命中的实体。
     *
     * 此方法检查玩家准星是否命中一个目标，并判断该目标是否为实体类型。
     * 如果命中结果是实体类型，则将其封装为 `HSEntityHitResult` 对象并返回；
     * 否则返回 `null`。
     *
     * @return 如果准星命中目标是实体，则返回对应的 `HSEntityHitResult` 实例；否则返回 `null`。
     */
    fun getHitEntity() = mc.hitResult?.let {
        if (it.type == HitResult.Type.ENTITY) {
            HSEntityHitResult(it as EntityHitResult)
        } else null
    }

    /**
     * 根据条件函数交换玩家当前选择的物品。
     *
     * 此方法会检查当前实体是否是主玩家，如果是主玩家，则调用交换物品栏中物品的逻辑。
     *
     * @param predicate 条件函数，用于判断哪些物品需要被选中交换，接收一个 `HSItemStack` 并返回 `true` 或 `false`。
     */
    fun swapItem(predicate: Predicate<HSItemStack>) {
        vanilla.swapSlotWithHotbar {
            predicate.test(HSItemStack(it))
        }
    }

}