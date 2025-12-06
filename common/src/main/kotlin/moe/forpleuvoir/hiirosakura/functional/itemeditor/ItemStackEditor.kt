package moe.forpleuvoir.hiirosakura.functional.itemeditor

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DataComponentWrappers
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DataComponentWrappers.DataComponentWrapper
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DefaultComponentBuilder
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.ItemSelector
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.text.style.style
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.*
import moe.forpleuvoir.ibukigourd.util.textRenderer
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

@Suppress("UNCHECKED_CAST")
fun ItemStackEditor(
    itemStack: ItemStack = ItemStackMatcher.handheldItemStack ?: ItemStack(Items.MELON),
    consumer: (ItemStack) -> Unit
) = ConfirmDialog(
    stateOf(HSLang.itemEditor),
) {
    val result = itemStack.asMutableState
    val itemState = itemStack.item.asMutableState
    val countState = itemStack.count.asMutableState

    val componentMap = itemStack.copy().components.let {
        it as? PatchedDataComponentMap ?: PatchedDataComponentMap(it)
    }

    countState.subscribe {
        result.setValue(ItemStack(itemState.getValue(), countState.getValue(), componentMap.copy()))
    }

    var recompose by lateInitValueOf {}

    var previewRecompose by lateInitValueOf {
        result.setValue(ItemStack(itemState.getValue(), countState.getValue(), componentMap.copy()))
    }

    itemState.subscribe {
        previewRecompose()
    }

    confirm {
        consumer(result.getValue())
        closeScreen()
    }

    Row(Modifier.width(400f), horizontalArrangement = Arrangement.SpaceBetween) {
        ItemType(itemState)
        ItemPreview(itemSupplier = result).apply {
            previewRecompose = {
                result.setValue(ItemStack(itemState.getValue(), countState.getValue(), componentMap.copy()))
                this.executeRecompose()
            }
        }
    }

    var countRecompose: (DataComponentType<*>, Any?) -> Unit by lateInitValueOf { _, _ -> }

    Row(Modifier.matchSibling(), horizontalArrangement = Arrangement.SpaceBetween) {
        countRecompose = ItemCount(countState, componentMap)
        ComponentAdder(componentMap) {
            recompose()
            previewRecompose()
        }
    }
    recompose = Components(componentMap) { type, component ->
        countRecompose(type, component)
        previewRecompose()
    }

}

private fun RowScope.ItemPreview(itemSupplier: State<ItemStack>) = Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
    Text(HSLang.itemEditorItemPreview)
    Row(
        Modifier.padding(horizontal = 5f, vertical = 4f)
            .width(143f)
            .render { guiGraphics, _, _, _ ->
                guiGraphics.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
            }
            .renderOverlay { ctx, mx, my, d ->
                if (wasMouseOver) ctx.postEndRender {
                    renderItemDecorations(textRenderer, itemSupplier.getValue(), mx.toInt(), my.toInt())
                }
            },
        horizontalArrangement = Arrangement.spacedBy(2f),
    ) {
        ItemIcon(itemSupplier, .6f)
        Text(itemSupplier.getValue().styledHoverName.copyToText())
    }
}

private fun RowScope.ItemType(itemState: MutableState<Item>) = Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
    Text(HSLang.itemEditorItemType)
    ItemSelector(
        itemState,
        modifier = Modifier.width(100f).hoverText(HSLang.taskIcon),
    )
}

