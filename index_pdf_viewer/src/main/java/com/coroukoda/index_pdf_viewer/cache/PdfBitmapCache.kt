package com.coroukoda.index_pdf_viewer.cache

import android.graphics.Bitmap
import android.util.LruCache

internal class PdfBitmapCache {

    // FIX #6: Size cache by memory (bytes), not entry count.
    // Uses 1/4 of available heap, capped at 48MB.
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = minOf(maxMemory / 4, 48 * 1024) // KB

    private val cache = object : LruCache<Int, Bitmap>(cacheSize) {
        override fun sizeOf(key: Int, value: Bitmap): Int {
            return value.byteCount / 1024 // Return size in KB
        }
    }

    fun get(page: Int): Bitmap? = cache.get(page)

    fun put(page: Int, bitmap: Bitmap) {
        cache.put(page, bitmap)
    }

    fun clear() = cache.evictAll()
}