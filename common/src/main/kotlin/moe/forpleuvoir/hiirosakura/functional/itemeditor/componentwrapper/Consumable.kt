package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplay
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.*
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.component.Consumable
import net.minecraft.world.item.consume_effects.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import androidx.compose.foundation.layout.fillMaxWidth
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility

@Composable
fun ConsumableComponentWrapper(
    key: Identifier,
    value: Consumable,
    onValueChange: (Consumable) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.onConsumeEffects.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    if (showDialog) {
        ConsumableEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

/**
 * 消耗效果卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object ConsumableCardDefaults {

    /** 一张卡片的最小宽度：效果内容区（`ConsumableEditor.LocalConsumeEffectWrapperSize`）加卡片内边距。 */
    val MinCardWidth: Dp = 420.dp

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
 * 消耗组件编辑浮层：表头一行四项固定属性，下面是「一条消耗效果一张卡片」的卡片网格。
 *
 * 卡片头部的拖拽手柄与删除按钮由 `EditDialogContentCards` 提供，卡片体是 [ConsumeEffectContent]；
 * 效果增删、排序与编辑都在副本上做，确认时才写回。
 *
 * @param key 语言键来源
 * @param value 待编辑的消耗组件
 * @param onValueChange 确认时的写回
 * @param onDismissRequest 关闭请求
 * @param title 浮层标题
 */
@Composable
fun ConsumableEditorDialog(
    key: Identifier,
    value: Consumable,
    onValueChange: (Consumable) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var editing by remember { mutableStateOf(value) }

    val onConsumeEffects = rememberKeyedList(value.onConsumeEffects)
    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(editing.copy(onConsumeEffects = onConsumeEffects.entries.values().toMutableList()))
            true
        },
        modifier = ConsumableCardDefaults.DialogModifier,
        title = title,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fabScrollVisibility(addButtonVisibility),
            ) {
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                DataComponentSection(key, suffix = "consume_seconds", modifier = Modifier.weight(1f)) {
                                    FloatField(
                                        editing.consumeSeconds,
                                        { editing = editing.copy(consumeSeconds = it) },
                                        valueRange = 0f..64.0f,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                                DataComponentSection(key, suffix = "animation", modifier = Modifier.weight(1f)) {
                                    EnumSelector(
                                        selected = editing.animation,
                                        onSelect = { editing = editing.copy(animation = it) },
                                        items = ItemUseAnimation.entries,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                                HolderSoundEventSelector(
                                    editing.sound,
                                    { editing = editing.copy(sound = it) },
                                    modifier = Modifier.weight(1f)
                                )
                                DataComponentSection(
                                    key,
                                    suffix = "has_consume_particles",
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Switch(
                                            editing.hasConsumeParticles,
                                            { editing = editing.copy(hasConsumeParticles = it) },
                                        )
                                        Text(component = IGLang.Misc.coloredSwitch(editing.hasConsumeParticles))
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(key, suffix = "on_consume_effects", fontSize = SokitsuTheme.typography.body.fontSize)
                        }
                    },
                ) { _ ->
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        EditDialogContentCards(
                            state = onConsumeEffects,
                            lazyGridState = cardGridState,
                            columns = ConsumableCardDefaults.columnsFor(maxWidth),
                            maxHeight = ConsumableCardDefaults.DialogMaxHeight,
                            removeButton = { index, entry ->
                                RemoveConfirmButton(
                                    message = entry.type.id.asTranslateText().plainText,
                                    onConfirm = { onConsumeEffects.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, entry, onEntryChange ->
                            ConsumeEffectContent(entry, onEntryChange)
                        }
                    }
                }

                EditDialogAddButton(
                    visibility = addButtonVisibility,
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    FloatingAddButton(
                        fabMargin = PaddingValues(0.dp),
                        addMenuOptions = ConsumableEditor.consumeEffectAddMenuOptions,
                        addAction = { onConsumeEffects.add(it) },
                    )
                }
            }
        }
    )
}

/** 一条消耗效果的卡片体：效果类型标题 + 该类型自己的编辑控件。 */
@Composable
private fun ConsumeEffectContent(
    effect: ConsumeEffect,
    onValueChange: (ConsumeEffect) -> Unit,
) {
    val wrapper = ConsumableEditor.consumeEffectsWrappers[effect.type]
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        wrapper?.title()
        wrapper?.content(effect, onValueChange)
    }
}

object ConsumableEditor {

    val LocalConsumeEffectWrapperSize = staticCompositionLocalOf {
        DpSize(280.dp, 56.dp)
    }


    val consumeEffectsWrappers = mutableMapOf<ConsumeEffect.Type<*>, ConsumeEffectWrapper>(
        ConsumeEffect.Type.APPLY_EFFECTS to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.APPLY_EFFECTS.id) },
            content = { value, onValueChange ->
                val value = value as ApplyStatusEffectsConsumeEffect
                Box(
                    modifier = Modifier.size(LocalConsumeEffectWrapperSize.current).padding(vertical = 4.dp),
                ) {
                    var showDialog by remember { mutableStateOf(false) }
                    DataComponentDisplay(
                        modifier = Modifier.tooltip {
                            value.effects.take(20).forEach {
                                Row {
                                    Text(it.effect.value().displayName)
                                    Spacer(Modifier.width(8.dp))
                                    Text(Texts.translatable("enchantment.level.${it.amplifier}", it.amplifier.toString()))
                                }
                            }

                            if (value.effects.size > 20) Text("...")
                        },
                        onEdit = { showDialog = true },
                    ) {
                        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.effects.size))
                    }
                    if (showDialog) {
                        ApplyStatusEditorDialog({ showDialog = false }, value, { onValueChange(it) })
                    }
                }
            }
        ),
        ConsumeEffect.Type.REMOVE_EFFECTS to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.REMOVE_EFFECTS.id) },
            content = { value, onValueChange ->
                val value = value as RemoveStatusEffectsConsumeEffect
                Box(
                    modifier = Modifier.size(LocalConsumeEffectWrapperSize.current).padding(vertical = 4.dp),
                ) {
                    var showDialog by remember { mutableStateOf(false) }
                    DataComponentDisplay(
                        modifier = Modifier.tooltip {
                            value.effects.take(20).forEach {
                                Row {
                                    Text(it.value().displayName)
                                }
                            }

                            if (value.effects.count() > 20) Text("...")
                        },
                        onEdit = { showDialog = true },
                    ) {
                        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.effects.count()))
                    }
                    if (showDialog) {
                        RemoveStatusEditorDialog({ showDialog = false }, value, { onValueChange(it) })
                    }
                }
            }
        ),
        ConsumeEffect.Type.CLEAR_ALL_EFFECTS to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.CLEAR_ALL_EFFECTS.id) },
            content = { _, _ -> Spacer(Modifier.size(LocalConsumeEffectWrapperSize.current)) }
        ),
        ConsumeEffect.Type.TELEPORT_RANDOMLY to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.TELEPORT_RANDOMLY.id) },
            content = { value, onValueChange ->
                val value = value as TeleportRandomlyConsumeEffect
                Box(
                    modifier = Modifier.size(LocalConsumeEffectWrapperSize.current).padding(vertical = 4.dp),
                ) {
                    FloatField(
                        value.diameter,
                        { onValueChange(TeleportRandomlyConsumeEffect(it)) },
                        valueRange = 0f..Float.MAX_VALUE,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        ),
        ConsumeEffect.Type.PLAY_SOUND to ConsumeEffectWrapper(
            title = { Text(ConsumeEffect.Type.PLAY_SOUND.id) },
            content = { value, onValueChange ->
                val value = value as PlaySoundConsumeEffect
                Box(
                    modifier = Modifier.size(LocalConsumeEffectWrapperSize.current).padding(vertical = 4.dp),
                ) {
                    HolderSoundEventSelector(
                        value.sound,
                        { onValueChange(PlaySoundConsumeEffect(it)) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        )
    )

    class ConsumeEffectWrapper(
        val title: @Composable () -> Unit,
        val content: @Composable (value: ConsumeEffect, onValueChange: (ConsumeEffect) -> Unit) -> Unit
    )

    val consumeEffectAddMenuOptions: MutableList<AddMenuOption<ConsumeEffect>> = mutableListOf(
        AddMenuOption(
            { Text(ConsumeEffect.Type.APPLY_EFFECTS.id) },
            { ApplyStatusEffectsConsumeEffect(MobEffectInstance(MobEffects.LUCK)) }
        ) { addAction, defaultValue, onDismiss ->
            ApplyStatusEditorDialog(onDismiss, defaultValue() as ApplyStatusEffectsConsumeEffect, addAction)
        },
        AddMenuOption(
            { Text(ConsumeEffect.Type.REMOVE_EFFECTS.id) },
            { RemoveStatusEffectsConsumeEffect(HolderSet.empty()) }
        ) { addAction, defaultValue, onDismiss ->
            RemoveStatusEditorDialog(onDismiss, defaultValue() as RemoveStatusEffectsConsumeEffect, addAction)
        },
        AddMenuOption(
            { Text(ConsumeEffect.Type.CLEAR_ALL_EFFECTS.id) },
            { ClearAllStatusEffectsConsumeEffect.INSTANCE },
            null
        ),
        AddMenuOption(
            { Text(ConsumeEffect.Type.TELEPORT_RANDOMLY.id) },
            { TeleportRandomlyConsumeEffect() },
            null
        ),
        AddMenuOption(
            { Text(ConsumeEffect.Type.PLAY_SOUND.id) },
            { PlaySoundConsumeEffect(BuiltInRegistries.SOUND_EVENT.get(0).get()) },
            null
        )
    )

}


//region ApplyStatus

/**
 * 状态效果卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object MobEffectCardDefaults {

    /** 一张卡片的最小宽度：状态效果选择器 + 时长 / 倍率输入框 + 卡片内边距。 */
    val MinCardWidth: Dp = 360.dp

    /** 一行的卡片数下限。 */
    const val MinColumns: Int = 2

    /** 一行的卡片数上限。 */
    const val MaxColumns: Int = 3

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
 * 状态效果编辑浮层：表头是触发概率，下面是「一条状态效果一张卡片」的卡片网格。
 *
 * 卡片头部的拖拽手柄与删除按钮由 `EditDialogContentCards` 提供，卡片体是
 * [MobEffectInstanceEditorContent]；效果增删、排序与编辑都在副本上做，确认时才写回。
 */
@Composable
fun ApplyStatusEditorDialog(
    onDismissRequest: () -> Unit,
    value: ApplyStatusEffectsConsumeEffect,
    onValueChange: (ApplyStatusEffectsConsumeEffect) -> Unit,
) {
    var probability by remember { mutableFloatStateOf(value.probability) }

    val list = rememberKeyedList(value.effects)

    val id = ConsumeEffect.Type.APPLY_EFFECTS.id
    var showAddDialog by remember { mutableStateOf(false) }
    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(ApplyStatusEffectsConsumeEffect(list.entries.values().toMutableList(), probability))
            true
        },
        title = { DataComponentDialogTitle(id) },
        modifier = MobEffectCardDefaults.DialogModifier,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fabScrollVisibility(addButtonVisibility),
            ) {
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
                        DataComponentField(id, suffix = "probability") {
                            FloatField(
                                probability,
                                { probability = it },
                                valueRange = 0f..1f,
                            )
                        }
                    },
                ) { _ ->
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        EditDialogContentCards(
                            state = list,
                            lazyGridState = cardGridState,
                            columns = MobEffectCardDefaults.columnsFor(maxWidth),
                            maxHeight = MobEffectCardDefaults.DialogMaxHeight,
                            removeButton = { index, _ ->
                                RemoveConfirmButton(
                                    message = "#${index + 1}",
                                    onConfirm = { list.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, entry, onEntryChange ->
                            MobEffectInstanceEditorContent(entry, onEntryChange, id)
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
                MobEffectInstanceEditorDialog(
                    MobEffectInstance(MobEffects.LUCK),
                    { list.add(it) },
                    id,
                    { showAddDialog = false },
                    {
                        Row {
                            DataComponentDialogTitle(component = IGLang.Misc.add)
                            Spacer(Modifier.width(8.dp))
                            DataComponentDialogTitle(id)
                        }
                    }
                )
            }
        }
    )
}

//endregion

//region RemoveStatus
@Composable
fun RemoveStatusEditorDialog(
    onDismissRequest: () -> Unit,
    value: RemoveStatusEffectsConsumeEffect,
    onValueChange: (RemoveStatusEffectsConsumeEffect) -> Unit,
) {
    HolderSetMobEffectEditorDialog(
        value.effects,
        { onValueChange(RemoveStatusEffectsConsumeEffect(it)) },
        onDismissRequest = onDismissRequest,
        { DataComponentDialogTitle(ConsumeEffect.Type.CLEAR_ALL_EFFECTS.id) }
    )
}

//endregion

fun Consumable.copy(
    consumeSeconds: Float = this.consumeSeconds,
    animation: ItemUseAnimation = this.animation,
    sound: Holder<SoundEvent> = this.sound,
    hasConsumeParticles: Boolean = this.hasConsumeParticles,
    onConsumeEffects: List<ConsumeEffect> = this.onConsumeEffects,
) = Consumable(
    consumeSeconds.coerceIn(0f..Float.MAX_VALUE),
    animation,
    sound,
    hasConsumeParticles,
    onConsumeEffects,
)

val ConsumeEffect.Type<*>.id: Identifier
    get() = BuiltInRegistries.CONSUME_EFFECT_TYPE.getKey(this) ?: Identifier.parse("minecraft:unknown")
