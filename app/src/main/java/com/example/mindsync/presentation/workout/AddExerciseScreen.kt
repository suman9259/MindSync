package com.example.mindsync.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Exercise(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val muscleGroup: String,
    val equipment: String = "All Equipment",
    val isSelected: Boolean = false
)

enum class MuscleGroup(val displayName: String, val emoji: String) {
    ALL("All Muscles", "🔘"),
    ABDOMINALS("Abdominals", "🎯"),
    ABDUCTORS("Abductors", "🦵"),
    ADDUCTORS("Adductors", "🦵"),
    BICEPS("Biceps", "💪"),
    CALVES("Calves", "🦶"),
    CARDIO("Cardio", "❤️"),
    CHEST("Chest", "🫁"),
    FOREARMS("Forearms", "💪"),
    GLUTES("Glutes", "🍑"),
    HAMSTRINGS("Hamstrings", "🦵"),
    LATS("Lats", "🔙"),
    LOWER_BACK("Lower Back", "🔙"),
    NECK("Neck", "🦒"),
    QUADRICEPS("Quadriceps", "🦵"),
    SHOULDERS("Shoulders", "💪"),
    TRAPS("Traps", "🔙"),
    TRICEPS("Triceps", "💪"),
    UPPER_BACK("Upper Back", "🔙"),
    FULL_BODY("Full Body", "🏋️")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    onNavigateBack: () -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    onCreateExercise: () -> Unit
) {
    val darkBackground = Color(0xFF0D0D0D)
    val cardBackground = Color(0xFF1A1A1A)
    val blueAccent = Color(0xFF4A90D9)
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf(MuscleGroup.ALL) }
    var showMuscleFilter by remember { mutableStateOf(false) }
    var selectedEquipment by remember { mutableStateOf("All Equipment") }
    
    // Sample exercises
    val allExercises = remember {
        listOf(
            Exercise(name = "Bulgarian Split Squat", muscleGroup = "Quadriceps"),
            Exercise(name = "Squat (Machine)", muscleGroup = "Quadriceps"),
            Exercise(name = "Squat (Smith Machine)", muscleGroup = "Quadriceps"),
            Exercise(name = "Seated Leg Curl (Machine)", muscleGroup = "Hamstrings"),
            Exercise(name = "Warm Up", muscleGroup = "Full Body"),
            Exercise(name = "Lat Pulldown - Close Grip (Cable)", muscleGroup = "Lats"),
            Exercise(name = "Lat Pulldown (Machine)", muscleGroup = "Lats"),
            Exercise(name = "Dumbbell Row", muscleGroup = "Lats"),
            Exercise(name = "Bench Press", muscleGroup = "Chest"),
            Exercise(name = "Incline Bench Press", muscleGroup = "Chest"),
            Exercise(name = "Push Up", muscleGroup = "Chest"),
            Exercise(name = "Bicep Curl", muscleGroup = "Biceps"),
            Exercise(name = "Hammer Curl", muscleGroup = "Biceps"),
            Exercise(name = "Tricep Pushdown", muscleGroup = "Triceps"),
            Exercise(name = "Tricep Dips", muscleGroup = "Triceps"),
            Exercise(name = "Shoulder Press", muscleGroup = "Shoulders"),
            Exercise(name = "Lateral Raise", muscleGroup = "Shoulders"),
            Exercise(name = "Deadlift", muscleGroup = "Lower Back"),
            Exercise(name = "Romanian Deadlift", muscleGroup = "Hamstrings"),
            Exercise(name = "Leg Press", muscleGroup = "Quadriceps"),
            Exercise(name = "Calf Raise", muscleGroup = "Calves"),
            Exercise(name = "Plank", muscleGroup = "Abdominals"),
            Exercise(name = "Crunch", muscleGroup = "Abdominals")
        )
    }
    
    val filteredExercises = allExercises.filter { exercise ->
        val matchesSearch = searchQuery.isEmpty() || 
            exercise.name.contains(searchQuery, ignoreCase = true)
        val matchesMuscle = selectedMuscleGroup == MuscleGroup.ALL || 
            exercise.muscleGroup.equals(selectedMuscleGroup.displayName, ignoreCase = true)
        matchesSearch && matchesMuscle
    }

    // Muscle group bottom sheet
    if (showMuscleFilter) {
        ModalBottomSheet(
            onDismissRequest = { showMuscleFilter = false },
            containerColor = cardBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Muscle Group",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                MuscleGroup.values().forEach { muscle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedMuscleGroup = muscle
                                showMuscleFilter = false
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A2A2A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(muscle.emoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            muscle.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedMuscleGroup == muscle) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = blueAccent
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Exercise",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Cancel", color = blueAccent)
                    }
                },
                actions = {
                    TextButton(onClick = onCreateExercise) {
                        Text("Create", color = blueAccent)
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
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search exercise", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = blueAccent,
                    unfocusedContainerColor = cardBackground,
                    focusedContainerColor = cardBackground
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            // Filter buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = { /* equipment filter */ },
                    label = { Text(selectedEquipment) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = cardBackground,
                        labelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedMuscleGroup != MuscleGroup.ALL,
                    onClick = { showMuscleFilter = true },
                    label = { 
                        Text(
                            if (selectedMuscleGroup == MuscleGroup.ALL) "All Muscles" 
                            else selectedMuscleGroup.displayName
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = cardBackground,
                        selectedContainerColor = blueAccent.copy(alpha = 0.3f),
                        labelColor = Color.White
                    )
                )
            }
            
            // Recent exercises header
            Text(
                "Recent Exercises",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Exercise list
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredExercises) { exercise ->
                    ExerciseListItem(
                        exercise = exercise,
                        onClick = { onExerciseSelected(exercise) },
                        blueAccent = blueAccent
                    )
                    Divider(color = Color(0xFF2C2C2E), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun ExerciseListItem(
    exercise: Exercise,
    onClick: () -> Unit,
    blueAccent: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Exercise icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            Text("🏋️", fontSize = 20.sp)
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                exercise.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                exercise.muscleGroup,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        
        Icon(
            Icons.Default.Refresh,
            contentDescription = "History",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}
