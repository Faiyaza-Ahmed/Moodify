// The ResultScreen.kt remains unchanged, as the ViewModel API is the same.
// For completeness, here it is again:

package uk.ac.tees.mad.moodify.ui.result

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    mood: String,
    viewModel: ResultViewModel = hiltViewModel(),
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var playlistUrl by remember { mutableStateOf<String?>(null) }
    var activitySuggestion by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(mood) {
        mood.let {
            val spotifyUrl = viewModel.fetchSpotifyPlaylist(it)
            val activity = viewModel.suggestActivity(it)
            playlistUrl = spotifyUrl
            activitySuggestion = activity
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Result", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurplePrimary.copy(alpha = 0.9f),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = LavenderMist
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(GradientStart, GradientEnd))
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    val emoji = when (mood?.lowercase()) {
                        "positive" -> "😊"
                        "negative" -> "😔"
                        "neutral" -> "😐"
                        else -> "🧘"
                    }

                    Text(
                        text = "Detected Mood: ${mood?.replaceFirstChar { it.uppercase() }} $emoji",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    playlistUrl?.let {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                    context.startActivity(intent)
                                },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LibraryMusic,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Recommended Playlist",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Tap to open in Spotify",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    activitySuggestion?.let {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.SelfImprovement,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Suggested Activity",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = it,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    ElevatedButton(
                        onClick = {
                            scope.launch {
                                viewModel.saveMoodEntry(mood ?: "unknown")
                                Toast.makeText(context, "Mood entry saved!", Toast.LENGTH_SHORT).show()
                                onBackToHome()
                            }
                        },
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save to History", color = Color.White)
                    }
                }
            }
        }
    }
}