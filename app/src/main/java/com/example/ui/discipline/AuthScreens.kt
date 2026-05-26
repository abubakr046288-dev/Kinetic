package com.example.ui.discipline

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.theme.KineticLogo
import com.example.ui.theme.KineticLogoSymbol
import com.example.ui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: WorkoutViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Branding Lockup
            KineticLogo(iconSize = 64.dp, textSize = 32.sp, showTagline = true)
            
            Spacer(modifier = Modifier.height(36.dp))

            // Selection Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isSignUp) AccentLime.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { isSignUp = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "SIGN IN",
                        fontWeight = FontWeight.ExtraBold,
                        color = if (!isSignUp) AccentLime else TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSignUp) AccentLime.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { isSignUp = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "REGISTER",
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSignUp) AccentLime else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        errorMessage!!,
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Input Fields
            if (isSignUp) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMessage = null },
                    label = { Text("Username", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AccentLime) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentLime,
                        unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                        focusedLabelColor = AccentLime
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("auth_username_field")
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Email Address", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AccentLime) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentLime,
                    unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                    focusedLabelColor = AccentLime
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("auth_email_field")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Password", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AccentLime) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentLime,
                    unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                    focusedLabelColor = AccentLime
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("auth_password_field")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty() || (isSignUp && username.isEmpty())) {
                        errorMessage = "Please fill in all layout credentials to authenticate."
                        return@Button
                    }
                    isLoading = true
                    coroutineScope.launch {
                        delay(1200) // Simulated secure backend authentication latency
                        try {
                            if (isSignUp) {
                                viewModel.triggerAuthEmail(email, username)
                            } else {
                                viewModel.triggerAuthEmail(email, email.split("@")[0])
                            }
                            isLoading = false
                            onAuthSuccess()
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Federated server connection timed out."
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_submit_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = PrimaryBackground, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        if (isSignUp) "CREATE ACCOUNT" else "SECURE SIGN IN",
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryBackground,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("OR CONNECT VIA SECURE FEDERATION", fontSize = 10.sp, letterSpacing = 1.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(16.dp))

            // Social Federated Flows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google OAuth
                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            delay(1000)
                            viewModel.triggerAuthGoogle("athlete@gmail.com", "Focus Athlete")
                            isLoading = false
                            onAuthSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, TextMuted.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp).testTag("google_login_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AccentNeonBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GOOGLE", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    }
                }

                // Guest Session Bypass
                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            delay(400)
                            viewModel.triggerAuthGuest()
                            isLoading = false
                            onAuthSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, AccentLime.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp).testTag("guest_login_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = AccentLime, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GUEST MODE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = AccentLime)
                    }
                }
            }
        }
    }
}

/**
 * Compliance-Approved Interactive Onboarding Permission disclosure view.
 */
@Composable
fun OnboardingPermissionsScreen(
    onPermissionsAcknowledged: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Header
            Column(
                modifier = Modifier.padding(top = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "PRIVACY & ETHICAL USE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentLime,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "System Permissions",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Explanatory visual container
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = "PermissionChangeAnim"
            ) { currentStep ->
                when (currentStep) {
                    1 -> PermissionDetailsCard(
                        title = "1. Accessibility Service API",
                        subtitle = "DOOMSCROLL INTERCEPTION",
                        description = "Required to detect when social communication applications (like Instagram and TikTok) transition into the foreground. Triggering instant lockouts guarantees you complete Squats to restore attention allowances.",
                        compliance = "Compliance Note: Kinetic complies completely with Google Play policies. Screen visual recording, password text extraction, or user credentials capturing are strictly restricted. Processes execute completely offline.",
                        icon = Icons.Default.LockOpen,
                        highlightColor = AccentLime
                    )
                    2 -> PermissionDetailsCard(
                        title = "2. System Draw Overlays",
                        subtitle = "PERSISTENT DISCIPLINE SHIELDS",
                        description = "Enables drawing high-visibility dark blurry cards when your daily free minutes run fully out. Intercepts distraction attempts seamlessly, preventing notification loops or bypassing locks.",
                        compliance = "Compliance Note: Dismisses immediately upon workout confirmation. Will never block system alarms or emergency calls.",
                        icon = Icons.Default.AspectRatio,
                        highlightColor = AccentNeonBlue
                    )
                    else -> PermissionDetailsCard(
                        title = "3. Usage Analytics Stats",
                        subtitle = "SCREEN REDUCTION ANALYTICS",
                        description = "Tracks per-application active runtime metrics to graph your progress, calculate daily willpower averages, and reward consistency multipliers.",
                        compliance = "Compliance Note: Data persists locally inside your offline Room files. Zero user analytics ever upload to ad trackers.",
                        icon = Icons.Default.QueryStats,
                        highlightColor = AccentNeonBlue
                    )
                }
            }

            // Flow Navigation Control Row
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    repeat(3) { idx ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (step == idx + 1) 24.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(if (step == idx + 1) AccentLime else CardBackground)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            onPermissionsAcknowledged()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        if (step < 3) "I SECURELY AUTHORIZE" else "UNLOCK SYSTEM FREEDOM",
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryBackground,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionDetailsCard(
    title: String,
    subtitle: String,
    description: String,
    compliance: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlightColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, highlightColor.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(highlightColor.copy(alpha = 0.12f))
                    .border(1.dp, highlightColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = highlightColor, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = highlightColor,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                compliance,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = TextMuted,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
