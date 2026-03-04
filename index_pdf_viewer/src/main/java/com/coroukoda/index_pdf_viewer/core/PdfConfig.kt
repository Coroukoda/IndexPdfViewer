package com.coroukoda.index_pdf_viewer.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PdfConfig(
    val page: PageConfig = PageConfig(),
    val thumbnail: ThumbnailConfig = ThumbnailConfig()
)