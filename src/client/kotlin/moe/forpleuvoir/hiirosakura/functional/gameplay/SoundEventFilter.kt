package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.soundEventList
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import net.minecraft.client.sound.SoundInstance

object SoundEventFilter : ModConfigContainer("sound_event_filter") {

    val enabled by keyBindBoolean("enable", false)

    val filterMapping by soundEventList("filter_mapping", emptyList())

    @JvmStatic
    fun shouldFilter(sound: SoundInstance): Boolean {
        if (!enabled.value) return false
        return sound.id in filterMapping.map { it.id }
    }

}