package com.example.mindsync.presentation.student

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
import com.example.mindsync.domain.model.Assignment
import com.example.mindsync.domain.model.AssignmentPriority
import com.example.mindsync.domain.model.AssignmentStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentsScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "In Progress", "Completed", "Overdue")
    
    var assignments by remember {
        mutableStateOf(
            listOf(
                Assignment(
                    title = "Math Homework",
                    subject = "Mathematics",
                    description = "Complete exercises 5.1 to 5.10",
                    dueDate = System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000,
                    priority = AssignmentPriority.HIGH,
                    status = AssignmentStatus.IN_PROGRESS
                ),
                Assignment(
                    title = "Science Project",
                    subject = "Physics",
                    description = "Build a working model of solar system",
                    dueDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
                    priority = AssignmentPriority.URGENT,
                    status = AssignmentStatus.PENDING
                ),
                Assignment(
                    title = "English Essay",
                    subject = "English",
                    description = "Write 500 words on Climate Change",
                    dueDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000,
                    priority = AssignmentPriority.MEDIUM,
                    status = AssignmentStatus.PENDING
                ),
                Assignment(
                    title = "History Report",
                    subject = "History",
                    description = "Research on World War II",
                    dueDate = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000,
                    priority = AssignmentPriority.HIGH,
                    status = AssignmentStatus.OVERDUE
                ),
                Assignment(
                    title = "Chemistry Lab",
                    subject = "Chemistry",
                    description = "Complete lab report on acid-base reactions",
                    dueDate = System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000,
                    priority = AssignmentPriority.LOW,
                    status = AssignmentStatus.COMPLETED
                )
            )
        )
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newSubject by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var newPriority by remember { mutableStateOf(AssignmentPriority.MEDIUM) }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("📚 Add Assignment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSubject,
                        onValueChange = { newSubject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Text("Priority:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssignmentPriority.values().forEach { priority ->
                            FilterChip(
                                selected = newPriority == priority,
                                onClick = { newPriority = priority },
                                label = { Text(priority.emoji) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            assignments = assignments + Assignment(
                                title = newTitle,
                                subject = newSubject.ifBlank { "General" },
                                description = newDescription,
                                dueDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
                                priority = newPriority,
                                status = AssignmentStatus.PENDING
                            )
                            newTitle = ""
                            newSubject = ""
                            newDescription = ""
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

    val filteredAssignments = when (selectedFilter) {
        "All" -> assignments
        "Pending" -> assignments.filter { it.status == AssignmentStatus.PENDING }
        "In Progress" -> assignments.filter { it.status == AssignmentStatus.IN_PROGRESS }
        "Completed" -> assignments.filter { it.status == AssignmentStatus.COMPLETED }
        "Overdue" -> assignments.filter { it.status == AssignmentStatus.OVERDUE }
        else -> assignments
    }

    val pendingCount = assignments.count { it.status == AssignmentStatus.PENDING || it.status == AssignmentStatus.IN_PROGRESS }
    val overdueCount = assignments.count { it.status == AssignmentStatus.OVERDUE }
    
    val darkBackground = Color(0xFF0D0D0D)
    val cardBackground = Color(0xFF1A1A1A)
    val blueAccent = Color(0xFF4A90D9)

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Assignments", fontWeight = FontWeight.Bold, color = Color.White) },
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
                text = { Text("Add Assignment") },
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
                
                // Stats Card
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
                                    colors = listOf(Color(0xFF3F51B5), Color(0xFF7986CB))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                "📚 Assignment Tracker",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Pending", "$pendingCount", Color.White)
                                StatItem("Overdue", "$overdueCount", Color(0xFFFFCDD2))
                                StatItem("Total", "${assignments.size}", Color.White)
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
                                selectedContainerColor = Color(0xFF3F51B5),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    "Your Assignments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(filteredAssignments) { assignment ->
                AssignmentCard(
                    assignment = assignment,
                    onStatusChange = { newStatus ->
                        assignments = assignments.map {
                            if (it.id == assignment.id) it.copy(status = newStatus) else it
                        }
                    },
                    onDelete = {
                        assignments = assignments.filter { it.id != assignment.id }
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
private fun AssignmentCard(
    assignment: Assignment,
    onStatusChange: (AssignmentStatus) -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dueDate = dateFormat.format(Date(assignment.dueDate))
    
    val statusColor = when (assignment.status) {
        AssignmentStatus.PENDING -> Color(0xFFFFA726)
        AssignmentStatus.IN_PROGRESS -> Color(0xFF42A5F5)
        AssignmentStatus.COMPLETED -> Color(0xFF66BB6A)
        AssignmentStatus.SUBMITTED -> Color(0xFF26A69A)
        AssignmentStatus.OVERDUE -> Color(0xFFEF5350)
    }

    Card(
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(assignment.priority.emoji)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        assignment.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
            }
            
            Text(
                assignment.subject,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3F51B5),
                fontWeight = FontWeight.Medium
            )
            
            Text(
                assignment.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (assignment.status == AssignmentStatus.OVERDUE) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Due: $dueDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (assignment.status == AssignmentStatus.OVERDUE) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                AssistChip(
                    onClick = { 
                        val nextStatus = when (assignment.status) {
                            AssignmentStatus.PENDING -> AssignmentStatus.IN_PROGRESS
                            AssignmentStatus.IN_PROGRESS -> AssignmentStatus.COMPLETED
                            else -> assignment.status
                        }
                        onStatusChange(nextStatus)
                    },
                    label = { Text(assignment.status.displayName, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = statusColor.copy(alpha = 0.2f),
                        labelColor = statusColor
                    )
                )
            }
        }
    }
}
