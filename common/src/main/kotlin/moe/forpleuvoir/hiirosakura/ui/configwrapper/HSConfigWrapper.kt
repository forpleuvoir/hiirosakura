package moe.forpleuvoir.hiirosakura.ui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.AutoReplantEntryListConfigWrapper
import moe.forpleuvoir.hiirosakura.config.items.ChainDoorsRuleListConfigWrapper
import moe.forpleuvoir.hiirosakura.config.items.matcher.*
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoorsRule
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigUIWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.UIWrappers
import moe.forpleuvoir.nebula.common.api.Initializable
import moe.forpleuvoir.nebula.config.item.ConfigList
import moe.forpleuvoir.nebula.config.item.ConfigMap

object HSConfigWrapper : Initializable {

    override fun init() {
        ClientLifecycleEvent.Starting.register { register() }
    }

    private fun register() = UIWrappers.apply {
        registerCheckValueType<ItemStackMatcher> { ItemStackMatcherConfigWrapper(it) }
        registerMap<ItemStackMatcher> { ItemStackMatcherMapConfigWrapper(it) }
        registerCheckValueType<BlockInfoMatcher> { BlockInfoMatcherConfigWrapper(it) }
        registerMap<BlockInfoMatcher> { BlockInfoMatcherMapConfigWrapper(it) }

        registerMap<BlockInfoItemStackPair> { BlockInfoItemStackPairMapWrapper(it) }

        registerList<ChainDoorsRule> { ChainDoorsRuleListConfigWrapper(it) }
        registerList<AutoReplant.Entry> { AutoReplantEntryListConfigWrapper(it) }

//        register<ConfigSoundEventList> { c, m -> SoundEventListWrapper(c, m) }
//
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

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified V : Any> registerMap(wrapper: ConfigUIWrapper<ConfigMap<V>>) {
        UIWrappers.register({ it is ConfigMap<*> && it.entryValueType == V::class }) {
            wrapper.content(it as ConfigMap<V>)
        }
    }


    @Suppress("UNCHECKED_CAST")
    private inline fun <reified V : Any> registerList(wrapper: ConfigUIWrapper<ConfigList<V>>) {
        UIWrappers.register({ it is ConfigList<*> && it.elementType == V::class }) {
            wrapper.content(it as ConfigList<V>)
        }
    }
}