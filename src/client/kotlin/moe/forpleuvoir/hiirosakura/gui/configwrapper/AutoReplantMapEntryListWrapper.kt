package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.ConfigAutoReplantMapEntryList
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.gui.widget.AutoReplantMapEntryWrapper
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherSimpleInfo
import moe.forpleuvoir.ibukigourd.IGLang
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
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit

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
            hoverContent = hoverContent@{
                if (it.count() == 0) {
                    TextLabel(IGLang.hasNothing.plainText)
                    return@hoverContent
                }
                val limit = 20
                Row(
                    horizontalAlignment = Alignment.Left,
                ) {
                    Column(
                        horizontalArrangement = Arrangement.spacedBy(10f),
                    ) {
                        Row(
                            verticalArrangement = Arrangement.spacedBy(1f),
                            horizontalAlignment = Alignment.Left,
                        ) {
                            TextLabel(HSLang.autoReplantMapEntryTargetBlock)
                            it.forEachWithLimit(limit) { entry ->
                                BlockInfoMatcherSimpleInfo(entry.targetBlock, Modifier.height(10f))
                            }
                        }
                        Row(
                            verticalArrangement = Arrangement.spacedBy(1f),
                            horizontalAlignment = Alignment.Left,
                        ) {
                            TextLabel(HSLang.autoReplantMapEntryReplantItem)
                            it.forEachWithLimit(limit) { entry ->
                                ItemStackMatcherSimpleInfo(entry.replantItem, Modifier.height(10f))
                            }
                        }
                        Row(
                            verticalArrangement = Arrangement.spacedBy(1f),
                            horizontalAlignment = Alignment.Left,
                        ) {
                            TextLabel(HSLang.autoReplantMapEntryGroundBlock)
                            it.forEachWithLimit(limit) { entry ->
                                BlockInfoMatcherSimpleInfo(entry.groundBlock, Modifier.height(10f))
                            }
                        }

                    }
                    if (it.count() > limit) TextLabel("...")
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

