package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.*
import moe.forpleuvoir.hiirosakura.ui.widget.ColorListEditor
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.appendNewLine
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks

@Composable
fun FireworksComponentWrapper(
    key: Identifier,
    value: Fireworks,
    onValueChange: (Fireworks) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.thenIf(mc.player != null) {
            Modifier.tooltip {
                val tooltip = mutableListOf<Component>()
                value.addToTooltip(Item.TooltipContext.EMPTY, { tooltip.add(it) }, TooltipFlag.NORMAL, mc.player!!)
                val base: MutableComponent = Literal("")
                for ((index, element) in tooltip.withIndex()) {
                    base.append(element)
                    if (index != tooltip.lastIndex) base.appendNewLine()
                }
                Text(base)
            }
        },
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.explosions.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    if (showDialog) {
        FireworksEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

/**
 * 爆炸条目的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object FireworksCardDefaults {

    /** 一张卡片的最小宽度：形状选择器 + 两组颜色行 + 两个开关 + 卡片内边距。 */
    val MinCardWidth: Dp = 400.dp

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

    /** 浮层高度上限：可视区放得下数行卡片并有富余。 */
    val DialogMaxHeight: Dp = 920.dp

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
 * 烟花编辑浮层：表头一项飞行时间，下面是一发爆炸一张卡片的卡片网格。
 *
 * 卡片头部的拖拽手柄与删除按钮由 `EditDialogContentCards` 提供，卡片体是 [ExplosionContent]；
 * 爆炸只增删与排序，改内容就地写回副本。
 *
 * @param key 语言键来源
 * @param value 待编辑的烟花组件
 * @param onValueChange 确认时的写回
 * @param onDismissRequest 关闭请求
 * @param title 浮层标题
 */
@Composable
fun FireworksEditorDialog(
    key: Identifier,
    value: Fireworks,
    onValueChange: (Fireworks) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var flightDuration by remember { mutableIntStateOf(value.flightDuration) }

    val explosions = rememberKeyedList(value.explosions)

    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(Fireworks(flightDuration, explosions.entries.values().toMutableList()))
            true
        },
        modifier = FireworksCardDefaults.DialogModifier,
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
                        DataComponentField(key, suffix = "flight_duration", modifier = Modifier.fillMaxWidth()) {
                            IntField(
                                flightDuration,
                                { flightDuration = it.coerceIn(0..255) },
                                valueRange = 0..255,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    },
                ) { _ ->
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        EditDialogContentCards(
                            state = explosions,
                            lazyGridState = cardGridState,
                            columns = FireworksCardDefaults.columnsFor(maxWidth),
                            maxHeight = FireworksCardDefaults.DialogMaxHeight,
                            removeButton = { index, _ ->
                                RemoveConfirmButton(
                                    message = key.asTranslateText(suffix = "explosion").plainText,
                                    onConfirm = { explosions.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, explosion, onEntryChange ->
                            ExplosionContent(explosion, onEntryChange, key, Modifier.padding(DataComponentCardContentPadding))
                        }
                    }
                }

                EditDialogAddButton(
                    visibility = addButtonVisibility,
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Button(
                        onClick = {
                            if (explosions.size < Fireworks.MAX_EXPLOSIONS) explosions.add(FireworkExplosion.DEFAULT)
                        },
                        contentPadding = ConfigControlDefaults.IconButtonPadding,
                    ) {
                        Icon(Icons.Add, scale = configIconScale())
                    }
                }
            }
        }
    )
}

/** 一发爆炸的卡片体：形状、两组颜色行与拖尾 / 闪烁开关；颜色行的增删改在行内完成。 */
@Composable
private fun ExplosionContent(
    explosion: FireworkExplosion,
    onValueChange: (FireworkExplosion) -> Unit,
    key: Identifier,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DataComponentSection(key, suffix = "shape") {
            EnumSelector(
                selected = explosion.shape,
                onSelect = { onValueChange(FireworkExplosion(it, explosion.colors, explosion.fadeColors, explosion.hasTrail, explosion.hasTwinkle)) },
                items = FireworkExplosion.Shape.entries,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        DataComponentSection(key, suffix = "colors") {
            ColorListEditor(
                explosion.colors,
                { onValueChange(FireworkExplosion(explosion.shape, it, explosion.fadeColors, explosion.hasTrail, explosion.hasTwinkle)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        DataComponentSection(key, suffix = "fade_colors") {
            ColorListEditor(
                explosion.fadeColors,
                { onValueChange(FireworkExplosion(explosion.shape, explosion.colors, it, explosion.hasTrail, explosion.hasTwinkle)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(key, suffix = "has_trail")
            Spacer(Modifier.weight(1f))
            Switch(
                explosion.hasTrail,
                { onValueChange(FireworkExplosion(explosion.shape, explosion.colors, explosion.fadeColors, it, explosion.hasTwinkle)) })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(key, suffix = "has_twinkle")
            Spacer(Modifier.weight(1f))
            Switch(
                explosion.hasTwinkle,
                { onValueChange(FireworkExplosion(explosion.shape, explosion.colors, explosion.fadeColors, explosion.hasTrail, it)) })
        }
    }
}
