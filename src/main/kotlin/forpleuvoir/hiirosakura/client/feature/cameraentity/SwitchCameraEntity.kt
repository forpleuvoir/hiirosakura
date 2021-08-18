package forpleuvoir.hiirosakura.client.feature.cameraentity

import forpleuvoir.hiirosakura.client.HiiroSakuraClient.addTickHandler
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
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.cameraentity
 *
 * #class_name SwitchCameraEntity
 *
 * #create_time 2021/6/22 21:12
 */
object SwitchCameraEntity {
	private var targetEntity: Entity? = null
	private val client = MinecraftClient.getInstance()

	init {
		addTickHandler {
			if (client.options.keySneak.wasPressed()) {
				if (client.getCameraEntity() == targetEntity) client.setCameraEntity(
					client.player
				)
			}
		}
	}

	val isSwitched: Boolean
		get() = client.getCameraEntity() === targetEntity
	private val players: List<AbstractClientPlayerEntity>?
		get() = if (client.world != null) client.world!!.players else null
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

	fun switchOtherPlayer(playerName: String) {
		players!!.stream().filter { player: AbstractClientPlayerEntity -> player.entityName == playerName }
			.findFirst().ifPresent { entity: AbstractClientPlayerEntity? -> client.setCameraEntity(entity) }
	}

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