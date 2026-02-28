package com.example.mindsync.presentation.meditation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mindsync.domain.model.Meditation
import com.example.mindsync.domain.model.MeditationCategory
import com.example.mindsync.presentation.components.AnimatedListItem
import com.example.mindsync.presentation.components.AnimatedStatCard
import com.example.mindsync.presentation.components.BreathingCircle
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddMeditation: () -> Unit,
    onNavigateToAddReminder: (String?) -> Unit,
    viewModel: MeditationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MeditationEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is MeditationEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                is MeditationEffect.NavigateBack -> onNavigateBack()
                is MeditationEffect.NavigateToDetail -> { }
                is MeditationEffect.ReminderScheduled -> { }
            }
        }
    }

    val darkBackground = Color(0xFF121212)  // Material dark background
    val cardBackground = Color(0xFF1E1E1E)  // Material dark surface
    val tealAccent = Color(0xFF03DAC5)       // Material Teal accent
    
    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Meditation", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddMeditation,
                containerColor = tealAccent
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Meditation")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MeditationHeader(state)
                }

                item {
                    MeditationStatsSection(state)
                }

                item {
                    CategoryFilterSection(
                        selectedCategory = state.selectedCategory,
                        onCategorySelected = { category ->
                            viewModel.processIntent(MeditationIntent.SelectCategory(category))
                        }
                    )
                }

                item {
                    Text(
                        text = "Your Meditations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                val filteredMeditations = if (state.selectedCategory != null) {
                    state.meditations.filter { it.category == state.selectedCategory }
                } else {
                    state.meditations
                }

                if (filteredMeditations.isEmpty()) {
                    item {
                        EmptyMeditationsView(onAddClick = onNavigateToAddMeditation)
                    }
                } else {
                    itemsIndexed(filteredMeditations) { index, meditation ->
                        AnimatedListItem(index = index) {
                            MeditationCard(
                                meditation = meditation,
                                onClick = { viewModel.processIntent(MeditationIntent.SelectMeditation(meditation)) },
                                onReminderClick = { onNavigateToAddReminder(meditation.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MeditationHeader(state: MeditationState) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(500)) + slideInVertically(
            initialOffsetY = { -it / 2 },
            animationSpec = tween(500)
        )
    ) {
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
                            colors = listOf(Color(0xFF03DAC5), Color(0xFF64FFDA))  // Material Teal gradient
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Find Your Peace",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Take a moment to breathe and relax",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    BreathingCircle(
                        modifier = Modifier.size(80.dp),
                        size = 80.dp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MeditationStatsSection(state: MeditationState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedStatCard(
            title = "Sessions",
            value = "${state.stats.totalSessions}",
            modifier = Modifier.weight(1f)
        )
        AnimatedStatCard(
            title = "Minutes",
            value = "${state.stats.totalMinutes}",
            modifier = Modifier.weight(1f)
        )
        AnimatedStatCard(
            title = "Streak",
            value = "${state.stats.currentStreak}",
            subtitle = "days",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryFilterSection(
    selectedCategory: MeditationCategory?,
    onCategorySelected: (MeditationCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") }
            )
        }
        items(MeditationCategory.entries) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) }
            )
        }
    }
}

@Composable
private fun MeditationCard(
    meditation: Meditation,
    onClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = getCategoryColors(meditation.category)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = meditation.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${meditation.durationMinutes} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = meditation.category.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (meditation.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = meditation.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
            IconButton(onClick = onReminderClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Set Reminder",
                    tint = if (meditation.reminderEnabled) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyMeditationsView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BreathingCircle(
            size = 120.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No meditations yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start your mindfulness journey by adding your first meditation",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getCategoryColors(category: MeditationCategory): List<Color> {
    return when (category) {
        MeditationCategory.MINDFULNESS -> listOf(Color(0xFFBB86FC), Color(0xFF9C27B0))  // Purple
        MeditationCategory.BREATHING -> listOf(Color(0xFF03DAC5), Color(0xFF64FFDA))  // Teal
        MeditationCategory.SLEEP -> listOf(Color(0xFF3700B3), Color(0xFFBB86FC))  // Deep purple
        MeditationCategory.STRESS_RELIEF -> listOf(Color(0xFFCF6679), Color(0xFFB00020))  // Error/coral
        MeditationCategory.FOCUS -> listOf(Color(0xFFFFD54F), Color(0xFFFFC107))  // Amber
        MeditationCategory.GRATITUDE -> listOf(Color(0xFF03DAC5), Color(0xFFBB86FC))  // Teal to purple
        MeditationCategory.BODY_SCAN -> listOf(Color(0xFF03DAC5), Color(0xFF00C2A8))  // Teal variants
        MeditationCategory.GUIDED -> listOf(Color(0xFFBB86FC), Color(0xFF03DAC5))  // Purple to teal
    }
}
