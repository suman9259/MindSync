package com.example.mindsync.presentation.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mindsync.domain.model.ProgressPeriod
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.presentation.components.AnimatedCircularProgress
import com.example.mindsync.presentation.components.AnimatedListItem
import com.example.mindsync.presentation.components.AnimatedProgressBar
import com.example.mindsync.presentation.components.AnimatedStatCard
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddWorkout: () -> Unit,
    onNavigateToAddReminder: (String?) -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToWorkoutDetail: (String) -> Unit = {},
    onNavigateToLogWorkout: () -> Unit = {},
    viewModel: WorkoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WorkoutEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is WorkoutEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                is WorkoutEffect.NavigateBack -> onNavigateBack()
                is WorkoutEffect.NavigateToDetail -> onNavigateToWorkoutDetail(effect.workoutId)
                is WorkoutEffect.ReminderScheduled -> { }
            }
        }
    }

    val darkBackground = Color(0xFF121212)  // Material dark background
    val cardBackground = Color(0xFF1E1E1E)  // Material dark surface
    val tealAccent = Color(0xFF03DAC5)       // Material Teal accent

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Workout",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(16.dp)
                                .clickable { /* dropdown */ }
                        )
                    }
                },
                actions = {
                    // PRO badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF3B0))  // Pastel yellow
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "PRO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = tealAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Empty Workout Button
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLogWorkout() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Start Empty Workout",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Routines Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Routines",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Folder",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onNavigateToAddWorkout() }
                        )
                    }
                }

                // New Routine and Explore buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToAddWorkout() },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📋", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "New Routine",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { /* explore */ },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔍", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Explore",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Info tip
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))  // Material dark surface variant
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💡", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Press and hold a routine to reorder",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB0B0B0)
                            )
                        }
                    }
                }

                // My Routines Header
                item {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "▼",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "My Routines (${state.workouts.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray
                        )
                    }
                }

                // Routine Cards
                items(state.workouts) { workout ->
                    RoutineCard(
                        workout = workout,
                        onStart = { viewModel.processIntent(WorkoutIntent.SelectWorkout(workout)) },
                        tealAccent = tealAccent,
                        cardBackground = cardBackground
                    )
                }

                // Empty state
                if (state.workouts.isEmpty()) {
                    item {
                        EmptyWorkoutsView(onAddClick = onNavigateToAddWorkout)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun RoutineCard(
    workout: Workout,
    onStart: () -> Unit,
    tealAccent: Color,
    cardBackground: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    "⋮",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                workout.exercises.take(3).joinToString(", ") { it.name } + 
                    if (workout.exercises.size > 3) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Start Routine Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onStart),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = tealAccent)
            ) {
                Text(
                    "Start Routine",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun WorkoutListContent(
    state: WorkoutState,
    viewModel: WorkoutViewModel,
    onNavigateToAddWorkout: () -> Unit,
    onNavigateToAddReminder: (String?) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WorkoutHeader(state)
        }

        item {
            WorkoutStatsSection(state)
        }

        item {
            CategoryFilterSection(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { category ->
                    viewModel.processIntent(WorkoutIntent.SelectCategory(category))
                }
            )
        }

        item {
            Text(
                text = "Recommended Workouts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.defaultWorkouts) { workout ->
                    DefaultWorkoutCard(
                        workout = workout,
                        onClick = { viewModel.processIntent(WorkoutIntent.SelectWorkout(workout)) }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Workouts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        val filteredWorkouts = if (state.selectedCategory != null) {
            state.workouts.filter { it.category == state.selectedCategory }
        } else {
            state.workouts
        }

        if (filteredWorkouts.isEmpty()) {
            item {
                EmptyWorkoutsView(onAddClick = onNavigateToAddWorkout)
            }
        } else {
            itemsIndexed(filteredWorkouts) { index, workout ->
                AnimatedListItem(index = index) {
                    WorkoutCard(
                        workout = workout,
                        onClick = { viewModel.processIntent(WorkoutIntent.SelectWorkout(workout)) },
                        onReminderClick = { onNavigateToAddReminder(workout.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutProgressContent(
    state: WorkoutState,
    viewModel: WorkoutViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PeriodSelector(
                selectedPeriod = state.selectedPeriod,
                onPeriodSelected = { period ->
                    viewModel.processIntent(WorkoutIntent.SelectPeriod(period))
                }
            )
        }

        item {
            ProgressOverviewCard(state)
        }

        item {
            Text(
                text = "Workout Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedStatCard(
                    title = "Workouts",
                    value = "${state.progress.totalWorkouts}",
                    modifier = Modifier.weight(1f)
                )
                AnimatedStatCard(
                    title = "Minutes",
                    value = "${state.progress.totalMinutes}",
                    modifier = Modifier.weight(1f)
                )
                AnimatedStatCard(
                    title = "Calories",
                    value = "${state.progress.totalCalories}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            StreakCard(state)
        }
    }
}

@Composable
private fun WorkoutHeader(state: WorkoutState) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(500)) + slideInVertically(
            initialOffsetY = { -it / 2 },
            animationSpec = tween(500)
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFF5B8D0), Color(0xFFFFDAC1), Color(0xFFFFF3B0))  // Pastel rose to peach to yellow
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Let's Crush It! �",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.stats.totalWorkouts} workouts completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🔥 ${state.stats.currentStreak} day streak",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Text(
                        text = "🏋️",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutStatsSection(state: WorkoutState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedStatCard(
            title = "Total",
            value = "${state.stats.totalWorkouts}",
            subtitle = "workouts",
            modifier = Modifier.weight(1f)
        )
        AnimatedStatCard(
            title = "Time",
            value = "${state.stats.totalMinutes}",
            subtitle = "minutes",
            modifier = Modifier.weight(1f)
        )
        AnimatedStatCard(
            title = "Streak",
            value = "${state.stats.currentStreak}",
            subtitle = "days",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryFilterSection(
    selectedCategory: WorkoutCategory?,
    onCategorySelected: (WorkoutCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") }
            )
        }
        items(WorkoutCategory.entries) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) }
            )
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: ProgressPeriod,
    onPeriodSelected: (ProgressPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProgressPeriod.entries.forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(period.displayName) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProgressOverviewCard(state: WorkoutState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${state.selectedPeriod.displayName} Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            val targetWorkouts = when (state.selectedPeriod) {
                ProgressPeriod.WEEKLY -> 5
                ProgressPeriod.MONTHLY -> 20
                ProgressPeriod.YEARLY -> 200
            }
            val progress = (state.progress.totalWorkouts.toFloat() / targetWorkouts).coerceIn(0f, 1f)
            
            AnimatedCircularProgress(
                progress = progress,
                size = 150.dp,
                strokeWidth = 16.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${state.progress.totalWorkouts} / $targetWorkouts workouts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StreakCard(state: WorkoutState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🔥 Current Streak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keep it up!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "${state.progress.currentStreak} days",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DefaultWorkoutCard(
    workout: Workout,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                // Gradient background if no image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                getCategoryColors(workout.category)
                            )
                        )
                )
                if (workout.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = workout.imageUrl,
                        contentDescription = workout.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )
                // Play button overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // Workout name at bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = workout.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = workout.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏱️ ", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "${workout.durationMinutes}min",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💪 ", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "${workout.exercises.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    workout: Workout,
    onClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (workout.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = workout.imageUrl,
                        contentDescription = workout.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    getCategoryColors(workout.category)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "${workout.durationMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(" • ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${workout.exercises.size} exercises",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = workout.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(onClick = onReminderClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Set Reminder",
                    tint = if (workout.reminderEnabled)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyWorkoutsView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏋️",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No custom workouts yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your own workout routine to track your progress",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getCategoryColors(category: WorkoutCategory): List<Color> {
    return when (category) {
        WorkoutCategory.STRENGTH -> listOf(Color(0xFFf093fb), Color(0xFFf5576c))
        WorkoutCategory.CARDIO -> listOf(Color(0xFF11998e), Color(0xFF38ef7d))
        WorkoutCategory.FLEXIBILITY -> listOf(Color(0xFF667eea), Color(0xFF764ba2))
        WorkoutCategory.HIIT -> listOf(Color(0xFFfc4a1a), Color(0xFFf7b733))
        WorkoutCategory.YOGA -> listOf(Color(0xFFa8edea), Color(0xFFfed6e3))
        WorkoutCategory.CROSSFIT -> listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
        WorkoutCategory.BODYWEIGHT -> listOf(Color(0xFFf2709c), Color(0xFFff9472))
        WorkoutCategory.POWERLIFTING -> listOf(Color(0xFF2c3e50), Color(0xFF4ca1af))
    }
}
