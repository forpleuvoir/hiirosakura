package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import sh.calvin.reorderable.rememberReorderableLazyListState
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.hiirosakura.ui.util.rememberAdaptiveGridSpan
import androidx.compose.ui.platform.LocalDensity

@Composable
fun <T, K : Any> ReorderableEditorList(
    items: List<T>,
    key: (item: T) -> K,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    listVerticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    listContentPadding: PaddingValues = PaddingValues(0.dp),
    emptyContent: @Composable BoxScope.() -> Unit = {
        Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
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
            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = listVerticalArrangement,
                    contentPadding = listContentPadding,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                ) {
                    itemsIndexed(
                        items = items,
                        key = { _, item -> key(item) },
                    ) { index, item ->
                        ReorderableItem(
                            state = reorderableState,
                            key = key(item),
                            animateItemModifier = hsItemAnimation(),
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

                Spacer(Modifier.width(8.dp))

                VerticalFlatScroller(
                    adapter = rememberScrollerAdapter(lazyListState),
                    modifier = Modifier.fillMaxHeight(),
                )
            }
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
    gridContentPadding: PaddingValues = PaddingValues(8.dp),
    emptyContent: @Composable BoxScope.() -> Unit = {
        Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
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
            Row(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = columns,
                    state = lazyGridState,
                    verticalArrangement = gridVerticalArrangement,
                    horizontalArrangement = gridHorizontalArrangement,
                    contentPadding = gridContentPadding,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                ) {
                    itemsIndexed(
                        items = items,
                        key = { _, item -> key(item) },
                    ) { index, item ->
                        ReorderableItem(
                            state = reorderableState,
                            key = key(item),
                            animateItemModifier = hsItemAnimation(),
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

                Spacer(Modifier.width(8.dp))

                val density = LocalDensity.current
                val spanCount = when (val cells = columns) {
                    // CMP 把 GridCells.Fixed 的 count 设为 private，改由交叉轴尺寸列表推列数
                    is GridCells.Fixed -> with(cells) {
                        with(density) {
                            calculateCrossAxisCellSizes(lazyGridState.layoutInfo.viewportSize.width, 0).size
                        }
                    }

                    else               -> rememberAdaptiveGridSpan(lazyGridState)
                }
                VerticalFlatScroller(
                    adapter = rememberScrollerAdapter(lazyGridState, spanCount),
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }

        floatingActionButton?.invoke(this, lazyGridState)
    }
}