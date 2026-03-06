package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.WrappedBox
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.onClose
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.withColor
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.resources.ResourceLocation
import kotlin.time.Duration.Companion.seconds

private var IDENTIFIER_COMPONENT_WRAPPER: Tip? = null

fun ContainerScope.IdentifierComponentWrapper(
    key: ResourceLocation,
    component: ResourceLocation,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (ResourceLocation, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = component
    WrappedBox(Modifier.width(115f)) {
        Text(component.asTranslateText())
    }
    Button(
        Modifier.hoverText(IGLang.edit)
    ) {
        Icon(IconTextures.EDIT)
        click {
            IdentifierEditor(key.asTranslateText(), component) { it, recompose ->
                if (component != it) {
                    component = it
                    onValueChange(component, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}

fun IdentifierEditor(
    title: Text,
    identifier: ResourceLocation,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (ResourceLocation, Boolean) -> Unit,
): IGScreenImpl {
    val namespaceState = identifier.namespace.asMutableState
    var namespaceEditor: (() -> Transform)? = null

    val pathState = identifier.path.asMutableState
    var pathEditor: (() -> Transform)? = null

    return DataComponentEditor(
        title,
        { ResourceLocation.fromNamespaceAndPath(namespaceState.getValue(), pathState.getValue()) to true },
        onValueChange,
        modifier,
        screenModifier.attachLeft { onClose { TipHandler.popTip(IDENTIFIER_COMPONENT_WRAPPER) } },
        {
            val namespaceValid = ResourceLocation.isValidNamespace(namespaceState.getValue())
            if (!namespaceValid) {
                namespaceEditor?.let {
                    TipHandler.popTip(IDENTIFIER_COMPONENT_WRAPPER)
                    IDENTIFIER_COMPONENT_WRAPPER = TipHandler.pushTip(5.seconds, it, Tip {
                        Text(
                            Literal("Non [a-z0-9_.-] character in namespace of location: ${namespaceState.getValue()}")
                                .withColor(Colors.RED)
                        )
                    })
                }
            }
            val pathValid = ResourceLocation.isValidPath(pathState.getValue())
            if (!pathValid) {
                pathEditor?.let {
                    TipHandler.popTip(IDENTIFIER_COMPONENT_WRAPPER)
                    IDENTIFIER_COMPONENT_WRAPPER = TipHandler.pushTip(5.seconds, it, Tip {
                        Text(
                            Literal("Non [a-z0-9/._-] character in path of location: ${pathState.getValue()}")
                                .withColor(Colors.RED)
                        )
                    })
                }
            }
            namespaceValid && pathValid
        }
    ) {
        DialogContent {
            Column(Modifier.width(260f), verticalArrangement = Arrangement.spacedBy(5f)) {
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("namespace")
                    TextEditor(Modifier.width(180f)) {
                        bindState(namespaceState)
                        namespaceEditor = { this.owner().transform }
                    }
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("path")
                    TextEditor(Modifier.width(180f)) {
                        bindState(pathState)
                        pathEditor = { this.owner().transform }
                    }
                }
            }
        }
    }
}
