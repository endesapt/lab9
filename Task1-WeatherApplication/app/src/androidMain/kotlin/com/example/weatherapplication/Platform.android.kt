package com.example.weatherapplication

import android.content.Context
import java.util.Locale

actual class PlatformContext(val androidContext: Context)

actual fun currentPlatform(): PlatformKind = PlatformKind.Android

actual fun currentLanguage(): String = Locale.getDefault().language
