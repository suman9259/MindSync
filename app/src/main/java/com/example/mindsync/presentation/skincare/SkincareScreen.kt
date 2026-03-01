package com.example.mindsync.presentation.skincare

import androidx.compose.animation.animateColorAsState
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
import com.example.mindsync.domain.model.SkincareCategory
import com.example.mindsync.domain.model.SkincareRoutine
import com.example.mindsync.domain.model.SkincareRoutineType
import com.example.mindsync.domain.model.SkincareStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkincareScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddSkincare: () -> Unit = {},
    onNavigateToSkincareDetail: (String) -> Unit = {},
    viewModel: SkincareViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val routines = state.routines
    
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Morning", "Evening", "Weekly")
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var routineToDelete by remember { mutableStateOf<SkincareRoutine?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var routineToEdit by remember { mutableStateOf<SkincareRoutine?>(null) }
    var editName by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    
    // Edit dialog
    if (showEditDialog && routineToEdit != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false; routineToEdit = null },
            title = { Text("✏️ Edit Routine") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        routineToEdit?.let { routine ->
                            viewModel.updateRoutine(
                                id = routine.id,
                                name = editName,
                                description = editDescription,
                                routineType = routine.routineType,
                                estimatedMinutes = routine.estimatedMinutes,
                                scheduledTime = routine.scheduledTime,
                                reminderEnabled = routine.reminderEnabled
                            )
                        }
                        showEditDialog = false
                        routineToEdit = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; routineToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    val filteredRoutines = when (selectedFilter) {
        "Morning" -> routines.filter { it.routineType == SkincareRoutineType.MORNING }
        "Evening" -> routines.filter { it.routineType == SkincareRoutineType.EVENING }
        "Weekly" -> routines.filter { it.routineType == SkincareRoutineType.WEEKLY }
        else -> routines
    }
    
    if (showDeleteDialog && routineToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Routine") },
            text = { Text("Remove ${routineToDelete?.name} from your routines?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        routineToDelete?.let { viewModel.deleteRoutine(it.id) }
                        showDeleteDialog = false
                        routineToDelete = null
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
                title = { Text("Skincare Routines", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Reminders", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddSkincare,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Routine") },
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
                                "✨ Your Skin Journey",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Consistency is the key to glowing skin",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Done Today", "${state.completedTodayCount}/${state.totalRoutinesCount}", Color.White)
                                StatItem("Products", "${state.totalProductsCount}", Color.White)
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
                    "Your Routines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (filteredRoutines.isEmpty()) {
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
                            Text("✨", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No routines added yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                "Tap the button below to create your first skincare routine",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            items(filteredRoutines) { skincareRoutine ->
                SkincareRoutineCard(
                    routine = skincareRoutine,
                    onClick = { onNavigateToSkincareDetail(skincareRoutine.id) },
                    onEdit = {
                        routineToEdit = skincareRoutine
                        editName = skincareRoutine.name
                        editDescription = skincareRoutine.description
                        showEditDialog = true
                    },
                    onDelete = {
                        routineToDelete = skincareRoutine
                        showDeleteDialog = true
                    },
                    onStart = {
                        viewModel.toggleRoutineCompleted(skincareRoutine.id)
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
private fun SkincareRoutineCard(
    routine: SkincareRoutine,
    onClick: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onStart: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        if (routine.completedToday) Color(0xFF03DAC5).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        label = "bgColor"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
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
                        .background(
                            if (routine.completedToday) Color(0xFF03DAC5)  // Material Teal
                            else Color(0xFF03DAC5).copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (routine.completedToday) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Text(
                            routine.routineType.emoji,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        routine.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        routine.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                
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
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${routine.estimatedMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "${routine.steps.size} steps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (routine.currentStreak > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "🔥 ${routine.currentStreak} days",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFD54F)  // Amber
                        )
                    }
                }
                
                if (!routine.completedToday) {
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF03DAC5)  // Material Teal
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Start")
                    }
                }
            }
            
            if (routine.steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(routine.steps.take(5)) { step ->
                        AssistChip(
                            onClick = { },
                            label = { Text(step.category.emoji + " " + step.name, style = MaterialTheme.typography.labelSmall) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF03DAC5).copy(alpha = 0.2f)  // Teal tint
                            )
                        )
                    }
                    if (routine.steps.size > 5) {
                        item {
                            AssistChip(
                                onClick = { },
                                label = { Text("+${routine.steps.size - 5} more") }
                            )
                        }
                    }
                }
            }
        }
    }
}
