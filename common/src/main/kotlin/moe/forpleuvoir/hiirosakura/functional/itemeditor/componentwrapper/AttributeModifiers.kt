package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayText

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplay
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.modifier.vanillaTooltip
import moe.forpleuvoir.hiirosakura.ui.widget.EntityAttributeSelector
import moe.forpleuvoir.hiirosakura.ui.widget.REGISTERED_ATTRIBUTE
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
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.DoubleField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import androidx.compose.foundation.layout.Row
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility

@Composable
fun AttributeModifiersComponentWrapper(
    key: Identifier,
    value: ItemAttributeModifiers,
    onValueChange: (ItemAttributeModifiers) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.vanillaTooltip(getDisplayTexts(value)),
        onEdit = { showDialog = true },
    ) {
        Text(
            component = IGLang.ConfigWrapper.listConfigWrapperText(value.modifiers.size),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

    if (showDialog) {
        ItemAttributeModifiersEditor(
            value,
            onValueChange,
            key,
            { DataComponentDialogTitle(key) },
            onDismissRequest = { showDialog = false }
        )
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

/**
 * 属性修饰符卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object ItemAttributeModifiersCardDefaults {

    /** 一张卡片的最小宽度：标签 + 编辑控件 + 卡片内边距。 */
    val MinCardWidth: Dp = 560.dp

    /** 一行的卡片数下限。 */
    const val MinColumns: Int = 1

    /** 一行的卡片数上限。 */
    const val MaxColumns: Int = 2

    /** 卡片间距，取卡片列表的缺省值（算列数时按它反推浮层宽度）。 */
    val CardSpacing: Dp = EditDialogContentDefaults.cardSpacing

    /** 卡片列表右侧细滚动条的占位宽度，算列数时先扣掉。 */
    val ScrollbarAllowance: Dp = 16.dp

    /** 浮层内容内边距，与 `FlexibleDialogDefaults.contentPadding` 一致。 */
    val DialogContentPadding: Dp = 24.dp

    /** 浮层宽度下限：一行放得下 [MinColumns] 张卡片。 */
    val DialogMinWidth: Dp
        get() = MinCardWidth * MinColumns + CardSpacing * (MinColumns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层宽度上限：一行最多 [MaxColumns] 张卡片。 */
    val DialogMaxWidth: Dp
        get() = MinCardWidth * MaxColumns + CardSpacing * (MaxColumns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层高度上限：可视区放得下两行卡片并有富余。 */
    val DialogMaxHeight: Dp = 1100.dp

    /** 浮层尺寸约束：宽度只夹上下限，具体宽度由窗口决定。 */
    val DialogModifier: Modifier
        get() = Modifier
            .widthIn(min = DialogMinWidth, max = DialogMaxWidth)
            .heightIn(max = DialogMaxHeight)

    /**
     * 按浮层内容区的可用宽度算卡片列数：先扣掉滚动条占位，再按「一张卡片 + 一段间距」整除，
     * 结果夹在 [MinColumns]..[MaxColumns] 内。
     */
    fun columnsFor(availableWidth: Dp): Int =
        ((availableWidth - ScrollbarAllowance + CardSpacing) / (MinCardWidth + CardSpacing))
            .toInt()
            .coerceIn(MinColumns, MaxColumns)
}

/**
 * 属性修饰符编辑浮层：一条修饰符一张卡片。
 *
 * 卡片头部两端是拖拽手柄与删除按钮（由 `EditDialogContentCards` 提供），卡片体是
 * [ModifierEntryContent]；条目增删、排序与编辑都在副本上做，确认时才写回。
 *
 * @param value 待编辑的属性修饰符组件
 * @param onValueChange 确认时的写回
 * @param key 语言键来源
 * @param title 浮层标题
 * @param onDismissRequest 关闭请求
 * @param dialogModifier 附加到浮层的尺寸修饰；缺省用卡片布局的下限 / 上限
 */
@Composable
fun ItemAttributeModifiersEditor(
    value: ItemAttributeModifiers,
    onValueChange: (ItemAttributeModifiers) -> Unit,
    key: Identifier,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    dialogModifier: Modifier = ItemAttributeModifiersCardDefaults.DialogModifier,
) {
    val editingModifiers = rememberKeyedList(value.modifiers)
    var showAddDialog by remember { mutableStateOf(false) }
    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            onValueChange(ItemAttributeModifiers(editingModifiers.entries.values().toMutableList()))
            true
        },
        modifier = dialogModifier,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fabScrollVisibility(addButtonVisibility),
            ) {
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {},
                ) { _ ->
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        EditDialogContentCards(
                            state = editingModifiers,
                            lazyGridState = cardGridState,
                            columns = ItemAttributeModifiersCardDefaults.columnsFor(maxWidth),
                            maxHeight = ItemAttributeModifiersCardDefaults.DialogMaxHeight,
                            removeButton = { index, _ ->
                                RemoveConfirmButton(
                                    message = "#${index + 1}",
                                    onConfirm = { editingModifiers.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, entry, onEntryChange ->
                            ModifierEntryContent(entry, onEntryChange, key)
                        }
                    }
                }

                EditDialogAddButton(
                    visibility = addButtonVisibility,
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        contentPadding = ConfigControlDefaults.IconButtonPadding,
                    ) {
                        Icon(Icons.Add, scale = configIconScale())
                    }
                }
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
                    title = { DataComponentDialogTitle(component = IGLang.Misc.add) },
                    content = {
                        ModifierEntryContent(addingEntry, { addingEntry = it }, key)
                    }
                )
            }
        }
    )
}

