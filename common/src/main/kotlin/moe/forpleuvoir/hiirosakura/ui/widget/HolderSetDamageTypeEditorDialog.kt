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
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.LocalRegistryAccess
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.HolderSet
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DeathMessageType.INTENTIONAL_GAME_DESIGN
import net.minecraft.world.entity.EntityTypes
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.jvm.optionals.getOrNull

/**
 * 伤害类型编辑浮层：表头是来源模式开关与新增控件，下面是「一条伤害类型一张卡片」的卡片网格。
 *
 * 卡片头部的拖拽手柄与删除按钮由 `EditDialogContentCards` 提供，卡片体是伤害类型的死亡消息文本；
 * 类型增删、排序都在副本上做，确认时才写回。
 *
 * @param value 待编辑的伤害类型集合
 * @param onValueChange 确认时的写回
 * @param onDismissRequest 关闭请求
 * @param title 浮层标题
 */
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

    val registryAccess = LocalRegistryAccess.current
    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val registry = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE)
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
                                DamageTypeSelector(
                                    damageTypes.stream().findFirst().get(),
                                    { type ->
                                        if (!types.entries.any { it.value == type }) {
                                            types.add(type)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    content = { Text(component = HSLang.ItemEditor.addFromRegistry) }
                                )

                                firstTag?.let {
                                    Spacer(Modifier.width(12.dp))
                                    DamageTypeTagSelector(
                                        it,
                                        { tag ->
                                            tag.damageTypes(registryAccess).forEach { item ->
                                                if (!types.entries.any { keyed -> keyed.value == item.value() }) {
                                                    types.add(item.value())
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        content = { Text(component = HSLang.ItemEditor.addFromTag) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            DataComponentSection(HSLang.ItemEditor.damageTypes) {
                                Row(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                                    val lazyListState = rememberLazyListState()
                                    if (types.entries.isEmpty()) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            moe.forpleuvoir.ibukigourd.ui.sokitsu.Text(
                                                component = IGLang.Misc.hasNothing,
                                                color = SokitsuTheme.colorScheme.onSurfaceVariant
                                            )
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
                                                                    component = type.translatableText,
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .tooltip { Text(type.toString()) }
                                                                )
                                                                Spacer(Modifier.width(12.dp))
                                                                RemoveConfirmButton(
                                                                    type.translatableText.string,
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
                        tag?.let { DamageTypeTagSelector(it, { tagKey -> tag = tagKey }) }
                            ?: Text(component = IGLang.Misc.hasNothing)
                    }
                }
            }
        }
    )
}

internal val damageTypeTags
    @Composable get() = LocalRegistryAccess.current.lookupOrThrow(Registries.DAMAGE_TYPE).tags.map { it.key() }

internal fun TagKey<DamageType>.damageTypes(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getTagOrEmpty(this)

internal val damageTypes
    @Composable get() = LocalRegistryAccess.current.lookupOrThrow(Registries.DAMAGE_TYPE)

internal fun TagKey<DamageType>.asHolderSet(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).tags.filter {
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
            EntityTypes.PIG.description.plainText
        )
    }

@Composable
fun DamageTypeTagSelector(
    selected: TagKey<DamageType>,
    onSelect: (TagKey<DamageType>) -> Unit,
    items: List<TagKey<DamageType>> = damageTypeTags.toList(),
    itemEquals: (TagKey<DamageType>, TagKey<DamageType>) -> Boolean = { a, b -> a == b },
    registryAccess: RegistryAccess = LocalRegistryAccess.current,
    content: @Composable (TagKey<DamageType>) -> Unit = {
        Text(
            "#${it.location}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.tooltip {
                val types = it.damageTypes(registryAccess)
                if (types.count() == 0) {
                    Text(component = IGLang.Misc.hasNothing)
                    return@tooltip
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
    itemContent: @Composable (TagKey<DamageType>, Boolean) -> Unit = { item, _ ->
        Text(
            "#${item.location}",
            modifier = Modifier.tooltip {
                val types = item.damageTypes(registryAccess)
                if (types.count() == 0) {
                    Text(component = IGLang.Misc.hasNothing)
                    return@tooltip
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
        str in tag.location.toString() || tag.damageTypes(registryAccess).any {
            str in it.value().translatableText.plainText || str in it.value().msgId
        }
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<DamageType>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<DamageType>) -> Unit)?)? = null,
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
        modifier = modifier,
    )
}


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
            modifier = Modifier.tooltip {
                Text(it.toString())
            }
        )
    },
    itemContent: @Composable (DamageType, Boolean) -> Unit = { item, _ ->
        Text(item.translatableText, modifier = Modifier.tooltip {
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
        modifier = modifier,
    )
}
