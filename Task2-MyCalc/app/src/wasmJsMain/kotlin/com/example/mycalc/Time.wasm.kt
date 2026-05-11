package com.example.mycalc

import kotlinx.browser.window

actual fun currentTimeMillis(): Long =
    window.performance.now().toLong()
