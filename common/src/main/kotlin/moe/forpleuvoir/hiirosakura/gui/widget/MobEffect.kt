package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.DropDownMenuScope
import moe.forpleuvoir.ibukigourd.gui.widget.SelectorWithSearcher
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.defaultSelectedColor
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.TableWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextWidget
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects

//region MobEffectEditor
fun MobEffectEditor(
    value: HolderSet<MobEffect>,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (HolderSet<MobEffect>) -> Unit
) = ConfirmDialog(
    Text.literal("mob_effect").asState,
    modifier,
    screenModifier
) {
    val list = value.map { it.value() }.toMutableList()
    var recompose = {}

    onConfirm = {
        onValueChange(HolderSet.direct({
            BuiltInRegistries.MOB_EFFECT.wrapAsHolder(it)
        }, *list.toTypedArray()))
        closeScreen()
    }

    Row(Modifier.matchSibling(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            var toggle by lateInitValueOf {}
            val holders = BuiltInRegistries.MOB_EFFECT
            MobEffectSelector(
                mobEffects = holders,
                mobEffect = mutableStateOf(holders.first()),
                modifier = Modifier.hoverText(IGLang.add).width(140f),
                selectedWrapper = {
                    Text("Add From MobEffect")
                },
                onSelected = { type ->
                    toggle()
                    if (!list.contains(type)) {
                        list.add(type)
                        recompose()
                    } else {
                        Toast.showToast(HSLang.itemEditorItemComponentExist(type.displayName))
                    }
                },
                optionsDirection = listOf(Direction.Bottom)
            ) {
                toggle = { this.toggle() }
            }
        }
    }

    TableWrapped(list, tableModifier = { Modifier.height(150f).width(260f) }) {
        recompose = { executeRecompose() }
        Header(1) {
            Text("mob_effect", setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
        }.Column { index, _ ->
            MobEffectSelector(list[index].asMutableState, onSelected = {
                list[index] = it
            }, modifier = Modifier.width(555f))
        }

        Header {
            Text(text = IGLang.remove, setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
        }.Column { index, _ ->
            DeleteButton(
                confirmMessage = { HSLang.deleteConfirm(list[index].displayName) },
                recompose = { recompose() }
            ) {
                list.removeAt(index)
            }
        }
    }
}
//endregion

//region HolderMobEffectSelector
fun ContainerScope.HolderMobEffectSelector(
    mobEffect: MutableState<Holder<MobEffect>>,
    mobEffects: Iterable<Holder<MobEffect>> = BuiltInRegistries.MOB_EFFECT.asHolderIdMap(),
    onSelected: (Holder<MobEffect>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Holder<MobEffect>) -> GuiWidget = {
        Text(it.value().displayName, modifier = Modifier.weight(1))
    },
    optionWrapper: ButtonScope.(Holder<MobEffect>) -> GuiWidget = {
        Text(it.value().displayName, modifier = Modifier.weight(1))
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width(mobEffects.map { it.value().displayName }.maxWidth + 14f) },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier.width(mobEffects.map { it.value().displayName }.maxWidth + 14f).maxHeight(160f) },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = mobEffects,
    selected = mobEffect,
    predicate = { effect, str ->
        val effect = effect.value()
        effect.descriptionId.contains(str, ignoreCase = true)
                || effect.displayName.string.contains(str, ignoreCase = true)
                || BuiltInRegistries.MOB_EFFECT.getKey(effect).toString().contains(str, ignoreCase = true)
    },
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    searchBarModifier = searchBarModifier,
    listWrapperModifier = listWrapperModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    amountStep = 15f,
    scope = scope
)
//endregion

//region MobEffectSelector
fun ContainerScope.MobEffectSelector(
    mobEffect: MutableState<MobEffect>,
    mobEffects: Iterable<MobEffect> = BuiltInRegistries.MOB_EFFECT,
    onSelected: (MobEffect) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(MobEffect) -> GuiWidget = {
        Text(it.displayName, modifier = Modifier.weight(1))
    },
    optionWrapper: ButtonScope.(MobEffect) -> GuiWidget = {
        Text(it.displayName, modifier = Modifier.weight(1))
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width(mobEffects.map { it.displayName }.maxWidth + 14f) },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier.width(mobEffects.map { it.displayName }.maxWidth + 14f).maxHeight(160f) },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = mobEffects,
    selected = mobEffect,
    predicate = { effect, str ->
        effect.descriptionId.contains(str, ignoreCase = true)
                || effect.displayName.string.contains(str, ignoreCase = true)
                || BuiltInRegistries.MOB_EFFECT.getKey(effect).toString().contains(str, ignoreCase = true)
    },
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    searchBarModifier = searchBarModifier,
    listWrapperModifier = listWrapperModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    amountStep = 15f,
    scope = scope
)
//endregion

//region MobEffectInstanceEditor
fun MobEffectInstanceEditor(
    mobEffectInstance: MobEffectInstance,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (MobEffectInstance) -> Unit
): IGScreenImpl = ConfirmDialog(
    title = Literal("MobEffectInstance Editor"),
    modifier = modifier,
    screenModifier = screenModifier,
) {
    //region 属性声明
    val effect = mobEffectInstance.effect.asMutableState
    val duration = mobEffectInstance.duration.asMutableState
    val amplifier = mobEffectInstance.amplifier.asMutableState
    val ambient = mobEffectInstance.isAmbient.asMutableState
    val visible = mobEffectInstance.visible.asMutableState
    val showIcon = mobEffectInstance.showIcon.asMutableState
    var hiddenEffect: MobEffectInstance? = mobEffectInstance.hiddenEffect
    //endregion

    onConfirm = {
        onValueChange(
            MobEffectInstance(
                effect.getValue(),
                duration.getValue(),
                amplifier.getValue(),
                ambient.getValue(),
                visible.getValue(),
                showIcon.getValue(),
                hiddenEffect
            )
        )
        closeScreen()
    }

    val width = 294f

    ColumnListWrapped {
        //effect
        Row(
            modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("effect")
            HolderMobEffectSelector(effect, modifier = Modifier.width(160f))
        }
        //duration
        Row(
            modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("duration")
            IntEditor(duration, modifier = Modifier.width(160f), editorModifier = { Modifier.weight(1) })
        }
        //amplifier
        Row(
            modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("amplifier")
            IntEditor(amplifier, range = 0..255, modifier = Modifier.width(160f), editorModifier = { Modifier.weight(1) })
        }
        //ambient
        Row(
            modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ambient")
            SwitchButton(ambient)
        }
        //visible
        Row(
            modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("visible")
            SwitchButton(visible)
        }
        //showIcon
        Row(
            modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("show_icon")
            SwitchButton(showIcon)
        }
        //hiddenEffect
        Row(
            modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("hidden_effect")
            Row(horizontalArrangement = Arrangement.spacedBy(2f)) {
                hiddenEffect?.let { hidden ->
                    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                        Text(hidden.effect.value().displayName)
                        EditButton {
                            MobEffectInstanceEditor(hidden, Modifier, Modifier) {
                                hiddenEffect = it
                            }.open()
                        }
                    }
                } ?: run {
                    AddButton {
                        MobEffectInstanceEditor(MobEffectInstance(MobEffects.LUCK), Modifier, Modifier) {
                            hiddenEffect = it
                        }.open()
                    }
                }
                DeleteButton(
                    confirmMessage = { IGLang.removeConfirm("hidden_effect") },
                    recompose = { this.executeRecompose() }
                ) {
                    hiddenEffect = null
                }
            }
        }
    }
}
//endregion

