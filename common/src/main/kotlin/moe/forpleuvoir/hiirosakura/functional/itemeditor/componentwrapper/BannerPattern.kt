package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.DyeColorSelector
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Add
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.client.renderer.Sheets
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.entity.BannerPattern
import net.minecraft.world.level.block.entity.BannerPatternLayers
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.roundToInt
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor

@Composable
fun BannerPatternComponentWrapper(
    key: Identifier,
    value: BannerPatternLayers,
    onValueChange: (BannerPatternLayers) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        var showDialog by remember { mutableStateOf(false) }
        AssistChip(
            {},
            modifier = Modifier
                .fillMaxHeight()
                .plainTooltip {
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
                }
                .width(DataComponentEditorDefaults.entrySize.width),
            label = {
                Text(IGLang.ConfigWrapper.listConfigWrapperText(value.layers.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
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
            BannerPatternLayersEditorDialog(
                key = key,
                value = value,
                onValueChange = onValueChange,
                onDismissRequest = { showDialog = false },
                title = { Text(key) }
            )
        }
    }
}

@Composable
fun BannerPatternLayersEditorDialog(
    key: Identifier,
    value: BannerPatternLayers,
    onValueChange: (BannerPatternLayers) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)? = null,
) {
    val layers = rememberKeyedList(value.layers)
    var nextKey by remember { mutableLongStateOf(layers.size.toLong()) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(BannerPatternLayers(layers.values().toMutableList()))
            true
        },
        modifier = Modifier.padding(24.dp).width(800.dp).height(720.dp),
        title = title,
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val lazyListState = rememberLazyListState()
                if (layers.isEmpty()) {
                    Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    val hapticFeedback = LocalHapticFeedback.current
                    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                        layers.moveElement(from.index, to.index)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    }
                    val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxSize(),
                        state = lazyListState
                    ) {
                        itemsIndexed(
                            layers,
                            key = { _, keyed -> keyed.key }
                        ) { index, (key, layer) ->
                            ReorderableItem(reorderableLazyListState, key) { isDragging ->
                                val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                                val handleInteraction = remember { MutableInteractionSource() }
                                val handleHovered by handleInteraction.collectIsHoveredAsState()
                                Card(
                                    modifier = Modifier.fillMaxWidth().scale(scale)
                                ) {
                                    Row(
                                        Modifier.padding(12.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            DragHandle(
                                                hapticFeedback,
                                                handleInteraction,
                                                handleHovered,
                                                isDragging
                                            )
                                            DyeColorSelector(
                                                layer.color,
                                                {
                                                    layers[index] = layers[index].copyValue(BannerPatternLayers.Layer(layer.pattern, it))
                                                },
                                                displayColor = {
                                                    listOf(NebulaColor.fromARGB(it.textureDiffuseColor))
                                                }
                                            )
                                            BannerPatternSelector(
                                                layer.pattern,
                                                {
                                                    layers[index] = layers[index].copyValue(BannerPatternLayers.Layer(it, layer.color))
                                                },
                                                layer.color,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        RemoveConfirmButton(
                                            layer.pattern.translatableText(layer.color).plainText,
                                            { layers.removeAt(index) }
                                        )
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

                var showAddDialog by remember { mutableStateOf(false) }
                FloatingActionButton(
                    onClick = {
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .fabVisibilityAnimation(
                            rememberFabVisibilityByScroll(lazyListState)
                        )
                ) {
                    Icon(Icons.Add, IGLang.Misc.add.plainText)
                }

                if (showAddDialog) {
                    var newColor by remember { mutableStateOf(DyeColor.WHITE) }
                    var newPattern by remember { mutableStateOf(REGISTERED_BANNER_PATTERN[0]) }
                    SimpleAlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        onConfirmRequest = {
                            layers.add(Keyed(nextKey++, BannerPatternLayers.Layer(newPattern, newColor)))
                            true
                        },
                        title = { Text(IGLang.Misc.add) },
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

        }
    )
}

val REGISTERED_BANNER_PATTERN: List<Holder<BannerPattern>>
    get() = registryAccess?.lookupOrThrow(Registries.BANNER_PATTERN)?.asHolderIdMap()?.toList() ?: emptyList()

fun Holder<BannerPattern>.translatableText(color: DyeColor) =
    Text.translatable("${this.value().translationKey()}.${color.getName()}", this.value().assetId.toString())

@OptIn(ExperimentalMaterial3Api::class)
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
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
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

    var bitmap by remember(resourceId) {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(resourceId) {
        bitmap = SkiaTextureHelper.getTextureCache(resourceId)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            drawRect(
                Color(
                    color.textureDiffuseColor
                )
            )

            val image = bitmap ?: return@Canvas

            /*
             * 原版从 64×64 旗帜图案中截取：
             * x = 0..21
             * y = 1..41
             */
            val sourceLeft = 0
            val sourceTop = (image.height / 64f).roundToInt()
            val sourceRight = (image.width * 21f / 64f).roundToInt()
            val sourceBottom = (image.height * 41f / 64f).roundToInt()

            drawImage(
                image = image,
                srcOffset = IntOffset(
                    x = sourceLeft,
                    y = sourceTop,
                ),
                srcSize = IntSize(
                    width = sourceRight - sourceLeft,
                    height = sourceBottom - sourceTop,
                ),
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(
                    width = size.width.roundToInt(),
                    height = size.height.roundToInt(),
                ),
                filterQuality = FilterQuality.None,
            )
        }
    }
}