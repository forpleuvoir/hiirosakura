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
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffect
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.jvm.optionals.getOrNull


internal val mobEffectTags
    @Composable get() = LocalRegistryAccess.current.lookupOrThrow(Registries.MOB_EFFECT).tags.map { it.key() }

internal fun TagKey<MobEffect>.mobEffects(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.MOB_EFFECT).getTagOrEmpty(this)

internal val mobEffects
    @Composable get() = LocalRegistryAccess.current.lookupOrThrow(Registries.MOB_EFFECT).stream()

private fun TagKey<MobEffect>.asHolderSet(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.MOB_EFFECT).tags.filter {
        it.key().location == this.location
    }.findFirst().get()

@Composable
fun HolderSetMobEffectEditorDialog(
    value: HolderSet<MobEffect>,
    onValueChange: (HolderSet<MobEffect>) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit
) {

    val firstTag = mobEffectTags.findFirst().getOrNull()
    var tag by remember {
        mutableStateOf(if (value is HolderSet.Named) value.key() else firstTag)
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
            val registry = registryAccess.lookupOrThrow(Registries.MOB_EFFECT)
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
                //这个好像没有Tag
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
                                MobEffectSelector(
                                    mobEffects.findFirst().get(),
                                    { type ->
                                        if (!types.entries.any { it.value == type }) {
                                            types.add(type)
                                        }
                                    },
                                    items = BuiltInRegistries.MOB_EFFECT.toList() - types.values.toSet(),
                                    modifier = Modifier.weight(1f).height(configControlHeight()),
                                    content = { Text(component = HSLang.ItemEditor.addFromRegistry) }
                                )
                                firstTag?.let {
                                    Spacer(Modifier.width(12.dp))
                                    MobEffectTagSelector(
                                        firstTag,
                                        { tag ->
                                            tag.mobEffects(registryAccess).forEach { item ->
                                                if (!types.entries.any { it.value == item.value() }) {
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
                            DataComponentSection(component = HSLang.ItemEditor.mobEffects) {
                                Row(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                                    val lazyListState = rememberLazyListState()
                                    if (types.entries.isEmpty()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
                                        }
                                    } else {
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
                                                    reorderableLazyListState, key,
                                                    animateItemModifier = hsItemAnimation(),
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
                                                                    type.displayName,
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .tooltip { Text(type.descriptionId) }
                                                                )
                                                                Spacer(Modifier.width(12.dp))
                                                                RemoveConfirmButton(
                                                                    type.displayName.string,
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
                        tag?.let {
                            MobEffectTagSelector(it, { newTag -> tag = newTag })
                        } ?: Text(component = IGLang.Misc.hasNothing)
                    }
                }
            }
        }
    )
}


@Composable
fun MobEffectTagSelector(
    selected: TagKey<MobEffect>,
    onSelect: (TagKey<MobEffect>) -> Unit,
    items: List<TagKey<MobEffect>> = mobEffectTags.toList(),
    itemEquals: (TagKey<MobEffect>, TagKey<MobEffect>) -> Boolean = { a, b -> a == b },
    registryAccess: RegistryAccess = LocalRegistryAccess.current,
    content: @Composable (TagKey<MobEffect>) -> Unit = {
        Text(
            "#${it.location}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.tooltip {
                val types = it.mobEffects(registryAccess)
                if (types.count() == 0) {
                    Text(component = IGLang.Misc.hasNothing)
                    return@tooltip
                } else {
                    Column {
                        types.take(20).forEach { type ->
                            Text(type.value().displayName)
                        }

                        if (types.count() > 20) Text("...")
                    }
                }
            }
        )
    },
    itemContent: @Composable (TagKey<MobEffect>, Boolean) -> Unit = { item, _ ->
        Text(
            "#${item.location}",
            modifier = Modifier.tooltip {
                val types = item.mobEffects(registryAccess)
                if (types.count() == 0) {
                    Text(component = IGLang.Misc.hasNothing)
                    return@tooltip
                } else {
                    Column {
                        types.take(20).forEach { type ->
                            Text(type.value().displayName)
                        }

                        if (types.count() > 20) Text("...")
                    }
                }
            }
        )
    },
    enabled: Boolean = true,
    searchFilter: ((String, TagKey<MobEffect>) -> Boolean)? = { str, tag ->
        str in tag.location.toString() || tag.mobEffects(registryAccess).any {
            str in it.value().displayName.plainText || str in it.value().descriptionId
        }
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<MobEffect>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<MobEffect>) -> Unit)?)? = null,
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
