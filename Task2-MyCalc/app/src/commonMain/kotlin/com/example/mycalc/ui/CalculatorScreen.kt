package com.example.mycalc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mycalc.PlatformKind
import com.example.mycalc.localization.currentStrings
import com.example.mycalc.model.Compounding
import com.example.mycalc.model.formatMoney

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    platform: PlatformKind,
    onPrincipalChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onYearsChange: (Int) -> Unit,
    onCompoundingChange: (Compounding) -> Unit,
    onCalculate: () -> Unit,
    onClearHistory: () -> Unit,
    onChartScaleChange: (Float) -> Unit
) {
    val strings = currentStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = strings.title, style = MaterialTheme.typography.headlineSmall)

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val wide = maxWidth > 900.dp
            if (wide) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        InputSection(
                            state = state,
                            platform = platform,
                            onPrincipalChange = onPrincipalChange,
                            onRateChange = onRateChange,
                            onYearsChange = onYearsChange,
                            onCompoundingChange = onCompoundingChange,
                            onCalculate = onCalculate
                        )
                        ResultSection(state = state)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ChartSection(state = state, platform = platform, onChartScaleChange = onChartScaleChange)
                        HistorySection(state = state, onClearHistory = onClearHistory)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    InputSection(
                        state = state,
                        platform = platform,
                        onPrincipalChange = onPrincipalChange,
                        onRateChange = onRateChange,
                        onYearsChange = onYearsChange,
                        onCompoundingChange = onCompoundingChange,
                        onCalculate = onCalculate
                    )
                    ResultSection(state = state)
                    ChartSection(state = state, platform = platform, onChartScaleChange = onChartScaleChange)
                    HistorySection(state = state, onClearHistory = onClearHistory)
                }
            }
        }
    }
}

@Composable
private fun InputSection(
    state: CalculatorUiState,
    platform: PlatformKind,
    onPrincipalChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onYearsChange: (Int) -> Unit,
    onCompoundingChange: (Compounding) -> Unit,
    onCalculate: () -> Unit
) {
    val strings = currentStrings()
    val shape = RoundedCornerShape(16.dp)
    val border = if (platform == PlatformKind.Desktop) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null

    Card(shape = shape, border = border, elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberInput(
                label = strings.principal,
                value = state.principal,
                error = state.validation.principalError,
                platform = platform,
                testTag = "principal_input",
                onChange = onPrincipalChange
            )
            NumberInput(
                label = strings.annualRate,
                value = state.annualRate,
                error = state.validation.rateError,
                platform = platform,
                testTag = "rate_input",
                onChange = onRateChange
            )
            YearsInput(
                years = state.years,
                error = state.validation.yearsError,
                platform = platform,
                onYearsChange = onYearsChange
            )
            CompoundingInput(
                selected = state.compounding,
                platform = platform,
                onChange = onCompoundingChange
            )
            when (platform) {
                PlatformKind.IOS -> TextButton(
                    onClick = onCalculate,
                    modifier = Modifier.testTag("calculate_button")
                ) {
                    Text(text = strings.calculate)
                }
                PlatformKind.Desktop -> OutlinedButton(
                    onClick = onCalculate,
                    modifier = Modifier.testTag("calculate_button")
                ) {
                    Text(text = strings.calculate)
                }
                else -> Button(
                    onClick = onCalculate,
                    modifier = Modifier.testTag("calculate_button")
                ) {
                    Text(text = strings.calculate)
                }
            }
        }
    }
}

