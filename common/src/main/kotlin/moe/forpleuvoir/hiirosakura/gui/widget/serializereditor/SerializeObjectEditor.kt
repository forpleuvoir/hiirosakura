package moe.forpleuvoir.hiirosakura.gui.widget.serializereditor

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.AddButton
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.getUserDataOrPut
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.base.userData
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.hoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.util.ScrollState
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.text.withColor
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.renameKey
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.pick
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import kotlin.time.Duration.Companion.seconds

private var TIP: Tip? = null

fun ContainerScope.SerializeObjectEditor(
    serializeObject: SerializeObject,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = ScrollState(),
    spacing: Float = 2f,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    barThickness: Float = 9f,
    listModifier: RowScope.() -> Modifier = { Modifier.fill().weight(1) },
    onValueChange: (SerializeObject) -> Unit
) = Column(verticalArrangement = Arrangement.spacedBy(2f), horizontalAlignment = Alignment.Left) {
    var recompose by lateInitValueOf<() -> Unit>()
    AddButton {
        SerializeElementAdder(
            serializeObject.size.toString().asMutableState,
            { !serializeObject.containsKey(it) }
        ) { key, element ->
            serializeObject[key] = element
            onValueChange(serializeObject)
            recompose()
        }.open()
    }
    ColumnListWrapped(
        modifier,
        scrollState,
        spacing,
        horizontalAlignment,
        barThickness,
        listModifier
    ) {
        amountStep(15f)
        if (serializeObject.isEmpty()) Text(IGLang.hasNothing, Modifier.fill())
        serializeObject.forEach { (key, value) ->
            SerializeElementEntryEditor(
                key,
                value,
                Modifier.fill().unlockConstraint(),
                keyWrapper = { k ->
                    FlatButton(hoveredColor = Colors.LIMEGREEN.alpha(.25f), modifier = Modifier.hoverText(IGLang.edit)) {
                        Text(k)
                        click {
                            ConfirmDialog(IGLang.edit.asState, screenModifier = Modifier.onClose { TipHandler.popTip(TIP) }) {
                                var newKey = k
                                var editor by lateInitValueOf<() -> Transform>()
                                Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                                    Text(IGLang.mapKey)
                                    TextEditor(Modifier.width(160f)) {
                                        text = newKey
                                        textConsumer { newKey = it }
                                        editor = { owner().transform }
                                    }
                                }
                                confirm {
                                    if (serializeObject.containsKey(newKey)) {
                                        TipHandler.popTip(TIP)
                                        TIP = TipHandler.pushTip(2.seconds, editor, Tip {
                                            Text(IGLang.keyExists(newKey).withColor(Colors.RED))
                                        })
                                    } else {
                                        serializeObject.renameKey(key, newKey)
                                        closeScreen()
                                        recompose()
                                    }
                                }
                            }.open()
                        }
                    }
                },
                onValueChange = {
                    serializeObject[key] = it
                    onValueChange(serializeObject)
                }
            ) {
                DeleteButton(
                    { HSLang.deleteConfirm(key) },
                    { recompose() }
                ) {
                    serializeObject.remove(key)
                }
            }
        }
    }.apply {
        recompose = { this.executeRecompose() }
    }
}

fun ContainerScope.SerializeObjectEntryEditor(
    key: String,
    serializeObject: SerializeObject,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    keyWrapper: RowScope.(String) -> Unit = { Text(it) },
    onValueChange: (SerializeObject) -> Unit,
    content: RowScope.() -> Unit
) = Column(modifier) {
    val expanded = mutableStateOf(this.getUserDataOrPut("#object_expanded") { serializeObject.size <= 5 })
    expanded.subscribe {
        this.userData["#object_expanded"] = it
    }
    var recompose by lateInitValueOf<() -> Unit>()
    var childrenBox by lateInitValueOf { Box.Unspecified }
    Button(
        modifier = Modifier.bgHoverHighlightBox()
            .hoverHighlightBox(
                boxSupplier = { childrenBox() },
                colorRange = Colors.CYAN.alpha(0f) to Colors.CYAN.alpha(0.15f),
            )
            .padding(0)
    ) {
        click { expanded.switch() }
        ElementEntry(key, Modifier.weight(1), horizontalArrangement, verticalAlignment, keyWrapper) {
            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                AddButton {
                    SerializeElementAdder(
                        serializeObject.size.toString().asMutableState,
                        { !serializeObject.containsKey(it) }
                    ) { key, element ->
                        serializeObject[key] = element
                        onValueChange(serializeObject)
                        recompose()
                    }.open()
                }
                Icon(
                    mutableStateOf(expanded) {
                        it.pick(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN)
                    },
                    modifier = Modifier
                )
                content()
            }
        }
    }
    SwitchableProxy(
        {
            Row {
                Widget(
                    Modifier.width(1.5f)
                        .matchSibling()
                        .padding(0, 0, 2, 2)
                        .margin(4, 0.5, 0, 0)
                        .render { guiGraphics, _, _, _ ->
                            guiGraphics.pushBox(
                                transform.asWorldCoordinateBox.trimEdges(padding.top, padding.bottom, padding.left, padding.right),
                                configContainerWrapperGuidelinesColor
                            )
                        }
                )
                Column(
                    modifier = Modifier.padding(2, 4, 2, 2),
                    verticalArrangement = Arrangement.spacedBy(2f)
                ) {
                    if (serializeObject.isEmpty()) Text(IGLang.hasNothing, Modifier.fill())
                    serializeObject.forEach { (key, value) ->
                        SerializeElementEntryEditor(
                            key,
                            value,
                            Modifier.fill(),
                            keyWrapper = { k ->
                                FlatButton(hoveredColor = Colors.LIMEGREEN.alpha(.25f), modifier = Modifier.hoverText(IGLang.edit)) {
                                    Text(k)
                                    click {
                                        ConfirmDialog(IGLang.edit.asState, screenModifier = Modifier.onClose { TipHandler.popTip(TIP) }) {
                                            var newKey = k
                                            var editor by lateInitValueOf<() -> Transform>()
                                            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                                                Text(IGLang.mapKey)
                                                TextEditor(Modifier.width(160f)) {
                                                    text = newKey
                                                    textConsumer { newKey = it }
                                                    editor = { owner().transform }
                                                }
                                            }
                                            confirm {
                                                if (serializeObject.containsKey(newKey)) {
                                                    TipHandler.popTip(TIP)
                                                    TIP = TipHandler.pushTip(2.seconds, editor, Tip {
                                                        Text(IGLang.keyExists(newKey).withColor(Colors.RED))
                                                    })
                                                } else {
                                                    serializeObject.renameKey(key, newKey)
                                                    closeScreen()
                                                    recompose()
                                                }
                                            }
                                        }.open()
                                    }
                                }
                            },
                            onValueChange = {
                                serializeObject[key] = it
                                onValueChange(serializeObject)
                            }
                        ) {
                            DeleteButton(
                                { HSLang.deleteConfirm(key) },
                                { recompose() }
                            ) {
                                serializeObject.remove(key)
                            }
                        }
                    }
                }.apply {
                    recompose = { this.executeRecompose() }
                    childrenBox = { this.transform.asWorldCoordinateBox }
                }
            }
        },
        {
            recompose = {}
            childrenBox = { Box.Unspecified }
            Widget(Modifier)
        },
        expanded
    )
}