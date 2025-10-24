package uk.ac.tees.mad.moodify.ui.theme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.moodify.R
import uk.ac.tees.mad.moodify.ui.theme.PurplePrimary
import uk.ac.tees.mad.moodify.ui.theme.TextSecondary

@Composable
fun SplashScreen(navController: NavController) {

    val scale = remember { Animatable(0f) }

    // Animation effect when splash launches
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1500)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // App Icon
            Image(
                painter = painterResource(id = R.drawable.moodify), 
                contentDescription = "Moodify Logo",
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "Moodify",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PurplePrimary
            )

            // Tagline
            Text(
                text = "Track Feelings. Find Balance.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = TextSecondary
            )
        }
    }
}