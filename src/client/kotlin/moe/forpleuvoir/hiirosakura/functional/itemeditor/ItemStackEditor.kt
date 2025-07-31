package moe.forpleuvoir.hiirosakura.functional.itemeditor

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DataComponentWrappers
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DataComponentWrappers.DataComponentWrapper
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DefaultComponentBuilder
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.ItemSelector
import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.registryManager
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderTextureColored
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
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
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.text.style.style
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.*
import moe.forpleuvoir.ibukigourd.util.textRenderer
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.MergedComponentMap
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKeys

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
        it as? MergedComponentMap ?: MergedComponentMap(it)
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

    var countRecompose: (ComponentType<*>, Any?) -> Unit by lateInitValueOf { _, _ -> }

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
    TextLabel(HSLang.itemEditorItemPreview)
    Row(
        Modifier.padding(horizontal = 5f, vertical = 4f)
            .width(143f)
            .render { ctx, _, _, _ ->
                ctx.batchRenderTextureColored {
                    pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
                }
            }
            .renderOverlay { ctx, mx, my, d ->
                if (wasMouseOver) ctx.postRender {
                    drawItemTooltip(textRenderer, itemSupplier.getValue(), mx.toInt(), my.toInt())
                }
            },
        horizontalArrangement = Arrangement.spacedBy(2f),
    ) {
        ItemIcon(itemSupplier, .6f)
        TextLabel(itemSupplier.getValue().formattedName.copyToText())
    }
}

private fun RowScope.ItemType(itemState: MutableState<Item>) = Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
    TextLabel(HSLang.itemEditorItemType)
    ItemSelector(
        itemState,
        modifier = Modifier.width(100f).hoverText(HSLang.taskIcon),
    )
}

private fun RowScope.ItemCount(countState: MutableState<Int>, componentMap: MergedComponentMap): (ComponentType<*>, Any?) -> Unit {
    var result: (ComponentType<*>, Any?) -> Unit by lateInitValueOf { _, _ -> }
    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
        TextLabel(HSLang.itemEditorItemCount)
        var state = true
        val maxCount = (componentMap.find { it.type.id == DataComponentTypes.MAX_STACK_SIZE.id }?.value as? Int ?: 64).asMutableState
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
                if (type.id == DataComponentTypes.MAX_STACK_SIZE.id) {
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
    componentMap: MergedComponentMap,
    onAdd: () -> Unit
) = Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(5f)) {
    val registryManager = registryManager!!
    val components = registryManager.getOrThrow(RegistryKeys.DATA_COMPONENT_TYPE).sortedBy { it.id(registryManager) }
    val selected = components.first().asMutableState
    TextLabel(HSLang.itemEditorItemAddComponent)
    var toggle by lateInitValueOf {}
    Selector(
        options = components,
        selected = selected,
        optionWrapper = {
            val isAdapted = DataComponentWrappers.isAdaptedComponent(it)
            TextLabel(
                it.id(registryManager).toString(),
                style = style(if (isAdapted) HSVColor(195f, 1f, 1f) else HSVColor(5f, .6f, 1f)),
                modifier = Modifier.hoverText(if (isAdapted) HSLang.itemEditorAdaptedComponent else HSLang.itemEditorUnadaptedComponent)
            )
        },
        selectedWrapper = {
            TextLabel(it.id(registryManager).toString(), modifier = Modifier.width(120f))
        },
        amountStep = 15f,
        onSelected = { type ->
            closeScreen()
            toggle()
            runCatching {
                DataComponentWrappers.defaultValue(type)?.let {
                    if (!componentMap.contains(type)) {
                        componentMap.set(type as ComponentType<Any>, it)
                        onAdd()
                    } else {
                        Toast.showToast(HSLang.itemEditorItemComponentExist(type.id(registryManager) ?: "unknown"))
                    }
                } ?: run {
                    DefaultComponentBuilder(type.id(registryManager)!!, type) { component, recompose ->
                        if (!componentMap.contains(type)) {
                            componentMap.set(type as ComponentType<Any>, component)
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
    components: MergedComponentMap,
    onComponentChange: (ComponentType<*>, Any?) -> Unit
) = ColumnListWrapped(
    Modifier.matchSibling().minHeight(140f).maxHeight(180f),
    spacing = 2f,
    horizontalAlignment = Alignment.Left,
    listModifier = {
        Modifier.weight(1).fill()
    }
) {
    components.types.sortedBy {
        it.id(registryManager!!)
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
                components[type as ComponentType<Any>] = component
                onComponentChange(type, component)
                if (recompose) this.executeRecompose()
            }
        }
    }
}.run {
    { this.executeRecompose() }
}



