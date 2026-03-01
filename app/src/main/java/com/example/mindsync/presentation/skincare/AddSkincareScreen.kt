package com.example.mindsync.presentation.skincare

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mindsync.domain.model.SkincareCategory
import com.example.mindsync.domain.model.SkincareRoutineType
import com.example.mindsync.domain.model.SkincareStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSkincareScreen(
    onNavigateBack: () -> Unit = {},
    onSave: () -> Unit = {},
    viewModel: SkincareViewModel = org.koin.androidx.compose.koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(SkincareRoutineType.MORNING) }
    var estimatedMinutes by remember { mutableStateOf("15") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var selectedSteps by remember { mutableStateOf<List<SkincareCategory>>(emptyList()) }
    var scheduledTime by remember { mutableStateOf(0L) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Skincare Routine", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val steps = selectedSteps.mapIndexed { index, category ->
                                SkincareStep(
                                    name = category.displayName,
                                    productName = "",
                                    category = category,
                                    orderIndex = index
                                )
                            }
                            viewModel.addRoutine(
                                name = name,
                                description = description,
                                routineType = selectedType,
                                estimatedMinutes = estimatedMinutes.toIntOrNull() ?: 15,
                                scheduledTime = scheduledTime,
                                reminderEnabled = reminderEnabled,
                                steps = steps
                            )
                            onSave()
                            onNavigateBack()
                        },
                        enabled = name.isNotBlank() && selectedSteps.isNotEmpty()
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✨", style = MaterialTheme.typography.displayMedium)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Create Your Routine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Build a personalized skincare routine",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Routine Name") },
                placeholder = { Text("e.g., Morning Glow, Night Repair") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Spa, contentDescription = null) }
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                placeholder = { Text("Describe your routine goals") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            // Routine Type Selection
            Text(
                "Routine Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SkincareRoutineType.entries) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text("${type.emoji} ${type.displayName}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE91E63),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Duration Field
            OutlinedTextField(
                value = estimatedMinutes,
                onValueChange = { estimatedMinutes = it.filter { c -> c.isDigit() } },
                label = { Text("Estimated Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) }
            )

            // Steps Selection
            Text(
                "Select Steps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Tap to add steps to your routine",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Step Categories Grid
            val stepCategories = SkincareCategory.entries
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                stepCategories.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { category ->
                            val isSelected = selectedSteps.contains(category)
                            Card(
                                onClick = {
                                    selectedSteps = if (isSelected) {
                                        selectedSteps - category
                                    } else {
                                        selectedSteps + category
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFE91E63) 
                                                    else MaterialTheme.colorScheme.surface
                                ),
                                border = if (!isSelected) CardDefaults.outlinedCardBorder() else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(category.emoji, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        category.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) Color.White 
                                               else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Selected Steps Preview
            if (selectedSteps.isNotEmpty()) {
                Text(
                    "Your Routine (${selectedSteps.size} steps)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                selectedSteps.forEachIndexed { index, category ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE91E63)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(category.emoji, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                category.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { selectedSteps = selectedSteps - category }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            // Reminder Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFFE91E63)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Enable Reminders",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Get notified for your routine",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFE91E63)
                        )
                    )
                }
            }
            
            // Schedule Time Section
            if (reminderEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Schedule Time",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (scheduledTime > 0) {
                            val timeFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "⏰ ${timeFormat.format(java.util.Date(scheduledTime))}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                TextButton(onClick = { showTimePicker = true }) {
                                    Text("Change")
                                }
                            }
                        } else {
                            Text(
                                "No time set. Tap below to schedule.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (scheduledTime > 0) "Change Time" else "Set Reminder Time")
                        }
                    }
                }
            }
            
            // Time Picker Dialog
            if (showTimePicker) {
                var selectedHour by remember { mutableStateOf(if (selectedType == SkincareRoutineType.MORNING) 8 else 21) }
                var selectedMinute by remember { mutableStateOf(0) }
                
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    title = { Text("Select Time") },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Hour picker
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(onClick = { selectedHour = (selectedHour + 1) % 24 }) {
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase hour")
                                    }
                                    Text(
                                        String.format("%02d", selectedHour),
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    IconButton(onClick = { selectedHour = if (selectedHour > 0) selectedHour - 1 else 23 }) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease hour")
                                    }
                                }
                                
                                Text(":", style = MaterialTheme.typography.headlineMedium)
                                
                                // Minute picker
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(onClick = { selectedMinute = (selectedMinute + 5) % 60 }) {
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase minute")
                                    }
                                    Text(
                                        String.format("%02d", selectedMinute),
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    IconButton(onClick = { selectedMinute = if (selectedMinute >= 5) selectedMinute - 5 else 55 }) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease minute")
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (selectedHour < 12) "AM" else "PM",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val calendar = java.util.Calendar.getInstance()
                                calendar.set(java.util.Calendar.HOUR_OF_DAY, selectedHour)
                                calendar.set(java.util.Calendar.MINUTE, selectedMinute)
                                calendar.set(java.util.Calendar.SECOND, 0)
                                calendar.set(java.util.Calendar.MILLISECOND, 0)
                                scheduledTime = calendar.timeInMillis
                                showTimePicker = false
                            }
                        ) {
                            Text("Set")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Recommended Routines
            Text(
                "Recommended Routines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            val recommendedRoutines = listOf(
                Triple("🌅 Basic Morning", "Cleanser → Moisturizer → Sunscreen", listOf(SkincareCategory.CLEANSER, SkincareCategory.MOISTURIZER, SkincareCategory.SUNSCREEN)),
                Triple("🌙 Basic Night", "Cleanser → Toner → Moisturizer", listOf(SkincareCategory.CLEANSER, SkincareCategory.TONER, SkincareCategory.MOISTURIZER)),
                Triple("✨ K-Beauty Morning", "Cleanser → Toner → Serum → Moisturizer → Sunscreen", listOf(SkincareCategory.CLEANSER, SkincareCategory.TONER, SkincareCategory.SERUM, SkincareCategory.MOISTURIZER, SkincareCategory.SUNSCREEN)),
                Triple("💆 Full Night Routine", "Double Cleanse → Toner → Serum → Eye Cream → Moisturizer", listOf(SkincareCategory.CLEANSER, SkincareCategory.TONER, SkincareCategory.SERUM, SkincareCategory.EYE_CREAM, SkincareCategory.MOISTURIZER))
            )

            recommendedRoutines.forEach { (title, steps, categories) ->
                Card(
                    onClick = {
                        name = title.substringAfter(" ")
                        selectedSteps = categories
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            steps,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
