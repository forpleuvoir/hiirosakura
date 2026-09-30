package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.ScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter

/**
 * 与内容**并列**的 flat 滚动条列：调用方把它写在 `Row` 里，紧跟在可滚动内容之后。
 *
 * 滚动条占自己的一列、不叠在内容之上（浮在 `CenterEnd` 上会盖住行尾按钮）；
 * **没有可滚动空间时整列不出现**，免得右缘平白多出一条空位。列宽与间距取配置页同一套排版量，
 * 因此各页面的滚动条粗细、与内容的距离都一致。
 *
 * 本列只影响宽度、不影响高度，所以"出现 → 内容变窄 → 仍可滚"不会来回抖。
 *
 * @param lazyListState 惰性列表的滚动状态
 */
@Composable
fun ScrollbarColumn(lazyListState: LazyListState) {
    if (!lazyListState.canScroll) return
    ScrollbarSlot(rememberScrollerAdapter(lazyListState))
}

/**
 * 与内容**并列**的 flat 滚动条列，配合 `Modifier.verticalScroll` 的滚动状态使用。
 *
 * @param scrollState 普通滚动容器的滚动状态
 */
@Composable
fun ScrollbarColumn(scrollState: ScrollState) {
    // 首帧滚动容器还没量过（`maxValue` 仍是初值、`viewportSize` 为 0），先按"没有滚动条"处理
    if (scrollState.viewportSize <= 0 || scrollState.maxValue <= 0) return
    ScrollbarSlot(rememberScrollerAdapter(scrollState))
}

/** 滚动条本体：一段间距 + 一条定宽列；只画，不管显隐。 */
@Composable
private fun ScrollbarSlot(adapter: ScrollerAdapter) {
    Spacer(Modifier.width(ConfigRowDefaults.ScrollbarSpacing))
    Box(Modifier.width(ConfigRowDefaults.ScrollbarWidth).fillMaxHeight()) {
        VerticalFlatScroller(
            adapter = adapter,
            modifier = Modifier.fillMaxSize(),
            autoHide = true,
            autoFade = true,
        )
    }
}
