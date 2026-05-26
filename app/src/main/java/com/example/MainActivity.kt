package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.home.HomeDashboardScreen
import com.example.ui.discipline.*
import com.example.ui.theme.*
import com.example.ui.tracker.TrackerScreen
import com.example.ui.viewmodel.WorkoutViewModel
import com.example.ui.viewmodel.WorkoutViewModelFactory
import com.example.ui.workout.ActiveWorkoutPlayer
import com.example.ui.workout.AiCameraCoachScreen
import com.example.ui.workout.WorkoutsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val viewModel: WorkoutViewModel by viewModels {
                    WorkoutViewModelFactory(application)
                }
                LaunchedEffect(Unit) {
                    viewModel.refreshDateKey()
                }

                MainAppLayout(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        com.example.services.DisciplineEngine.isAppInForeground = true
    }

    override fun onPause() {
        super.onPause()
        com.example.services.DisciplineEngine.isAppInForeground = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(viewModel: WorkoutViewModel) {
    var currentTab by remember { mutableStateOf(0) }
    var needsOnboarding by remember { mutableStateOf(true) }
    var isAiCameraOpen by remember { mutableStateOf(false) }
    var needsPermissionsOnboarding by remember { mutableStateOf(false) }

    // Read reactive digital identity states & locks
    val currentUserProfile by viewModel.currentUserProfile.collectAsState()
    val isOverlayLocked by viewModel.isOverlayLocked.collectAsState()
    val activeRoutine by viewModel.activeRoutine.collectAsState()

    LaunchedEffect(currentUserProfile) {
        if (currentUserProfile != null) {
            needsOnboarding = false
        }
    }

    if (needsOnboarding) {
        // 1. Cinematic Onboarding on first launch
        OnboardingScreen(
            onFinished = {
                needsOnboarding = false
                viewModel.speak("Let the discipline training begin.")
            }
        )
    } else if (currentUserProfile == null) {
        // 2. Enterprise Authentication (Google / Email Signup)
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = {
                needsPermissionsOnboarding = true
                viewModel.speak("Identity authorized successfully.")
            }
        )
    } else if (needsPermissionsOnboarding) {
        // 3. Play Store Compliance Onboarding Permissions Check
        OnboardingPermissionsScreen(
            onPermissionsAcknowledged = {
                needsPermissionsOnboarding = false
                viewModel.speak("Fidelity shields loaded.")
            }
        )
    } else if (isOverlayLocked) {
        // 4. Strict Lockout overlay triggered when limits run dry
        AppBlockOverlay(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    } else if (isAiCameraOpen) {
        // 5. Immersive AI Coach Camera Overlay
        AiCameraCoachScreen(
            viewModel = viewModel,
            onClose = { isAiCameraOpen = false }
        )
    } else if (activeRoutine != null) {
        // 6. Default Workout Player screen
        ActiveWorkoutPlayer(
            viewModel = viewModel,
            onFinish = {
                // Return to dashboard
            }
        )
    } else {
        // 5. Normal Dashboard Layout
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = CardBackground,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .testTag("app_navigation_bar")
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBackground,
                            selectedTextColor = AccentLime,
                            indicatorColor = AccentLime,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("home_tab_button")
                    )

                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "Workouts") },
                        label = { Text("Workout", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBackground,
                            selectedTextColor = AccentLime,
                            indicatorColor = AccentLime,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("workout_tab_button")
                    )

                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = { Icon(imageVector = Icons.Default.SelfImprovement, contentDescription = "Focus") },
                        label = { Text("Focus", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBackground,
                            selectedTextColor = AccentLime,
                            indicatorColor = AccentLime,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("focus_tab_button")
                    )

                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        icon = { Icon(imageVector = Icons.Default.History, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBackground,
                            selectedTextColor = AccentLime,
                            indicatorColor = AccentLime,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("profile_tab_button")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryBackground)
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    0 -> {
                        HomeDashboardScreen(
                            viewModel = viewModel,
                            onNavigateToWorkoutTab = { currentTab = 1 },
                            onNavigateToFocusTab = { currentTab = 2 },
                            onOpenAiCamera = { isAiCameraOpen = true }
                        )
                    }
                    1 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Immersive AI teaser banner right at the top of the workouts screen
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                border = BorderStroke(1.dp, AccentLime.copy(0.25f)),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .clickable { isAiCameraOpen = true }
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(AccentLime.copy(0.12f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.AccessibilityNew, contentDescription = null, tint = AccentLime)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("AI Pose Camera Trainer", fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("Go live to trace forms & count reps automatically.", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = AccentLime)
                                }
                            }

                            // Standard routines & challenges
                            WorkoutsScreen(
                                viewModel = viewModel,
                                onSelectRoutine = { routine ->
                                    viewModel.startRoutine(routine)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    2 -> {
                        FocusModeScreen(viewModel = viewModel)
                    }
                    3 -> {
                        TrackerScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
