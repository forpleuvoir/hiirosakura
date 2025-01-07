package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import net.minecraft.client.sound.SoundInstance
import org.joml.Vector3d

class HSSoundInstance(private val soundInstance: SoundInstance) {

    fun getId() = soundInstance.id.toString()

    fun getCategory() = soundInstance.category.name

    fun isRepeatable() = soundInstance.isRepeatable

    fun isRelative() = soundInstance.isRelative

    fun getRepeatDelay() = soundInstance.repeatDelay

    fun getVolume() = soundInstance.volume

    fun getPitch() = soundInstance.pitch

    fun getX() = soundInstance.x

    fun getY() = soundInstance.y

    fun getZ() = soundInstance.z

    fun getPos() = Vector3d(getX(), getY(), getZ())

    fun getAttenuationType() = soundInstance.attenuationType.name

    fun shouldAlwaysPlay() = soundInstance.shouldAlwaysPlay()

    fun canplay() = soundInstance.canPlay()


}