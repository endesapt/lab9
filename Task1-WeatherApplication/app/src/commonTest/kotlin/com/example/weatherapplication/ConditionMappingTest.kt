package com.example.weatherapplication

import com.example.weatherapplication.data.WeatherCondition
import com.example.weatherapplication.data.conditionFromCode
import kotlin.test.Test
import kotlin.test.assertEquals

class ConditionMappingTest {
    @Test
    fun mapsClearCode() {
        assertEquals(WeatherCondition.Clear, conditionFromCode(0))
    }

    @Test
    fun mapsRainCode() {
        assertEquals(WeatherCondition.Rain, conditionFromCode(61))
    }

    @Test
    fun mapsThunderCode() {
        assertEquals(WeatherCondition.Thunder, conditionFromCode(95))
    }
}
