package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CustomWorkoutLogEntity
import com.example.data.PersonalRecordEntity
import com.example.data.WaterLogEntity
import com.example.data.WeightLogEntity
import com.example.data.WorkoutDatabase
import com.example.data.WorkoutHistoryEntity
import com.example.data.WorkoutRepository
import com.example.data.WorkoutRoutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WorkoutViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val repository: WorkoutRepository
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    // Fetch Date String
    private fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // --- REALTIME BACKEND IDENTITY AND MULTI-DEVICE SYNCS ---
    val currentUserProfile: StateFlow<com.example.backend.UserProfile?> = com.example.backend.AuthService.currentUserProfile
    val userAuthToken: StateFlow<String?> = com.example.backend.AuthService.authToken

    // Realtime Streaks State synced from backend StreakEngine
    private val _streakState = MutableStateFlow(com.example.backend.StreakTelemetryState(userId = ""))
    val streakState: StateFlow<com.example.backend.StreakTelemetryState> = _streakState.asStateFlow()

    fun triggerAuthGuest() {
        com.example.backend.AuthService.loginAsGuest(getApplication())
        syncStreakState()
    }

    fun triggerAuthGoogle(email: String, name: String) {
        com.example.backend.AuthService.loginWithGoogle(getApplication(), "ID_TOKEN_SECURE", email, name)
        syncStreakState()
    }

    fun triggerAuthEmail(email: String, name: String) {
        com.example.backend.AuthService.signupWithEmail(getApplication(), email, name)
        syncStreakState()
    }

    fun triggerLogout() {
        com.example.backend.AuthService.logout(getApplication())
    }

    fun syncStreakState() {
        val user = currentUserProfile.value ?: return
        val currentStreak = com.example.backend.StreakTelemetryState(
            userId = user.uid,
            currentDailyStreak = 7 // Default simulated baseline for beautiful layout stats
        )
        val todayStr = getTodayDateKey()
        val updatedStreak = com.example.backend.StreakEngine.processDailyActivity(
            currentState = currentStreak,
            todayDateStr = todayStr,
            ntpSynchronizedTime = System.currentTimeMillis()
        )
        _streakState.value = updatedStreak
    }

    fun applyDisciplineXP(xp: Int) {
        val user = currentUserProfile.value ?: return
        val updated = com.example.backend.UserService.applyXpRewards(getApplication(), user, xp)
        // Refresh local session
        com.example.backend.AuthService.loadPersistedSession(getApplication())
    }

    init {
        val database = WorkoutDatabase.getDatabase(application)
        repository = WorkoutRepository(database.workoutDao())
        
        // Load persistent authorization session from local preferences on start
        val sessionLoaded = com.example.backend.AuthService.loadPersistedSession(application)
        if (sessionLoaded) {
            syncStreakState()
        }

        // Initialize TTS
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            Log.e("WorkoutViewModel", "Failed to initialize TTS: ${e.message}")
        }

        // Sync periodic screen-time and overlay values from the background DisciplineEngine
        viewModelScope.launch {
            while (true) {
                delay(500)
                _screenTimeRemainingSec.value = com.example.services.DisciplineEngine.screenTimeRemainingSec
                _isOverlayLocked.value = com.example.services.DisciplineEngine.isOverlayLocked
                _blockingEnabled.value = com.example.services.DisciplineEngine.isEnabled
                _violatingAppName.value = com.example.services.DisciplineEngine.currentViolatingAppName
                
                // Keep Focus Score updating dynamically based on screen-time limits
                val timePenalty = (180 - com.example.services.DisciplineEngine.screenTimeRemainingSec).coerceAtLeast(0) / 10
                _focusScore.value = (100 - timePenalty).coerceIn(45, 100)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val result = tts?.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                    speak("Welcome to Home Workout trainer.")
                }
            } catch (e: Exception) {
                Log.e("WorkoutViewModel", "TTS setLanguage failed: ${e.message}")
            }
        }
    }

    fun speak(text: String) {
        if (isTtsReady) {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } catch (e: Exception) {
                Log.e("WorkoutViewModel", "TTS speak failed: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("WorkoutViewModel", "TTS shutdown failed: ${e.message}")
        }
    }

    // --- DB Live Flows ---
    val allWorkouts: StateFlow<List<WorkoutHistoryEntity>> = repository.allWorkouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWeightLogs: StateFlow<List<WeightLogEntity>> = repository.allWeightLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomWorkoutLogs: StateFlow<List<CustomWorkoutLogEntity>> = repository.allCustomWorkoutLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPersonalRecords: StateFlow<List<PersonalRecordEntity>> = repository.allPersonalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentDateKey = MutableStateFlow(getTodayDateKey())
    
    val waterLogToday: StateFlow<WaterLogEntity?> = _currentDateKey.flatMapLatest { key ->
        repository.getWaterLog(key)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- DIGITAL WELLBEING ENGINE BRIDGE ---
    private val _screenTimeRemainingSec = MutableStateFlow(com.example.services.DisciplineEngine.screenTimeRemainingSec)
    val screenTimeRemainingSecFlow: StateFlow<Int> = _screenTimeRemainingSec.asStateFlow()

    private val _isOverlayLocked = MutableStateFlow(com.example.services.DisciplineEngine.isOverlayLocked)
    val isOverlayLocked: StateFlow<Boolean> = _isOverlayLocked.asStateFlow()

    private val _blockingEnabled = MutableStateFlow(com.example.services.DisciplineEngine.isEnabled)
    val blockingEnabled: StateFlow<Boolean> = _blockingEnabled.asStateFlow()

    private val _violatingAppName = MutableStateFlow(com.example.services.DisciplineEngine.currentViolatingAppName)
    val violatingAppName: StateFlow<String> = _violatingAppName.asStateFlow()

    private val _focusScore = MutableStateFlow(88) // Current real-time digital willpower score (0-100)
    val focusScore: StateFlow<Int> = _focusScore.asStateFlow()

    fun updateBlockingEnabled(enabled: Boolean) {
        com.example.services.DisciplineEngine.isEnabled = enabled
        _blockingEnabled.value = enabled
    }

    fun addEarnedScreenTime(sec: Int) {
        com.example.services.DisciplineEngine.addScreenTime(sec)
        _screenTimeRemainingSec.value = com.example.services.DisciplineEngine.screenTimeRemainingSec
        _isOverlayLocked.value = com.example.services.DisciplineEngine.isOverlayLocked
        speak("Discipline reward applied: Unlocked ${sec / 60} minutes of social apps.")
    }

    fun forceLockSimulated() {
        com.example.services.DisciplineEngine.isOverlayLocked = true
        com.example.services.DisciplineEngine.currentViolatingAppName = "Instagram"
        _isOverlayLocked.value = true
        _violatingAppName.value = "Instagram"
    }

    fun dismissLockOverlay() {
        com.example.services.DisciplineEngine.isOverlayLocked = false
        _isOverlayLocked.value = false
    }

    // --- Actions ---
    fun addWater(amountMl: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLog = waterLogToday.value
            val currentAmount = currentLog?.amountMl ?: 0
            val newLog = WaterLogEntity(
                dateKey = _currentDateKey.value,
                amountMl = (currentAmount + amountMl).coerceAtLeast(0)
            )
            repository.saveWaterLog(newLog)
        }
    }

    fun logWeight(weightKg: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val log = WeightLogEntity(
                dateMillis = System.currentTimeMillis(),
                weightKg = weightKg
            )
            repository.saveWeightLog(log)
        }
    }

    fun deleteWeightLog(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWeightLog(id)
        }
    }

    fun logCompletedWorkout(routine: WorkoutRoutine, durationSeconds: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val workout = WorkoutHistoryEntity(
                dateMillis = System.currentTimeMillis(),
                routineName = routine.name,
                category = routine.category,
                durationSeconds = durationSeconds,
                caloriesBurned = routine.totalCalories
            )
            repository.insertWorkout(workout)
        }
    }

    fun deleteWorkoutFromHistory(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWorkout(id)
        }
    }

    fun saveCustomWorkoutLog(routineName: String, exercisesText: String, sets: Int, reps: Int, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val log = CustomWorkoutLogEntity(
                dateMillis = System.currentTimeMillis(),
                routineName = routineName,
                exercisesText = exercisesText,
                sets = sets,
                reps = reps,
                notes = notes
            )
            repository.saveCustomWorkoutLog(log)
        }
    }

    fun deleteCustomWorkoutLog(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCustomWorkoutLog(id)
        }
    }

    fun savePersonalRecord(exerciseName: String, pbValue: Float, metric: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val record = PersonalRecordEntity(
                exerciseName = exerciseName,
                pbValue = pbValue,
                metric = metric,
                dateMillis = System.currentTimeMillis(),
                notes = notes
            )
            repository.savePersonalRecord(record)
        }
    }

    fun deletePersonalRecord(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePersonalRecord(id)
        }
    }

    // --- ACTIVE WORKOUT SESSION STATE ---
    enum class PlayerState {
         NOT_STARTED, READY_PREVIEW, WORKOUT, COOLDOWN, COMPLETION
    }

    private val _activeRoutine = MutableStateFlow<WorkoutRoutine?>(null)
    val activeRoutine = _activeRoutine.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex = _currentExerciseIndex.asStateFlow()

    private val _sessionPlayerState = MutableStateFlow(PlayerState.NOT_STARTED)
    val sessionPlayerState = _sessionPlayerState.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(10)
    val secondsRemaining = _secondsRemaining.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private var timerJob: Job? = null
    private var totalSessionTimeSeconds = 0

    fun startRoutine(routine: WorkoutRoutine) {
        _activeRoutine.value = routine
        _currentExerciseIndex.value = 0
        totalSessionTimeSeconds = 0
        _isPaused.value = false
        triggerReadyState()
    }

    private fun triggerReadyState() {
        val exercises = activeRoutine.value?.exercises ?: return
        val currentIdx = currentExerciseIndex.value
        _sessionPlayerState.value = PlayerState.READY_PREVIEW
        _secondsRemaining.value = 10 // 10s preview ready count
        
        val currentEx = exercises[currentIdx]
        val speakText = "Next up: ${currentEx.name}. Ready, go!"
        speak(speakText)

        startTimer {
            triggerWorkoutState()
        }
    }

    private fun triggerWorkoutState() {
        val exercises = activeRoutine.value?.exercises ?: return
        val currentIdx = currentExerciseIndex.value
        val exercise = exercises[currentIdx]
        
        _sessionPlayerState.value = PlayerState.WORKOUT
        _isPaused.value = false
        
        if (exercise.durationSeconds > 0) {
            _secondsRemaining.value = exercise.durationSeconds
            speak("Begin ${exercise.name} for ${exercise.durationSeconds} seconds.")
            startTimer {
                triggerCooldowneStateOrFinish()
            }
        } else {
            // Reps exercise - users taps Done
            _secondsRemaining.value = 0
            speak("Do ${exercise.reps} repetitions of ${exercise.name}.")
        }
    }

    fun completeRepExercise() {
        if (sessionPlayerState.value == PlayerState.WORKOUT) {
            triggerCooldowneStateOrFinish()
        }
    }

    private fun triggerCooldowneStateOrFinish() {
        val exercises = activeRoutine.value?.exercises ?: return
        val currentIdx = currentExerciseIndex.value
        
        if (currentIdx >= exercises.size - 1) {
            // FINISHED ALL EXERCISES!
            _sessionPlayerState.value = PlayerState.COMPLETION
            speak("Congratulations! You completed the workout.")
            logCompletedWorkout(activeRoutine.value!!, totalSessionTimeSeconds)
        } else {
            // Take dynamic rest break
            _sessionPlayerState.value = PlayerState.COOLDOWN
            _secondsRemaining.value = 15 // 15 seconds rest break
            speak("Standard rest. Next exercise coming up.")
            startTimer {
                _currentExerciseIndex.value = currentExerciseIndex.value + 1
                triggerReadyState()
            }
        }
    }

    fun skipBreak() {
        if (sessionPlayerState.value == PlayerState.COOLDOWN) {
            timerJob?.cancel()
            _currentExerciseIndex.value = currentExerciseIndex.value + 1
            triggerReadyState()
        }
    }

    fun addMoreBreakTime() {
        if (sessionPlayerState.value == PlayerState.COOLDOWN) {
            _secondsRemaining.value = _secondsRemaining.value + 15
            speak("Added 15 seconds of extra rest.")
        }
    }

    fun pauseResume() {
        _isPaused.value = !_isPaused.value
        if (_isPaused.value) {
            speak("Paused.")
        } else {
            speak("Resuming.")
        }
    }

    fun nextExercise() {
        val exercises = activeRoutine.value?.exercises ?: return
        val currentIdx = currentExerciseIndex.value
        timerJob?.cancel()
        
        if (currentIdx >= exercises.size - 1) {
            // Finished
            _sessionPlayerState.value = PlayerState.COMPLETION
            speak("Workout completed.")
            logCompletedWorkout(activeRoutine.value!!, totalSessionTimeSeconds)
        } else {
            _currentExerciseIndex.value = currentIdx + 1
            triggerReadyState()
        }
    }

    fun prevExercise() {
        val currentIdx = currentExerciseIndex.value
        if (currentIdx > 0) {
            timerJob?.cancel()
            _currentExerciseIndex.value = currentIdx - 1
            triggerReadyState()
        }
    }

    fun quitWorkout() {
        timerJob?.cancel()
        _sessionPlayerState.value = PlayerState.NOT_STARTED
        _activeRoutine.value = null
    }

    private fun startTimer(onFinished: () -> Unit) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (secondsRemaining.value > 0) {
                if (!isPaused.value) {
                    delay(1000)
                    _secondsRemaining.value = _secondsRemaining.value - 1
                    totalSessionTimeSeconds += 1
                    
                    val currentSecs = _secondsRemaining.value
                    if (currentSecs in 1..3 && _sessionPlayerState.value == PlayerState.READY_PREVIEW) {
                        speak("$currentSecs")
                    }
                } else {
                    delay(200)
                }
            }
            if (!isPaused.value) {
                onFinished()
            }
        }
    }

    // For manual database state refreshes if needed (eg. Day ticks)
    fun refreshDateKey() {
        _currentDateKey.value = getTodayDateKey()
    }
}

class WorkoutViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
