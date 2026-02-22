package com.example.mindsync.presentation.lifestyle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import java.util.UUID

data class ScreenBreakReminder(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val intervalMinutes: Int,
    val breakDurationSeconds: Int,
    val isEnabled: Boolean = true,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTimeScreen(
    onNavigateBack: () -> Unit = {}
) {
    var breakReminders by remember {
        mutableStateOf(
            listOf(
                ScreenBreakReminder(
                    name = "Eye Rest (20-20-20)",
                    intervalMinutes = 20,
                    breakDurationSeconds = 20,
                    emoji = "👁️"
                ),
                ScreenBreakReminder(
                    name = "Stand & Stretch",
                    intervalMinutes = 30,
                    breakDurationSeconds = 60,
                    emoji = "🧍"
                ),
                ScreenBreakReminder(
                    name = "Walk Break",
                    intervalMinutes = 60,
                    breakDurationSeconds = 300,
                    emoji = "🚶"
                ),
                ScreenBreakReminder(
                    name = "Hydration Reminder",
                    intervalMinutes = 45,
                    breakDurationSeconds = 30,
                    emoji = "💧"
                )
            )
        )
    }
    
    var focusModeEnabled by remember { mutableStateOf(false) }
    var dailyLimitHours by remember { mutableStateOf(6) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Screen Time & Breaks", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF7C4DFF), Color(0xFFB388FF))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📱", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Take Care of Your Eyes",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Regular breaks reduce eye strain",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                // 20-20-20 Rule Explanation
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "👁️ The 20-20-20 Rule",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Every 20 minutes, look at something 20 feet away for 20 seconds. This helps reduce digital eye strain.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                // Focus Mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Focus Mode",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Block distracting notifications",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = focusModeEnabled,
                            onCheckedChange = { focusModeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF7C4DFF)
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    "Break Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            breakReminders.forEachIndexed { index, reminder ->
                item {
                    BreakReminderCard(
                        reminder = reminder,
                        onToggle = {
                            breakReminders = breakReminders.toMutableList().apply {
                                this[index] = reminder.copy(isEnabled = !reminder.isEnabled)
                            }
                        }
                    )
                }
            }

            item {
                // Add Custom Break
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Custom Break Reminder")
                }
            }

            item {
                // Tips Card
                Text(
                    "Healthy Screen Habits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val tips = listOf(
                            "💡 Adjust screen brightness to match surroundings",
                            "📏 Keep screen at arm's length distance",
                            "🪑 Maintain proper posture while working",
                            "🌙 Use night mode/blue light filter in evening",
                            "😊 Blink often to prevent dry eyes",
                            "🖥️ Position screen slightly below eye level"
                        )
                        tips.forEach { tip ->
                            Text(
                                tip,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BreakReminderCard(
    reminder: ScreenBreakReminder,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isEnabled) MaterialTheme.colorScheme.surface 
                           else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (reminder.isEnabled) Color(0xFFEDE7F6) else Color(0xFFEEEEEE)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(reminder.emoji, style = MaterialTheme.typography.titleLarge)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    reminder.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Every ${reminder.intervalMinutes} min • ${reminder.breakDurationSeconds}s break",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = reminder.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF7C4DFF)
                )
            )
        }
    }
}
