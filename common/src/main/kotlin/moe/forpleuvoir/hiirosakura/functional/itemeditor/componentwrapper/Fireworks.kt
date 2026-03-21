package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import it.unimi.dsi.fastutil.ints.IntList
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.ColorSettingButton
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.AddButton
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.hoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.DropDownMenuScope
import moe.forpleuvoir.ibukigourd.gui.widget.Selector
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnWidget
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks

//region Wrapper
fun ContainerScope.FireworksComponentWrapper(
    key: Identifier,
    component: Fireworks,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Fireworks, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.hoverText(IGLang.edit).width(140f),
    ) {
        Text(IGLang.edit)
        click {
            FireworksEditor(
                key.asTranslateText(),
                component,
                modifier = Modifier.width(320f),
                screenModifier = Modifier.padding(20f),
                onValueChange = onValueChange
            ).open()
        }
    }
}
//endregion

//region Editor
fun FireworksEditor(
    title: Text,
    component: Fireworks,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (Fireworks, Boolean) -> Unit,
): IGScreenImpl {

    val flightDuration = component.flightDuration.asMutableState
    val explosions = ArrayList(component.explosions)

    var listRecompose by lateInitValueOf<() -> Unit>()
    return DataComponentEditor(
        title,
        {
            Fireworks(
                flightDuration.getValue().coerceIn(0..255),
                explosions,
            ) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        //普通属性
        Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
            Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("flight_duration", modifier = Modifier.priority(-1))
                IntEditor(flightDuration, 0..255, modifier = Modifier.width(40f), editorModifier = { Modifier.weight(1) })
            }
            Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                Widget(Modifier.weight(1))
                FireworkExplosionShapeSelector(
                    FireworkExplosion.Shape.LARGE_BALL.asMutableState,
                    onSelected = {
                        if (explosions.size < Fireworks.MAX_EXPLOSIONS) {
                            explosions.add(FireworkExplosion(it, IntList.of(), IntList.of(), false, false))
                            listRecompose()
                        } else {
                            Toast.showToast("Maximum number(${Fireworks.MAX_EXPLOSIONS}) of FireworkExplosion reached")
                        }
                    },
                    selectedWrapper = {
                        Text("Add FireworkExplosion")
                    },
                )
            }
        }
        ColumnListWrapped(
            Modifier.fill().weight(1),
            spacing = 2f,
            listModifier = { Modifier.weight(1).fill() },
            onCreate = { listRecompose = { this.executeRecompose() } }
        ) {
            explosions.forEachIndexed { index, explosion ->
                FireworkExplosionEntryWrapper(
                    explosions[index],
                    index,
                    {
                        explosions.removeAt(index)
                        listRecompose()
                    },
                    onValueChange = {
                        explosions[index] = it
                    },
                    modifier = Modifier.unlockConstraint()
                )
            }
        }
    }
}
//endregion


//region FireworkExplosionWrapper
fun ContainerScope.FireworkExplosionEntryWrapper(
    fireworkExplosion: FireworkExplosion,
    index: Int,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onValueChange: (FireworkExplosion) -> Unit
): ColumnWidget = Column(modifier.attachLeft { padding(2f) }, verticalArrangement, horizontalAlignment) {
    val icon = mutableStateOf(WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN)
    val expanded = mutableStateOf(false)
    expanded.subscribe {
        icon.setValue(it.either(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN))
    }
    var childrenBox by lateInitValueOf { Box.Unspecified }

    var fireworkExplosion = fireworkExplosion

    //region 属性声明
    val shape = fireworkExplosion.shape.asMutableState
    shape.subscribe {
        fireworkExplosion = FireworkExplosion(
            it,
            fireworkExplosion.colors,
            fireworkExplosion.fadeColors,
            fireworkExplosion.hasTrail,
            fireworkExplosion.hasTwinkle,
        )
        onValueChange(fireworkExplosion)
    }
    val hasTrail = fireworkExplosion.hasTrail.asMutableState
    hasTrail.subscribe {
        fireworkExplosion = FireworkExplosion(
            fireworkExplosion.shape,
            fireworkExplosion.colors,
            fireworkExplosion.fadeColors,
            it,
            fireworkExplosion.hasTwinkle,
        )
        onValueChange(fireworkExplosion)
    }
    val hasTwinkle = fireworkExplosion.hasTwinkle.asMutableState
    hasTwinkle.subscribe {
        fireworkExplosion = FireworkExplosion(
            fireworkExplosion.shape,
            fireworkExplosion.colors,
            fireworkExplosion.fadeColors,
            fireworkExplosion.hasTrail,
            it,
        )
        onValueChange(fireworkExplosion)
    }

    //endregion
    val width = 314f
    Button(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.bgHoverHighlightBox()
            .width(320f)
            .hoverHighlightBox(
                boxSupplier = { childrenBox() },
                colorRange = Colors.CYAN.alpha(0f) to Colors.CYAN.alpha(0.15f),
            )
            .padding(0)
    ) {
        Row(
            modifier = Modifier.padding(2f).weight(1),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("FireworkExplosion:$index")
            FireworkExplosionShapeSelector(shape)
        }
        Icon(icon, modifier = Modifier.padding(vertical = 2.5f, horizontal = 2f))
        DeleteButton(
            confirmMessage = { IGLang.removeConfirm("$index : ${shape.getValue().getName()}") },
            recompose = {}
        ) {
            onRemove()
        }
        click { expanded.switch() }
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
                Column {
                    //duration
                    IntColorListEntryWrapper(ArrayList(fireworkExplosion.colors), Literal("colors")) {
                        fireworkExplosion = FireworkExplosion(
                            fireworkExplosion.shape,
                            it,
                            fireworkExplosion.fadeColors,
                            fireworkExplosion.hasTrail,
                            fireworkExplosion.hasTwinkle,
                        )
                        onValueChange(fireworkExplosion)
                    }
                    IntColorListEntryWrapper(ArrayList(fireworkExplosion.fadeColors), Literal("fade_colors")) {
                        fireworkExplosion = fireworkExplosion.withFadeColors(it)
                        onValueChange(fireworkExplosion)
                    }
                    //hasTrail
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("has_trail")
                        SwitchButton(hasTrail)
                    }
                    //hasTwinkle
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("has_twinkle")
                        SwitchButton(hasTwinkle)
                    }


                }
            }.apply {
                childrenBox = { this.transform.asWorldCoordinateBox }
            }
        },
        {
            childrenBox = { Box.Unspecified }
            Widget(Modifier)
        },
        expanded
    )
}
//endregion


