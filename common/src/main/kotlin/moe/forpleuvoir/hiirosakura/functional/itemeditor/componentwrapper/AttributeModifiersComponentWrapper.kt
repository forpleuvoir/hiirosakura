package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.gui.widget.EntiryAttributeSelector
import moe.forpleuvoir.hiirosakura.gui.widget.REGISTERED_ATTRIBUTE
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
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
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.hoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Box
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.DoubleEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.component.ItemAttributeModifiers

fun ContainerScope.AttributeModifiersComponentWrapper(
    id: ResourceLocation,
    component: ItemAttributeModifiers,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (ItemAttributeModifiers, Boolean) -> Unit,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = component
    Button(
        modifier = Modifier.width(140f)
    ) {
        Text(IGLang.listConfigWrapperText(component.modifiers.size))

        click {
            AttributeModifiersComponentEditor(id.asTranslateText(), component) { it, recompose ->
                if (component != it) {
                    component = it
                    onValueChange(component, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}

fun AttributeModifiersComponentEditor(
    title: Text,
    component: ItemAttributeModifiers,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (ItemAttributeModifiers, Boolean) -> Unit,
): IGScreenImpl {

    val modifiers = component.modifiers.toMutableList()

    return DataComponentWrapperDialog(
        title,
        {
            ItemAttributeModifiers(modifiers) to true
        },
        onValueChange,
        modifier,
        screenModifier
    ) {
        var recompose = {}

        Row(
            modifier = Modifier.matchSibling(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                Icon(IconTextures.PLUS, modifier = Modifier.size(8f, 8f))
                EntiryAttributeSelector(REGISTERED_ATTRIBUTE.first().asMutableState, modifier = Modifier.width(160f), onSelected = {
                    modifiers.addLast(
                        ItemAttributeModifiers.Entry(
                            it,
                            AttributeModifier(ResourceLocation.parse("minecraft:unknow"), 0.0, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND
                        )
                    )
                    recompose()
                })
            }
        }

        ColumnListWrapped(
            spacing = 2f, modifier = Modifier, listModifier = { Modifier.height(170f) }
        ) {
            amountStep(15f)
            if (modifiers.isEmpty()) Text(IGLang.hasNothing)
            modifiers.forEachIndexed { index, entry ->
                AttributeModifiersComponentEntryWrapper(entry, Literal("Modifier:${index}"), {
                    modifiers.removeAt(index)
                    recompose()
                }) {
                    modifiers[index] = it
                }
            }
        }.apply {
            recompose = { this.executeRecompose() }
        }
    }
}

fun ContainerScope.AttributeModifiersComponentEntryWrapper(
    entry: ItemAttributeModifiers.Entry,
    title: Text,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onValueChange: (ItemAttributeModifiers.Entry) -> Unit
) = Column(modifier.attachLeft { padding(2f) }, verticalArrangement, horizontalAlignment) {
    val expanded = mutableStateOf(true)
    var childrenBox by lateInitValueOf { Box.Unspecified }

    Button(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.bgHoverHighlightBox()
            .width(300f)
            .hoverHighlightBox(
                boxSupplier = { childrenBox() },
                colorRange = Colors.CYAN.alpha(0f) to Colors.CYAN.alpha(0.15f),
            )
            .padding(0)
    ) {
        Text(title)
        RemoveButton { onRemove() }
        click { expanded.switch() }
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
                Column {
                    var entry = entry

                    val type = entry.attribute.asMutableState
                    type.subscribe {
                        entry = ItemAttributeModifiers.Entry(it, entry.modifier, entry.slot)
                        onValueChange(entry)
                    }

                    val id = entry.modifier.id.asMutableState
                    id.subscribe {
                        entry =
                            ItemAttributeModifiers.Entry(
                                entry.attribute,
                                AttributeModifier(it, entry.modifier.amount, entry.modifier.operation),
                                entry.slot
                            )
                        onValueChange(entry)
                    }
                    val amount = entry.modifier.amount.asMutableState
                    amount.subscribe {
                        entry =
                            ItemAttributeModifiers.Entry(
                                entry.attribute,
                                AttributeModifier(entry.modifier.id, it, entry.modifier.operation),
                                entry.slot
                            )
                        onValueChange(entry)
                    }
                    val operation = entry.modifier.operation.asMutableState
                    operation.subscribe {
                        entry =
                            ItemAttributeModifiers.Entry(entry.attribute, AttributeModifier(entry.modifier.id, entry.modifier.amount, it), entry.slot)
                        onValueChange(entry)
                    }

                    val slot = entry.slot.asMutableState
                    slot.subscribe {
                        entry = ItemAttributeModifiers.Entry(entry.attribute, entry.modifier, it)
                        onValueChange(entry)
                    }

                    val width = 294f

                    //type
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("type")
                        EntiryAttributeSelector(type, modifier = Modifier.width(160f))
                    }
                    //id
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("id")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5f)
                        ) {
                            Box(
                                Modifier.width(135f).padding(4f).renderBackground { guiGraphics, _, _, _ ->
                                    guiGraphics.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
                                }
                            ) {
                                Text(mutableStateBy { id.getValue().asTranslateText() })
                            }
                            Button(
                                Modifier.hoverText(IGLang.edit)
                            ) {
                                Icon(IconTextures.EDIT)
                                click {
                                    IdentifierComponentEditor(id.getValue().asTranslateText(), id.getValue()) { it, recompose ->
                                        if (id.getValue() != it) {
                                            id.setValue(it)
                                            if (recompose) executeRecompose()
                                        }
                                    }.open()
                                }
                            }
                        }
                    }
                    //amount
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("amount")
                        DoubleEditor(amount, modifier = Modifier.width(160f), editorModifier = { Modifier.weight(1) })
                    }
                    //operation
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("operation")
                        EnumSelector(operation, modifier = Modifier.width(160f))
                    }
                    //slot
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("slot")
                        EnumSelector(slot, modifier = Modifier.width(160f))
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

