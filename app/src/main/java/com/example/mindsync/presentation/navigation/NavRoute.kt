package com.example.mindsync.presentation.navigation

sealed class NavRoute(val route: String) {
    data object Splash : NavRoute("splash")
    data object Login : NavRoute("login")
    data object OtpVerification : NavRoute("otp_verification/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "otp_verification/$phoneNumber"
    }
    data object Dashboard : NavRoute("dashboard")
    
    data object Profile : NavRoute("profile")
    data object Settings : NavRoute("settings")
    data object Insights : NavRoute("insights")
    data object ReminderList : NavRoute("reminder_list")
    
    data object Meditation : NavRoute("meditation")
    data object AddMeditation : NavRoute("add_meditation")
    data object MeditationDetail : NavRoute("meditation_detail/{meditationId}") {
        fun createRoute(meditationId: String) = "meditation_detail/$meditationId"
    }
    data object MeditationReminder : NavRoute("meditation_reminder/{meditationId}") {
        fun createRoute(meditationId: String?) = "meditation_reminder/${meditationId ?: "new"}"
    }
    
    data object Workout : NavRoute("workout")
    data object AddWorkout : NavRoute("add_workout")
    data object WorkoutDetail : NavRoute("workout_detail/{workoutId}") {
        fun createRoute(workoutId: String) = "workout_detail/$workoutId"
    }
    data object WorkoutReminder : NavRoute("workout_reminder/{workoutId}") {
        fun createRoute(workoutId: String?) = "workout_reminder/${workoutId ?: "new"}"
    }
    data object WorkoutProgress : NavRoute("workout_progress")
    data object LogWorkout : NavRoute("log_workout")
    data object AddExercise : NavRoute("add_exercise")
    
    data object Medicine : NavRoute("medicine")
    data object AddMedicine : NavRoute("add_medicine")
    data object MedicineDetail : NavRoute("medicine_detail/{medicineId}") {
        fun createRoute(medicineId: String) = "medicine_detail/$medicineId"
    }
    
    data object Skincare : NavRoute("skincare")
    data object AddSkincare : NavRoute("add_skincare")
    data object SkincareDetail : NavRoute("skincare_detail/{routineId}") {
        fun createRoute(routineId: String) = "skincare_detail/$routineId"
    }
    
    // New Features
    data object RemindersHub : NavRoute("reminders_hub")
    data object Grocery : NavRoute("grocery")
    data object Assignments : NavRoute("assignments")
    data object FamilyMedicine : NavRoute("family_medicine")
    data object WaterIntake : NavRoute("water_intake")
    data object Bills : NavRoute("bills")
    data object Birthdays : NavRoute("birthdays")
    data object SleepTracker : NavRoute("sleep_tracker")
    data object Vehicle : NavRoute("vehicle")
    data object ScreenTime : NavRoute("screen_time")
    data object PlantCare : NavRoute("plant_care")
}
