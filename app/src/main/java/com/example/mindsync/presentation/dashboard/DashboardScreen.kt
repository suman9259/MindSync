package com.example.mindsync.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    state: DashboardState,
    onNavigationItemSelected: (Int) -> Unit,
    onRetry: () -> Unit,
    onStartRoutine: () -> Unit
) {
    // Show loading state
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Show error state
    state.error?.let { error ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Error: $error")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
        return
    }

    // Main content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcome message with user's name
        Text(
            text = "Welcome, ${state.userName.ifEmpty { "User" }}! ",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Morning Glow Card
        Card(
            onClick = onStartRoutine,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF7E6)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Morning Glow", style = MaterialTheme.typography.titleMedium)
                    Text("Start your day right", style = MaterialTheme.typography.bodyMedium)
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Start Routine",
                    tint = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Progress Section
        Text("Your Progress", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Circular Progress
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            CircularProgressIndicator(
                progress = state.progress,
                modifier = Modifier.size(200.dp),
                strokeWidth = 12.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(state.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text("Completed")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Completed Steps
        Text("Completed Steps", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        
        state.completedSteps.forEach { step ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Start Routine",
                    tint = Color.Gray
                )
                Text(
                    text = step,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Bottom Navigation Bar
        NavigationBar {
            NavigationBarItem(
                selected = state.selectedTab == 0,
                onClick = { onNavigationItemSelected(0) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = state.selectedTab == 1,
                onClick = { onNavigationItemSelected(1) },
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping") },
                label = { Text("Shopping") }
            )
            NavigationBarItem(
                selected = state.selectedTab == 2,
                onClick = { onNavigationItemSelected(2) },
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Health") },
                label = { Text("Health") }
            )
            NavigationBarItem(
                selected = state.selectedTab == 3,
                onClick = { onNavigationItemSelected(3) },
                icon = { Icon(Icons.Default.Notifications, contentDescription = "Reminders") },
                label = { Text("Reminders") }
            )
            NavigationBarItem(
                selected = state.selectedTab == 4,
                onClick = { onNavigationItemSelected(4) },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") }
            )
        }
    }
}

@Composable
private fun CategoryRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(count = 4) { index: Int ->
            val item = listOf("Skincare", "Health", "Calendar", "Progress")[index]
            CategoryItem(item = item)
        }
    }
}

@Composable
private fun CategoryItem(item: String) {
    Card(
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                text = item,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun MorningGlowCard(onStartClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF9A9E),
                            Color(0xFFFAD0C4)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    "Morning Glow",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    "Your daily skincare ritual",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("4 steps", color = Color(0xFFFF9A9E))
                }
            }
            Text(
                "~15 min",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun ProgressSection(
    progress: Float,
    completedCount: Int,
    totalCount: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Circular Progress
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(120.dp),
                color = Color(0xFF4CAF50),
                strokeWidth = 8.dp,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(progress * 100).toInt()}%", 
                    style = MaterialTheme.typography.headlineMedium)
                Text("Complete", style = MaterialTheme.typography.bodyMedium)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "$completedCount of $totalCount Steps Complete",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            "Keep going, you're doing great!",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
        )
    }
}

@Composable
private fun CompletedSteps(steps: List<String>) {
    Column {
        Text(
            "Completed Steps",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        steps.forEach { step ->
            CompletedStepItem(step)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CompletedStepItem(stepName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Completed",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            stepName,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(" Completed", color = Color(0xFF4CAF50))
    }
}

@Composable
private fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf("Home", "Shopping", "Health", "Reminders", "Profile")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.ShoppingCart,
        Icons.Default.Favorite,
        Icons.Default.Notifications,
        Icons.Default.Person
    )
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.height(72.dp)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = icons[index],
                        contentDescription = item
                    ) 
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}
