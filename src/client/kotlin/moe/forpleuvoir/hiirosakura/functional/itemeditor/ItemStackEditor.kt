package moe.forpleuvoir.hiirosakura.functional.itemeditor

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DataComponentWrappers
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DataComponentWrappers.DataComponentWrapper
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.DefaultComponentBuilder
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.ItemSelector
import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.lateInitValueOf
import moe.forpleuvoir.hiirosakura.util.registryManager
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.IntSlider
import moe.forpleuvoir.ibukigourd.gui.widget.Selector
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
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
import moe.forpleuvoir.ibukigourd.util.state.*
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.component.ComponentType
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
    //TODO i18n
    stateOf(Literal("ItemStackEditor")),
) {
    val itemState = itemStack.item.asMutableState
    val countState = itemStack.count.asMutableState

    val componentMap = itemStack.copy().components.let {
        it as? MergedComponentMap ?: MergedComponentMap(it)
    }

    var recompose by lateInitValueOf<() -> Unit>()

    confirm {
        consumer(ItemStack(itemState.getValue(), countState.getValue(), componentMap.copy()))
        closeScreen()
    }

    Row(Modifier.width(360f), horizontalArrangement = Arrangement.SpaceBetween) {
        ItemType(itemState)
        ItemCount(countState)
    }
    ComponentAdder(componentMap) { recompose() }
    recompose = Component(componentMap)

}

private fun RowScope.ItemType(itemState: MutableState<Item>) = Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
    //TODO i18n
    TextLabel("ItemType")
    ItemSelector(
        itemState,
        modifier = Modifier.width(100f).hoverText(HSLang.taskIcon),
    )
}

private fun RowScope.ItemCount(countState: MutableState<Int>) = Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
    //TODO i18n
    TextLabel("Count")
    val state = mutableStateOf(true)
    SwitchableProxy(
        { IntSlider(countState, 1..64, modifier = Modifier.width(60f)) },
        { IntEditor(countState, 1..99, modifier = Modifier.width(60f), editorModifier = { Modifier.weight(1) }) },
        state
    )
    Button {
        click { state.switch() }
        Icon(IconTextures.SWITCH)
    }
}


@Suppress("UNCHECKED_CAST")
private fun ContainerScope.ComponentAdder(
    componentMap: MergedComponentMap,
    onAdd: () -> Unit
) =
    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
        val components = registryManager!!.getOrThrow(RegistryKeys.DATA_COMPONENT_TYPE).toList()
        val selected = components.first().asMutableState
        TextLabel("DataComponent")
        Selector(
            options = components,
            selected = selected,
            selectedWrapper = {
                TextLabel(it.id.toString())
            },
            optionWrapper = {
                TextLabel(it.id.toString())
            },
            amountStep = 15f
        )
        Button {
            Icon(IconTextures.PLUS, HSVColor(120f, 1f, .65f), modifier = Modifier.size(9f, 9f))
            click {
                runCatching {
                    DataComponentWrappers.defaultValue(selected.getValue())?.let {
                        componentMap.set(selected.getValue() as ComponentType<Any>, it)
                        onAdd()
                    } ?: run {
                        DefaultComponentBuilder(selected.getValue().id!!, selected.getValue()) { component, recompose ->
                            componentMap.set(selected.getValue() as ComponentType<Any>, component)
                            if (recompose) onAdd()
                        }.open()
                    }
                }.onFailure {
                    Toast.showToast(Literal(it.message ?: "unknown error").withColor(Colors.RED))
                    DataComponentWrappers.log.error(it)
                }
            }
        }
    }

@Suppress("UNCHECKED_CAST")
private fun ColumnScope.Component(components: MergedComponentMap): () -> Unit {
    var recompose by lateInitValueOf<() -> Unit>()
    ColumnListWrapped(
        Modifier.matchSibling().height(160f),
        spacing = 2f,
        horizontalAlignment = Alignment.Left,
        listModifier = {
            Modifier.weight(1).fill()
        }
    ) {
        recompose = { this.executeRecompose() }
        components.types.sortedBy {
            it.id
        }.forEach { type ->
            components[type]?.let { c ->
                DataComponentWrapper(
                    type, c,
                    removeAction = {
                        components.remove(type)
                        recompose()
                    }
                ) { component, recompose ->
                    components[type as ComponentType<Any>] = component
                    if (recompose) recompose()
                }
            }
        }
    }
    return recompose
}


