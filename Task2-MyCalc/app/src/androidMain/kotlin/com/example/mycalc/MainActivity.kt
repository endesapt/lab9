package com.example.mycalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mycalc.cache.createCacheStore
import com.example.mycalc.ui.CalculatorApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deps = AppDependencies(
            cacheStore = createCacheStore(PlatformContext(this)),
            platform = PlatformKind.Android
        )

        setContent {
            CalculatorApp(deps)
        }
    }
}
