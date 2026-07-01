package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.configSoundEventList
import moe.forpleuvoir.ibukigourd.config.item.configToggleKeybind
import moe.forpleuvoir.nebula.config.ConfigGroup
import net.minecraft.client.resources.sounds.SoundInstance

object SoundEventFilter : ConfigGroup("sound_event_filter") {

    val enabled by configToggleKeybind("enable", false)

    val filterMapping by configSoundEventList("filter_mapping", emptyList())

    @JvmStatic
    fun shouldFilter(sound: SoundInstance): Boolean {
        if (!enabled.enabled) return false
        return filterMapping.any { sound.identifier == it.location }
    }

}