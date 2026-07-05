package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.*

@Composable
fun rememberScrollFabProgress(
    scrollState: ScrollState?,
    threshold: Int = 8,
    reverse: Boolean = false
): Float {

    var lastScroll by remember(scrollState) {
        mutableIntStateOf(scrollState?.value ?: 0)
    }

    var showFab by remember(scrollState) {
        mutableStateOf(true)
    }

    LaunchedEffect(scrollState) {
        if (scrollState == null) return@LaunchedEffect

        var initialized = false

        snapshotFlow { scrollState.value }
            .collect { value ->

                if (!initialized) {
                    initialized = true
                    lastScroll = value
                    showFab = true
                    return@collect
                }

                val delta = value - lastScroll

                if (kotlin.math.abs(delta) < threshold) return@collect

                val goingDown = delta > 0

                showFab = if (reverse) {
                    goingDown
                } else {
                    !goingDown
                }

                lastScroll = value
            }
    }

    return animateFloatAsState(
        targetValue = if (showFab) 1f else 0f,
        label = "fab"
    ).value
}

@Composable
fun rememberScrollFabProgress(
    state: LazyGridState?,
    threshold: Int = 8,
    reverse: Boolean = false
): Float {

    var lastScroll by remember(state) {
        mutableIntStateOf(
            state?.firstVisibleItemIndex?.times(10000)
                ?.plus(state.firstVisibleItemScrollOffset)
                ?: 0
        )
    }

    var showFab by remember(state) {
        mutableStateOf(true)
    }

    LaunchedEffect(state) {
        if (state == null) return@LaunchedEffect

        var initialized = false

        snapshotFlow {
            state.firstVisibleItemIndex * 10000 +
                    state.firstVisibleItemScrollOffset
        }.collect { value ->

            if (!initialized) {
                initialized = true
                lastScroll = value
                showFab = true
                return@collect
            }

            val delta = value - lastScroll

            if (kotlin.math.abs(delta) < threshold) return@collect

            val goingDown = delta > 0

            showFab = if (reverse) {
                goingDown
            } else {
                !goingDown
            }

            lastScroll = value
        }
    }

    return animateFloatAsState(
        targetValue = if (showFab) 1f else 0f,
        label = "fab"
    ).value
}

@Composable
fun rememberScrollFabProgress(
    state: LazyListState?,
    threshold: Int = 8,
    reverse: Boolean = false
): Float {

    var lastScroll by remember(state) {
        mutableIntStateOf(
            state?.firstVisibleItemIndex?.times(10000)
                ?.plus(state.firstVisibleItemScrollOffset)
                ?: 0
        )
    }

    var showFab by remember(state) {
        mutableStateOf(true)
    }

    LaunchedEffect(state) {
        if (state == null) return@LaunchedEffect

        var initialized = false

        snapshotFlow {
            state.firstVisibleItemIndex * 10000 +
                    state.firstVisibleItemScrollOffset
        }.collect { value ->

            if (!initialized) {
                initialized = true
                lastScroll = value
                showFab = true
                return@collect
            }

            val delta = value - lastScroll

            if (kotlin.math.abs(delta) < threshold) return@collect

            val goingDown = delta > 0

            showFab = if (reverse) {
                goingDown
            } else {
                !goingDown
            }

            lastScroll = value
        }
    }

    return animateFloatAsState(
        targetValue = if (showFab) 1f else 0f,
        label = "fab"
    ).value
}


@Composable
fun rememberScrollFabProgress(
    adapter: ScrollbarAdapter?,
    threshold: Int = 8,
    reverse: Boolean = false
): Float {

    var lastScroll by remember(adapter) {
        mutableDoubleStateOf(0.0)
    }

    var showFab by remember(adapter) {
        mutableStateOf(true)
    }

    LaunchedEffect(adapter) {
        if (adapter == null) return@LaunchedEffect

        var initialized = false

        snapshotFlow { adapter.scrollOffset }
            .collect { value ->

                if (!initialized) {
                    initialized = true
                    lastScroll = value
                    showFab = true
                    return@collect
                }

                val delta = value - lastScroll

                if (kotlin.math.abs(delta) < threshold) return@collect

                val goingDown = delta > 0

                showFab = if (reverse) {
                    goingDown
                } else {
                    !goingDown
                }

                lastScroll = value
            }
    }

    return animateFloatAsState(
        targetValue = if (showFab) 1f else 0f,
        label = "fab"
    ).value
}