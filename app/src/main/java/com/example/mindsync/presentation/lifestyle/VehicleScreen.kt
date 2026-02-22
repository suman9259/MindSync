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
import com.example.mindsync.domain.model.MaintenanceRecord
import com.example.mindsync.domain.model.MaintenanceType
import com.example.mindsync.domain.model.Vehicle
import com.example.mindsync.domain.model.VehicleType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleScreen(
    onNavigateBack: () -> Unit = {}
) {
    var vehicles by remember {
        mutableStateOf(
            listOf(
                Vehicle(
                    name = "My Car",
                    type = VehicleType.CAR,
                    make = "Honda",
                    model = "City",
                    year = 2021,
                    licensePlate = "MH 01 AB 1234",
                    insuranceExpiry = System.currentTimeMillis() + 45 * 24 * 60 * 60 * 1000,
                    pollutionExpiry = System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000,
                    maintenanceRecords = listOf(
                        MaintenanceRecord(
                            type = MaintenanceType.OIL_CHANGE,
                            date = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000,
                            nextDueDate = System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000,
                            mileage = 25000,
                            cost = 2500.0
                        ),
                        MaintenanceRecord(
                            type = MaintenanceType.SERVICE,
                            date = System.currentTimeMillis() - 90 * 24 * 60 * 60 * 1000,
                            nextDueDate = System.currentTimeMillis() + 90 * 24 * 60 * 60 * 1000,
                            mileage = 23000,
                            cost = 8000.0
                        )
                    )
                ),
                Vehicle(
                    name = "My Bike",
                    type = VehicleType.MOTORCYCLE,
                    make = "Royal Enfield",
                    model = "Classic 350",
                    year = 2022,
                    licensePlate = "MH 02 CD 5678",
                    insuranceExpiry = System.currentTimeMillis() + 120 * 24 * 60 * 60 * 1000,
                    pollutionExpiry = System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000,
                    maintenanceRecords = listOf(
                        MaintenanceRecord(
                            type = MaintenanceType.SERVICE,
                            date = System.currentTimeMillis() - 60 * 24 * 60 * 60 * 1000,
                            nextDueDate = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000,
                            mileage = 8000,
                            cost = 3500.0
                        )
                    )
                )
            )
        )
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newVehicleName by remember { mutableStateOf("") }
    var newVehicleMake by remember { mutableStateOf("") }
    var newVehicleModel by remember { mutableStateOf("") }
    var newVehicleType by remember { mutableStateOf(VehicleType.CAR) }
    var newLicensePlate by remember { mutableStateOf("") }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("🚗 Add Vehicle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newVehicleName,
                        onValueChange = { newVehicleName = it },
                        label = { Text("Vehicle Name") },
                        placeholder = { Text("e.g., My Car") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newVehicleMake,
                            onValueChange = { newVehicleMake = it },
                            label = { Text("Make") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newVehicleModel,
                            onValueChange = { newVehicleModel = it },
                            label = { Text("Model") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = newLicensePlate,
                        onValueChange = { newLicensePlate = it.uppercase() },
                        label = { Text("License Plate") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text("Type:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VehicleType.values().forEach { type ->
                            FilterChip(
                                selected = newVehicleType == type,
                                onClick = { newVehicleType = type },
                                label = { Text(type.emoji) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newVehicleName.isNotBlank()) {
                            vehicles = vehicles + Vehicle(
                                name = newVehicleName,
                                type = newVehicleType,
                                make = newVehicleMake,
                                model = newVehicleModel,
                                year = Calendar.getInstance().get(Calendar.YEAR),
                                licensePlate = newLicensePlate,
                                insuranceExpiry = System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L,
                                pollutionExpiry = System.currentTimeMillis() + 180 * 24 * 60 * 60 * 1000L
                            )
                            newVehicleName = ""
                            newVehicleMake = ""
                            newVehicleModel = ""
                            newLicensePlate = ""
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
                title = { Text("Vehicle Maintenance", fontWeight = FontWeight.Bold, color = Color.White) },
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
                text = { Text("Add Vehicle") },
                containerColor = blueAccent
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
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF546E7A), Color(0xFF78909C))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                "🚗 Vehicle Care",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Keep your vehicles in top condition",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Vehicles", "${vehicles.size}", Color.White)
                                StatItem("Due Soon", "${vehicles.sumOf { v -> 
                                    (if (v.insuranceExpiry != null && v.insuranceExpiry < System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000) 1 else 0) +
                                    (if (v.pollutionExpiry != null && v.pollutionExpiry < System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000) 1 else 0).toInt()
                                }}", Color(0xFFFFCDD2))
                            }
                        }
                    }
                }
            }

            items(vehicles) { vehicle ->
                VehicleCard(vehicle = vehicle)
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
private fun VehicleCard(vehicle: Vehicle) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFECEFF1)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(vehicle.type.emoji, style = MaterialTheme.typography.headlineMedium)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        vehicle.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${vehicle.make} ${vehicle.model} (${vehicle.year})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        vehicle.licensePlate,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF546E7A),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            // Alerts for insurance/pollution
            val insuranceDays = vehicle.insuranceExpiry?.let { 
                ((it - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt() 
            }
            val pollutionDays = vehicle.pollutionExpiry?.let { 
                ((it - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt() 
            }

            if (insuranceDays != null && insuranceDays <= 30) {
                Spacer(modifier = Modifier.height(8.dp))
                AlertChip(
                    text = "Insurance expires in $insuranceDays days",
                    isUrgent = insuranceDays <= 7
                )
            }
            if (pollutionDays != null && pollutionDays <= 30) {
                Spacer(modifier = Modifier.height(4.dp))
                AlertChip(
                    text = "PUC expires in $pollutionDays days",
                    isUrgent = pollutionDays <= 7
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "Maintenance History",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                vehicle.maintenanceRecords.forEach { record ->
                    MaintenanceRow(record = record, dateFormat = dateFormat)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Maintenance Record")
                }
            }
        }
    }
}

@Composable
private fun AlertChip(text: String, isUrgent: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isUrgent) Color(0xFFFFEBEE) else Color(0xFFFFF3E0),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (isUrgent) "⚠️" else "📅",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isUrgent) Color.Red else Color(0xFFE65100)
        )
    }
}

@Composable
private fun MaintenanceRow(record: MaintenanceRecord, dateFormat: SimpleDateFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(record.type.emoji, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                record.type.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Done: ${dateFormat.format(Date(record.date))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            record.nextDueDate?.let {
                val daysUntil = ((it - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
                Text(
                    "Next: ${dateFormat.format(Date(it))} ($daysUntil days)",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (daysUntil <= 14) Color(0xFFE65100) else Color(0xFF4CAF50)
                )
            }
        }
        Text(
            "₹${record.cost.toInt()}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF546E7A)
        )
    }
}
