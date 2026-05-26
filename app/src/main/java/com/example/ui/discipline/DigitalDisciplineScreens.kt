package com.example.ui.discipline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay

// ==========================================
// 1. CINEMATIC ONBOARDING SCREEN
// ==========================================
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        OnboardingPageData(
            title = "Turn Screen Time\nInto Strength",
            description = "Welcome to Kinetic Focus. Earn social media time by performing real physical exercises validated in real-time.",
            icon = Icons.Default.ElectricBolt,
            highlightColor = AccentLime
        ),
        OnboardingPageData(
            title = "AI Real-Time\nCamera Feedback",
            description = "Activate your front-facing AI trainer. Perfect your squats, planks, and pushups with automatic rep counting and posture coaching.",
            icon = Icons.Default.AccessibilityNew,
            highlightColor = AccentNeonBlue
        ),
        OnboardingPageData(
            title = "Digital Discipline\nDoomscroll Block",
            description = "Our battery-optimized Accessibility system monitors attention traps like Instagram and TikTok, blocking access until your daily physical tax is paid.",
            icon = Icons.Default.Lock,
            highlightColor = ErrorRed
        )
    )

    val currentPageData = pages[currentPage]

    // Background subtle gradient pulse
    val infiniteTransition = rememberInfiniteTransition(label = "BgTransition")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
    ) {
        // Glowing Ambient Radial Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = currentPageData.highlightColor.copy(alpha = 0.12f * ambientPulse),
                radius = size.width * 1.2f,
                center = Offset(size.width / 2f, size.height * 0.2f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Branding
            KineticLogo(
                modifier = Modifier.padding(top = 16.dp),
                iconSize = 36.dp,
                textSize = 20.sp,
                showTagline = false
            )

            // Central Dynamic Icon Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(CardBackground.copy(alpha = 0.6f))
                    .border(2.dp, currentPageData.highlightColor.copy(alpha = 0.3f), CircleShape)
            ) {
                if (currentPage == 0) {
                    KineticLogoSymbol(sizeDp = 110.dp)
                } else {
                    Icon(
                        imageVector = currentPageData.icon,
                        contentDescription = null,
                        tint = currentPageData.highlightColor,
                        modifier = Modifier
                            .size(80.dp)
                            .scale(1f)
                    )
                }
            }

            // Description Block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = currentPageData.title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp,
                    textAlign = TextAlign.Center,
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        shadow = Shadow(
                            color = currentPageData.highlightColor.copy(alpha = 0.5f),
                            blurRadius = 16f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentPageData.description,
                    fontSize = 15.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // Bottom Navigation Controllers
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    pages.forEachIndexed { index, _ ->
                        val dotWidth by animateDpAsState(
                            targetValue = if (index == currentPage) 24.dp else 8.dp,
                            label = "dotWidth"
                        )
                        val dotColor by animateColorAsState(
                            targetValue = if (index == currentPage) currentPageData.highlightColor else TextMuted,
                            label = "dotColor"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(dotWidth)
                                .clip(RoundedCornerShape(4.dp))
                                .background(dotColor)
                        )
                    }
                }

                // CTA Button
                Button(
                    onClick = {
                        if (currentPage < pages.lastIndex) {
                            currentPage++
                        } else {
                            onFinished()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentPageData.highlightColor,
                        contentColor = PrimaryBackground
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_next_button")
                ) {
                    Text(
                        text = if (currentPage == pages.lastIndex) "Activate Discipline Engine" else "Understand Framework",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val highlightColor: Color
)


// ==========================================
// 2. FOCUS MODE & DIGITAL WELLBEING CONTROL
// ==========================================
@Composable
fun FocusModeScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val screenTimeSec by viewModel.screenTimeRemainingSecFlow.collectAsState()
    val isBlockingEnabled by viewModel.blockingEnabled.collectAsState()
    val focusScore by viewModel.focusScore.collectAsState()
    val scrollState = rememberScrollState()

    var activeBreatheMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        if (activeBreatheMode) {
            // Interactive glowing Breath Training
            BreatheTrainerWidget(
                onClose = { activeBreatheMode = false }
            )
        } else {
            // Standard Focus config View
            Spacer(modifier = Modifier.height(12.dp))
            
            // Focus willpower title
            Text(
                text = "ATTENTION CENTER",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AccentNeonBlue,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Willpower Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Main Status indicator Cards Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Focus Score Ring Item
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Willpower Index",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                            Canvas(modifier = Modifier.size(72.dp)) {
                                drawCircle(color = TextMuted.copy(alpha = 0.2f), style = Stroke(6.dp.toPx()))
                                drawArc(
                                    color = AccentNeonBlue,
                                    startAngle = -90f,
                                    sweepAngle = (focusScore / 100f) * 360f,
                                    useCenter = false,
                                    style = Stroke(6.dp.toPx())
                                )
                            }
                            Text(
                                text = "$focusScore%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (focusScore > 80) "Elite Focus" else "Distracted",
                            fontSize = 11.sp,
                            color = if (focusScore > 80) SuccessGreen else AccentLime,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // App blocking status module
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Discipline Lock",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Icon(
                            imageVector = if (isBlockingEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (isBlockingEnabled) AccentLime else ErrorRed,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.updateBlockingEnabled(!isBlockingEnabled) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBlockingEnabled) ErrorRed.copy(0.2f) else AccentLime.copy(0.2f),
                                contentColor = if (isBlockingEnabled) ErrorRed else AccentLime
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = if (isBlockingEnabled) "Disable" else "Activate",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Dopamine Restorer CTA button (Breathing Session)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, AccentNeonBlue.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { activeBreatheMode = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AccentNeonBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = AccentNeonBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset Dopamine Clutter",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Inhale cyclic breaths to erase distraction loops.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = AccentNeonBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Screen time available display card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Social Allotment Balance",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentLime,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            val minutes = screenTimeSec / 60
                            val seconds = screenTimeSec % 60
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text(
                                text = "Minutes remaining of unlocked social play.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Button(
                            onClick = { viewModel.addEarnedScreenTime(300) }, // earn 5 mins
                            colors = ButtonDefaults.buttonColors(containerColor = AccentLime, contentColor = PrimaryBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+5m Demo", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Core Apps block configuration module
            Text(
                text = "BLOCKED TRAP REGIME",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            BlockedAppRowItem(
                appName = "Instagram",
                pkgName = "com.instagram.android",
                icon = Icons.Default.PhotoCamera,
                tint = Color(0xFFE1306C)
            )
            BlockedAppRowItem(
                appName = "TikTok",
                pkgName = "com.zhiliaoapp.musically",
                icon = Icons.Default.MusicNote,
                tint = Color(0xFF00F2FE)
            )
            BlockedAppRowItem(
                appName = "YouTube Shorts",
                pkgName = "com.google.android.youtube",
                icon = Icons.Default.VideoLibrary,
                tint = Color(0xFFFF0000)
            )
            BlockedAppRowItem(
                appName = "Facebook Reels",
                pkgName = "com.facebook.katana",
                icon = Icons.Default.Feed,
                tint = Color(0xFF1877F2)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // DOOMSCROLL SIMULATION CALLOUT (to test the lockout UI easily)
            Card(
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(0.12f)),
                border = BorderStroke(1.dp, ErrorRed.copy(0.3f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Doomscroll Simulation Guard",
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Simulate loading a locked app like Instagram to visualize and test the full-screen workout locking overlay intervention.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.forceLockSimulated() },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Report, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trigger Simulated Lockdown Mode", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BlockedAppRowItem(
    appName: String,
    pkgName: String,
    icon: ImageVector,
    tint: Color
) {
    var checkToggled by remember { mutableStateOf(com.example.services.DisciplineEngine.blockedApps.contains(pkgName)) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = appName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(text = "Enforce squat requirements", fontSize = 11.sp, color = TextMuted)
                }
            }
            Switch(
                checked = checkToggled,
                onCheckedChange = { active ->
                    checkToggled = active
                    if (active) {
                        com.example.services.DisciplineEngine.blockedApps.add(pkgName)
                    } else {
                        com.example.services.DisciplineEngine.blockedApps.remove(pkgName)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryBackground,
                    checkedTrackColor = AccentLime,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = PrimaryBackground
                )
            )
        }
    }
}


// ==========================================
// 3. GLOWING MIND MINDFULNESS BREATH COACH
// ==========================================
@Composable
fun BreatheTrainerWidget(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phaseSecondsRemaining by remember { mutableStateOf(4) }
    var currentPhase by remember { mutableStateOf("Inhale") } // "Inhale", "Hold", "Exhale", "Hold Out"
    
    // Auto timer logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (phaseSecondsRemaining > 1) {
                phaseSecondsRemaining--
            } else {
                when (currentPhase) {
                    "Inhale" -> {
                        currentPhase = "Hold"
                        phaseSecondsRemaining = 4
                    }
                    "Hold" -> {
                        currentPhase = "Exhale"
                        phaseSecondsRemaining = 4
                    }
                    "Exhale" -> {
                        currentPhase = "Hold Out"
                        phaseSecondsRemaining = 4
                    }
                    "Hold Out" -> {
                        currentPhase = "Inhale"
                        phaseSecondsRemaining = 4
                    }
                }
            }
        }
    }

    // Dynamic scale driven by breath phase
    val targetScale = when (currentPhase) {
        "Inhale" -> 1.5f
        "Hold" -> 1.5f
        "Exhale" -> 0.9f
        "Hold Out" -> 0.9f
        else -> 1.0f
    }
    
    val breatheScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(4000, easing = LinearEasing),
        label = "breatheScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.Start)
                .background(CardBackground, CircleShape)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "DOPAMINE CLEANSING BREATH",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AccentLime,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tactical Box Breathing",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "De-congest dopamine pathways in your central nervous system before physical workouts.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.weight(2f))

        // Rhythmic breathing sphere
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            // Ripple background glow
            Canvas(modifier = Modifier
                .size(160.dp)
                .scale(breatheScale)
            ) {
                drawCircle(
                    color = AccentNeonBlue.copy(alpha = 0.15f),
                    radius = size.width / 2f
                )
                drawCircle(
                    color = AccentNeonBlue.copy(alpha = 0.35f),
                    radius = size.width * 0.45f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentNeonBlue, AccentNeonBlue.copy(0f)),
                        center = center
                    ),
                    radius = size.width * 0.38f
                )
            }

            // Core center indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentPhase.uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "$phaseSecondsRemaining seconds",
                    fontSize = 14.sp,
                    color = AccentLime,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(2f))

        // Progress indicators representation
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            val steps = listOf("Inhale", "Hold", "Exhale", "Hold Out")
            steps.forEach { step ->
                val isActive = currentPhase == step
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(48.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isActive) AccentLime else CardBackground)
                )
            }
        }

        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = CardBackground, contentColor = TextPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Complete Dopamine Checkin", fontWeight = FontWeight.Bold)
        }
    }
}


