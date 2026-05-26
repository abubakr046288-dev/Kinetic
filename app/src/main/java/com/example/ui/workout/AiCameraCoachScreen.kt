package com.example.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay
import kotlin.math.atan2

// Body Landmark Coordinates for skeletal simulator
data class Landmark(val name: String, var x: Float, var y: Float)

@Composable
fun AiCameraCoachScreen(
    viewModel: WorkoutViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedExercise by remember { mutableStateOf("Squats") } // "Squats" or "PushUps"
    var isFrontCameraActive by remember { mutableStateOf(true) }
    var scaleLowBatteryMode by remember { mutableStateOf(false) }

    // Reps Counter State Machine
    var totalReps by remember { mutableStateOf(0) }
    var currentJointAngle by remember { mutableStateOf(160f) }
    var coachingAdvice by remember { mutableStateOf("Position your full body in frame") }
    var isRepCycleActive by remember { mutableStateOf(false) } // State machine transition check

    // Playback simulator coordinates (automating squat/push-up cycles for interactive demo)
    var cycleFrame by remember { mutableStateOf(0) }
    var autoPlaySimulation by remember { mutableStateOf(true) }

    // Frame loops to dynamically move the virtual skeleton and compute angles
    LaunchedEffect(autoPlaySimulation, selectedExercise, cycleFrame) {
        if (autoPlaySimulation) {
            delay(if (scaleLowBatteryMode) 100 else 40) // battery saver adjusts framerate
            cycleFrame = (cycleFrame + 1) % 100

            val angleDelta = if (selectedExercise == "Squats") {
                // Squat cycle: 170 deg down to 75 deg then back up
                if (cycleFrame < 50) {
                    170f - (95f * (cycleFrame / 50f))
                } else {
                    75f + (95f * ((cycleFrame - 50) / 45f))
                }
            } else {
                // Push-up cycle: 165 deg down to 80 deg then back up
                if (cycleFrame < 50) {
                    165f - (85f * (cycleFrame / 50f))
                } else {
                    80f + (85f * ((cycleFrame - 50) / 45f))
                }
            }
            currentJointAngle = angleDelta

            // REP COUNTER STATE MACHINE LOGIC (Calculated from skeleton coordinates)
            if (selectedExercise == "Squats") {
                if (currentJointAngle <= 90f) {
                    if (!isRepCycleActive) {
                        isRepCycleActive = true
                        coachingAdvice = "Good Depth! Now push back up."
                        viewModel.speak("Excellent depth!")
                    }
                } else if (currentJointAngle >= 145f) {
                    if (isRepCycleActive) {
                        isRepCycleActive = false
                        totalReps++
                        coachingAdvice = "Rep $totalReps complete! Keep it up."
                        viewModel.speak("squat $totalReps count.")
                        viewModel.addEarnedScreenTime(45) // 45 seconds per squat
                    }
                } else {
                    if (isRepCycleActive) {
                        coachingAdvice = "Hold straight posture, knees out!"
                    } else {
                        coachingAdvice = "Go lower to activate glutes!"
                    }
                }
            } else {
                // Pushups
                if (currentJointAngle <= 85f) {
                    if (!isRepCycleActive) {
                        isRepCycleActive = true
                        coachingAdvice = "Great elbow compression! Elevate core."
                        viewModel.speak("Lover limit reached!")
                    }
                } else if (currentJointAngle >= 150f) {
                    if (isRepCycleActive) {
                        isRepCycleActive = false
                        totalReps++
                        coachingAdvice = "Rep $totalReps pushed! Brilliant."
                        viewModel.speak("push-up $totalReps count.")
                        viewModel.addEarnedScreenTime(60) // 60 seconds per push-up
                    }
                } else {
                    if (isRepCycleActive) {
                        coachingAdvice = "Keep head aligned, straight back!"
                    } else {
                        coachingAdvice = "Lower chest parallel to hands!"
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(16.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.background(CardBackground, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            Text(
                text = "AI TRAINER IN SESSION",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AccentNeonBlue,
                letterSpacing = 1.5.sp
            )

            IconButton(
                onClick = { isFrontCameraActive = !isFrontCameraActive },
                modifier = Modifier.background(CardBackground, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Flip Cam",
                    tint = if (isFrontCameraActive) AccentLime else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Main Immersive Camera View / Overlay Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(2.dp, AccentLime.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
        ) {
            // Simulated Active Camera Feed (layered with geometric landmarks)
            if (isFrontCameraActive) {
                // Drawing dynamic stick figure pose dynamically onto custom Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    val w = size.width
                    val h = size.height

                    // SQUAT SKELETON POSITIONS (moving dynamically based on angle values)
                    val scaleFactor = currentJointAngle / 180f
                    
                    // Root Joint coordinates
                    val headOffset = Offset(w * 0.5f, h * 0.18f)
                    val shoulderOffset = Offset(w * 0.5f, h * 0.32f)
                    val hipOffset = Offset(w * 0.5f, h * 0.55f + (1f - scaleFactor) * 80f)
                    val kneeOffset = if (selectedExercise == "Squats") {
                        Offset(
                            w * 0.35f - (1f - scaleFactor) * 50f, 
                            h * 0.72f + (1f - scaleFactor) * 30f
                        )
                    } else {
                        Offset(w * 0.55f, h * 0.7f)
                    }
                    val ankleOffset = Offset(w * 0.42f, h * 0.88f)

                    // Draw bones lines (Body limbs)
                    // Head Circle
                    drawCircle(color = AccentNeonBlue, radius = 24.dp.toPx(), center = headOffset)
                    drawCircle(color = PrimaryBackground, radius = 21.dp.toPx(), center = headOffset)

                    // Spine / Torso
                    drawLine(
                        color = AccentNeonBlue,
                        start = headOffset,
                        end = shoulderOffset,
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = AccentNeonBlue,
                        start = shoulderOffset,
                        end = hipOffset,
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Thigh / Leg joint lines
                    drawLine(
                        color = if (selectedExercise == "Squats" && currentJointAngle <= 90f) AccentLime else AccentNeonBlue,
                        start = hipOffset,
                        end = kneeOffset,
                        strokeWidth = 8.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = AccentNeonBlue,
                        start = kneeOffset,
                        end = ankleOffset,
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Arms representation (Elbow bending)
                    val elbowOffset = if (selectedExercise == "PushUps") {
                        Offset(w * 0.68f + (1f - scaleFactor) * 60f, h * 0.42f)
                    } else {
                        Offset(w * 0.65f, h * 0.38f)
                    }
                    val handOffset = if (selectedExercise == "PushUps") {
                        Offset(w * 0.5f, h * 0.52f)
                    } else {
                        Offset(w * 0.7f, h * 0.45f)
                    }
                    drawLine(
                        color = if (selectedExercise == "PushUps" && currentJointAngle <= 85f) AccentLime else AccentNeonBlue,
                        start = shoulderOffset,
                        end = elbowOffset,
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = AccentNeonBlue,
                        start = elbowOffset,
                        end = handOffset,
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Render Joint Nodes
                    drawCircle(color = AccentLime, radius = 6.dp.toPx(), center = shoulderOffset)
                    drawCircle(color = AccentLime, radius = 6.dp.toPx(), center = hipOffset)
                    drawCircle(color = AccentLime, radius = 6.dp.toPx(), center = kneeOffset)
                    drawCircle(color = AccentLime, radius = 6.dp.toPx(), center = elbowOffset)

                    // Draw green scanning guidelines
                    drawLine(
                        color = AccentLime.copy(alpha = 0.3f),
                        start = Offset(0f, hipOffset.y),
                        end = Offset(w, hipOffset.y),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Device Camera Feed Blocked\nEnable permissions in OS settings", color = TextSecondary, textAlign = TextAlign.Center)
                }
            }

            // High Tech Stats overlays in corners
            // Upper Left: Active joint angle mathematical readout
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryBackground.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentLime))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format("%s Angle: %.1f°", if (selectedExercise == "Squats") "Knee" else "Elbow", currentJointAngle),
                        fontSize = 11.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Upper Right: FPS telemetry
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryBackground.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = if (scaleLowBatteryMode) "Battery Save: 15 FPS" else "AI Model: 45 FPS",
                    fontSize = 10.sp,
                    color = AccentNeonBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }

            // Bottom Core Coaching Alert
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryBackground.copy(alpha = 0.9f)),
                border = BorderStroke(1.dp, AccentLime.copy(0.3f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(14.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speaker",
                        tint = AccentLime,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { viewModel.speak(coachingAdvice) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI FORM COACH DIRECTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentLime
                        )
                        Text(
                            text = coachingAdvice,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lower Controllers Dashboard
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Exercise selection selector row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Squats", "PushUps").forEach { ex ->
                        val isSelected = selectedExercise == ex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentLime else PrimaryBackground)
                                .clickable {
                                    selectedExercise = ex
                                    totalReps = 0
                                    viewModel.speak("Switched posture tracking to $ex")
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (ex == "Squats") "20 Squats Challenge" else "15 Pushups Tax",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PrimaryBackground else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rep counter display and state controller
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "VALID REPS COUNT",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$totalReps",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = AccentLime
                            )
                            Text(
                                text = if (selectedExercise == "Squats") " / 20" else " / 15",
                                fontSize = 18.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                            )
                        }
                    }

                    // AutoPlay simulation controller
                    Button(
                        onClick = { autoPlaySimulation = !autoPlaySimulation },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (autoPlaySimulation) AccentNeonBlue.copy(0.15f) else AccentLime,
                            contentColor = if (autoPlaySimulation) AccentNeonBlue else PrimaryBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (autoPlaySimulation) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (autoPlaySimulation) "Pause Scan" else "Calibrate",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add Manual credits bypass (ideal for emulator previewing!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = scaleLowBatteryMode,
                            onCheckedChange = { scaleLowBatteryMode = it!! },
                            colors = CheckboxDefaults.colors(checkedColor = AccentLime, uncheckedColor = TextMuted)
                        )
                        Text("FPS Battery Saver", fontSize = 11.sp, color = TextSecondary)
                    }

                    TextButton(
                        onClick = {
                            totalReps += 5
                            viewModel.addEarnedScreenTime(300) // instantly grant 5 mins!
                            coachingAdvice = "Completed 5 manual test reps!"
                        },
                        modifier = Modifier.testTag("log_manual_rep_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mock Rep (+5m Unlocked)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentNeonBlue)
                    }
                }
            }
        }
    }
}
