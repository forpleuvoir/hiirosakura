package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcher
import moe.forpleuvoir.hiirosakura.config.items.ConfigItemStackMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherBuilder
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherBuilder
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigColumnWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf

fun WidgetContainerScope.ItemStackMatcherWrapper(
    config: ConfigItemStackMatcher,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    val value = mutableStateOf(config.getValue()).apply {
        subscribe { config.setValue(it) }
    }
    Button(
        Modifier.width(120f)
    ) {
        TextLabel(IGLang.edit)
        click {
            ItemStackMatcherBuilder(value.getValue(), {
                value.setValue(it)
            }).open()
        }
    }
    ConfigResetButton(config)
}

fun WidgetContainerScope.BlockInfoMatcherWrapper(
    config: ConfigBlockInfoMatcher,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    val value = mutableStateOf(config.getValue()).apply {
        subscribe { config.setValue(it) }
    }
    Button(
        Modifier.width(120f)
    ) {
        TextLabel(IGLang.edit)
        click {
            BlockInfoMatcherBuilder(value.getValue(), {
                value.setValue(it)
            }).open()
        }
    }
    ConfigResetButton(config)
}