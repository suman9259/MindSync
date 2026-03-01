package com.example.mindsync.presentation.medicine

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mindsync.domain.model.Medicine
import com.example.mindsync.domain.model.MedicineFrequency
import com.example.mindsync.domain.model.MedicineUnit
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddMedicine: () -> Unit = {},
    onNavigateToMedicineDetail: (String) -> Unit = {},
    viewModel: MedicineViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val medicines = state.medicines
    
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Morning", "Afternoon", "Evening", "Night")
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var medicineToDelete by remember { mutableStateOf<Medicine?>(null) }
    var showReminderSettings by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var medicineToEdit by remember { mutableStateOf<Medicine?>(null) }
    
    // Reminder settings dialog
    if (showReminderSettings) {
        AlertDialog(
            onDismissRequest = { showReminderSettings = false },
            title = { Text("🔔 Reminder Settings") },
            text = {
                Column {
                    if (medicines.isEmpty()) {
                        Text("No medicines added yet.")
                    } else {
                        Text("Manage reminders for your medicines.")
                        Spacer(modifier = Modifier.height(8.dp))
                        medicines.forEach { med ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(med.name, style = MaterialTheme.typography.bodyMedium)
                                Switch(
                                    checked = med.reminderEnabled,
                                    onCheckedChange = { enabled ->
                                        viewModel.toggleReminder(med.id, enabled)
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReminderSettings = false }) {
                    Text("Done")
                }
            }
        )
    }
    
    // Edit dialog
    if (showEditDialog && medicineToEdit != null) {
        var editName by remember { mutableStateOf(medicineToEdit!!.name) }
        var editDosage by remember { mutableStateOf(medicineToEdit!!.dosage) }
        var editDescription by remember { mutableStateOf(medicineToEdit!!.description) }
        var editInstructions by remember { mutableStateOf(medicineToEdit!!.instructions) }
        
        AlertDialog(
            onDismissRequest = { showEditDialog = false; medicineToEdit = null },
            title = { Text("✏️ Edit Medicine") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDosage,
                        onValueChange = { editDosage = it },
                        label = { Text("Dosage") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editInstructions,
                        onValueChange = { editInstructions = it },
                        label = { Text("Instructions") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        medicineToEdit?.let { med ->
                            viewModel.updateMedicine(
                                id = med.id,
                                name = editName,
                                description = editDescription,
                                dosage = editDosage,
                                unit = med.unit,
                                frequency = med.frequency,
                                instructions = editInstructions,
                                reminderEnabled = med.reminderEnabled
                            )
                        }
                        showEditDialog = false
                        medicineToEdit = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; medicineToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showDeleteDialog && medicineToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Medicine") },
            text = { Text("Remove ${medicineToDelete?.name} from your list?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        medicineToDelete?.let { viewModel.deleteMedicine(it.id) }
                        showDeleteDialog = false
                        medicineToDelete = null
                    }
                ) {
                    Text("Remove", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val darkBackground = Color(0xFF121212)  // Material dark background
    val cardBackground = Color(0xFF1E1E1E)  // Material dark surface
    val tealAccent = Color(0xFF03DAC5)       // Material Teal accent
    
    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Medicine & Supplements", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showReminderSettings = true }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Reminders", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddMedicine,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Medicine") },
                containerColor = tealAccent
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
                                    colors = listOf(Color(0xFF03DAC5), Color(0xFF64FFDA))  // Material Teal gradient
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                "💊 Today's Medicines",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Stay on track with your health routine",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Taken", "${state.takenCount}", Color.White)
                                StatItem("Pending", "${state.pendingCount}", Color.White)
                                StatItem("Streak", "${state.maxStreak} days", Color.White)
                            }
                        }
                    }
                }
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF03DAC5),  // Material Teal
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
            
            item {
                Text(
                    "Your Medicines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (medicines.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💊", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No medicines added yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                "Tap the button below to add your first medicine",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            items(medicines) { medicine ->
                MedicineCard(
                    medicine = medicine,
                    onToggleTaken = {
                        viewModel.toggleMedicineTaken(medicine.id)
                    },
                    onClick = { onNavigateToMedicineDetail(medicine.id) },
                    onEdit = {
                        medicineToEdit = medicine
                        showEditDialog = true
                    },
                    onDelete = {
                        medicineToDelete = medicine
                        showDeleteDialog = true
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicineCard(
    medicine: Medicine,
    onToggleTaken: () -> Unit,
    onClick: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        if (medicine.takenToday) Color(0xFF03DAC5).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        label = "bgColor"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
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
                        if (medicine.takenToday) Color(0xFF03DAC5)  // Material Teal
                        else Color(0xFF03DAC5).copy(alpha = 0.3f)  // Teal light
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (medicine.takenToday) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Taken",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Text("💊", style = MaterialTheme.typography.headlineSmall)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    medicine.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${medicine.dosage} ${medicine.unit.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        medicine.frequency.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (medicine.currentStreak > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "🔥 ${medicine.currentStreak} day streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFD54F)  // Amber
                        )
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Button(
                    onClick = onToggleTaken,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (medicine.takenToday) Color(0xFFCF6679) else Color(0xFF03DAC5)  // Error or Teal
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        if (medicine.takenToday) "Undo" else "Take",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
