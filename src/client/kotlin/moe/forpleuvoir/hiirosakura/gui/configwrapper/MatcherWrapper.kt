package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcher
import moe.forpleuvoir.hiirosakura.config.items.ConfigItemStackMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTtp
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigColumnWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf

fun WidgetContainerScope.ItemStackMatcherWrapper(
    config: ConfigItemStackMatcher,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    val value = mutableStateOf(config.getValue()).apply {
        subscribe { config.setValue(it) }
    }
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            Modifier.width(120f)
                .hoverTtp { ItemStackMathcerInfo(value) }
        ) {
            ItemStackMathcerSimpleInfo(value).apply {
                value.subscribe { this.recompose() }
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

fun WidgetContainerScope.BlockInfoMatcherWrapper(
    config: ConfigBlockInfoMatcher,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    val value = mutableStateOf(config.getValue()).apply {
        subscribe { config.setValue(it) }
    }
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            Modifier.width(120f)
                .hoverTtp { BlockInfoMatcherInfo(value) }
        ) {
            BlockInfoMathcerSimpleInfo(value).apply {
                value.subscribe { this.recompose() }
            }
            click {
                BlockInfoMatcherBuilder(value.getValue(), {
                    value.setValue(it)
                }).open()
            }
        }
        ConfigResetButton(config) {
            value.setValue(config.getValue())
        }
    }
}