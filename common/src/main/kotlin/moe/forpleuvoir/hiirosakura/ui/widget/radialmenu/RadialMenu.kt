package moe.forpleuvoir.hiirosakura.ui.widget.radialmenu

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class MouseButton { LEFT, MIDDLE, RIGHT }

/**
 * 径向菜单（Radial Menu）
 *
 * 渲染使用四边形条带（[SectorQuad]）描画每个扇区，
 * 命中测试仍使用角度 + 半径范围计算，与实际渲染不要求完全吻合。
 *
 * @param options 选项列表（全部选项，内部自动分页）
 * @param modifier 修饰符
 * @param state 菜单状态（页码等）
 * @param innerRadius 内圆半径
 * @param outerRadius 外圆半径
 * @param optionRadius 选项内容的分布半径
 * @param startAngleDegree 第一个扇区的中心角度（度），-90° 表示顶部
 * @param gap 扇区之间的间隙（线性值，与半径同一单位空间）
 * @param optionsPerPage 每页最大扇区数
 * @param optionContentSize 每个选项内容的尺寸
 * @param idleOuterColor 未选中扇区的环颜色
 * @param idleInnerColor 未选中扇区的中心颜色
 * @param selectedOuterColor 选中扇区的环颜色
 * @param selectedInnerColor 选中扇区的中心颜色
 * @param onOptionClick 选项点击回调，接收被点击的选项与按键
 * @param centerContent 中心区域内容插槽
 * @param optionContent 选项内容插槽
 */
