package com.example.mycalc

enum class PlatformKind {
    Android,
    IOS,
    Desktop,
    Web
}

expect class PlatformContext

expect fun currentPlatform(): PlatformKind

expect fun currentLanguage(): String
