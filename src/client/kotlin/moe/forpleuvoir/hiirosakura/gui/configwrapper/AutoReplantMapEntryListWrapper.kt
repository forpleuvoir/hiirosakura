package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.ConfigAutoReplantMapEntryList
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.gui.widget.AutoReplantMapEntryWrapper
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.margin
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.minWidth
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.execute
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.recompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigColumnWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveButton
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRenderBackground
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.RowListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.collection.notifiableList

fun WidgetContainerScope.AutoReplantMapEntryListWrapper(
    config: ConfigAutoReplantMapEntryList,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    val listValue = notifiableList(config.getValue()).apply {
        subscribe { config.setValue(it) }
    }
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            Modifier.width(80f)
        ) {
            TextLabel(mutableStateBy { IGLang.listConfigWrapperText(listValue.size) })
            click {
                Dialog {
                    TextLabel(stateOf(config.translateText))
                    DialogContent(
                        Modifier.padding(5f, 3f, 5f, 5f)
                    ) {
                        RowListWrapped(
                            modifier = Modifier.disableRenderBackground().padding(0).minWidth(240f),
                            listModifier = { Modifier.height(160f) }
                        ) {
                            if (listValue.isEmpty()) TextLabel(IGLang.hasNothing)
                            listValue.forEachIndexed { index, mapEntry ->
                                Column(
                                    horizontalArrangement = Arrangement.spacedBy(2f),
                                ) {
                                    MoveButton(this@RowListWrapped, listValue, index)
                                    TextLabel(
                                        index.toString(),
                                        modifier = Modifier.width(mc.textRenderer.getWidth(listValue.lastIndex.toString()) + 1f)
                                    )

                                    AutoReplantMapEntryWrapper(
                                        mapEntry,
                                        {
                                            listValue.disableNotify {
                                                listValue[index] = it
                                            }
                                        },
                                        Modifier.width(200f),
                                        Arrangement.SpaceAround
                                    )

                                    FlatButton(
                                        hoveredColor = Colors.LIGHT_RED,
                                        modifier = Modifier.margin(right = 2f).hoverText(IGLang.remove)
                                    ) {
                                        Icon(IconTextures.DELETE, Colors.RED, Modifier.size(10f, 10f))
                                        click {
                                            listValue.removeAt(index)
                                            execute {
                                                this@RowListWrapped.recompose()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40f)
                            .hoverText(IGLang.add)
                    ) {
                        Icon(IconTextures.PLUS, Color(0xFF2EE62E), Modifier.size(8f, 8f))
                        click {
                            listValue.add(AutoReplant.MapEntry())
                            execute {
                                this@Dialog.recompose()
                            }
                        }
                    }
                }.open()
            }
        }
        ConfigResetButton(config) {
            listValue.clear()
            listValue.addAll(config.defaultValue)
        }
    }

}