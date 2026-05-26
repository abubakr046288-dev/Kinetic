package com.example.backend

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Enterprise user profile storing behavioral telemetry scores,
 * active subscription state, timezone/locale localization settings,
 * and adaptive AI personalization parameters.
 */
data class UserProfile(
    val uid: String,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val timezone: String,
    val locale: String,
    val isPremium: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val focusScore: Int = 100,      // Range: 0-100 (daily digital self-control)
    val disciplineScore: Int = 80,  // Range: 0-100 (habit compliance)
    val energyScore: Int = 100,     // Range: 0-100 (physical capacity index)
    val xpLevel: Int = 1,
    val xpAccumulated: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val deviceMetadata: DeviceMetadata = DeviceMetadata()
)

data class DeviceMetadata(
    val deviceModel: String = android.os.Build.MODEL,
    val osVersion: String = android.os.Build.VERSION.RELEASE,
    val appVersion: String = "1.0.0",
    val pushToken: String = "token_sim_12345"
)

/**
 * Configuration preferences defining blocked/restricted apps,
 * daily tax-unlock guidelines, and behavioral coach personalities.
 */
data class KineticSettings(
    val userId: String,
    val activeBlockedPackages: Set<String> = setOf(
        "com.instagram.android",
        "com.zhiliaoapp.musically",
        "com.google.android.youtube",
        "com.facebook.katana"
    ),
    val exerciseToUnlockMinutes: Map<String, Int> = mapOf(
        "SQUATS" to 15, // 15 squats = 10 minutes reward, etc.
        "PUSHUPS" to 12,
        "PLANKS" to 1
    ),
    val focusWindowStart: String = "08:00",
    val focusWindowEnd: String = "22:00",
    val selectedAiCoachVoice: String = "Serene_Willpower",
    val allowCameraAssistance: Boolean = true
)

/**
 * Robust workout session telemetry supporting multi-device syncing,
 * posture grade analysis, movement uniformity metrics, and cheating risk.
 */
data class WorkoutSessionTelemetry(
    val sessionId: String,
    val workoutType: String,
    val repsCompleted: Int,
    val caloriesBurned: Float,
    val durationSeconds: Int,
    val aiConfidenceScore: Float, // ML-kit landmark confidence average
    val postureQualityScore: Float, // 0.0f - 1.0f (straight back, standard bend)
    val movementConsistency: Float, // Range of motion delta consistency
    val deviceModel: String,
    val creationTime: Long,
    val isVerified: Boolean = false,
    val antiCheatRiskScore: Float = 0.0f // AI computed probability of cheating
)

/**
 * Pose Landmark vectors simulated for validation,
 * representing joint angle calculations in squatted, pushup, or plank positions.
 */
data class PoseLandmarkAngleData(
    val kneeJointAngle: Float,
    val hipJointAngle: Float,
    val shoulderJointAngle: Float,
    val trunkInclination: Float,
    val timestampMs: Long
)

/**
 * Timezone-aware streak parameters including exploit-resistant network timestamps,
 * safety streak freezes, and cumulative boosters.
 */
data class StreakTelemetryState(
    val userId: String,
    val currentDailyStreak: Int = 0,
    val longestDailyStreak: Int = 0,
    val workoutStreak: Int = 0,
    val focusStreak: Int = 0,
    val activeStreakFreezesRemaining: Int = 2,
    val lastCompletedActionDate: String = "", // Event local date string format: yyyy-MM-dd
    val freezeAutoGracePeriodApplied: Boolean = false,
    val xpMultiplier: Float = 1.0f,
    val currentMomentumXPBonus: Int = 0,
    val secureServerEpochTimestamp: Long = System.currentTimeMillis()
)

/**
 * Real-time usage and distraction event captures for screen-time blocking efficiency.
 */
data class ScreenUsageTrend(
    val appPackageName: String,
    val appOpenedCount: Int,
    val totalSecondsSpent: Int,
    val lockScreenInterceptionDeflections: Int, // How many times blocker successfully shielded the app
    val doomscrollDetectionSpikes: Int, // Extreme scrolling acceleration sessions detected by accessibility service
    val timestampDateKey: String // "yyyy-MM-dd"
)

/**
 * Sync Queue structures for Offline-First operation. High-performance conflict resolution values.
 */
@Entity(tableName = "offline_sync_mutations")
data class OfflineSyncMutation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // "UPSERT_PROFILE", "TRACK_WORKOUT", "UPDATE_STREAK", "REWARD_LOG"
    val payloadJson: String,
    val isProcessed: Boolean = false,
    val retryCount: Int = 0,
    val conflictVersion: Int = 1
)
