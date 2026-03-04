package com.coroukoda.index_pdf_viewer.viewer

import android.graphics.Bitmap
import com.coroukoda.index_pdf_viewer.cache.PdfBitmapCache
import com.coroukoda.index_pdf_viewer.core.PdfRendererCore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Manages PDF rendering lifecycle using coroutines.
 * - Renders pages lazily on [Dispatchers.IO]
 * - Caches results in [PdfBitmapCache]
 * - Exposes per-page state via [StateFlow]
 * - Must call [close] when done to cancel scope and free resources
 */
internal class PdfRenderController(
    file: File,
    private val targetWidth: Int
) {
    private val renderer = PdfRendererCore(file)
    private val cache = PdfBitmapCache()

    // SupervisorJob: one failing page render won't cancel all others
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    val pageCount: Int get() = renderer.pageCount

    // Per-page state: null = not loaded yet, Bitmap = rendered
    private val _pages = mutableMapOf<Int, MutableStateFlow<Bitmap?>>()

    fun getPageFlow(index: Int): StateFlow<Bitmap?> {
        return _pages.getOrPut(index) {
            MutableStateFlow<Bitmap?>(cache.get(index)).also { flow ->
                if (flow.value == null) renderPage(index, flow)
            }
        }.asStateFlow()
    }

    private fun renderPage(index: Int, flow: MutableStateFlow<Bitmap?>) {
        scope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                // Double-check cache before rendering (another coroutine may have beaten us)
                cache.get(index) ?: renderer.renderPage(index, targetWidth).also {
                    cache.put(index, it)
                }
            }
            flow.value = bitmap
        }
    }

    fun close() {
        scope.cancel()
        renderer.close()
        cache.clear()
    }
}