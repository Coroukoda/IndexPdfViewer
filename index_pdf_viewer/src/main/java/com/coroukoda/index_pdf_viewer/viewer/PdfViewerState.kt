package com.coroukoda.index_pdf_viewer.viewer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*

class PdfViewerState internal constructor(
    val listState: LazyListState
) {
    val currentPage: Int
        get() {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return listState.firstVisibleItemIndex

            // Find the item that occupies the most screen space
            val mostVisible = visibleItems.maxByOrNull { item ->
                val itemTop = maxOf(item.offset, 0)
                val itemBottom = minOf(
                    item.offset + item.size,
                    layoutInfo.viewportEndOffset
                )
                itemBottom - itemTop // visible height of this item
            }

            return mostVisible?.index ?: listState.firstVisibleItemIndex
        }
}
@Composable
fun rememberPdfViewerState(): PdfViewerState {
    val listState = rememberLazyListState()
    return remember(listState) { PdfViewerState(listState) }
}