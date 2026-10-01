package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mojang.blaze3d.textures.FilterMode
import moe.forpleuvoir.compose_minecraft.platform.render.plugins.UVMapping
import moe.forpleuvoir.compose_minecraft.platform.ui.draw.minecraftTexture
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.DyeColorSelector
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.client.renderer.Sheets
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.entity.BannerPattern
import net.minecraft.world.level.block.entity.BannerPatternLayers
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor

@Composable
fun BannerPatternComponentWrapper(
    key: Identifier,
    value: BannerPatternLayers,
    onValueChange: (BannerPatternLayers) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }
    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        tooltip = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                value.layers().forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BannerPattern(it.pattern, it.color)
                        Spacer(Modifier.width(8.dp))
                        Text(it.description())
                    }
                }
            }
        },
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.layers.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
    if (showDialog) {
        BannerPatternLayersEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

/**
 * 旗帜图案卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object BannerPatternCardDefaults {

    /** 一张卡片的最小宽度：染料选择器 + 图案选择器 + 卡片内边距。 */
    val MinCardWidth: Dp = 460.dp

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
    val DialogMaxHeight: Dp = 800.dp

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
 * 旗帜图案编辑浮层：一层图案一张卡片。
 *
 * 卡片头部两端的拖拽手柄与删除按钮由 `EditDialogContentCards` 提供，卡片体是
 * [BannerPatternLayerContent]；条目增删、排序与编辑都在副本上做，确认时才写回。
 *
 * @param key 语言键来源
 * @param value 待编辑的旗帜图案层
 * @param onValueChange 确认时的写回
 * @param onDismissRequest 关闭请求
 * @param title 浮层标题
 */
@Composable
fun BannerPatternLayersEditorDialog(
    key: Identifier,
    value: BannerPatternLayers,
    onValueChange: (BannerPatternLayers) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)? = null,
) {
    val layers = rememberKeyedList(value.layers)

    var showAddDialog by remember { mutableStateOf(false) }
    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(BannerPatternLayers(layers.entries.values().toMutableList()))
            true
        },
        modifier = BannerPatternCardDefaults.DialogModifier,
        title = title,
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
                            state = layers,
                            lazyGridState = cardGridState,
                            columns = BannerPatternCardDefaults.columnsFor(maxWidth),
                            maxHeight = BannerPatternCardDefaults.DialogMaxHeight,
                            removeButton = { index, layer ->
                                RemoveConfirmButton(
                                    message = layer.pattern.translatableText(layer.color).plainText,
                                    onConfirm = { layers.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, layer, onEntryChange ->
                            BannerPatternLayerContent(layer, onEntryChange)
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
                var newColor by remember { mutableStateOf(DyeColor.WHITE) }

                var newPattern by remember { mutableStateOf(REGISTERED_BANNER_PATTERN[0]) }
                SimpleAlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    onConfirmRequest = {
                        layers.add(BannerPatternLayers.Layer(newPattern, newColor))
                        true
                    },
                    title = { DataComponentDialogTitle(component = IGLang.Misc.add) },
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            DyeColorSelector(
                                newColor,
                                { newColor = it },
                                displayColor = {
                                    listOf(NebulaColor.fromARGB(it.textureDiffuseColor))
                                }
                            )
                            BannerPatternSelector(
                                newPattern,
                                {
                                    newPattern = it
                                },
                                newColor,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }
        }
    )
}

/** 一条旗帜图案的卡片体：染料选择器 + 图案选择器。 */
@Composable
private fun BannerPatternLayerContent(
    layer: BannerPatternLayers.Layer,
    onValueChange: (BannerPatternLayers.Layer) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DyeColorSelector(
            layer.color,
            { onValueChange(BannerPatternLayers.Layer(layer.pattern, it)) },
            displayColor = { listOf(NebulaColor.fromARGB(it.textureDiffuseColor)) },
        )
        BannerPatternSelector(
            layer.pattern,
            { onValueChange(BannerPatternLayers.Layer(it, layer.color)) },
            layer.color,
            modifier = Modifier.weight(1f),
        )
    }
}


val REGISTERED_BANNER_PATTERN: List<Holder<BannerPattern>>
    get() = registryAccess?.lookupOrThrow(Registries.BANNER_PATTERN)?.asHolderIdMap()?.toList() ?: emptyList()

fun Holder<BannerPattern>.translatableText(color: DyeColor) =
    Text.translatable("${this.value().translationKey()}.${color.getName()}", this.value().assetId.toString())

@Composable
fun BannerPatternSelector(
    selected: Holder<BannerPattern>,
    onSelect: (Holder<BannerPattern>) -> Unit,
    dyeColor: DyeColor,
    items: List<Holder<BannerPattern>> = REGISTERED_BANNER_PATTERN,
    itemEquals: (Holder<BannerPattern>, Holder<BannerPattern>) -> Boolean = { a, b -> a == b },
    content: @Composable (Holder<BannerPattern>) -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BannerPattern(it, dyeColor)
            Spacer(Modifier.width(8.dp))
            Text(it.translatableText(dyeColor))
        }
    },
    itemContent: @Composable (Holder<BannerPattern>, Boolean) -> Unit = { item, _ ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            BannerPattern(item, dyeColor)
            Spacer(Modifier.width(8.dp))
            Text(item.translatableText(dyeColor))
        }
    },
    enabled: Boolean = true,
    searchFilter: ((String, Holder<BannerPattern>) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (Holder<BannerPattern>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (Holder<BannerPattern>) -> Unit)?)? = null,
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
fun BannerPattern(
    bannerPattern: Holder<BannerPattern>,
    color: DyeColor,
    modifier: Modifier = Modifier.size(
        height = 30.dp,
        width = 24.dp,
    ),
) {
    val spriteId = Sheets.getBannerSprite(bannerPattern)

    val textureId: Identifier = spriteId.texture()

    val resourceId = Identifier.fromNamespaceAndPath(
        textureId.namespace,
        "textures/${textureId.path}.png",
    )

    // 鍘熺増浠?64脳64 鏃楀笢鍥炬涓埅鍙栵細x = 0..21锛寉 = 1..41
    Box(
        modifier = modifier
            .background(Color(color.textureDiffuseColor))
            .minecraftTexture(
                resourceId,
                filterMode = FilterMode.NEAREST,
                uv = UVMapping(0, 1, 21, 41),
            ),
    ) {
    }
}
