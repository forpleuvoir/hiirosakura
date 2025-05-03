package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherTableColumn
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.ItemStackMatcherTableColumn
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateTextWithParent
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList

class ConfigAutoReplantMapEntryList(
    key: String,
    defaultValue: List<AutoReplant.MapEntry>
) : ConfigList<AutoReplant.MapEntry>(
    key,
    defaultValue,
    {
        it.serialization()
    },
    {
        AutoReplant.MapEntry.deserialization(it)
    }
)

fun ConfigContainer.autoReplantMapEntryList(
    key: String,
    defaultValue: List<AutoReplant.MapEntry>
) = addConfig(ConfigAutoReplantMapEntryList(key, defaultValue))


//------------ GUI Wrapper ------------\\

fun WidgetContainerScope.AutoReplantMapEntryListWrapper(
    config: ConfigAutoReplantMapEntryList,
    modifier: Modifier = Modifier,
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        TableConfigListWrappedButton(
            config,
            title = config.translateTextWithParent(1, " -> "),
            newValue = { AutoReplant.MapEntry() },
            hoverTableScope = {
                Header {
                    TextLabel(HSLang.autoReplantMapEntryTargetBlock)
                }.Column {
                    BlockInfoMatcherSimpleInfo(it.targetBlock, Modifier.height(10f).align(Alignment.CenterLeft))
                }
                Header {
                    TextLabel(HSLang.autoReplantMapEntryReplantItem)
                }.Column {
                    ItemStackMatcherSimpleInfo(it.replantItem, Modifier.height(10f).align(Alignment.CenterLeft))
                }
                Header {
                    TextLabel(HSLang.autoReplantMapEntryGroundBlock)
                }.Column {
                    BlockInfoMatcherSimpleInfo(it.groundBlock, Modifier.height(10f).align(Alignment.CenterLeft))
                }
            },
            dialogModifier = Modifier.padding(20f)
        ) {
            val recompose = { this@TableConfigListWrappedButton.executeRecompose() }
            MoveableTableHeader().MoveableTableColumCell(config, recompose, false)

            val consumer = { index: Int, entry: AutoReplant.MapEntry ->
                config.getValue()[index] = entry
                this@TableConfigListWrappedButton.executeRecompose()
            }

            BlockInfoMatcherTableColumn({ index, entry, block ->
                consumer(index, entry.copy(targetBlock = block))
            }, { it.targetBlock }, { Modifier }, 1) {
                TextLabel(
                    HSLang.autoReplantMapEntryTargetBlock,
                    setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.padding(bottom = 3f)
                        .hoverText(HSLang.autoReplantMapEntryTargetBlockComment)
                )
            }

            ItemStackMatcherTableColumn({ index, entry, item ->
                consumer(index, entry.copy(replantItem = item))
            }, { it.replantItem }, { Modifier }, 1) {
                TextLabel(
                    HSLang.autoReplantMapEntryReplantItem,
                    setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.padding(bottom = 3f)
                        .hoverText(HSLang.autoReplantMapEntryReplantItemComment)
                )
            }

            BlockInfoMatcherTableColumn({ index, entry, block ->
                consumer(index, entry.copy(groundBlock = block))
            }, { it.groundBlock }, { Modifier }, 1) {
                TextLabel(
                    HSLang.autoReplantMapEntryGroundBlock,
                    setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.padding(bottom = 3f)
                        .hoverText(HSLang.autoReplantMapEntryGroundBlockComment)
                )
            }

            Header {
                TextLabel(IGLang.remove)
            }.Column { index, entry ->
                DeleteButton(IGLang.removeConfirm("$index"), recompose) {
                    config.removeAt(index)
                }
            }

        }

        ConfigResetButton(config) {
            this@Row.executeRecompose()
        }
    }
}

