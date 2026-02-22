package com.example.mindsync.presentation.lifestyle

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
import com.example.mindsync.domain.model.BillCategory
import com.example.mindsync.domain.model.BillReminder
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Due Soon", "Paid", "Overdue")
    
    var bills by remember {
        mutableStateOf(
            listOf(
                BillReminder(
                    name = "Electricity Bill",
                    category = BillCategory.ELECTRICITY,
                    amount = 2500.0,
                    dueDate = System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000,
                    isRecurring = true,
                    isPaid = false
                ),
                BillReminder(
                    name = "Internet Bill",
                    category = BillCategory.INTERNET,
                    amount = 999.0,
                    dueDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000,
                    isRecurring = true,
                    isPaid = false
                ),
                BillReminder(
                    name = "House Rent",
                    category = BillCategory.RENT,
                    amount = 25000.0,
                    dueDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
                    isRecurring = true,
                    isPaid = false
                ),
                BillReminder(
                    name = "Water Bill",
                    category = BillCategory.WATER,
                    amount = 450.0,
                    dueDate = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                    isRecurring = true,
                    isPaid = false
                ),
                BillReminder(
                    name = "Netflix Subscription",
                    category = BillCategory.SUBSCRIPTION,
                    amount = 649.0,
                    dueDate = System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000,
                    isRecurring = true,
                    isPaid = true
                ),
                BillReminder(
                    name = "Credit Card",
                    category = BillCategory.CREDIT_CARD,
                    amount = 15000.0,
                    dueDate = System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000,
                    isRecurring = true,
                    isPaid = false
                )
            )
        )
    }

    val totalDue = bills.filter { !it.isPaid }.sumOf { it.amount }
    val overdueBills = bills.filter { !it.isPaid && it.dueDate < System.currentTimeMillis() }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newBillName by remember { mutableStateOf("") }
    var newBillAmount by remember { mutableStateOf("") }
    var newBillCategory by remember { mutableStateOf(BillCategory.OTHER) }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("💰 Add New Bill") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newBillName,
                        onValueChange = { newBillName = it },
                        label = { Text("Bill Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newBillAmount,
                        onValueChange = { newBillAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text("Category:", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(BillCategory.values().toList()) { category ->
                            FilterChip(
                                selected = newBillCategory == category,
                                onClick = { newBillCategory = category },
                                label = { Text(category.emoji, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newBillName.isNotBlank() && newBillAmount.isNotBlank()) {
                            bills = bills + BillReminder(
                                name = newBillName,
                                category = newBillCategory,
                                amount = newBillAmount.toDoubleOrNull() ?: 0.0,
                                dueDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
                                isRecurring = true,
                                isPaid = false
                            )
                            newBillName = ""
                            newBillAmount = ""
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
                title = { Text("Bills & Payments", fontWeight = FontWeight.Bold, color = Color.White) },
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
                text = { Text("Add Bill") },
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
                
                // Summary Card
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
                                    colors = listOf(Color(0xFF795548), Color(0xFFA1887F))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                "💰 Bill Summary",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Total Due", "₹${totalDue.toInt()}", Color.White)
                                StatItem("Overdue", "${overdueBills.size}", Color(0xFFFFCDD2))
                                StatItem("Upcoming", "${bills.count { !it.isPaid && it.dueDate > System.currentTimeMillis() }}", Color.White)
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
                                selectedContainerColor = Color(0xFF795548),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    "Your Bills",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val filteredBills = when (selectedFilter) {
                "Due Soon" -> bills.filter { !it.isPaid && it.dueDate > System.currentTimeMillis() }
                "Paid" -> bills.filter { it.isPaid }
                "Overdue" -> bills.filter { !it.isPaid && it.dueDate < System.currentTimeMillis() }
                else -> bills
            }

            items(filteredBills.sortedBy { it.dueDate }) { bill ->
                BillCard(
                    bill = bill,
                    onTogglePaid = {
                        bills = bills.map { if (it.id == bill.id) it.copy(isPaid = !it.isPaid) else it }
                    },
                    onDelete = {
                        bills = bills.filter { it.id != bill.id }
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
private fun BillCard(
    bill: BillReminder,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dueDate = dateFormat.format(Date(bill.dueDate))
    val isOverdue = !bill.isPaid && bill.dueDate < System.currentTimeMillis()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                bill.isPaid -> Color(0xFFE8F5E9)
                isOverdue -> Color(0xFFFFEBEE)
                else -> MaterialTheme.colorScheme.surface
            }
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(bill.category.emoji, style = MaterialTheme.typography.titleLarge)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    bill.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Due: $dueDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "₹${bill.amount.toInt()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF795548)
                )
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
                
                Button(
                    onClick = onTogglePaid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bill.isPaid) Color(0xFFFF9800) 
                            else if (isOverdue) Color.Red 
                            else Color(0xFF4CAF50)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (bill.isPaid) "Undo" else "Pay",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
