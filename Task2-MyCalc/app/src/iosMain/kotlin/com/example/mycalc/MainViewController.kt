package com.example.mycalc

import androidx.compose.ui.window.ComposeUIViewController
import com.example.mycalc.cache.createCacheStore
import com.example.mycalc.ui.CalculatorApp

fun MainViewController() = ComposeUIViewController {
    val deps = AppDependencies(
        cacheStore = createCacheStore(),
        platform = PlatformKind.IOS
    )
    CalculatorApp(deps)
}
