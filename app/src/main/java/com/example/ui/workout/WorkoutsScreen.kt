package com.example.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.WorkoutRoutine
import com.example.data.WorkoutRoutinesData
import com.example.data.BeginnerPlanData
import com.example.data.PlanDay
import com.example.data.PlanExercise
import com.example.data.Exercise
import com.example.data.SVGIconType
import com.example.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    viewModel: WorkoutViewModel,
    onSelectRoutine: (WorkoutRoutine) -> Unit,
    modifier: Modifier = Modifier
) {
    var screenTab by remember { mutableStateOf("Routines") } // "Routines" or "4WeekPlan"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Selection Tabs
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
                    .background(if (screenTab == "Routines") MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { screenTab = "Routines" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Single Routines",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (screenTab == "Routines") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (screenTab == "4WeekPlan") MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { screenTab = "4WeekPlan" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "4-Week Plan",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (screenTab == "4WeekPlan") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (screenTab == "Routines") {
            SingleRoutinesList(onSelectRoutine = onSelectRoutine)
        } else {
            Beginner4WeekPlanView(viewModel = viewModel, onSelectRoutine = onSelectRoutine)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleRoutinesList(
    onSelectRoutine: (WorkoutRoutine) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedLevel by remember { mutableStateOf("All") }

    val categories = listOf("All", "Full Body", "Abs", "Chest", "Arms", "Legs")
    val levels = listOf("All", "Beginner", "Intermediate", "Advanced")

    val filteredRoutines = remember(selectedCategory, selectedLevel) {
        WorkoutRoutinesData.routinesList.filter { routine ->
            (selectedCategory == "All" || routine.category.equals(selectedCategory, ignoreCase = true)) &&
            (selectedLevel == "All" || routine.level.equals(selectedLevel, ignoreCase = true))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Banner card (Inspiring visual callout)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF381E72), // Signature Deep Lavender Accent Base
                            Color(0xFF2B2930)  // Fades into Elegant Surface dark tone
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No Equipment, No Excuses",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFE6E1E5), // Elegant Text Light
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Professional home exercises to sculpt your ultimate dream physique at your own convenience.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCAC4D0) // Elegant Text Alt (subtle grey-white)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Categories selector
        Text(
            text = "Target Area",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Level selector
        Text(
            text = "Difficulty Level",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(levels) { level ->
                val isSelected = selectedLevel == level
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedLevel = level },
                    label = { Text(level) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Routines listing
        Text(
            text = "Structured Routines (${filteredRoutines.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredRoutines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No routines found matching search parameters.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredRoutines) { routine ->
                    RoutineItemCard(
                        routine = routine,
                        onClick = { onSelectRoutine(routine) }
                    )
                }
            }
        }
    }
}

@Composable
fun Beginner4WeekPlanView(
    viewModel: WorkoutViewModel,
    onSelectRoutine: (WorkoutRoutine) -> Unit
) {
    var selectedWeek by remember { mutableStateOf(1) } // 1..4
    var loggingDay by remember { mutableStateOf<PlanDay?>(null) }

    val daysInWeek = remember(selectedWeek) {
        BeginnerPlanData.planDays.filter { it.weekNumber == selectedWeek }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Inspiring Plan header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Beginner Strength Academy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A scientifically-designed 4-week program to build functional bodyweight power, joints stability, and solid muscle foundation. Absolutely no weights required.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Week Selector Row
        Text(
            text = "Select Week",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (w in 1..4) {
                val isSelected = selectedWeek == w
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { selectedWeek = w }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Week $w",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days listing
        Text(
            text = "Daily Routine Schedule",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(daysInWeek) { day ->
                PlanDayCard(
                    day = day,
                    onStart = {
                        val routine = convertPlanDayToRoutine(day)
                        onSelectRoutine(routine)
                    },
                    onLog = {
                        loggingDay = day
                    }
                )
            }
        }
    }

    // Trigger log dialog
    loggingDay?.let { day ->
        LogBeginnerWorkoutDialog(
            day = day,
            onDismiss = { loggingDay = null },
            onSave = { sets, reps, notes ->
                val exercisesList = day.exercises.joinToString("\n") { 
                    "${it.name}: $sets sets x $reps reps (Rest: ${it.restTimeSeconds}s)" 
                }
                viewModel.saveCustomWorkoutLog(
                    routineName = "Week ${day.weekNumber} Day ${day.dayNumber}: ${day.title}",
                    exercisesText = exercisesList,
                    sets = sets,
                    reps = reps,
                    notes = notes
                )
                viewModel.speak("Workout logged completed! Excellent effort.")
                loggingDay = null
            }
        )
    }
}

@Composable
fun PlanDayCard(
    day: PlanDay,
    onStart: () -> Unit,
    onLog: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_day_card_${day.dayNumber}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Day Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DAY ${day.dayNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (day.isRestDay) Color(0x33FF9800) else Color(0x334CAF50)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (day.isRestDay) "REST & LASS" else "STRENGTH WORK",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (day.isRestDay) Color(0xFFFF9800) else Color(0xFF4CAF50)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = day.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand day routine details"
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = day.focusDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(visible = expanded || !day.isRestDay) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))

                    if (day.isRestDay) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "💡 Active recovery guidelines: Maintain lightweight actions such as systemic joints lubrication stretching, deep structural diaphragmatic locks, or a pleasant 20-30 min walk to fully support fiber synthesis.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Display exercises list
                        Text(
                            text = "Exercises Included (${day.exercises.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            day.exercises.forEach { ex ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ex.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = ex.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${ex.sets} Sets",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = ex.repsOrDuration,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "Rest: ${ex.restTimeSeconds}s",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Actions row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onStart,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COACH MODE")
                            }

                            Button(
                                onClick = onLog,
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("LOG COMPLETION")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogBeginnerWorkoutDialog(
    day: PlanDay,
    onDismiss: () -> Unit,
    onSave: (sets: Int, reps: Int, notes: String) -> Unit
) {
    var sets by remember { mutableStateOf(3) }
    var reps by remember { mutableStateOf(12) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Workout Completion",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Weekly Strength: ${day.title}. Confirm typical sets and reps you successfully executed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sets Performed", style = MaterialTheme.typography.labelMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (sets > 1) sets-- }) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = null)
                            }
                            Text("$sets", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { sets++ }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Avg Reps/Set", style = MaterialTheme.typography.labelMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (reps > 1) reps-- }) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = null)
                            }
                            Text("$reps", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { reps++ }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Progress & Feeling Notes") },
                    placeholder = { Text("Ex: Muscles burning, achieved full rest cycles. Smashed it!") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(sets, reps, notes)
                }
            ) {
                Text("Log to Database")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun convertPlanDayToRoutine(day: PlanDay): WorkoutRoutine {
    val exercises = day.exercises.map { pe ->
        val standardEx = WorkoutRoutinesData.routinesList
            .flatMap { it.exercises }
            .firstOrNull { it.id == pe.id }
        
        Exercise(
            id = pe.id,
            name = pe.name,
            description = pe.description,
            durationSeconds = if (pe.repsOrDuration.contains("Secs")) pe.repsOrDuration.replace(" Secs", "").toIntOrNull() ?: 30 else 0,
            reps = if (pe.repsOrDuration.contains("Reps")) pe.repsOrDuration.replace(" Reps", "").toIntOrNull() ?: 10 else 0,
            caloriesBurned = if (pe.repsOrDuration.contains("Secs")) 10f else 12f,
            iconType = standardEx?.iconType ?: SVGIconType.SQUATS
        )
    }
    return WorkoutRoutine(
        id = "plan_day_${day.dayNumber}",
        name = "Day ${day.dayNumber}: ${day.title}",
        level = "Beginner",
        category = "Full Body",
        exercises = exercises,
        description = day.focusDescription
    )
}

@Composable
fun RoutineItemCard(
    routine: WorkoutRoutine,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("routine_card_${routine.id}")
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = routine.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = routine.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start ${routine.name}",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info bar metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number of Exercises
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${routine.exercises.size} Exercises",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Estimated Duration (e.g. 5 mins)
                val totalMins = Math.ceil(routine.totalEstimatedTimeSeconds / 60.0).toInt()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$totalMins mins",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Calories
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${routine.totalCalories.toInt()} kcal",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Difficulty chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (routine.level.lowercase()) {
                                "beginner" -> Color(0x224CAF50)
                                "intermediate" -> Color(0x22FF9800)
                                else -> Color(0x22F44336)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = routine.level,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (routine.level.lowercase()) {
                            "beginner" -> Color(0xFF81C784)
                            "intermediate" -> Color(0xFFFFB74D)
                            else -> Color(0xFFE57373)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
