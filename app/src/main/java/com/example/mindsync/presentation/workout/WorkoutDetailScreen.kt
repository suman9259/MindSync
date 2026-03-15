package com.example.mindsync.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mindsync.domain.model.Exercise
import com.example.mindsync.domain.model.ExerciseLog
import com.example.mindsync.domain.model.MuscleGroup
import com.example.mindsync.domain.model.SetLog
import com.example.mindsync.domain.model.Workout
import com.example.mindsync.domain.model.WorkoutCategory
import com.example.mindsync.domain.model.WorkoutSession
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

data class SessionExerciseItem(
    val exerciseId: String,
    val name: String,
    val muscleGroup: String,
    val isWithWeight: Boolean,
    val sets: List<ExerciseSet>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workout: Workout,
    onNavigateBack: () -> Unit,
    onSetReminder: () -> Unit,
    onWorkoutComplete: () -> Unit,
    viewModel: WorkoutViewModel = koinViewModel()
) {
    val darkBackground = Color(0xFF0D0D0D)
    val cardBackground = Color(0xFF1A1A1A)
    val blueAccent = Color(0xFF4A90D9)
    val gradientColors = getCategoryGradient(workout.category)

    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddExerciseSheet by remember { mutableStateOf(false) }
    var currentWorkout by remember { mutableStateOf(workout) }
    val sheetState = rememberModalBottomSheetState()

    val totalSetsCount = currentWorkout.exercises.sumOf { maxOf(it.sets, 1) }

    val sessionExercises = remember(workout.id) {
        mutableStateListOf(*workout.exercises.map { ex ->
            SessionExerciseItem(
                exerciseId = ex.id,
                name = ex.name,
                muscleGroup = ex.muscleGroup.displayName,
                isWithWeight = ex.isWithWeight,
                sets = (1..maxOf(ex.sets, 1)).map { setNum ->
                    ExerciseSet(
                        setNumber = setNum,
                        reps = if (ex.reps > 0) ex.reps.toString() else "",
                        kg = if (ex.isWithWeight && ex.weight > 0f) ex.weight.toString() else "",
                        isCompleted = false
                    )
                }
            )
        }.toTypedArray())
    }

    val completedCount = sessionExercises.sumOf { item -> item.sets.count { it.isCompleted } }
    val progress = if (totalSetsCount > 0) completedCount.toFloat() / totalSetsCount.toFloat() else 0f

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Routine?", color = Color.White) },
            text = { Text("This will permanently delete \"${workout.name}\".", color = Color.Gray) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.processIntent(WorkoutIntent.DeleteWorkout(workout.id))
                    showDeleteDialog = false
                    onNavigateBack()
                }) { Text("Remove", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
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
                    Text(
                        "Routine",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Remove Routine", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackground
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                RoutineHeaderBanner(
                    workout = currentWorkout,
                    gradientColors = gradientColors,
                    progress = progress,
                    completedSets = completedCount,
                    totalSets = totalSetsCount
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Exercises (${sessionExercises.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$completedCount / $totalSetsCount sets done",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            sessionExercises.forEachIndexed { exerciseIndex, item ->
                item(key = item.exerciseId + exerciseIndex) {
                    SessionExerciseCard(
                        item = item,
                        onRemove = {
                            sessionExercises.removeAt(exerciseIndex)
                        },
                        onAddSet = {
                            val newSets = item.sets + ExerciseSet(
                                setNumber = item.sets.size + 1,
                                reps = "",
                                kg = "",
                                isCompleted = false
                            )
                            sessionExercises[exerciseIndex] = item.copy(sets = newSets)
                        },
                        onUpdateSet = { setIndex, kg, reps ->
                            val newSets = item.sets.mapIndexed { i, set ->
                                if (i == setIndex) set.copy(kg = kg, reps = reps) else set
                            }
                            sessionExercises[exerciseIndex] = item.copy(sets = newSets)
                        },
                        onToggleSetComplete = { setIndex ->
                            val newSets = item.sets.mapIndexed { i, set ->
                                if (i == setIndex) set.copy(isCompleted = !set.isCompleted) else set
                            }
                            sessionExercises[exerciseIndex] = item.copy(sets = newSets)
                        },
                        cardBackground = cardBackground,
                        blueAccent = blueAccent
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { showAddExerciseSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = blueAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Add Exercise to Routine", color = blueAccent, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val session = WorkoutSession(
                            workoutId = currentWorkout.id,
                            exerciseLogs = sessionExercises.map { item ->
                                ExerciseLog(
                                    exerciseId = item.exerciseId,
                                    exerciseName = item.name,
                                    sets = item.sets.map { set ->
                                        SetLog(
                                            setNumber = set.setNumber,
                                            reps = set.reps.toIntOrNull() ?: 0,
                                            weight = set.kg.toFloatOrNull() ?: 0f,
                                            isCompleted = set.isCompleted
                                        )
                                    },
                                    isCompleted = item.sets.any { it.isCompleted }
                                )
                            }
                        )
                        viewModel.processIntent(
                            WorkoutIntent.MarkWorkoutComplete(currentWorkout.name, session)
                        )
                        onWorkoutComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = blueAccent)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finish Workout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showAddExerciseSheet) {
        AddExerciseToRoutineSheet(
            availableExercises = state.exercises.ifEmpty {
                listOf(
                    Exercise(name = "Bench Press", muscleGroup = MuscleGroup.CHEST, sets = 3, reps = 10),
                    Exercise(name = "Squat", muscleGroup = MuscleGroup.LEGS, sets = 3, reps = 10),
                    Exercise(name = "Deadlift", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 8),
                    Exercise(name = "Shoulder Press", muscleGroup = MuscleGroup.SHOULDERS, sets = 3, reps = 10),
                    Exercise(name = "Bicep Curl", muscleGroup = MuscleGroup.BICEPS, sets = 3, reps = 12),
                    Exercise(name = "Tricep Pushdown", muscleGroup = MuscleGroup.TRICEPS, sets = 3, reps = 12),
                    Exercise(name = "Lat Pulldown", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 10),
                    Exercise(name = "Leg Press", muscleGroup = MuscleGroup.LEGS, sets = 3, reps = 10),
                    Exercise(name = "Romanian Deadlift", muscleGroup = MuscleGroup.BACK, sets = 3, reps = 10),
                    Exercise(name = "Plank", muscleGroup = MuscleGroup.CORE, sets = 3, reps = 1, isWithWeight = false)
                )
            },
            sheetState = sheetState,
            onDismiss = { scope.launch { sheetState.hide() }.invokeOnCompletion { showAddExerciseSheet = false } },
            onExerciseSelected = { exercise ->
                sessionExercises.add(
                    SessionExerciseItem(
                        exerciseId = exercise.id,
                        name = exercise.name,
                        muscleGroup = exercise.muscleGroup.displayName,
                        isWithWeight = exercise.isWithWeight,
                        sets = listOf(ExerciseSet(setNumber = 1))
                    )
                )
                val updatedWorkout = currentWorkout.copy(
                    exercises = currentWorkout.exercises + exercise
                )
                currentWorkout = updatedWorkout
                viewModel.processIntent(WorkoutIntent.UpdateWorkout(updatedWorkout))
                scope.launch { sheetState.hide() }.invokeOnCompletion { showAddExerciseSheet = false }
            },
            cardBackground = cardBackground,
            blueAccent = blueAccent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExerciseToRoutineSheet(
    availableExercises: List<Exercise>,
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    cardBackground: Color,
    blueAccent: Color
) {
    var query by remember { mutableStateOf("") }
    val filtered = availableExercises.filter {
        query.isBlank() || it.name.contains(query, ignoreCase = true)
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1A1A1A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Add Exercise",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search exercises…", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF3A3A3A),
                    focusedBorderColor = blueAccent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filtered, key = { it.id }) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onExerciseSelected(exercise) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(blueAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💪", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    exercise.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    exercise.muscleGroup.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionExerciseCard(
    item: SessionExerciseItem,
    onRemove: () -> Unit,
    onAddSet: () -> Unit,
    onUpdateSet: (Int, String, String) -> Unit,
    onToggleSetComplete: (Int) -> Unit,
    cardBackground: Color,
    blueAccent: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = blueAccent
                    )
                    Text(
                        text = item.muscleGroup,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove exercise",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SET", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(32.dp))
                Text("REPS", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                if (item.isWithWeight) {
                    Text("KG", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.width(44.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            item.sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (set.isCompleted) blueAccent else Color(0xFF2A2A2A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${set.setNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedTextField(
                        value = set.reps,
                        onValueChange = { onUpdateSet(index, set.kg, it) },
                        modifier = Modifier.width(80.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center,
                            color = Color.White
                        ),
                        placeholder = { Text("0", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = if (set.isCompleted) blueAccent else Color(0xFF3A3A3A),
                            focusedBorderColor = blueAccent
                        )
                    )
                    if (item.isWithWeight) {
                        OutlinedTextField(
                            value = set.kg,
                            onValueChange = { onUpdateSet(index, it, set.reps) },
                            modifier = Modifier.width(80.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = Color.White
                            ),
                            placeholder = { Text("0.0", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (set.isCompleted) blueAccent else Color(0xFF3A3A3A),
                                focusedBorderColor = blueAccent
                            )
                        )
                    }
                    Checkbox(
                        checked = set.isCompleted,
                        onCheckedChange = { onToggleSetComplete(index) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = blueAccent,
                            uncheckedColor = Color.Gray
                        )
                    )
                }
            }

            TextButton(
                onClick = onAddSet,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("+ Add Set", color = blueAccent)
            }
        }
    }
}

@Composable
private fun RoutineHeaderBanner(
    workout: Workout,
    gradientColors: List<Color>,
    progress: Float,
    completedSets: Int,
    totalSets: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (workout.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = workout.imageUrl,
                contentDescription = workout.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = Brush.linearGradient(gradientColors))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RoutineChip("${workout.exercises.size} exercises")
                if (workout.durationMinutes > 0) RoutineChip("${workout.durationMinutes} min")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progress", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                Text("$completedSets / $totalSets sets", style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun RoutineChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White)
    }
}

private fun getCategoryGradient(category: WorkoutCategory): List<Color> {
    return when (category) {
        WorkoutCategory.STRENGTH -> listOf(Color(0xFFf093fb), Color(0xFFf5576c))
        WorkoutCategory.CARDIO -> listOf(Color(0xFF11998e), Color(0xFF38ef7d))
        WorkoutCategory.FLEXIBILITY -> listOf(Color(0xFF667eea), Color(0xFF764ba2))
        WorkoutCategory.HIIT -> listOf(Color(0xFFfc4a1a), Color(0xFFf7b733))
        WorkoutCategory.YOGA -> listOf(Color(0xFFa8edea), Color(0xFFfed6e3))
        WorkoutCategory.CROSSFIT -> listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
        WorkoutCategory.BODYWEIGHT -> listOf(Color(0xFFf2709c), Color(0xFFff9472))
        WorkoutCategory.POWERLIFTING -> listOf(Color(0xFF2c3e50), Color(0xFF4ca1af))
    }
}
