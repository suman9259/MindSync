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
    onNavigateToMedicineDetail: (String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Morning", "Afternoon", "Evening", "Night")
    
    var medicines by remember {
        mutableStateOf(
            listOf(
                Medicine(
                    name = "Vitamin D3",
                    description = "Essential for bone health, immune function, and mood regulation. Best absorbed with fatty foods.",
                    dosage = "1000 IU",
                    unit = MedicineUnit.TABLET,
                    frequency = MedicineFrequency.DAILY,
                    timesPerDay = 1,
                    reminderEnabled = true,
                    takenToday = true,
                    currentStreak = 15
                ),
                Medicine(
                    name = "Omega-3 Fish Oil",
                    description = "Supports heart, brain, and joint health. Contains EPA and DHA fatty acids.",
                    dosage = "1000mg",
                    unit = MedicineUnit.CAPSULE,
                    frequency = MedicineFrequency.DAILY,
                    timesPerDay = 2,
                    reminderEnabled = true,
                    takenToday = false,
                    currentStreak = 7
                ),
                Medicine(
                    name = "Multivitamin",
                    description = "Complete daily nutrition with essential vitamins A, C, D, E, K and B-complex.",
                    dosage = "1",
                    unit = MedicineUnit.TABLET,
                    frequency = MedicineFrequency.DAILY,
                    timesPerDay = 1,
                    reminderEnabled = true,
                    takenToday = false,
                    currentStreak = 30
                ),
                Medicine(
                    name = "Probiotic",
                    description = "Supports gut health with 10 billion CFU of beneficial bacteria strains.",
                    dosage = "10 billion CFU",
                    unit = MedicineUnit.CAPSULE,
                    frequency = MedicineFrequency.DAILY,
                    timesPerDay = 1,
                    reminderEnabled = true,
                    takenToday = true,
                    currentStreak = 12
                ),
                Medicine(
                    name = "Magnesium Glycinate",
                    description = "Supports muscle relaxation, sleep quality, and stress management. Highly bioavailable form.",
                    dosage = "400mg",
                    unit = MedicineUnit.CAPSULE,
                    frequency = MedicineFrequency.DAILY,
                    timesPerDay = 1,
                    reminderEnabled = true,
                    takenToday = false,
                    currentStreak = 8
                ),
                Medicine(
                    name = "Vitamin B12",
                    description = "Essential for energy production, nerve function, and red blood cell formation.",
                    dosage = "1000mcg",
                    unit = MedicineUnit.TABLET,
                    frequency = MedicineFrequency.DAILY,
                    timesPerDay = 1,
                    reminderEnabled = true,
                    takenToday = false,
                    currentStreak = 5
                )
            )
        )
    }
    
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
                    Text("All reminders are enabled for your medicines.")
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
                                    medicines = medicines.map { 
                                        if (it.id == med.id) it.copy(reminderEnabled = enabled) else it 
                                    }
                                }
                            )
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
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        medicines = medicines.map {
                            if (it.id == medicineToEdit?.id) it.copy(name = editName, dosage = editDosage) else it
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
                        medicines = medicines.filter { it.id != medicineToDelete?.id }
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
                                StatItem("Taken", "2", Color.White)
                                StatItem("Pending", "2", Color.White)
                                StatItem("Streak", "15 days", Color.White)
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
            
            items(medicines) { medicine ->
                MedicineCard(
                    medicine = medicine,
                    onToggleTaken = {
                        medicines = medicines.map {
                            if (it.id == medicine.id) it.copy(takenToday = !it.takenToday) else it
                        }
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