private fun RowScope.ItemCount(countState: MutableState<Int>, componentMap: PatchedDataComponentMap): (DataComponentType<*>, Any?) -> Unit {
    var result: (DataComponentType<*>, Any?) -> Unit by lateInitValueOf { _, _ -> }
    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
        Text(HSLang.itemEditorItemCount)
        var state = true
        val maxCount = (componentMap.find { it.type.key == DataComponents.MAX_STACK_SIZE.key }?.value as? Int ?: 64).asMutableState
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            val switch = state.asMutableState
            SwitchableProxy(
                { IntSlider(countState, 1..maxCount.getValue(), modifier = Modifier.width(75f)) },
                { IntEditor(countState, 1..maxCount.getValue(), modifier = Modifier.width(75f), editorModifier = { Modifier.weight(1) }) },
                switch
            )
            Button {
                click {
                    switch.switch()
                    state = switch.getValue()
                }
                Icon(IconTextures.SWITCH)
            }
        }.apply {
            result = { type, component ->
                if (type.key == DataComponents.MAX_STACK_SIZE.key) {
                    maxCount.setValue(component as? Int ?: 64)
                    countState.setValue(countState.getValue().coerceIn(0..maxCount.getValue()))
                    this.executeRecompose()
                }
            }
        }
    }
    return result
}


@Suppress("UNCHECKED_CAST")
private fun RowScope.ComponentAdder(
    componentMap: PatchedDataComponentMap,
    onAdd: () -> Unit
) = Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(5f)) {
    val registryManager = registryAccess!!
    val components = registryManager.lookupOrThrow(Registries.DATA_COMPONENT_TYPE).sortedBy { it.id(registryManager) }
    val selected = components.first().asMutableState
    Text(HSLang.itemEditorItemAddComponent)
    var toggle by lateInitValueOf {}
    Selector(
        options = components,
        selected = selected,
        optionWrapper = {
            val isAdapted = DataComponentWrappers.isAdaptedComponent(it)
            Text(
                it.id(registryManager).toString(),
                style = style(if (isAdapted) HSVColor(195f, 1f, 1f) else HSVColor(5f, .6f, 1f)),
                modifier = Modifier.hoverText(if (isAdapted) HSLang.itemEditorAdaptedComponent else HSLang.itemEditorUnadaptedComponent)
            )
        },
        selectedWrapper = {
            Text(it.id(registryManager).toString(), modifier = Modifier.width(120f))
        },
        amountStep = 15f,
        onSelected = { type ->
            closeScreen()
            toggle()
            runCatching {
                DataComponentWrappers.defaultValue(type)?.let {
                    if (!componentMap.has(type)) {
                        componentMap.set(type as DataComponentType<Any>, it)
                        onAdd()
                    } else {
                        Toast.showToast(HSLang.itemEditorItemComponentExist(type.id(registryManager) ?: "unknown"))
                    }
                } ?: run {
                    DefaultComponentBuilder(type.id(registryManager)!!, type) { component, recompose ->
                        if (!componentMap.has(type)) {
                            componentMap.set(type as DataComponentType<Any>, component)
                            if (recompose) onAdd()
                        } else {
                            Toast.showToast(HSLang.itemEditorItemComponentExist(type.id(registryManager) ?: "unknown"))
                        }
                    }.open()
                }
            }.onFailure {
                Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                DataComponentWrappers.log.error(it)
            }
        }
    ) {
        toggle = { this.toggle() }
    }
}

@Suppress("UNCHECKED_CAST")
private fun ColumnScope.Components(
    components: PatchedDataComponentMap,
    onComponentChange: (DataComponentType<*>, Any?) -> Unit
) = ColumnListWrapped(
    Modifier.matchSibling().minHeight(140f).maxHeight(180f),
    spacing = 2f,
    horizontalAlignment = Alignment.Left,
    listModifier = {
        Modifier.weight(1).fill()
    }
) {
    components.keySet().sortedBy {
        it.id(registryAccess!!)
    }.forEach { type ->
        components[type]?.let { c ->
            DataComponentWrapper(
                type, c,
                removeAction = {
                    components.remove(type)
                    onComponentChange(type, null)
                    this.executeRecompose()
                }
            ) { component, recompose ->
                components[type as DataComponentType<Any>] = component
                onComponentChange(type, component)
                if (recompose) this.executeRecompose()
            }
        }
    }
}.run {
    { this.executeRecompose() }
}



