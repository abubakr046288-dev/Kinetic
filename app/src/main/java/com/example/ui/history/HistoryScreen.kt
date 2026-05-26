package com.example.ui.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutHistoryEntity
import com.example.ui.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val workoutsList by viewModel.allWorkouts.collectAsState()

    // Calculate sum statistics
    val totalWorkouts = workoutsList.size
    val totalCalories = workoutsList.sumOf { it.caloriesBurned.toDouble() }.toFloat()
    val totalDurationMinutes = remember(workoutsList) {
        workoutsList.sumOf { it.durationSeconds } / 60
    }

    // Dynamic Streak Calculator
    val consecutiveStreaks = remember(workoutsList) {
        calculateCurrentStreak(workoutsList)
    }

    // Active calendar week logs
    val currentWeekDays = remember(workoutsList) {
        getWeekCompletionDays(workoutsList)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Stats Overview Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCompactCard(
                title = "Workouts",
                value = "$totalWorkouts",
                icon = Icons.Default.EmojiEvents,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            StatsCompactCard(
                title = "Calories",
                value = "${totalCalories.toInt()} kcal",
                icon = Icons.Default.LocalFireDepartment,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1.3f)
            )

            StatsCompactCard(
                title = "Active Mins",
                value = "$totalDurationMinutes m",
                icon = Icons.Default.Schedule,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1.1f)
            )
        }

        // Streak & Weekly strip module
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Active Streak",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🔥  $consecutiveStreaks Day Streak",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checked Calendar weekstrip (Mon - Sun)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    currentWeekDays.forEach { (dayLabel, isDone) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = dayLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDone) MaterialTheme.colorScheme.tertiary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle, 
                                        contentDescription = "Completed",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // History list feed
        Text(
            text = "Exercise Log History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (workoutsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your completed workouts appear here.\nComplete a routine to kickstart your logs!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(workoutsList) { workout ->
                    HistoryItemCard(
                        workout = workout,
                        onDeleteLog = { viewModel.deleteWorkoutFromHistory(workout.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatsCompactCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    workout: WorkoutHistoryEntity,
    onDeleteLog: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${workout.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.routineName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "⏱️ ${workout.durationSeconds / 60}m ${workout.durationSeconds % 60}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "🔥 ${workout.caloriesBurned.toInt()} kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                val dateStr = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(workout.dateMillis))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                IconButton(
                    onClick = onDeleteLog,
                    modifier = Modifier.size(32.dp).testTag("delete_history_button_${workout.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete, 
                        contentDescription = "Delete history",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Custom function logic to calculate current active streaks
fun calculateCurrentStreak(workouts: List<WorkoutHistoryEntity>): Int {
    if (workouts.isEmpty()) return 0
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dates = workouts.map { format.format(Date(it.dateMillis)) }.distinct().sortedDescending()
    
    val today = format.format(Date())
    
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = format.format(cal.time)

    // If they haven't worked out today AND not yesterday, streak collapsed
    if (dates.first() != today && dates.first() != yesterday) {
        return 0
    }

    var streak = 0
    val checkCalendar = Calendar.getInstance()
    
    // Begin scanning consecutively back in time
    for (i in 0..100) {
        val checkDate = format.format(checkCalendar.time)
        if (dates.contains(checkDate)) {
            streak++
        } else {
            // If they didn't work out today but did yesterday, continue scanning yesterdays. If missing, break.
            if (i == 0) {
                // Keep scanning back
            } else {
                break
            }
        }
        checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
    }
    return streak
}

// Custom function mapping current Mon-Sun keys to complete statuses
fun getWeekCompletionDays(workouts: List<WorkoutHistoryEntity>): List<Pair<String, Boolean>> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val loggedDates = workouts.map { sdf.format(Date(it.dateMillis)) }.toSet()

    val dayLabelList = listOf("M", "T", "W", "T", "F", "S", "S")
    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.MONDAY
    
    // Set to Monday of current week
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    
    val result = mutableListOf<Pair<String, Boolean>>()
    for (i in 0..6) {
        val key = sdf.format(cal.time)
        val isDone = loggedDates.contains(key)
        result.add(Pair(dayLabelList[i], isDone))
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return result
}
