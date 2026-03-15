package com.example.mindsync.data.repository

import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.model.ExerciseLog
import com.example.mindsync.domain.model.MuscleGroup
import com.example.mindsync.domain.model.ProgressPeriod
import com.example.mindsync.domain.model.SetLog
import com.example.mindsync.domain.model.WeightUnit
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.domain.model.WorkoutProgress
import com.example.mindsync.domain.model.WorkoutReminder
import com.example.mindsync.domain.model.WorkoutSession
import com.example.mindsync.domain.model.WorkoutStats
import com.example.mindsync.domain.repository.WorkoutRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class WorkoutRepositoryImpl(
    private val firestore: FirebaseFirestore
) : WorkoutRepository {

    private val workoutsCollection = firestore.collection("workouts")
    private val exercisesCollection = firestore.collection("exercises")
    private val sessionsCollection = firestore.collection("workout_sessions")
    private val remindersCollection = firestore.collection("workout_reminders")

    override fun getWorkouts(userId: String): Flow<List<Workout>> = callbackFlow {
        val listener = workoutsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val workouts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toWorkout()
                } ?: emptyList()
                trySend(workouts)
            }
        awaitClose { listener.remove() }
    }

    override fun getWorkoutById(id: String): Flow<Workout?> = callbackFlow {
        val listener = workoutsCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toWorkout())
            }
        awaitClose { listener.remove() }
    }

    override fun getDefaultWorkouts(): Flow<List<Workout>> = flow {
        emit(getDefaultWorkoutsList())
    }

    override suspend fun addWorkout(workout: Workout): Result<Workout> = runCatching {
        val docRef = workoutsCollection.document(workout.id)
        docRef.set(workout.toMap()).await()
        workout
    }

    override suspend fun updateWorkout(workout: Workout): Result<Workout> = runCatching {
        val updated = workout.copy(updatedAt = System.currentTimeMillis())
        workoutsCollection.document(workout.id).set(updated.toMap()).await()
        updated
    }

    override suspend fun deleteWorkout(id: String): Result<Unit> = runCatching {
        workoutsCollection.document(id).delete().await()
    }

    override fun getExercises(): Flow<List<Exercise>> = flow {
        emit(getDefaultExercises())
    }

    override fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>> = flow {
        val exercises = getDefaultExercises().filter { 
            it.muscleGroup.name.equals(muscleGroup, ignoreCase = true) 
        }
        emit(exercises)
    }

    override fun getWorkoutSessions(userId: String): Flow<List<WorkoutSession>> = callbackFlow {
        val listener = sessionsCollection
            .whereEqualTo("userId", userId)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toWorkoutSession()
                } ?: emptyList()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    override fun getWorkoutSessionsByWorkoutId(workoutId: String): Flow<List<WorkoutSession>> = callbackFlow {
        val listener = sessionsCollection
            .whereEqualTo("workoutId", workoutId)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toWorkoutSession()
                } ?: emptyList()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addWorkoutSession(session: WorkoutSession): Result<WorkoutSession> = runCatching {
        val docRef = sessionsCollection.document(session.id)
        docRef.set(session.toMap()).await()
        session
    }

    override fun getWorkoutReminders(userId: String): Flow<List<WorkoutReminder>> = callbackFlow {
        val listener = remindersCollection
            .whereEqualTo("userId", userId)
            .orderBy("scheduledTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reminders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toWorkoutReminder()
                } ?: emptyList()
                trySend(reminders)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addWorkoutReminder(reminder: WorkoutReminder): Result<WorkoutReminder> = runCatching {
        val docRef = remindersCollection.document(reminder.id)
        docRef.set(reminder.toMap()).await()
        reminder
    }

    override suspend fun updateWorkoutReminder(reminder: WorkoutReminder): Result<WorkoutReminder> = runCatching {
        remindersCollection.document(reminder.id).set(reminder.toMap()).await()
        reminder
    }

    override suspend fun deleteWorkoutReminder(id: String): Result<Unit> = runCatching {
        remindersCollection.document(id).delete().await()
    }

    override fun getWorkoutProgress(userId: String, period: ProgressPeriod): Flow<WorkoutProgress> = callbackFlow {
        val cutoffTime = System.currentTimeMillis() - (period.days * 24 * 60 * 60 * 1000L)
        val listener = sessionsCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThan("completedAt", cutoffTime)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.documents?.mapNotNull { it.toWorkoutSession() } ?: emptyList()
                val progress = calculateProgress(sessions, period)
                trySend(progress)
            }
        awaitClose { listener.remove() }
    }

    override fun getWorkoutStats(userId: String): Flow<WorkoutStats> = callbackFlow {
        val listener = sessionsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.documents?.mapNotNull { it.toWorkoutSession() } ?: emptyList()
                val stats = calculateStats(sessions)
                trySend(stats)
            }
        awaitClose { listener.remove() }
    }

    private fun calculateProgress(sessions: List<WorkoutSession>, period: ProgressPeriod): WorkoutProgress {
        val totalWorkouts = sessions.size
        val totalMinutes = sessions.sumOf { it.totalDurationMinutes }
        val totalCalories = sessions.sumOf { it.totalCaloriesBurned }
        val workoutsByDay = sessions.groupBy { it.completedAt / (24 * 60 * 60 * 1000) }
            .mapValues { it.value.size }
            .mapKeys { it.key * (24 * 60 * 60 * 1000) }

        return WorkoutProgress(
            period = period,
            totalWorkouts = totalWorkouts,
            totalMinutes = totalMinutes,
            totalCalories = totalCalories,
            workoutsByDay = workoutsByDay
        )
    }

    private fun calculateStats(sessions: List<WorkoutSession>): WorkoutStats {
        if (sessions.isEmpty()) return WorkoutStats()

        val totalMinutes = sessions.sumOf { it.totalDurationMinutes }
        val totalCalories = sessions.sumOf { it.totalCaloriesBurned }

        return WorkoutStats(
            totalWorkouts = sessions.size,
            totalMinutes = totalMinutes,
            totalCalories = totalCalories
        )
    }

    private fun getDefaultWorkoutsList(): List<Workout> = listOf(
        Workout(
            id = "default_chest_day",
            name = "Chest Day",
            description = "Complete chest workout for building strength",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Bench Press", muscleGroup = MuscleGroup.CHEST, sets = 4, reps = 10),
                Exercise(name = "Incline Dumbbell Press", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 12),
                Exercise(name = "Cable Flyes", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 15),
                Exercise(name = "Push-ups", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 20)
            ),
            durationMinutes = 45,
            caloriesBurned = 300,
            imageUrl = "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400"
        ),
        Workout(
            id = "default_back_day",
            name = "Back Day",
            description = "Build a strong and defined back",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Deadlift", muscleGroup = MuscleGroup.BACK, sets = 4, reps = 8),
                Exercise(name = "Pull-ups", muscleGroup = MuscleGroup.BACK, sets = 4, reps = 10),
                Exercise(name = "Barbell Rows", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 12),
                Exercise(name = "Lat Pulldown", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 12)
            ),
            durationMinutes = 50,
            caloriesBurned = 350,
            imageUrl = "https://images.unsplash.com/photo-1603287681836-b174ce5074c2?w=400"
        ),
        Workout(
            id = "default_leg_day",
            name = "Leg Day",
            description = "Build powerful legs and glutes",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Squats", muscleGroup = MuscleGroup.LEGS, sets = 4, reps = 10),
                Exercise(name = "Leg Press", muscleGroup = MuscleGroup.QUADRICEPS, sets = 3, reps = 12),
                Exercise(name = "Romanian Deadlift", muscleGroup = MuscleGroup.HAMSTRINGS, sets = 3, reps = 10),
                Exercise(name = "Calf Raises", muscleGroup = MuscleGroup.CALVES, sets = 4, reps = 15)
            ),
            durationMinutes = 55,
            caloriesBurned = 400,
            imageUrl = "https://images.unsplash.com/photo-1434608519344-49d77a699e1d?w=400"
        ),
        Workout(
            id = "default_hiit",
            name = "HIIT Cardio",
            description = "High intensity interval training for fat burn",
            category = WorkoutCategory.HIIT,
            exercises = listOf(
                Exercise(name = "Burpees", muscleGroup = MuscleGroup.FULL_BODY, sets = 4, reps = 15),
                Exercise(name = "Mountain Climbers", muscleGroup = MuscleGroup.CORE, sets = 4, reps = 30),
                Exercise(name = "Jump Squats", muscleGroup = MuscleGroup.LEGS, sets = 4, reps = 20),
                Exercise(name = "High Knees", muscleGroup = MuscleGroup.CARDIO, sets = 4, reps = 30)
            ),
            durationMinutes = 30,
            caloriesBurned = 450,
            imageUrl = "https://images.unsplash.com/photo-1601422407692-ec4eeec1d9b3?w=400"
        ),
        Workout(
            id = "default_full_body",
            name = "Full Body Workout",
            description = "Complete full body strength training",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Squats", muscleGroup = MuscleGroup.LEGS, sets = 3, reps = 12),
                Exercise(name = "Bench Press", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 10),
                Exercise(name = "Bent Over Rows", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 12),
                Exercise(name = "Shoulder Press", muscleGroup = MuscleGroup.SHOULDERS, sets = 3, reps = 10),
                Exercise(name = "Plank", muscleGroup = MuscleGroup.CORE, sets = 3, reps = 60)
            ),
            durationMinutes = 60,
            caloriesBurned = 500,
            imageUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400"
        ),
        Workout(
            id = "default_yoga_flow",
            name = "Yoga Flow",
            description = "Relaxing yoga session for flexibility and mindfulness",
            category = WorkoutCategory.YOGA,
            exercises = listOf(
                Exercise(name = "Sun Salutation", muscleGroup = MuscleGroup.FULL_BODY, sets = 3, reps = 5),
                Exercise(name = "Warrior Pose", muscleGroup = MuscleGroup.LEGS, sets = 2, reps = 30),
                Exercise(name = "Downward Dog", muscleGroup = MuscleGroup.FULL_BODY, sets = 3, reps = 30),
                Exercise(name = "Child's Pose", muscleGroup = MuscleGroup.BACK, sets = 2, reps = 60),
                Exercise(name = "Cobra Stretch", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 20)
            ),
            durationMinutes = 40,
            caloriesBurned = 180,
            imageUrl = "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400"
        ),
        Workout(
            id = "default_cardio_blast",
            name = "Cardio Blast",
            description = "Get your heart pumping with this cardio routine",
            category = WorkoutCategory.CARDIO,
            exercises = listOf(
                Exercise(name = "Jumping Jacks", muscleGroup = MuscleGroup.CARDIO, sets = 4, reps = 30),
                Exercise(name = "Running in Place", muscleGroup = MuscleGroup.CARDIO, sets = 4, reps = 60),
                Exercise(name = "Box Jumps", muscleGroup = MuscleGroup.LEGS, sets = 3, reps = 15),
                Exercise(name = "Speed Skaters", muscleGroup = MuscleGroup.LEGS, sets = 4, reps = 20),
                Exercise(name = "Jump Rope", muscleGroup = MuscleGroup.CARDIO, sets = 4, reps = 50)
            ),
            durationMinutes = 35,
            caloriesBurned = 400,
            imageUrl = "https://images.unsplash.com/photo-1538805060514-97d9cc17730c?w=400"
        ),
        Workout(
            id = "default_arm_day",
            name = "Arm Day",
            description = "Build bigger biceps and triceps",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Bicep Curls", muscleGroup = MuscleGroup.BICEPS, sets = 4, reps = 12),
                Exercise(name = "Hammer Curls", muscleGroup = MuscleGroup.BICEPS, sets = 3, reps = 12),
                Exercise(name = "Tricep Pushdowns", muscleGroup = MuscleGroup.TRICEPS, sets = 4, reps = 12),
                Exercise(name = "Skull Crushers", muscleGroup = MuscleGroup.TRICEPS, sets = 3, reps = 10),
                Exercise(name = "Close Grip Bench", muscleGroup = MuscleGroup.TRICEPS, sets = 3, reps = 10)
            ),
            durationMinutes = 40,
            caloriesBurned = 250,
            imageUrl = "https://images.unsplash.com/photo-1581009137042-c552e485697a?w=400"
        ),
        Workout(
            id = "default_shoulder_day",
            name = "Shoulder Sculpt",
            description = "Define and strengthen your shoulders",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Military Press", muscleGroup = MuscleGroup.SHOULDERS, sets = 4, reps = 10),
                Exercise(name = "Lateral Raises", muscleGroup = MuscleGroup.SHOULDERS, sets = 4, reps = 15),
                Exercise(name = "Front Raises", muscleGroup = MuscleGroup.SHOULDERS, sets = 3, reps = 12),
                Exercise(name = "Rear Delt Flyes", muscleGroup = MuscleGroup.SHOULDERS, sets = 3, reps = 15),
                Exercise(name = "Shrugs", muscleGroup = MuscleGroup.SHOULDERS, sets = 4, reps = 15)
            ),
            durationMinutes = 45,
            caloriesBurned = 280,
            imageUrl = "https://images.unsplash.com/photo-1581009146145-b5ef050c149a?w=400"
        ),
        Workout(
            id = "default_core_crusher",
            name = "Core Crusher",
            description = "Strengthen your abs and core muscles",
            category = WorkoutCategory.BODYWEIGHT,
            exercises = listOf(
                Exercise(name = "Crunches", muscleGroup = MuscleGroup.ABS, sets = 4, reps = 25),
                Exercise(name = "Plank", muscleGroup = MuscleGroup.CORE, sets = 3, reps = 60),
                Exercise(name = "Russian Twists", muscleGroup = MuscleGroup.ABS, sets = 3, reps = 30),
                Exercise(name = "Leg Raises", muscleGroup = MuscleGroup.ABS, sets = 3, reps = 15),
                Exercise(name = "Mountain Climbers", muscleGroup = MuscleGroup.CORE, sets = 3, reps = 30),
                Exercise(name = "Bicycle Crunches", muscleGroup = MuscleGroup.ABS, sets = 3, reps = 30)
            ),
            durationMinutes = 25,
            caloriesBurned = 200,
            imageUrl = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400"
        ),
        Workout(
            id = "default_flexibility",
            name = "Flexibility & Stretch",
            description = "Improve flexibility and reduce muscle tension",
            category = WorkoutCategory.FLEXIBILITY,
            exercises = listOf(
                Exercise(name = "Hamstring Stretch", muscleGroup = MuscleGroup.HAMSTRINGS, sets = 2, reps = 30),
                Exercise(name = "Quad Stretch", muscleGroup = MuscleGroup.QUADRICEPS, sets = 2, reps = 30),
                Exercise(name = "Hip Flexor Stretch", muscleGroup = MuscleGroup.LEGS, sets = 2, reps = 30),
                Exercise(name = "Shoulder Stretch", muscleGroup = MuscleGroup.SHOULDERS, sets = 2, reps = 30),
                Exercise(name = "Cat-Cow Stretch", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 10),
                Exercise(name = "Pigeon Pose", muscleGroup = MuscleGroup.GLUTES, sets = 2, reps = 30)
            ),
            durationMinutes = 20,
            caloriesBurned = 80,
            imageUrl = "https://images.unsplash.com/photo-1552196563-55cd4e45efb3?w=400"
        ),
        Workout(
            id = "default_push_day",
            name = "Push Day",
            description = "Chest, shoulders, and triceps workout",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Bench Press", muscleGroup = MuscleGroup.CHEST, sets = 4, reps = 10),
                Exercise(name = "Overhead Press", muscleGroup = MuscleGroup.SHOULDERS, sets = 4, reps = 10),
                Exercise(name = "Incline Press", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 12),
                Exercise(name = "Lateral Raises", muscleGroup = MuscleGroup.SHOULDERS, sets = 3, reps = 15),
                Exercise(name = "Tricep Dips", muscleGroup = MuscleGroup.TRICEPS, sets = 3, reps = 12)
            ),
            durationMinutes = 50,
            caloriesBurned = 350,
            imageUrl = "https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=400"
        ),
        Workout(
            id = "default_pull_day",
            name = "Pull Day",
            description = "Back and biceps focused workout",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Deadlift", muscleGroup = MuscleGroup.BACK, sets = 4, reps = 8),
                Exercise(name = "Pull-ups", muscleGroup = MuscleGroup.BACK, sets = 4, reps = 10),
                Exercise(name = "Barbell Rows", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 12),
                Exercise(name = "Face Pulls", muscleGroup = MuscleGroup.SHOULDERS, sets = 3, reps = 15),
                Exercise(name = "Bicep Curls", muscleGroup = MuscleGroup.BICEPS, sets = 3, reps = 12)
            ),
            durationMinutes = 50,
            caloriesBurned = 380,
            imageUrl = "https://images.unsplash.com/photo-1598971639058-fab3c3109a00?w=400"
        ),
        Workout(
            id = "default_glute_focus",
            name = "Glute Builder",
            description = "Build and shape your glutes",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Hip Thrusts", muscleGroup = MuscleGroup.GLUTES, sets = 4, reps = 12),
                Exercise(name = "Bulgarian Split Squats", muscleGroup = MuscleGroup.GLUTES, sets = 3, reps = 10),
                Exercise(name = "Romanian Deadlift", muscleGroup = MuscleGroup.HAMSTRINGS, sets = 3, reps = 12),
                Exercise(name = "Cable Kickbacks", muscleGroup = MuscleGroup.GLUTES, sets = 3, reps = 15),
                Exercise(name = "Glute Bridges", muscleGroup = MuscleGroup.GLUTES, sets = 3, reps = 20)
            ),
            durationMinutes = 45,
            caloriesBurned = 320,
            imageUrl = "https://images.unsplash.com/photo-1574680178050-55c6a6a96e0a?w=400"
        ),
        Workout(
            id = "default_tabata",
            name = "Tabata Training",
            description = "20 seconds work, 10 seconds rest - maximum intensity",
            category = WorkoutCategory.HIIT,
            exercises = listOf(
                Exercise(name = "Burpees", muscleGroup = MuscleGroup.FULL_BODY, sets = 8, reps = 20),
                Exercise(name = "Squat Jumps", muscleGroup = MuscleGroup.LEGS, sets = 8, reps = 20),
                Exercise(name = "Push-ups", muscleGroup = MuscleGroup.CHEST, sets = 8, reps = 20),
                Exercise(name = "Mountain Climbers", muscleGroup = MuscleGroup.CORE, sets = 8, reps = 20)
            ),
            durationMinutes = 20,
            caloriesBurned = 350,
            imageUrl = "https://images.unsplash.com/photo-1549576490-b0b4831ef60a?w=400"
        )
    )

    private fun getDefaultExercises(): List<Exercise> = listOf(
        Exercise(id = "ex_bench_press", name = "Bench Press", muscleGroup = MuscleGroup.CHEST, sets = 4, reps = 10, imageUrl = "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=200"),
        Exercise(id = "ex_squat", name = "Squats", muscleGroup = MuscleGroup.LEGS, sets = 4, reps = 10, imageUrl = "https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=200"),
        Exercise(id = "ex_deadlift", name = "Deadlift", muscleGroup = MuscleGroup.BACK, sets = 4, reps = 8, imageUrl = "https://images.unsplash.com/photo-1603287681836-b174ce5074c2?w=200"),
        Exercise(id = "ex_pullup", name = "Pull-ups", muscleGroup = MuscleGroup.BACK, sets = 4, reps = 10, imageUrl = "https://images.unsplash.com/photo-1598971639058-fab3c3109a00?w=200"),
        Exercise(id = "ex_shoulder_press", name = "Shoulder Press", muscleGroup = MuscleGroup.SHOULDERS, sets = 3, reps = 12, imageUrl = "https://images.unsplash.com/photo-1581009146145-b5ef050c149a?w=200"),
        Exercise(id = "ex_bicep_curl", name = "Bicep Curls", muscleGroup = MuscleGroup.BICEPS, sets = 3, reps = 12, imageUrl = "https://images.unsplash.com/photo-1581009137042-c552e485697a?w=200"),
        Exercise(id = "ex_tricep_dip", name = "Tricep Dips", muscleGroup = MuscleGroup.TRICEPS, sets = 3, reps = 15, imageUrl = "https://images.unsplash.com/photo-1530822847156-5df684ec5ee1?w=200"),
        Exercise(id = "ex_leg_press", name = "Leg Press", muscleGroup = MuscleGroup.QUADRICEPS, sets = 3, reps = 12, imageUrl = "https://images.unsplash.com/photo-1434608519344-49d77a699e1d?w=200"),
        Exercise(id = "ex_plank", name = "Plank", muscleGroup = MuscleGroup.CORE, sets = 3, reps = 60, imageUrl = "https://images.unsplash.com/photo-1566241142559-40e1dab266c6?w=200"),
        Exercise(id = "ex_burpees", name = "Burpees", muscleGroup = MuscleGroup.FULL_BODY, sets = 4, reps = 15, imageUrl = "https://images.unsplash.com/photo-1601422407692-ec4eeec1d9b3?w=200")
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.toWorkout(): Workout? {
        return try {
            @Suppress("UNCHECKED_CAST")
            val exercisesList = (get("exercises") as? List<Map<String, Any>>)?.map { exerciseMap ->
                Exercise(
                    id = exerciseMap["id"] as? String ?: "",
                    name = exerciseMap["name"] as? String ?: "",
                    muscleGroup = MuscleGroup.valueOf(exerciseMap["muscleGroup"] as? String ?: "CHEST"),
                    sets = (exerciseMap["sets"] as? Long)?.toInt() ?: 3,
                    reps = (exerciseMap["reps"] as? Long)?.toInt() ?: 10,
                    weight = (exerciseMap["weight"] as? Double)?.toFloat() ?: 0f,
                    weightUnit = WeightUnit.valueOf(exerciseMap["weightUnit"] as? String ?: "KG"),
                    restSeconds = (exerciseMap["restSeconds"] as? Long)?.toInt() ?: 60,
                    imageUrl = exerciseMap["imageUrl"] as? String ?: "",
                    notes = exerciseMap["notes"] as? String ?: "",
                    isWithWeight = exerciseMap["isWithWeight"] as? Boolean ?: true
                )
            } ?: emptyList()

            Workout(
                id = id,
                userId = getString("userId") ?: "",
                name = getString("name") ?: "",
                description = getString("description") ?: "",
                category = WorkoutCategory.valueOf(getString("category") ?: "STRENGTH"),
                exercises = exercisesList,
                durationMinutes = getLong("durationMinutes")?.toInt() ?: 0,
                caloriesBurned = getLong("caloriesBurned")?.toInt() ?: 0,
                imageUrl = getString("imageUrl") ?: "",
                isCustom = getBoolean("isCustom") ?: false,
                reminderTime = getLong("reminderTime"),
                reminderEnabled = getBoolean("reminderEnabled") ?: false,
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toWorkoutSession(): WorkoutSession? {
        return try {
            @Suppress("UNCHECKED_CAST")
            val exerciseLogsList = (get("exerciseLogs") as? List<Map<String, Any>>)?.map { logMap ->
                val setLogs = (logMap["sets"] as? List<Map<String, Any>>)?.map { setMap ->
                    SetLog(
                        setNumber = (setMap["setNumber"] as? Long)?.toInt() ?: 0,
                        reps = (setMap["reps"] as? Long)?.toInt() ?: 0,
                        weight = (setMap["weight"] as? Double)?.toFloat() ?: 0f,
                        isCompleted = setMap["isCompleted"] as? Boolean ?: false
                    )
                } ?: emptyList()

                ExerciseLog(
                    exerciseId = logMap["exerciseId"] as? String ?: "",
                    exerciseName = logMap["exerciseName"] as? String ?: "",
                    sets = setLogs,
                    isCompleted = logMap["isCompleted"] as? Boolean ?: false
                )
            } ?: emptyList()

            WorkoutSession(
                id = id,
                workoutId = getString("workoutId") ?: "",
                userId = getString("userId") ?: "",
                exerciseLogs = exerciseLogsList,
                totalDurationMinutes = getLong("totalDurationMinutes")?.toInt() ?: 0,
                totalCaloriesBurned = getLong("totalCaloriesBurned")?.toInt() ?: 0,
                completedAt = getLong("completedAt") ?: System.currentTimeMillis(),
                notes = getString("notes") ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toWorkoutReminder(): WorkoutReminder? {
        return try {
            @Suppress("UNCHECKED_CAST")
            WorkoutReminder(
                id = id,
                workoutId = getString("workoutId") ?: "",
                userId = getString("userId") ?: "",
                title = getString("title") ?: "",
                scheduledTime = getLong("scheduledTime") ?: 0,
                repeatDays = (get("repeatDays") as? List<Long>)?.map { it.toInt() } ?: emptyList(),
                isEnabled = getBoolean("isEnabled") ?: true,
                notes = getString("notes") ?: "",
                createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Workout.toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "name" to name,
        "description" to description,
        "category" to category.name,
        "exercises" to exercises.map { it.toMap() },
        "durationMinutes" to durationMinutes,
        "caloriesBurned" to caloriesBurned,
        "imageUrl" to imageUrl,
        "isCustom" to isCustom,
        "reminderTime" to reminderTime,
        "reminderEnabled" to reminderEnabled,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    private fun Exercise.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "description" to description,
        "muscleGroup" to muscleGroup.name,
        "sets" to sets,
        "reps" to reps,
        "weight" to weight,
        "weightUnit" to weightUnit.name,
        "restSeconds" to restSeconds,
        "imageUrl" to imageUrl,
        "notes" to notes,
        "isWithWeight" to isWithWeight
    )

    private fun WorkoutSession.toMap(): Map<String, Any?> = mapOf(
        "workoutId" to workoutId,
        "userId" to userId,
        "exerciseLogs" to exerciseLogs.map { log ->
            mapOf(
                "exerciseId" to log.exerciseId,
                "exerciseName" to log.exerciseName,
                "sets" to log.sets.map { set ->
                    mapOf(
                        "setNumber" to set.setNumber,
                        "reps" to set.reps,
                        "weight" to set.weight,
                        "isCompleted" to set.isCompleted
                    )
                },
                "isCompleted" to log.isCompleted
            )
        },
        "totalDurationMinutes" to totalDurationMinutes,
        "totalCaloriesBurned" to totalCaloriesBurned,
        "completedAt" to completedAt,
        "notes" to notes
    )

    private fun WorkoutReminder.toMap(): Map<String, Any?> = mapOf(
        "workoutId" to workoutId,
        "userId" to userId,
        "title" to title,
        "scheduledTime" to scheduledTime,
        "repeatDays" to repeatDays,
        "isEnabled" to isEnabled,
        "notes" to notes,
        "createdAt" to createdAt
    )
}
