package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.ui.util.LocalRegistryAccess
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configControlHeight
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.core.HolderSet
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.jvm.optionals.getOrNull

internal val entityTypeTags
    @Composable get() = LocalRegistryAccess.current.lookupOrThrow(Registries.ENTITY_TYPE).tags.map { it.key() }

internal fun TagKey<EntityType<*>>.entityTypes(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.ENTITY_TYPE).getTagOrEmpty(this)

internal val entityTypes
    @Composable get() = LocalRegistryAccess.current.lookupOrThrow(Registries.ENTITY_TYPE).stream()

private fun TagKey<EntityType<*>>.asHolderSet(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.ENTITY_TYPE).tags.filter {
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
    val registryAccess = LocalRegistryAccess.current
    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val registry = registryAccess.lookupOrThrow(Registries.ENTITY_TYPE)
            val list = HolderSet.direct(types.entries.values().map { registry.wrapAsHolder(it) })
            onValueChange(
                if (mode) list
                else tag?.asHolderSet(registryAccess) ?: list
            )
            true
        },
        maxWidth = AlertDialogDefaults.maxWidth + 120.dp,
        content = {
            Column {
                firstTag?.let {
                    RadioButtonGroup(
                        selected = if (mode) 0 else 1,
                        onSelect = { mode = it == 0 },
                    ) {
                        item { Text(component = HSLang.ItemEditor.fromRegistry) }
                        item { Text(component = HSLang.ItemEditor.fromTag) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                AnimatedContent(
                    targetState = mode,
                    transitionSpec = {
                        if (targetState) {
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                        } else {
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                        }
                    },
                    label = "ModeSlide",
                ) { currentMode ->
                    if (currentMode) {
                        Column {
                            Row {
                                EntityTypeSelector(
                                    entityTypes.findFirst().get(),
                                    { type ->
                                        if (!types.entries.any { it.value == type }) {
                                            types.add(type)
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(configControlHeight()),
                                    content = { Text(component = HSLang.ItemEditor.addFromRegistry) }
                                )

                                firstTag?.let {
                                    Spacer(Modifier.width(12.dp))
                                    EntityTypeTagSelector(
                                        it,
                                        { tag ->
                                            tag.entityTypes(registryAccess).forEach { item ->
                                                if (!types.entries.any { keyed -> keyed.value == item.value() }) {
                                                    types.add(item.value())
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(configControlHeight()),
                                        content = { Text(component = HSLang.ItemEditor.addFromTag) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            DataComponentSection(component = HSLang.ItemEditor.entities) {
                                Row(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                                    val lazyListState = rememberLazyListState()
                                    if (types.entries.isEmpty()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    else {
                                        val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                                            types.move(from.index, to.index)
                                        }
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f).fillMaxHeight(),
                                            state = lazyListState
                                        ) {
                                            itemsIndexed(
                                                types.entries,
                                                key = { _, keyed -> keyed.key }
                                            ) { index, (key, type) ->
                                                ReorderableItem(
                                                    reorderableLazyListState,
                                                    key,
                                                ) { _ ->
                                                    Surface(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        CompositionLocalProvider(IconButtonDefaults.LocalMinSize provides DisplayFieldDefaults.ButtonMinSize) {
                                                            Row(
                                                                Modifier
                                                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                                                    .fillMaxWidth(),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                            ) {
                                                                DragHandle(modifier = Modifier.draggableHandle())
                                                                Spacer(Modifier.width(12.dp))
                                                                Text(
                                                                    type.description,
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .tooltip { Text(type.descriptionId) }
                                                                )
                                                                Spacer(Modifier.width(12.dp))
                                                                RemoveConfirmButton(
                                                                    type.description.string,
                                                                    onConfirm = { types.removeAt(index) },
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (lazyListState.canScroll) {
                                            Spacer(Modifier.width(8.dp))

                                            VerticalFlatScroller(
                                                adapter = rememberScrollerAdapter(lazyListState),
                                                modifier = Modifier.fillMaxHeight()
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        tag?.let { EntityTypeTagSelector(it, { tagKey -> tag = tagKey }) }
                            ?: Text(component = IGLang.Misc.hasNothing)
                    }
                }
            }
        }
    )
}


@Composable
fun EntityTypeSelector(
    selected: EntityType<*>,
    onSelect: (EntityType<*>) -> Unit,
    items: List<EntityType<*>> = entityTypes.toList(),
    itemEquals: (EntityType<*>, EntityType<*>) -> Boolean = { a, b -> a == b },
    content: @Composable (EntityType<*>) -> Unit = {
        Text(it.description, maxLines = 1, overflow = TextOverflow.Ellipsis)
    },
    itemContent: @Composable (EntityType<*>, Boolean) -> Unit = { item, _ ->
        Text(item.description)
    },
    enabled: Boolean = true,
    searchFilter: ((String, EntityType<*>) -> Boolean)? = { str, type ->
        str in type.description.plainText || str in type.descriptionId
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (EntityType<*>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (EntityType<*>) -> Unit)?)? = null
) {
    Selector(
        selected = selected,
        onSelect = onSelect,
        items = items,
        itemEquals = itemEquals,
        content = content,
        itemContent = itemContent,
        enabled = enabled,
        itemLeadingIcon = itemLeadingIcon,
        itemTrailingIcon = itemTrailingIcon,
        searchFilter = searchFilter?.let { filter -> { item, query -> filter(query, item) } },
        modifier = modifier.fillMaxWidth(),
    )
}


@Composable
fun EntityTypeTagSelector(
    selected: TagKey<EntityType<*>>,
    onSelect: (TagKey<EntityType<*>>) -> Unit,
    items: List<TagKey<EntityType<*>>> = entityTypeTags.toList(),
    itemEquals: (TagKey<EntityType<*>>, TagKey<EntityType<*>>) -> Boolean = { a, b -> a == b },
    registryAccess: RegistryAccess = LocalRegistryAccess.current,
    content: @Composable (TagKey<EntityType<*>>) -> Unit = {
        Text(
            "#${it.location}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.tooltip {
                val types = it.entityTypes(registryAccess)
                if (types.count() == 0) {
                    Text(component = IGLang.Misc.hasNothing)
                    return@tooltip
                } else {
                    Column {
                        types.take(15).forEach { type ->
                            Text(type.value().description)
                        }

                        if (types.count() > 15) Text("...")
                    }
                }
            }
        )
    },
    itemContent: @Composable (TagKey<EntityType<*>>, Boolean) -> Unit = { item, _ ->
        Text(
            "#${item.location}",
            modifier = Modifier.tooltip {
                val types = item.entityTypes(registryAccess)
                if (types.count() == 0) {
                    Text(component = IGLang.Misc.hasNothing)
                    return@tooltip
                } else {
                    Column {
                        types.take(15).forEach { type ->
                            Text(type.value().description)
                        }

                        if (types.count() > 15) Text("...")
                    }
                }
            }
        )
    },
    enabled: Boolean = true,
    searchFilter: ((String, TagKey<EntityType<*>>) -> Boolean)? = { str, tag ->
        str in tag.location.toString() || tag.entityTypes(registryAccess).any {
            str in it.value().description.plainText || str in it.value().descriptionId
        }
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<EntityType<*>>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<EntityType<*>>) -> Unit)?)? = null,
) {
    Selector(
        selected = selected,
        onSelect = onSelect,
        items = items,
        itemEquals = itemEquals,
        content = content,
        itemContent = itemContent,
        enabled = enabled,
        itemLeadingIcon = itemLeadingIcon,
        itemTrailingIcon = itemTrailingIcon,
        searchFilter = searchFilter?.let { filter -> { item, query -> filter(query, item) } },
        modifier = modifier.fillMaxWidth(),
    )
}
