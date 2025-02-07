package moe.forpleuvoir.hiirosakura.functional.gameplay

import moe.forpleuvoir.hiirosakura.config.items.soundEventList
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.nebula.config.item.impl.stringList
import net.minecraft.client.sound.SoundInstance

object SoundEventFilter {

    object Config : ModConfigContainer("sound_event_filter") {

        val enabled by keyBindBoolean("enable", false)

        val filterMapping by soundEventList("filter_mapping", emptyList())

    }

    @JvmStatic
    fun shouldFilter(sound: SoundInstance): Boolean {
        if (!Config.enabled.value) return false
        return sound.id in Config.filterMapping.map { it.id }
    }

}