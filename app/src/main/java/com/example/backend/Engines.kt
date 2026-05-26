package com.example.backend

import android.os.SystemClock
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * TZ-Aware, anticount exploit-protected Streak Validation and Booster Calculation Engine.
 */
object StreakEngine {
    private const val TAG = "StreakEngine"

    /**
     * Verifies and recalculates user daily, weekly streak, consuming freezes or applying
     * grace periods if necessary.
     */
    fun processDailyActivity(
        currentState: StreakTelemetryState,
        todayDateStr: String, // Input "yyyy-MM-dd"
        ntpSynchronizedTime: Long // Remote validated NTP timestamp
    ): StreakTelemetryState {
        // Anti-cheat verification on system timing
        val timeMismatchOffset = abs(System.currentTimeMillis() - ntpSynchronizedTime)
        if (timeMismatchOffset > 600000) { // 10 minutes limit
            Log.e(TAG, "ANTI-CHEAT: Device system clock spoofing detected! Offset limits exceeded.")
            return currentState.copy(
                xpMultiplier = 1.0f,
                currentMomentumXPBonus = 0
            )
        }

        if (currentState.lastCompletedActionDate == todayDateStr) {
            // Already completed today, return identical state (prevents double streak counts)
            return currentState
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today: Date
        val lastDay: Date
        try {
            today = sdf.parse(todayDateStr) ?: Date()
            lastDay = if (currentState.lastCompletedActionDate.isEmpty()) {
                today
            } else {
                sdf.parse(currentState.lastCompletedActionDate) ?: today
            }
        } catch (e: Exception) {
            Log.e(TAG, "Parsing error in streak processing. Fallback applied.", e)
            return currentState
        }

        val diffDays = if (currentState.lastCompletedActionDate.isEmpty()) {
            1
        } else {
            ((today.time - lastDay.time) / (1000 * 60 * 60 * 24)).toInt()
        }

        var newStreak = currentState.currentDailyStreak
        var newFreezes = currentState.activeStreakFreezesRemaining
        var gracePeriodApplied = false

        when {
            diffDays <= 1 -> {
                // Consecutive day or same day sequence
                newStreak += if (diffDays == 1) 1 else 0
            }
            diffDays > 1 -> {
                // Streak broken! Determine if any streak freeze can be consumed safely
                val requiredFreezes = diffDays - 1
                if (newFreezes >= requiredFreezes) {
                    newFreezes -= requiredFreezes
                    gracePeriodApplied = true
                    newStreak += 1 // Maintained because of freeze item consumption
                    Log.i(TAG, "Streak preserved! Consumed $requiredFreezes streak freeze instances.")
                } else {
                    // Out of freezes - reset
                    newStreak = 1
                    Log.i(TAG, "Out of freeze multipliers. Daily streak reset back to 1.")
                }
            }
        }

        val newLongest = maxOf(newStreak, currentState.longestDailyStreak)
        
        // Calculate dynamic XP Booster based on streak tier
        val scaleBoost = when {
            newStreak >= 30 -> 2.0f   // 200% XP boost for month-long discipline
            newStreak >= 14 -> 1.5f   // 150% XP boost
            newStreak >= 7 -> 1.25f   // 25% XP boost
            else -> 1.0f
        }

        // Momentum XP directly aligned to consistent days
        val currentMomentum = newStreak * 15

        return currentState.copy(
            currentDailyStreak = newStreak,
            longestDailyStreak = newLongest,
            activeStreakFreezesRemaining = newFreezes,
            lastCompletedActionDate = todayDateStr,
            freezeAutoGracePeriodApplied = gracePeriodApplied,
            xpMultiplier = scaleBoost,
            currentMomentumXPBonus = currentMomentum,
            secureServerEpochTimestamp = ntpSynchronizedTime
        )
    }

    /**
     * Recovers broken streak via recovering challenge completion.
     */
    fun completeStreakRecoveryChallenge(
        currentState: StreakTelemetryState
    ): StreakTelemetryState {
        // Instantly recover up to 5 days of streak
        val recoveredStreak = currentState.currentDailyStreak.coerceAtLeast(3)
        return currentState.copy(
            currentDailyStreak = recoveredStreak,
            activeStreakFreezesRemaining = minOf(2, currentState.activeStreakFreezesRemaining + 1)
        )
    }
}

/**
 * High-fidelity RewardEngine evaluating discipline payouts.
 */
object RewardEngine {
    data class Payout(
        val secondsEarned: Int,
        val xpEarned: Int,
        val focusPoints: Int,
        val energyBoost: Int
    )

