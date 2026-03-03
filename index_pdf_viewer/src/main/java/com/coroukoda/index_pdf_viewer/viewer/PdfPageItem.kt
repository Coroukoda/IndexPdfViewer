package com.coroukoda.index_pdf_viewer.viewer

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp

@Composable
internal fun PdfPageItem(bitmap: Bitmap, pageSpacing: Dp) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = pageSpacing),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
internal fun PdfPagePlaceholder(pageSpacing: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.77f) // A4 approximate ratio
            .padding(vertical = pageSpacing)
            .background(Color(0xFFE0E0E0))
    )
}