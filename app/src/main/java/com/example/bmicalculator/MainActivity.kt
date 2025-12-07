package com.example.bmicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8FAFC) // bg-slate-50
                ) {
                    BMICalculator()
                }
            }
        }
    }
}

@Composable
fun BMICalculator() {
    // --- State Management (Mirrors React useState) ---
    var unitSystem by remember { mutableStateOf("metric") } // "metric" or "imperial"

    // Inputs
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var heightFt by remember { mutableStateOf("") }
    var heightIn by remember { mutableStateOf("") }

    // Results & Errors
    var bmi by remember { mutableStateOf<Float?>(null) }
    var category by remember { mutableStateOf("") }
    // We store colors as a Pair(TextColor, BgColor)
    var colorTheme by remember { mutableStateOf(Pair(Color.Black, Color.White)) }
    var error by remember { mutableStateOf("") }

    // --- Logic Functions ---

    fun handleReset() {
        height = ""
        weight = ""
        heightFt = ""
        heightIn = ""
        bmi = null
        category = ""
        error = ""
        colorTheme = Pair(Color.Black, Color.White)
    }

    fun handleUnitChange(system: String) {
        handleReset()
        unitSystem = system
    }

    fun determineCategory(value: Float) {
        // Tailwind Colors mapped to Compose Colors
        val redText = Color(0xFFDC2626) // text-red-600
        val redBg = Color(0xFFFEE2E2)   // bg-red-100

        val greenText = Color(0xFF16A34A) // text-green-600
        val greenBg = Color(0xFFDCFCE7)   // bg-green-100

        val yellowText = Color(0xFFCA8A04) // text-yellow-600
        val yellowBg = Color(0xFFFEF9C3)   // bg-yellow-100

        when {
            value < 18.5 -> {
                category = "Underweight"
                colorTheme = Pair(redText, redBg)
            }
            value < 25 -> {
                category = "Normal Weight"
                colorTheme = Pair(greenText, greenBg)
            }
            value < 30 -> {
                category = "Overweight"
                colorTheme = Pair(yellowText, yellowBg)
            }
            else -> {
                category = "Obese"
                colorTheme = Pair(redText, redBg)
            }
        }
    }

    fun validateInput(): Boolean {
        var isValid = true
        var errorMsg = ""

        if (unitSystem == "metric") {
            val hVal = height.toFloatOrNull()
            val wVal = weight.toFloatOrNull()
            if (hVal == null || hVal <= 0 || hVal > 300) {
                isValid = false
                errorMsg = "Please enter a valid height (1-300 cm)."
            } else if (wVal == null || wVal <= 0 || wVal > 500) {
                isValid = false
                errorMsg = "Please enter a valid weight (1-500 kg)."
            }
        } else {
            val ftVal = heightFt.toFloatOrNull()
            val inVal = heightIn.toFloatOrNull()
            val wVal = weight.toFloatOrNull()

            // Check specifically if fields are empty or negative
            if ((heightFt.isEmpty() && heightIn.isEmpty()) || (ftVal ?: 0f) < 0 || (inVal ?: 0f) < 0) {
                isValid = false
                errorMsg = "Please enter valid height values."
            } else if (wVal == null || wVal <= 0 || wVal > 1000) {
                isValid = false
                errorMsg = "Please enter a valid weight (lbs)."
            }
        }

        if (!isValid) error = errorMsg
        return isValid
    }

    fun calculateBMI() {
        error = ""
        if (!validateInput()) return

        var bmiValue = 0f

        if (unitSystem == "metric") {
            val heightInMeters = (height.toFloatOrNull() ?: 0f) / 100
            val weightInKg = weight.toFloatOrNull() ?: 0f
            if (heightInMeters > 0) {
                bmiValue = weightInKg / (heightInMeters * heightInMeters)
            }
        } else {
            val feet = heightFt.toFloatOrNull() ?: 0f
            val inches = heightIn.toFloatOrNull() ?: 0f
            val totalInches = (feet * 12) + inches
            val weightInLbs = weight.toFloatOrNull() ?: 0f

            if (totalInches == 0f) {
                error = "Height cannot be zero."
                return
            }
            bmiValue = (weightInLbs / (totalInches * totalInches)) * 703
        }

        // Round to 1 decimal place
        val finalBmi = String.format("%.1f", bmiValue).toFloat()
        bmi = finalBmi
        determineCategory(finalBmi)
    }

    // --- UI Layout ---

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF4F46E5)) // bg-indigo-600
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.MonitorHeart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BMI Calc",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Monitor your health metrics",
                            color = Color(0xFFE0E7FF), // text-indigo-100
                            fontSize = 14.sp
                        )
                    }
                }

                // Unit Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9)) // bg-slate-100
                        .padding(8.dp)
                ) {
                    UnitToggleButton(
                        text = "Metric (kg/cm)",
                        isSelected = unitSystem == "metric",
                        onClick = { handleUnitChange("metric") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    UnitToggleButton(
                        text = "Imperial (lbs/ft)",
                        isSelected = unitSystem == "imperial",
                        onClick = { handleUnitChange("imperial") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Main Content
                Column(modifier = Modifier.padding(24.dp)) {

                    // Height Input
                    Text(
                        text = "Height",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155), // slate-700
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    if (unitSystem == "metric") {
                        CustomTextField(
                            value = height,
                            onValueChange = { height = it },
                            placeholder = "175",
                            suffix = "cm"
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomTextField(
                                value = heightFt,
                                onValueChange = { heightFt = it },
                                placeholder = "5",
                                suffix = "ft",
                                modifier = Modifier.weight(1f)
                            )
                            CustomTextField(
                                value = heightIn,
                                onValueChange = { heightIn = it },
                                placeholder = "9",
                                suffix = "in",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Weight Input
                    Text(
                        text = "Weight",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    CustomTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        placeholder = if (unitSystem == "metric") "70" else "154",
                        suffix = if (unitSystem == "metric") "kg" else "lbs"
                    )

                    // Error Message
                    AnimatedVisibility(visible = error.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = Color(0xFFDC2626),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Calculate Button
                    Button(
                        onClick = { calculateBMI() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Calculate BMI", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Results Section
                    AnimatedVisibility(visible = bmi != null && error.isEmpty(), enter = fadeIn()) {
                        Column(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorTheme.second)
                                .border(1.dp, colorTheme.first.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "YOUR BMI",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = colorTheme.first.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = bmi.toString(),
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorTheme.first
                                    )
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = category,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorTheme.first
                                    )
                                }
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color.Black.copy(alpha = 0.05f)
                            )

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = colorTheme.first.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (category == "Normal Weight")
                                        "Great job! Keep up the balanced diet and regular exercise."
                                    else
                                        "Consider consulting a healthcare provider for personalized advice.",
                                    fontSize = 14.sp,
                                    color = colorTheme.first.copy(alpha = 0.9f),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Reset Button
                    if (bmi != null || height.isNotEmpty() || weight.isNotEmpty() || heightFt.isNotEmpty()) {
                        TextButton(
                            onClick = { handleReset() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset Calculator", color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
        }
    }
}

// --- Helper Components ---

@Composable
fun UnitToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color.White else Color.Transparent
    val textColor = if (isSelected) Color(0xFF4F46E5) else Color(0xFF64748B)
    val shadowElevation = if (isSelected) 2.dp else 0.dp

    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        shadowElevation = shadowElevation
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    suffix: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
        trailingIcon = {
            Text(
                text = suffix,
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6366F1), // indigo-500
            unfocusedBorderColor = Color(0xFFE2E8F0), // slate-200
            focusedContainerColor = Color(0xFFF8FAFC), // slate-50
            unfocusedContainerColor = Color(0xFFF8FAFC)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}