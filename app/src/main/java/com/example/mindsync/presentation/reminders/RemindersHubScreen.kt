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
    onNavigateToPlants: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Health", "Personal", "Shopping", "Studies", "Family")

    val healthReminders = listOf(
        ReminderCategory("medicine", "Medicine & Refills", "💊", 4, listOf(Color(0xFF4CAF50), Color(0xFF81C784)), "medicine"),
        ReminderCategory("workout", "Workout", "💪", 3, listOf(Color(0xFFf093fb), Color(0xFFf5576c)), "workout"),
        ReminderCategory("skincare", "Skincare", "✨", 2, listOf(Color(0xFFE91E63), Color(0xFFF48FB1)), "skincare"),
        ReminderCategory("water", "Water Intake", "💧", 8, listOf(Color(0xFF2196F3), Color(0xFF64B5F6)), "water"),
        ReminderCategory("sleep", "Sleep Tracker", "😴", 1, listOf(Color(0xFF1a237e), Color(0xFF534bae)), "sleep")
    )

    val personalReminders = listOf(
        ReminderCategory("meditation", "Meditation", "🧘", 2, listOf(Color(0xFF667eea), Color(0xFF764ba2)), "meditation"),
        ReminderCategory("screen_time", "Screen Breaks", "�", 4, listOf(Color(0xFF7C4DFF), Color(0xFFB388FF)), "screen_time"),
        ReminderCategory("birthdays", "Birthdays", "🎂", 5, listOf(Color(0xFFFF9800), Color(0xFFFFB74D)), "birthdays"),
        ReminderCategory("bills", "Bills", "💰", 3, listOf(Color(0xFF795548), Color(0xFFA1887F)), "bills"),
        ReminderCategory("vehicle", "Vehicle Care", "🚗", 2, listOf(Color(0xFF546E7A), Color(0xFF78909C)), "vehicle"),
        ReminderCategory("plants", "Plant Care", "🌱", 5, listOf(Color(0xFF4CAF50), Color(0xFF81C784)), "plants")
    )

    val shoppingReminders = listOf(
        ReminderCategory("grocery", "Grocery List", "🛒", 12, listOf(Color(0xFF009688), Color(0xFF4DB6AC)), "grocery")
    )

    val studyReminders = listOf(
        ReminderCategory("assignments", "Assignments", "📚", 4, listOf(Color(0xFF3F51B5), Color(0xFF7986CB)), "assignments"),
        ReminderCategory("tests", "Test Revision", "📝", 2, listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)), "tests")
    )

    val familyReminders = listOf(
        ReminderCategory("family_medicine", "Family Medicine", "👨‍👩‍👧", 6, listOf(Color(0xFFE91E63), Color(0xFFF06292)), "family")
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
                    QuickStatsCard()
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
private fun QuickStatsCard() {
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
                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
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
                    StatItem("Pending", "12", Color.White)
                    StatItem("Completed", "8", Color.White)
                    StatItem("Upcoming", "5", Color.White)
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
