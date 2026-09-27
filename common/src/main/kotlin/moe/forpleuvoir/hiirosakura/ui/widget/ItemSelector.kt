package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import kotlinx.coroutines.flow.distinctUntilChanged
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
import moe.forpleuvoir.ibukigourd.ui.util.FabVisibilityState
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
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
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
import moe.forpleuvoir.ibukigourd.ui.util.rememberHideActionState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.graphicsLayer
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Tab
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TabRow
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TabRowDefaults
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.LazyGridState
import moe.forpleuvoir.ibukigourd.ui.item.ItemIconDefaults
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.foundation.interaction.HoverInteraction
import moe.forpleuvoir.ibukigourd.ui.sokitsu.hoverHighlight
import androidx.compose.runtime.LaunchedEffect

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
    itemGroups: List<ResourceKey<CreativeModeTab>>? = null,
    itemDisplay: @Composable (ItemLike) -> Unit = ItemBrowserDefaults::ItemWrapper,
    filter: (ItemLike) -> Boolean = { true },
    searchItems: List<ItemLike>? = null,
    gridCellSize: Dp = ItemBrowserDefaults.gridCellSize(contentPadding = PaddingValues(4.dp)),
    searchBarBackgroundColor: Color = SokitsuTheme.colorScheme.surface,
    modifier: Modifier = Modifier
) {
    // 这两个默认值原来直接求值,而 ItemBrowser 会随对话框状态反复重组:
    // `getAllTabs()` 内部是 `CreativeModeTabs.tryRebuildTabContents(...)`(重建全部分类内容),
    // `allItem.toList()` 会复制整个物品注册表 —— 每重组一次就来一遍。改成只算一次。
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

    // 切分类从顶部开始:这里用「按分类各记一个网格状态」实现,而不是切页后 scrollToItem ——
    // 后者是在协程里写滚动状态,在本渲染栈下会撞上测量/快照锁把整个场景卡死
    // (见 FabVisibility.kt 的说明;本文件 181 行那次报错就是它)。
    val gridState = remember(selectedTabIndex) { LazyGridState() }
    // 滚动显隐的嵌套滚动回调必须挂在滚动容器的祖先上;搜索框是它的兄弟节点,挂在搜索框上收不到位移
    val fabVisibility = rememberFabScrollVisibility(gridState)

    val textFieldState = rememberTextFieldState()
    var searchQuery by remember { mutableStateOf("") }
    // 用 snapshotFlow 收敛输入,而不是在组合期读 textFieldState.text:后者会让每次按键都重组
    // 整个浏览器(含整个网格),输入非常卡。
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .collect { searchQuery = it }
    }

    // 当前页签的物品清单:只在页签/入参变化时重算一次(原来用 mutableStateOf 包了一层多余状态)
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
        // 分类条用 IG 的 TabRow + Tab。TabRow 是等宽的,分类多时**显式给出内容宽度**交给横向滚动 ——
        // 与 IG dev 测试页 TabRowTest(40 个页签)完全同一个做法:外层 Box 横向滚动,
        // 内层给足内容宽,滚轮(见 CMP 的 scrollDelta 修复)和拖动都能到达后面的分类。
        val tabDensity = LocalDensity.current
        val tabScroll = rememberScrollState()
        val tabContentWidth = CategoryTabWidth * tabList.size + TabRowDefaults.tabGap * (tabList.size - 1).coerceAtLeast(0)
        // 选中页签自动滚进视野(照 IG dev 测试页 TabRowTest 的 M3 ScrollableTabData 同式):
        // 没有这段的话,超出可视区的分类点不到、也翻不过去 —— 测试页能翻页靠的就是它。
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
        // 切换过渡:进度必须在**组合期**读(TabStrip 的注释:只在绘制期读的状态在本渲染栈推进不到)
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
            // 列表与滚动条各占一列:滚动条不再浮在列表上,所以不用 Box + align 叠起来
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
                androidx.compose.animation.AnimatedVisibility(
                    visible = !hiddenByKey && fabVisibility.state == FabVisibilityState.Visible,
                    modifier = Modifier
                        .align(BiasAlignment(0f, 0.85f))
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    enter = fadeIn(tween(150)),
                    exit = fadeOut(tween(150)),
                ) {
                    SearchBar(
                        textFieldState = textFieldState,
                        backgroundColor = searchBarBackgroundColor,
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
    backgroundColor: Color = SokitsuTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // 输入区必须用 weight:原来这里是 fillMaxWidth,会吃掉整行宽度,
            // 把清除按钮挤出 Row 之外 —— 既看不见也点不到。
            Box(Modifier.weight(1f).height(48.dp), contentAlignment = Alignment.CenterStart) {
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
                // 点按目标放大到 36dp、图标放大到 3 倍像素:原来默认尺寸太小不好点
                IconButton(
                    onClick = { textFieldState.edit { replace(0, length, "") } },
                    minSize = DpSize(36.dp, 36.dp),
                ) {
                    Icon(Icons.Close, scale = 3)
                }
            }
        }
    }
}

/** 物品图标的展示尺寸；新版上游移除了旧 IG 的同名组合本地量，这里按本项目像素风取值。 */
val LocalItemIconVanillaSize = staticCompositionLocalOf { DpSize(32.dp, 32.dp) }

/** 分类条每格的内容宽:够放「16dp 图标 + 间隙 + 完整分类名」(不截断)。 */
private val CategoryTabWidth = 168.dp

/** Lazy 网格的条目 key:用注册名,保证切页签 / 搜索时条目身份稳定。 */
private fun itemKey(item: ItemLike): Any = when (item) {
    is Item  -> item.key
    is Block -> item.key
    else     -> item
}

object ItemBrowserDefaults {

    @Composable
    fun gridCellSize(wrapperSize: DpSize = DpSize(LocalItemIconSize.current, LocalItemIconSize.current), contentPadding: PaddingValues): Dp =
        (wrapperSize.width + contentPadding.calculateLeftPadding(LayoutDirection.Ltr) + contentPadding.calculateRightPadding(LayoutDirection.Ltr))
            .coerceAtMost(wrapperSize.height + contentPadding.calculateTopPadding() + contentPadding.calculateBottomPadding())

    val LocalItemIconSize = compositionLocalOf {
        48.dp
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
                    else Modifier
                ),
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

