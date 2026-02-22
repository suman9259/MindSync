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
import com.example.mindsync.domain.model.SpecialDate
import com.example.mindsync.domain.model.SpecialDateType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdaysScreen(
    onNavigateBack: () -> Unit = {}
) {
    var specialDates by remember {
        mutableStateOf(
            listOf(
                SpecialDate(
                    personName = "Mom",
                    dateType = SpecialDateType.BIRTHDAY,
                    date = System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000,
                    year = 1970,
                    giftIdeas = "Flowers, Jewelry, Spa voucher"
                ),
                SpecialDate(
                    personName = "Best Friend",
                    dateType = SpecialDateType.BIRTHDAY,
                    date = System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000,
                    year = 1995,
                    giftIdeas = "Gaming headset, Gift card"
                ),
                SpecialDate(
                    personName = "Sister",
                    dateType = SpecialDateType.BIRTHDAY,
                    date = System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000,
                    year = 1998
                ),
                SpecialDate(
                    personName = "Parents",
                    dateType = SpecialDateType.ANNIVERSARY,
                    date = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000,
                    giftIdeas = "Dinner reservation, Photo frame"
                ),
                SpecialDate(
                    personName = "College Friend",
                    dateType = SpecialDateType.BIRTHDAY,
                    date = System.currentTimeMillis() + 45 * 24 * 60 * 60 * 1000,
                    year = 1996
                ),
                SpecialDate(
                    personName = "Brother",
                    dateType = SpecialDateType.GRADUATION,
                    date = System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000
                )
            )
        )
    }

    val upcomingThisWeek = specialDates.filter { 
        val daysUntil = (it.date - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)
        daysUntil in 0..7
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newPersonName by remember { mutableStateOf("") }
    var newDateType by remember { mutableStateOf(SpecialDateType.BIRTHDAY) }
    var newGiftIdeas by remember { mutableStateOf("") }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("🎂 Add Special Date") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newPersonName,
                        onValueChange = { newPersonName = it },
                        label = { Text("Person's Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Text("Type:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SpecialDateType.values().take(3).forEach { type ->
                            FilterChip(
                                selected = newDateType == type,
                                onClick = { newDateType = type },
                                label = { Text(type.emoji) }
                            )
                        }
                    }
                    
                    OutlinedTextField(
                        value = newGiftIdeas,
                        onValueChange = { newGiftIdeas = it },
                        label = { Text("Gift Ideas (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPersonName.isNotBlank()) {
                            specialDates = specialDates + SpecialDate(
                                personName = newPersonName,
                                dateType = newDateType,
                                date = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000,
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
                            specialDates = specialDates.filter { it.id != date.id }
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
                            specialDates = specialDates.filter { it.id != date.id }
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
    val daysUntil = ((specialDate.date - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
    
    val daysText = when {
        daysUntil == 0 -> "Today! 🎉"
        daysUntil == 1 -> "Tomorrow!"
        daysUntil > 0 -> "In $daysUntil days"
        else -> "${-daysUntil} days ago"
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
                Text(
                    "${specialDate.dateType.displayName} • $formattedDate",
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
                    daysText,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (daysUntil <= 7) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (daysUntil <= 7) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
