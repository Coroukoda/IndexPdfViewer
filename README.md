[![](https://jitpack.io/v/Coroukoda/IndexPdfViewer.svg)](https://jitpack.io/#Coroukoda/IndexPdfViewer)

# IndexPdfViewer
# 📄 index-pdf-viewer

A lightweight, customizable PDF viewer library for **Android Jetpack Compose**.  
Supports zooming, horizontal/vertical scrolling, thumbnail sidebar, async rendering, and memory-safe bitmap caching.

---

## ✨ Features

- 📜 Vertical & horizontal page scrolling
- 🔍 Pinch-to-zoom with pan support
- 🖼️ Thumbnail sidebar for quick navigation
- ⚡ Async page rendering (off the main thread)
- 🧠 Memory-safe LRU bitmap cache
- 🔒 Thread-safe PDF renderer core
- 🎨 Fully Compose-native — no WebView, no legacy Views

---

## 📦 Installation

Add the dependency to your `build.gradle.kts` (app module):

```kotlin
dependencies {
    implementation("com.coroukoda:index-pdf-viewer:<version>")
}
```

Make sure `mavenCentral()` is in your repository list:

```kotlin
repositories {
    mavenCentral()
}
```

---

## 🚀 Quick Start

```kotlin
import com.coroukoda.index_pdf_viewer.viewer.PdfViewer
import com.coroukoda.index_pdf_viewer.core.PdfConfig
import java.io.File

@Composable
fun MyScreen() {
    val file = File(context.cacheDir, "document.pdf")

    PdfViewer(
        file = file,
        modifier = Modifier.fillMaxSize()
    )
}
```

---

## ⚙️ Configuration

Customize the viewer using `PdfConfig`:

```kotlin
PdfViewer(
    file = file,
    config = PdfConfig(
        enableZoom = true,        // Enable pinch-to-zoom
        horizontalMode = false,   // true = horizontal scroll, false = vertical
        pageSpacing = 12.dp,      // Space between pages
    )
)
```

### `PdfConfig` Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `enableZoom` | `Boolean` | `true` | Enables pinch-to-zoom and pan |
| `horizontalMode` | `Boolean` | `false` | Switches scroll direction to horizontal |
| `pageSpacing` | `Dp` | `12.dp` | Vertical/horizontal padding between pages |

---

## 🏗️ Project Structure

```
index_pdf_viewer/
├── core/
│   ├── PdfRendererCore.kt     # Thread-safe PdfRenderer wrapper
│   ├── PdfConfig.kt           # Configuration data class
│   └── PdfPage.kt             # Page metadata model
├── viewer/
│   ├── PdfViewer.kt           # Main composable entry point
│   ├── PdfViewerState.kt      # Scroll state + current page tracking
│   └── PdfPageItem.kt         # Single page + loading placeholder composable
├── gesture/
│   └── zoomable.kt            # Pinch-to-zoom Modifier
├── thumbnail/
│   └── PdfThumbnailSidebar.kt # Thumbnail navigation sidebar
└── cache/
    └── PdfBitmapCache.kt      # Memory-safe LRU bitmap cache
```

---

## 🧵 Threading Model

PDF rendering is performed on `Dispatchers.IO` via `LaunchedEffect`, keeping the main thread free.  
`PdfRendererCore` is protected with a `synchronized` lock — safe to use with concurrent coroutines.

Pages are rendered lazily as they scroll into view and cached in an `LruCache` sized to **25% of available heap** (capped at 48 MB), preventing out-of-memory errors on large documents.

---

## 📋 Requirements

| Requirement | Minimum |
|-------------|---------|
| Android SDK | 21+ |
| Jetpack Compose | 1.5+ |
| Kotlin | 1.9+ |
| `androidx.core` | 1.10+ |

---

## 🔒 Permissions

No special permissions are required if the PDF file is already accessible from your app's storage (e.g., `cacheDir`, `filesDir`, or a URI resolved via `ContentResolver`).

For files on external storage, declare:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

---

## 🐛 Known Limitations

- Thumbnail sidebar currently shows page numbers only — bitmap thumbnails are planned for a future release.
- Text selection and hyperlinks are not supported (render-only viewer).
- Encrypted/password-protected PDFs are not supported.

---

## 📄 License

```
MIT License

Copyright (c) 2026 coroukoda

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```
