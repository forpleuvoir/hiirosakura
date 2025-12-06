package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import net.minecraft.client.resources.sounds.SoundInstance
import org.joml.Vector3d

class HSSoundInstance(internal val soundInstance: SoundInstance) {

    fun getId() = soundInstance.location.toString()

    fun getCategory() = soundInstance.source.name

    fun isLooping() = soundInstance.isLooping

    fun isRelative() = soundInstance.isRelative

    fun getDelay() = soundInstance.delay

    fun getVolume() = soundInstance.volume

    fun getPitch() = soundInstance.pitch

    fun getX() = soundInstance.x

    fun getY() = soundInstance.y

    fun getZ() = soundInstance.z

    fun getPos() = Vector3d(getX(), getY(), getZ())

    fun getAttenuation() = soundInstance.attenuation.name

    fun canStartSilent() = soundInstance.canStartSilent()

    fun canplay() = soundInstance.canPlaySound()


}