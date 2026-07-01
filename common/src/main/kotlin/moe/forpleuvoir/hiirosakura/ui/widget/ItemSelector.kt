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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.*
import moe.forpleuvoir.hiirosakura.util.ItemRegistryHelper
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.name
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.closeScreen
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Close
import moe.forpleuvoir.ibukigourd.ui.openComposePopupScreen
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.util.toComposeColor
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemSelector(
    value: Item,
    onValueChange: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    ItemBrowserDefaults.ItemWrapper(value, false, 1f, modifier.size(LocalItemIconVanillaSize.current)) {
        openItemBrowserScreen(
            itemDisplay = {
                ItemBrowserDefaults.ItemWrapper(it) { selected ->
                    onValueChange(selected.asItem())
                    closeScreen()
                }
            },
            modifier = Modifier.fillMaxWidth(0.45f).fillMaxHeight(0.55f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BlockSelector(
    value: Block,
    onValueChange: (Block) -> Unit,
    modifier: Modifier = Modifier,
) {
    ItemBrowserDefaults.ItemWrapper(value, false, 1f, modifier.size(LocalItemIconVanillaSize.current)) {
        openItemBrowserScreen(
            itemDisplay = {
                ItemBrowserDefaults.ItemWrapper(it) { selected ->
                    if (selected is Block) {
                        onValueChange(selected)
                    }
                    closeScreen()
                }
            },
            filter = {
                it is BlockItem || it is Block
            },
            searchItems = ItemRegistryHelper.allBlock.toList(),
            modifier = Modifier.fillMaxWidth(0.45f).fillMaxHeight(0.55f)
        )
    }
}


fun openItemBrowserScreen(
    itemGroups: List<ResourceKey<CreativeModeTab>> = ItemRegistryHelper.getAllTabs(),
    itemDisplay: @Composable (ItemLike) -> Unit = ItemBrowserDefaults::ItemWrapper,
    filter: (ItemLike) -> Boolean = { true },
    searchItems: List<ItemLike> = ItemRegistryHelper.allItem.toList(),
    contentPadding: PaddingValues = PaddingValues(4.dp),
    gridCellSize: Dp? = null,
    modifier: Modifier = Modifier
) {
    openComposePopupScreen {
        val gridCellSize = gridCellSize ?: ItemBrowserDefaults.gridCellSize(contentPadding = contentPadding)
        IbukiGourdTheme {
            ItemBrowser(
                itemGroups,
                itemDisplay,
                filter,
                searchItems,
                contentPadding,
                gridCellSize,
                modifier
            )
        }
    }
}

@Composable
fun ItemBrowser(
    itemGroups: List<ResourceKey<CreativeModeTab>> = ItemRegistryHelper.getAllTabs(),
    itemDisplay: @Composable (ItemLike) -> Unit = ItemBrowserDefaults::ItemWrapper,
    filter: (ItemLike) -> Boolean = { true },
    searchItems: List<ItemLike> = ItemRegistryHelper.allItem.toList(),
    contentPadding: PaddingValues = PaddingValues(4.dp),
    gridCellSize: Dp = ItemBrowserDefaults.gridCellSize(contentPadding = contentPadding),
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

    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.extraLarge)
                                .padding(horizontal = 24.dp)
                        ) {
                            Column(Modifier.fillMaxWidth(1f)) {
                                Box(Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.CenterStart) {
                                    if (textFieldState.text.isEmpty()) {
                                        Text(
                                            IGLang.Misc.search,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                    BasicTextField(
                                        state = textFieldState,
                                        lineLimits = TextFieldLineLimits.SingleLine,
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                        ),
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                            if (textFieldState.text.isNotEmpty()) {
                                IconButton(onClick = { textFieldState.edit { replace(0, length, "") } }) {
                                    Icon(Icons.Close, null)
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
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

                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(gridState)
                        )
                    }
                }
            }
        }
    }
}


object ItemBrowserDefaults {

    @Composable
    fun gridCellSize(wrapperSize: DpSize = LocalItemIconVanillaSize.current, contentPadding: PaddingValues): Dp =
        (wrapperSize.width + contentPadding.calculateLeftPadding(LayoutDirection.Ltr) + contentPadding.calculateRightPadding(LayoutDirection.Ltr))
            .coerceAtMost(wrapperSize.height + contentPadding.calculateTopPadding() + contentPadding.calculateBottomPadding())

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ItemWrapper(item: ItemLike, border: Boolean = true, scaleOnHover: Float = 1.1f, modifier: Modifier = Modifier, onCLick: (ItemLike) -> Unit = {}) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        TipBox({
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
        }) {
            Box(
                modifier = modifier
                    .aspectRatio(1f)
                    .hoverable(interactionSource)
                    .onClick { onCLick(item) }
                    .then(
                        if (isHovered && border) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.extraSmall)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                val icon = ItemStack(item)
                if (icon.item != Items.AIR) {
                    ItemIcon(
                        icon,
                        modifier = Modifier.size(42.dp),
                        imageSize = if (icon.item is BlockItem) IntSize(128, 128) else IntSize(64, 64),
                        showTooltip = false,
                        scaleOnHover = scaleOnHover
                    )
                } else {
                    Canvas(Modifier.size(42.dp)) {
                        val tileSize = (42 / 2).dp.toPx()
                        val cols = (size.width / tileSize).toInt()
                        val rows = (size.height / tileSize).toInt()
                        for (row in 0 until rows) for (col in 0 until cols) {
                            drawRect(
                                color = if ((row + col) % 2 == 0) Colors.PURPLE.toComposeColor else Colors.BLACK.toComposeColor,
                                topLeft = Offset(col * tileSize, row * tileSize),
                                size = Size(tileSize, tileSize)
                            )
                        }
                    }

                }
            }
        }
    }
}