@Composable
fun <T> RadialMenu(
    options: List<T>,
    modifier: Modifier = Modifier,
    state: RadialMenuState = rememberRadialMenuState(),
    innerRadius: Dp = RadialMenuDefaults.InnerRadius,
    outerRadius: Dp = RadialMenuDefaults.OuterRadius,
    optionRadius: Dp = RadialMenuDefaults.OptionRadius,
    startAngleDegree: Float = RadialMenuDefaults.START_ANGLE_DEGREE,
    gap: Float = RadialMenuDefaults.GAP,
    optionsPerPage: Int = RadialMenuDefaults.OPTIONS_PRE_PAGE,
    optionContentSize: Dp = RadialMenuDefaults.OptionContentSize,
    idleOuterColor: Color = RadialMenuDefaults.IdleOuterColor,
    idleInnerColor: Color = RadialMenuDefaults.IdleInnerColor,
    selectedOuterColor: Color = RadialMenuDefaults.SelectedOuterColor,
    selectedInnerColor: Color = RadialMenuDefaults.SelectedInnerColor,
    onOptionClick: (T?, MouseButton) -> Unit = { _, _ -> },
    centerContent: @Composable BoxScope.(selected: T?) -> Unit = {},
    optionContent: @Composable BoxScope.(option: T, selected: Boolean) -> Unit = { _, _ -> },
) {
    require(optionsPerPage > 0) { "optionsPerPage must be > 0" }
    require(innerRadius >= 0.dp) { "innerRadius must be >= 0" }
    require(outerRadius > innerRadius) { "outerRadius must be > innerRadius" }
    require(optionRadius >= 0.dp) { "optionRadius must be >= 0" }
    require(gap >= 0f) { "gap must be >= 0" }

    val density = LocalDensity.current
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val pageCount = remember(options, optionsPerPage) {
        maxOf(1, (options.size + optionsPerPage - 1) / optionsPerPage)
    }

    LaunchedEffect(pageCount) {
        state.clampPageIndex(pageCount)
    }

    val currentPage by remember(state.currentPageIndex, optionsPerPage, options) {
        derivedStateOf {
            options.drop(state.currentPageIndex * optionsPerPage).take(optionsPerPage)
        }
    }

    val innerRadiusPx = with(density) { innerRadius.toPx() }
    val outerRadiusPx = with(density) { outerRadius.toPx() }
    val optionRadiusPx = with(density) { optionRadius.toPx() }
    val optionContentSizePx = with(density) { optionContentSize.toPx() }

    val center = remember(containerSize) {
        Offset(containerSize.width / 2f, containerSize.height / 2f)
    }

    val availableRadius = minOf(containerSize.width, containerSize.height) / 2f
    val scale = if (outerRadiusPx > 0f) (availableRadius / outerRadiusPx).coerceAtMost(1f) else 1f
    val actualInnerRadiusPx = innerRadiusPx * scale
    val actualOuterRadiusPx = outerRadiusPx * scale
    val actualOptionRadiusPx = optionRadiusPx * scale
    val actualGap = gap * scale

    val sectors = remember(startAngleDegree, optionsPerPage) {
        calculateSectors(startAngleDegree, optionsPerPage)
    }

    val sectorQuadMap = remember(sectors, actualInnerRadiusPx, actualOuterRadiusPx, actualGap, center) {
        sectors.map { sector ->
            getRadialSectorQuads(center, sector, actualInnerRadiusPx, actualOuterRadiusPx, actualGap)
        }
    }

    var hoveredIndex by remember { mutableStateOf(-1) }

    val selectedOption by remember(hoveredIndex, currentPage) {
        derivedStateOf { currentPage.getOrNull(hoveredIndex) }
    }

    val currentOnOptionClick by rememberUpdatedState(onOptionClick)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(sectors, center, actualInnerRadiusPx, actualOuterRadiusPx, currentPage) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        when (event.type) {
                            PointerEventType.Move -> {
                                val change = event.changes.firstOrNull() ?: continue
                                hoveredIndex = findSectorIndex(
                                    sectors = sectors,
                                    center = center,
                                    pointer = change.position,
                                    innerRadiusPx = actualInnerRadiusPx,
                                    outerRadiusPx = actualOuterRadiusPx,
                                )
                            }
                            PointerEventType.Exit -> hoveredIndex = -1
                            PointerEventType.Press -> {
                                val change = event.changes.firstOrNull() ?: continue
                                val idx = findSectorIndex(
                                    sectors = sectors,
                                    center = center,
                                    pointer = change.position,
                                    innerRadiusPx = actualInnerRadiusPx,
                                    outerRadiusPx = actualOuterRadiusPx,
                                )
                                val button = when {
                                    event.buttons.isTertiaryPressed -> MouseButton.MIDDLE
                                    event.buttons.isSecondaryPressed -> MouseButton.RIGHT
                                    else -> MouseButton.LEFT
                                }
                                currentOnOptionClick(currentPage.getOrNull(idx), button)
                                change.consume()
                            }
                            PointerEventType.Scroll -> {
                                val change = event.changes.firstOrNull() ?: continue
                                if (change.scrollDelta.y != 0f) {
                                    val direction = if (change.scrollDelta.y > 0f) 1 else -1
                                    val newPage = (state.currentPageIndex + direction)
                                        .coerceIn(0, (pageCount - 1).coerceAtLeast(0))
                                    if (newPage != state.currentPageIndex) {
                                        state.currentPageIndex = newPage
                                        hoveredIndex = -1
                                    }
                                    change.consume()
                                }
                            }
                        }
                    }
                }
            }
            .drawBehind {
                sectorQuadMap.forEachIndexed { index, quads ->
                    val isSelected = index == hoveredIndex
                    if (isSelected) {
                        drawRadialGradientSector(
                            quads = quads,
                            center = center,
                            innerRadius = actualInnerRadiusPx,
                            outerRadius = actualOuterRadiusPx,
                            innerColor = selectedInnerColor,
                            outerColor = selectedOuterColor,
                        )
                    }
                }
                sectorQuadMap.forEachIndexed { index, quads ->
                    if (index != hoveredIndex) {
                        drawRadialGradientSector(
                            quads = quads,
                            center = center,
                            innerRadius = actualInnerRadiusPx,
                            outerRadius = actualOuterRadiusPx,
                            innerColor = idleInnerColor,
                            outerColor = idleOuterColor,
                        )
                    }
                }
            },
    ) {
        currentPage.forEachIndexed { index, option ->
            val sector = sectors[index]
            val targetCenter = pointOnCircle(center, actualOptionRadiusPx, sector.centerAngle)
            val halfSize = optionContentSizePx / 2f
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (targetCenter.x - halfSize).roundToInt(),
                            (targetCenter.y - halfSize).roundToInt()
                        )
                    }
                    .size(optionContentSize),
                contentAlignment = Alignment.Center,
            ) {
                optionContent(option, index == hoveredIndex && index in currentPage.indices)
            }
        }

        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
                    centerContent(selectedOption)
                }
                if (pageCount > 1) {
                    Spacer(Modifier.height(4.dp))
                    PageIndicator(
                        pageCount = pageCount,
                        currentPageIndex = state.currentPageIndex,
                    )
                }
            }
        }
    }
}
