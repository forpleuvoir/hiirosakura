package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.AutoReplantMapEntryListWrapper
import moe.forpleuvoir.hiirosakura.config.items.ConfigAutoReplantMapEntryList
import moe.forpleuvoir.hiirosakura.config.items.ConfigSoundEventList
import moe.forpleuvoir.hiirosakura.config.items.SoundEffectListWrapper
import moe.forpleuvoir.hiirosakura.config.items.matcher.*
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigWrapperMap
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber

@EventSubscriber
object HSConfigWrapper {

    @Subscriber
    fun init(event: ClientLifecycleEvent.ClientStartingEvent) = ConfigWrapperMap.apply {
        register<ConfigSoundEventList> { c, m -> SoundEffectListWrapper(c, m) }

        register<ConfigItemStackMatcher> { c, m -> ItemStackMatcherWrapper(c, m) }
        register<ConfigItemStackMatcherMap> { c, m -> ItemStackMatcherMapWrapper(c, m) }
        register<ConfigStringItemStackBlockInfoPairMap> { c, m -> ConfigStringItemStackBlockInfoPairMapWrapper(c, m) }

        register<ConfigBlockInfoMatcher> { c, m -> BlockInfoMatcherWrapper(c, m) }
        register<ConfigBlockInfoMatcherMap> { c, m -> BlockInfoMatcherMapWrapper(c, m) }
        register<ConfigStringBlockInfoItemStackPairMap> { c, m -> ConfigStringBlockInfoItemStackPairMapWrapper(c, modifier = m) }

        register<ConfigAutoReplantMapEntryList> { c, m -> AutoReplantMapEntryListWrapper(c, m) }
    }


}