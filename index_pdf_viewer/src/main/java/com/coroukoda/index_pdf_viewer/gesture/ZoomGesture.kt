package com.coroukoda.index_pdf_viewer.gesture

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

internal fun Modifier.zoomable(enabled: Boolean): Modifier = composed {
    if (!enabled) return@composed Modifier

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    this
        .onSizeChanged { size = it }
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                // FIX #4: Compute newScale first, then use it for bounds — not stale `scale`
                val newScale = (scale * zoom).coerceIn(1f, 5f)

                if (newScale > 1f) {
                    val maxX = (size.width * (newScale - 1)) / 2
                    val maxY = (size.height * (newScale - 1)) / 2

                    offset = Offset(
                        x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                        y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                    )
                } else {
                    offset = Offset.Zero
                }

                scale = newScale
            }
        }
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationX = offset.x
            translationY = offset.y
            clip = false
        }
}