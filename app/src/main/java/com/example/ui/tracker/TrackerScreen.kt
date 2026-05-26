package com.example.ui.tracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WeightLogEntity
import com.example.data.CustomWorkoutLogEntity
import com.example.data.PersonalRecordEntity
import com.example.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues

@Composable
fun TrackerScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    var trackerTab by remember { mutableStateOf("HealthStats") } // "HealthStats" or "PRs"

    val userProfile by viewModel.currentUserProfile.collectAsState()
    val streakTele by viewModel.streakState.collectAsState()
    val authToken by viewModel.userAuthToken.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // --- REALTIME KINETIC PROFILE & SYNC CARD ---
        userProfile?.let { user ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.username,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    if (user.isPremium) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(0.15f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("PREMIUM", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                                Text(
                                    text = user.email,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Logout button
                        TextButton(
                            onClick = { viewModel.triggerLogout() },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("LOGOUT", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Divider
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)))

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sync & Multipliers Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cloud Sync Secured", fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Streak Multiplier: ${streakTele.xpMultiplier}x",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (authToken != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Secure JWT Signature Validated",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Tab Headers picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (trackerTab == "HealthStats") MaterialTheme.colorScheme.secondary else Color.Transparent)
                    .clickable { trackerTab = "HealthStats" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Health Stats",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (trackerTab == "HealthStats") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (trackerTab == "PRs") MaterialTheme.colorScheme.secondary else Color.Transparent)
                    .clickable { trackerTab = "PRs" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PRs & Workout Logs",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (trackerTab == "PRs") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (trackerTab == "HealthStats") {
            HealthStatsView(viewModel = viewModel)
        } else {
            PersonalRecordsAndLogsView(viewModel = viewModel)
        }
    }
}

@Composable
fun HealthStatsView(
    viewModel: WorkoutViewModel
) {
    val waterLog by viewModel.waterLogToday.collectAsState()
    val weightLogs by viewModel.allWeightLogs.collectAsState()

    var userHeightCm by remember { mutableStateOf(170f) }
    var userWeightKg by remember { mutableStateOf(70f) }

    val currentWaterAmount = waterLog?.amountMl ?: 0
    val waterTarget = 2000 // default 2000 ml target

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- SECTION 1: DYNAMIC WATER TRACKER ---
        Text(
            text = "Water Intake Tracker",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Target: $waterTarget ml",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currentWaterAmount ml Consumed",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Procedural Water Glass visual fill illustration
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp, topStart = 6.dp, topEnd = 6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("water_fill_fluid")
                ) {
                    val progressFraction = (currentWaterAmount.toFloat() / waterTarget).coerceIn(0f, 1f)
                    val progressHeight by animateFloatAsState(targetValue = progressFraction, label = "water_refill")

                    // Filled water level block
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(progressHeight)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF81D4FA),
                                        Color(0xFF03A9F4)
                                    )
                                )
                            )
                    )

                    // Percentage tag in center
                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (progressFraction > 0.45f) Color.White else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Water operational control pads
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Decrease cup (-250ml)
                    IconButton(
                        onClick = { viewModel.addWater(-250) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Deduct water Cup")
                    }

                    // Increments: Cup (250ml)
                    Button(
                        onClick = { viewModel.addWater(250) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_cup_button")
                    ) {
                        Icon(imageVector = Icons.Default.LocalDrink, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+250ml Cup")
                    }

                    // Increments: Bottle (500ml)
                    Button(
                        onClick = { viewModel.addWater(500) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_bottle_button")
                    ) {
                        Icon(imageVector = Icons.Default.LocalDrink, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+500ml Bottle")
                    }
                }
            }
        }

        // --- SECTION 2: LIVE BMI CALCULATOR ---
        Text(
            text = "Body Mass Index (BMI) & Weight",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant calculator",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }

                // Slider Input Height
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Height:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "${userHeightCm.toInt()} cm",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = userHeightCm,
                        onValueChange = { userHeightCm = it },
                        valueRange = 100f..230f,
                        modifier = Modifier.testTag("height_slider")
                    )
                }

                // Slider Input Weight
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Weight:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = String.format("%.1f kg", userWeightKg),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = userWeightKg,
                        onValueChange = { userWeightKg = it },
                        valueRange = 30f..150f,
                        modifier = Modifier.testTag("weight_slider")
                    )
                }

                // BMI computation logic
                val heightMeters = userHeightCm / 100f
                val bmiValue = if (heightMeters > 0) userWeightKg / (heightMeters * heightMeters) else 0f
                
                val bmiCategory = when {
                    bmiValue < 18.5f -> "Underweight"
                    bmiValue < 25f -> "Normal (Healthy)"
                    bmiValue < 30f -> "Overweight"
                    else -> "Obese"
                }

                val bmiColor = when {
                    bmiValue < 18.5f -> Color(0xFF0288D1) // Blue
                    bmiValue < 25f -> Color(0xFF388E3C)  // Green
                    bmiValue < 30f -> Color(0xFFF57C00)  // Orange
                    else -> Color(0xFFD32F2F)            // Red
                }

                // Output Display Panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bmiColor.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "YOUR CALCULATED BMI",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%.1f", bmiValue),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = bmiColor
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Status Category",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = bmiCategory,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = bmiColor
                            )
                        }
                    }
                }

                // Submit/Log Today's Weight log to historical tracker
                Button(
                    onClick = {
                        viewModel.logWeight(userWeightKg)
                        viewModel.speak("Weight saved: ${String.format("%.1f", userWeightKg)} kilograms.")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("log_weight_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.MonitorWeight, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG TODAY'S WEIGHT")
                }
            }
        }

        // --- SECTION 3: LINE GRAPH PROGRESSION ---
        if (weightLogs.isNotEmpty()) {
            Text(
                text = "Weight Progression Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "History Trend (${weightLogs.size} logs)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Real fully drawn Vector Line Graph using native Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(8.dp)
                    ) {
                        val strokeCol = MaterialTheme.colorScheme.primary
                        val fillGradient = Brush.verticalGradient(
                            colors = listOf(
                                strokeCol.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val maxWeight = (weightLogs.maxOf { it.weightKg } + 5f).coerceAtLeast(80f)
                            val minWeight = (weightLogs.minOf { it.weightKg } - 5f).coerceAtLeast(30f)
                            val weightDiff = if (maxWeight == minWeight) 1f else maxWeight - minWeight

                            val pointsCount = weightLogs.size
                            val stepX = if (pointsCount > 1) w / (pointsCount - 1) else w

                            val graphPoints = weightLogs.mapIndexed { idx, entity ->
                                val x = idx * stepX
                                val y = h - ((entity.weightKg - minWeight) / weightDiff) * h
                                Offset(x, y)
                            }

                            // Draw reference grid dashes
                            for (gridY in listOf(0f, h / 2, h)) {
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    start = Offset(0f, gridY),
                                    end = Offset(w, gridY),
                                    strokeWidth = 3f
                                )
                            }

                            // Draw custom dynamic smooth line
                            val path = Path()
                            val fillPath = Path()

                            if (graphPoints.isNotEmpty()) {
                                path.moveTo(graphPoints[0].x, graphPoints[0].y)
                                fillPath.moveTo(graphPoints[0].x, h)
                                fillPath.lineTo(graphPoints[0].x, graphPoints[0].y)

                                for (i in 1..graphPoints.lastIndex) {
                                    val current = graphPoints[i]
                                    val prev = graphPoints[i - 1]
                                    // quadratic bezier connection
                                    val controlX = (prev.x + current.x) / 2
                                    path.quadraticTo(controlX, prev.y, current.x, current.y)
                                    fillPath.quadraticTo(controlX, prev.y, current.x, current.y)
                                }

                                fillPath.lineTo(graphPoints.last().x, h)
                                fillPath.lineTo(0f, h)
                                fillPath.close()

                                // Draw soft glow under line
                                drawPath(path = fillPath, brush = fillGradient)

                                // Draw main line
                                drawPath(
                                    path = path,
                                    color = strokeCol,
                                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                                )

                                // Draw circle dots on values
                                graphPoints.forEach { pt ->
                                    drawCircle(
                                        color = strokeCol,
                                        radius = 8f,
                                        center = pt
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 4f,
                                        center = pt
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Minimal list of past entries that can be deleted
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        weightLogs.takeLast(4).reversed().forEach { log ->
                            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(log.dateMillis))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonitorWeight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${log.weightKg} kg",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { viewModel.deleteWeightLog(log.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete entry",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalRecordsAndLogsView(
    viewModel: WorkoutViewModel
) {
    val prList by viewModel.allPersonalRecords.collectAsState()
    val customLogs by viewModel.allCustomWorkoutLogs.collectAsState()

    var selectedExercisePr by remember { mutableStateOf("Push-ups") }
    val exerciseOptions = listOf("Push-ups", "Squats", "Forearm Plank", "Jumping Jacks", "Alternate Lunges", "Tricep Sofa Dips")

    // Modals control
    var showPrLogDialog by remember { mutableStateOf(false) }
    var showWorkoutLogDialog by remember { mutableStateOf(false) }

    val filteredPrsForChart = remember(prList, selectedExercisePr) {
        prList.filter { it.exerciseName.equals(selectedExercisePr, ignoreCase = true) }
            .sortedBy { it.dateMillis }
    }

    val bestPrValue = remember(filteredPrsForChart) {
        if (filteredPrsForChart.isNotEmpty()) {
            filteredPrsForChart.maxOf { it.pbValue }
        } else 0f
    }

    val metricType = remember(selectedExercisePr) {
        if (selectedExercisePr.contains("Plank")) "Seconds" else "Reps"
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. PERSONAL RECORDS TRACKER CARD ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Key Exercise PRs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { showPrLogDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ADD RECORD")
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Exercise selector pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(exerciseOptions) { exercise ->
                        val isSelected = selectedExercisePr == exercise
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedExercisePr = exercise },
                            label = { Text(exercise) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Best value display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ALL-TIME BEST PROGRESS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (bestPrValue > 0) "${bestPrValue.toInt()} $metricType" else "No Data Logged",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PROGRESSION GRAPH DRAWN ON NATIVE CANVAS
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    if (filteredPrsForChart.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Log your first PR to see your progress chart graph here!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (filteredPrsForChart.size == 1) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Current record: ${filteredPrsForChart[0].pbValue.toInt()} $metricType. Log another to unlock progress trend chart!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        val strokeCol = MaterialTheme.colorScheme.primary
                        val fillGradient = Brush.verticalGradient(
                            colors = listOf(
                                strokeCol.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val maxVal = (filteredPrsForChart.maxOf { it.pbValue } + 5f).coerceAtLeast(10f)
                            val minVal = (filteredPrsForChart.minOf { it.pbValue } - 5f).coerceAtLeast(0f)
                            val rangeDiff = if (maxVal == minVal) 1f else maxVal - minVal

                            val pts = filteredPrsForChart.size
                            val intervalX = w / (pts - 1)

                            val points = filteredPrsForChart.mapIndexed { index, entity ->
                                val px = index * intervalX
                                val py = h - ((entity.pbValue - minVal) / rangeDiff) * h
                                Offset(px, py)
                            }

                            // Horizontal reference lines
                            for (gridY in listOf(0f, h / 2, h)) {
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.15f),
                                    start = Offset(0f, gridY),
                                    end = Offset(w, gridY),
                                    strokeWidth = 2f
                                )
                            }

                            val drawPath = Path()
                            val fillPath = Path()

                            drawPath.moveTo(points[0].x, points[0].y)
                            fillPath.moveTo(points[0].x, h)
                            fillPath.lineTo(points[0].x, points[0].y)

                            for (i in 1..points.lastIndex) {
                                val current = points[i]
                                val prev = points[i - 1]
                                val ctrlX = (prev.x + current.x) / 2
                                drawPath.quadraticTo(ctrlX, prev.y, current.x, current.y)
                                fillPath.quadraticTo(ctrlX, prev.y, current.x, current.y)
                            }

                            fillPath.lineTo(points.last().x, h)
                            fillPath.lineTo(0f, h)
                            fillPath.close()

                            // Draw gradient and line
                            drawPath(path = fillPath, brush = fillGradient)
                            drawPath(
                                path = drawPath,
                                color = strokeCol,
                                style = Stroke(width = 5f, cap = StrokeCap.Round)
                            )

                            // Nodes
                            points.forEach { pt ->
                                drawCircle(color = strokeCol, radius = 7f, center = pt)
                                drawCircle(color = Color.White, radius = 3.5f, center = pt)
                            }
                        }
                    }
                }

                if (filteredPrsForChart.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "History & Timeline Records",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredPrsForChart.reversed().take(3).forEach { record ->
                            val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(record.dateMillis))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${record.pbValue.toInt()} $metricType",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (record.notes.isNotEmpty()) {
                                        Text(
                                            text = "“${record.notes}”",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { viewModel.deletePersonalRecord(record.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete record",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. WORKOUT MANUAL LOGGER SECTION ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Manual Workout Logs Feed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { showWorkoutLogDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("LOG SESSION")
            }
        }

        if (customLogs.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No custom workouts logged. Tap 'Log Session' to populate your training history card manually, or log direct completions in the 4-Week Plan tab!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                customLogs.forEach { log ->
                    val dateFormatted = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date(log.dateMillis))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = log.routineName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = dateFormatted,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteCustomWorkoutLog(log.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete log entry",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${log.sets} Sets",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${log.reps} Reps",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Exercises Performed:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = log.exercisesText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            if (log.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.padding(start = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = log.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOG 1: LOG PR SUCCESS ---
    if (showPrLogDialog) {
        var recordValStr by remember { mutableStateOf("") }
        var inputNotes by remember { mutableStateOf("") }
        var dropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showPrLogDialog = false },
            title = {
                Text(
                    text = "Add Personal Best Record",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Track your progressive limits. Select the key exercise done and enter sets reps or seconds count.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Custom dropdown mimicking Material 3 Spinner
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedExercisePr,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Exercise Topic") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { dropdownExpanded = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            exerciseOptions.forEach { ex ->
                                DropdownMenuItem(
                                    text = { Text(ex) },
                                    onClick = {
                                        selectedExercisePr = ex
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = recordValStr,
                        onValueChange = { recordValStr = it },
                        label = { Text("Record Value ($metricType)") },
                        placeholder = { Text(if (metricType == "Reps") "Ex: 25" else "Ex: 60") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputNotes,
                        onValueChange = { inputNotes = it },
                        label = { Text("Brief Progress Notes") },
                        placeholder = { Text("Ex: Smooth focus, full breathing lock. Powerful!") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pbValue = recordValStr.toFloatOrNull() ?: 0f
                        if (pbValue > 0f) {
                            viewModel.savePersonalRecord(
                                exerciseName = selectedExercisePr,
                                pbValue = pbValue,
                                metric = metricType,
                                notes = inputNotes
                            )
                            viewModel.speak("New Personal Record for $selectedExercisePr saved!")
                            showPrLogDialog = false
                        }
                    }
                ) {
                    Text("Save record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrLogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- DIALOG 2: LOG CUSTOM WORKOUT ---
    if (showWorkoutLogDialog) {
        var routineNameLog by remember { mutableStateOf("") }
        var exercisesTextLog by remember { mutableStateOf("") }
        var setsLog by remember { mutableStateOf(3) }
        var repsLog by remember { mutableStateOf(10) }
        var notesLog by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showWorkoutLogDialog = false },
            title = {
                Text(
                    text = "Manual Workout Log Entry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Manually record any custom session you executed outside the predefined lists below.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = routineNameLog,
                        onValueChange = { routineNameLog = it },
                        label = { Text("Session Name / Title") },
                        placeholder = { Text("Ex: Late Night Leg Burners") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = exercisesTextLog,
                        onValueChange = { exercisesTextLog = it },
                        label = { Text("Exercises List (reps details)") },
                        placeholder = { Text("Ex: Squats: 3 sets\nPushups: 3 sets x 12 reps") },
                        singleLine = false,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Sets", style = MaterialTheme.typography.labelSmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (setsLog > 1) setsLog-- }) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Text("$setsLog", fontWeight = FontWeight.Bold)
                                IconButton(onClick = { setsLog++ }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Avg Reps", style = MaterialTheme.typography.labelSmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (repsLog > 1) repsLog-- }) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Text("$repsLog", fontWeight = FontWeight.Bold)
                                IconButton(onClick = { repsLog++ }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notesLog,
                        onValueChange = { notesLog = it },
                        label = { Text("Session Progress Notes") },
                        placeholder = { Text("Ex: Short of breath, feeling energetic.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (routineNameLog.isNotEmpty() && exercisesTextLog.isNotEmpty()) {
                            viewModel.saveCustomWorkoutLog(
                                routineName = routineNameLog,
                                exercisesText = exercisesTextLog,
                                sets = setsLog,
                                reps = repsLog,
                                notes = notesLog
                            )
                            viewModel.speak("Custom workout session saved!")
                            showWorkoutLogDialog = false
                        }
                    }
                ) {
                    Text("Log session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWorkoutLogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Minimal placeholder object for TextField decoration defaults
@OptIn(ExperimentalMaterial3Api::class)
object OutlinedTextFieldDefaults {
    @Composable
    fun colors(
        disabledTextColor: Color,
        disabledBorderColor: Color,
        disabledLabelColor: Color
    ) = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
        disabledTextColor = disabledTextColor,
        disabledBorderColor = disabledBorderColor,
        disabledLabelColor = disabledLabelColor
    )
}
