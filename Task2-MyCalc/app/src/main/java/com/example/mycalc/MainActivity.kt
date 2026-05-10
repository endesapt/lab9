package com.example.mycalc

import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity(), GestureOverlayView.OnGesturePerformedListener {

    private val calculatorState = CalculatorUiState()
    private lateinit var gestureLibrary: GestureLibrary
    private lateinit var gestureStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        gestureStatus = findViewById(R.id.gestureStatus)

        findViewById<ComposeView>(R.id.composeContainer).setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorConverterApp(calculatorState)
                }
            }
        }

        val gestureOverlay = findViewById<GestureOverlayView>(R.id.gestureOverlay)
        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures)
        if (gestureLibrary.load()) {
            gestureOverlay.addOnGesturePerformedListener(this)
            gestureStatus.text = getString(R.string.gesture_ready)
        } else {
            gestureStatus.text = getString(R.string.gesture_failed)
        }
    }

    override fun onGesturePerformed(overlay: GestureOverlayView?, gesture: Gesture?) {
        if (gesture == null) return
        val prediction = gestureLibrary.recognize(gesture).maxByOrNull { it.score }
        if (prediction == null || prediction.score <= 1.0) {
            gestureStatus.text = getString(R.string.gesture_unknown)
            calculatorState.lastGesture = "?"
            return
        }

        val normalized = prediction.name.trim().lowercase(Locale.US)
        calculatorState.handleGesture(normalized)
        gestureStatus.text = getString(R.string.gesture_recognized, prediction.name)
    }
}

private class CalculatorUiState {
    var displayText by mutableStateOf("0")
    var previousOperand by mutableStateOf("")
    var currentOperator by mutableStateOf("")
    var isNewInput by mutableStateOf(true)
    var errorMessage by mutableStateOf("")
    var fromCurrency by mutableStateOf("USD")
    var toCurrency by mutableStateOf("UAH")
    var lastGesture by mutableStateOf("-")

    fun onButtonClick(action: String) {
        errorMessage = ""
        when (action) {
            "C" -> clear()
            "±" -> toggleSign()
            "." -> appendDecimal()
            "+", "-", "*", "/" -> setOperator(action)
            "=" -> evaluate()
            else -> appendDigit(action)
        }
    }

    fun handleGesture(gestureName: String) {
        lastGesture = gestureName
        when (gestureName) {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> appendDigit(gestureName)
            "+", "-" -> setOperator(gestureName)
            "/" -> setOperator("/")
            "x", "*" -> setOperator("*")
            "c" -> clear()
            "=",
            "stop" -> evaluate()
        }
    }

    private fun appendDigit(digit: String) {
        displayText = if (isNewInput || displayText == "0") digit else displayText + digit
        isNewInput = false
    }

    private fun appendDecimal() {
        if (isNewInput) {
            displayText = "0."
            isNewInput = false
        } else if (!displayText.contains(".")) {
            displayText += "."
        }
    }

    private fun setOperator(operator: String) {
        previousOperand = displayText
        currentOperator = operator
        isNewInput = true
    }

    private fun evaluate() {
        if (previousOperand.isEmpty() || currentOperator.isEmpty()) return

        val value1 = previousOperand.toDoubleOrNull() ?: 0.0
        val value2 = displayText.toDoubleOrNull() ?: 0.0
        if (currentOperator == "/" && value2 == 0.0) {
            errorMessage = "Invalid operation (division by zero)"
            displayText = "0"
            previousOperand = ""
            currentOperator = ""
            isNewInput = true
            return
        }

        val result = when (currentOperator) {
            "+" -> value1 + value2
            "-" -> value1 - value2
            "*" -> value1 * value2
            "/" -> value1 / value2
            else -> value2
        }
        displayText = if (result % 1.0 == 0.0) {
            result.toLong().toString()
        } else {
            result.toString()
        }
        previousOperand = ""
        currentOperator = ""
        isNewInput = true
    }

    private fun clear() {
        displayText = "0"
        previousOperand = ""
        currentOperator = ""
        errorMessage = ""
        isNewInput = true
    }

    private fun toggleSign() {
        if (displayText != "0") {
            displayText = if (displayText.startsWith("-")) {
                displayText.removePrefix("-")
            } else {
                "-$displayText"
            }
        }
    }
}

@Composable
private fun CalculatorConverterApp(state: CalculatorUiState) {
    val currentValue = state.displayText.toDoubleOrNull() ?: 0.0
    val convertedValue = (currentValue / exchangeRates.getValue(state.fromCurrency)) *
        exchangeRates.getValue(state.toCurrency)
    val formattedConverted = String.format(Locale.US, "%.2f", convertedValue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Gesture-enabled calculator", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Last gesture: ${state.lastGesture}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        DisplaySection(
            displayText = state.displayText,
            convertedText = formattedConverted,
            errorMessage = state.errorMessage,
            fromCurr = state.fromCurrency,
            toCurr = state.toCurrency,
            onFromChange = { state.fromCurrency = it },
            onToChange = { state.toCurrency = it }
        )
        Spacer(modifier = Modifier.height(20.dp))
        KeypadSection(
            onButtonClick = state::onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}

@Composable
private fun DisplaySection(
    displayText: String,
    convertedText: String,
    errorMessage: String,
    fromCurr: String,
    toCurr: String,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit
) {
    if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        CurrencyDropdown(selected = fromCurr, onSelect = onFromChange)
        Text(text = displayText, fontSize = 36.sp)
    }
    Divider(modifier = Modifier.padding(vertical = 8.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        CurrencyDropdown(selected = toCurr, onSelect = onToChange)
        Text(
            text = convertedText,
            fontSize = 36.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CurrencyDropdown(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(text = selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            exchangeRates.keys.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onSelect(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun KeypadSection(onButtonClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val buttons = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("±", "0", ".", "+"),
        listOf("C", "=")
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { button ->
                    Button(
                        onClick = { onButtonClick(button) },
                        modifier = Modifier
                            .weight(if (button == "C" || button == "=") 2f else 1f)
                            .fillMaxHeight()
                    ) {
                        Text(text = button, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
