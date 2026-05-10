package com.example.mycalc

import kotlinx.browser.window

actual class PlatformContext

actual fun currentPlatform(): PlatformKind = PlatformKind.Web

actual fun currentLanguage(): String = window.navigator.language
