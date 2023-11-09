package com.odea.compose_scrollbars

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlin.math.roundToInt

@Composable
fun ListScrollbar(
    listState: LazyListState,
    thumb: @Composable (Modifier) -> Unit,
    track: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    val orientation = remember { derivedStateOf { listState.layoutInfo.orientation } }
    val numItems = remember { derivedStateOf { listState.layoutInfo.totalItemsCount } }
    val currentItem = remember { derivedStateOf {listState.firstVisibleItemIndex } }

    Scrollbar(
        currentItem = currentItem.value,
        numItems = numItems.value,
        scrollTo = { listState.animateScrollToItem(it) },
        orientation = orientation.value,
        thumb = thumb,
        track = track,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerScrollbar(
    pagerState: PagerState,
    orientation: Orientation,
    thumb: @Composable (Modifier) -> Unit,
    track: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scrollbar(
        currentItem = pagerState.targetPage,
        numItems = pagerState.pageCount,
        scrollTo = { pagerState.animateScrollToPage(it) },
        orientation = orientation,
        thumb = thumb,
        track = track,
        modifier = modifier,
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Composable
fun Scrollbar(
    currentItem: Int,
    numItems: Int,
    scrollTo: suspend (Int) -> Unit,
    orientation: Orientation,
    thumb: @Composable (Modifier) -> Unit,
    track: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val maxWidthInPixels = with(density) { maxWidth.roundToPx() }
        val stepWidth = maxWidthInPixels / numItems

        var dragPosition by remember {
            mutableFloatStateOf((currentItem * stepWidth).toFloat())
        }

        LaunchedEffect(Unit) {
            snapshotFlow { dragPosition }
                .mapLatest {
                    ((dragPosition / stepWidth).roundToInt())
                        // The Pager code also does this `coerceInPage`, but I had to add it here
                        //  to to make sure the scrollbar doesn't bounce
                        .coerceIn(0 until numItems)
                }
                .debounce(10)
                .collectLatest {
                    scrollTo(it)
                }
        }

        track(
            Modifier
                .minimumInteractiveComponentSize()
                .draggable(
                    state = rememberDraggableState { delta ->
                        dragPosition += delta
                    },
                    orientation = orientation,
                    startDragImmediately = true,
                    onDragStarted = {
                        dragPosition = if (orientation == Orientation.Horizontal) it.x else it.y
                    },
                    onDragStopped = {
                    },
                )
        )

        val thumbWidth = maxWidth / numItems / 2

        thumb(
            Modifier
                .padding(start = (maxWidth - (thumbWidth / 2)) / numItems * currentItem)
                .width(thumbWidth)
                .minimumInteractiveComponentSize(),
        )
    }
}