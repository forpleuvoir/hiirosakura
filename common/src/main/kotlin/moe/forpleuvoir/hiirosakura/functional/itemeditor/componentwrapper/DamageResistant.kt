package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.util.rememberSegmentedButtonWidth
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DeathMessageType.INTENTIONAL_GAME_DESIGN
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.component.DamageResistant
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.jvm.optionals.getOrNull


@Composable
fun DamageResistantComponentWrapper(
    key: Identifier,
    value: DamageResistant,
    onValueChange: (DamageResistant) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        val count = value.types.count()
        var showDialog by remember { mutableStateOf(false) }

        val tip = if (count != 0) {
            Modifier.plainTooltip {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    value.types.take(10).forEach { type ->
                        Text(type.value().translatableText)
                    }
                }
            }
        } else Modifier
        AssistChip(
            {},
            modifier = Modifier
                .fillMaxHeight()
                .then(tip)
                .width(DataComponentEditorDefaults.entrySize.width),
            label = {
                Text(IGLang.ConfigWrapper.listConfigWrapperText(count), overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            trailingIcon = {
                IconButton(onClick = {
                    showDialog = true
                }) {
                    Icon(Icons.EditNote, null)
                }
            }
        )

        if (showDialog) {
            HolderSetDamageTypeEditorDialog(
                value = value.types,
                onValueChange = { onValueChange(DamageResistant(it)) },
                onDismissRequest = { showDialog = false },
                title = { Text(key) }
            )
        }
    }
}


