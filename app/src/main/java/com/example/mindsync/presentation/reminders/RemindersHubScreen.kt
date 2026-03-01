package com.example.mindsync.presentation.reminders

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ReminderCategory(
    val id: String,
    val name: String,
    val emoji: String,
    val count: Int,
    val colors: List<Color>,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersHubScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToMedicine: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToSkincare: () -> Unit = {},
    onNavigateToMeditation: () -> Unit = {},
    onNavigateToGrocery: () -> Unit = {},
    onNavigateToAssignments: () -> Unit = {},
    onNavigateToFamily: () -> Unit = {},
    onNavigateToWater: () -> Unit = {},
    onNavigateToBills: () -> Unit = {},
    onNavigateToBirthdays: () -> Unit = {},
    onNavigateToSleep: () -> Unit = {},
    onNavigateToVehicle: () -> Unit = {},
    onNavigateToScreenTime: () -> Unit = {},
    onNavigateToPlants: () -> Unit = {},
    viewModel: com.example.mindsync.presentation.reminder.ReminderViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Health", "Personal", "Shopping", "Studies", "Family")

    val healthReminders = listOf(
        ReminderCategory("medicine", "Medicine & Refills", "💊", state.medicineCount, listOf(Color(0xFF03DAC5), Color(0xFF64FFDA)), "medicine"),
        ReminderCategory("workout", "Workout", "💪", state.workoutCount, listOf(Color(0xFF03DAC5), Color(0xFF00C2A8)), "workout"),
        ReminderCategory("skincare", "Skincare", "✨", state.skincareCount, listOf(Color(0xFF03DAC5), Color(0xFFBB86FC)), "skincare"),
        ReminderCategory("water", "Water Intake", "💧", 0, listOf(Color(0xFF03DAC5), Color(0xFF84FFFF)), "water"),
        ReminderCategory("sleep", "Sleep Tracker", "😴", 0, listOf(Color(0xFFBB86FC), Color(0xFF3700B3)), "sleep")
    )

    val personalReminders = listOf(
        ReminderCategory("meditation", "Meditation", "🧘", state.meditationCount, listOf(Color(0xFFBB86FC), Color(0xFF9C27B0)), "meditation"),
        ReminderCategory("screen_time", "Screen Breaks", "📱", 0, listOf(Color(0xFF03DAC5), Color(0xFFBB86FC)), "screen_time"),
        ReminderCategory("birthdays", "Birthdays", "🎂", 0, listOf(Color(0xFFFFD54F), Color(0xFFFFC107)), "birthdays"),
        ReminderCategory("bills", "Bills", "💰", 0, listOf(Color(0xFFFFD54F), Color(0xFF03DAC5)), "bills"),
        ReminderCategory("vehicle", "Vehicle Care", "🚗", 0, listOf(Color(0xFF03DAC5), Color(0xFF64FFDA)), "vehicle"),
        ReminderCategory("plants", "Plant Care", "🌱", 0, listOf(Color(0xFF03DAC5), Color(0xFF00C2A8)), "plants")
    )

    val shoppingReminders = listOf(
        ReminderCategory("grocery", "Grocery List", "🛒", 0, listOf(Color(0xFF03DAC5), Color(0xFF64FFDA)), "grocery")
    )

    val studyReminders = listOf(
        ReminderCategory("assignments", "Assignments", "📚", 0, listOf(Color(0xFFBB86FC), Color(0xFF03DAC5)), "assignments"),
        ReminderCategory("tests", "Test Revision", "📝", 0, listOf(Color(0xFFBB86FC), Color(0xFF9C27B0)), "tests")
    )

    val familyReminders = listOf(
        ReminderCategory("family_medicine", "Family Medicine", "👨‍👩‍👧", 0, listOf(Color(0xFFBB86FC), Color(0xFF03DAC5)), "family")
    )

    val allCategories = when (selectedTab) {
        0 -> healthReminders + personalReminders + shoppingReminders + studyReminders + familyReminders
        1 -> healthReminders
        2 -> personalReminders
        3 -> shoppingReminders
        4 -> studyReminders
        5 -> familyReminders
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Reminders", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quick Stats
                item {
                    QuickStatsCard(
                        pendingCount = state.pendingCount,
                        completedCount = state.completedCount,
                        upcomingCount = state.upcomingCount
                    )
                }

                item {
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(allCategories) { category ->
                    ReminderCategoryCard(
                        category = category,
                        onClick = {
                            when (category.id) {
                                "medicine" -> onNavigateToMedicine()
                                "workout" -> onNavigateToWorkout()
                                "skincare" -> onNavigateToSkincare()
                                "meditation" -> onNavigateToMeditation()
                                "grocery" -> onNavigateToGrocery()
                                "assignments" -> onNavigateToAssignments()
                                "family_medicine" -> onNavigateToFamily()
                                "water" -> onNavigateToWater()
                                "bills" -> onNavigateToBills()
                                "birthdays" -> onNavigateToBirthdays()
                                "sleep" -> onNavigateToSleep()
                                "vehicle" -> onNavigateToVehicle()
                                "screen_time" -> onNavigateToScreenTime()
                                "plants" -> onNavigateToPlants()
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
private fun QuickStatsCard(
    pendingCount: Int,
    completedCount: Int,
    upcomingCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFB8A9E8), Color(0xFFF5B8D0))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    "📊 Today's Overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Pending", "$pendingCount", Color.White)
                    StatItem("Completed", "$completedCount", Color.White)
                    StatItem("Upcoming", "$upcomingCount", Color.White)
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderCategoryCard(
    category: ReminderCategory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(category.colors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(category.emoji, style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${category.count} reminders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
