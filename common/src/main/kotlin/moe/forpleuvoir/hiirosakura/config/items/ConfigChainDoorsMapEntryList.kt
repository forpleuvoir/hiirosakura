package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.ChainDoors
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherTableColumn
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateTextWithParent
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.maxWidth
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList

class ConfigChainDoorsMapEntryList(
    key: String,
    defaultValue: List<ChainDoors.MapEntry>
) : ConfigList<ChainDoors.MapEntry>(
    key,
    defaultValue,
    { it.serialization() },
    { ChainDoors.MapEntry.deserialization(it) }
)

fun ConfigContainer.chainDoorsMapEntryList(
    key: String,
    defaultValue: List<ChainDoors.MapEntry>
) = addConfig(ConfigChainDoorsMapEntryList(key, defaultValue))

//------------ GUI Wrapper ------------\\

fun ContainerScope.ChainDoorsMapEntryListWrapper(
    config: ConfigChainDoorsMapEntryList,
    modifier: Modifier = Modifier,
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        TableConfigListWrappedButton(
            config,
            title = config.translateTextWithParent(1, " -> "),
            newValue = { ChainDoors.MapEntry() },
            hoverTableScope = {
                Header {
                    Text(HSLang.doubleDoorsClickedDoor)
                }.Column {
                    BlockInfoMatcherSimpleInfo(it.clickedDoor, Modifier.height(10f).align(Alignment.CenterLeft))
                }
                Header {
                    Text(HSLang.doubleDoorsLinkedDoor)
                }.Column {
                    BlockInfoMatcherSimpleInfo(it.linkedDoor, Modifier.height(10f).align(Alignment.CenterLeft))
                }
            },
            dialogModifier = Modifier.padding(20f),
            tableWrappedModifier = { Modifier.maxWidth(420f) }
        ) {
            val recompose = { this@TableConfigListWrappedButton.executeRecompose() }
            MoveableTableHeader().MoveableTableColumCell(config, recompose, false)

            val consumer = { index: Int, entry: ChainDoors.MapEntry ->
                config.getValue()[index] = entry
                this@TableConfigListWrappedButton.executeRecompose()
            }

            BlockInfoMatcherTableColumn({ index, entry, block ->
                consumer(index, entry.copy(clickedDoor = block))
            }, { it.clickedDoor }, { Modifier }, 1) {
                Text(
                    HSLang.doubleDoorsClickedDoor,
                    setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.hoverText(HSLang.doubleDoorsClickedDoorComment)
                )
            }

            BlockInfoMatcherTableColumn({ index, entry, block ->
                consumer(index, entry.copy(linkedDoor = block))
            }, { it.linkedDoor }, { Modifier }, 1) {
                Text(
                    HSLang.doubleDoorsLinkedDoor,
                    setting = TextSetting().copy(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.hoverText(HSLang.doubleDoorsLinkedDoorComment)
                )
            }

            Header {
                Text(IGLang.remove)
            }.Column { index, entry ->
                DeleteButton({ IGLang.removeConfirm("$index") }, recompose) {
                    config.removeAt(index)
                }
            }

        }

        ConfigResetButton(config) {
            this@Row.executeRecompose()
        }
    }
}

