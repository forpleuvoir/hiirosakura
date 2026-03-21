package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.IdentifierText
import moe.forpleuvoir.hiirosakura.gui.widget.AddButton
import moe.forpleuvoir.hiirosakura.gui.widget.WrappedBox
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.hoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.*
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.style.style
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.component.LodestoneTracker
import net.minecraft.world.level.Level
import java.util.*
import kotlin.jvm.optionals.getOrNull

//region Wrapper
fun ContainerScope.LodestoneTrackerComponentWrapper(
    key: Identifier,
    component: LodestoneTracker,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (LodestoneTracker, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.hoverText(IGLang.edit).width(140f),
    ) {
        Text(IGLang.edit)
        click {
            LodestoneTrackerEditor(
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
fun LodestoneTrackerEditor(
    title: Text,
    component: LodestoneTracker,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (LodestoneTracker, Boolean) -> Unit,
): IGScreenImpl {
    val target = component.target.getOrNull().asMutableState
    val tracked = component.tracked.asMutableState

    return DataComponentEditor(
        title,
        {
            LodestoneTracker(
                Optional.ofNullable(target.getValue()),
                tracked.getValue()
            ) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        ColumnListWrapped(Modifier.fill(), spacing = 2f, listModifier = { Modifier.weight(1) }) {
            GlobalPosEntryWrapper(target, Literal("target"), Modifier.fill())
            Row(Modifier.fill().padding(2f)) {
                Row(Modifier.weight(1).padding(2f).bgHoverHighlightBox(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("tracked", modifier = Modifier.priority(-1))
                    SwitchButton(tracked)
                }
            }
        }
    }
}
//endregion

//region GlobalPosEntryWrapper
fun ContainerScope.GlobalPosEntryWrapper(
    globalPos: MutableState<GlobalPos?>,
    title: Text,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
): ColumnWidget = Column(modifier.attachLeft { padding(2f) }, verticalArrangement, horizontalAlignment) {
    var isPresent = globalPos.getValue() != null
    val expanded = mutableStateOf(isPresent)
    val icon = mutableStateOf(isPresent.either(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN))
    expanded.subscribe {
        icon.setValue(it.either(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN))
    }
    var childrenBox by lateInitValueOf { Box.Unspecified }


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
        if (isPresent) {
            val dimension = globalPos.getValue()!!.dimension.asMutableState
            dimension.onSetValue = {
                globalPos.setValue(GlobalPos(it, globalPos.getValue()!!.pos))
                it
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Right)) {
                Text("dimension")
                WrappedBox(Modifier.width(115f)) {
                    Text(dimension.getValue().identifier().asTranslateText())
                }
                Button(
                    Modifier.hoverText(IGLang.edit)
                ) {
                    Icon(IconTextures.EDIT)
                    click {
                        IdentifierEditor(Literal("dimension"), dimension.getValue().identifier()) { id, recompose ->
                            if (dimension.getValue().identifier() != it) {
                                dimension.setValue(ResourceKey.create(Registries.DIMENSION, id))
                                if (recompose) this@Row.executeRecompose()
                            }
                        }.open()
                    }
                }
            }
            Icon(icon, modifier = Modifier.padding(vertical = 2.5f, horizontal = 2f))
            DeleteButton(
                confirmMessage = { IGLang.removeConfirm(title) },
                recompose = { this.executeRecompose() }
            ) {
                globalPos.setValue(null)
                expanded.setValue(false)
                isPresent = false
            }
        } else {
            AddButton {
                globalPos.setValue(GlobalPos(mc.player!!.dimension(), mc.player!!.blockPosition()))
                expanded.setValue(true)
                isPresent = true
                this.executeRecompose()
            }
        }
        click {
            if (isPresent) {
                expanded.switch()
            } else
                expanded.setValue(false)
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
                Column(verticalArrangement = Arrangement.spacedBy(2f)) {
                    Row(Modifier.fill().bgHoverHighlightBox().padding(vertical = 0f, horizontal = 4f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("x", style = style(Colors.RED))
                        val x = globalPos.getValue()!!.pos.x.asMutableState
                        x.onSetValue = {
                            val pos = globalPos.getValue()!!.pos
                            globalPos.setValue(GlobalPos(globalPos.getValue()!!.dimension, BlockPos(it, pos.y, pos.z)))
                            it
                        }
                        IntEditor(x, modifier = Modifier.width(120f), editorModifier = { Modifier.weight(1) })
                    }
                    Row(Modifier.fill().bgHoverHighlightBox().padding(vertical = 0f, horizontal = 4f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("y", style = style(Colors.LIME))
                        val y = globalPos.getValue()!!.pos.y.asMutableState
                        y.onSetValue = {
                            val pos = globalPos.getValue()!!.pos
                            globalPos.setValue(GlobalPos(globalPos.getValue()!!.dimension, BlockPos(pos.x, it, pos.z)))
                            it
                        }
                        IntEditor(y, modifier = Modifier.width(120f), editorModifier = { Modifier.weight(1) })
                    }
                    Row(Modifier.fill().bgHoverHighlightBox().padding(vertical = 0f, horizontal = 4f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("z", style = style(Colors.BLUE))
                        val z = globalPos.getValue()!!.pos.z.asMutableState
                        z.onSetValue = {
                            val pos = globalPos.getValue()!!.pos
                            globalPos.setValue(GlobalPos(globalPos.getValue()!!.dimension, BlockPos(pos.x, pos.y, it)))
                            it
                        }
                        IntEditor(z, modifier = Modifier.width(120f), editorModifier = { Modifier.weight(1) })
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

fun Player.dimension() = this.level().dimension()

@Deprecated("Data that the client can't get")
fun MinecraftServer.getLevels() =
    registries()
        .compositeAccess()
        .lookupOrThrow(Registries.LEVEL_STEM)
        .entrySet()
        .map { ResourceKey.create(Registries.DIMENSION, it.key.identifier()) }


//region DimensionSelector
fun ContainerScope.DimensionSelector(
    options: Iterable<ResourceKey<Level>>,
    selected: MutableState<ResourceKey<Level>> = mutableStateOf(options.first()),
    checker: (ResourceKey<Level>, ResourceKey<Level>) -> Boolean = { a, b -> a.identifier().toString() == b.identifier().toString() },
    onSelected: (ResourceKey<Level>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(ResourceKey<Level>) -> GuiWidget = {
        IdentifierText(
            it.identifier(),
            modifier = Modifier
                .width(options.map { key -> key.identifier().asTranslateText() }.maxWidth.coerceAtLeast(30f))
        )
    },
    optionWrapper: ButtonScope.(ResourceKey<Level>) -> GuiWidget = {
        IdentifierText(
            it.identifier(),
            modifier = Modifier
                .width(options.map { key -> key.identifier().asTranslateText() }.maxWidth.coerceAtLeast(30f))
        )
    },
    modifier: Modifier = Modifier,
    listWrapperModifier: BoxScope.() -> Modifier = { Modifier },
    listModifier: RowScope.() -> Modifier = { Modifier },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = null,
    scope: DropDownMenuScope.() -> Unit = {}
) = Selector(
    options = options,
    selected = selected,
    checker = checker,
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    listWrapperModifier = listWrapperModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    amountStep = amountStep,
    scope = scope
)

//endregion