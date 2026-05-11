package com.example.weatherapplication

import platform.Foundation.NSLocale

actual class PlatformContext

actual fun currentPlatform(): PlatformKind = PlatformKind.IOS

actual fun currentLanguage(): String =
    NSLocale.preferredLanguages.firstOrNull()?.toString()?.substringBefore('-') ?: "en"
