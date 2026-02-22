package com.example.mindsync.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutTest {

    @Test
    fun `workout creates with default values`() {
        val workout = Workout()
        
        assertNotNull(workout.id)
        assertEquals("", workout.userId)
        assertEquals("", workout.name)
        assertEquals(WorkoutCategory.STRENGTH, workout.category)
        assertEquals(emptyList<Exercise>(), workout.exercises)
        assertEquals(0, workout.durationMinutes)
        assertFalse(workout.isCustom)
        assertFalse(workout.reminderEnabled)
    }

    @Test
    fun `workout creates with custom values`() {
        val exercises = listOf(
            Exercise(name = "Bench Press", muscleGroup = MuscleGroup.CHEST, sets = 4, reps = 10)
        )
        
        val workout = Workout(
            id = "workout-1",
            userId = "user-1",
            name = "Chest Day",
            description = "Build a strong chest",
            category = WorkoutCategory.STRENGTH,
            exercises = exercises,
            durationMinutes = 45,
            caloriesBurned = 300,
            isCustom = true,
            reminderEnabled = true
        )
        
        assertEquals("workout-1", workout.id)
        assertEquals("user-1", workout.userId)
        assertEquals("Chest Day", workout.name)
        assertEquals("Build a strong chest", workout.description)
        assertEquals(WorkoutCategory.STRENGTH, workout.category)
        assertEquals(1, workout.exercises.size)
        assertEquals(45, workout.durationMinutes)
        assertEquals(300, workout.caloriesBurned)
        assertTrue(workout.isCustom)
        assertTrue(workout.reminderEnabled)
    }

    @Test
    fun `exercise creates correctly`() {
        val exercise = Exercise(
            id = "ex-1",
            name = "Bench Press",
            description = "Chest exercise",
            muscleGroup = MuscleGroup.CHEST,
            sets = 4,
            reps = 10,
            weight = 60f,
            weightUnit = WeightUnit.KG,
            restSeconds = 90
        )
        
        assertEquals("ex-1", exercise.id)
        assertEquals("Bench Press", exercise.name)
        assertEquals(MuscleGroup.CHEST, exercise.muscleGroup)
        assertEquals(4, exercise.sets)
        assertEquals(10, exercise.reps)
        assertEquals(60f, exercise.weight)
        assertEquals(WeightUnit.KG, exercise.weightUnit)
        assertEquals(90, exercise.restSeconds)
    }

    @Test
    fun `workoutCategory has correct display names`() {
        assertEquals("Strength", WorkoutCategory.STRENGTH.displayName)
        assertEquals("Cardio", WorkoutCategory.CARDIO.displayName)
        assertEquals("Flexibility", WorkoutCategory.FLEXIBILITY.displayName)
        assertEquals("HIIT", WorkoutCategory.HIIT.displayName)
        assertEquals("Yoga", WorkoutCategory.YOGA.displayName)
        assertEquals("CrossFit", WorkoutCategory.CROSSFIT.displayName)
        assertEquals("Bodyweight", WorkoutCategory.BODYWEIGHT.displayName)
        assertEquals("Powerlifting", WorkoutCategory.POWERLIFTING.displayName)
    }

    @Test
    fun `muscleGroup has correct display names`() {
        assertEquals("Chest", MuscleGroup.CHEST.displayName)
        assertEquals("Back", MuscleGroup.BACK.displayName)
        assertEquals("Shoulders", MuscleGroup.SHOULDERS.displayName)
        assertEquals("Biceps", MuscleGroup.BICEPS.displayName)
        assertEquals("Triceps", MuscleGroup.TRICEPS.displayName)
        assertEquals("Legs", MuscleGroup.LEGS.displayName)
        assertEquals("Quadriceps", MuscleGroup.QUADRICEPS.displayName)
        assertEquals("Hamstrings", MuscleGroup.HAMSTRINGS.displayName)
        assertEquals("Glutes", MuscleGroup.GLUTES.displayName)
        assertEquals("Abs", MuscleGroup.ABS.displayName)
        assertEquals("Core", MuscleGroup.CORE.displayName)
        assertEquals("Full Body", MuscleGroup.FULL_BODY.displayName)
    }

    @Test
    fun `weightUnit has correct symbols`() {
        assertEquals("kg", WeightUnit.KG.symbol)
        assertEquals("lbs", WeightUnit.LBS.symbol)
    }

    @Test
    fun `workoutSession creates correctly`() {
        val exerciseLogs = listOf(
            ExerciseLog(
                exerciseId = "ex-1",
                exerciseName = "Bench Press",
                sets = listOf(
                    SetLog(setNumber = 1, reps = 10, weight = 60f, isCompleted = true)
                ),
                isCompleted = true
            )
        )
        
        val session = WorkoutSession(
            id = "session-1",
            workoutId = "workout-1",
            userId = "user-1",
            exerciseLogs = exerciseLogs,
            totalDurationMinutes = 45,
            totalCaloriesBurned = 300,
            notes = "Great workout!"
        )
        
        assertEquals("session-1", session.id)
        assertEquals("workout-1", session.workoutId)
        assertEquals(1, session.exerciseLogs.size)
        assertEquals(45, session.totalDurationMinutes)
        assertEquals(300, session.totalCaloriesBurned)
        assertEquals("Great workout!", session.notes)
    }

    @Test
    fun `progressPeriod has correct values`() {
        assertEquals("Weekly", ProgressPeriod.WEEKLY.displayName)
        assertEquals(7, ProgressPeriod.WEEKLY.days)
        assertEquals("Monthly", ProgressPeriod.MONTHLY.displayName)
        assertEquals(30, ProgressPeriod.MONTHLY.days)
        assertEquals("Yearly", ProgressPeriod.YEARLY.displayName)
        assertEquals(365, ProgressPeriod.YEARLY.days)
    }

    @Test
    fun `workoutProgress creates with defaults`() {
        val progress = WorkoutProgress()
        
        assertEquals(ProgressPeriod.WEEKLY, progress.period)
        assertEquals(0, progress.totalWorkouts)
        assertEquals(0, progress.totalMinutes)
        assertEquals(0, progress.totalCalories)
        assertEquals(0, progress.currentStreak)
    }

    @Test
    fun `workoutStats creates with defaults`() {
        val stats = WorkoutStats()
        
        assertEquals(0, stats.totalWorkouts)
        assertEquals(0, stats.totalMinutes)
        assertEquals(0, stats.totalCalories)
        assertEquals(0, stats.currentStreak)
    }
}
