package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.runtime.compositionLocalOf

/**
 * [MultiItemSelector] / [MultiItemSelectorDialog] 的缺省值。
 *
 * 时长、尺寸、间距集中在这里,调用方要调观感不必改组件源码。
 */
object MultiItemSelectorDefaults {

    /** 触发入口最多摆几件(其余用 `+N` 表示):摆满会把屏幕外的物品也一路渲染掉。 */
    val LocalTriggerPreviewCount = compositionLocalOf { 4 }

    /** 触发入口已选图标之间的间距。 */
    val LocalTriggerGap = compositionLocalOf { 4.dp }

    /** 对话框尺寸(与单选版 [ItemSelector] 一致)。 */
    val LocalDialogSize = compositionLocalOf { DpSize(680.dp, 520.dp) }

    /** 备选网格的内容内边距(同时决定格子尺寸)。 */
    val LocalGridContentPadding = compositionLocalOf { PaddingValues(4.dp) }

    /** 已选容器的格间距。 */
    val LocalCellGap = compositionLocalOf { 4.dp }

    /** 已选容器的内边距。 */
    val LocalContainerPadding = compositionLocalOf { 8.dp }

    /** 已选容器的最大高度:超过后容器内滚动。 */
    val LocalContainerMaxHeight = compositionLocalOf { 160.dp }

    /** 备选网格与已选容器之间的间距。 */
    val LocalContentSpacing = compositionLocalOf { 8.dp }

    /** 上限计数文本的内边距。 */
    val LocalLimitLabelPadding = compositionLocalOf { PaddingValues(start = 8.dp, bottom = 4.dp) }

    /** 备选区里「已选中(禁用态)」的不透明度。 */
    val LocalDisabledAlpha = compositionLocalOf { 0.5f }

    /** 选中后飞向容器的时长。 */
    val LocalFlyDuration = compositionLocalOf { 260.milliseconds }

    /** 从容器移除前的缩小动画时长。 */
    val LocalShrinkDuration = compositionLocalOf { 160.milliseconds }
}

/**
 * 多物品选择对话框:点物品**累积选中**(不关闭对话框),已选物品集中在下方容器里,
 * 点容器里的物品移除,只有「确认」才写回、「取消」/ESC 丢弃。
 *
 * 独立于触发入口 —— 想在别处打开(按钮、快捷键、其他界面)直接调它即可,不必套 [MultiItemSelector]。
 *
 * - 选中时该物品从**被点的那个格子**飞向它在容器里的槽位;
 * - 移除时先播缩小动画,放完才真正从列表里移除;
 * - [limit] 不为 null 时在容器上方显示 `n/limit`,已选中的物品在备选区显示为 50% 半透明(禁用态)。
 *
 * @param value 初始已选物品(仅在打开时读取一次)
 * @param onDismissRequest 取消 / ESC
 * @param onConfirmRequest 确认,交出最终的已选列表
 * @param limit 选择上限;null = 不限
 */
