package moe.forpleuvoir.hiirosakura.ui.widget

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
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.util.rememberSegmentedButtonWidth
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.jvm.optionals.getOrNull

internal val entityTypeTags
    get() = registryAccess!!.lookupOrThrow(Registries.ENTITY_TYPE).tags.map { it.key() }

internal val TagKey<EntityType<*>>.entityTypes
    get() = registryAccess!!.lookupOrThrow(Registries.ENTITY_TYPE).getTagOrEmpty(this)

internal val entityTypes
    get() = registryAccess!!.lookupOrThrow(Registries.ENTITY_TYPE).stream()

private val TagKey<EntityType<*>>.asHolderSet
    get() = registryAccess!!.lookupOrThrow(Registries.ENTITY_TYPE).tags.filter {
        it.key().location == this.location
    }.findFirst().get()

@Composable
fun HolderSetEntityTypeEditorDialog(
    value: HolderSet<EntityType<*>>,
    onValueChange: (HolderSet<EntityType<*>>) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit
) {
    val firstTag = entityTypeTags.findFirst().getOrNull()
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
            val registry = registryAccess!!.lookupOrThrow(Registries.ENTITY_TYPE)
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
                                EntityTypeSelector(
                                    entityTypes.findFirst().get(),
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
                                    EntityTypeTagSelector(
                                        it,
                                        { tag ->
                                            tag.entityTypes.forEach { item ->
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
                                                        modifier = Modifier.fillMaxWidth().scale(scale)
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
                                                                Text(type.description)
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
                        tag?.let { EntityTypeTagSelector(it, { tag = it }) }
                            ?: Text(IGLang.Misc.hasNothing)
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityTypeSelector(
    selected: EntityType<*>,
    onSelect: (EntityType<*>) -> Unit,
    items: List<EntityType<*>> = entityTypes.toList(),
    itemEquals: (EntityType<*>, EntityType<*>) -> Boolean = { a, b -> a == b },
    content: @Composable (EntityType<*>) -> Unit = {
        Text(it.description, maxLines = 1, overflow = TextOverflow.Ellipsis)
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (EntityType<*>, Boolean) -> Unit = { item, _ ->
        Text(item.description)
    },
    enabled: Boolean = true,
    searchFilter: ((String, EntityType<*>) -> Boolean)? = { str, type ->
        str in type.description.plainText || str in type.descriptionId
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (EntityType<*>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (EntityType<*>) -> Unit)?)? = null,
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
fun EntityTypeTagSelector(
    selected: TagKey<EntityType<*>>,
    onSelect: (TagKey<EntityType<*>>) -> Unit,
    items: List<TagKey<EntityType<*>>> = entityTypeTags.toList(),
    itemEquals: (TagKey<EntityType<*>>, TagKey<EntityType<*>>) -> Boolean = { a, b -> a == b },
    content: @Composable (TagKey<EntityType<*>>) -> Unit = {
        Text(
            "#${it.location}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.plainTooltip {
                val types = it.entityTypes
                if (types.count() == 0) {
                    Text(IGLang.Misc.hasNothing)
                    return@plainTooltip
                } else {
                    Column {
                        types.take(20).forEach { type ->
                            Text(type.value().description)
                        }
                        if (types.count() > 20) Text("...")
                    }
                }
            }
        )
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (TagKey<EntityType<*>>, Boolean) -> Unit = { item, _ ->
        Text(
            "#${item.location}",
            modifier = Modifier.plainTooltip {
                val types = item.entityTypes
                if (types.count() == 0) {
                    Text(IGLang.Misc.hasNothing)
                    return@plainTooltip
                } else {
                    Column {
                        types.take(20).forEach { type ->
                            Text(type.value().description)
                        }
                        if (types.count() > 20) Text("...")
                    }
                }
            }
        )
    },
    enabled: Boolean = true,
    searchFilter: ((String, TagKey<EntityType<*>>) -> Boolean)? = { str, tag ->
        str in tag.location.toString() || tag.entityTypes.any {
            str in it.value().description.plainText || str in it.value().descriptionId
        }
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<EntityType<*>>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<EntityType<*>>) -> Unit)?)? = null,
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