@file:Suppress("unused")

package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import moe.forpleuvoir.hiirosakura.util.math.toVector
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries

open class HSEntity(protected open val entity: Entity) {

    companion object {
        @JvmStatic
        fun fromEntity(entity: Entity) = when (entity) {
            is PlayerEntity -> HSPlayerEntity(entity)
            is LivingEntity -> HSLivingEntity(entity)
            else            -> HSEntity(entity)
        }
    }

    fun getName(): String = entity.name.string

    fun getDisplayName(): String? = entity.displayName?.string

    fun getPos() = entity.pos.toVector()

    fun getRotation() = entity.rotationClient.toVector()

    fun getPitch() = entity.pitch

    fun getYaw() = entity.yaw

    fun getEyePos() = entity.eyePos.toVector()

    fun getSpeed() = entity.speed

    fun getUuid(): String = entity.uuidAsString

    fun getId() = entity.id

    fun getType() = Registries.ENTITY_TYPE.getId(entity.type).toString()

    fun getWorld() = entity.world.registryKey.value.toString()

    fun isFireImmune() = entity.isFireImmune

    fun isOnFire() = entity.isOnFire

    fun isTouchingWaterOrRain() = entity.isTouchingWaterOrRain

    fun isSneaking() = entity.isSneaking

    fun isSwimming() = entity.isSwimming

    fun isAlive() = entity.isAlive

    fun sInvisible() = entity.isInvisible

    fun isInvulnerable() = entity.isInvulnerable

}

open class HSLivingEntity(override val entity: LivingEntity) : HSEntity(entity) {

    fun getMaxHealth() = entity.maxHealth

    fun getHealth() = entity.health

    fun getHealthPercent() = entity.health / entity.maxHealth

    fun getMainHandStack() = HSItemStack(entity.mainHandStack)

    fun getOffHandStack() = HSItemStack(entity.offHandStack)

    fun isBaby() = entity.isBaby

}

class HSPlayerEntity(override val entity: PlayerEntity) : HSLivingEntity(entity) {

    fun isCreative() = entity.isCreative

    fun isSpectator() = entity.isSpectator

    fun getExperienceLevel() = entity.experienceLevel

    fun getTotalExperience() = entity.totalExperience

    fun getExperienceProgress() = entity.experienceProgress

}