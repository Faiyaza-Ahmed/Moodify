package uk.ac.tees.mad.moodify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.moodify.ui.auth.AuthScreen
import uk.ac.tees.mad.moodify.ui.auth.AuthViewModel
import uk.ac.tees.mad.moodify.ui.home.HomeScreen
import uk.ac.tees.mad.moodify.ui.theme.MoodifyTheme
import uk.ac.tees.mad.moodify.ui.splash.SplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodifyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Moodify()
                }
            }
        }
    }
}

sealed class MoodifyNavigation(val destination : String){
    object Splash : MoodifyNavigation("splash")
    object Auth : MoodifyNavigation("auth")
    object Home : MoodifyNavigation("home")
}

@Composable
fun Moodify() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()
    val startDestination = if (viewModel.isUserLoggedIn.value) {
        MoodifyNavigation.Home.destination
    } else {
        MoodifyNavigation.Auth.destination
    }

    NavHost(navController = navController, startDestination = MoodifyNavigation.Splash.destination) {
        composable(MoodifyNavigation.Splash.destination) {
            SplashScreen(navController, startDestination)
        }
        composable(MoodifyNavigation.Auth.destination) {
            AuthScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(MoodifyNavigation.Home.destination) {
            // Uncomment and implement HomeScreen as needed

            HomeScreen(
            )

        }
    }
}