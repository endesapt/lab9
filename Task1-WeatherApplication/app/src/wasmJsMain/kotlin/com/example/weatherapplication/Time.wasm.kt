package com.example.weatherapplication

import kotlinx.browser.window

actual fun currentTimeMillis(): Long =
    window.performance.now().toLong()
