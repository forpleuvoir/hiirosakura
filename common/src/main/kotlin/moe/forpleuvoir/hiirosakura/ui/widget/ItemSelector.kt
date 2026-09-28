package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.ui.util.rememberAdaptiveGridSpan
import moe.forpleuvoir.hiirosakura.util.ItemRegistryHelper
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.name
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.item.ItemIconDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.FabVisibilityState
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberHideActionState
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ItemSelector(
    value: Item,
    onValueChange: (Item) -> Unit,
    showTooltip: Boolean = true,
    scaleOnHover: Float = 1f,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    ItemBrowserDefaults.ItemWrapper(
        value,
        true,
        scaleOnHover,
        modifier.pointerHoverIcon(PointerIcon.Hand)
            .size(LocalItemIconVanillaSize.current),
        showTooltip
    ) {
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
    ItemBrowserDefaults.ItemWrapper(
        value,
        true,
        1f,
        modifier.pointerHoverIcon(PointerIcon.Hand)
            .size(LocalItemIconVanillaSize.current),
        showTooltip
    ) {
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
    itemGroups: List<ResourceKey<CreativeModeTab>>? = null,
    itemDisplay: @Composable (ItemLike) -> Unit = ItemBrowserDefaults::ItemWrapper,
    filter: (ItemLike) -> Boolean = { true },
    searchItems: List<ItemLike>? = null,
    // 格子按图标尺寸定(内边距不计入 minSize,否则会被 Adaptive 拉伸得比图标大);间距交给网格的 arrangement。
    gridCellSize: Dp = ItemBrowserDefaults.gridCellSize(contentPadding = PaddingValues(0.dp)),
    modifier: Modifier = Modifier
) {
    // 两个默认值开销大(`getAllTabs()` 会重建分类内容、`allItem.toList()` 复制整个物品注册表),
    // 而 ItemBrowser 会随对话框状态反复重组,故只在入参变化时求值。
    val groups = remember(itemGroups) { itemGroups ?: ItemRegistryHelper.getAllTabs() }
    val searchPool = remember(searchItems) { searchItems ?: ItemRegistryHelper.allItem.toList() }

    val tabList = remember(groups) {
        listOf(ItemStack(Items.COMPASS) to IGLang.Misc.search.string) +
                groups.map {
                    val tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(it)
                    tab.iconItem to tab.displayName.string
                }
    }

    var selectedTabIndex by remember { mutableStateOf(if (tabList.size > 1) 1 else 0) }

    // 切分类从顶部开始:按分类各记一个网格状态(协程里写滚动状态会撞测量/快照锁)。
    val gridState = remember(selectedTabIndex) { LazyGridState() }
    // 滚动显隐的嵌套滚动回调必须挂在滚动容器的祖先上;搜索框是它的兄弟节点,挂在搜索框上收不到位移
    val fabVisibility = rememberFabScrollVisibility(gridState)

    val textFieldState = rememberTextFieldState()
    var searchQuery by remember { mutableStateOf("") }
    // 输入经 snapshotFlow 收敛:组合期直接读 textFieldState.text 会让每次按键重组整个浏览器。
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .collect { searchQuery = it }
    }

    // 当前页签的物品清单:只在页签/入参变化时重算
    val tabItems = remember(selectedTabIndex, groups, searchPool, filter) {
        if (selectedTabIndex != 0)
            ItemRegistryHelper.getItemsByTab(groups[selectedTabIndex - 1])
                .filter { filter(it.item) }
                .map { it.item }
                .distinctBy { it.key }
        else searchPool.filter { filter(it) }.distinctBy {
            when (it) {
                is Item  -> it.key
                is Block -> it.key
                else     -> it
            }
        }
    }

    val displayItems = remember(tabItems, searchQuery) {
        if (searchQuery.isBlank()) tabItems
        else tabItems.filter {
            when (it) {
                is Item  -> it.name.plainText.contains(searchQuery, ignoreCase = true)
                        || it.key.toString().contains(searchQuery, ignoreCase = true)

                is Block -> it.name.plainText.contains(searchQuery, ignoreCase = true)
                        || it.key.toString().contains(searchQuery, ignoreCase = true)

                else     -> false
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 分类条用 IG 的 TabRow + Tab(等宽):外层横向滚动 + 内层给足内容宽度,
        // 滚轮与拖动都能到达超出一屏的分类。
        val tabDensity = LocalDensity.current
        val tabScroll = rememberScrollState()
        val tabContentWidth = CategoryTabWidth * tabList.size + TabRowDefaults.tabGap * (tabList.size - 1).coerceAtLeast(0)
        // 选中页签自动滚进视野:超出可视区的分类否则点不到。
        LaunchedEffect(selectedTabIndex) {
            val maxScroll = snapshotFlow { tabScroll.maxValue }.first { it in 1 until Int.MAX_VALUE }
            val density = tabDensity
            val tabWidthPx = with(density) { CategoryTabWidth.roundToPx() }
            val tabGapPx = with(density) { TabRowDefaults.tabGap.roundToPx() }
            val tabStridePx = tabWidthPx + tabGapPx
            val totalWidthPx = with(density) { tabContentWidth.roundToPx() }
            val visibleWidth = totalWidthPx - maxScroll
            val tabLeft = selectedTabIndex * tabStridePx
            val centered = tabLeft - (visibleWidth / 2 - tabWidthPx / 2)
            val available = (totalWidthPx - visibleWidth).coerceAtLeast(0)
            tabScroll.animateScrollTo(
                value = centered.coerceIn(0, available),
                animationSpec = tween(TabRowDefaults.IndicatorAnimationDurationMillis),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(tabScroll),
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                modifier = Modifier.width(tabContentWidth),
                tabs = {
                    tabList.forEachIndexed { index, tabKey ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier.tooltip { Text(tabKey.second) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ItemIcon(
                                        tabKey.first,
                                        size = DpSize(32.dp, 32.dp),
                                        showTooltip = false,
                                        scaleOnHover = 1f,
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(tabKey.second, maxLines = 1)
                                }
                            },
                        )
                    }
                },
            )
        }
        Spacer(Modifier.height(8.dp))
        // 切换过渡:进度在组合期读(只在绘制期读的状态在本渲染栈推进不到)
        val panelSlide = remember { Animatable(1f) }
        var previousTab by remember { mutableIntStateOf(selectedTabIndex) }
        LaunchedEffect(selectedTabIndex) {
            if (previousTab != selectedTabIndex) {
                panelSlide.snapTo(0f)
                panelSlide.animateTo(1f, tween(180))
                previousTab = selectedTabIndex
            }
        }
        // 列表**一次给全**(滚动条长度因此稳定),只是每格的**渲染**按帧放开:
        // 每个物品首次出现都要新建一次离屏 PIP 纹理,一帧上百个就是切换卡顿的尖峰。
        // 只需放开首屏那几十格,其余交给 Lazy 按需组合(滚到时才动)。
        var revealedCount by remember(displayItems) { mutableIntStateOf(0) }
        LaunchedEffect(displayItems) {
            revealedCount = 0
            val warmUp = displayItems.size.coerceAtMost(128)
            while (revealedCount < warmUp) {
                delay(12.milliseconds)
                revealedCount = (revealedCount + 6).coerceAtMost(warmUp)
            }
            revealedCount = displayItems.size
        }

        val panelProgress = panelSlide.value
        val slideDirection = if (selectedTabIndex >= previousTab) 1f else -1f
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = slideDirection * (1f - panelProgress) * 48f
                    alpha = panelProgress
                }
                .fillMaxSize()
                .fabScrollVisibility(fabVisibility)
        ) {
            // 列表与滚动条各占一列
            Row(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(gridCellSize),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    // key 用注册名:切页签 / 改搜索词时 Compose 能复用已有条目,而不是整片重建
                    // 未放开的格子先用占位撑住尺寸:列表长度/滚动条不受影响
                    itemsIndexed(displayItems, key = { _, item -> itemKey(item) }) { index, itemStack ->
                        if (index < revealedCount) {
                            itemDisplay(itemStack)
                        } else {
                            Spacer(Modifier.size(gridCellSize))
                        }
                    }
                }

                VerticalFlatScroller(
                    adapter = rememberScrollerAdapter(gridState, rememberAdaptiveGridSpan(gridState))
                )
            }

            if (selectedTabIndex == 0) {
                // 与浮动按钮同一套显隐来源:滚动收起 + 按住隐藏动作键(IGConfig.Gui.hideActionKeyCode)收起
                val hiddenByKey = rememberHideActionState()
                OverlayVisibility(
                    visible = !hiddenByKey && fabVisibility.state == FabVisibilityState.Visible,
                    modifier = Modifier
                        .align(BiasAlignment(0f, 0.85f))
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    enter = fadeIn(tween(250)),
                    exit = fadeOut(tween(250)),
                ) {
                    SearchBar(
                        textFieldState = textFieldState,
                        modifier = Modifier.width(320.dp)
                    )
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
    TextField(
        state = textFieldState,
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier,
        hint = IGLang.Misc.search,
        trailingIcon = if (textFieldState.text.isNotEmpty()) {
            {
                IconButton(
                    onClick = { textFieldState.edit { replace(0, length, "") } },
                    minSize = DpSize(36.dp, 36.dp),
                ) {
                    Icon(Icons.Close)
                }
            }
        } else null
    )
}

/** 物品图标的展示尺寸；新版上游移除了旧 IG 的同名组合本地量，这里按本项目像素风取值。 */
val LocalItemIconVanillaSize = staticCompositionLocalOf { DpSize(48.dp, 48.dp) }

/** 分类条每格的内容宽:够放「16dp 图标 + 间隙 + 完整分类名」(不截断)。 */
private val CategoryTabWidth = 168.dp

/** Lazy 网格的条目 key:用注册名,保证切页签 / 搜索时条目身份稳定。 */
private fun itemKey(item: ItemLike): Any = when (item) {
    is Item  -> item.key
    is Block -> item.key
    else     -> item
}

/**
 * 覆盖层的显隐过渡。
 *
 * 调用点外层的 ColumnScope 存在同名扩展重载;本函数体内没有隐式接收者,裸名解析到顶层重载。
 */
@Composable
private fun OverlayVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(tween(150)),
    exit: ExitTransition = fadeOut(tween(150)),
    label: String = "overlayVisibility",
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        label = label,
        content = content,
    )
}

object ItemBrowserDefaults {

    @Composable
    fun gridCellSize(wrapperSize: DpSize = DpSize(LocalItemIconSize.current, LocalItemIconSize.current), contentPadding: PaddingValues): Dp =
        (wrapperSize.width + contentPadding.calculateLeftPadding(LayoutDirection.Ltr) + contentPadding.calculateRightPadding(LayoutDirection.Ltr))
            .coerceAtMost(wrapperSize.height + contentPadding.calculateTopPadding() + contentPadding.calculateBottomPadding())

    val LocalItemIconSize = compositionLocalOf {
        64.dp
    }

    @Composable
    fun ItemWrapper(
        item: ItemLike,
        hoverHighlight: Boolean = true,
        scaleOnHover: Float = 1.1f,
        // 默认给一个尺寸:自身是 aspectRatio(1f),没有外部约束时会塌掉 —— 默认取
        // ItemIconDefaults.size(48dp),调用方要别的尺寸再显式传。
        modifier: Modifier = Modifier.size(ItemIconDefaults.size),
        showTooltip: Boolean = true,
        // 物品着色(Color.White 为不调制):走物品绘制着色,不产生 graphicsLayer ——
        // 本平台图层是命令烘焙 + 父链连锁重录,用 Modifier.alpha 表达「半透明」会给每个元素建图层。
        color: Color = Color.White,
        onCLick: (ItemLike) -> Unit = {}
    ) {
        val interactionSource = remember { MutableInteractionSource() }


        Box(
            modifier = modifier
                .aspectRatio(1f)
                .hoverable(interactionSource)
                .clickable(interactionSource = interactionSource, indication = null) { onCLick(item) }
                // 悬停反馈改为 IG 的悬停高亮(原来是一圈 1dp 描边):
                // 高亮块铺在内容之下,主题色 / 浓度 / 淡入淡出全部沿用 hoverHighlight 的定义。
                .thenIf(hoverHighlight) { Modifier.hoverHighlight(interactionSource) }
                .thenIf(showTooltip) {
                    Modifier.tooltip {
                        Column {
                            if (item is Item) {
                                Text(item.asItem().name)
                                Spacer(Modifier.height(4.dp))
                                Text(item.asItem().key.toString(), color = SokitsuTheme.colorScheme.onSurfaceVariant)
                            } else if (item is Block) {
                                Text(item.name)
                                Spacer(Modifier.height(4.dp))
                                Text(item.key.toString(), color = SokitsuTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // ItemStack 构造放进 remember:网格滚动/重绘时不再反复构造;构造失败取 null 走下面的
            // 占位格 —— 原来在组合期直接 closeScreen() + Toast 属于组合期副作用。
            val icon = remember(item) { runCatching { ItemStack(item) }.getOrNull() }
            if (icon != null && icon.item != Items.AIR) {
                ItemIcon(
                    icon,
                    size = DpSize(LocalItemIconSize.current, LocalItemIconSize.current),
                    showTooltip = false,
                    scaleOnHover = scaleOnHover,
                    color = color,
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
