package com.coroukoda.index_pdf_viewer.core

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PdfConfig(
    val enableZoom: Boolean = true,
    val horizontalMode: Boolean = false,
    val pageSpacing: Dp = 12.dp,
)