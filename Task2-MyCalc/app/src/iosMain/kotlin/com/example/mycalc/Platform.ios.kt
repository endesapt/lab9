package com.example.mycalc

import platform.Foundation.NSLocale

actual class PlatformContext

actual fun currentPlatform(): PlatformKind = PlatformKind.IOS

actual fun currentLanguage(): String = NSLocale.currentLocale.languageCode ?: "en"
