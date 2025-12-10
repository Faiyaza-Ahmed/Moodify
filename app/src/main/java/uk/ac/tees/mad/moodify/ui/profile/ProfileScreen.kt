package uk.ac.tees.mad.moodify.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.moodify.MoodifyNavigation
import uk.ac.tees.mad.moodify.ui.history.HistoryViewModel
import uk.ac.tees.mad.moodify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val name by viewModel.userName.collectAsState()
    val error by viewModel.error.collectAsState()
    val moods by historyViewModel.moodsFromRoom.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var editableName by remember(name) { mutableStateOf(name) }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val moodValues = remember(moods) {
        moods.map {
            when (it.mood.lowercase()) {
                "positive" -> 5
                "neutral" -> 3
                "negative" -> 1
                else -> 3
            }
        }.reversed().takeLast(7)
    }

    val quote = "“Your mood is a reflection of your focus. Choose joy today.”"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PurplePrimary
                )
            )
        },
        containerColor = LavenderMist,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = quote,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Weekly Mood Trend 🌤️",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (moodValues.isNotEmpty()) {
                        MoodTrendChart(data = moodValues)
                    } else {
                        Text(
                            "No mood data yet. Start logging!",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = editableName,
                onValueChange = { editableName = it },
                label = { Text("Your Name", color = TextSecondary) },
                shape = RoundedCornerShape(25.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CoralAccent,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = CoralAccent,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.updateUserData(editableName)
                },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                enabled = editableName.trim() != name && editableName.isNotBlank()
            ) {
                Text("Save Name", color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedButton(
                onClick = {
                    viewModel.logOut()
                    navController?.navigate( MoodifyNavigation.Auth.destination) {
                        popUpTo(0)
                    }
                },
                shape = RoundedCornerShape(25.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralAccent),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Logout", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MoodTrendChart(
    data: List<Int>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 140.dp
) {
    val maxMood = 5f
    val minMood = 1f
    val moodRange = maxMood - minMood

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(chartHeight)
            .padding(horizontal = 8.dp)
    ) {
        val width = size.width
        val height = size.height

        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 2f
        )

        if (data.size >= 2) {
            val spacing = width / (data.size - 1).coerceAtLeast(1)

            val path = Path()
            data.forEachIndexed { index, mood ->
                val x = spacing * index
                val y = height - ((mood - minMood) / moodRange * height)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                brush = Brush.horizontalGradient(listOf(CoralAccent, SkyBlue)),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            data.forEachIndexed { index, mood ->
                val x = spacing * index
                val y = height - ((mood - minMood) / moodRange * height)
                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = Offset(x, y)
                )
            }
        }

        listOf(1f, 3f, 5f).forEach { level ->
            val y = height - ((level - minMood) / moodRange * height)
            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }
    }
}