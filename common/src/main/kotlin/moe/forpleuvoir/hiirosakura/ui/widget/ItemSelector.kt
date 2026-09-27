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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.*
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.util.ItemRegistryHelper
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.name
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.util.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastHandler
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import androidx.compose.ui.graphics.RectangleShape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.hiirosakura.ui.util.rememberAdaptiveGridSpan
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.compat.closeScreen
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.ButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.DpSize
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButtonDefaults
import androidx.compose.foundation.clickable

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
    searchBarBackgroundColor: Color = SokitsuTheme.colorScheme.surface,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tabList.forEachIndexed { index, tabKey ->
                FlatButton(
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp, end = 4.dp),
                    colors = if (selectedTabIndex == index) {
                        FlatButtonDefaults.colors(
                            color = SokitsuTheme.colorScheme.primaryContainer,
                            contentColor = SokitsuTheme.colorScheme.onPrimaryContainer,
                        )
                    } else {
                        FlatButtonDefaults.colors()
                    },
                ) {
                    ItemIcon(
                        tabKey.first,
                        modifier = Modifier.size(32.dp),
                        showTooltip = false,
                        scaleOnHover = 1f,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(tabKey.second)
                }
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
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(gridCellSize),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(displayItems) { itemStack ->
                            itemDisplay(itemStack)
                        }
                    }


                    VerticalScroller(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollerAdapter(gridState, rememberAdaptiveGridSpan(gridState))
                    )

                    if (selectedTabIndex == 0) {
                        SearchBar(
                            textFieldState = textFieldState,
                            modifier = Modifier
                                .align(BiasAlignment(0f, 0.85f))
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                                .width(320.dp)
                                .fabScrollVisibility(rememberFabScrollVisibility(gridState))
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
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = SokitsuTheme.colorScheme.surface,
    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.CenterStart) {
            if (textFieldState.text.isEmpty()) {
                Text(
                    component = IGLang.Misc.search,
                    color = SokitsuTheme.colorScheme.onSurfaceVariant,
                )
            }
            BasicTextField(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(SokitsuTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (textFieldState.text.isNotEmpty()) {
            IconButton(onClick = { textFieldState.edit { replace(0, length, "") } }) {
                Icon(Icons.Close)
            }
        }
    }
    }
}

/** 物品图标的展示尺寸；新版上游移除了旧 IG 的同名组合本地量，这里按本项目像素风取值。 */
val LocalItemIconVanillaSize = staticCompositionLocalOf { DpSize(32.dp, 32.dp) }

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
                .clickable(interactionSource = interactionSource, indication = null) { onCLick(item) }
                .then(
                    if (isHovered && border) Modifier.border(1.dp, SokitsuTheme.colorScheme.outline)
                    else Modifier
                ).then(
                    if (showTooltip) Modifier.tooltip {
                        Column {
                            if (item is Item) {
                                Text(item.asItem().name)
                                Spacer(Modifier.height(4.dp))
                                Text(item.asItem().key.toString(), color = SokitsuTheme.colorScheme.primaryContainer)
                            } else if (item is Block) {
                                Text(item.name)
                                Spacer(Modifier.height(4.dp))
                                Text(item.key.toString(), color = SokitsuTheme.colorScheme.primaryContainer)
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
                ToastHandler.showContent { Text(component = HSLang.Common.itemInitFailure) }
            }.getOrThrow()
            if (icon.item != Items.AIR) {
                ItemIcon(
                    icon,
                    modifier = Modifier.size(LocalItemIconSize.current),
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

