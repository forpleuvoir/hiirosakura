package moe.forpleuvoir.hiirosakura.ui.widget.radialmenu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.input.MouseButton
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 径向菜单（Radial Menu）
 *
 * 渲染使用圆弧路径描画每个扇区（支持圆角），
 * 命中测试仍使用角度 + 半径范围计算，与实际渲染不要求完全吻合。
 *
 * @param options 选项列表（全部选项，内部自动分页）
 * @param modifier 修饰符
 * @param state 菜单状态（页码等）
 * @param innerRadius 内圆半径
 * @param outerRadius 外圆半径
 * @param optionRadius 选项内容的分布半径
 * @param startAngleDegree 第一个扇区的中心角度（度），-90° 表示顶部
 * @param gap 扇区之间的间隙（线性宽度，任意半径处恒定，间隙两侧保持平行）
 * @param cornerRadius 扇区四个角的圆角半径，0 时为直角扇形
 * @param optionsPerPage 每页最大扇区数
 * @param optionContentSize 每个选项内容的尺寸
 * @param idleOuterColor 未选中扇区的环颜色
 * @param idleInnerColor 未选中扇区的中心颜色
 * @param idleBorderColor 未选中扇区的描边颜色
 * @param selectedOuterColor 选中扇区的环颜色
 * @param selectedInnerColor 选中扇区的中心颜色
 * @param selectedBorderColor 选中扇区的描边颜色
 * @param borderWidth 扇区的描边宽度（选中与未选中共用），0 时不描边
 * @param animation 扇区动画参数（缩放、沿中心点方向的位移等）；
 * 整体旋转无需专门参数，直接在外部对 [startAngleDegree] 做动画即可，命中测试会随之一致旋转
 * @param onOptionClick 选项点击回调，点击扇区时触发：扇区有选项则接收对应选项，无选项则为 null；点击扇区以外区域不触发
 * @param onEmptyClick 点击扇区以外区域（中心、环外）时的回调，接收按键
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
    cornerRadius: Dp = RadialMenuDefaults.CornerRadius,
    optionsPerPage: Int = RadialMenuDefaults.OPTIONS_PRE_PAGE,
    optionContentSize: Dp = RadialMenuDefaults.OptionContentSize,
    idleOuterColor: Color = RadialMenuDefaults.IdleOuterColor,
    idleInnerColor: Color = RadialMenuDefaults.IdleInnerColor,
    idleBorderColor: Color = RadialMenuDefaults.IdleBorderColor,
    selectedOuterColor: Color = RadialMenuDefaults.SelectedOuterColor,
    selectedInnerColor: Color = RadialMenuDefaults.SelectedInnerColor,
    selectedBorderColor: Color = RadialMenuDefaults.SelectedBorderColor,
    borderWidth: Dp = RadialMenuDefaults.BorderWidth,
    animation: RadialMenuAnimation = RadialMenuDefaults.Animation,
    onOptionClick: (T?, MouseButton) -> Unit = { _, _ -> },
    onEmptyClick: (MouseButton) -> Unit = {},
    centerContent: @Composable BoxScope.(selected: T?) -> Unit = {},
    optionContent: @Composable BoxScope.(option: T, selected: Boolean) -> Unit = { _, _ -> },
) {
    require(optionsPerPage > 0) { "optionsPerPage must be > 0" }
    require(innerRadius >= 0.dp) { "innerRadius must be >= 0" }
    require(outerRadius > innerRadius) { "outerRadius must be > innerRadius" }
    require(optionRadius >= 0.dp) { "optionRadius must be >= 0" }
    require(gap >= 0f) { "gap must be >= 0" }
    require(cornerRadius >= 0.dp) { "cornerRadius must be >= 0" }
    require(borderWidth >= 0.dp) { "borderWidth must be >= 0" }

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
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }
    val borderWidthPx = with(density) { borderWidth.toPx() }

    val center = remember(containerSize) {
        Offset(containerSize.width / 2f, containerSize.height / 2f)
    }

    val availableRadius = minOf(containerSize.width, containerSize.height) / 2f
    val layoutScale = if (outerRadiusPx > 0f) (availableRadius / outerRadiusPx).coerceAtMost(1f) else 1f
    val actualInnerRadiusPx = innerRadiusPx * layoutScale
    val actualOuterRadiusPx = outerRadiusPx * layoutScale
    val actualOptionRadiusPx = optionRadiusPx * layoutScale
    val actualGap = gap * layoutScale
    val actualCornerRadiusPx = cornerRadiusPx * layoutScale
    val actualBorderWidthPx = borderWidthPx * layoutScale

    val sectors = remember(startAngleDegree, optionsPerPage) {
        calculateSectors(startAngleDegree, optionsPerPage)
    }

    val sectorPathMap = remember(sectors, actualInnerRadiusPx, actualOuterRadiusPx, actualGap, actualCornerRadiusPx, center) {
        sectors.map { sector ->
            getSectorPath(center, sector, actualInnerRadiusPx, actualOuterRadiusPx, actualGap, actualCornerRadiusPx)
        }
    }

    var hoveredIndex by remember { mutableStateOf(-1) }

    // 每个扇区独立的选中进度（0f 未选中 -> 1f 选中），驱动扇区动画
    val sectorProgresses = List(sectors.size) { index ->
        animateFloatAsState(
            targetValue = if (index == hoveredIndex) 1f else 0f,
            animationSpec = animation.spec,
            label = "RadialMenuSectorProgress",
        )
    }

    val selectedOption by remember(hoveredIndex, currentPage) {
        derivedStateOf { currentPage.getOrNull(hoveredIndex) }
    }

    val currentOnOptionClick by rememberUpdatedState(onOptionClick)
    val currentOnEmptyClick by rememberUpdatedState(onEmptyClick)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(sectors, center, actualInnerRadiusPx, actualOuterRadiusPx, currentPage) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        when (event.type) {
                            PointerEventType.Move   -> {
                                val change = event.changes.firstOrNull() ?: continue
                                hoveredIndex = findSectorIndex(
                                    sectors = sectors,
                                    center = center,
                                    pointer = change.position,
                                    innerRadiusPx = actualInnerRadiusPx,
                                    outerRadiusPx = actualOuterRadiusPx,
                                )
                            }

                            PointerEventType.Exit   -> hoveredIndex = -1
                            PointerEventType.Press  -> {
                                val change = event.changes.firstOrNull() ?: continue
                                val idx = findSectorIndex(
                                    sectors = sectors,
                                    center = center,
                                    pointer = change.position,
                                    innerRadiusPx = actualInnerRadiusPx,
                                    outerRadiusPx = actualOuterRadiusPx,
                                )
                                val button = when {
                                    event.buttons.isPrimaryPressed   -> MouseButton.LEFT
                                    event.buttons.isTertiaryPressed  -> MouseButton.MIDDLE
                                    event.buttons.isSecondaryPressed -> MouseButton.RIGHT
                                    event.buttons.isBackPressed      -> MouseButton.BUTTON_4
                                    event.buttons.isForwardPressed   -> MouseButton.BUTTON_5
                                    else                             -> null
                                }
                                button?.let {
                                    if (idx >= 0) {
                                        // 点击扇区：有选项返回对应选项，无选项返回 null
                                        currentOnOptionClick(currentPage.getOrNull(idx), button)
                                    } else {
                                        // 点击扇区以外区域，不进入 onOptionClick
                                        currentOnEmptyClick(button)
                                    }
                                }
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
                val sectorRadialOffsetPx = animation.sectorRadialOffset.toPx() * layoutScale
                val drawSector: (Int, Path, Color, Color, Color?) -> Unit = { index, path, innerColor, outerColor, borderColor ->
                    val progress = sectorProgresses[index].value
                    val sectorScale = 1f + (animation.sectorScale - 1f) * progress
                    val radialOffset = sectorRadialOffsetPx * progress
                    val draw: DrawScope.() -> Unit = {
                        drawRadialGradientSector(
                            path = path,
                            center = center,
                            innerRadius = actualInnerRadiusPx,
                            outerRadius = actualOuterRadiusPx,
                            innerColor = innerColor,
                            outerColor = outerColor,
                        )
                        if (borderColor != null && actualBorderWidthPx > 0f) {
                            drawPath(path, borderColor, style = Stroke(width = actualBorderWidthPx))
                        }
                    }
                    if (sectorScale != 1f || radialOffset != 0f) {
                        val radians = Math.toRadians(sectors[index].centerAngle.toDouble())
                        val dirX = cos(radians).toFloat()
                        val dirY = sin(radians).toFloat()
                        // 缩放原点取内容锚点（扇区中心角方向、option 半径处），
                        // 扇区围绕内容原地膨胀，不产生额外的径向位移；
                        // 变换矩阵按调用顺序左乘，点先经 scale 再经 translate，位移量与参数严格一致
                        val pivot = Offset(
                            center.x + actualOptionRadiusPx * dirX,
                            center.y + actualOptionRadiusPx * dirY,
                        )
                        withTransform({
                            translate(left = dirX * radialOffset, top = dirY * radialOffset)
                            scale(sectorScale, sectorScale, pivot = pivot)
                        }) {
                            draw()
                        }
                    } else {
                        draw()
                    }
                }
                // 未选中的先画，选中的后画，避免选中扇区放大/位移后被相邻扇区盖住
                sectorPathMap.forEachIndexed { index, path ->
                    if (index != hoveredIndex) {
                        drawSector(index, path, idleInnerColor, idleOuterColor, idleBorderColor)
                    }
                }
                sectorPathMap.forEachIndexed { index, path ->
                    if (index == hoveredIndex) {
                        drawSector(index, path, selectedInnerColor, selectedOuterColor, selectedBorderColor)
                    }
                }
            },
    ) {
        currentPage.forEachIndexed { index, option ->
            val sector = sectors[index]
            val progress = sectorProgresses[index]
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
                    .graphicsLayer {
                        val p = progress.value
                        val contentScale = 1f + (animation.contentScale - 1f) * p
                        scaleX = contentScale
                        scaleY = contentScale
                        // 与扇区同步：扇区缩放以内容锚点为原点（不位移锚点），
                        // 因此内容位移 = 扇区径向位移 + 内容额外径向位移
                        val radialOffset =
                            (animation.sectorRadialOffset + animation.contentRadialOffset).toPx() * layoutScale * p
                        if (radialOffset != 0f) {
                            val radians = Math.toRadians(sector.centerAngle.toDouble())
                            translationX = (cos(radians) * radialOffset).toFloat()
                            translationY = (sin(radians) * radialOffset).toFloat()
                        }
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
