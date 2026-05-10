package com.example.mycalc.cache

import com.example.mycalc.PlatformContext
import platform.Foundation.NSUserDefaults

actual fun createCacheStore(context: PlatformContext?): CacheStore {
    return IosCacheStore()
}

private class IosCacheStore : CacheStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun read(key: String): String? = defaults.stringForKey(key)

    override fun write(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    override fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }
}
