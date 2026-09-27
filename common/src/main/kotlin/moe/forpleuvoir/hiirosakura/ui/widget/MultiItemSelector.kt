package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.draw.alpha
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.lazy.grid.rememberLazyGridState

/** 飞行动画时长(ms)与缩小时长(ms)。 */
private const val FlyDurationMillis = 260
private const val ShrinkDurationMillis = 160

/** 已选容器每格的边长与格间距。 */
private val SelectedCellSize = 40.dp
private val SelectedCellGap = 4.dp

/**
 * 多物品选择器:基于 [ItemBrowser] 的单选版本([ItemSelector])改造。
 *
 * 与单选版的区别:
 * - 点物品是**切换选中**,不再关闭对话框;
 * - 选中时该物品从**被点的那个格子**飞向下方容器里的槽位;
 * - 点下方容器里的物品是移除,并播放**缩小**动画;
 * - 只有「确认」才写回,「取消」/ESC 丢弃;[limit] 不为 null 时容器左上角显示 n/limit。
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

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (value.isEmpty()) {
            ItemBrowserDefaults.ItemWrapper(
                Items.AIR,
                border = false,
                scaleOnHover = 1f,
                modifier = Modifier.size(LocalItemIconVanillaSize.current),
                showTooltip = false,
            ) { showDialog = true }
        } else {
            value.forEach { item ->
                ItemBrowserDefaults.ItemWrapper(
                    item,
                    border = false,
                    scaleOnHover = 1f,
                    modifier = Modifier.size(LocalItemIconVanillaSize.current),
                    showTooltip = showTooltip,
                ) { showDialog = true }
            }
        }
    }

    if (!showDialog) return

    val selected = remember { value.toMutableStateList() }
    // 布局期(onGloballyPositioned)只写**普通 Map**:往快照状态里写会撞测量/快照锁(卡死)。
    // 飞行开始时按需读一次即可,不需要响应式。
    val cellPositions = remember { HashMap<Any, Offset>() }
    val slotPositions = remember { HashMap<Any, Offset>() }
    var overlayOrigin by remember { mutableStateOf(Offset.Zero) }
    var flyingItem by remember { mutableStateOf<Item?>(null) }
    var flyingFrom by remember { mutableStateOf(Offset.Zero) }
    var flyingTarget by remember { mutableStateOf<Offset?>(null) }
    // 从 0 起:1f 会让覆盖层在槽位坐标还没上报的那一帧先画在终点(表现为闪一下)
    val flyProgress = remember { Animatable(0f) }
    var shrinkingKey by remember { mutableStateOf<Any?>(null) }
    val selectedGridState = rememberLazyGridState()

    FlexibleDialog(
        onDismissRequest = { showDialog = false },
        onConfirmRequest = {
            onValueChange(selected.toList())
            showDialog = false
            true
        },
        content = {
            // 容器格子尺寸与网格取同一个来源,保证两边物品一样大
            val selectedCellSize = ItemBrowserDefaults.gridCellSize(contentPadding = PaddingValues(4.dp))
            Box(
                modifier = Modifier
                    .size(680.dp, 520.dp)
                    .onGloballyPositioned { overlayOrigin = it.positionInRoot() },
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    ItemBrowser(
                        itemDisplay = { item ->
                            val asItem = item.asItem()
                            val picked = asItem in selected
                            val itemKey = asItem.key
                            ItemBrowserDefaults.ItemWrapper(
                                item,
                                // border 是 ItemWrapper 的**悬停**描边,对所有格子都开;
                                // 选中态由下面的 50% 半透明表示,不占用描边。
                                border = true,
                                showTooltip = showTooltip,
                                modifier = Modifier
                                    .fillMaxSize()
                                    // 已选中的物品在备选区是禁用态:50% 半透明
                                    .alpha(if (picked) 0.5f else 1f)
                                    .onGloballyPositioned { cellPositions[itemKey] = it.positionInRoot() },
                            ) {
                                when {
                                    // 备选区里点已经选中的物品:没有任何反应(移除只能在下方容器里点)
                                    picked -> Unit
                                    limit == null || selected.size < limit -> {
                                        // 先立"正在飞"标志再入列:槽位**第一次组合**时就已经是隐藏的(不再闪一帧)
                                        flyingItem = asItem
                                        flyingFrom = cellPositions[itemKey] ?: Offset.Zero
                                        flyingTarget = null
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

                    Spacer(Modifier.height(8.dp))


                    // 上限计数放在容器**上方**(不在框里),留出左边距
                    if (limit != null) {
                        Text("${selected.size}/$limit", modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
                    }
                    Surface(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            if (selected.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(selectedCellSize),
                                    contentAlignment = Alignment.Center,
                                ) {}
                            } else {
                                LazyVerticalGrid(
                                    state = selectedGridState,

                                    columns = GridCells.Adaptive(selectedCellSize),
                                    modifier = Modifier.fillMaxWidth().heightIn(max = 160.dp),
                                    horizontalArrangement = Arrangement.spacedBy(SelectedCellGap),
                                    verticalArrangement = Arrangement.spacedBy(SelectedCellGap),
                                ) {
                                    itemsIndexed(selected, key = { _, item -> item.key }) { _, item ->
                                        val itemKey = item.key
                                        val shrinking = shrinkingKey == itemKey
                                        val scale by animateFloatAsState(
                                            targetValue = if (shrinking) 0f else 1f,
                                            animationSpec = tween(ShrinkDurationMillis),
                                        )
                                        LaunchedEffect(shrinking) {
                                            if (shrinking) {
                                                delay(ShrinkDurationMillis.toLong())
                                                selected.remove(item)
                                                if (shrinkingKey == itemKey) shrinkingKey = null
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(selectedCellSize)
                                                .onGloballyPositioned { slotPositions[itemKey] = it.positionInRoot() }
                                                .scale(scale),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            // 正飞向这里的那一件先不画:槽位占好位置,图标等飞到位才出现
                                            if (flyingItem != item) {
                                                ItemBrowserDefaults.ItemWrapper(
                                                    item,
                                                    border = false,
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

                val flying = flyingItem
                if (flying != null) {
                    LaunchedEffect(flying) {
                        flyProgress.snapTo(0f)
                        // 等容器把这件物品排好并稳定(普通 Map 不引起重组,轮询即可;最多等约 0.3s)
                        var waited = 0
                        while (slotPositions[flying.key] == null && waited < 40) {
                            delay(8)
                            waited++
                        }
                        delay(16)
                        flyingTarget = slotPositions[flying.key]
                        if (flyingTarget != null) {
                            flyProgress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(FlyDurationMillis, easing = FastOutSlowInEasing),
                            )
                        }
                        flyingItem = null
                        flyingTarget = null
                    }
                    val progress = flyProgress.value
                    val from = flyingFrom - overlayOrigin
                    val dest = (flyingTarget ?: flyingFrom) - overlayOrigin
                    // 用与格子同尺寸的 Box 承载图标(图标在格内居中):起点/终点与格子坐标严格对齐
                    Box(
                        modifier = Modifier
                            .size(selectedCellSize)
                            .offset {
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