@Composable
private fun ModifierEntryContent(
    value: ItemAttributeModifiers.Entry,
    onValueChange: (ItemAttributeModifiers.Entry) -> Unit,
    key: Identifier
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        //attribute
        DataComponentField(key, suffix = "attribute", fallback = "Attribute", modifier = Modifier.fillMaxWidth()) {
            EntityAttributeSelector(
                value.attribute,
                { onValueChange(value.copy(attribute = it)) },
                content = {
                    Text(it.registeredName, overflow = TextOverflow.Ellipsis, maxLines = 1)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        //id
        var showIdDialog by remember { mutableStateOf(false) }

        DataComponentField(key, suffix = "id", fallback = "ID", modifier = Modifier.fillMaxWidth()) {
            DataComponentDisplay(
                modifier = Modifier.fillMaxWidth(),
                onEdit = { showIdDialog = true },
            ) {
                DataComponentDisplayText(Component.literal(value.modifier.id.toString()))
            }
        }

        if (showIdDialog) {
            IdentifierEditorDialog(
                value.modifier.id,
                { onValueChange(value.copy(id = it)) },
                { DataComponentDialogTitle(key, suffix = "id", fallback = "ID") },
                { showIdDialog = false })
        }
        //amount
        DataComponentField(key, suffix = "amount", fallback = "Amount", modifier = Modifier.fillMaxWidth()) {
            DoubleField(
                value = value.modifier.amount,
                onValueChange = { onValueChange(value.copy(amount = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        //operation
        DataComponentField(key, suffix = "operation", fallback = "Operation", modifier = Modifier.fillMaxWidth()) {
            EnumSelector(
                selected = value.modifier.operation,
                onSelect = { onValueChange(value.copy(operation = it)) },
                items = AttributeModifier.Operation.entries,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        //slot
        DataComponentField(key, suffix = "slot", fallback = "Slot", modifier = Modifier.fillMaxWidth()) {
            EnumSelector(
                selected = value.slot,
                onSelect = { onValueChange(value.copy(slot = it)) },
                items = EquipmentSlotGroup.entries,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        //display
        var showDisplayDialog by remember { mutableStateOf(false) }

        DataComponentField(key, suffix = "display", fallback = "Display", modifier = Modifier.fillMaxWidth()) {
            DataComponentDisplay(
                modifier = Modifier.fillMaxWidth(),
                onEdit = { showDisplayDialog = true },
            ) {
                var overrideText: Component? by remember(value.display) { mutableStateOf(null) }

                value.display.apply({
                    overrideText = it
                }, mc.player, value.attribute, value.modifier)

                overrideText?.let {
                    DataComponentDisplayText(it, modifier = Modifier.fillMaxWidth())
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
        }

        if (showDisplayDialog) {
            var editingType by remember { mutableStateOf(value.display.type()) }

            val state = remember {
                RichTextEditorState.fromMcText((value.display as? ItemAttributeModifiers.Display.OverrideText)?.component ?: Literal(""))
            }
            SimpleAlertDialog(
                onDismissRequest = { showDisplayDialog = false },
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
                title = { DataComponentDialogTitle(key, suffix = "display", fallback = "Display") },
                content = {
                    Column(Modifier.fillMaxWidth()) {
                        DisplayTypeSelector(editingType, { editingType = it }, key)
                        Spacer(Modifier.height(8.dp))
                        AnimatedVisibility(visible = editingType == OVERRIDE) {
                            RichTextEditor(
                                state = state,
                                modifier = Modifier.fillMaxWidth(),
                                enabledPreviewRender = editingType == OVERRIDE
                            )
                        }
                    }
                }
            )
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
