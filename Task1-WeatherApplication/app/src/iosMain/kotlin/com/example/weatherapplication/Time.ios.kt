package com.example.weatherapplication

import platform.posix.time

actual fun currentTimeMillis(): Long = time(null).toLong() * 1000L
