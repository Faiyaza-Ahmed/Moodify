package uk.ac.tees.mad.moodify.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
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
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.moodify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var journalText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var detectedMood by remember { mutableStateOf<String?>(null) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            spokenText?.let {
                journalText = journalText + if (journalText.isNotEmpty()) " $it" else it
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your mood...")
                }
                speechLauncher.launch(intent)
            } else {
                Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Moodify", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                actions = {
                    IconButton(onClick = { onNavigateToProfile() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = LavenderMist
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(GradientStart, GradientEnd))
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "How are you feeling today?",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = journalText,
                onValueChange = { journalText = it },
                placeholder = { Text("Type your thoughts...") },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.White.copy(alpha = 0.15f)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    cursorColor = Color.White
                )
            )

            Spacer(Modifier.height(15.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ElevatedButton(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice")
                    Spacer(Modifier.width(8.dp))
                    Text("Voice Note", color = Color.White)
                }

                ElevatedButton(
                    onClick = {
                        if (journalText.isBlank()) {
                            Toast.makeText(context, "Please write something first.", Toast.LENGTH_SHORT).show()
                        } else {
                            scope.launch {
                                isLoading = true
                                val mood = viewModel.detectSentiment(journalText)
                                isLoading = false
                                detectedMood = mood
                            }
                        }
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Analyze")
                    Spacer(Modifier.width(8.dp))
                    Text("Analyze Mood", color = Color.White)
                }
            }

            Spacer(Modifier.height(25.dp))

            Crossfade(targetState = detectedMood != null) { show ->
                if (show) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable{
                                navController.navigate("result/${detectedMood}")
                                detectedMood = null
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Text("Result Available!! Click to continue", color = Color.White)
                        }
                    }
                } else if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            Spacer(Modifier.height(40.dp))

            OutlinedButton(
                onClick = onNavigateToHistory,
                shape = RoundedCornerShape(25.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("View History")
            }
        }
    }
}

