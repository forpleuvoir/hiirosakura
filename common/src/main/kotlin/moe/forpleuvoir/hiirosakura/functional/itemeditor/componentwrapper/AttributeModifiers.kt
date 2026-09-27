package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.modifier.vanillaTooltip
import moe.forpleuvoir.hiirosakura.ui.widget.EntityAttributeSelector
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.REGISTERED_ATTRIBUTE
import moe.forpleuvoir.hiirosakura.ui.widget.hsItemAnimation
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditor
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditorState
import moe.forpleuvoir.hiirosakura.util.asTranslateKey
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.ItemAttributeModifiers.Display.Type.*
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.DoubleField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.hiirosakura.ui.util.rememberAdaptiveGridSpan
import androidx.compose.foundation.layout.Row
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility

@Composable
fun AttributeModifiersComponentWrapper(
    key: Identifier,
    value: ItemAttributeModifiers,
    onValueChange: (ItemAttributeModifiers) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        var showDialog by remember { mutableStateOf(false) }

        FlatButton(
            onClick = {},
            modifier = Modifier
                .fillMaxHeight()
                .vanillaTooltip(getDisplayTexts(value))
                .width(DataComponentEditorDefaults.entrySize.width),
        ) {
            Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.modifiers.size))
            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Edit)
            }
        }
        if (showDialog) {
            ItemAttributeModifiersEditor(
                value,
                onValueChange,
                key,
                { Text(key) },
                onDismissRequest = { showDialog = false }
            )
        }
    }
}

private fun getDisplayTexts(value: ItemAttributeModifiers) = buildList {
    var current = ""
    EquipmentSlotGroup.entries.forEach { slot ->
        value.forEach(slot) { attribute, modifier, display ->
            if (display != ItemAttributeModifiers.Display.hidden()) {
                val sloatText = Texts.translatable("item.modifiers.${slot.serializedName}").withStyle(ChatFormatting.GRAY)
                if (current != sloatText.plainText) {
                    add(sloatText)
                    current = sloatText.plainText
                }
            }
            display.apply({
                add(it)
            }, mc.player, attribute, modifier)
        }
    }
}

@Composable
fun ItemAttributeModifiersEditor(
    value: ItemAttributeModifiers,
    onValueChange: (ItemAttributeModifiers) -> Unit,
    key: Identifier,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit
) {
    val editingModifiers = rememberKeyedList(value.modifiers)
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            onValueChange(ItemAttributeModifiers(editingModifiers.entries.values().toMutableList()))
            true
        },
        modifier = Modifier.padding(24.dp),
        content = {
            BoxWithConstraints {
                Box(
                    modifier = Modifier
                        .height(820.dp)
                        .width(if (maxWidth > 1140.dp) 1140.dp else 780.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    val lazyGridState = rememberLazyGridState()
                    if (editingModifiers.entries.isEmpty()) {
                        Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val hapticFeedback = LocalHapticFeedback.current
                        val reorderableLazyGridState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
                            editingModifiers.move(from.index, to.index)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                        }


                        val canScroll = lazyGridState.canScrollBackward || lazyGridState.canScrollForward
                        LazyVerticalGrid(
                            state = lazyGridState,
                            columns = GridCells.Adaptive(360.dp),
                            modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxHeight(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            itemsIndexed(editingModifiers.entries, key = { _, v -> v.key }) { index, item ->
                                ReorderableItem(
                                    reorderableLazyGridState, item.key,
                                    animateItemModifier = hsItemAnimation(),
                                ) { isDragging ->
                                    val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                                    val handleInteraction = remember { MutableInteractionSource() }

                                    val handleHovered by handleInteraction.collectIsHoveredAsState()
                                    ModifierCard(
                                        item.value,
                                        {
                                            editingModifiers.setValue(index, it)
                                        },
                                        modifier = Modifier.scale(scale).width(360.dp),
                                        onRemove = {
                                            editingModifiers.removeAt(index)
                                        },
                                        key = key,
                                        hapticFeedback = hapticFeedback,
                                        handleInteraction = handleInteraction,
                                        handleHovered = handleHovered,
                                        isDragging = isDragging,
                                    )
                                }
                            }
                        }

                        VerticalScroller(
                            adapter = rememberScrollerAdapter(lazyGridState, rememberAdaptiveGridSpan(lazyGridState)),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }

                    var showAddDialog by remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            showAddDialog = true
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(40.dp)
                            .fabScrollVisibility(rememberFabScrollVisibility(lazyGridState)),
                    ) {
                        Icon(Icons.Add)
                    }

                    if (showAddDialog) {
                        var addingEntry by remember {
                            mutableStateOf(
                                ItemAttributeModifiers.Entry(
                                    REGISTERED_ATTRIBUTE.first(),
                                    AttributeModifier(
                                        Identifier.parse("minecraft:unknow"),
                                        0.0,
                                        AttributeModifier.Operation.ADD_VALUE
                                    ),
                                    EquipmentSlotGroup.MAINHAND
                                )
                            )
                        }
                        SimpleAlertDialog(
                            onDismissRequest = { showAddDialog = false },
                            onConfirmRequest = {
                                editingModifiers.add(addingEntry)
                                true
                            },
                            title = { Text(component = IGLang.Misc.add) },
                            content = {
                                ModifierEntryContent(addingEntry, { addingEntry = it }, key)
                            }
                        )
                    }
                }
            }
        }
    )


}

