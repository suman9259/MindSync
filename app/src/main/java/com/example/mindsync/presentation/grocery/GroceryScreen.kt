package com.example.mindsync.presentation.grocery

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.mindsync.domain.model.GroceryCategory
import com.example.mindsync.domain.model.GroceryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All") + GroceryCategory.entries.map { it.displayName }
    
    // Suggestions for grocery items
    val suggestions = listOf(
        "Milk", "Bread", "Eggs", "Butter", "Cheese", "Yogurt",
        "Apples", "Bananas", "Oranges", "Tomatoes", "Onions", "Potatoes",
        "Chicken", "Rice", "Pasta", "Olive Oil", "Salt", "Sugar",
        "Coffee", "Tea", "Juice", "Water", "Cereal", "Oats"
    )
    
    var groceryItems by remember {
        mutableStateOf(
            listOf(
                GroceryItem(name = "Apples", quantity = 6, category = GroceryCategory.FRUITS),
                GroceryItem(name = "Bananas", quantity = 1, unit = "bunch", category = GroceryCategory.FRUITS),
                GroceryItem(name = "Milk", quantity = 2, unit = "liters", category = GroceryCategory.DAIRY, isPurchased = true),
                GroceryItem(name = "Eggs", quantity = 12, category = GroceryCategory.DAIRY),
                GroceryItem(name = "Bread", quantity = 1, unit = "loaf", category = GroceryCategory.BAKERY),
                GroceryItem(name = "Chicken Breast", quantity = 500, unit = "g", category = GroceryCategory.MEAT),
                GroceryItem(name = "Spinach", quantity = 1, unit = "bunch", category = GroceryCategory.VEGETABLES),
                GroceryItem(name = "Tomatoes", quantity = 4, category = GroceryCategory.VEGETABLES),
                GroceryItem(name = "Rice", quantity = 1, unit = "kg", category = GroceryCategory.GRAINS, isPurchased = true),
                GroceryItem(name = "Olive Oil", quantity = 1, unit = "bottle", category = GroceryCategory.SPICES)
            )
        )
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("1") }
    var newItemCategory by remember { mutableStateOf(GroceryCategory.OTHER) }

    // Filter suggestions based on input
    val filteredSuggestions = if (newItemName.length >= 2) {
        suggestions.filter { it.contains(newItemName, ignoreCase = true) && it.lowercase() != newItemName.lowercase() }.take(5)
    } else emptyList()
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("🛒 Add Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Suggestions
                    if (filteredSuggestions.isNotEmpty()) {
                        Text("Suggestions:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(filteredSuggestions) { suggestion ->
                                SuggestionChip(
                                    onClick = { newItemName = suggestion },
                                    label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                    
                    OutlinedTextField(
                        value = newItemQuantity,
                        onValueChange = { newItemQuantity = it.filter { c -> c.isDigit() } },
                        label = { Text("Quantity") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Text("Category:", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(GroceryCategory.entries.take(6)) { category ->
                            FilterChip(
                                selected = newItemCategory == category,
                                onClick = { newItemCategory = category },
                                label = { Text(category.emoji, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            groceryItems = groceryItems + GroceryItem(
                                name = newItemName,
                                quantity = newItemQuantity.toIntOrNull() ?: 1,
                                category = newItemCategory
                            )
                            newItemName = ""
                            newItemQuantity = "1"
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

    val filteredItems = if (selectedFilter == "All") {
        groceryItems
    } else {
        groceryItems.filter { it.category.displayName == selectedFilter }
    }

    val purchasedCount = groceryItems.count { it.isPurchased }
    val totalCount = groceryItems.size
    
    val darkBackground = Color(0xFF0D0D0D)
    val cardBackground = Color(0xFF1A1A1A)
    val blueAccent = Color(0xFF4A90D9)

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Grocery List", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        val shareText = buildString {
                            appendLine("🛒 Grocery List")
                            appendLine("---")
                            groceryItems.filter { !it.isPurchased }.forEach { item ->
                                appendLine("☐ ${item.name} (${item.quantity} ${item.unit})")
                            }
                            if (groceryItems.any { it.isPurchased }) {
                                appendLine("\n✅ Purchased:")
                                groceryItems.filter { it.isPurchased }.forEach { item ->
                                    appendLine("☑ ${item.name}")
                                }
                            }
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Grocery List"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Item") },
                containerColor = Color(0xFF009688)
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
                
                // Progress Card
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
                                    colors = listOf(Color(0xFF009688), Color(0xFF4DB6AC))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                "🛒 Shopping Progress",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { if (totalCount > 0) purchasedCount.toFloat() / totalCount else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "$purchasedCount of $totalCount items purchased",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters.take(6)) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF009688),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    "Items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Group by category
            val groupedItems = filteredItems.groupBy { it.category }
            
            groupedItems.forEach { (category, items) ->
                item {
                    Text(
                        "${category.emoji} ${category.displayName}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                items(items) { item ->
                    GroceryItemCard(
                        item = item,
                        onToggle = { 
                            groceryItems = groceryItems.map { 
                                if (it.id == item.id) it.copy(isPurchased = !it.isPurchased) else it 
                            }
                        },
                        onDelete = {
                            groceryItems = groceryItems.filter { it.id != item.id }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroceryItemCard(
    item: GroceryItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPurchased) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isPurchased,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50)
                )
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else null,
                    color = if (item.isPurchased) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${item.quantity} ${item.unit}".trim(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
