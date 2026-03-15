package com.example.mindsync.presentation.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun DashboardScreen(
    onNavigateToMeditation: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMedicine: () -> Unit = {},
    onNavigateToSkincare: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToRemindersHub: () -> Unit = {},
    onNavigateToGrocery: () -> Unit = {},
    onNavigateToAssignments: () -> Unit = {},
    onNavigateToFamily: () -> Unit = {},
    onNavigateToInsights: () -> Unit = {},
    onNavigateToWaterIntake: () -> Unit = {},
    viewModel: DashboardViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val onNavigationItemSelected: (Int) -> Unit = { viewModel.processIntent(DashboardIntent.SelectTab(it)) }
    val onRetry: () -> Unit = { viewModel.processIntent(DashboardIntent.Retry) }

    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }
    
    // Quick Notes state
    var quickNote by remember { mutableStateOf("") }
    var showNoteInput by remember { mutableStateOf(false) }
    val savedNotes = state.quickNotes
    
    // Get upcoming tasks from state (real data from database)
    val upcomingTasks = state.upcomingTasks
    
    // Expandable state for upcoming tasks
    var showAllTasks by remember { mutableStateOf(false) }
    val displayedTasks = if (showAllTasks) upcomingTasks else upcomingTasks.take(3)
    
    // Use real progress from state + completed workouts
    val workoutsCompleted = state.workoutsCompletedToday.size
    val totalTasks = (if (state.totalSteps > 0) state.totalSteps else 5) + workoutsCompleted
    val completedTasks = state.completedTasksCount + workoutsCompleted
    val realProgress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else state.progress

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    state.error?.let { error ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: $error")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) { Text("Retry") }
            }
        }
        return
    }

    val darkBackground = Color(0xFF121212)  // Material dark background
    val cardBackground = Color(0xFF1E1E1E)
    
    Scaffold(
        containerColor = darkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = cardBackground,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = state.selectedTab == 0,
                    onClick = { onNavigationItemSelected(0) },
                    icon = { Icon(if (state.selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = state.selectedTab == 1,
                    onClick = { onNavigationItemSelected(1); onNavigateToMeditation() },
                    icon = { Icon(if (state.selectedTab == 1) Icons.Filled.SelfImprovement else Icons.Outlined.SelfImprovement, contentDescription = "Meditate") },
                    label = { Text("Meditate") }
                )
                NavigationBarItem(
                    selected = state.selectedTab == 2,
                    onClick = { onNavigationItemSelected(2); onNavigateToWorkout() },
                    icon = { Icon(if (state.selectedTab == 2) Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter, contentDescription = "Workout") },
                    label = { Text("Workout") }
                )
                NavigationBarItem(
                    selected = state.selectedTab == 3,
                    onClick = { onNavigationItemSelected(3); onNavigateToReminders() },
                    icon = { Icon(if (state.selectedTab == 3) Icons.Filled.Notifications else Icons.Outlined.Notifications, contentDescription = "Reminders") },
                    label = { Text("Reminders") }
                )
                NavigationBarItem(
                    selected = state.selectedTab == 4,
                    onClick = { onNavigateToProfile() },
                    icon = { Icon(if (state.selectedTab == 4) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(darkBackground)
        ) {
            // Main scrollable content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Clean Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Good ${getGreeting()} ☀️",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Card(
                            onClick = onNavigateToProfile,
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Progress Card - Clickable for insights
                item {
                    DailyProgressCard(
                        progress = realProgress,
                        completedTasks = completedTasks,
                        totalTasks = totalTasks,
                        onClick = onNavigateToInsights
                    )
                }

                // Quick Note - Compact
                item {
                    QuickNoteSection(
                        showInput = showNoteInput,
                        note = quickNote,
                        savedNotes = savedNotes,
                        onNoteChange = { quickNote = it },
                        onToggleInput = { showNoteInput = !showNoteInput },
                        onSaveNote = {
                            if (quickNote.isNotBlank()) {
                                viewModel.processIntent(DashboardIntent.AddQuickNote(quickNote))
                                quickNote = ""
                                showNoteInput = false
                            }
                        },
                        onDeleteNote = { index ->
                            viewModel.processIntent(DashboardIntent.DeleteQuickNote(index))
                        }
                    )
                }

                // Quick Actions - Horizontal scroll, compact
                item {
                    QuickActionsRow(
                        onMeditationClick = onNavigateToMeditation,
                        onWorkoutClick = onNavigateToWorkout,
                        onMedicineClick = onNavigateToMedicine,
                        onSkincareClick = onNavigateToSkincare
                    )
                }

                // Set Reminder Button
                item {
                    SetReminderButton(onClick = onNavigateToRemindersHub)
                }
                
                // Today's Completed Workouts
                if (state.workoutsCompletedToday.isNotEmpty()) {
                    item {
                        Text(
                            "✅ Today's Workouts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(state.workoutsCompletedToday) { workoutName ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2E1B))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💪", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            workoutName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            "Completed today",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF66BB6A)
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF66BB6A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Upcoming Reminders
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "⏰ Upcoming",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (upcomingTasks.size > 3) {
                            TextButton(onClick = { showAllTasks = !showAllTasks }) {
                                Text(
                                    if (showAllTasks) "See Less" else "See All (${upcomingTasks.size})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF03DAC5)
                                )
                            }
                        }
                    }
                }
                
                items(displayedTasks) { task ->
                    UpcomingTaskRow(
                        task = task,
                        onClick = {
                            when (task.category) {
                                "medicine" -> onNavigateToMedicine()
                                "meditation" -> onNavigateToMeditation()
                                "workout" -> onNavigateToWorkout()
                                "skincare" -> onNavigateToSkincare()
                                "water" -> onNavigateToWaterIntake()
                                else -> onNavigateToRemindersHub()
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SetReminderButton(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF03DAC5), Color(0xFF64FFDA))  // Material Teal gradient
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Set Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpcomingTaskRow(
    task: UpcomingTask,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(task.emoji, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    task.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QuickNoteSection(
    showInput: Boolean,
    note: String,
    savedNotes: List<String>,
    onNoteChange: (String) -> Unit,
    onToggleInput: () -> Unit,
    onSaveNote: () -> Unit,
    onDeleteNote: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "� Quick Notes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onToggleInput,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    if (showInput) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Add note",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        if (showInput) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Quick note...", style = MaterialTheme.typography.bodySmall) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onSaveNote,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF03DAC5), CircleShape)  // Material Teal
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
        
        savedNotes.forEachIndexed { index, savedNote ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f))  // Teal tint on dark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        savedNote,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = { onDeleteNote(index) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Delete",
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Morning"
        hour < 17 -> "Afternoon"
        else -> "Evening"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyProgressCard(
    progress: Float,
    completedTasks: Int,
    totalTasks: Int,
    onClick: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF03DAC5),  // Material Teal
                            Color(0xFF64FFDA)   // Light Teal
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Today's Progress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$completedTasks/$totalTasks tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (progress >= 0.8f) "Amazing! 🔥" 
                        else if (progress >= 0.5f) "Keep going! 💪" 
                        else "Let's start! 🚀",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        "Tap for insights →",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                    Text(
                        "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
private fun QuickActionsRow(
    onMeditationClick: () -> Unit,
    onWorkoutClick: () -> Unit,
    onMedicineClick: () -> Unit,
    onSkincareClick: () -> Unit
) {
    val actions = listOf(
        QuickAction("🧘", "Meditate", listOf(Color(0xFFBB86FC), Color(0xFF9C27B0)), onMeditationClick),  // Purple
        QuickAction("💪", "Workout", listOf(Color(0xFF03DAC5), Color(0xFF00C2A8)), onWorkoutClick),  // Teal
        QuickAction("💊", "Medicine", listOf(Color(0xFF03DAC5), Color(0xFF64FFDA)), onMedicineClick),  // Teal gradient
        QuickAction("✨", "Skincare", listOf(Color(0xFF03DAC5), Color(0xFFBB86FC)), onSkincareClick)  // Teal to purple
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(actions.size) { index ->
            QuickActionChip(action = actions[index])
        }
    }
}

data class QuickAction(
    val emoji: String,
    val title: String,
    val colors: List<Color>,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionChip(action: QuickAction) {
    Card(
        onClick = action.onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(brush = Brush.linearGradient(colors = action.colors))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(action.emoji, style = MaterialTheme.typography.titleLarge)
                Text(
                    action.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

