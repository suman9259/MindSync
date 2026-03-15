package com.example.mindsync.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.domain.model.MuscleGroup
import com.example.mindsync.domain.model.ReminderType
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.presentation.auth.LoginScreen
import com.example.mindsync.presentation.auth.OtpVerificationScreen
import com.example.mindsync.presentation.dashboard.DashboardScreen
import com.example.mindsync.presentation.insights.InsightsScreen
import com.example.mindsync.presentation.meditation.AddMeditationScreen
import com.example.mindsync.presentation.meditation.MeditationDetailScreen
import com.example.mindsync.presentation.meditation.MeditationScreen
import com.example.mindsync.presentation.profile.ProfileScreen
import com.example.mindsync.presentation.reminder.AddReminderScreen
import com.example.mindsync.presentation.reminder.ReminderListScreen
import com.example.mindsync.presentation.settings.SettingsScreen
import com.example.mindsync.presentation.splash.SplashScreen
import com.example.mindsync.presentation.workout.AddExerciseScreen
import com.example.mindsync.presentation.workout.AddWorkoutScreen
import com.example.mindsync.presentation.workout.ExerciseSet
import com.example.mindsync.presentation.workout.LogWorkoutScreen
import com.example.mindsync.presentation.workout.LoggedExercise
import com.example.mindsync.presentation.workout.WorkoutDetailScreen
import com.example.mindsync.presentation.workout.WorkoutIntent
import com.example.mindsync.presentation.workout.WorkoutScreen
import com.example.mindsync.presentation.workout.WorkoutViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.mindsync.presentation.medicine.MedicineScreen
import com.example.mindsync.presentation.medicine.AddMedicineScreen
import com.example.mindsync.presentation.medicine.MedicineDetailScreen
import com.example.mindsync.presentation.skincare.SkincareScreen
import com.example.mindsync.presentation.skincare.AddSkincareScreen
import com.example.mindsync.presentation.skincare.SkincareDetailScreen
import com.example.mindsync.presentation.reminders.RemindersHubScreen
import com.example.mindsync.presentation.grocery.GroceryScreen
import com.example.mindsync.presentation.student.AssignmentsScreen
import com.example.mindsync.presentation.family.FamilyMedicineScreen
import com.example.mindsync.presentation.lifestyle.WaterIntakeScreen
import com.example.mindsync.presentation.lifestyle.BillsScreen
import com.example.mindsync.presentation.lifestyle.BirthdaysScreen
import com.example.mindsync.presentation.lifestyle.SleepTrackerScreen
import com.example.mindsync.presentation.lifestyle.VehicleScreen
import com.example.mindsync.presentation.lifestyle.ScreenTimeScreen
import com.example.mindsync.presentation.lifestyle.PlantCareScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MindSyncNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var pendingWorkoutForDetail by remember { mutableStateOf<Workout?>(null) }

    NavHost(
        navController = navController,
        startDestination = NavRoute.Splash.route,
        modifier = modifier
    ) {
        composable(NavRoute.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(NavRoute.Login.route) {
                        popUpTo(NavRoute.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(NavRoute.Dashboard.route) {
                        popUpTo(NavRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavRoute.Login.route) {
            LoginScreen(
                onNavigateToOtp = { phoneNumber ->
                    navController.navigate(NavRoute.OtpVerification.createRoute(phoneNumber))
                },
                onLoginSuccess = {
                    navController.navigate(NavRoute.Dashboard.route) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = NavRoute.OtpVerification.route,
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            OtpVerificationScreen(
                phoneNumber = phoneNumber,
                onVerificationSuccess = {
                    navController.navigate(NavRoute.Dashboard.route) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavRoute.Dashboard.route) {
            DashboardScreen(
                onNavigateToMeditation = {
                    navController.navigate(NavRoute.Meditation.route)
                },
                onNavigateToWorkout = {
                    navController.navigate(NavRoute.Workout.route)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoute.Profile.route)
                },
                onNavigateToMedicine = {
                    navController.navigate(NavRoute.Medicine.route)
                },
                onNavigateToSkincare = {
                    navController.navigate(NavRoute.Skincare.route)
                },
                onNavigateToReminders = {
                    navController.navigate(NavRoute.ReminderList.route)
                },
                onNavigateToRemindersHub = {
                    navController.navigate(NavRoute.RemindersHub.route)
                },
                onNavigateToGrocery = {
                    navController.navigate(NavRoute.Grocery.route)
                },
                onNavigateToAssignments = {
                    navController.navigate(NavRoute.Assignments.route)
                },
                onNavigateToFamily = {
                    navController.navigate(NavRoute.FamilyMedicine.route)
                },
                onNavigateToWaterIntake = {
                    navController.navigate(NavRoute.WaterIntake.route)
                },
                onNavigateToInsights = {
                    navController.navigate(NavRoute.Insights.route)
                }
            )
        }
        
        composable(NavRoute.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = {
                    navController.navigate(NavRoute.Settings.route)
                },
                onNavigateToReminders = {
                    navController.navigate(NavRoute.ReminderList.route)
                },
                onNavigateToInsights = {
                    navController.navigate(NavRoute.Insights.route)
                },
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(NavRoute.Login.route) {
                        popUpTo(NavRoute.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavRoute.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(NavRoute.Insights.route) {
            InsightsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(NavRoute.ReminderList.route) {
            ReminderListScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddReminder = { type ->
                    when (type) {
                        ReminderType.MEDITATION -> navController.navigate(NavRoute.MeditationReminder.createRoute(null))
                        ReminderType.WORKOUT -> navController.navigate(NavRoute.WorkoutReminder.createRoute(null))
                        ReminderType.GENERAL -> navController.navigate(NavRoute.MeditationReminder.createRoute(null))
                    }
                }
            )
        }
        
        composable(NavRoute.Meditation.route) {
            MeditationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddMeditation = {
                    navController.navigate(NavRoute.AddMeditation.route)
                },
                onNavigateToAddReminder = { meditationId ->
                    navController.navigate(NavRoute.MeditationReminder.createRoute(meditationId))
                }
            )
        }
        
        composable(NavRoute.AddMeditation.route) {
            AddMeditationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = NavRoute.MeditationDetail.route,
            arguments = listOf(
                navArgument("meditationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val meditationId = backStackEntry.arguments?.getString("meditationId") ?: ""
            // For demo, create a sample meditation - in production, fetch from ViewModel
            val sampleMeditation = Meditation(
                id = meditationId,
                title = "Morning Meditation",
                description = "Start your day with mindfulness",
                category = MeditationCategory.MINDFULNESS,
                durationMinutes = 10,
                notes = "Focus on breath"
            )
            MeditationDetailScreen(
                meditation = sampleMeditation,
                onNavigateBack = { navController.popBackStack() },
                onSetReminder = {
                    navController.navigate(NavRoute.MeditationReminder.createRoute(meditationId))
                },
                onSessionComplete = { minutes ->
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = NavRoute.MeditationReminder.route,
            arguments = listOf(
                navArgument("meditationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val meditationId = backStackEntry.arguments?.getString("meditationId")
            AddReminderScreen(
                reminderType = ReminderType.MEDITATION,
                referenceId = meditationId,
                onNavigateBack = { navController.popBackStack() },
                onSaveReminder = { _, _, _, _, _, _ ->
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavRoute.Workout.route) {
            WorkoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddWorkout = {
                    navController.navigate(NavRoute.AddWorkout.route)
                },
                onNavigateToAddReminder = { workoutId ->
                    navController.navigate(NavRoute.WorkoutReminder.createRoute(workoutId))
                },
                onNavigateToProgress = {
                    navController.navigate(NavRoute.WorkoutProgress.route)
                },
                onNavigateToWorkoutDetail = { workout ->
                    pendingWorkoutForDetail = workout
                    navController.navigate(NavRoute.WorkoutDetail.createRoute(workout.id))
                },
                onNavigateToLogWorkout = {
                    navController.navigate(NavRoute.LogWorkout.route)
                }
            )
        }
        
        composable(NavRoute.LogWorkout.route) {
            LogWorkoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddExercise = {
                    navController.navigate(NavRoute.AddExercise.route)
                },
                onFinish = { navController.popBackStack() }
            )
        }
        
        composable(NavRoute.AddExercise.route) {
            val workoutViewModel: WorkoutViewModel = koinViewModel()
            AddExerciseScreen(
                onNavigateBack = { navController.popBackStack() },
                onExerciseSelected = { exercise ->
                    workoutViewModel.processIntent(
                        WorkoutIntent.AddSessionExercise(
                            LoggedExercise(
                                name = exercise.name,
                                muscleGroup = exercise.muscleGroup,
                                sets = mutableListOf(ExerciseSet(1))
                            )
                        )
                    )
                    navController.popBackStack()
                },
                onCreateExercise = { }
            )
        }
        
        composable(NavRoute.AddWorkout.route) {
            AddWorkoutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = NavRoute.WorkoutDetail.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            val workout = pendingWorkoutForDetail ?: getDefaultWorkoutById(workoutId)
            WorkoutDetailScreen(
                workout = workout,
                onNavigateBack = { navController.popBackStack() },
                onSetReminder = {
                    navController.navigate(NavRoute.WorkoutReminder.createRoute(workoutId))
                },
                onWorkoutComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = NavRoute.WorkoutReminder.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId")
            AddReminderScreen(
                reminderType = ReminderType.WORKOUT,
                referenceId = workoutId,
                onNavigateBack = { navController.popBackStack() },
                onSaveReminder = { _, _, _, _, _, _ ->
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavRoute.Medicine.route) {
            MedicineScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddMedicine = {
                    navController.navigate(NavRoute.AddMedicine.route)
                },
                onNavigateToMedicineDetail = { medicineId ->
                    navController.navigate(NavRoute.MedicineDetail.createRoute(medicineId))
                }
            )
        }
        
        composable(NavRoute.Skincare.route) {
            SkincareScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddSkincare = {
                    navController.navigate(NavRoute.AddSkincare.route)
                },
                onNavigateToSkincareDetail = { routineId ->
                    navController.navigate(NavRoute.SkincareDetail.createRoute(routineId))
                }
            )
        }
        
        composable(NavRoute.AddMedicine.route) {
            AddMedicineScreen(
                onNavigateBack = { navController.popBackStack() },
                onSave = { }
            )
        }
        
        composable(
            route = NavRoute.MedicineDetail.route,
            arguments = listOf(
                navArgument("medicineId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getString("medicineId") ?: ""
            MedicineDetailScreen(
                medicineId = medicineId,
                onNavigateBack = { navController.popBackStack() },
                onDelete = { }
            )
        }
        
        composable(NavRoute.AddSkincare.route) {
            AddSkincareScreen(
                onNavigateBack = { navController.popBackStack() },
                onSave = { }
            )
        }
        
        composable(
            route = NavRoute.SkincareDetail.route,
            arguments = listOf(
                navArgument("routineId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
            SkincareDetailScreen(
                routineId = routineId,
                onNavigateBack = { navController.popBackStack() },
                onDelete = { }
            )
        }
        
        // Reminders Hub
        composable(NavRoute.RemindersHub.route) {
            RemindersHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMedicine = { navController.navigate(NavRoute.Medicine.route) },
                onNavigateToWorkout = { navController.navigate(NavRoute.Workout.route) },
                onNavigateToSkincare = { navController.navigate(NavRoute.Skincare.route) },
                onNavigateToMeditation = { navController.navigate(NavRoute.Meditation.route) },
                onNavigateToGrocery = { navController.navigate(NavRoute.Grocery.route) },
                onNavigateToAssignments = { navController.navigate(NavRoute.Assignments.route) },
                onNavigateToFamily = { navController.navigate(NavRoute.FamilyMedicine.route) },
                onNavigateToWater = { navController.navigate(NavRoute.WaterIntake.route) },
                onNavigateToBills = { navController.navigate(NavRoute.Bills.route) },
                onNavigateToBirthdays = { navController.navigate(NavRoute.Birthdays.route) },
                onNavigateToSleep = { navController.navigate(NavRoute.SleepTracker.route) },
                onNavigateToVehicle = { navController.navigate(NavRoute.Vehicle.route) },
                onNavigateToScreenTime = { navController.navigate(NavRoute.ScreenTime.route) },
                onNavigateToPlants = { navController.navigate(NavRoute.PlantCare.route) }
            )
        }
        
        // Grocery
        composable(NavRoute.Grocery.route) {
            GroceryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Assignments
        composable(NavRoute.Assignments.route) {
            AssignmentsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Family Medicine
        composable(NavRoute.FamilyMedicine.route) {
            FamilyMedicineScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Water Intake
        composable(NavRoute.WaterIntake.route) {
            WaterIntakeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Bills
        composable(NavRoute.Bills.route) {
            BillsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Birthdays
        composable(NavRoute.Birthdays.route) {
            BirthdaysScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Sleep Tracker
        composable(NavRoute.SleepTracker.route) {
            SleepTrackerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Vehicle Maintenance
        composable(NavRoute.Vehicle.route) {
            VehicleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Screen Time
        composable(NavRoute.ScreenTime.route) {
            ScreenTimeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Plant Care
        composable(NavRoute.PlantCare.route) {
            PlantCareScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

private fun getDefaultWorkoutById(workoutId: String): Workout {
    val defaultWorkouts = mapOf(
        "default_chest_day" to Workout(
            id = "default_chest_day",
            name = "Chest Day",
            description = "Build a stronger chest with compound and isolation exercises",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Bench Press", description = "Lie flat on bench, lower bar to chest, press up", muscleGroup = MuscleGroup.CHEST, sets = 4, reps = 10, restSeconds = 90),
                Exercise(name = "Incline Dumbbell Press", description = "45° incline, press dumbbells up and together", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 12, restSeconds = 60),
                Exercise(name = "Cable Flyes", description = "Stand between cables, bring handles together in arc", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 15, restSeconds = 60),
                Exercise(name = "Push-ups", description = "Classic bodyweight chest exercise", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 15, restSeconds = 45),
                Exercise(name = "Dips", description = "Lower body between parallel bars, press up", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 12, restSeconds = 60)
            ),
            durationMinutes = 45,
            caloriesBurned = 320
        ),
        "default_back_day" to Workout(
            id = "default_back_day",
            name = "Back Attack",
            description = "Develop a wider, thicker back",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Deadlift", description = "Hinge at hips, keep back straight, lift bar", muscleGroup = MuscleGroup.BACK, sets = 4, reps = 8, restSeconds = 120),
                Exercise(name = "Pull-ups", description = "Hang from bar, pull chin over bar", muscleGroup = MuscleGroup.BACK, sets = 4, reps = 10, restSeconds = 90),
                Exercise(name = "Barbell Rows", description = "Bend over, pull bar to lower chest", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 12, restSeconds = 60),
                Exercise(name = "Lat Pulldown", description = "Pull bar down to upper chest", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 12, restSeconds = 60),
                Exercise(name = "Face Pulls", description = "Pull rope to face, squeeze shoulder blades", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 15, restSeconds = 45)
            ),
            durationMinutes = 50,
            caloriesBurned = 350
        ),
        "default_leg_day" to Workout(
            id = "default_leg_day",
            name = "Leg Day",
            description = "Build powerful legs and glutes",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Squats", description = "Bar on upper back, squat until thighs parallel", muscleGroup = MuscleGroup.LEGS, sets = 4, reps = 10, restSeconds = 120),
                Exercise(name = "Leg Press", description = "Push platform away with feet shoulder-width", muscleGroup = MuscleGroup.QUADRICEPS, sets = 3, reps = 12, restSeconds = 90),
                Exercise(name = "Romanian Deadlift", description = "Hinge at hips with slight knee bend", muscleGroup = MuscleGroup.HAMSTRINGS, sets = 3, reps = 10, restSeconds = 60),
                Exercise(name = "Leg Curls", description = "Curl weight toward glutes", muscleGroup = MuscleGroup.HAMSTRINGS, sets = 3, reps = 12, restSeconds = 60),
                Exercise(name = "Calf Raises", description = "Rise onto toes, squeeze at top", muscleGroup = MuscleGroup.CALVES, sets = 4, reps = 15, restSeconds = 45)
            ),
            durationMinutes = 55,
            caloriesBurned = 400
        ),
        "default_hiit" to Workout(
            id = "default_hiit",
            name = "HIIT Cardio",
            description = "High intensity interval training for fat burn",
            category = WorkoutCategory.HIIT,
            exercises = listOf(
                Exercise(name = "Burpees", description = "Squat, jump back to plank, push-up, jump up", muscleGroup = MuscleGroup.FULL_BODY, sets = 4, reps = 15, restSeconds = 30),
                Exercise(name = "Mountain Climbers", description = "Plank position, drive knees to chest rapidly", muscleGroup = MuscleGroup.CORE, sets = 4, reps = 30, restSeconds = 30),
                Exercise(name = "Jump Squats", description = "Squat down, explode up into jump", muscleGroup = MuscleGroup.LEGS, sets = 4, reps = 20, restSeconds = 30),
                Exercise(name = "High Knees", description = "Run in place bringing knees to chest", muscleGroup = MuscleGroup.CARDIO, sets = 4, reps = 30, restSeconds = 30),
                Exercise(name = "Box Jumps", description = "Jump onto elevated platform", muscleGroup = MuscleGroup.LEGS, sets = 3, reps = 12, restSeconds = 45)
            ),
            durationMinutes = 30,
            caloriesBurned = 450
        ),
        "default_full_body" to Workout(
            id = "default_full_body",
            name = "Full Body Workout",
            description = "Complete full body strength training",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Squats", description = "Fundamental lower body compound movement", muscleGroup = MuscleGroup.LEGS, sets = 3, reps = 12, restSeconds = 90),
                Exercise(name = "Bench Press", description = "Primary chest pressing movement", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 10, restSeconds = 90),
                Exercise(name = "Bent Over Rows", description = "Pull weight to lower chest while hinged", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 12, restSeconds = 60),
                Exercise(name = "Shoulder Press", description = "Press weight overhead from shoulders", muscleGroup = MuscleGroup.SHOULDERS, sets = 3, reps = 10, restSeconds = 60),
                Exercise(name = "Plank", description = "Hold rigid body position on forearms", muscleGroup = MuscleGroup.CORE, sets = 3, reps = 60, restSeconds = 45)
            ),
            durationMinutes = 60,
            caloriesBurned = 500
        ),
        "default_yoga_flow" to Workout(
            id = "default_yoga_flow",
            name = "Yoga Flow",
            description = "Relaxing yoga session for flexibility and mindfulness",
            category = WorkoutCategory.YOGA,
            exercises = listOf(
                Exercise(name = "Sun Salutation", description = "Flow through standing, forward fold, plank, cobra", muscleGroup = MuscleGroup.FULL_BODY, sets = 3, reps = 5, restSeconds = 30),
                Exercise(name = "Warrior Pose", description = "Lunge with arms extended, hold", muscleGroup = MuscleGroup.LEGS, sets = 2, reps = 30, restSeconds = 15),
                Exercise(name = "Downward Dog", description = "Inverted V position, heels toward ground", muscleGroup = MuscleGroup.FULL_BODY, sets = 3, reps = 30, restSeconds = 15),
                Exercise(name = "Child's Pose", description = "Kneel, sit back on heels, arms forward", muscleGroup = MuscleGroup.BACK, sets = 2, reps = 60, restSeconds = 15),
                Exercise(name = "Cobra Stretch", description = "Lie prone, press chest up with arms", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 20, restSeconds = 15)
            ),
            durationMinutes = 40,
            caloriesBurned = 180
        ),
        "default_arm_day" to Workout(
            id = "default_arm_day",
            name = "Arm Day",
            description = "Build bigger biceps and triceps",
            category = WorkoutCategory.STRENGTH,
            exercises = listOf(
                Exercise(name = "Bicep Curls", description = "Curl dumbbells with controlled movement", muscleGroup = MuscleGroup.BICEPS, sets = 4, reps = 12, restSeconds = 60),
                Exercise(name = "Hammer Curls", description = "Curl with neutral grip (palms facing)", muscleGroup = MuscleGroup.BICEPS, sets = 3, reps = 12, restSeconds = 60),
                Exercise(name = "Tricep Pushdowns", description = "Push cable attachment down, lock elbows", muscleGroup = MuscleGroup.TRICEPS, sets = 4, reps = 12, restSeconds = 60),
                Exercise(name = "Skull Crushers", description = "Lower bar to forehead, extend arms", muscleGroup = MuscleGroup.TRICEPS, sets = 3, reps = 10, restSeconds = 60),
                Exercise(name = "Close Grip Bench", description = "Bench press with hands closer together", muscleGroup = MuscleGroup.TRICEPS, sets = 3, reps = 10, restSeconds = 90)
            ),
            durationMinutes = 40,
            caloriesBurned = 250
        ),
        "default_core_crusher" to Workout(
            id = "default_core_crusher",
            name = "Core Crusher",
            description = "Strengthen your abs and core muscles",
            category = WorkoutCategory.BODYWEIGHT,
            exercises = listOf(
                Exercise(name = "Crunches", description = "Curl upper body toward knees", muscleGroup = MuscleGroup.ABS, sets = 4, reps = 25, restSeconds = 30),
                Exercise(name = "Plank", description = "Hold body rigid on forearms and toes", muscleGroup = MuscleGroup.CORE, sets = 3, reps = 60, restSeconds = 30),
                Exercise(name = "Russian Twists", description = "Seated twist with weight or bodyweight", muscleGroup = MuscleGroup.ABS, sets = 3, reps = 30, restSeconds = 30),
                Exercise(name = "Leg Raises", description = "Lie flat, raise legs to 90 degrees", muscleGroup = MuscleGroup.ABS, sets = 3, reps = 15, restSeconds = 30),
                Exercise(name = "Bicycle Crunches", description = "Alternate elbow to opposite knee", muscleGroup = MuscleGroup.ABS, sets = 3, reps = 30, restSeconds = 30)
            ),
            durationMinutes = 25,
            caloriesBurned = 200
        )
    )
    
    return defaultWorkouts[workoutId] ?: Workout(
        id = workoutId,
        name = "Custom Workout",
        description = "Your personalized workout routine",
        category = WorkoutCategory.STRENGTH,
        exercises = listOf(
            Exercise(name = "Warm-up", muscleGroup = MuscleGroup.FULL_BODY, sets = 1, reps = 5),
            Exercise(name = "Main Exercise", muscleGroup = MuscleGroup.FULL_BODY, sets = 3, reps = 10),
            Exercise(name = "Cool Down", muscleGroup = MuscleGroup.FULL_BODY, sets = 1, reps = 5)
        ),
        durationMinutes = 30,
        caloriesBurned = 200
    )
}
