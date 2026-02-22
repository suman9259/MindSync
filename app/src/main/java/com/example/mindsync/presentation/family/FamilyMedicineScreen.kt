package com.example.mindsync.presentation.family

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
import com.example.mindsync.domain.model.FamilyMember
import com.example.mindsync.domain.model.FamilyMedicine
import com.example.mindsync.domain.model.FamilyRelationship
import com.example.mindsync.domain.model.MedicineFrequency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMedicineScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedMember by remember { mutableStateOf<FamilyMember?>(null) }
    
    var familyMembers by remember {
        mutableStateOf(
            listOf(
                FamilyMember(
                    name = "Mom",
                    relationship = FamilyRelationship.MOTHER,
                    age = 55,
                    phoneNumber = "+91 98765 43210",
                    emergencyContact = true,
                    avatarEmoji = "👩",
                    medicines = listOf(
                        FamilyMedicine(
                            name = "Blood Pressure Medicine",
                            dosage = "10mg",
                            frequency = MedicineFrequency.DAILY,
                            scheduledTimes = listOf("08:00", "20:00"),
                            instructions = "Take with water after meals",
                            stockCount = 20,
                            takenToday = true
                        ),
                        FamilyMedicine(
                            name = "Vitamin D3",
                            dosage = "1000 IU",
                            frequency = MedicineFrequency.DAILY,
                            scheduledTimes = listOf("09:00"),
                            stockCount = 30,
                            takenToday = false
                        )
                    )
                ),
                FamilyMember(
                    name = "Dad",
                    relationship = FamilyRelationship.FATHER,
                    age = 58,
                    phoneNumber = "+91 98765 43211",
                    emergencyContact = true,
                    avatarEmoji = "👨",
                    medicines = listOf(
                        FamilyMedicine(
                            name = "Diabetes Medicine",
                            dosage = "500mg",
                            frequency = MedicineFrequency.TWICE_DAILY,
                            scheduledTimes = listOf("07:00", "19:00"),
                            instructions = "Take 30 minutes before meals",
                            stockCount = 15,
                            takenToday = true
                        ),
                        FamilyMedicine(
                            name = "Cholesterol Medicine",
                            dosage = "20mg",
                            frequency = MedicineFrequency.DAILY,
                            scheduledTimes = listOf("21:00"),
                            stockCount = 8,
                            refillReminder = true,
                            takenToday = false
                        )
                    )
                ),
                FamilyMember(
                    name = "Grandma",
                    relationship = FamilyRelationship.GRANDMOTHER,
                    age = 78,
                    phoneNumber = "+91 98765 43212",
                    avatarEmoji = "👵",
                    medicines = listOf(
                        FamilyMedicine(
                            name = "Calcium Supplement",
                            dosage = "500mg",
                            frequency = MedicineFrequency.DAILY,
                            scheduledTimes = listOf("10:00"),
                            stockCount = 25,
                            takenToday = false
                        ),
                        FamilyMedicine(
                            name = "Joint Pain Medicine",
                            dosage = "400mg",
                            frequency = MedicineFrequency.TWICE_DAILY,
                            scheduledTimes = listOf("08:00", "20:00"),
                            stockCount = 12,
                            takenToday = true
                        )
                    )
                )
            )
        )
    }

    var showAddMemberDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Medicine", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddMemberDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Add Member") },
                containerColor = Color(0xFFE91E63)
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
                                    colors = listOf(Color(0xFFE91E63), Color(0xFFF06292))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                "👨‍👩‍👧 Family Health Tracker",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Track and remind medicines for your loved ones",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Members", "${familyMembers.size}", Color.White)
                                StatItem("Medicines", "${familyMembers.sumOf { it.medicines.size }}", Color.White)
                                StatItem("Pending", "${familyMembers.sumOf { m -> m.medicines.count { !it.takenToday } }}", Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Family Members",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(familyMembers) { member ->
                FamilyMemberCard(
                    member = member,
                    onMedicineTaken = { medicine ->
                        familyMembers = familyMembers.map { m ->
                            if (m.id == member.id) {
                                m.copy(
                                    medicines = m.medicines.map { med ->
                                        if (med.id == medicine.id) med.copy(takenToday = !med.takenToday) else med
                                    }
                                )
                            } else m
                        }
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
private fun FamilyMemberCard(
    member: FamilyMember,
    onMedicineTaken: (FamilyMedicine) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val pendingMedicines = member.medicines.count { !it.takenToday }
    val lowStockMedicines = member.medicines.count { it.stockCount <= it.refillThreshold }

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
                        .background(Color(0xFFFCE4EC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(member.avatarEmoji, style = MaterialTheme.typography.headlineMedium)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        member.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${member.relationship.displayName} • ${member.age} years",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        if (pendingMedicines > 0) {
                            AssistChip(
                                onClick = { },
                                label = { Text("$pendingMedicines pending", style = MaterialTheme.typography.labelSmall) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFFF3E0)
                                ),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        if (lowStockMedicines > 0) {
                            AssistChip(
                                onClick = { },
                                label = { Text("$lowStockMedicines low stock", style = MaterialTheme.typography.labelSmall) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFFEBEE)
                                ),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }
                
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "Medicines",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                member.medicines.forEach { medicine ->
                    MedicineRow(
                        medicine = medicine,
                        onTaken = { onMedicineTaken(medicine) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (member.emergencyContact) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call ${member.name}")
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicineRow(
    medicine: FamilyMedicine,
    onTaken: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (medicine.takenToday) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "💊 ${medicine.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                "${medicine.dosage} • ${medicine.scheduledTimes.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (medicine.stockCount <= medicine.refillThreshold) {
                Text(
                    "⚠️ Low stock: ${medicine.stockCount} left",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE53935)
                )
            }
        }
        
        Button(
            onClick = onTaken,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (medicine.takenToday) Color(0xFF4CAF50) else Color(0xFFFFA726)
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(if (medicine.takenToday) "✓ Done" else "Remind")
        }
    }
}
