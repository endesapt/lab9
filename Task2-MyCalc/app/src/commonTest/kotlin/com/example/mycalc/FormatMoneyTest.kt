package com.example.mycalc

import com.example.mycalc.model.formatMoney
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatMoneyTest {
    @Test
    fun formatsTwoDecimals() {
        assertEquals("10.00", formatMoney(10.0))
        assertEquals("10.05", formatMoney(10.045))
    }
}
