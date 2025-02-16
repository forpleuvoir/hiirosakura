package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.ConfigAutoReplantMapEntryList
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.gui.widget.AutoReplantMapEntryWrapper
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.execute
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.recompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigColumnWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ListConfigWrapedButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveableListConfigEntryWrapper
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column

fun WidgetContainerScope.AutoReplantMapEntryListWrapper(
    config: ConfigAutoReplantMapEntryList,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        ListConfigWrapedButton(
            config,
            newValue = { AutoReplant.MapEntry() },
        ) { entry, index ->
            MoveableListConfigEntryWrapper(
                config, index, { execute { this@ListConfigWrapedButton.recompose() } }
            ) {
                AutoReplantMapEntryWrapper(
                    entry,
                    {
                        config.getValue()[index] = it
                        execute { this@ListConfigWrapedButton.recompose() }
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

