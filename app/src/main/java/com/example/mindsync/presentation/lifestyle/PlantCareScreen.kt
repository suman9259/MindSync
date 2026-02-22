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
import com.example.mindsync.domain.model.Plant
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantCareScreen(
    onNavigateBack: () -> Unit = {}
) {
    var plants by remember {
        mutableStateOf(
            listOf(
                Plant(
                    name = "Money Plant",
                    species = "Epipremnum aureum",
                    location = "Living Room",
                    wateringFrequencyDays = 3,
                    lastWatered = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                    nextWatering = System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000
                ),
                Plant(
                    name = "Snake Plant",
                    species = "Sansevieria",
                    location = "Bedroom",
                    wateringFrequencyDays = 14,
                    lastWatered = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
                    nextWatering = System.currentTimeMillis() + 4 * 24 * 60 * 60 * 1000
                ),
                Plant(
                    name = "Peace Lily",
                    species = "Spathiphyllum",
                    location = "Office",
                    wateringFrequencyDays = 5,
                    lastWatered = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                    nextWatering = System.currentTimeMillis()
                ),
                Plant(
                    name = "Aloe Vera",
                    species = "Aloe barbadensis",
                    location = "Bathroom",
                    wateringFrequencyDays = 21,
                    lastWatered = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                    nextWatering = System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000
                ),
                Plant(
                    name = "Tulsi",
                    species = "Holy Basil",
                    location = "Balcony",
                    wateringFrequencyDays = 1,
                    lastWatered = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000,
                    nextWatering = System.currentTimeMillis()
                )
            )
        )
    }

    val needsWatering = plants.count { plant ->
        plant.nextWatering?.let { it <= System.currentTimeMillis() } ?: false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plant Care", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Plant") },
                containerColor = Color(0xFF4CAF50)
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
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌱", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "My Garden",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Total Plants", "${plants.size}", Color.White)
                                StatItem("Need Water", "$needsWatering", if (needsWatering > 0) Color(0xFFFFCDD2) else Color.White)
                            }
                        }
                    }
                }
            }

            if (needsWatering > 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💧", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "$needsWatering plant(s) need watering today!",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    "Your Plants",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(plants) { plant ->
                PlantCard(
                    plant = plant,
                    onWater = {
                        plants = plants.map { p ->
                            if (p.id == plant.id) {
                                p.copy(
                                    lastWatered = System.currentTimeMillis(),
                                    nextWatering = System.currentTimeMillis() + p.wateringFrequencyDays * 24 * 60 * 60 * 1000
                                )
                            } else p
                        }
                    },
                    onDelete = {
                        plants = plants.filter { it.id != plant.id }
                    }
                )
            }

            item {
                // Care Tips
                Text(
                    "Plant Care Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val tips = listOf(
                            "☀️ Check sunlight requirements for each plant",
                            "💧 Water in the morning for best absorption",
                            "🌡️ Avoid sudden temperature changes",
                            "✂️ Prune dead leaves regularly",
                            "🪴 Repot when roots outgrow the container",
                            "🧪 Fertilize during growing season"
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
private fun PlantCard(
    plant: Plant,
    onWater: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val needsWater = plant.nextWatering?.let { it <= System.currentTimeMillis() } ?: false
    val daysUntilWater = plant.nextWatering?.let {
        ((it - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
    } ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (needsWater) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface
        ),
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
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text("🪴", style = MaterialTheme.typography.headlineMedium)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    plant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    plant.species,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("📍", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        plant.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                plant.lastWatered?.let {
                    Text(
                        "Last watered: ${dateFormat.format(Date(it))}",
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
                        contentDescription = "Remove",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Button(
                    onClick = onWater,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (needsWater) Color(0xFF2196F3) else Color(0xFF4CAF50)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (needsWater) "💧 Water Now" else "In $daysUntilWater days",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
