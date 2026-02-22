package com.example.mindsync.presentation.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.*

data class UpcomingReminder(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val title: String,
    val time: String,
    val category: String,
    val priority: Int = 0 // lower = higher priority
)

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
    viewModel: DashboardViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val onNavigationItemSelected: (Int) -> Unit = { viewModel.processIntent(DashboardIntent.SelectTab(it)) }
    val onRetry: () -> Unit = { viewModel.processIntent(DashboardIntent.Retry) }

    val motivationalQuotes = listOf(
        "✨ Every day is a new beginning. Take a deep breath and start again.",
        "🌟 Believe you can and you're halfway there.",
        "💪 The only bad workout is the one that didn't happen.",
        "🧘 Peace comes from within. Do not seek it without.",
        "🌿 Take care of your body. It's the only place you have to live.",
        "🚀 Small steps every day lead to big changes.",
        "🌈 Your health is an investment, not an expense."
    )
    val currentQuote = remember { motivationalQuotes.random() }
    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }
    
    // Quick Notes state
    var quickNote by remember { mutableStateOf("") }
    var showNoteInput by remember { mutableStateOf(false) }
    var savedNotes by remember { mutableStateOf(listOf<String>()) }
    
    // Upcoming reminders with real-ish data based on time
    val upcomingReminders = remember {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        mutableStateListOf(
            UpcomingReminder("1", "💊", "Vitamin D3", "8:00 AM", "medicine", if (hour < 8) 0 else 5),
            UpcomingReminder("2", "💧", "Drink Water", "Every 2 hours", "water", 1),
            UpcomingReminder("3", "🧘", "Morning Meditation", "9:00 AM", "meditation", if (hour < 9) 0 else 5),
            UpcomingReminder("4", "💪", "Workout Session", "6:00 PM", "workout", if (hour < 18) 2 else 5),
            UpcomingReminder("5", "✨", "Evening Skincare", "9:00 PM", "skincare", if (hour < 21) 3 else 5)
        ).sortedBy { it.priority }
    }
    
    // Calculate real progress based on completed tasks
    val totalTasks = 5
    val completedTasks = remember { mutableStateOf(2) } // Can be updated based on actual completion
    val realProgress = completedTasks.value.toFloat() / totalTasks

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

    val darkBackground = Color(0xFF0D0D0D)
    val cardBackground = Color(0xFF1A1A1A)
    val blueAccent = Color(0xFF4A90D9)
    
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
                        completedTasks = completedTasks.value,
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
                                savedNotes = savedNotes + quickNote
                                quickNote = ""
                                showNoteInput = false
                            }
                        },
                        onDeleteNote = { index ->
                            savedNotes = savedNotes.filterIndexed { i, _ -> i != index }
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
                
                // Upcoming Reminders
                item {
                    Text(
                        "⏰ Upcoming",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(upcomingReminders.take(3)) { reminder ->
                    UpcomingReminderRow(
                        reminder = reminder,
                        onClick = {
                            when (reminder.category) {
                                "medicine" -> onNavigateToMedicine()
                                "meditation" -> onNavigateToMeditation()
                                "workout" -> onNavigateToWorkout()
                                "skincare" -> onNavigateToSkincare()
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
                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
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
private fun UpcomingReminderRow(
    reminder: UpcomingReminder,
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
            Text(reminder.emoji, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    reminder.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    reminder.time,
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
                        .background(Color(0xFF4CAF50), CircleShape)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
        
        savedNotes.forEachIndexed { index, savedNote ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
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
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
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
private fun MotivationalCard(quote: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = quote,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp),
            lineHeight = 22.sp
        )
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
        QuickAction("🧘", "Meditate", listOf(Color(0xFF667eea), Color(0xFF764ba2)), onMeditationClick),
        QuickAction("💪", "Workout", listOf(Color(0xFFf093fb), Color(0xFFf5576c)), onWorkoutClick),
        QuickAction("💊", "Medicine", listOf(Color(0xFF4CAF50), Color(0xFF81C784)), onMedicineClick),
        QuickAction("✨", "Skincare", listOf(Color(0xFFE91E63), Color(0xFFF48FB1)), onSkincareClick)
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

