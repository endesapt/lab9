package com.example.mycalc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.mycalc.cache.createCacheStore
import com.example.mycalc.localization.currentStrings
import com.example.mycalc.ui.CalculatorApp

fun main() = application {
    val deps = AppDependencies(
        cacheStore = createCacheStore(),
        platform = PlatformKind.Desktop
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = currentStrings().appName
    ) {
        CalculatorApp(deps)
    }
}
