package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentList

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DeathMessageType.INTENTIONAL_GAME_DESIGN
import net.minecraft.world.item.component.DamageResistant
import kotlin.jvm.optionals.getOrNull
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButtonGroup
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import androidx.compose.foundation.layout.Row
import net.minecraft.world.entity.EntityTypes


@Composable
fun DamageResistantComponentWrapper(
    key: Identifier,
    value: DamageResistant,
    onValueChange: (DamageResistant) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    val count = value.types.count()
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.thenIf(count != 0) {
            Modifier.tooltip {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    value.types.take(10).forEach { type ->
                        Text(type.value().translatableText)
                    }
                }
            }
        },
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(count), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    if (showDialog) {
        HolderSetDamageTypeEditorDialog(
            value = value.types,
            onValueChange = { onValueChange(DamageResistant(it)) },
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

/**
 * 伤害类型卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object DamageTypeCardDefaults {

    /** 一张卡片的最小宽度：伤害类型名称一行加卡片内边距。 */
    val MinCardWidth: Dp = 360.dp

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
    val DialogMaxHeight: Dp = 900.dp

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

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val registry = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE)
            val list = HolderSet.direct(types.entries.values().map { registry.wrapAsHolder(it) })
            onValueChange(
                if (mode) list
                else tag?.asHolderSet ?: list
            )
            true
        },
        modifier = DamageTypeCardDefaults.DialogModifier,
        content = {
            EditDialogContent(
                modifier = Modifier.fillMaxSize(),
                header = {
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
                        if (mode) {
                            Text(
                                component = HSLang.ItemEditor.tags,
                                fontSize = SokitsuTheme.typography.body.fontSize,
                            )
                        }
                    }
                },
            ) { listState ->
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
                            Row(verticalAlignment = Alignment.Bottom) {
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
                                            tag.damageTypes.forEach { item ->
                                                if (!types.entries.any { it.value == item.value() }) {
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
                            EditDialogContentList(
                                state = types,
                                lazyListState = listState,
                                maxHeight = DamageTypeCardDefaults.DialogMaxHeight,
                                removeButton = { index, entry ->
                                    RemoveConfirmButton(
                                        message = entry.translatableText.plainText,
                                        onConfirm = { types.removeAt(index) },
                                        iconScale = LocalIconScale.current,
                                        contentPadding = EditDialogContentDefaults.iconPadding,
                                    )
                                },
                                columns = {
                                    column(
                                        width = weight(1f),
                                        alignment = Alignment.CenterStart,
                                    ) { _, entry ->
                                        Text(
                                            entry.value.translatableText,
                                            modifier = Modifier.tooltip {
                                                Text(entry.value.toString())
                                            }
                                        )
                                    }
                                },
                            )
                        }

                    } else {
                        tag?.let { DamageTypeTagSelector(it, { tag = it }) }
                            ?: Text(component = IGLang.Misc.hasNothing)
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
            EntityTypes.PIG.description.plainText
        )
    }

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
            modifier = Modifier.tooltip {
                val types = it.damageTypes
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
                val types = item.damageTypes
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
        str in tag.location.toString() || tag.damageTypes.any {
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
