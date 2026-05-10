package com.example.mycalc

import com.example.mycalc.cache.CacheStore
import com.example.mycalc.cache.HistoryRepository
import com.example.mycalc.model.CalculationResult
import com.example.mycalc.model.Compounding
import com.example.mycalc.model.GraphPoint
import com.example.mycalc.model.HistoryEntry
import com.example.mycalc.model.InputParams
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistoryRepositoryIntegrationTest {
    @Test
    fun savesAndLoadsHistory() {
        val store = TestCacheStore()
        val repo = HistoryRepository(store)
        val entry = HistoryEntry(
            timestampMillis = 1,
            params = InputParams(1000.0, 5.0, 2, Compounding.Yearly),
            result = CalculationResult(1100.0, 100.0, listOf(GraphPoint(0, 1000.0)))
        )
        repo.saveHistory(listOf(entry))
        val loaded = repo.loadHistory()
        assertEquals(1, loaded.size)
        assertEquals(1100.0, loaded.first().result.finalAmount)
    }

    @Test
    fun trimsHistoryToLimit() {
        val store = TestCacheStore()
        val repo = HistoryRepository(store)
        val entries = (1..25).map { index ->
            HistoryEntry(
                timestampMillis = index.toLong(),
                params = InputParams(100.0, 1.0, 1, Compounding.Yearly),
                result = CalculationResult(101.0, 1.0, listOf(GraphPoint(0, 100.0)))
            )
        }
        repo.saveHistory(entries)
        val loaded = repo.loadHistory()
        assertTrue(loaded.size <= 20)
    }

    @Test
    fun clearsHistory() {
        val store = TestCacheStore()
        val repo = HistoryRepository(store)
        repo.saveHistory(emptyList())
        repo.clearHistory()
        assertTrue(repo.loadHistory().isEmpty())
    }
}

private class TestCacheStore : CacheStore {
    private val data = mutableMapOf<String, String>()

    override fun read(key: String): String? = data[key]

    override fun write(key: String, value: String) {
        data[key] = value
    }

    override fun remove(key: String) {
        data.remove(key)
    }
}
