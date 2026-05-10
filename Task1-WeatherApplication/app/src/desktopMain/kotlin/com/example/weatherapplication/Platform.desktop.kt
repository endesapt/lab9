package com.example.weatherapplication

import java.util.Locale

actual class PlatformContext

actual fun currentPlatform(): PlatformKind = PlatformKind.Desktop

actual fun currentLanguage(): String = Locale.getDefault().language
