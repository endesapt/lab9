package com.example.mycalc.cache

import com.example.mycalc.model.HistoryEntry
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val HISTORY_KEY = "history_entries"

class HistoryRepository(private val store: CacheStore) {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadHistory(): List<HistoryEntry> {
        val raw = store.read(HISTORY_KEY) ?: return emptyList()
        return runCatching { json.decodeFromString<List<HistoryEntry>>(raw) }
            .getOrElse { emptyList() }
    }

    fun saveHistory(entries: List<HistoryEntry>) {
        val trimmed = entries.takeLast(20)
        store.write(HISTORY_KEY, json.encodeToString(trimmed))
    }

    fun clearHistory() {
        store.remove(HISTORY_KEY)
    }
}
