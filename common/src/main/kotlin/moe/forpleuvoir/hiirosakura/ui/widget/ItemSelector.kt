package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.*
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.shadow.Shadow
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.util.ItemRegistryHelper
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.name
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.closeScreen
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Close
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.preset.LocalItemIconVanillaSize
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block

@Composable
fun ItemSelector(
    value: Item,
    onValueChange: (Item) -> Unit,
    showTooltip: Boolean = true,
    scaleOnHover: Float = 1f,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    ItemBrowserDefaults.ItemWrapper(value, false, scaleOnHover, modifier.size(LocalItemIconVanillaSize.current), showTooltip) {
        showDialog = true
    }
    if (showDialog) {
        FlexibleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmRequest = { true },
            content = {
                ItemBrowser(
                    itemDisplay = {
                        ItemBrowserDefaults.ItemWrapper(it) { selected ->
                            onValueChange(selected.asItem())
                            showDialog = false
                        }
                    },
                    modifier = Modifier.size(680.dp, 520.dp)
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun BlockSelector(
    value: Block,
    onValueChange: (Block) -> Unit,
    showTooltip: Boolean = true,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    ItemBrowserDefaults.ItemWrapper(value, false, 1f, modifier.size(LocalItemIconVanillaSize.current), showTooltip) {
        showDialog = true
    }
    if (showDialog) {
        FlexibleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmRequest = { true },
            content = {
                ItemBrowser(
                    itemDisplay = {
                        ItemBrowserDefaults.ItemWrapper(it) { selected ->
                            if (selected is BlockItem) {
                                onValueChange(selected.block)
                            } else if (selected is Block) {
                                onValueChange(selected)
                            }
                            showDialog = false
                        }
                    },
                    filter = {
                        it is BlockItem || it is Block
                    },
                    searchItems = ItemRegistryHelper.allBlock.toList(),
                    modifier = Modifier.size(680.dp, 520.dp)
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun ItemBrowser(
    itemGroups: List<ResourceKey<CreativeModeTab>> = ItemRegistryHelper.getAllTabs(),
    itemDisplay: @Composable (ItemLike) -> Unit = ItemBrowserDefaults::ItemWrapper,
    filter: (ItemLike) -> Boolean = { true },
    searchItems: List<ItemLike> = ItemRegistryHelper.allItem.toList(),
    gridCellSize: Dp = ItemBrowserDefaults.gridCellSize(contentPadding = PaddingValues(4.dp)),
    searchBarBackgroundColor: Color = AlertDialogDefaults.containerColor,
    modifier: Modifier = Modifier
) {
    val tabList = remember(itemGroups) {
        listOf(ItemStack(Items.COMPASS) to IGLang.Misc.search.string) +
                itemGroups.map {
                    val tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(it)
                    tab.iconItem to tab.displayName.string
                }
    }
    var selectedTabIndex by remember { mutableStateOf(if (tabList.size > 1) 1 else 0) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent
        ) {
            tabList.forEachIndexed { index, tabKey ->
                Tab(
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp, end = 4.dp).clip(MaterialTheme.shapes.medium),
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    icon = {
                        ItemIcon(
                            tabKey.first,
                            modifier = Modifier.size(32.dp),
                            showTooltip = false,
                            scaleOnHover = 1f,
                        )
                    },
                    text = { Text(tabKey.second) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = {
                val currentIdx = initialState
                val targetIdx = targetState
                val direction = if (targetIdx > currentIdx) 1 else -1
                (slideInHorizontally(tween(150)) { width -> direction * width } + fadeIn(tween(150))) togetherWith
                        (slideOutHorizontally(tween(150)) { width -> -direction * width } + fadeOut(tween(150)))
            },
            label = "ItemBrowserTabContent"
        ) {
            Column {

                val gridState = rememberLazyGridState()
                val textFieldState = rememberTextFieldState()

                val items by remember(selectedTabIndex) {
                    mutableStateOf(
                        if (selectedTabIndex != 0)
                            ItemRegistryHelper.getItemsByTab(itemGroups[selectedTabIndex - 1])
                                .filter { filter(it.item) }
                                .map { it.item }
                                .distinctBy { it.key }
                        else searchItems.filter { filter(it) }.distinctBy {
                            when (it) {
                                is Item  -> it.key
                                is Block -> it.key
                                else     -> it
                            }
                        }
                    )
                }

                var searchQuery by remember { mutableStateOf("") }
                val displayItems by remember(items, searchQuery) {
                    mutableStateOf(
                        if (searchQuery.isBlank()) items
                        else items.filter {
                            when (it) {
                                is Item  -> it.name.plainText.contains(searchQuery, ignoreCase = true)
                                        || it.key.toString().contains(searchQuery, ignoreCase = true)

                                is Block -> it.name.plainText.contains(searchQuery, ignoreCase = true)
                                        || it.key.toString().contains(searchQuery, ignoreCase = true)

                                else     -> false
                            }
                        }
                    )
                }
                if (selectedTabIndex == 0) {
                    LaunchedEffect(textFieldState.text.toString()) {
                        searchQuery = textFieldState.text.toString()
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    val backdrop = rememberLayerBackdrop {
                        drawRect(searchBarBackgroundColor)
                        drawContent()
                    }
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(gridCellSize),
                        modifier = Modifier
                            .layerBackdrop(backdrop)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(displayItems) { itemStack ->
                            itemDisplay(itemStack)
                        }
                    }

                    val adapter = rememberScrollbarAdapter(gridState)
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(gridState)
                    )

                    if (selectedTabIndex == 0) {
                        SearchBar(
                            textFieldState = textFieldState,
                            backdrop = backdrop,
                            modifier = Modifier
                                .align(BiasAlignment(0f, 0.85f))
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                                .width(320.dp)
                                .fabVisibilityAnimation(rememberFabVisibilityByScroll(adapter))
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SearchBar(
    textFieldState: TextFieldState,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.extraLarge
    val color = MaterialTheme.colorScheme.onSurface.copy(0.25f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                shadow = {
                    Shadow.Default.copy(radius = 8.dp, offset = DpOffset(4.dp, 4.dp), color = Color.Black.copy(alpha = 0.25f))
                },
                effects = {
                    vibrancy()
                    blur(16f)
                    lens(8.dp.toPx(), 32.dp.toPx(), depthEffect = true, chromaticAberration = true)
                },
                onDrawSurface = {
                    drawRect(color)
                }
            )
            .padding(horizontal = 24.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.CenterStart) {
            if (textFieldState.text.isEmpty()) {
                Text(
                    IGLang.Misc.search,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = LocalTextStyle.current.merge(
                        MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                )
            }
            BasicTextField(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = LocalTextStyle.current.merge(
                    MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (textFieldState.text.isNotEmpty()) {
            IconButton(onClick = { textFieldState.edit { replace(0, length, "") } }) {
                Icon(Icons.Close, null)
            }
        }
    }
}

object ItemBrowserDefaults {

    @Composable
    fun gridCellSize(wrapperSize: DpSize = LocalItemIconVanillaSize.current, contentPadding: PaddingValues): Dp =
        (wrapperSize.width + contentPadding.calculateLeftPadding(LayoutDirection.Ltr) + contentPadding.calculateRightPadding(LayoutDirection.Ltr))
            .coerceAtMost(wrapperSize.height + contentPadding.calculateTopPadding() + contentPadding.calculateBottomPadding())

    val LocalItemIconSize = compositionLocalOf {
        42.dp
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ItemWrapper(
        item: ItemLike,
        border: Boolean = true,
        scaleOnHover: Float = 1.1f,
        modifier: Modifier = Modifier,
        showTooltip: Boolean = true,
        onCLick: (ItemLike) -> Unit = {}
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .hoverable(interactionSource)
                .onClick { onCLick(item) }
                .then(
                    if (isHovered && border) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.extraSmall)
                    else Modifier
                ).then(
                    if (showTooltip) Modifier.plainTooltip {
                        Column {
                            if (item is Item) {
                                Text(item.asItem().name)
                                Spacer(Modifier.height(4.dp))
                                Text(item.asItem().key.toString(), color = MaterialTheme.colorScheme.primaryContainer)
                            } else if (item is Block) {
                                Text(item.name)
                                Spacer(Modifier.height(4.dp))
                                Text(item.key.toString(), color = MaterialTheme.colorScheme.primaryContainer)
                            }
                        }
                    }
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            val icon = runCatching {
                ItemStack(item)
            }.onFailure {
                closeScreen()
                ToastHandler.showContent { Text(HSLang.Common.itemInitFailure) }
            }.getOrThrow()
            if (icon.item != Items.AIR) {
                ItemIcon(
                    icon,
                    modifier = Modifier.size(LocalItemIconSize.current),
                    imageSize = if (icon.item is BlockItem) IntSize(256, 256) else IntSize(128, 128),
                    showTooltip = false,
                    scaleOnHover = scaleOnHover
                )
            } else {
                Canvas(Modifier.size(LocalItemIconSize.current)) {
                    val tileSize = size / 2f
                    for (row in 0 until 2) for (col in 0 until 2) {
                        drawRect(
                            color = if ((row + col) % 2 == 0) Color(128, 0, 128) else Color(0XFF000000),
                            topLeft = Offset(col * tileSize.width, row * tileSize.height),
                            size = tileSize
                        )
                    }
                }

            }
        }
    }
}