@Composable
fun MultiItemSelectorDialog(
    value: List<Item>,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (List<Item>) -> Unit,
    modifier: Modifier = Modifier,
    limit: Int? = null,
    showTooltip: Boolean = true,
) {
    // 观感参数一律从 CompositionLocal 取(见 MultiItemSelectorDefaults,可局部覆盖)
    val dialogSize = MultiItemSelectorDefaults.LocalDialogSize.current
    val gridContentPadding = MultiItemSelectorDefaults.LocalGridContentPadding.current
    val cellGap = MultiItemSelectorDefaults.LocalCellGap.current
    val containerPadding = MultiItemSelectorDefaults.LocalContainerPadding.current
    val containerMaxHeight = MultiItemSelectorDefaults.LocalContainerMaxHeight.current
    val contentSpacing = MultiItemSelectorDefaults.LocalContentSpacing.current
    val limitLabelPadding = MultiItemSelectorDefaults.LocalLimitLabelPadding.current
    val disabledAlpha = MultiItemSelectorDefaults.LocalDisabledAlpha.current
    val flyDuration = MultiItemSelectorDefaults.LocalFlyDuration.current
    val shrinkDuration = MultiItemSelectorDefaults.LocalShrinkDuration.current

    // 每次打开从 value 复制:取消 / ESC 不会污染外部状态
    val selected = remember { value.toMutableStateList() }

    // 布局期(onGloballyPositioned)只写**普通容器**:往快照状态里写会撞测量/快照锁(卡死)。
    // 飞行时需要坐标,按需读一次即可,不需要响应式。
    val cellPositions = remember { HashMap<Any, Offset>() }
    val slotPositions = remember { HashMap<Any, Offset>() }
    val overlayOrigin = remember { arrayOf(Offset.Zero) }

    var flyingItem by remember { mutableStateOf<Item?>(null) }
    var flyingFrom by remember { mutableStateOf(Offset.Zero) }
    // 从 0 起:1f 会让覆盖层在槽位坐标还没上报的那一帧先画在终点(表现为闪一下)
    val flyProgress = remember { Animatable(0f) }
    var shrinkingKey by remember { mutableStateOf<Any?>(null) }
    val selectedGridState = rememberLazyGridState()

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onConfirmRequest(selected.toList())
            true
        },
        content = {
            // 容器格子尺寸与网格取同一个来源,保证两边物品一样大
            val selectedCellSize = ItemBrowserDefaults.gridCellSize(contentPadding = gridContentPadding)
            Box(
                modifier = modifier
                    .size(dialogSize)
                    .onGloballyPositioned { overlayOrigin[0] = it.positionInRoot() },
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    ItemBrowser(
                        itemDisplay = { item ->
                            val asItem = item.asItem()
                            val picked = asItem in selected
                            val itemKey = asItem.key
                            ItemBrowserDefaults.ItemWrapper(
                                item,
                                // 悬停反馈由 ItemWrapper 内部挂的 IG 悬停高亮承担
                                hoverHighlight = true,
                                // 选中态 = 禁用态:50% 半透明。用**物品着色**表达 —— 它走物品绘制着色,
                                // 不建 graphicsLayer(本平台图层是命令烘焙 + 父链连锁重录,用 Modifier.alpha
                                // 会为每个选中项建层,选中越多越慢)。
                                color = if (picked) Color.White.copy(alpha = disabledAlpha) else Color.White,
                                showTooltip = showTooltip,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onGloballyPositioned { cellPositions[itemKey] = it.positionInRoot() },
                            ) {
                                when {
                                    // 备选区里点已经选中的物品:没有任何反应(移除只能在下方容器里点)
                                    picked -> Unit
                                    limit == null || selected.size < limit -> {
                                        // 先立"正在飞"标志再入列:槽位**第一次组合**时就已经是隐藏的(不再闪一帧)
                                        flyingItem = asItem
                                        flyingFrom = (cellPositions[itemKey] ?: Offset.Zero) - overlayOrigin[0]
                                        selected.add(asItem)
                                        // 自动滚到最新项:用 requestScrollToItem(非挂起,下次测量趟生效),
                                        // 而不是在协程里 scrollToItem/animateScrollToItem —— 后者在这个渲染栈会卡死场景。
                                        selectedGridState.requestScrollToItem(selected.size - 1)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(Modifier.height(contentSpacing))

                    // 上限计数放在容器**上方**(不在框里),留出左边距
                    if (limit != null) {
                        Text("${selected.size}/$limit", modifier = Modifier.padding(limitLabelPadding))
                    }

                    // 高度**由内容决定**(不自己算、不固定):加一行就长一行,一行也放得下。
                    // 这里**不能**套 animateContentSize:内部是 LazyVerticalGrid + heightIn,
                    // 网格会跟着动画后的高度一起长 → 目标尺寸每帧都变 → 尺寸动画永不收敛。
                    Surface(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(containerPadding)) {
                            if (selected.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(selectedCellSize),
                                    contentAlignment = Alignment.Center,
                                ) {}
                            } else {
                                LazyVerticalGrid(
                                    state = selectedGridState,
                                    columns = GridCells.Adaptive(selectedCellSize),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = containerMaxHeight),
                                    horizontalArrangement = Arrangement.spacedBy(cellGap),
                                    verticalArrangement = Arrangement.spacedBy(cellGap),
                                ) {
                                    itemsIndexed(selected, key = { _, item -> item.key }) { _, item ->
                                        val itemKey = item.key
                                        val shrinking = shrinkingKey == itemKey
                                        val scale by animateFloatAsState(
                                            targetValue = if (shrinking) 0f else 1f,
                                            animationSpec = tween(shrinkDuration.inWholeMilliseconds.toInt()),
                                        )
                                        // 缩小动画放完再真正移除(否则物品先消失、动画没得播)
                                        LaunchedEffect(shrinking) {
                                            if (shrinking) {
                                                delay(shrinkDuration)
                                                selected.remove(item)
                                                if (shrinkingKey == itemKey) shrinkingKey = null
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(selectedCellSize)
                                                .onGloballyPositioned { slotPositions[itemKey] = it.positionInRoot() }
                                                // 只在播放缩小时才套 scale,否则每个槽位都会白白多一个图层
                                                .then(if (shrinking) Modifier.scale(scale) else Modifier),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            // 正飞向这里的那一件先不画:槽位占好位置,图标等飞到位才出现
                                            if (flyingItem != item) {
                                                ItemBrowserDefaults.ItemWrapper(
                                                    item,
                                                    hoverHighlight = false,
                                                    scaleOnHover = 1f,
                                                    modifier = Modifier.fillMaxSize(),
                                                    showTooltip = false,
                                                ) { shrinkingKey = itemKey }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 飞行副本:起点(被点的格子)→ 终点(它在容器里的槽位),到位后消失
                val flying = flyingItem
                if (flying != null) {
                    // 协程在这里**只推进进度**:终点在绘制时从槽位坐标实时读,不在协程里写状态(那会撞快照锁)
                    LaunchedEffect(flying) {
                        flyProgress.snapTo(0f)
                        flyProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(flyDuration.inWholeMilliseconds.toInt(), easing = FastOutSlowInEasing),
                        )
                        flyingItem = null
                    }
                    val progress = flyProgress.value
                    // 用与格子同尺寸的 Box 承载图标(图标在格内居中):起点/终点与格子坐标严格对齐
                    Box(
                        modifier = Modifier
                            .size(selectedCellSize)
                            .offset {
                                // 起点=被点的格子(点击时算好);终点=该物品槽位的**实时**坐标:
                                // 槽位布局出来就立刻用上,不需要在协程里写状态(那会撞快照锁 → 卡死)
                                val from = flyingFrom
                                val dest = slotPositions[flying.key]?.minus(overlayOrigin[0]) ?: from
                                IntOffset(
                                    (from.x + (dest.x - from.x) * progress).roundToInt(),
                                    (from.y + (dest.y - from.y) * progress).roundToInt(),
                                )
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        ItemIcon(
                            ItemStack(flying),
                            size = DpSize(
                                ItemBrowserDefaults.LocalItemIconSize.current,
                                ItemBrowserDefaults.LocalItemIconSize.current,
                            ),
                            showTooltip = false,
                            scaleOnHover = 1f,
                        )
                    }
                }
            }
        },
    )
}

/**
 * 多物品选择器的触发入口:横排预览已选物品(最多 [triggerPreviewCount] 件,
 * 其余用 `+N` 表示),点击打开 [MultiItemSelectorDialog]。
 *
 * 与单选版 [ItemSelector] 的区别是「选中不关闭对话框、可累积、可移除、要确认」—— 详见 [MultiItemSelectorDialog]。
 *
 * @param value 当前已选物品
 * @param onValueChange 确认后的回调(取消不回调)
 * @param limit 选择上限;null = 不限
 */
@Composable
fun MultiItemSelector(
    value: List<Item>,
    onValueChange: (List<Item>) -> Unit,
    modifier: Modifier = Modifier,
    limit: Int? = null,
    showTooltip: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    // 观感参数一律从 CompositionLocal 取(见 MultiItemSelectorDefaults,可局部覆盖)
    val triggerPreviewCount = MultiItemSelectorDefaults.LocalTriggerPreviewCount.current
    val triggerGap = MultiItemSelectorDefaults.LocalTriggerGap.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(triggerGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (value.isEmpty()) {
            ItemBrowserDefaults.ItemWrapper(
                Items.AIR,
                hoverHighlight = false,
                scaleOnHover = 1f,
                modifier = Modifier.size(LocalItemIconVanillaSize.current),
                showTooltip = false,
            ) { showDialog = true }
        } else {
            // 只摆前几件:摆满时对话框打开期间主屏与对话框会**同时**渲染这些图标,
            // 而且 Row 没有懒加载,屏幕外的物品也会参与渲染。
            val preview = triggerPreviewCount
            value.take(preview).forEach { item ->
                ItemBrowserDefaults.ItemWrapper(
                    item,
                    hoverHighlight = false,
                    scaleOnHover = 1f,
                    modifier = Modifier.size(LocalItemIconVanillaSize.current),
                    showTooltip = showTooltip,
                ) { showDialog = true }
            }
            if (value.size > preview) {
                Text("+${value.size - preview}")
            }
        }
    }

    if (showDialog) {
        MultiItemSelectorDialog(
            value = value,
            onDismissRequest = { showDialog = false },
            onConfirmRequest = {
                onValueChange(it)
                showDialog = false
            },
            limit = limit,
            showTooltip = showTooltip,
        )
    }
}
