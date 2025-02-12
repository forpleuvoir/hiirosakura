package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.ConfigAutoReplantMapEntryList
import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcher
import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcherMap
import moe.forpleuvoir.hiirosakura.config.items.ConfigItemStackMatcher
import moe.forpleuvoir.hiirosakura.config.items.ConfigSoundEventList
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.gui.configwrapper.HSConfigWrapper
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigWrapperMap
import moe.forpleuvoir.nebula.config.item.impl.ConfigStringKeyMap
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber

@EventSubscriber
object HSConfigWrapper {

    @Subscriber
    fun init(event: ClientLifecycleEvent.ClientStartingEvent) = ConfigWrapperMap.apply {
        register<ConfigSoundEventList> { c, m -> SoundEffectListWrapper(c, m) }

        register<ConfigItemStackMatcher> { c, m -> ItemStackMatcherWrapper(c, m) }
        register<ConfigBlockInfoMatcher> { c, m -> BlockInfoMatcherWrapper(c, m) }
        register<ConfigBlockInfoMatcherMap> { c, m -> BlockInfoMatcherMapWrapper(c, m) }

        register<ConfigAutoReplantMapEntryList> { c, m -> AutoReplantMapEntryListWrapper(c, m) }
    }


}