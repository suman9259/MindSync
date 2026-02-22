package com.example.mindsync.di

import com.example.mindsync.data.local.UserPreferences
import com.example.mindsync.data.local.database.MindSyncDatabase
import com.example.mindsync.data.local.security.CryptoManager
import com.example.mindsync.data.local.security.SecurePreferences
import com.example.mindsync.data.repository.MeditationRepositoryImpl
import com.example.mindsync.data.repository.ReminderRepositoryImpl
import com.example.mindsync.data.repository.WorkoutRepositoryImpl
import com.example.mindsync.data.worker.ReminderScheduler
import com.example.mindsync.domain.repository.MeditationRepository
import com.example.mindsync.domain.repository.ReminderRepository
import com.example.mindsync.domain.repository.WorkoutRepository
import com.example.mindsync.domain.usecase.meditation.AddMeditationReminderUseCase
import com.example.mindsync.domain.usecase.meditation.AddMeditationUseCase
import com.example.mindsync.domain.usecase.meditation.GetMeditationStatsUseCase
import com.example.mindsync.domain.usecase.meditation.GetMeditationsUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutReminderUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutSessionUseCase
import com.example.mindsync.domain.usecase.workout.AddWorkoutUseCase
import com.example.mindsync.domain.usecase.workout.GetDefaultWorkoutsUseCase
import com.example.mindsync.domain.usecase.workout.GetExercisesUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutProgressUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutStatsUseCase
import com.example.mindsync.domain.usecase.workout.GetWorkoutsUseCase
import com.example.mindsync.presentation.auth.AuthViewModel
import com.example.mindsync.presentation.dashboard.DashboardViewModel
import com.example.mindsync.presentation.meditation.MeditationViewModel
import com.example.mindsync.presentation.splash.SplashViewModel
import com.example.mindsync.presentation.workout.WorkoutViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase
    single { FirebaseFirestore.getInstance() }
    
    // Security & Encryption
    single { CryptoManager() }
    single { SecurePreferences(androidContext()) }
    
    // Encrypted Database
    single { MindSyncDatabase.getDatabase(androidContext(), get()) }
    single { get<MindSyncDatabase>().meditationDao() }
    single { get<MindSyncDatabase>().workoutDao() }
    single { get<MindSyncDatabase>().reminderDao() }
    single { get<MindSyncDatabase>().medicineDao() }
    single { get<MindSyncDatabase>().skincareDao() }
    
    // Local (Legacy - kept for compatibility)
    single { UserPreferences(androidContext()) }
    single { ReminderScheduler(androidContext()) }
    
    // Repositories
    single<MeditationRepository> { MeditationRepositoryImpl(get()) }
    single<WorkoutRepository> { WorkoutRepositoryImpl(get()) }
    single<ReminderRepository> { ReminderRepositoryImpl(get()) }
    
    // Meditation Use Cases
    factory { GetMeditationsUseCase(get()) }
    factory { AddMeditationUseCase(get()) }
    factory { AddMeditationReminderUseCase(get()) }
    factory { GetMeditationStatsUseCase(get()) }
    
    // Workout Use Cases
    factory { GetWorkoutsUseCase(get()) }
    factory { GetDefaultWorkoutsUseCase(get()) }
    factory { GetExercisesUseCase(get()) }
    factory { AddWorkoutUseCase(get()) }
    factory { AddWorkoutSessionUseCase(get()) }
    factory { AddWorkoutReminderUseCase(get()) }
    factory { GetWorkoutProgressUseCase(get()) }
    factory { GetWorkoutStatsUseCase(get()) }
    
    // ViewModels
    viewModel { SplashViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { DashboardViewModel() }
    viewModel { MeditationViewModel(get(), get(), get(), get()) }
    viewModel { WorkoutViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
