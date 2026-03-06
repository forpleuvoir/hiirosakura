package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.DataComponentTypeSelector
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveableTableHeader
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.TableWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.component.TooltipDisplay

fun ContainerScope.TooltipDisplayComponentWrapper(
    key: ResourceLocation,
    component: TooltipDisplay,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (TooltipDisplay, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = TooltipDisplay(component.hideTooltip, component.hiddenComponents)
    Button(
        modifier = Modifier
            .width(140f)
            .hoverTip {
                Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(5f)) {
                    Text("hide_tooltip : ${component.hideTooltip}")
                    if (component.hiddenComponents.isEmpty()) {
                        Text(IGLang.hasNothing)
                        return@Column
                    }
                    component.hiddenComponents.forEachWithLimit(10) {
                        Text(it.keyOrUnknown(registryAccess!!).toString())
                    }
                }
            }
    ) {
        Text(IGLang.listConfigWrapperText(component.hiddenComponents.size))
        click {
            TooltipDisplayEditor(key.asTranslateText(), component) { it, recompose ->
                if (component != it) {
                    component = it
                    onValueChange(component, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}


fun TooltipDisplayEditor(
    title: Text,
    component: TooltipDisplay,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (TooltipDisplay, Boolean) -> Unit,
): IGScreenImpl {
    val hideTooltip = component.hideTooltip.asMutableState
    val hiddenComponents = component.hiddenComponents.toMutableList()

    return DataComponentEditor(
        title,
        {
            TooltipDisplay(hideTooltip.getValue(), LinkedHashSet(hiddenComponents)) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        var recompose = {}
        Row(Modifier.matchSibling(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                Text("hide_tooltip")
                SwitchButton(hideTooltip)
            }
            val selectedDataComponentType = mutableStateOf(BuiltInRegistries.DATA_COMPONENT_TYPE.toList().first())
            var toggle by lateInitValueOf {}
            DataComponentTypeSelector(
                selectedDataComponentType,
                modifier = Modifier.hoverText(IGLang.add).width(172f),
                selectedWrapper = {
                    Text("Add From DataComponentType", modifier = Modifier.weight(1))
                },
                onSelected = { dataComponentType ->
                    toggle()
                    if (dataComponentType.keyOrUnknown(registryAccess!!) !in hiddenComponents.map { it.keyOrUnknown(registryAccess!!) }) {
                        hiddenComponents.add(dataComponentType)
                        recompose()
                    } else {
                        Toast.showToast(HSLang.itemEditorItemComponentExist(dataComponentType.keyOrUnknown(registryAccess!!)))
                    }
                },
                optionsDirection = listOf(Direction.Bottom)
            ) {
                toggle = { this.toggle() }
            }
        }

        TableWrapped(
            hiddenComponents,
            tableModifier = { Modifier.width(280f).height(140f) }
        ) {
            recompose = { executeRecompose() }

            MoveableTableHeader {
                Text(IGLang.move, modifier = Modifier.padding(bottom = 3f))
            }.Column { index, _ ->
                MoveButton(recompose, hiddenComponents, index)
            }

            Header(1) {
                Text("hidden_components", setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally))
            }.Column { index, type ->
                val state = type.asMutableState
                state.onSetValue = { entry ->
                    hiddenComponents[index] = entry
                    recompose()
                    entry
                }
                DataComponentTypeSelector(state, modifier = Modifier.width(555f))
            }

            Header {
                Text(text = IGLang.remove)
            }.Column { index, _ ->
                DeleteButton(
                    confirmMessage = {
                        HSLang.deleteConfirm(
                            hiddenComponents[index].keyOrUnknown(registryAccess!!)
                        )
                    },
                    recompose = { recompose() }
                ) {
                    hiddenComponents.removeAt(index)
                }
            }
        }
    }
}