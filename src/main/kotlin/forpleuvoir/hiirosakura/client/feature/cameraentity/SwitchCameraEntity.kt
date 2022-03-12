package forpleuvoir.hiirosakura.client.feature.cameraentity

import forpleuvoir.ibuki_gourd.event.EventBus
import forpleuvoir.ibuki_gourd.event.events.ClientEndTickEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import java.util.*
import java.util.function.Consumer

/**
 * 切换相机实体
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.cameraentity
 *
 * 文件名 SwitchCameraEntity
 *
 * 创建时间 2021/6/22 21:12
 */
object SwitchCameraEntity {
	private var targetEntity: Entity? = null
	private val client = MinecraftClient.getInstance()

	init {
		EventBus.subscribe<ClientEndTickEvent> {
			if (client.options.sneakKey.wasPressed()) {
				if (client.getCameraEntity() == targetEntity) client.setCameraEntity(
					client.player
				)
			}
		}
	}

	@JvmStatic
	val isSwitched: Boolean
		get() = client.getCameraEntity() === targetEntity
	private val players: List<AbstractClientPlayerEntity>?
		get() = if (client.world != null) client.world!!.players else null

	@JvmStatic
	val playersSuggest: List<String>
		get() {
			val playerNames: MutableList<String> = LinkedList()
			if (players != null) players!!.forEach(Consumer { player: AbstractClientPlayerEntity ->
				playerNames.add(
					player.entityName
				)
			})
			return playerNames
		}

	@JvmStatic
	fun switchOtherPlayer(playerName: String) {
		players!!.stream().filter { player: AbstractClientPlayerEntity -> player.entityName == playerName }
			.findFirst().ifPresent { entity: AbstractClientPlayerEntity? -> client.setCameraEntity(entity) }
	}

	@JvmStatic
	fun switchToTarget(): Boolean {
		if (targetEntity != null) {
			if (targetEntity!!.isLiving) {
				if (targetEntity!!.isAlive) {
					client.setCameraEntity(targetEntity)
				} else {
					return false
				}
			} else {
				client.setCameraEntity(targetEntity)
			}
			return true
		}
		return false
	}

	@JvmStatic
	fun switchEntity() {
		if (client.getCameraEntity() != null) {
			setTargetEntity()
			if (targetEntity != null) client.setCameraEntity(targetEntity)
		} else {
			if (client.player != null) {
				resetCamera()
			}
		}
	}

	@JvmStatic
	fun setTargetEntity(): Boolean {
		if (client.crosshairTarget == null) return false
		if (client.crosshairTarget!!.type == HitResult.Type.ENTITY) {
			targetEntity = (client.crosshairTarget as EntityHitResult?)!!.entity
			if (targetEntity === client.player) {
				targetEntity = null
				return false
			}
			return true
		}
		return false
	}

	private fun resetCamera() {
		client.setCameraEntity(client.player)
	}
}