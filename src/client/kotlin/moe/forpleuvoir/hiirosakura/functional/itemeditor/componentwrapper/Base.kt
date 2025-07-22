package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
import moe.forpleuvoir.hiirosakura.gui.widget.TreeNodeEditor
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.hiirosakura.util.registryManager
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
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextWidgetScope
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.style.style
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.serialization.extensions.toMap
import moe.forpleuvoir.nebula.serialization.extensions.toSerializeObject
import net.minecraft.component.ComponentType
import net.minecraft.text.Style
import net.minecraft.util.Identifier


fun ContainerScope.IdentifierText(
    id: Identifier,
    style: Style = Style.EMPTY,
    modifier: Modifier = Modifier,
    setting: TextSetting = TextSetting(),
    scope: TextWidgetScope.() -> Unit = {}
) = TextLabel(id.asTranslateText().setStyle(style), modifier, setting, scope)


fun <C : Any> DataComponentWrapperDialog(
    title: Text,
    newComponent: () -> Pair<C, Boolean>,
    onValueChange: (C, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onConfirm: () -> Boolean = { true },
    content: ColumnScope.() -> Unit
) = ConfirmDialog(
    title.asState,
    modifier,
    screenModifier,
    onConfirm = {
        if (onConfirm()) {
            val (newComponent, recompose) = newComponent()
            onValueChange(newComponent, recompose)
            closeScreen()
        }
    }
) {
    content()
}

fun ContainerScope.DataComponentWrapperRow(
    id: Identifier,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: RowScope.() -> Unit
) = Row(Modifier.bgHoverHighlightBox().padding(horizontal = 2f).minHeight(19f).then(modifier), horizontalArrangement = Arrangement.SpaceBetween) {
    IdentifierText(id, style(HSVColor(195f, 1f, 1f)), Modifier.weight(1))
    Row(Modifier, horizontalArrangement, verticalAlignment) {
        content()
        RemoveButton {
            removeAction()
            this@DataComponentWrapperRow.executeRecompose()
        }
    }
}

fun ContainerScope.UnsupportedCComponentWrapper(
    id: Identifier,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    TextLabel("Unsupported")
}

@Suppress("UNCHECKED_CAST")
fun <C : Any> ContainerScope.DefaultComponentWrapper(
    id: Identifier,
    componentType: ComponentType<C>,
    component: Any?,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (C, Boolean) -> Unit
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(Modifier.hoverText(IGLang.edit)) {
        Icon(IconTextures.EDIT)
        click {
            runCatching {
                //TODO支持更多类型 而不只是Object,或许需要一个SerializeElement编辑器
                val data = componentType.codecOrThrow
                    .encodeStart(registryManager!!.getOps(NebulaOps), component as C)
                    .resultOrPartial {
                        Toast.showToast(Literal(it).withColor(Colors.RED))
                    }
                    .get()
                    .asObject
                    .toMap()
                    .toMutableMap()

                var result = component
                DataComponentWrapperDialog(
                    id.asTranslateText(),
                    { result!! as C to true },
                    onValueChange,
                    modifier = Modifier.width(340f),
                    onConfirm = {
                        var r = true
                        runCatching {
                            result = componentType.codecOrThrow
                                .parse(registryManager!!.getOps(NebulaOps), data.toSerializeObject())
                                .resultOrPartial {
                                    Toast.showToast(Literal(it).withColor(Colors.RED))
                                    DataComponentWrappers.log.error(it)
                                    r = false
                                }.get()
                        }.onFailure {
                            DataComponentWrappers.log.error(it)
                        }
                        r
                    }
                ) {
                    TreeNodeEditor(data, Modifier.width(330f).height(190f), { Modifier.weight(1).fill() })
                }.open()
            }.onFailure {
                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                DataComponentWrappers.log.error(it)
            }
        }
    }
}

fun <C : Any> DefaultComponentBuilder(
    id: Identifier,
    componentType: ComponentType<C>,
    onValueChange: (C, Boolean) -> Unit,
): IGScreenImpl {
    val data = mutableMapOf<String, Any?>()
    var result: C? = null
    return DataComponentWrapperDialog(
        id.asTranslateText(),
        { result!! to true },
        onValueChange,
        modifier = Modifier.width(340f),
        onConfirm = {
            var r = true
            runCatching {
                result = componentType.codecOrThrow
                    .parse(registryManager!!.getOps(NebulaOps), data.toSerializeObject())
                    .resultOrPartial {
                        Toast.showToast(Literal(it).withColor(Colors.RED))
                        DataComponentWrappers.log.error(it)
                        r = false
                    }.get()
            }.onFailure {
//                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                DataComponentWrappers.log.error(it)
            }
            r
        }
    ) {
        TreeNodeEditor(data, Modifier.width(330f).height(190f), { Modifier.weight(1).fill() })
    }
}