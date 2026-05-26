package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workout_history")
data class WorkoutHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long,
    val routineName: String,
    val category: String,
    val durationSeconds: Int,
    val caloriesBurned: Float
)

@Entity(tableName = "water_log")
data class WaterLogEntity(
    @PrimaryKey val dateKey: String, // Format: "yyyy-MM-dd"
    val amountMl: Int
)

@Entity(tableName = "weight_log")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long,
    val weightKg: Float
)

@Entity(tableName = "custom_workout_logs")
data class CustomWorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMillis: Long,
    val routineName: String,
    val exercisesText: String,
    val sets: Int,
    val reps: Int,
    val notes: String
)

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseName: String,
    val pbValue: Float,
    val metric: String, // "Reps" or "Seconds"
    val dateMillis: Long,
    val notes: String
)

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_history ORDER BY dateMillis DESC")
    fun getAllWorkoutHistory(): Flow<List<WorkoutHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutHistory(history: WorkoutHistoryEntity)

    @Query("DELETE FROM workout_history WHERE id = :id")
    suspend fun deleteWorkoutHistory(id: Int)

    @Query("SELECT * FROM water_log WHERE dateKey = :dateKey")
    fun getWaterLogByDate(dateKey: String): Flow<WaterLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLogEntity)

    @Query("SELECT * FROM weight_log ORDER BY dateMillis ASC")
    fun getAllWeightLogs(): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(weightLog: WeightLogEntity)

    @Query("DELETE FROM weight_log WHERE id = :id")
    suspend fun deleteWeightLog(id: Int)

    @Query("SELECT * FROM custom_workout_logs ORDER BY dateMillis DESC")
    fun getAllCustomWorkoutLogs(): Flow<List<CustomWorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomWorkoutLog(log: CustomWorkoutLogEntity)

    @Query("DELETE FROM custom_workout_logs WHERE id = :id")
    suspend fun deleteCustomWorkoutLog(id: Int)

    @Query("SELECT * FROM personal_records ORDER BY dateMillis ASC")
    fun getAllPersonalRecords(): Flow<List<PersonalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalRecord(record: PersonalRecordEntity)

    @Query("DELETE FROM personal_records WHERE id = :id")
    suspend fun deletePersonalRecord(id: Int)
}

@Database(entities = [WorkoutHistoryEntity::class, WaterLogEntity::class, WeightLogEntity::class, CustomWorkoutLogEntity::class, PersonalRecordEntity::class], version = 2, exportSchema = false)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allWorkouts: Flow<List<WorkoutHistoryEntity>> = workoutDao.getAllWorkoutHistory()
    val allWeightLogs: Flow<List<WeightLogEntity>> = workoutDao.getAllWeightLogs()
    val allCustomWorkoutLogs: Flow<List<CustomWorkoutLogEntity>> = workoutDao.getAllCustomWorkoutLogs()
    val allPersonalRecords: Flow<List<PersonalRecordEntity>> = workoutDao.getAllPersonalRecords()

    fun getWaterLog(dateKey: String): Flow<WaterLogEntity?> = workoutDao.getWaterLogByDate(dateKey)

    suspend fun insertWorkout(history: WorkoutHistoryEntity) = workoutDao.insertWorkoutHistory(history)
    suspend fun deleteWorkout(id: Int) = workoutDao.deleteWorkoutHistory(id)

    suspend fun saveWaterLog(waterLog: WaterLogEntity) = workoutDao.insertWaterLog(waterLog)

    suspend fun saveWeightLog(weightLog: WeightLogEntity) = workoutDao.insertWeightLog(weightLog)
    suspend fun deleteWeightLog(id: Int) = workoutDao.deleteWeightLog(id)

    suspend fun saveCustomWorkoutLog(log: CustomWorkoutLogEntity) = workoutDao.insertCustomWorkoutLog(log)
    suspend fun deleteCustomWorkoutLog(id: Int) = workoutDao.deleteCustomWorkoutLog(id)

    suspend fun savePersonalRecord(record: PersonalRecordEntity) = workoutDao.insertPersonalRecord(record)
    suspend fun deletePersonalRecord(id: Int) = workoutDao.deletePersonalRecord(id)
}
