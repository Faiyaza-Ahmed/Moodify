package uk.ac.tees.mad.moodify.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.moodify.data.local.MoodEntries
import uk.ac.tees.mad.moodify.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel : HistoryViewModel = hiltViewModel(),
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val moodEntries by viewModel.moodsFromRoom.collectAsState()


    val filteredList = remember(selectedFilter, moodEntries) {
        if (selectedFilter == "All") moodEntries
        else if (selectedFilter == "Negative") moodEntries.filter { it.mood.equals(selectedFilter, true) }
        else if (selectedFilter == "Neutral") moodEntries.filter { it.mood.equals(selectedFilter, true) }
        else if (selectedFilter == "Angry") moodEntries.filter { it.mood.equals(selectedFilter, true) }
        else if (selectedFilter == "Sad") moodEntries.filter { it.mood.equals(selectedFilter, true) }
        else if (selectedFilter == "Positive") moodEntries.filter { it.mood.equals(selectedFilter, true) }
        else moodEntries.filter { it.mood.equals(selectedFilter, true) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mood History",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PurplePrimary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = LavenderMist,
        modifier = modifier
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(brush = Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
                .padding(16.dp)
        ) {
            FilterChipsRow(
                filters = listOf("All", "Positive", "Sad", "Angry", "Negative"),
                selectedFilter = selectedFilter,
                onFilterSelected = {
                    selectedFilter = it
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedVisibility(filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No mood entries yet 🕊️",
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Write or speak your mood and it will appear here.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            AnimatedVisibility(filteredList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { entry ->
                        MoodEntryCard(entry = entry, navController)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun FilterChipsRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter,
                        color = if (selectedFilter == filter) PurplePrimary else TextPrimary
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.White.copy(alpha = 0.20f),
                    selectedLabelColor = PurplePrimary,
                    containerColor = Color.White.copy(alpha = 0.06f),
                    labelColor = TextSecondary
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun MoodEntryCard(entry: MoodEntries, navController: NavController) {
    val moodEmoji = when (entry.mood.lowercase(Locale.getDefault())) {
        "happy", "positive" -> "😊"
        "sad", "negative" -> "😢"
        "angry" -> "😡"
        "neutral" -> "😐"
        else -> "🌸"
    }

    val date = remember(entry.timestamp) {
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            .format(Date(entry.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                navController.navigate("result/${entry.mood}")
            },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = PurplePrimary.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = moodEmoji, style = MaterialTheme.typography.headlineSmall)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.mood.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))



                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "View",
                style = MaterialTheme.typography.labelMedium,
                color = CoralAccent,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
