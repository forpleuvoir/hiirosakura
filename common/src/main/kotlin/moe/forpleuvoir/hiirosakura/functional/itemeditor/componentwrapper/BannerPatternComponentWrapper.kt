package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.DyeColorSelector
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.active
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveableTableHeader
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.*
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.math.x
import moe.forpleuvoir.ibukigourd.util.math.y
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.client.renderer.Sheets
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.entity.BannerPattern
import net.minecraft.world.level.block.entity.BannerPatternLayers

fun ContainerScope.BannerPatternComponentWrapper(
    key: ResourceLocation,
    component: BannerPatternLayers,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (BannerPatternLayers, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = component
    Button(
        modifier = Modifier.width(140f)
            .hoverTip {
                Column(verticalArrangement = Arrangement.spacedBy(5f), horizontalAlignment = Alignment.Left) {
                    component.layers().forEach {
                        Row {
                            BannerPattern(it.pattern.asState, it.color.asState)
                            Text(it.description().copyToText())
                        }
                    }
                }
            }
    ) {
        Text(IGLang.listConfigWrapperText(component.layers().size))

        click {
            BannerPatternComponentEditor(key.asTranslateText(), component) { it, recompose ->
                if (component != it) {
                    component = it
                    onValueChange(component, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}

fun BannerPatternComponentEditor(
    title: Text,
    component: BannerPatternLayers,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (BannerPatternLayers, Boolean) -> Unit,
): IGScreenImpl {
    val layers = component.layers().toMutableList()

    return DataComponentEditor(
        title,
        {
            BannerPatternLayers(layers) to true
        },
        onValueChange,
        modifier,
        screenModifier
    ) {
        var recompose = {}
        var sizeChanged: (Int) -> Unit = {}
        TableWrapped(layers, tableModifier = { Modifier.width(320f).height(130f) }) {
            recompose = { executeRecompose() }
            MoveableTableHeader {
                Text(IGLang.move, modifier = Modifier.padding(bottom = 3f))
            }.Column { index, _ ->
                MoveButton(recompose, layers, index)
            }

            Header(2) {
                Text("pattern", setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally))
            }.Column { index, layer ->
                val state = layer.pattern.asMutableState
                state.onSetValue = { pattern ->
                    layers[index] = BannerPatternLayers.Layer(pattern, layer.color)
                    recompose()
                    pattern
                }
                BannerPatternSelector(state, layer.color.asState, modifier = Modifier.width(555f))
            }

            Header(1) {
                Text("color", setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally))
            }.Column { index, layer ->
                val state = layer.color().asMutableState
                state.onSetValue = { color ->
                    layers[index] = BannerPatternLayers.Layer(layer.pattern, color)
                    recompose()
                    color
                }
                DyeColorSelector(state, modifier = Modifier.width(120f))
            }

            Header {
                Text(text = IGLang.remove)
            }.Column { index, _ ->
                DeleteButton(
                    {
                        HSLang.deleteConfirm(layers[index].description())
                    },
                    { recompose() }
                ) {
                    layers.removeAt(index)
                    sizeChanged(layers.size)
                }
            }

        }

        Button(
            Modifier
                .align(Alignment.CenterHorizontally)
                .width(40f)
                .hoverText(IGLang.add)
        ) {
            active(layers.size < 6)
            sizeChanged = {
                active(layers.size < 6)
            }
            Icon(IconTextures.PLUS, Color.ofRGB(0x2EE62E), Modifier.size(8f, 8f))
            click {
                BannerPatternLayerEditor { value ->
                    layers.add(value)
                    sizeChanged(layers.size)
                    recompose()
                }.open()
            }
        }
    }
}

fun BannerPatternLayerEditor(
    value: BannerPatternLayers.Layer? = null,
    modifier: Modifier = Modifier,
    consumer: (BannerPatternLayers.Layer) -> Unit,
) = ConfirmDialog(if (value == null) IGLang.add.asState else IGLang.edit.asState, modifier = modifier) {
    val default = BannerPatternLayers.Layer(REGISTERED_BANNER_PATTERN.first(), DyeColor.WHITE)
    val patternState = (value ?: default).pattern().asMutableState
    val dyeColorState = (value ?: default).color().asMutableState
    onConfirm = {
        consumer(
            BannerPatternLayers.Layer(patternState.getValue(), dyeColorState.getValue()),
        )
        closeScreen()
    }
    DialogContent {
        Column(Modifier.width(240f), verticalArrangement = Arrangement.spacedBy(5f)) {
            var recompose = {}
            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                recompose = { executeRecompose() }
                Text("pattern")
                BannerPatternSelector(patternState, dyeColorState, modifier = Modifier.width(190f))
            }
            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("color")
                DyeColorSelector(dyeColorState, modifier = Modifier.width(190f), onSelected = {
                    recompose()
                })
            }
        }
    }
}


val REGISTERED_BANNER_PATTERN: List<Holder<BannerPattern>>
    get() = registryAccess?.lookupOrThrow(Registries.BANNER_PATTERN)?.asHolderIdMap()?.toList() ?: emptyList()

fun Holder<BannerPattern>.translatableText(color: DyeColor) =
    Text.translatable("${this.value().translationKey()}.${color.getName()}", this.value().assetId.toString())

fun ContainerScope.BannerPatternSelector(
    bannerPattern: MutableState<Holder<BannerPattern>>,
    dyeColor: State<DyeColor> = DyeColor.WHITE.asState,
    bannerPatterns: List<Holder<BannerPattern>> = REGISTERED_BANNER_PATTERN,
    onSelected: (Holder<BannerPattern>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Holder<BannerPattern>) -> GuiWidget = {
        Row(Modifier.weight(1), horizontalArrangement = Arrangement.Left) {
            BannerPattern(it.asState, dyeColor)
            Text(it.translatableText(dyeColor.getValue()))
        }
    },
    optionWrapper: ButtonScope.(Holder<BannerPattern>) -> GuiWidget = {
        Row(Modifier.weight(1), horizontalArrangement = Arrangement.Left) {
            BannerPattern(it.asState, dyeColor)
            Text(it.translatableText(dyeColor.getValue()))
        }
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width((bannerPatterns.map { it.translatableText(dyeColor.getValue()).plainText }.maxWidth + 24f).coerceAtLeast(90f))
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width((bannerPatterns.map { it.translatableText(dyeColor.getValue()).plainText }.maxWidth + 24f).coerceAtLeast(90f)).maxHeight(150f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = 15f,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = bannerPatterns,
    predicate = { layer, str ->
        layer.translatableText(dyeColor.getValue()).plainText.contains(str)
                || layer.value().translationKey().toString().contains(str)
                || layer.value().assetId.toString().contains(str)
    },
    selected = bannerPattern,
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    searchBarModifier = searchBarModifier,
    listWrapperModifier = listWrapperModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    amountStep = amountStep,
    scope = scope
)

fun ContainerScope.BannerPattern(
    bannerPattern: State<Holder<BannerPattern>>,
    color: State<DyeColor>,
    modifier: Modifier = Modifier.size(10f, 8f),
) = Widget(modifier.attachLeft {
    render { guiGraphics, _, _, _ ->
        guiGraphics {
            pose().pushMatrix()
            pose().translate(transform.worldCenter.x - 2.5f, transform.worldCenter.y - 5)
            val sprite = getSprite(Sheets.getBannerMaterial(bannerPattern.getValue()))
            val u0 = sprite.u0
            val u1 = u0 + (sprite.u1 - sprite.u0) * 21.0f / 64.0f
            val vSize = sprite.v1 - sprite.v0
            val v0 = sprite.v0 + vSize / 64.0f
            val v1 = v0 + vSize * 40.0f / 64.0f
            fill(0, 0, 5, 10, color.getValue().textureDiffuseColor)
            blit(sprite.atlasLocation(), 0, 0, 5, 10, u0, u1, v0, v1)
            pose().popMatrix()
        }
    }
})
