package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutRoutinesData
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorkoutViewModel

@Composable
fun HomeDashboardScreen(
    viewModel: WorkoutViewModel,
    onNavigateToWorkoutTab: () -> Unit,
    onNavigateToFocusTab: () -> Unit,
    onOpenAiCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenTimeSec by viewModel.screenTimeRemainingSecFlow.collectAsState()
    val focusScore by viewModel.focusScore.collectAsState()
    val allHistory by viewModel.allWorkouts.collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    // Derived states
    val completedCount = allHistory.size
    val totalCalories = remember(allHistory) {
        allHistory.sumOf { it.caloriesBurned.toDouble() }.toFloat()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .verticalScroll(scrollState)
            .padding(18.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Premium Branding Lockup Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            KineticLogo(iconSize = 32.dp, textSize = 20.sp, showTagline = true)

            // Streak Pill Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentLime.copy(alpha = 0.12f))
                    .border(1.dp, AccentLime.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = "🔥", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "7 D STREAK",
                    color = AccentLime,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        HorizontalDivider(
            color = TextMuted.copy(alpha = 0.15f),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Greeting Row
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "WELCOME BACK",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AccentNeonBlue,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "Focus Athlete",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // HERO CARD: Earn Screen Time Dial
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ACTIVE SOCIAL ALLOTMENT DIAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentLime,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Circular Progress Dial
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(190.dp)
                ) {
                    val sweepPercentage = (screenTimeSec / 600f).coerceIn(0f, 1f)
                    
                    Canvas(modifier = Modifier.size(175.dp)) {
                        // Background track
                        drawCircle(
                            color = TextMuted.copy(alpha = 0.12f),
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Active colored sweep
                        drawArc(
                            color = if (screenTimeSec < 60) ErrorRed else AccentLime,
                            startAngle = -220f,
                            sweepAngle = sweepPercentage * 260f,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Numeric Clock
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val minutes = screenTimeSec / 60
                        val seconds = screenTimeSec % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = "Remaining",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (screenTimeSec <= 0) "Social trap locks armed" else "Secure: paying attention taxes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (screenTimeSec <= 0) ErrorRed else SuccessGreen
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Big Premium CTA: Pay tax via Camera
                Button(
                    onClick = { onOpenAiCamera() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLime, contentColor = PrimaryBackground),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Icon(imageVector = Icons.Default.AccessibilityNew, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pay Tax: Launch AI Rep Counter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Horizontal Grid Items (Calories, Stats completed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Calories Box
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = AccentNeonBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "EST CALORIES", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = String.format("%.0f kcal", totalCalories),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // Workouts count
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = AccentLime,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "COMPLETED", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$completedCount sessions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        // DAILY DISCIPLINE ADVICE WIDGET
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { onNavigateToFocusTab() }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AccentNeonBlue.copy(0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.TrendingDown, contentDescription = null, tint = AccentNeonBlue)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Willpower Score: $focusScore%", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "You've saved 45 mins from doomscrolling today.", fontSize = 11.sp, color = TextSecondary)
                }
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = TextMuted)
            }
        }

        // HOT WORKOUT PRESETS CAROUSEL (Direct Link to workouts)
        Text(
            text = "RECOMMENDED ATTENTION RECOVERY PLANS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val shortList = WorkoutRoutinesData.routinesList.take(2)
            shortList.forEach { routine ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToWorkoutTab() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = routine.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentLime
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = routine.name,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${routine.exercises.size} ex • ${routine.totalCalories} kcal",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Play Store Compliant Privacy disclosure box
        Card(
            colors = CardDefaults.cardColors(containerColor = TextMuted.copy(0.08f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kinetic Focus blocks social applications only locally on your device in compliance with Google Play Privacy guidelines. No data is collected or uploaded.",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