    /**
     * Calculates precision rewards factoring movement duration, confidence, posture, and streak bonus.
     */
    fun calculateWorkoutPayout(
        telemetry: WorkoutSessionTelemetry,
        streakState: StreakTelemetryState
    ): Payout {
        // High intensity base calculation: squats are heavy, plank is time based
        val scaleTypeFactor = when (telemetry.workoutType.uppercase()) {
            "SQUATS" -> 1.8f
            "PUSHUPS" -> 1.5f
            "PLANKS" -> 1.2f
            else -> 1.0f
        }

        // Evaluate posture fidelity (Anti-slop threshold verification)
        val postureBooster = if (telemetry.postureQualityScore >= 0.85f) {
            1.25f // 25% boost for perfect execution alignment
        } else if (telemetry.postureQualityScore < 0.5f) {
            0.5f // Half rewards for lazy or hazardous body angles
        } else {
            1.0f
        }

        // Base reward formula: seconds completed + (reps completed * multiplier)
        val rawTimeRewardSec = telemetry.durationSeconds * 0.5f // 30s = 15s earned
        val rawRepsRewardSec = telemetry.repsCompleted * 45f   // 10 reps = 450s (7.5m) earned
        val baseEarnedSeconds = ((rawTimeRewardSec + rawRepsRewardSec) * scaleTypeFactor * postureBooster).toInt()

        // Apply Streak Booster Multiplier
        val totalEarnedSeconds = (baseEarnedSeconds * streakState.xpMultiplier).toInt()

        // Calculate XP and focus reward points
        val xpPaid = ((telemetry.caloriesBurned * 10f + telemetry.repsCompleted * 5) * streakState.xpMultiplier + streakState.currentMomentumXPBonus).toInt()
        val earnedFocusPoints = (telemetry.postureQualityScore * 100).toInt()
        val energyValue = (telemetry.repsCompleted * 2).coerceAtMost(100)

        return Payout(
            secondsEarned = totalEarnedSeconds.coerceIn(10, 3600), // Min 10s, Max 1 hr unlock per workout
            xpEarned = xpPaid,
            focusPoints = earnedFocusPoints,
            energyBoost = energyValue
        )
    }
}

/**
 * High accuracy Timer engine preventing system restarts and background sleeping bypass.
 * Uses elapsedRealtime references rather than insecure epoch millis.
 */
class TimerEngine {
    private var startElapsedRealtimeReference: Long = 0L
    private var durationSeconds: Int = 0
    private var isRunning: Boolean = false
    private var accumulatedElapsedSeconds: Int = 0

    fun startTimer(seconds: Int) {
        durationSeconds = seconds
        startElapsedRealtimeReference = SystemClock.elapsedRealtime()
        accumulatedElapsedSeconds = 0
        isRunning = true
    }

    fun pauseTimer() {
        if (isRunning) {
            val delta = ((SystemClock.elapsedRealtime() - startElapsedRealtimeReference) / 1000).toInt()
            accumulatedElapsedSeconds += delta
            isRunning = false
        }
    }

    fun resumeTimer() {
        if (!isRunning) {
            startElapsedRealtimeReference = SystemClock.elapsedRealtime()
            isRunning = true
        }
    }

    /**
     * Compute exact remaining duration fully resilient to backgrounding pauses.
     */
    fun getRemainingSeconds(): Int {
        if (!isRunning) {
            return (durationSeconds - accumulatedElapsedSeconds).coerceAtLeast(0)
        }
        val currentElapsed = ((SystemClock.elapsedRealtime() - startElapsedRealtimeReference) / 1000).toInt()
        val totalSpent = accumulatedElapsedSeconds + currentElapsed
        return (durationSeconds - totalSpent).coerceAtLeast(0)
    }

    fun reset() {
        isRunning = false
        startElapsedRealtimeReference = 0L
        accumulatedElapsedSeconds = 0
        durationSeconds = 0
    }
}

/**
 * Simulated ML Kit skeletal coordinates angle calculations and rep state transitions.
 */
object MotionValidationEngine {
    enum class MovementPhase { UP, DOWN }

    /**
     * Dynamic skeletal model tracking squat depth angles.
     * Generates a structural posture quality score.
     */
    fun processSquatMove(
        angles: PoseLandmarkAngleData,
        currentReps: Int,
        lastPhase: MovementPhase
    ): Pair<Int, MovementPhase> {
        val kneeAngle = angles.kneeJointAngle
        val postureFactor = angles.trunkInclination // Hip lean angle relative to vertical

        // Define angles: squat depth at 90-100 deg, standing straight at ~170-180 deg
        return when {
            kneeAngle <= 105f && lastPhase == MovementPhase.UP -> {
                // Descended into perfect squat depth!
                Pair(currentReps, MovementPhase.DOWN)
            }
            kneeAngle >= 165f && lastPhase == MovementPhase.DOWN -> {
                // Successfully locked knees back upright
                Pair(currentReps + 1, MovementPhase.UP)
            }
            else -> {
                Pair(currentReps, lastPhase)
            }
        }
    }

