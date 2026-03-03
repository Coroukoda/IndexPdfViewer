package com.coroukoda.index_pdf_viewer.utils

import java.io.Closeable

internal fun Closeable.safeClose() {
    try {
        close()
    } catch (_: Exception) {}
}