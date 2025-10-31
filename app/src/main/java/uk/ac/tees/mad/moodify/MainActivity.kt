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
fun Moodify(){
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(navController, startDestination = MoodifyNavigation.Splash.destination) {
        composable(MoodifyNavigation.Splash.destination) {
            SplashScreen(navController)
        }
        composable(MoodifyNavigation.Auth.destination) {

            AuthScreen(
                onLogin = { email, password ->
                    kotlinx.coroutines.suspendCancellableCoroutine<Result<Unit>> { cont ->
                        authViewModel.login(email, password) {
                            cont.resume(it, null)
                        }
                    }
                },
                onSignup = { first, last, email, password ->
                    kotlinx.coroutines.suspendCancellableCoroutine<Result<Unit>> { cont ->
                        authViewModel.signup(first, last, email, password) {
                            cont.resume(it, null)
                        }
                    }
                },
                onAuthSuccess = {
                    // navigate to home screen on success
                    // navController.navigate(MoodifyNavigation.Home.destination) {
                    //     popUpTo(MoodifyNavigation.Auth.destination) { inclusive = true }
                    // }
                }
            )
        }
    }
}