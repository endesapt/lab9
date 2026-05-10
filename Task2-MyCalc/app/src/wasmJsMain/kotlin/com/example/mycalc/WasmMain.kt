package com.example.mycalc

import androidx.compose.ui.window.CanvasBasedWindow
import com.example.mycalc.cache.createCacheStore
import com.example.mycalc.ui.CalculatorApp

fun main() {
    val deps = AppDependencies(
        cacheStore = createCacheStore(),
        platform = PlatformKind.Web
    )

    CanvasBasedWindow("Calculator") {
        CalculatorApp(deps)
    }
}
