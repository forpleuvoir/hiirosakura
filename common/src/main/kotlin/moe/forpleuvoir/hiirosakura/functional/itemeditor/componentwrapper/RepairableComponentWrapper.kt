package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.ItemSelector
import moe.forpleuvoir.hiirosakura.gui.widget.WrappedBox
import moe.forpleuvoir.hiirosakura.util.asTranslateText
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
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.RadioButtons
import moe.forpleuvoir.ibukigourd.gui.widget.layout.*
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextWidget
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Repairable

fun ContainerScope.RepairableComponentWrapper(
    key: ResourceLocation,
    component: Repairable,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Repairable, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.width(140f)
    ) {
        Text(IGLang.edit)
        click {
            RepairableEditor(key.asTranslateText(), component, onValueChange = onValueChange).open()
        }
    }
}

private val itemTags
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).tags.map { it.key() }

private val TagKey<Item>.items
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).getTagOrEmpty(this)

private val TagKey<Item>.asHolderSet
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).tags.filter {
        it.key().location == this.location
    }.findFirst().get()

fun RepairableEditor(
    title: Component,
    repairable: Repairable,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (Repairable, Boolean) -> Unit,
): IGScreenImpl {
    val items = repairable.items()

    val (tag, mode) =
        if (items is HolderSet.Named) items.key().asMutableState to false.asMutableState
        else itemTags.findFirst().get().asMutableState to true.asMutableState

    val list = items.map { it.value() }.toMutableList()

    return DataComponentEditor(
        title,
        {
            mode.getValue().pick(
                { Repairable(HolderSet.direct(list.map { BuiltInRegistries.ITEM.wrapAsHolder(it) })) },
                { Repairable(tag.getValue().asHolderSet) }) to true
        },
        onValueChange,
        modifier,
        screenModifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            RadioButtons(listOf(true, false), mode.getValue().asMutableState, onChange = {
                mode.setValue(it)
            }, optionWrapper = {
                Text(if (it) "From Items" else "From Tag")
            })
        }
        SwitchableProxy(
            switch = mode,
            widgetA = {
                var recompose = {}
                Column {
                    Row(Modifier.matchSibling(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                            ItemSelector(
                                Items.MELON.asMutableState,
                                onSelected = {
                                    if (!list.contains(it)) {
                                        list.add(it)
                                        recompose()
                                    }
                                },
                                modifier = Modifier.width(100f),
                                selectedWrapper = {
                                    Text("Add Item")
                                },
                            )
                        }
                        ItemTagSelector(
                            tag,
                            onSelected = { tag ->
                                var result = false
                                tag.items.map { it.value() }.forEach {
                                    if (!list.contains(it)) {
                                        list.add(it)
                                        result = true
                                    }
                                }
                                if (result) recompose()
                            },
                            selectedWrapper = {
                                Text("Add From Tag")
                            },
                            modifier = Modifier.width(100f)
                        )
                    }

                    TableWrapped(list, tableModifier = { Modifier.width(200f).height(160f) }) {
                        recompose = { executeRecompose() }
                        Header(1) {
                            Text("Item", modifier = Modifier.padding(2f), setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
                        }.Column { index, _ ->
                            WrappedBox {
                                Row(Modifier.fillWidth(), horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Left)) {
                                    ItemIcon(list[index], .6f)
                                    Text(list[index].name)
                                }
                            }
                        }
                        Header {
                            Text(IGLang.remove, setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
                        }.Column { index, _ ->
                            DeleteButton({ HSLang.deleteConfirm(list[index].name) }, recompose) {
                                list.removeAt(index)
                            }
                        }
                    }

                }
            },
            widgetB = {
                ItemTagSelector(
                    tag, modifier = Modifier.width(240f),
                    listWrapperModifier = {
                        Modifier.width(itemTags.toList().map { "#${it.location}" }.maxWidth + 12f).maxHeight(120f)
                    },
                )
            }
        )
    }
}


fun ContainerScope.ItemTagSelector(
    itemTag: MutableState<TagKey<Item>>,
    itemTags: Iterable<TagKey<Item>> = moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.itemTags.toList(),
    onSelected: (TagKey<Item>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(TagKey<Item>) -> GuiWidget = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left),
            modifier = Modifier.weight(1)
                .hoverTip {
                    val items = it.items
                    if (items.count() == 0) {
                        Text(IGLang.hasNothing)
                        return@hoverTip
                    } else {
                        Column(horizontalAlignment = Alignment.Left) {
                            items.forEachWithLimit(20) { type ->
                                Text(type.value().name)
                            }
                            if (items.count() > 20) Text("......")
                        }
                    }
                }
        ) {
            Text("#${it.location}")
        }
    },
    optionWrapper: ButtonScope.(TagKey<Item>) -> GuiWidget = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left),
            modifier = Modifier.weight(1).hoverTip {
                val items = it.items
                if (items.count() == 0) {
                    Text(IGLang.hasNothing)
                    return@hoverTip
                } else {
                    Column(horizontalAlignment = Alignment.Left) {
                        items.forEachWithLimit(20) { type ->
                            Text(type.value().name)
                        }
                        if (items.count() > 20) Text("......")
                    }
                }
            }
        ) {
            Text("#${it.location}")
        }
    },
    modifier: Modifier = Modifier.width(280f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width(itemTags.map { "#${it.location}" }.maxWidth + 12f)
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width(itemTags.map { "#${it.location}" }.maxWidth + 12f).maxHeight(160f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = itemTags,
    selected = itemTag,
    predicate = { tag, str ->
        "#${tag.location}".contains(str)
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
