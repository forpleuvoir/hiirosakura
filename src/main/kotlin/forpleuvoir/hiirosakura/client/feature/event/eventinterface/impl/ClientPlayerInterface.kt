package forpleuvoir.hiirosakura.client.feature.event.eventinterface.impl

import forpleuvoir.hiirosakura.client.feature.event.eventinterface.IClientPlayerInterface
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.registry.Registry

/**
 * 客户端玩家接口实现
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event.eventinterface.impl
 *
 * #class_name ClientPlayerInterface
 *
 * #create_time 2021/8/1 14:47
 */
class ClientPlayerInterface(private val player: ClientPlayerEntity) : IClientPlayerInterface {
	override val uuid: String
		get() = player.uuidAsString
	override val name: String
		get() = player.entityName
	override val health: Float
		get() = player.health
	override val maxHealth: Float
		get() = player.maxHealth
	override val healthProgress: Double
		get() = (health / maxHealth).toDouble()
	override val mainHandItemRegisterId: String
		get() = Registry.ITEM.getId(player.mainHandStack.item).toString()
	override val mainHandItemTranslationKey: String
		get() = player.mainHandStack.item.translationKey
	override val mainHandItemDisplayName: String
		get() = player.mainHandStack.name.string
	override val mainHandItemDurable: Int
		get() = mainHandItemMaxDurable - player.mainHandStack.damage
	override val mainHandItemMaxDurable: Int
		get() = player.mainHandStack.maxDamage
	override val mainHandItemDurableProgress: Double
		get() = mainHandItemDurable.toDouble() / player.mainHandStack.maxDamage
	override val isFireImmune: Boolean
		get() = player.isFireImmune
	override val isOnFire: Boolean
		get() = player.isOnFire
	override val isTouchingWaterOrRain: Boolean
		get() = player.isTouchingWaterOrRain
	override val isSneaking: Boolean
		get() = player.isSneaking
	override val isSwimming: Boolean
		get() = player.isSwimming
	override val isInvisible: Boolean
		get() = player.isInvisible
	override val position: DoubleArray
		get() = doubleArrayOf(player.pos.getX(), player.pos.getY(), player.pos.getX())
	override val posX: Double
		get() = player.pos.getX()
	override val posY: Double
		get() = player.pos.getY()
	override val posZ: Double
		get() = player.pos.getZ()
	override val pitch: Float
		get() = player.pitch
	override val yaw: Float
		get() = player.yaw
	override val speed: Float
		get() = player.speed
	override val experienceLevel: Int
		get() = player.experienceLevel
	override val totalExperience: Int
		get() = player.totalExperience
	override val experienceProgress: Float
		get() = player.experienceProgress
}