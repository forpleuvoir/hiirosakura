package moe.forpleuvoir.hiirosakura.ui.configwrapper

import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.ui.configwrapper.UIWrappers
import moe.forpleuvoir.nebula.common.api.Initializable

object HSConfigWrapper : Initializable {

    override fun init() {
        ClientLifecycleEvent.Starting.register { register() }
    }

    private fun register() = UIWrappers.apply {
//        register<ConfigSoundEventList> { c, m -> SoundEventListWrapper(c, m) }
//
//        register<ConfigItemStackMatcher> { c, m -> ItemStackMatcherWrapper(c, m) }
//        register<ConfigItemStackMatcherMap> { c, m -> ItemStackMatcherMapWrapper(c, m) }
//        register<ConfigStringItemStackBlockInfoPairMap> { c, m -> ConfigStringItemStackBlockInfoPairMapWrapper(c, m) }
//        register<ConfigStringChatBubbleServerConfigMap> { c, m -> ConfigStringChatBubbleServerConfigMapWrapper(c, m) }
//
//        register<ConfigBlockInfoMatcher> { c, m -> BlockInfoMatcherWrapper(c, m) }
//        register<ConfigBlockInfoMatcherMap> { c, m -> BlockInfoMatcherMapWrapper(c, m) }
//        register<ConfigStringBlockInfoItemStackPairMap> { c, m -> ConfigStringBlockInfoItemStackPairMapWrapper(c, modifier = m) }
//
//        register<ConfigAutoReplantMapEntryList> { c, m -> AutoReplantMapEntryListWrapper(c, m) }
//        register<ConfigChainDoorsRuleList> { c, m -> ConfigChainDoorsRuleListWrapper(c, m) }
    }


}