@Composable
private fun ReorderableCollectionItemScope.ModifierCard(
    value: ItemAttributeModifiers.Entry,
    onValueChange: (ItemAttributeModifiers.Entry) -> Unit,
    onRemove: () -> Unit,
    key: Identifier,
    hapticFeedback: HapticFeedback,
    handleInteraction: MutableInteractionSource,
    handleHovered: Boolean,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                DragHandle(modifier = Modifier)
                RemoveConfirmButton(
                    key.asTranslateText(suffix = "modifier", fallback = "Modifier").plainText,
                    onRemove
                )
            }
            ModifierEntryContent(value, onValueChange, key)
        }
    }
}

@Composable
private fun ModifierEntryContent(
    value: ItemAttributeModifiers.Entry,
    onValueChange: (ItemAttributeModifiers.Entry) -> Unit,
    key: Identifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        //attribute
        EntityAttributeSelector(
            value.attribute,
            { onValueChange(value.copy(attribute = it)) },
            label = { Text(key, suffix = "attribute", fallback = "Attribute") },
            content = {
                Text(it.registeredName, overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            modifier = Modifier.fillMaxWidth()
        )
        //id
        LabelBox(
            { Text(key, suffix = "id", fallback = "ID") },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp, 8.dp, 8.dp, 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var showDialog by remember { mutableStateOf(false) }

                Text(value.modifier.id, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.weight(1f))
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Edit)
                }

                if (showDialog) {
                    IdentifierEditorDialog(
                        value.modifier.id,
                        { onValueChange(value.copy(id = it)) },
                        { Text(key, suffix = "id", fallback = "ID") },
                        { showDialog = false })
                }
            }
        }
        //amount
        LabelBox(label = { Text(key, suffix = "amount", fallback = "Amount") }, modifier = Modifier.fillMaxWidth()) {
            DoubleField(
                value = value.modifier.amount,
                onValueChange = { onValueChange(value.copy(amount = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        //operation
        EnumSelector(
            value.modifier.operation,
            { onValueChange(value.copy(operation = it)) },
            label = { Text(key, suffix = "operation", fallback = "Operation") },
            modifier = Modifier.fillMaxWidth()
        )
        //slot
        EnumSelector(
            value.slot,
            { onValueChange(value.copy(slot = it)) },
            label = { Text(key, suffix = "slot", fallback = "Slot") },
            modifier = Modifier.fillMaxWidth()
        )
        //display
        LabelBox(
            { Text(key, suffix = "display", fallback = "Display") },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp, 8.dp, 8.dp, 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var showDialog by remember { mutableStateOf(false) }


                Column(modifier = Modifier.weight(1f)) {
                    var overrideText: Component? by remember(value.display) { mutableStateOf(null) }

                    value.display.apply({
                        overrideText = it
                    }, mc.player, value.attribute, value.modifier)

                    overrideText?.let {
                        Text(it, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.fillMaxWidth().vanillaTooltip(it))
                    } ?: run {
                        val typeKey = value.display.type().asTextKey(key)
                        val commentKey = "${typeKey}.comment"
                        Text(
                            value.display.type().asText(key),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth().thenIf(Language.getInstance().has(commentKey)) {
                                Modifier.tooltip {
                                    Text(Translatable(commentKey))
                                }
                            })
                    }
                }

                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Edit)
                }

                if (showDialog) {
                    var editingType by remember { mutableStateOf(value.display.type()) }

                    val state = remember {
                        RichTextEditorState.fromMcText((value.display as? ItemAttributeModifiers.Display.OverrideText)?.component ?: Literal(""))
                    }
                    SimpleAlertDialog(
                        onDismissRequest = { showDialog = false },
                        onConfirmRequest = {
                            onValueChange(
                                value.copy(
                                    display = when (editingType) {
                                        DEFAULT  -> ItemAttributeModifiers.Display.attributeModifiers()
                                        HIDDEN   -> ItemAttributeModifiers.Display.hidden()
                                        OVERRIDE -> ItemAttributeModifiers.Display.override(state.mcText)
                                    }
                                )
                            )
                            true
                        },
                        title = { Text(key, suffix = "display", fallback = "Display") },
                        content = {
                            Column(Modifier.fillMaxWidth()) {
                                DisplayTypeSelector(editingType, { editingType = it }, key)
                                Spacer(Modifier.height(8.dp))
                                AnimatedVisibility(visible = editingType == OVERRIDE) {
                                    RichTextEditor(
                                        state = state,
                                        modifier = Modifier.height(480.dp).width(720.dp),
                                        enabledPreviewRender = editingType == OVERRIDE
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DisplayTypeSelector(
    value: ItemAttributeModifiers.Display.Type,
    onValueChange: (ItemAttributeModifiers.Display.Type) -> Unit,
    key: Identifier,
) {
    val entries = ItemAttributeModifiers.Display.Type.entries
    Row(
        horizontalArrangement = Arrangement.spacedBy(RadioButtonDefaults.spacing),
    ) {
        entries.forEachIndexed { index, item ->
            val commentKey = "${item.asTextKey(key)}.comment"
            RadioButton(
                selected = value == item,
                index = index,
                count = entries.size,
                onSelect = { onValueChange(item) },
                modifier = Modifier.thenIf(Language.getInstance().has(commentKey)) {
                    Modifier.tooltip {
                        Text(Translatable(commentKey))
                    }
                },
            ) {
                Text(
                    item.asText(key),
                    maxLines = 1
                )
            }
        }
    }
}

fun ItemAttributeModifiers.Display.Type.asTextKey(key: Identifier) = when (this) {
    DEFAULT  -> key.asTranslateKey(suffix = "display.default")
    HIDDEN   -> key.asTranslateKey(suffix = "display.hidden")
    OVERRIDE -> key.asTranslateKey(suffix = "display.override")
}

fun ItemAttributeModifiers.Display.Type.asText(key: Identifier) = when (this) {
    DEFAULT  -> key.asTranslateText(suffix = "display.default", fallback = "Default")
    HIDDEN   -> key.asTranslateText(suffix = "display.hidden", fallback = "Hidden")
    OVERRIDE -> key.asTranslateText(suffix = "display.override", fallback = "Override")
}

fun ItemAttributeModifiers.Entry.copy(
    attribute: Holder<Attribute> = this.attribute,
    id: Identifier = this.modifier.id,
    amount: Double = this.modifier.amount,
    operation: AttributeModifier.Operation = this.modifier.operation,
    slot: EquipmentSlotGroup = this.slot,
    display: ItemAttributeModifiers.Display = this.display
): ItemAttributeModifiers.Entry = ItemAttributeModifiers.Entry(
    attribute, AttributeModifier(id, amount, operation), slot, display
)
