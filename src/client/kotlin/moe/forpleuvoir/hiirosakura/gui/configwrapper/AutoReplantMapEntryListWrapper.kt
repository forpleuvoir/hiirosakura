package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.ConfigAutoReplantMapEntryList
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.gui.widget.AutoReplantMapEntryInfo
import moe.forpleuvoir.hiirosakura.gui.widget.AutoReplantMapEntryWrapper
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.execute
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.recompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigColumnWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ListConfigWrappedButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveableListConfigEntryWrapper
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row

fun WidgetContainerScope.AutoReplantMapEntryListWrapper(
    config: ConfigAutoReplantMapEntryList,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        ListConfigWrappedButton(
            config,
            newValue = { AutoReplant.MapEntry() },
            rowListModifier = { Modifier.height(180f) },
            hoverContent = {
                Row(
                    horizontalAlignment = Alignment.Left,
                ) {
                    it.forEach { entry ->
                        AutoReplantMapEntryInfo(entry)
                    }
                }
            }
        ) { entry, index ->
            MoveableListConfigEntryWrapper(
                config, index, { execute { this@ListConfigWrappedButton.recompose() } }
            ) {
                AutoReplantMapEntryWrapper(
                    entry,
                    {
                        config.getValue()[index] = it
                        execute { this@ListConfigWrappedButton.recompose() }
                    },
                    Modifier.weight(1),
                    Arrangement.SpaceAround
                )
            }
        }
        ConfigResetButton(config) {
            execute { this@Column.recompose() }
        }
    }
}

