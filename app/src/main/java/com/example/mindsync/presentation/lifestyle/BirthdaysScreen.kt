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
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mindsync.domain.model.SpecialDate
import com.example.mindsync.domain.model.SpecialDateType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdaysScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: BirthdaysViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val specialDates = state.specialDates

    val upcomingThisWeek = specialDates.filter { 
        val daysUntil = (it.date - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)
        daysUntil in 0..7
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var newPersonName by remember { mutableStateOf("") }
    var newDateType by remember { mutableStateOf(SpecialDateType.BIRTHDAY) }
    var newGiftIdeas by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedHour by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("🎂 Add Special Date") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isEventType = newDateType == SpecialDateType.EVENT
                    
                    OutlinedTextField(
                        value = newPersonName,
                        onValueChange = { newPersonName = it },
                        label = { Text(if (isEventType) "Event Title" else "Person's Name") },
                        placeholder = { Text(if (isEventType) "e.g., Flight to NYC" else "e.g., John") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Text("Type:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            SpecialDateType.BIRTHDAY,
                            SpecialDateType.ANNIVERSARY,
                            SpecialDateType.EVENT,
                            SpecialDateType.GRADUATION
                        ).forEach { type ->
                            FilterChip(
                                selected = newDateType == type,
                                onClick = { newDateType = type },
                                label = { Text(type.emoji, style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Text("Date:", style = MaterialTheme.typography.labelMedium)
                    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dateFormat.format(java.util.Date(selectedDate)))
                    }
                    
                    // Show time picker only for EVENT type
                    if (isEventType) {
                        Text("Time:", style = MaterialTheme.typography.labelMedium)
                        val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                        val timeCalendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                        }
                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(timeFormat.format(timeCalendar.time))
                        }
                    }
                    
                    OutlinedTextField(
                        value = newGiftIdeas,
                        onValueChange = { newGiftIdeas = it },
                        label = { Text(if (isEventType) "Notes (optional)" else "Gift Ideas (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPersonName.isNotBlank()) {
                            // Combine date and time for events
                            val finalDate = if (newDateType == SpecialDateType.EVENT) {
                                Calendar.getInstance().apply {
                                    timeInMillis = selectedDate
                                    set(Calendar.HOUR_OF_DAY, selectedHour)
                                    set(Calendar.MINUTE, selectedMinute)
                                    set(Calendar.SECOND, 0)
                                }.timeInMillis
                            } else {
                                selectedDate
                            }
                            
                            viewModel.addSpecialDate(
                                personName = newPersonName,
                                dateType = newDateType,
                                date = finalDate,
                                giftIdeas = newGiftIdeas
                            )
                            newPersonName = ""
                            newGiftIdeas = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val darkBackground = Color(0xFF0D0D0D)
    val cardBackground = Color(0xFF1A1A1A)
    val blueAccent = Color(0xFF4A90D9)
    
    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Birthdays & Events", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Date") },
                containerColor = blueAccent
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Header Card
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
                                    colors = listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                "🎂 Never Miss a Celebration!",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("This Week", "${upcomingThisWeek.size}", Color.White)
                                StatItem("Birthdays", "${specialDates.count { it.dateType == SpecialDateType.BIRTHDAY }}", Color.White)
                                StatItem("Total", "${specialDates.size}", Color.White)
                            }
                        }
                    }
                }
            }

            if (upcomingThisWeek.isNotEmpty()) {
                item {
                    Text(
                        "🔔 Coming Up This Week",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                }

                items(upcomingThisWeek) { date ->
                    SpecialDateCard(
                        specialDate = date,
                        isHighlighted = true,
                        onDelete = {
                            viewModel.deleteSpecialDate(date.id)
                        }
                    )
                }
            }

            item {
                Text(
                    "📅 All Upcoming Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(specialDates.sortedBy { it.date }) { date ->
                if (!upcomingThisWeek.contains(date)) {
                    SpecialDateCard(
                        specialDate = date,
                        isHighlighted = false,
                        onDelete = {
                            viewModel.deleteSpecialDate(date.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
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
private fun SpecialDateCard(
    specialDate: SpecialDate,
    isHighlighted: Boolean,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(specialDate.date))
    
    // For birthdays/anniversaries: calculate next occurrence if date has passed
    val isBirthday = specialDate.dateType == SpecialDateType.BIRTHDAY
    val isAnniversary = specialDate.dateType == SpecialDateType.ANNIVERSARY
    val isRecurring = isBirthday || isAnniversary
    
    val daysUntil: Int
    if (isRecurring) {
        // Get the month and day from the stored date
        val storedCal = Calendar.getInstance().apply { timeInMillis = specialDate.date }
        val storedMonth = storedCal.get(Calendar.MONTH)
        val storedDay = storedCal.get(Calendar.DAY_OF_MONTH)
        
        // Calculate next occurrence
        val nextOccurrence = Calendar.getInstance().apply {
            set(Calendar.MONTH, storedMonth)
            set(Calendar.DAY_OF_MONTH, storedDay)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If this year's date has passed, move to next year
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.YEAR, 1)
            }
        }
        daysUntil = ((nextOccurrence.timeInMillis - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
    } else {
        // For one-time events, use the actual date
        daysUntil = ((specialDate.date - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
    }
    
    // For birthdays: show in years if >= 365 days, months if >= 30 days, else days
    // For events (graduation, etc.): always show in days
    val timeUntilText = when {
        daysUntil == 0 -> "Today! 🎉"
        daysUntil == 1 -> "Tomorrow!"
        daysUntil < 0 && !isRecurring -> "${-daysUntil} days ago"
        !isBirthday -> "In $daysUntil days" // Events always in days
        daysUntil >= 365 -> {
            val years = daysUntil / 365
            if (years == 1) "In 1 year" else "In $years years"
        }
        daysUntil >= 30 -> {
            val months = daysUntil / 30
            if (months == 1) "In 1 month" else "In $months months"
        }
        else -> "In $daysUntil days"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlighted) 4.dp else 2.dp)
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
                        if (isHighlighted) Color(0xFFFF9800) else Color(0xFFFFF3E0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    specialDate.dateType.emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    specialDate.personName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                val isEvent = specialDate.dateType == SpecialDateType.EVENT
                val displayText = if (isEvent) {
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    "${specialDate.dateType.displayName} • $formattedDate at ${timeFormat.format(Date(specialDate.date))}"
                } else {
                    "${specialDate.dateType.displayName} • $formattedDate"
                }
                Text(
                    displayText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (specialDate.year != null) {
                    val age = Calendar.getInstance().get(Calendar.YEAR) - specialDate.year
                    Text(
                        "Turning $age years old",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (specialDate.giftIdeas.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "🎁 ${specialDate.giftIdeas}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Text(
                    timeUntilText,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (daysUntil <= 7) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (daysUntil <= 7) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
