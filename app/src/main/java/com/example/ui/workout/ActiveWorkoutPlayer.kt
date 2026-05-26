package com.example.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutPlayer(
    viewModel: WorkoutViewModel,
    onFinish: () -> Unit
) {
    val routine by viewModel.activeRoutine.collectAsState()
    val currentIndex by viewModel.currentExerciseIndex.collectAsState()
    val playerState by viewModel.sessionPlayerState.collectAsState()
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    val currentRoutine = routine ?: return

    val exercises = currentRoutine.exercises
    val currentExercise = if (currentIndex in exercises.indices) exercises[currentIndex] else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentRoutine.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.quitWorkout() },
                        modifier = Modifier.testTag("exit_workout_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Exit Workout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingVals ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingVals)
        ) {
            when (playerState) {
                WorkoutViewModel.PlayerState.READY_PREVIEW -> {
                    currentExercise?.let { exercise ->
                        ReadyPreviewLayout(
                            exercise = exercise,
                            secondsRemaining = secondsRemaining,
                            onSkipReady = { viewModel.nextExercise() }
                        )
                    }
                }
                WorkoutViewModel.PlayerState.WORKOUT -> {
                    currentExercise?.let { exercise ->
                        WorkoutTimerLayout(
                            exercise = exercise,
                            secondsRemaining = secondsRemaining,
                            isPaused = isPaused,
                            progress = if (exercise.durationSeconds > 0) {
                                (exercise.durationSeconds - secondsRemaining).toFloat() / exercise.durationSeconds
                            } else 1f,
                            totalExercises = exercises.size,
                            currentIndex = currentIndex,
                            onPauseResume = { viewModel.pauseResume() },
                            onPrev = { viewModel.prevExercise() },
                            onNext = { viewModel.nextExercise() },
                            onCompleteReps = { viewModel.completeRepExercise() }
                        )
                    }
                }
                WorkoutViewModel.PlayerState.COOLDOWN -> {
                    val nextExercise = if (currentIndex + 1 in exercises.indices) exercises[currentIndex + 1] else null
                    CooldownRestLayout(
                        secondsRemaining = secondsRemaining,
                        nextExercise = nextExercise,
                        onSkipBreak = { viewModel.skipBreak() },
                        onAddMoreTime = { viewModel.addMoreBreakTime() }
                    )
                }
                WorkoutViewModel.PlayerState.COMPLETION -> {
                    WorkoutCompletionLayout(
                        routine = currentRoutine,
                        onFinishHome = {
                            viewModel.quitWorkout()
                            onFinish()
                        }
                    )
                }
                else -> { /* Inactive state */ }
            }
        }
    }
}

@Composable
fun ReadyPreviewLayout(
    exercise: com.example.data.Exercise,
    secondsRemaining: Int,
    onSkipReady: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "GET READY!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(30.dp))

        // Big visual circular countdown
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { secondsRemaining / 10f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 10.dp
            )
            Text(
                text = "$secondsRemaining",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "UPCOMING EXERCISE",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = exercise.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (exercise.durationSeconds > 0) "Duration: ${exercise.durationSeconds}s" else "Reps: x${exercise.reps}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onSkipReady,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.testTag("skip_ready_button")
        ) {
            Text("SKIP READY COUNT")
        }
    }
}

@Composable
fun WorkoutTimerLayout(
    exercise: com.example.data.Exercise,
    secondsRemaining: Int,
    isPaused: Boolean,
    progress: Float,
    totalExercises: Int,
    currentIndex: Int,
    onPauseResume: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onCompleteReps: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Simple top indicator of indices
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Exercise ${currentIndex + 1} of $totalExercises",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
            
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalExercises },
                modifier = Modifier
                    .width(100.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Beautiful custom dynamic Canvas visualizer (shows athletic posture animation)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            WorkoutVisualizer(
                iconType = exercise.iconType,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title of Exercise
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action timer OR Rep layout
        if (exercise.durationSeconds > 0) {
            // Time-based exercise countdown
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%02d", secondsRemaining),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        } else {
            // Rep-based exercise
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "x${exercise.reps}",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "REPETITIONS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCompleteReps,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                        .testTag("done_reps_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DONE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Player controller (Prev, Play/Pause, Next)
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button
            IconButton(
                onClick = onPrev,
                enabled = currentIndex > 0,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentIndex > 0) MaterialTheme.colorScheme.surface 
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Previous Exercise",
                    tint = if (currentIndex > 0) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
            }

            // Play / Pause (Only meaningful for timer based layouts)
            if (exercise.durationSeconds > 0) {
                IconButton(
                    onClick = onPauseResume,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Next Button / Skip
            IconButton(
                onClick = onNext,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("skip_next_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward, 
                    contentDescription = "Skip Exercise",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Full detailed written explanation/guide
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How to perform",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun CooldownRestLayout(
    secondsRemaining: Int,
    nextExercise: com.example.data.Exercise?,
    onSkipBreak: () -> Unit,
    onAddMoreTime: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "REST BREAK",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large time display
        Text(
            text = String.format("00:%02d", secondsRemaining),
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Cooldown helpers (+15s, Skip)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onAddMoreTime,
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("+15s REST")
            }

            Button(
                onClick = onSkipBreak,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f).height(48.dp).testTag("skip_break_button")
            ) {
                Text("SKIP REST")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // What's next card
        if (nextExercise != null) {
            Text(
                text = "NEXT EXERCISE",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        WorkoutVisualizer(
                            iconType = nextExercise.iconType,
                            modifier = Modifier.fillMaxSize(0.8f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = nextExercise.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (nextExercise.durationSeconds > 0) "${nextExercise.durationSeconds}s" else "x${nextExercise.reps}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCompletionLayout(
    routine: com.example.data.WorkoutRoutine,
    onFinishHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Trophy Success",
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "WORKOUT COMPLETED!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Awesome effort! Your body is thanking you for this investment.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // High contrast summary cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${routine.totalCalories.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Calories Kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val totalMins = Math.ceil(routine.totalEstimatedTimeSeconds / 60.0).toInt()
                    Text(
                        text = "$totalMins mins",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total Time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onFinishHome,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(52.dp)
                .testTag("finish_workout_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "CONTINUE TO DASHBOARD",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
