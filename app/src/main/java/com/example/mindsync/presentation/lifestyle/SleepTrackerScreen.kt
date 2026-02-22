package com.example.mindsync.presentation.lifestyle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.mindsync.domain.model.SleepLog
import com.example.mindsync.domain.model.SleepQuality
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTrackerScreen(
    onNavigateBack: () -> Unit = {}
) {
    var bedTime by remember { mutableStateOf("22:30") }
    var wakeTime by remember { mutableStateOf("06:30") }
    var targetHours by remember { mutableStateOf(8f) }
    var bedtimeReminderEnabled by remember { mutableStateOf(true) }
    
    // Edit dialog state
    var showEditDialog by remember { mutableStateOf(false) }
    var editBedTime by remember { mutableStateOf(bedTime) }
    var editWakeTime by remember { mutableStateOf(wakeTime) }
    var editTargetHours by remember { mutableStateOf(targetHours) }
    
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("⏰ Edit Sleep Schedule") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editBedTime,
                        onValueChange = { editBedTime = it },
                        label = { Text("🌙 Bedtime (HH:MM)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editWakeTime,
                        onValueChange = { editWakeTime = it },
                        label = { Text("☀️ Wake Time (HH:MM)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text("Target Hours: ${editTargetHours.toInt()}h", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = editTargetHours,
                        onValueChange = { editTargetHours = it },
                        valueRange = 5f..10f,
                        steps = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        bedTime = editBedTime
                        wakeTime = editWakeTime
                        targetHours = editTargetHours
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    val sleepLogs = remember {
        listOf(
            SleepLog(date = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000, quality = SleepQuality.GOOD),
            SleepLog(date = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000, quality = SleepQuality.EXCELLENT),
            SleepLog(date = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000, quality = SleepQuality.FAIR),
            SleepLog(date = System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000, quality = SleepQuality.GOOD),
            SleepLog(date = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000, quality = SleepQuality.POOR),
            SleepLog(date = System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000, quality = SleepQuality.GOOD),
            SleepLog(date = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000, quality = SleepQuality.EXCELLENT)
        )
    }
    
    val avgQuality = sleepLogs.map { 
        when (it.quality) {
            SleepQuality.EXCELLENT -> 5
            SleepQuality.GOOD -> 4
            SleepQuality.FAIR -> 3
            SleepQuality.POOR -> 2
            SleepQuality.TERRIBLE -> 1
        }
    }.average()

    val darkBackground = Color(0xFF0D0D0D)
    val cardBackground = Color(0xFF1A1A1A)
    
    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Sleep Tracker", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        editBedTime = bedTime
                        editWakeTime = wakeTime
                        editTargetHours = targetHours
                        showEditDialog = true 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Schedule", tint = Color.White)
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
                
                // Sleep Stats Card
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
                                    colors = listOf(Color(0xFF1a237e), Color(0xFF534bae))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("😴", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Sleep Schedule",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TimeCard("🌙 Bedtime", bedTime, Color.White)
                                TimeCard("☀️ Wake Up", wakeTime, Color.White)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Target: ${targetHours.toInt()} hours",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                // Reminder Toggle
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
                            Text("🔔", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Bedtime Reminder",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "30 minutes before bedtime",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = bedtimeReminderEnabled,
                            onCheckedChange = { bedtimeReminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF1a237e)
                            )
                        )
                    }
                }
            }

            item {
                // Weekly Stats
                Text(
                    "This Week's Sleep Quality",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            sleepLogs.reversed().forEach { log ->
                                val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                                SleepDayIndicator(
                                    day = dayFormat.format(Date(log.date)),
                                    quality = log.quality
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Average Quality",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                when {
                                    avgQuality >= 4.5 -> "Excellent 😴"
                                    avgQuality >= 3.5 -> "Good 🙂"
                                    avgQuality >= 2.5 -> "Fair 😐"
                                    else -> "Poor 😫"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1a237e)
                            )
                        }
                    }
                }
            }

            item {
                // Sleep Tips
                Text(
                    "Sleep Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val tips = listOf(
                            "📵 Avoid screens 1 hour before bed",
                            "🌡️ Keep bedroom cool (65-68°F)",
                            "☕ No caffeine after 2 PM",
                            "🏃 Exercise regularly but not before bed",
                            "📅 Maintain consistent sleep schedule",
                            "🧘 Try relaxation techniques before sleep"
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
private fun TimeCard(label: String, time: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
        Text(
            time,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun SleepDayIndicator(day: String, quality: SleepQuality) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when (quality) {
                        SleepQuality.EXCELLENT -> Color(0xFF4CAF50)
                        SleepQuality.GOOD -> Color(0xFF8BC34A)
                        SleepQuality.FAIR -> Color(0xFFFFC107)
                        SleepQuality.POOR -> Color(0xFFFF9800)
                        SleepQuality.TERRIBLE -> Color(0xFFF44336)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(quality.emoji, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            day,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
