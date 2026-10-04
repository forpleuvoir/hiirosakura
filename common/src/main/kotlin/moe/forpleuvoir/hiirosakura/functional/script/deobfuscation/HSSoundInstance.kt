package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import net.minecraft.client.resources.sounds.SoundInstance
import org.joml.Vector3d

class HSSoundInstance(@JvmField val vanilla: SoundInstance) {

    fun getId() = vanilla.identifier.toString()

    fun getCategory() = vanilla.source.name

    fun isLooping() = vanilla.isLooping

    fun isRelative() = vanilla.isRelative

    fun getDelay() = vanilla.delay

    /**
     * 获取音量。
     *
     * 原版实现需要音效资源已解析；未解析时取值会抛异常，这里回落到 1.0。
     */
    fun getVolume() = runCatching { vanilla.volume }.getOrDefault(1f)

    /**
     * 获取音调。
     *
     * 原版实现需要音效资源已解析；未解析时取值会抛异常，这里回落到 1.0。
     */
    fun getPitch() = runCatching { vanilla.pitch }.getOrDefault(1f)

    fun getX() = vanilla.x

    fun getY() = vanilla.y

    fun getZ() = vanilla.z

    fun getPos() = Vector3d(getX(), getY(), getZ())

    fun getAttenuation() = vanilla.attenuation.name

    fun canStartSilent() = vanilla.canStartSilent()

    fun canPlay() = vanilla.canPlaySound()


}