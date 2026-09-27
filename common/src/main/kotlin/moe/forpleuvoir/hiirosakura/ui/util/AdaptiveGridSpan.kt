package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * 自适应列数网格（`GridCells.Adaptive`）的当前列数。
 *
 * sokitsu 的网格滚动条适配器需要显式列数，而 `LazyGridState` 不对外暴露列数（`GridCells.Adaptive`
 * 的列数还随宽度变化），因此从当前布局里「同一行的可见单元格数」推断：取可见项里 y 偏移最小的一组，
 * 其个数即列数。列数变化时重组刷新。
 */
@Composable
fun rememberAdaptiveGridSpan(state: LazyGridState): Int {
    val visible = state.layoutInfo.visibleItemsInfo
    return remember(visible.size, visible.firstOrNull()?.index) {
        val topY = visible.minOfOrNull { it.offset.y } ?: 0
        visible.count { it.offset.y == topY }.coerceAtLeast(1)
    }
}
