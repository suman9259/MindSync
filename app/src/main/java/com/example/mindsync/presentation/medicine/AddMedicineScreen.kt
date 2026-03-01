package com.example.mindsync.presentation.medicine

import androidx.compose.foundation.layout.*
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
import com.example.mindsync.domain.model.MedicineFrequency
import com.example.mindsync.domain.model.MedicineUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    onNavigateBack: () -> Unit = {},
    onSave: () -> Unit = {},
    viewModel: MedicineViewModel = org.koin.androidx.compose.koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf(MedicineUnit.TABLET) }
    var selectedFrequency by remember { mutableStateOf(MedicineFrequency.DAILY) }
    var instructions by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var unitExpanded by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }
    var timesPerDay by remember { mutableStateOf(1) }
    
    // Time picker state
    var showTimePicker by remember { mutableStateOf(false) }
    var scheduledTimes by remember { mutableStateOf(listOf<Long>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.addMedicine(
                                name = name,
                                description = description,
                                dosage = dosage,
                                unit = selectedUnit,
                                frequency = selectedFrequency,
                                timesPerDay = timesPerDay,
                                scheduledTimes = scheduledTimes,
                                instructions = instructions,
                                reminderEnabled = reminderEnabled
                            )
                            onSave()
                            onNavigateBack()
                        },
                        enabled = name.isNotBlank() && dosage.isNotBlank()
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
            // Medicine Icon
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💊", style = MaterialTheme.typography.displayMedium)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Add New Medicine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Track your supplements & medications",
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
                label = { Text("Medicine Name") },
                placeholder = { Text("e.g., Vitamin D3, Aspirin") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Medication, contentDescription = null) }
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                placeholder = { Text("What is this medicine for?") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            // Dosage Field
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage") },
                placeholder = { Text("e.g., 1000mg, 500 IU") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) }
            )

            // Unit Dropdown
            ExposedDropdownMenuBox(
                expanded = unitExpanded,
                onExpandedChange = { unitExpanded = !unitExpanded }
            ) {
                OutlinedTextField(
                    value = selectedUnit.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = unitExpanded,
                    onDismissRequest = { unitExpanded = false }
                ) {
                    MedicineUnit.entries.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit.displayName) },
                            onClick = {
                                selectedUnit = unit
                                unitExpanded = false
                            }
                        )
                    }
                }
            }

            // Frequency Dropdown
            ExposedDropdownMenuBox(
                expanded = frequencyExpanded,
                onExpandedChange = { frequencyExpanded = !frequencyExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFrequency.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Frequency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = frequencyExpanded,
                    onDismissRequest = { frequencyExpanded = false }
                ) {
                    MedicineFrequency.entries.forEach { frequency ->
                        DropdownMenuItem(
                            text = { Text(frequency.displayName) },
                            onClick = {
                                selectedFrequency = frequency
                                frequencyExpanded = false
                            }
                        )
                    }
                }
            }

            // Instructions Field
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions (Optional)") },
                placeholder = { Text("e.g., Take with food, before bed") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

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
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Enable Reminders",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Get notified when it's time",
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
                            checkedTrackColor = Color(0xFF4CAF50)
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
                                tint = Color(0xFF03DAC5)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Schedule Time",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Time slots
                        if (scheduledTimes.isEmpty()) {
                            Text(
                                "No time scheduled. Add a reminder time below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            scheduledTimes.forEachIndexed { index, time ->
                                val timeFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "⏰ ${timeFormat.format(java.util.Date(time))}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    IconButton(
                                        onClick = {
                                            scheduledTimes = scheduledTimes.filterIndexed { i, _ -> i != index }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove time",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Add time button
                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Reminder Time")
                        }
                    }
                }
            }
            
            // Time Picker Dialog
            if (showTimePicker) {
                var selectedHour by remember { mutableStateOf(8) }
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
                                // Create timestamp for today with selected time
                                val calendar = java.util.Calendar.getInstance()
                                calendar.set(java.util.Calendar.HOUR_OF_DAY, selectedHour)
                                calendar.set(java.util.Calendar.MINUTE, selectedMinute)
                                calendar.set(java.util.Calendar.SECOND, 0)
                                calendar.set(java.util.Calendar.MILLISECOND, 0)
                                scheduledTimes = scheduledTimes + calendar.timeInMillis
                                showTimePicker = false
                            }
                        ) {
                            Text("Add")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Popular Supplements Section
            Text(
                "Popular Supplements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            val popularSupplements = listOf(
                Triple("🌞", "Vitamin D3", "1000 IU daily - Bone health & immunity"),
                Triple("🐟", "Omega-3 Fish Oil", "1000mg daily - Heart & brain health"),
                Triple("🧬", "Multivitamin", "1 tablet daily - Complete nutrition"),
                Triple("🦠", "Probiotic", "10B CFU daily - Gut health"),
                Triple("💪", "Vitamin B12", "1000mcg daily - Energy & nerves"),
                Triple("🧡", "Vitamin C", "500mg daily - Immunity boost"),
                Triple("🦴", "Calcium", "500mg daily - Bone strength"),
                Triple("🧲", "Magnesium", "400mg daily - Muscle & sleep")
            )

            popularSupplements.forEach { (emoji, supplementName, desc) ->
                Card(
                    onClick = {
                        name = supplementName
                        description = desc.substringAfter(" - ")
                        dosage = desc.substringBefore(" -").trim()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (name == supplementName) Color(0xFFE8F5E9) 
                                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                supplementName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (name == supplementName) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