//region ColorListEntryWrapper
fun ContainerScope.IntColorListEntryWrapper(
    colors: MutableList<Int>,
    title: Text,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onValueChange: (IntList) -> Unit
): ColumnWidget = Column(modifier.attachLeft { padding(2f) }, verticalArrangement, horizontalAlignment) {
    val icon = mutableStateOf(WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN)
    val expanded = mutableStateOf(false)
    expanded.subscribe {
        icon.setValue(it.either(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN))
    }
    var childrenBox by lateInitValueOf { Box.Unspecified }
    var listRecompose by lateInitValueOf { }

    Button(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.bgHoverHighlightBox()
            .width(320f)
            .hoverHighlightBox(
                boxSupplier = { childrenBox() },
                colorRange = Colors.CYAN.alpha(0f) to Colors.CYAN.alpha(0.15f),
            )
            .padding(2f)
    ) {
        Text(title, modifier = Modifier.weight(1))
        Icon(icon, modifier = Modifier.padding(vertical = 2.5f, horizontal = 2f))
        AddButton {
            colors.add(0)
            onValueChange(IntList.of(*colors.toIntArray()))
            expanded.setValue(true)
            listRecompose()
        }
        DeleteButton(
            confirmMessage = { IGLang.removeConfirm(title) },
            recompose = {}
        ) {
            colors.clear()
            onValueChange(IntList.of())
            listRecompose()
        }
        click { expanded.switch() }
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
                Column(verticalArrangement = Arrangement.spacedBy(2f)) {
                    colors.forEachIndexed { index, c ->
                        Row(Modifier.fill().bgHoverHighlightBox().padding(2f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${title.string} : $index")
                            val color: MutableState<ARGBColor> = mutableStateOf(Color.ofRGB(c))
                            color.onSetValue = {
                                it.apply {
                                    colors[index] = rgb
                                    onValueChange(IntList.of(*colors.toIntArray()))
                                }
                            }
                            Row {
                                ColorSettingButton(color, Modifier.width(80f))
                                DeleteButton(confirmMessage = { IGLang.removeConfirm("${title.string} : $index") }, recompose = { listRecompose() }) {
                                    colors.removeAt(index)
                                    onValueChange(IntList.of(*colors.toIntArray()))
                                }
                            }
                        }
                    }
                }.apply {
                    listRecompose = { this.executeRecompose() }
                }
            }.apply {
                childrenBox = { this.transform.asWorldCoordinateBox }
            }
        },
        {
            childrenBox = { Box.Unspecified }
            Widget(Modifier)
        },
        expanded
    )
}
//endregion


//region FireworkExplosionShapeSelector
fun ContainerScope.FireworkExplosionShapeSelector(
    selected: MutableState<FireworkExplosion.Shape>,
    options: Iterable<FireworkExplosion.Shape> = FireworkExplosion.Shape.entries,
    onSelected: (FireworkExplosion.Shape) -> Unit = {},
    selectedWrapper: DropDownMenuScope.(FireworkExplosion.Shape) -> GuiWidget = { shape ->
        Text(
            shape.getName(),
            modifier = Modifier
                .width(FireworkExplosion.Shape.entries.map { it.getName() }.maxWidth.coerceAtLeast(30f))
        )
    },
    optionWrapper: ButtonScope.(FireworkExplosion.Shape) -> GuiWidget = { shape ->
        Text(
            shape.getName(),
            modifier = Modifier
                .width(FireworkExplosion.Shape.entries.map { it.getName() }.maxWidth.coerceAtLeast(30f))
        )
    },
    modifier: Modifier = Modifier,
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = null
) = Selector(
    options = options,
    selected = selected,
    onSelected = onSelected,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    optionsDirection = optionsDirection,
    amountStep = amountStep
)
//endregion