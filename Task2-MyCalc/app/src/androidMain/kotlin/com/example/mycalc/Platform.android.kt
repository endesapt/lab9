package com.example.mycalc

import android.content.Context
import java.util.Locale

actual typealias PlatformContext = Context

actual fun currentPlatform(): PlatformKind = PlatformKind.Android

actual fun currentLanguage(): String = Locale.getDefault().language
