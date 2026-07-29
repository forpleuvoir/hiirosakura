package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.foundation.gestures.ScrollableState

inline val ScrollableState.canScroll
    get() = canScrollBackward || canScrollForward
