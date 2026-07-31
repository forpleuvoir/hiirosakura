package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun <T, K : Any> ReorderableEditorList(
    items: List<T>,
    key: (item: T) -> K,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    listVerticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    scrollbarPadding: Dp = 12.dp,
    listContentPadding: PaddingValues = PaddingValues(0.dp),
    emptyContent: @Composable BoxScope.() -> Unit = {
        Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
    },
    floatingActionButton: (@Composable BoxScope.(lazyListState: LazyListState) -> Unit)? = null,
    itemContent: @Composable ReorderableCollectionItemScope.(index: Int, item: T, isDragging: Boolean, hapticFeedback: HapticFeedback) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val lazyListState = rememberLazyListState()
        val hapticFeedback = LocalHapticFeedback.current

        val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
            onMove(from.index, to.index)
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }

        if (items.isEmpty()) {
            emptyContent()
        } else {
            LazyColumn(
                state = lazyListState,
                verticalArrangement = listVerticalArrangement,
                contentPadding = listContentPadding,
                modifier = Modifier
                    .padding(end = if (lazyListState.canScroll) scrollbarPadding else 0.dp)
                    .fillMaxSize(),
            ) {
                itemsIndexed(
                    items = items,
                    key = { _, item -> key(item) },
                ) { index, item ->
                    ReorderableItem(
                        state = reorderableState,
                        key = key(item),
                    ) { isDragging ->
                        itemContent(
                            index,
                            item,
                            isDragging,
                            hapticFeedback
                        )
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }

        floatingActionButton?.invoke(this, lazyListState)
    }
}

@Composable
fun <T, K : Any> ReorderableEditorVerticalGrid(
    items: List<T>,
    key: (item: T) -> K,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    columns: GridCells,
    modifier: Modifier = Modifier,
    gridVerticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    gridHorizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    scrollbarPadding: Dp = 12.dp,
    gridContentPadding: PaddingValues = PaddingValues(8.dp),
    emptyContent: @Composable BoxScope.() -> Unit = {
        Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
    },
    floatingActionButton: (@Composable BoxScope.(lazyGridState: LazyGridState) -> Unit)? = null,
    itemContent: @Composable ReorderableCollectionItemScope.(index: Int, item: T, isDragging: Boolean, hapticFeedback: HapticFeedback) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val lazyGridState = rememberLazyGridState()
        val hapticFeedback = LocalHapticFeedback.current

        val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
            onMove(from.index, to.index)
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }

        if (items.isEmpty()) {
            emptyContent()
        } else {
            LazyVerticalGrid(
                columns = columns,
                state = lazyGridState,
                verticalArrangement = gridVerticalArrangement,
                horizontalArrangement = gridHorizontalArrangement,
                contentPadding = gridContentPadding,
                modifier = Modifier
                    .padding(end = if (lazyGridState.canScroll) scrollbarPadding else 0.dp)
                    .fillMaxSize(),
            ) {
                itemsIndexed(
                    items = items,
                    key = { _, item -> key(item) },
                ) { index, item ->
                    ReorderableItem(
                        state = reorderableState,
                        key = key(item),
                    ) { isDragging ->
                        itemContent(
                            index,
                            item,
                            isDragging,
                            hapticFeedback
                        )
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(lazyGridState),
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }

        floatingActionButton?.invoke(this, lazyGridState)
    }
}