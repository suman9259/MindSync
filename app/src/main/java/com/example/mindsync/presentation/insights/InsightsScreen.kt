package com.example.mindsync.presentation.insights

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onNavigateBack: () -> Unit,
    viewModel: InsightsViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val periods = listOf("Today", "Week", "Month", "Year")
    val selectedPeriod = when (state.selectedPeriod) {
        ReportPeriod.TODAY -> "Today"
        ReportPeriod.WEEK -> "Week"
        ReportPeriod.MONTH -> "Month"
        ReportPeriod.YEAR -> "Year"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    periods.forEach { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { 
                                val reportPeriod = when (period) {
                                    "Today" -> ReportPeriod.TODAY
                                    "Week" -> ReportPeriod.WEEK
                                    "Month" -> ReportPeriod.MONTH
                                    "Year" -> ReportPeriod.YEAR
                                    else -> ReportPeriod.TODAY
                                }
                                viewModel.selectPeriod(reportPeriod)
                            },
                            label = { Text(period) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                OverallProgressCard(
                    medicineProgress = state.medicineProgress,
                    skincareProgress = state.skincareProgress,
                    medicinesTaken = state.medicinesTaken,
                    medicinesTotal = state.medicinesTotal,
                    skincareCompleted = state.skincareCompleted,
                    skincareTotal = state.skincareTotal,
                    currentStreak = maxOf(state.medicineStreak, state.skincareStreak)
                )
            }

            item {
                Text(
                    text = "Activity Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActivitySummaryCard(
                        title = "Medicine",
                        value = "${state.medicinesTaken}/${state.medicinesTotal}",
                        unit = "taken today",
                        color = Color(0xFF667eea),
                        modifier = Modifier.weight(1f)
                    )
                    ActivitySummaryCard(
                        title = "Skincare",
                        value = "${state.skincareCompleted}/${state.skincareTotal}",
                        unit = "completed",
                        color = Color(0xFFf093fb),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                ProgressReportCard(
                    report = state.progressReport,
                    periodLabel = selectedPeriod
                )
            }

            item {
                WeeklyActivityChart()
            }

            item {
                Text(
                    text = "Achievements",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                AchievementsRow(achievements = state.achievements)
            }

            item {
                Text(
                    text = "Streaks & Goals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                StreaksCard(
                    currentStreak = maxOf(state.medicineStreak, state.skincareStreak),
                    longestStreak = maxOf(state.longestMedicineStreak, state.longestSkincareStreak),
                    totalDays = state.totalActiveDays
                )
            }

            item {
                GoalsProgressCard(
                    medicinesTaken = state.medicinesTaken,
                    medicinesTotal = state.medicinesTotal,
                    skincareCompleted = state.skincareCompleted,
                    skincareTotal = state.skincareTotal
                )
            }

            item {
                Text(
                    text = "Health Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HealthMetricCard(
                        title = "Avg. Heart Rate",
                        value = "72",
                        unit = "bpm",
                        trend = "+2%",
                        isPositive = false,
                        modifier = Modifier.weight(1f)
                    )
                    HealthMetricCard(
                        title = "Calories Burned",
                        value = "2,450",
                        unit = "kcal",
                        trend = "+15%",
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun OverallProgressCard(
    medicineProgress: Float,
    skincareProgress: Float,
    medicinesTaken: Int,
    medicinesTotal: Int,
    skincareCompleted: Int,
    skincareTotal: Int,
    currentStreak: Int
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedMedicineProgress by animateFloatAsState(
        targetValue = if (animationPlayed) medicineProgress else 0f,
        animationSpec = tween(1000),
        label = "medicineProgress"
    )
    val animatedSkincareProgress by animateFloatAsState(
        targetValue = if (animationPlayed) skincareProgress else 0f,
        animationSpec = tween(1000),
        label = "skincareProgress"
    )

    LaunchedEffect(Unit) {
        delay(300)
        animationPlayed = true
    }

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
                        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Your Progress Today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircularProgressWithLabel(
                        progress = animatedMedicineProgress,
                        label = "Medicine",
                        value = "${(medicineProgress * 100).toInt()}%",
                        color = Color(0xFF38ef7d)
                    )
                    CircularProgressWithLabel(
                        progress = animatedSkincareProgress,
                        label = "Skincare",
                        value = "${(skincareProgress * 100).toInt()}%",
                        color = Color(0xFFf5576c)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem("$medicinesTaken/$medicinesTotal", "Medicines")
                    StatItem("$skincareCompleted/$skincareTotal", "Routines")
                    StatItem("$currentStreak", "Day Streak")
                }
            }
        }
    }
}

@Composable
private fun CircularProgressWithLabel(
    progress: Float,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color.White.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ActivitySummaryCard(
    title: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (title == "Meditation") Icons.Default.Favorite else Icons.Default.Star,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
private fun WeeklyActivityChart() {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val meditationData = listOf(0.6f, 0.8f, 0.4f, 0.9f, 0.7f, 0.5f, 0.8f)
    val workoutData = listOf(0.4f, 0f, 0.7f, 0.5f, 0f, 0.8f, 0.6f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Weekly Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(100.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Workout bar
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height((100 * workoutData[index]).dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0xFFf093fb).copy(alpha = 0.5f))
                                    .align(Alignment.BottomStart)
                            )
                            // Meditation bar
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height((100 * meditationData[index]).dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0xFF667eea))
                                    .align(Alignment.BottomEnd)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                LegendItem(color = Color(0xFF667eea), label = "Meditation")
                LegendItem(color = Color(0xFFf093fb), label = "Workout")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun AchievementsRow(achievements: List<Achievement>) {
    if (achievements.isEmpty()) {
        Text(
            text = "Complete tasks to unlock achievements!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements) { achievement ->
                AchievementCard(achievement)
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = achievement.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.labelSmall,
                color = if (achievement.unlocked)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun StreaksCard(
    currentStreak: Int,
    longestStreak: Int,
    totalDays: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StreakItem(
                emoji = "🔥",
                value = "$currentStreak",
                label = "Current Streak"
            )
            StreakItem(
                emoji = "🏆",
                value = "$longestStreak",
                label = "Longest Streak"
            )
            StreakItem(
                emoji = "📅",
                value = "$totalDays",
                label = "Total Days"
            )
        }
    }
}

@Composable
private fun StreakItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GoalsProgressCard(
    medicinesTaken: Int,
    medicinesTotal: Int,
    skincareCompleted: Int,
    skincareTotal: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Today's Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (medicinesTotal > 0) {
                GoalProgressItem(
                    title = "Take all medicines",
                    progress = if (medicinesTotal > 0) medicinesTaken.toFloat() / medicinesTotal else 0f,
                    current = medicinesTaken,
                    target = medicinesTotal
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (skincareTotal > 0) {
                GoalProgressItem(
                    title = "Complete skincare routines",
                    progress = if (skincareTotal > 0) skincareCompleted.toFloat() / skincareTotal else 0f,
                    current = skincareCompleted,
                    target = skincareTotal
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            val totalTasks = medicinesTotal + skincareTotal
            val completedTasks = medicinesTaken + skincareCompleted
            GoalProgressItem(
                title = "Complete all daily tasks",
                progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f,
                current = completedTasks,
                target = totalTasks
            )
        }
    }
}

@Composable
private fun GoalProgressItem(
    title: String,
    progress: Float,
    current: Int,
    target: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$current/$target",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                        )
                    )
            )
        }
    }
}

@Composable
private fun HealthMetricCard(
    title: String,
    value: String,
    unit: String,
    trend: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trend,
                style = MaterialTheme.typography.labelSmall,
                color = if (isPositive) Color(0xFF11998e) else Color(0xFFf5576c)
            )
        }
    }
}

@Composable
private fun ProgressReportCard(
    report: ProgressReport,
    periodLabel: String
) {
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
                        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "📊 $periodLabel Progress Report",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReportStatItem(
                        label = "Completion",
                        value = "${(report.completionRate * 100).toInt()}%",
                        color = Color.White
                    )
                    ReportStatItem(
                        label = "Tasks Done",
                        value = "${report.totalCompleted}",
                        color = Color.White
                    )
                    ReportStatItem(
                        label = "Days Tracked",
                        value = "${report.daysTracked}",
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReportStatItem(
                        label = "Medicines",
                        value = "${report.medicinesTaken}/${report.medicinesTotal}",
                        color = Color.White
                    )
                    ReportStatItem(
                        label = "Skincare",
                        value = "${report.skincareCompleted}/${report.skincareTotal}",
                        color = Color.White
                    )
                    ReportStatItem(
                        label = "Workouts",
                        value = "${report.workoutsCompleted}",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}
