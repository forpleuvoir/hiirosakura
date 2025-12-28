package moe.forpleuvoir.hiirosakura.gui.widget.serializereditor

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.AddButton
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.getUserDataOrPut
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.margin
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.render
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.userData
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveButton
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.hoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.util.ScrollState
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import moe.forpleuvoir.nebula.serialization.base.SerializeArray

fun ContainerScope.SerializeArrayEditor(
    serializeArray: SerializeArray,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = ScrollState(),
    spacing: Float = 2f,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    barThickness: Float = 9f,
    listModifier: RowScope.() -> Modifier = { Modifier.fill().weight(1) },
    onValueChange: (SerializeArray) -> Unit
) = Column(verticalArrangement = Arrangement.spacedBy(2f), horizontalAlignment = Alignment.Left) {
    var recompose by lateInitValueOf<() -> Unit>()
    AddButton {
        SerializeElementAdder(serializeArray.size.toString().asState, { true }) { _, element ->
            serializeArray.addLast(element)
            onValueChange(serializeArray)
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
        if (serializeArray.isEmpty()) Text(IGLang.hasNothing, Modifier.fill())
        serializeArray.forEachIndexed { index, _ ->
            SerializeElementEntryEditor(
                index.toString(),
                serializeArray[index],
                Modifier.fill().unlockConstraint(),
                keyWrapper = {
                    Row(horizontalArrangement = Arrangement.spacedBy(2f)) {
                        MoveButton({ recompose() }, serializeArray, index)
                        Text(it)
                    }
                },
                onValueChange = {
                    serializeArray[index] = it
                    onValueChange(serializeArray)
                }
            ) {
                DeleteButton(
                    { HSLang.deleteConfirm(index) },
                    { recompose() }
                ) {
                    serializeArray.removeAt(index)
                }
            }
        }
    }.apply {
        recompose = { this.executeRecompose() }
    }
}

fun ContainerScope.SerializeArrayEntryEditor(
    key: String,
    serializeArray: SerializeArray,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    keyWrapper: RowScope.(String) -> Unit = { Text(it) },
    onValueChange: (SerializeArray) -> Unit,
    content: RowScope.() -> Unit
) = Column(modifier) {
    val expanded = mutableStateOf(this.getUserDataOrPut("#array_expanded") { serializeArray.size <= 5 })
    expanded.subscribe {
        this.userData["#array_expanded"] = it
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
                    SerializeElementAdder(serializeArray.size.toString().asState, { true }) { _, element ->
                        serializeArray.addLast(element)
                        onValueChange(serializeArray)
                        recompose()
                    }.open()
                }
                Icon(
                    mutableStateOf(expanded) {
                        it.either(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN)
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
                    if (serializeArray.isEmpty()) Text(IGLang.hasNothing, Modifier.fill())
                    serializeArray.forEachIndexed { index, _ ->
                        SerializeElementEntryEditor(
                            index.toString(),
                            serializeArray[index],
                            Modifier.fill(),
                            keyWrapper = {
                                Row(horizontalArrangement = Arrangement.spacedBy(2f)) {
                                    MoveButton({ recompose() }, serializeArray, index)
                                    Text(it)
                                }
                            },
                            onValueChange = {
                                serializeArray[index] = it
                                onValueChange(serializeArray)
                            }
                        ) {
                            DeleteButton(
                                { HSLang.deleteConfirm(index) },
                                { recompose() }
                            ) {
                                serializeArray.removeAt(index)
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
            recompose = { }
            childrenBox = { Box.Unspecified }
            Widget(Modifier)
        },
        expanded
    )
}