package com.example.mycalc

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.mycalc.model.CalculationResult
import com.example.mycalc.model.Compounding
import com.example.mycalc.model.GraphPoint
import com.example.mycalc.model.InputParams
import com.example.mycalc.ui.CalculatorScreen
import com.example.mycalc.ui.CalculatorUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class CalculatorScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsInputsAndButton() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(),
                platform = PlatformKind.Android,
                onPrincipalChange = {},
                onRateChange = {},
                onYearsChange = {},
                onCompoundingChange = {},
                onCalculate = {},
                onClearHistory = {},
                onChartScaleChange = {}
            )
        }

        composeTestRule.onNodeWithTag("principal_input").assertExists()
        composeTestRule.onNodeWithTag("rate_input").assertExists()
        composeTestRule.onNodeWithTag("calculate_button").assertExists()
    }

    @Test
    fun showsYearsSliderOnAndroid() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(),
                platform = PlatformKind.Android,
                onPrincipalChange = {},
                onRateChange = {},
                onYearsChange = {},
                onCompoundingChange = {},
                onCalculate = {},
                onClearHistory = {},
                onChartScaleChange = {}
            )
        }

        composeTestRule.onNodeWithTag("years_slider").assertExists()
    }

    @Test
    fun showsChartAndResult() {
        val params = InputParams(1000.0, 5.0, 2, Compounding.Yearly)
        val result = CalculationResult(1100.0, 100.0, listOf(GraphPoint(0, 1000.0), GraphPoint(1, 1100.0)))

        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(result = result),
                platform = PlatformKind.Android,
                onPrincipalChange = {},
                onRateChange = {},
                onYearsChange = {},
                onCompoundingChange = {},
                onCalculate = {},
                onClearHistory = {},
                onChartScaleChange = {}
            )
        }

        composeTestRule.onNodeWithTag("chart").assertExists()
        composeTestRule.onNodeWithText("1100.00").assertExists()
    }
}