@Composable
private fun NumberInput(
    label: String,
    value: String,
    error: String?,
    platform: PlatformKind,
    testTag: String,
    onChange: (String) -> Unit
) {
    val isLinux = platform == PlatformKind.Desktop
    if (isLinux) {
        SpinBox(
            label = label,
            value = value,
            step = if (label.contains("%")) 0.5 else 100.0,
            onChange = onChange,
            testTag = testTag
        )
        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
    } else {
        TextField(
            value = value,
            onValueChange = onChange,
            label = { Text(text = label) },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun YearsInput(
    years: Int,
    error: String?,
    platform: PlatformKind,
    onYearsChange: (Int) -> Unit
) {
    val strings = currentStrings()
    when (platform) {
        PlatformKind.IOS -> PickerField(
            label = strings.years,
            selected = years,
            options = (1..30).toList(),
            onChange = onYearsChange,
            testTag = "years_picker"
        )
        PlatformKind.Desktop -> SpinBox(
            label = strings.years,
            value = years.toString(),
            step = 1.0,
            onChange = { value -> onYearsChange(value.toIntOrNull() ?: years) },
            testTag = "years_spinbox"
        )
        else -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "${strings.years}: $years")
                Slider(
                    value = years.toFloat(),
                    onValueChange = { onYearsChange(it.toInt()) },
                    valueRange = 1f..30f,
                    steps = 28,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("years_slider")
                )
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
    if (platform == PlatformKind.Desktop && error != null) {
        Text(text = error, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun CompoundingInput(
    selected: Compounding,
    platform: PlatformKind,
    onChange: (Compounding) -> Unit
) {
    val strings = currentStrings()
    when (platform) {
        PlatformKind.IOS -> PickerField(
            label = strings.compounding,
            selected = selected,
            options = Compounding.values().toList(),
            onChange = onChange,
            testTag = "compounding_picker"
        )
        else -> {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CompoundingButton(
                    text = strings.monthly,
                    selected = selected == Compounding.Monthly,
                    onClick = { onChange(Compounding.Monthly) }
                )
                CompoundingButton(
                    text = strings.quarterly,
                    selected = selected == Compounding.Quarterly,
                    onClick = { onChange(Compounding.Quarterly) }
                )
                CompoundingButton(
                    text = strings.yearly,
                    selected = selected == Compounding.Yearly,
                    onClick = { onChange(Compounding.Yearly) }
                )
            }
        }
    }
}

@Composable
private fun CompoundingButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Surface(
        color = colors,
        contentColor = contentColor,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("compounding_$text")
        )
    }
}

@Composable
private fun ResultSection(state: CalculatorUiState) {
    val strings = currentStrings()
    AnimatedVisibility(visible = state.result != null) {
        Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = strings.finalAmount, fontWeight = FontWeight.SemiBold)
                Text(text = state.result?.finalAmount?.let { formatMoney(it) } ?: "-")
                Divider()
                Text(text = strings.profit, fontWeight = FontWeight.SemiBold)
                Text(text = state.result?.profit?.let { formatMoney(it) } ?: "-")
            }
        }
    }
}

@Composable
private fun ChartSection(
    state: CalculatorUiState,
    platform: PlatformKind,
    onChartScaleChange: (Float) -> Unit
) {
    val strings = currentStrings()
    val useGradient = platform == PlatformKind.Android || platform == PlatformKind.Web
    val stroke = if (platform == PlatformKind.IOS) 1.2f else 2.4f
    val chartHeight = if (platform == PlatformKind.Desktop) 220.dp else 260.dp
    val scale = state.chartScale

    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = strings.chartTitle, fontWeight = FontWeight.SemiBold)
            if (platform == PlatformKind.Web) {
                Text(text = strings.zoomHint, style = MaterialTheme.typography.bodySmall)
            }
            GrowthChart(
                points = state.result?.series.orEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .pointerInput(platform) {
                        if (platform == PlatformKind.Web) {
                            detectTransformGestures { _, _, zoom, _ ->
                                onChartScaleChange(scale * zoom)
                            }
                        }
                    }
                    .testTag("chart"),
                scale = scale,
                useGradient = useGradient,
                strokeWidth = stroke
            )
        }
    }
}

@Composable
private fun HistorySection(state: CalculatorUiState, onClearHistory: () -> Unit) {
    val strings = currentStrings()
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = strings.history, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                TextButton(onClick = onClearHistory) {
                    Text(text = strings.clearHistory)
                }
            }
            if (state.history.isEmpty()) {
                Text(text = "-")
            } else {
                LazyColumn(modifier = Modifier.height(180.dp)) {
                    items(state.history.takeLast(5).reversed()) { entry ->
                        Text(
                            text = "${formatMoney(entry.result.finalAmount)} (${entry.params.years}y)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> PickerField(
    label: String,
    selected: T,
    options: List<T>,
    onChange: (T) -> Unit,
    testTag: String
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.toString()) },
                    onClick = {
                        onChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SpinBox(
    label: String,
    value: String,
    step: Double,
    onChange: (String) -> Unit,
    testTag: String
) {
    Column {
        Text(text = label)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(260.dp)) {
            IconButton(onClick = {
                val current = value.toDoubleOrNull() ?: 0.0
                onChange((current - step).coerceAtLeast(0.0).toString())
            }) {
                Text(text = "-")
            }
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag(testTag),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RectangleShape
            )
            IconButton(onClick = {
                val current = value.toDoubleOrNull() ?: 0.0
                onChange((current + step).toString())
            }) {
                Text(text = "+")
            }
        }
    }
}
