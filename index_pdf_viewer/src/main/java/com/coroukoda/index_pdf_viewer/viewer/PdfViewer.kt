package com.coroukoda.index_pdf_viewer.viewer


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.coroukoda.index_pdf_viewer.core.PdfConfig
import com.coroukoda.index_pdf_viewer.gesture.zoomable
import com.coroukoda.index_pdf_viewer.thumbnail.PdfThumbnailSidebar
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PdfViewer(
    file: File,
    modifier: Modifier = Modifier,
    config: PdfConfig = PdfConfig(),
    state: PdfViewerState = rememberPdfViewerState(),

    ) {
    val screenWidthPx = with(LocalConfiguration.current) {
        (screenWidthDp * (densityDpi / 160f)).toInt()
    }

    // controller is remembered and cleaned up with DisposableEffect
    val controller = remember(file) {
        PdfRenderController(file = file, targetWidth = screenWidthPx)
    }

    DisposableEffect(controller) {
        onDispose { controller.close() }
    }

    val scope = rememberCoroutineScope()
    val onThumbnailClick: (Int) -> Unit = { page ->
        scope.launch { state.listState.animateScrollToItem(page) }
    }

    Row(modifier = modifier.fillMaxSize().background(config.page.backgroundColor)) {


        PdfThumbnailSidebar(
            pageCount = controller.pageCount,
            selectedPage = state.currentPage,
            onPageClick = onThumbnailClick,
            color = config.thumbnail.thumbnailBackgroundColor,
            borderSelectionColor = config.thumbnail.thumbnailSelectedBorderColor,
            itemColor = config.thumbnail.thumbnailItemColor,

            )


        PdfContent(
            modifier = Modifier.weight(1f),
            controller = controller,
            config = config,
            state = state,
            color = config.page.backgroundColor
        )

    }
}

@Composable
private fun PdfContent(
    modifier: Modifier,
    controller: PdfRenderController,
    config: PdfConfig,
    state: PdfViewerState,
    color: Color
) {
    val pageIndices = remember(controller.pageCount) {
        (0 until controller.pageCount).toList()
    }

    if (config.page.horizontalMode) {
        LazyRow(
            state = state.listState,
            modifier = modifier
                .zoomable(config.page.enableZoom)
                .background(color.copy(alpha = 0.3f))
        ) {
            items(pageIndices, key = { it }) { index ->
                PdfPageRenderer(
                    index = index,
                    controller = controller,
                    pageSpacing = config.page.pageSpacing
                )
            }
        }
    } else {
        Box(modifier = modifier.clipToBounds()) {
            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(config.page.enableZoom)
                    .background(color.copy(alpha = 0.3f))
            ) {
                items(pageIndices, key = { it }) { index ->
                    PdfPageRenderer(
                        index = index,
                        controller = controller,
                        pageSpacing = config.page.pageSpacing
                    )
                }
            }
        }
    }
}

/**
 * Collects the page [StateFlow] and renders it.
 * Shows a placeholder until the bitmap is ready.
 */
@Composable
private fun PdfPageRenderer(
    index: Int,
    controller: PdfRenderController,
    pageSpacing: androidx.compose.ui.unit.Dp
) {
    // collectAsState() re-renders only this item when its bitmap arrives
    val bitmap by controller.getPageFlow(index).collectAsState()

    if (bitmap != null) {
        PdfPageItem(
            bitmap = bitmap!!,
            pageSpacing = pageSpacing
        )
    } else {
        PdfPagePlaceholder(pageSpacing = pageSpacing)
    }
}