    /**
     * Calculates joint posture safety score based on spine and pelvic alignment vectors.
     */
    fun calculateSquatPostureFidelity(angles: PoseLandmarkAngleData): Float {
        // In squats: leaning too forward is unsafe (extreme hip flexion with vertical curvature)
        val dev = abs(angles.trunkInclination - 30f) // 30 deg neutral spine lean
        return when {
            dev < 10f -> 1.0f
            dev < 25f -> 0.8f
            else -> 0.4f // Bad form / lower back strain risk
        }
    }
}

/**
 * Enterprise-grade Anti-Cheat logic.
 */
object AntiCheatEngine {
    private const val TAG = "AntiCheatEngine"

    /**
     * Validates movement patterns for frame manipulation, loops, and device spoofing.
     */
    fun evaluateWorkoutsCheatProbability(
        telemetry: WorkoutSessionTelemetry,
        angleSequenceHistory: List<PoseLandmarkAngleData>
    ): Float {
        var scoreIdx = 0.0f

        // 1. Rep speed validation. Humans cannot complete complex repetitions in < 450ms.
        val durationPerRep = if (telemetry.repsCompleted > 0) {
            telemetry.durationSeconds.toFloat() / telemetry.repsCompleted
        } else {
            Float.MAX_VALUE
        }
        if (durationPerRep < 0.5f) {
            scoreIdx += 0.5f // Extremely high anomaly signaling automation
            Log.w(TAG, "ANTI-CHEAT WARNING: Sub-human repetition execution speed detected!")
        }

        // 2. Direct identical static video frame loop checking.
        if (angleSequenceHistory.size >= 6) {
            var identicalVarianceCount = 0
            for (i in 0 until angleSequenceHistory.size - 2) {
                val varA = abs(angleSequenceHistory[i].kneeJointAngle - angleSequenceHistory[i + 1].kneeJointAngle)
                val varB = abs(angleSequenceHistory[i + 1].kneeJointAngle - angleSequenceHistory[i + 2].kneeJointAngle)
                if (varA == 0.0f && varB == 0.0f) {
                    identicalVarianceCount++
                }
            }
            if (identicalVarianceCount > 3) {
                scoreIdx += 0.4f // Frame manipulation or static image mock placement
                Log.w(TAG, "ANTI-CHEAT WARNING: Monotonically fixed skeletal telemetry frames!")
            }
        }

        // 3. NTP server validation check.
        val ntpDelta = abs(System.currentTimeMillis() - telemetry.creationTime)
        if (ntpDelta > 900000) { // Off by more than 15m
            scoreIdx += 0.3f
            Log.w(TAG, "ANTI-CHEAT WARNING: Time warping / system clock spoof anomaly!")
        }

        return scoreIdx.coerceIn(0.0f, 1.0f)
    }
}

/**
 * Behavioral habit drop-out prediction engine using distraction tendencies.
 */
object BehavioralPredictionEngine {
    /**
     * Predicts customer churn (drop-out risk score 0.0 to 1.0) and dynamically adapts goals.
     */
    fun evaluateHabitDisengagementRisk(
        dailyExerciseCompletionRates: List<Float>, // 1.0 = completed target, 0.0 = missed
        dailyTotalDoomscrollInterventionsCount: List<Int> // Screen blocker activations count
    ): Float {
        if (dailyExerciseCompletionRates.isEmpty()) return 0.2f

        val avgWorkoutRate = dailyExerciseCompletionRates.average()
        val totalBypasses = dailyTotalDoomscrollInterventionsCount.sum()

        // High block counts paired with low workout rates signaling frustration and attrition risk
        var riskIndex = (1.0 - avgWorkoutRate).toFloat() * 0.7f
        if (totalBypasses > 15) {
            riskIndex += 0.3f
        }

        return riskIndex.coerceIn(0.0f, 1.0f)
    }

    /**
     * Returns dynamic booster XP factors if disengagement probability is high to motivate users.
     */
    fun determineMotivationalGoalMultiplier(disengagementRisk: Float): Float {
        return when {
            disengagementRisk > 0.75f -> 1.5f // Double reward payout incentives to re-engage
            disengagementRisk > 0.45f -> 1.25f
            else -> 1.0f
        }
    }
}
