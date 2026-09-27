package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.DyeColorSelector
import moe.forpleuvoir.hiirosakura.ui.widget.hsItemAnimation
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
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
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import androidx.compose.ui.graphics.RectangleShape
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import com.mojang.blaze3d.textures.FilterMode
import moe.forpleuvoir.compose_minecraft.platform.render.plugins.UVMapping
import moe.forpleuvoir.compose_minecraft.platform.ui.draw.minecraftTexture
import androidx.compose.foundation.background

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
        FlatButton(
            onClick = {},
            modifier = Modifier
                .fillMaxHeight()
                .tooltip {
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
        ) {
Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.layers.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
IconButton(onClick = {
    showDialog = true
}) {
    Icon(Icons.Edit)
}
        }
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
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(BannerPatternLayers(layers.entries.values().toMutableList()))
            true
        },
        modifier = Modifier.padding(24.dp).width(800.dp).height(720.dp),
        title = title,
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val lazyListState = rememberLazyListState()
                if (layers.entries.isEmpty()) {
                    Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
                } else {
                    val hapticFeedback = LocalHapticFeedback.current
                    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                        layers.move(from.index, to.index)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    }

                    val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxSize(),
                        state = lazyListState
                    ) {
                        itemsIndexed(layers.entries,
                            key = { _, keyed -> keyed.key }
                        ) { index, (key, layer) ->
                            ReorderableItem(
                                reorderableLazyListState, key,
                                animateItemModifier = hsItemAnimation(),
                            ) { isDragging ->
                                val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                                val handleInteraction = remember { MutableInteractionSource() }

                                val handleHovered by handleInteraction.collectIsHoveredAsState()
                                Surface(
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
                                            DragHandle(modifier = Modifier)
                                            DyeColorSelector(
                                                layer.color,
                                                {
                                                    layers.setValue(index, BannerPatternLayers.Layer(layer.pattern, it))
                                                },
                                                displayColor = {
                                                    listOf(NebulaColor.fromARGB(it.textureDiffuseColor))
                                                }
                                            )
                                            BannerPatternSelector(
                                                layer.pattern,
                                                {
                                                    layers.setValue(index, BannerPatternLayers.Layer(it, layer.color))
                                                },
                                                layer.color,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        RemoveConfirmButton(
                                            layer.pattern.translatableText(layer.color).plainText,
                                            onConfirm = { { layers.removeAt(index) } },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    VerticalScroller(
                        adapter = rememberScrollerAdapter(lazyListState),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                }


                var showAddDialog by remember { mutableStateOf(false) }
                Button(
                    onClick = {
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .fabScrollVisibility(
                    rememberFabScrollVisibility(lazyListState)
                        ),
                ) {
                    Icon(Icons.Add)
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
                        title = { Text(component = IGLang.Misc.add) },
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
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding(),
) {
        LabelBox(label = label, modifier = modifier, contentPadding = contentPadding) {
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
                modifier = Modifier.fillMaxWidth(),
            )
        }
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

    // 原版从 64×64 旗帜图案中截取：x = 0..21，y = 1..41
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