@Composable
fun HolderSetDamageTypeEditorDialog(
    value: HolderSet<DamageType>,
    onValueChange: (HolderSet<DamageType>) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit
) {
    val firstTag = damageTypeTags.findFirst().getOrNull()
    var tag by remember {
        mutableStateOf(
            if (value is HolderSet.Named) value.key() else firstTag
        )
    }

    var mode by remember { mutableStateOf(value !is HolderSet.Named) }
    LaunchedEffect(tag) {
        if (tag == null) mode = true
    }
    val types = rememberKeyedList(value.map { it.value() }.toList())
    var nextKey by remember { mutableLongStateOf(types.size.toLong()) }

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val registry = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE)
            val list = HolderSet.direct(types.values().map { registry.wrapAsHolder(it) })
            onValueChange(
                if (mode) list
                else tag?.asHolderSet ?: list
            )
            true
        },
        content = {
            Column {
                firstTag?.let {
                    SingleChoiceSegmentedButtonRow {
                        val buttonTexts = listOf(
                            HSLang.ItemEditor.fromRegistry,
                            HSLang.ItemEditor.fromTag,
                        )
                        val width = rememberSegmentedButtonWidth(
                            items = buttonTexts,
                            textStyle = MaterialTheme.typography.labelLarge,
                        ) { it.toAnnotatedString() }
                        SegmentedButton(
                            selected = mode,
                            onClick = { mode = true },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.width(width),
                            label = { Text(HSLang.ItemEditor.fromRegistry) }
                        )
                        SegmentedButton(
                            selected = !mode,
                            onClick = { mode = false },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.width(width),
                            label = { Text(HSLang.ItemEditor.fromTag) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                AnimatedContent(
                    targetState = mode,
                    transitionSpec = {
                        if (targetState) {
                            // false -> true：新内容从左侧进入
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                        } else {
                            // true -> false：新内容从右侧进入
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                        }
                    },
                    label = "ModeSlide",
                ) { currentMode ->
                    if (currentMode) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                DamageTypeSelector(
                                    damageTypes.stream().findFirst().get(),
                                    { type ->
                                        if (!types.any { it.value == type }) {
                                            types.add(Keyed(nextKey++, type))
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 8.dp, bottom = 8.dp),
                                    content = { Text(HSLang.ItemEditor.addFromRegistry) }
                                )

                                firstTag?.let {
                                    Spacer(Modifier.width(12.dp))
                                    DamageTypeTagSelector(
                                        it,
                                        { tag ->
                                            tag.damageTypes.forEach { item ->
                                                if (!types.any { it.value == item.value() }) {
                                                    types.add(Keyed(nextKey++, item.value()))
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 8.dp, bottom = 8.dp),
                                        content = { Text(HSLang.ItemEditor.addFromTag) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedLabelBox(
                                { Text(HSLang.ItemEditor.tags) },
                            ) {
                                Box(Modifier.fillMaxWidth().height(460.dp)) {
                                    val lazyListState = rememberLazyListState()
                                    if (types.isEmpty()) {
                                        Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    } else {
                                        val hapticFeedback = LocalHapticFeedback.current
                                        val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                                            types.moveElement(from.index, to.index)
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                                        }
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(end = if (lazyListState.canScroll) 12.dp else 0.dp).fillMaxSize(),
                                            state = lazyListState
                                        ) {
                                            itemsIndexed(
                                                types,
                                                key = { _, keyed -> keyed.key }
                                            ) { index, (key, type) ->
                                                ReorderableItem(reorderableLazyListState, key) { isDragging ->
                                                    val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                                                    val handleInteraction = remember { MutableInteractionSource() }
                                                    val handleHovered by handleInteraction.collectIsHoveredAsState()
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth().scale(scale).plainTooltip {
                                                           Text(type.toString())
                                                        }
                                                    ) {
                                                        Row(
                                                            Modifier.padding(4.dp).fillMaxWidth(),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                            ) {
                                                                DragHandle(
                                                                    hapticFeedback,
                                                                    handleInteraction,
                                                                    handleHovered,
                                                                    isDragging
                                                                )
                                                                Text(type.translatableText)
                                                            }

                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                            ) {
                                                                RemoveConfirmButton(
                                                                    "",
                                                                    { types.removeAt(index) }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        VerticalScrollbar(
                                            adapter = rememberScrollbarAdapter(lazyListState),
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        )

                                    }
                                }
                            }
                        }

                    } else {
                        tag?.let { DamageTypeTagSelector(it, { tag = it }) }
                            ?: Text(IGLang.Misc.hasNothing)
                    }
                }
            }
        }
    )
}

internal val damageTypeTags
    get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE).tags.map { it.key() }

internal val TagKey<DamageType>.damageTypes
    get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE).getTagOrEmpty(this)

internal val damageTypes
    get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE)

private val TagKey<DamageType>.asHolderSet
    get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE).tags.filter {
        it.key().location == this.location
    }.findFirst().get()

internal val DamageType.translatableText
    get() = when (this.deathMessageType()) {
        INTENTIONAL_GAME_DESIGN -> Text.translatable(
            "death.attack.${this.msgId}.message",
            mc.player?.name?.plainText ?: "xx",
            ComponentUtils.wrapInSquareBrackets(Text.translatable("death.attack.${this.msgId}.link"))
        )

        else                    -> Text.translatable(
            "death.attack.${this.msgId}",
            mc.player?.name?.plainText ?: "xx",
            EntityType.PIG.description.plainText
        )
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DamageTypeTagSelector(
    selected: TagKey<DamageType>,
    onSelect: (TagKey<DamageType>) -> Unit,
    items: List<TagKey<DamageType>> = damageTypeTags.toList(),
    itemEquals: (TagKey<DamageType>, TagKey<DamageType>) -> Boolean = { a, b -> a == b },
    content: @Composable (TagKey<DamageType>) -> Unit = {
        Text(
            "#${it.location}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.plainTooltip {
                val types = it.damageTypes
                if (types.count() == 0) {
                    Text(IGLang.Misc.hasNothing)
                    return@plainTooltip
                } else {
                    Column {
                        types.take(20).forEach { type ->
                            Text(type.value().translatableText)
                        }
                        if (types.count() > 20) Text("...")
                    }
                }
            }
        )
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (TagKey<DamageType>, Boolean) -> Unit = { item, _ ->
        Text(
            "#${item.location}",
            modifier = Modifier.plainTooltip {
                val types = item.damageTypes
                if (types.count() == 0) {
                    Text(IGLang.Misc.hasNothing)
                    return@plainTooltip
                } else {
                    Column {
                        types.take(20).forEach { type ->
                            Text(type.value().translatableText)
                        }
                        if (types.count() > 20) Text("...")
                    }
                }
            }
        )
    },
    enabled: Boolean = true,
    searchFilter: ((String, TagKey<DamageType>) -> Boolean)? = { str, tag ->
        str in tag.location.toString() || tag.damageTypes.any {
            str in it.value().translatableText.plainText || str in it.value().msgId
        }
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<DamageType>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<DamageType>) -> Unit)?)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
) = Selector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    itemEquals = itemEquals,
    content = content,
    labelPosition = labelPosition,
    label = label,
    itemContent = itemContent,
    enabled = enabled,
    searchFilter = searchFilter,
    modifier = modifier,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    textStyle = textStyle,
    interactionSource = interactionSource,
    shape = shape,
    colors = colors,
    contentPadding = contentPadding
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DamageTypeSelector(
    selected: DamageType,
    onSelect: (DamageType) -> Unit,
    items: List<DamageType> = damageTypes.toList(),
    itemEquals: (DamageType, DamageType) -> Boolean = { a, b -> a == b },
    content: @Composable (DamageType) -> Unit = {
        Text(
            it.translatableText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.plainTooltip {
                Text(it.toString())
            }
        )
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (DamageType, Boolean) -> Unit = { item, _ ->
        Text(item.translatableText, modifier = Modifier.plainTooltip {
            Text(item.toString())
        })
    },
    enabled: Boolean = true,
    searchFilter: ((String, DamageType) -> Boolean)? = { str, type ->
        str in type.translatableText.plainText || str in type.msgId
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (DamageType) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (DamageType) -> Unit)?)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
) = Selector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    itemEquals = itemEquals,
    content = content,
    labelPosition = labelPosition,
    label = label,
    itemContent = itemContent,
    enabled = enabled,
    searchFilter = searchFilter,
    modifier = modifier,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    textStyle = textStyle,
    interactionSource = interactionSource,
    shape = shape,
    colors = colors,
    contentPadding = contentPadding
)