// ==========================================
// 4. FULLSCREEN "EARN SCREEN TIME" BLOCK OVERLAY
// ==========================================
@Composable
fun AppBlockOverlay(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val screenTimeSec by viewModel.screenTimeRemainingSecFlow.collectAsState()
    val violatingAppName by viewModel.violatingAppName.collectAsState()

    var activeBreatheInsideLock by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
    ) {
        // Aesthetic ambient color backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = ErrorRed.copy(alpha = 0.15f),
                radius = size.width,
                center = Offset(size.width / 2f, size.height * 0.1f)
            )
        }

        if (activeBreatheInsideLock) {
            // Embed box breath
            BreatheTrainerWidget(
                onClose = { activeBreatheInsideLock = false }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Block Interceptor brand lockup
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(CardBackground)
                            .border(1.dp, ErrorRed.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        KineticLogoSymbol(sizeDp = 64.dp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "MOVEMENT CREATES FREEDOM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentLime,
                        letterSpacing = 1.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${violatingAppName.uppercase()} IS BLOCKED",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your digital discipline tax has not been paid.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                // Inspiring Quote block
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = AccentLime,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"Digital entertainment is cheap; physical willpower is royal. Pay your tax to earn attention back.\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "- KINETIC FITNESS PROTOCOL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentLime,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Unlock Requirements
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "REQUIRED TO UNLOCK 15 MINUTES",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Choice A: Camera Squats
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // Defer to camera trainer modes!
                                    viewModel.dismissLockOverlay()
                                    viewModel.addEarnedScreenTime(900) // Grant instant minutes after completing reps
                                },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, AccentLime.copy(alpha = 0.2f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.AccessibilityNew, contentDescription = null, tint = AccentLime)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("20 Squats", fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("validate via AI Cam", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        // Choice B: Camera Pushups
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.dismissLockOverlay()
                                    viewModel.addEarnedScreenTime(900) // Grant minutes
                                },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, AccentLime.copy(alpha = 0.2f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, tint = AccentLime)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("15 Push-ups", fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("validate via AI Cam", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tactical Box Breathe option to settle dopamine
                    Button(
                        onClick = { activeBreatheInsideLock = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CardBackground, contentColor = AccentNeonBlue),
                        border = BorderStroke(1.dp, AccentNeonBlue.copy(0.3f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.SelfImprovement, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dopamine Breathe Reset (2 min)", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bypasses / manual developer credits
                    TextButton(
                        onClick = {
                            viewModel.dismissLockOverlay()
                            viewModel.addEarnedScreenTime(1800) // Unlock 30 minutes
                        },
                        modifier = Modifier.testTag("dismiss_lock_button")
                    ) {
                        Text(
                            text = "Admin Unlock (For testing & verification)",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
