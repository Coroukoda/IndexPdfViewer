package com.coroukoda.index_pdf_viewer.viewer

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import com.coroukoda.index_pdf_viewer.cache.PdfBitmapCache
import com.coroukoda.index_pdf_viewer.core.PdfConfig
import com.coroukoda.index_pdf_viewer.core.PdfRendererCore
import com.coroukoda.index_pdf_viewer.gesture.zoomable
import com.coroukoda.index_pdf_viewer.thumbnail.PdfThumbnailSidebar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfViewer(
    file: File,
    modifier: Modifier = Modifier,
    config: PdfConfig = PdfConfig(),
    state: PdfViewerState = rememberPdfViewerState()
) {
    val renderer = remember { PdfRendererCore(file) }
    val cache = remember { PdfBitmapCache() }

    DisposableEffect(Unit) {
        onDispose {
            renderer.close()
            cache.clear()
        }
    }

    // FIX #2: Scroll to page when thumbnail is tapped
    val scope = rememberCoroutineScope()
    val onThumbnailClick: (Int) -> Unit = { page ->
        scope.launch { state.listState.animateScrollToItem(page) }
    }

    // Scale bitmaps to screen width to avoid oversized allocations
    val screenWidthPx = LocalConfiguration.current.screenWidthDp
        .let { it * (LocalConfiguration.current.densityDpi / 160f) }
        .toInt()

    Row(modifier = modifier.fillMaxSize()) {

        // Always render the sidebar — it manages its own visibility via the toggle button
        PdfThumbnailSidebar(
            pageCount = renderer.pageCount,
            selectedPage = state.currentPage,
            onPageClick = onThumbnailClick
        )

        if (config.horizontalMode) {
            LazyRow(state = state.listState, modifier = Modifier.weight(1f).zoomable(config.enableZoom)) {
                items((0 until renderer.pageCount).toList()) { index ->
                    AsyncPdfPage(index, cache, renderer, screenWidthPx, config.pageSpacing)
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f).clipToBounds()) {
                LazyColumn(state = state.listState, modifier = Modifier.fillMaxSize().zoomable(config.enableZoom)) {
                    items((0 until renderer.pageCount).toList()) { index ->
                        AsyncPdfPage(index, cache, renderer, screenWidthPx, config.pageSpacing)
                    }
                }
            }
        }
    }
}

@Composable
private fun AsyncPdfPage(
    index: Int,
    cache: PdfBitmapCache,
    renderer: PdfRendererCore,
    targetWidth: Int,
    pageSpacing: androidx.compose.ui.unit.Dp
) {
    var bitmap by remember(index) { mutableStateOf<Bitmap?>(cache.get(index)) }

    LaunchedEffect(index) {
        if (bitmap == null) {
            val rendered = withContext(Dispatchers.IO) {
                renderer.renderPage(index, targetWidth)
            }
            cache.put(index, rendered)
            bitmap = rendered
        }
    }

    bitmap?.let {
        PdfPageItem(bitmap = it, pageSpacing = pageSpacing)
    } ?: PdfPagePlaceholder(pageSpacing = pageSpacing)
}