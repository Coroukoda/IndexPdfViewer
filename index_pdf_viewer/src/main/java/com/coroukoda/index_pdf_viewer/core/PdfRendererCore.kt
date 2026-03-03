package com.coroukoda.index_pdf_viewer.core

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import java.io.File

internal class PdfRendererCore(file: File) {

    private val fileDescriptor =
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

    private val renderer = PdfRenderer(fileDescriptor)

    // FIX #7: Single mutex to prevent concurrent page access
    private val lock = Any()

    val pageCount: Int
        get() = renderer.pageCount

    fun renderPage(index: Int, targetWidth: Int = 0): Bitmap {
        synchronized(lock) {
            val page = renderer.openPage(index)

            // FIX: Scale bitmap to target width for memory efficiency
            val scale = if (targetWidth > 0) targetWidth.toFloat() / page.width else 1f
            val bitmapWidth = if (targetWidth > 0) targetWidth else page.width
            val bitmapHeight = (page.height * scale).toInt()

            val bitmap = createBitmap(bitmapWidth, bitmapHeight)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            return bitmap
        }
    }

    fun getPageInfo(index: Int): PdfPage {
        synchronized(lock) {
            val page = renderer.openPage(index)
            val info = PdfPage(index, page.width, page.height)
            page.close()
            return info
        }
    }

    fun close() {
        synchronized(lock) {
            renderer.close()
            fileDescriptor.close()
        }
    }
}