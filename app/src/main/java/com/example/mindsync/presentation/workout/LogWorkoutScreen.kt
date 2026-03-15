package com.example.mindsync.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Timer
import kotlin.concurrent.timerTask
import org.koin.androidx.compose.koinViewModel

data class ExerciseSet(
    val setNumber: Int,
    var kg: String = "",
    var reps: String = "",
    var isCompleted: Boolean = false
)

data class LoggedExercise(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val muscleGroup: String,
    val sets: MutableList<ExerciseSet> = mutableListOf(ExerciseSet(1))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogWorkoutScreen(
    onNavigateBack: () -> Unit,
    onAddExercise: () -> Unit,
    onFinish: () -> Unit,
    viewModel: WorkoutViewModel = koinViewModel()
) {
    val darkBackground = Color(0xFF0D0D0D)
    val cardBackground = Color(0xFF1A1A1A)
    val blueAccent = Color(0xFF4A90D9)
    val purpleAccent = Color(0xFF6B5CE7)

    val state by viewModel.state.collectAsState()
    val exercises = state.sessionExercises

    var duration by remember { mutableStateOf(0) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.processIntent(WorkoutIntent.ClearSession)
        val timer = Timer()
        timer.scheduleAtFixedRate(timerTask {
            duration++
        }, 1000, 1000)
    }
    
    val totalVolume = exercises.sumOf { exercise ->
        exercise.sets.filter { it.isCompleted }.sumOf { 
            (it.kg.toDoubleOrNull() ?: 0.0) * (it.reps.toIntOrNull() ?: 0) 
        }
    }
    
    val totalSets = exercises.sumOf { it.sets.count { set -> set.isCompleted } }
    
    // Discard dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Workout?", color = Color.White) },
            text = { Text("Are you sure you want to discard this workout?", color = Color.Gray) },
            confirmButton = {
                TextButton(onClick = { onNavigateBack() }) {
                    Text("Discard", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancel", color = blueAccent)
                }
            },
            containerColor = cardBackground
        )
    }

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Log Workout",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    // Timer icon
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = Color.White
                        )
                    }
                    // Finish button
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(blueAccent)
                            .clickable { onFinish() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Finish",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Duration
                Column {
                    Text(
                        "Duration",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        formatDuration(duration),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = purpleAccent
                    )
                }
                // Volume
                Column {
                    Text(
                        "Volume",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        "${totalVolume.toInt()} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                // Sets
                Column {
                    Text(
                        "Sets",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        "$totalSets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                // Body icons placeholder
                Row {
                    Text("🧍", fontSize = 24.sp)
                    Text("🧍", fontSize = 24.sp)
                }
            }
            
            Divider(color = Color(0xFF2C2C2E), thickness = 1.dp)
            
            if (exercises.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🏋️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Get started",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add an exercise to start your workout",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                // Exercise list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(exercises) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onUpdateSet = { setIndex, kg, reps ->
                                val updated = exercises.map { ex ->
                                    if (ex.id == exercise.id) {
                                        ex.copy(sets = ex.sets.mapIndexed { index, set ->
                                            if (index == setIndex) set.copy(kg = kg, reps = reps)
                                            else set
                                        }.toMutableList())
                                    } else ex
                                }
                                viewModel.processIntent(WorkoutIntent.UpdateSessionExercises(updated))
                            },
                            onToggleComplete = { setIndex ->
                                val updated = exercises.map { ex ->
                                    if (ex.id == exercise.id) {
                                        ex.copy(sets = ex.sets.mapIndexed { index, set ->
                                            if (index == setIndex) set.copy(isCompleted = !set.isCompleted)
                                            else set
                                        }.toMutableList())
                                    } else ex
                                }
                                viewModel.processIntent(WorkoutIntent.UpdateSessionExercises(updated))
                            },
                            onAddSet = {
                                val updated = exercises.map { ex ->
                                    if (ex.id == exercise.id) {
                                        val newSets = ex.sets.toMutableList()
                                        newSets.add(ExerciseSet(newSets.size + 1))
                                        ex.copy(sets = newSets)
                                    } else ex
                                }
                                viewModel.processIntent(WorkoutIntent.UpdateSessionExercises(updated))
                            },
                            cardBackground = cardBackground,
                            blueAccent = blueAccent
                        )
                    }
                }
            }
            
            // Bottom buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add Exercise button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddExercise() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = blueAccent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Add Exercise",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
                
                // Settings and Discard row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* settings */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground)
                    ) {
                        Text(
                            "Settings",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDiscardDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground)
                    ) {
                        Text(
                            "Discard Workout",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: LoggedExercise,
    onUpdateSet: (Int, String, String) -> Unit,
    onToggleComplete: (Int) -> Unit,
    onAddSet: () -> Unit,
    cardBackground: Color,
    blueAccent: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Exercise header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🏋️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = blueAccent
                    )
                    Text(
                        exercise.muscleGroup,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SET", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(40.dp))
                Text("KG", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                Text("REPS", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.width(40.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sets
            exercise.sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${set.setNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.width(40.dp)
                    )
                    OutlinedTextField(
                        value = set.kg,
                        onValueChange = { onUpdateSet(index, it, set.reps) },
                        modifier = Modifier.width(80.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center,
                            color = Color.White
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = blueAccent
                        )
                    )
                    OutlinedTextField(
                        value = set.reps,
                        onValueChange = { onUpdateSet(index, set.kg, it) },
                        modifier = Modifier.width(80.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center,
                            color = Color.White
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = blueAccent
                        )
                    )
                    Checkbox(
                        checked = set.isCompleted,
                        onCheckedChange = { onToggleComplete(index) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = blueAccent,
                            uncheckedColor = Color.Gray
                        )
                    )
                }
            }
            
            // Add set button
            TextButton(
                onClick = onAddSet,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("+ Add Set", color = blueAccent)
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%d:%02d", minutes, secs)
    }
}
