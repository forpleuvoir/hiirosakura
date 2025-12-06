package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherBuilder
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherInfo
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherSimpleInfo
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTip
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement

class ConfigItemStackMatcher(
    override val key: String,
    override val defaultValue: ItemStackMatcher
) : ConfigBase<ItemStackMatcher, ConfigItemStackMatcher>() {

    override var configValue: ItemStackMatcher = defaultValue.clone()

    override fun serialization(): SerializeElement = getValue().serialization()

    override fun deserialization(serializeElement: SerializeElement) {
        setValue(ItemStackMatcher.deserialization(serializeElement))
    }

}

fun ConfigContainer.itemStackMatcher(
    key: String,
    defaultValue: ItemStackMatcher
) = addConfig(ConfigItemStackMatcher(key, defaultValue))


//------------ GUI Wrapper ------------\\

fun ContainerScope.ItemStackMatcherWrapper(
    config: ConfigItemStackMatcher,
    modifier: Modifier = Modifier,
) = ConfigRowWrapper(config, modifier) {
    val value = mutableStateOf(config.getValue()).apply {
        subscribe { config.setValue(it) }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            Modifier.width(120f)
                .hoverTip { ItemStackMatcherInfo(value.getValue()) }
        ) {
            ItemStackMatcherSimpleInfo(value.getValue()).apply {
                value.subscribe {
                    this@Button.executeRecompose()
                }
            }
            click {
                ItemStackMatcherBuilder(value.getValue(), {
                    value.setValue(it)
                }).open()
            }
        }
        ConfigResetButton(config) {
            value.setValue(config.getValue())
        }
    }
}