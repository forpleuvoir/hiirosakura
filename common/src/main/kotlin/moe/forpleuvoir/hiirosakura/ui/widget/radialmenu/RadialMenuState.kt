package moe.forpleuvoir.hiirosakura.ui.widget.radialmenu

import androidx.compose.runtime.*
import kotlin.math.max

@Stable
class RadialMenuState(currentPageIndex: Int = 0) {
    var currentPageIndex by mutableIntStateOf(currentPageIndex)
        internal set

    fun clampPageIndex(pageCount: Int) {
        currentPageIndex = currentPageIndex.coerceIn(0, max(0, pageCount - 1))
    }
}

@Composable
fun rememberRadialMenuState(currentPageIndex: Int = 0): RadialMenuState {
    return remember { RadialMenuState(currentPageIndex) }
}