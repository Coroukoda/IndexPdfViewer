package com.coroukoda.indexpdf

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coroukoda.index_pdf_viewer.core.PdfConfig
import com.coroukoda.index_pdf_viewer.viewer.PdfViewer
import com.coroukoda.index_pdf_viewer.viewer.rememberPdfViewerState
import com.coroukoda.indexpdf.ui.theme.IndexPdfTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndexPdfTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        PdfScreen()
                    }
                }
            }
        }
    }
}


fun Context.copyPdfFromAssets(fileName: String): File {
    val file = File(cacheDir, fileName)

    if (!file.exists()) {
        assets.open(fileName).use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    return file
}
@Composable
fun PdfScreen() {

    val context = LocalContext.current
    val file = remember {
        context.copyPdfFromAssets("understanding_animation_as_state_in_android.pdf")
    }

    PdfViewer(
        file = file,
        config = PdfConfig(
            enableZoom = true,        // Enable pinch-to-zoom
            horizontalMode = false,   // true = horizontal scroll, false = vertical
            pageSpacing = 12.dp,
        )
    )
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IndexPdfTheme {
        PdfScreen()
    }
}