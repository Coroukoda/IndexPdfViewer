package com.coroukoda.index_pdf_viewer.thumbnail

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
internal fun PdfThumbnailSidebar(
    pageCount: Int,
    selectedPage: Int,
    onPageClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color =Color.LightGray.copy(alpha = 0.3f),
    borderSelectionColor: Color = MaterialTheme.colorScheme.primary,
    itemColor: Color = MaterialTheme.colorScheme.surface
) {
    // Local state: sidebar starts visible
    var isVisible by remember { mutableStateOf(true) }

    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ── Animated sidebar panel ──────────────────────────────────────────
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(
                        color =color ,
                        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    count = pageCount,
                    key = { index -> index },
                    contentType = { "thumbnail" }
                ) { index ->
                    val isSelected = selectedPage == index

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .aspectRatio(0.75f)
                            .selectable(
                                selected = isSelected,
                                onClick = { onPageClick(index) },
                                role = Role.Tab
                            ),
                        shape = RoundedCornerShape(8.dp),
                        tonalElevation = if (isSelected) 4.dp else 0.dp,
                        border = if (isSelected) {
                            BorderStroke(2.dp, borderSelectionColor)
                        } else null
                    ) {
                        Box(contentAlignment = Alignment.Center,modifier = Modifier.background(itemColor)) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        // ── Toggle button — always pinned to the sidebar edge ───────────────
        FilledIconButton(
            onClick = { isVisible = !isVisible },
            modifier = Modifier
                .width(20.dp)
                .height(48.dp),
            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = if (isVisible)
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft
                else
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (isVisible) "Hide sidebar" else "Show sidebar",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}