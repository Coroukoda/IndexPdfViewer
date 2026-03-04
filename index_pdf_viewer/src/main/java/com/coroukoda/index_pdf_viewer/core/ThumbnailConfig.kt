package com.coroukoda.index_pdf_viewer.core

import androidx.compose.ui.graphics.Color

data class ThumbnailConfig (
    val thumbnailBackgroundColor: Color = Color.LightGray.copy(alpha = 0.3f),
    val thumbnailItemColor: Color = Color.White,
    val thumbnailSelectedBorderColor: Color = Color.Unspecified,
    // Toggle button colors
    val toggleButtonContainerColor: Color = Color.Unspecified,
    val toggleButtonContentColor: Color = Color.Unspecified
)