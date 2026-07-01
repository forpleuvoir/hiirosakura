package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import net.minecraft.client.resources.sounds.SoundInstance
import org.joml.Vector3d

class HSSoundInstance(@JvmField val vanilla: SoundInstance) {

    fun getId() = vanilla.identifier.toString()

    fun getCategory() = vanilla.source.name

    fun isLooping() = vanilla.isLooping

    fun isRelative() = vanilla.isRelative

    fun getDelay() = vanilla.delay

    fun getVolume() = vanilla.volume

    fun getPitch() = vanilla.pitch

    fun getX() = vanilla.x

    fun getY() = vanilla.y

    fun getZ() = vanilla.z

    fun getPos() = Vector3d(getX(), getY(), getZ())

    fun getAttenuation() = vanilla.attenuation.name

    fun canStartSilent() = vanilla.canStartSilent()

    fun canplay() = vanilla.canPlaySound()


}