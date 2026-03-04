package com.coroukoda.index_pdf_viewer.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PageConfig(
    val enableZoom: Boolean = true,
    val horizontalMode: Boolean = false,
    val pageSpacing: Dp = 12.dp,
    val backgroundColor: Color = Color.White
)